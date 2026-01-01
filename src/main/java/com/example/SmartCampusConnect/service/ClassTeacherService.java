package com.example.SmartCampusConnect.service;

import com.example.SmartCampusConnect.dtos.classTeacherDto.ClassTeacherAssignmentRequest;
import com.example.SmartCampusConnect.dtos.classTeacherDto.ClassTeacherResponseDto;
import com.example.SmartCampusConnect.dtos.classTeacherDto.ClassTeacherUpdateDto;
import com.example.SmartCampusConnect.dtos.classTeacherDto.ClassTeacherUpdateResponseDto;
import com.example.SmartCampusConnect.dtos.facultyDto.FacultyActionRequest;
import com.example.SmartCampusConnect.dtos.request.RequestResponse;
import com.example.SmartCampusConnect.enums.RequestStatus;
import com.example.SmartCampusConnect.enums.Role;
import com.example.SmartCampusConnect.exception.BadRequestException;
import com.example.SmartCampusConnect.exception.ResourceNotFoundException;
import com.example.SmartCampusConnect.model.*;
import com.example.SmartCampusConnect.respository.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ClassTeacherService {

    @Autowired
    private ClassTeacherJpaRepo classTeacherRepo;

    @Autowired
    private FacultyJpaRepo facultyRepo;

    @Autowired
    private StudentJpaRepo studentJpaRepo;

    @Autowired
    private ClassTeacherJpaRepo classTeacherJpaRepo;

    @Autowired
    private RequestJpaRepo requestJpaRepo;

    @Autowired
    private UserJpaRepo userJpaRepo;

    @Autowired
    private HodJpaRepo hodJpaRepo;

    // Assign a class teacher
    public ClassTeacherResponseDto assignClassTeacher(Long hodId,ClassTeacherAssignmentRequest req) {

        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        Faculty faculty =facultyRepo.findById(req.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        boolean exists = classTeacherJpaRepo.existsByFaculty_BranchAndSectionAndYear(req.getBranch().trim().toUpperCase(), req.getSection().trim().toUpperCase(), req.getYear());

        if (exists) {
            throw new BadRequestException("Faculty is already assigned to this class");
        }

        if(!hod.getBranch().equalsIgnoreCase(faculty.getBranch())){
            throw new AccessDeniedException("Not authorized to add the class teacher");
        }

        ClassTeacher classTeacher = new ClassTeacher();
        classTeacher.setFaculty(faculty);
        classTeacher.setBranch(req.getBranch().trim().toUpperCase());
        classTeacher.setSection(req.getSection().trim().toUpperCase());
        classTeacher.setYear(req.getYear());
        return toClassResponseDto(classTeacherJpaRepo.save(classTeacher));
    }

    public ClassTeacherUpdateResponseDto updateClassTeacher(Long id, ClassTeacherUpdateDto dto) {

        if(dto==null || dto.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be provided for update");
        }

        // Find the existing class teacher record
        ClassTeacher classTeacher = classTeacherJpaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassTeacher not found with id: " + id));

        // Update faculty if provided
        if (dto.getFacultyId() != null) {
            Faculty faculty = facultyRepo.findById(dto.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + dto.getFacultyId()));
            classTeacher.setFaculty(faculty);
        }

        if (dto.getBranch() != null && !dto.getBranch().isBlank()) {
            classTeacher.setBranch(dto.getBranch().trim().toUpperCase());
        }

        if (dto.getSection() != null && !dto.getSection().isBlank()) {
            classTeacher.setSection(dto.getSection().trim().toUpperCase());
        }

        if (dto.getYear() != null) {
            classTeacher.setYear(dto.getYear());
        }

        classTeacherJpaRepo.save(classTeacher);

        return toClassTeacherUpdateResponse(classTeacher);
    }

    public List<ClassTeacherResponseDto> filterClassTeachers(Long hodId,String branch, String section, Integer year) {
        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));
        List<ClassTeacher> classTeacherList =classTeacherJpaRepo.findByFilters(branch.trim().toUpperCase(), section.trim().toUpperCase(), year);
        List<ClassTeacherResponseDto> responseDtos=new ArrayList<>();
        for(ClassTeacher classTeacher:classTeacherList){
            ClassTeacherResponseDto response=toClassResponseDto(classTeacher);
            responseDtos.add(response);
        }
        return responseDtos;
    }


    public ClassTeacherResponseDto getClassTeacherForStudent(Long studentId) {
        Student student =studentJpaRepo.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id "+studentId));

        ClassTeacher classTeacher=classTeacherJpaRepo.findByBranchAndSectionAndYear(
                student.getBranch(),
                student.getSection(),
                student.getYear()
        );
        if (classTeacher == null) {
            throw new ResourceNotFoundException("Class Teacher is not assigned for this section yet");
        }

        return toClassResponseDto(classTeacher);
    }

    public List<RequestResponse> getAssignedRequests(Long id) {
        // Validate faculty
        ClassTeacher classTeacher = classTeacherJpaRepo.findByFaculty_Id(id);
        if(classTeacher==null){
            throw new ResourceNotFoundException("Class Teacher not found for user id "+id);
        }

        // Directly get requests ONLY assigned to this faculty
        List<Request> pendingRequests = requestJpaRepo.findByStudent_BranchAndStudent_SectionAndStudent_YearAndStatus(
                classTeacher.getBranch(),classTeacher.getSection(),classTeacher.getYear(),RequestStatus.SUBMITTED
        );

        // Convert to DTO list
        List<RequestResponse> responsesList = new ArrayList<>();
        for (Request request : pendingRequests) {
            responsesList.add(toResponseDto(request));
        }

        return responsesList;
    }

    // 2. Faculty Approve / Reject Request

    public RequestResponse handleRequestAction(Long facultyId, FacultyActionRequest dto) {

        ClassTeacher classTeacher = classTeacherJpaRepo.findByFaculty_Id(facultyId);
        if(classTeacher==null){
            throw new ResourceNotFoundException("Class Teacher not found for user id "+facultyId);
        }

        Request request = requestJpaRepo.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        // Ensure THIS Class Teacher is assigned to this request
        if (!request.getFaculty().getId().equals(facultyId)) {
            throw new BadRequestException("You are not assigned to this request");
        }

        // Ensure request is still pending
        if (request.getStatus() != RequestStatus.SUBMITTED) {
            throw new BadRequestException("Request already processed");
        }

        // Remarks required when rejecting
        if (!dto.getApproved()) {
            if (dto.getRemarks() == null || dto.getRemarks().isBlank()) {
                throw new BadRequestException("Remarks are required when rejecting");
            }
        }

        if (dto.getApproved()) {
            request.setFacultyRemarks("Valid Details");
        } else {
            request.setHodRemarks(dto.getRemarks());
        }

        // Set faculty remarks
        request.setFacultyRemarks(dto.getRemarks());
        request.setUpdatedAt(LocalDateTime.now());

        // Faculty Reject
        if (!dto.getApproved()) {
            request.setStatus(RequestStatus.CLASSTEACHER_REJECTED);
            requestJpaRepo.save(request);
            return toResponseDto(request);
        }

        // Faculty Approve
        request.setStatus(RequestStatus.CLASSTEACHER_APPROVED);
        requestJpaRepo.save(request);

        return toResponseDto(request);
    }

    public ClassTeacherResponseDto getClassTeacherById(Long hodId, Long id) {
        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));
        ClassTeacher classTeacher = classTeacherJpaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClassTeacher not found with id: " + id));
        if(!hod.getBranch().equals(classTeacher.getBranch())){
            throw new AccessDeniedException("Not authorized to access the other department specific class teacher");
        }
        return toClassResponseDto(classTeacher);
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

    public ClassTeacherUpdateResponseDto toClassTeacherUpdateResponse(ClassTeacher ct) {
        ClassTeacherUpdateResponseDto dto = new ClassTeacherUpdateResponseDto();
        dto.setId(ct.getId());
        dto.setFacultyId(ct.getFaculty().getId());
        dto.setFacultyName(ct.getFaculty().getName());
        dto.setBranch(ct.getBranch());
        dto.setSection(ct.getSection());
        dto.setYear(ct.getYear());
        return dto;
    }

    private RequestResponse toResponseDto(Request request) {
        return new RequestResponse(request.getId(), request.getRequestType(), request.getStatus(), request.getDescription(), request.getStudent().getName(), request.getFaculty().getName(), request.getFromDate(), request.getToDate(), request.getFacultyRemarks(), request.getHodRemarks(), request.getCreatedAt(), request.getUpdatedAt());
    }

    public void deleteClassTeacherById(Long hodId, Long id) {
        ClassTeacher classTeacher=classTeacherRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject Teacher not found"));
        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        if (!classTeacher.getBranch().equals(hod.getBranch())) {
            throw new AccessDeniedException("You cannot delete ST from another department");
        }
        classTeacherRepo.delete(classTeacher);
    }
}