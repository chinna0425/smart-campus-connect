package com.example.SmartCampusConnect.service;

import com.example.SmartCampusConnect.dtos.classTeacherDto.ClassTeacherResponseDto;
import com.example.SmartCampusConnect.dtos.request.CreateRequest;
import com.example.SmartCampusConnect.dtos.request.RequestResponse;
import com.example.SmartCampusConnect.dtos.studentDto.StudentResponse;
import com.example.SmartCampusConnect.dtos.studentDto.StudentUpdateDto;
import com.example.SmartCampusConnect.enums.RequestStatus;
import com.example.SmartCampusConnect.enums.RequestType;
import com.example.SmartCampusConnect.enums.Role;
import com.example.SmartCampusConnect.exception.BadRequestException;
import com.example.SmartCampusConnect.exception.ResourceNotFoundException;
import com.example.SmartCampusConnect.model.*;
import com.example.SmartCampusConnect.respository.*;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentJpaRepo studentJpaRepo;

    @Autowired
    private ClassTeacherJpaRepo classTeacherJpaRepo;

    @Autowired
    private RequestJpaRepo requestJpaRepo;

    @Autowired
    private AccessControlService access;  // RBAC middleware

    @Autowired
    private SubjectTeacherJpaRepo subjectTeacherJpaRepo;

    @Autowired
    private UserJpaRepo userJpaRepo;

    @Autowired
    private HodJpaRepo hodJpaRepo;

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    public List<StudentResponse> getAllStudents(Long currentUserId,String rollNumber, String branch, String section, Integer year) {
        //  RBAC filtering — only showing students allowed for user

        User user = userJpaRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (rollNumber != null && !rollNumber.isBlank()) {

            Student student = studentJpaRepo.findByRollNumber(normalize(rollNumber))
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

            try {
                access.requireStudentAccessForHodAndCTAndStudent(student);
            } catch (Exception ignored) {}

            return List.of(toStudentResponseDto(student));
        }
        List<Student> students = studentJpaRepo.filterStudents(normalize(rollNumber), normalize(branch), normalize(section), year);

        if (students.isEmpty()) {
            throw new ResourceNotFoundException("No students found");
        }

        List<StudentResponse> responseList=new ArrayList<>();
        for(Student student:students){
            try {
                access.requireStudentAccessForHodAndCTAndStudent(student);
                responseList.add(toStudentResponseDto(student));
            } catch (Exception ignored) {}
        }

        return responseList;
    }

    public StudentResponse getStudentProfile(Long studentId) {
        Student student = studentJpaRepo.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + studentId));
        return toStudentResponseDto(student);
    }

    public StudentResponse getStudentById(Long id) throws AccessDeniedException {
        Student student = studentJpaRepo.findByStudentId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));

        access.requireStudentViewAccessForCTAndHodAndSTAndAdmin(student); // RBAC check added

        return toStudentResponseDto(student);
    }

    public StudentResponse updateStudent(Long id, StudentUpdateDto dto) throws AccessDeniedException {

        if (dto == null || dto.isEmpty())
            throw new IllegalArgumentException("At least one field must be provided");

        Student student = studentJpaRepo.findByStudentId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // RBAC check — Only ClassTeacher/HOD of that class
        access.requireStudentViewAccessForCTAndHodAndSTAndAdmin(student);

        // updating fields
        if (dto.getName() != null && !dto.getName().isBlank()) student.setName(WordUtils.capitalizeFully(dto.getName().trim()));
        if (dto.getBranch() != null && !dto.getBranch().isBlank()) student.setBranch(dto.getBranch().trim().toUpperCase());
        if (dto.getSection() != null && !dto.getSection().isBlank()) student.setSection(dto.getSection().trim().toUpperCase());
        if (dto.getYear() != null) student.setYear(dto.getYear());

        studentJpaRepo.save(student);
        return toStudentResponseDto(student);
    }

    public RequestResponse createRequest(Long studentId, CreateRequest dto) {
        Student student = studentJpaRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (dto.getRequestType().equals(RequestType.LEAVE)) {
            if (dto.getFromDate() == null || dto.getToDate() == null)
                throw new BadRequestException("FromDate and ToDate are required");
        } else {
            dto.setFromDate(null);
            dto.setToDate(null);
        }

        ClassTeacher ct = classTeacherJpaRepo.findByBranchAndSectionAndYear(
                student.getBranch(), student.getSection(), student.getYear());

        if (ct == null)
            throw new BadRequestException("No class teacher assigned for this class");

        Faculty assignedFaculty = ct.getFaculty();

        Request request = new Request();
        request.setStudent(student);
        request.setFaculty(assignedFaculty);
        request.setRequestType(dto.getRequestType());
        request.setStatus(RequestStatus.SUBMITTED);
        request.setDescription(dto.getDescription());
        request.setFromDate(dto.getFromDate());
        request.setToDate(dto.getToDate());
        request.setCreatedAt(LocalDateTime.now());

        requestJpaRepo.save(request);
        return toResponseDto(request);
    }


    public List<RequestResponse> getStudentRequests(Long studentId) {
        Student student = studentJpaRepo.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        List<Request> list = requestJpaRepo.findByStudent_StudentId(studentId);
        List<RequestResponse> res = new ArrayList<>();

        for (Request r : list) res.add(toResponseDto(r));

        return res;
    }

    public ClassTeacherResponseDto toClassResponseDto(ClassTeacher ct){
        ClassTeacherResponseDto dto = new ClassTeacherResponseDto();
        dto.setId(ct.getId());
        dto.setFacultyId(ct.getFaculty().getId());
        dto.setFacultyName(ct.getFaculty().getName());
        dto.setBranch(ct.getBranch());
        dto.setSection(ct.getSection());
        dto.setYear(ct.getYear());
        return dto;
    }

    public RequestResponse getRequestById(Long requestId) throws AccessDeniedException {
        Request request = requestJpaRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        Student student = request.getStudent();

        // RBAC — Only Student, ClassTeacher, HOD can see
        access.requireStudentAccessForHodAndCTAndStudent(student);

        return toResponseDto(request);
    }

    public List<RequestResponse> getRequestsForTeacher(Long studentId) throws AccessDeniedException {

        Student student = studentJpaRepo.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // RBAC — only CT/HOD can see this student's requests
        access.requireStudentAccessForHodAndCT(student);

        List<Request> list = requestJpaRepo.findByStudent_StudentId(studentId);

        List<RequestResponse> responses = new ArrayList<>();
        for (Request req : list) {
            responses.add(toResponseDto(req));
        }

        return responses;
    }

    public void deleteStudent(Long currentId,Long id) {
        User user=userJpaRepo.findById(currentId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        Role role=user.getRole();
        Student student = studentJpaRepo.findByStudentId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        if(role==Role.ADMIN){
            studentJpaRepo.delete(student);
            userJpaRepo.delete(student.getUser());
        }else if(role==Role.HOD){
            Hod hod=hodJpaRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Hod not found"));
            if(hod.getBranch().equals(student.getBranch())){
                studentJpaRepo.delete(student);
                userJpaRepo.delete(student.getUser());
            }else{
                throw new AccessDeniedException("You are not authorized to delete the resource");
            }
        }else{
            throw new AccessDeniedException("You are not authorized to delete the resource");
        }
    }


    private RequestResponse toResponseDto(Request request) {
        return new RequestResponse(
                request.getId(),
                request.getRequestType(),
                request.getStatus(),
                request.getDescription(),
                request.getStudent().getName(),
                request.getFaculty() != null ? request.getFaculty().getName() : null,
                request.getFromDate(),
                request.getToDate(),
                request.getFacultyRemarks(),
                request.getHodRemarks(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    private StudentResponse toStudentResponseDto(Student student) {
        return new StudentResponse(
                student.getStudentId(),
                student.getName(),
                student.getRollNumber(),
                student.getBranch(),
                student.getSection(),
                student.getYear()
        );
    }

}
