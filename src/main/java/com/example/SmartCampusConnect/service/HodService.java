package com.example.SmartCampusConnect.service;

import com.example.SmartCampusConnect.dtos.hodDto.HodActionRequest;
import com.example.SmartCampusConnect.dtos.hodDto.HodResponse;
import com.example.SmartCampusConnect.dtos.hodDto.HodUpdateReq;
import com.example.SmartCampusConnect.dtos.request.RequestResponse;
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
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class HodService {

    @Autowired
    private HodJpaRepo hodJpaRepo;

    @Autowired
    private RequestJpaRepo requestJpaRepo;

    @Autowired
    private SubjectTeacherJpaRepo subjectTeacherJpaRepo;

    @Autowired
    private NotificationJpaRepo notificationJpaRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ClassTeacherJpaRepo classTeacherJpaRepo;

    @Autowired
    private FacultyJpaRepo facultyJpaRepo;

    @Autowired
    private UserJpaRepo userJpaRepo;

    @Autowired
    private StudentJpaRepo studentJpaRepo;


    public List<HodResponse> getAllHods() {
        List<Hod> hodList=hodJpaRepo.findAll();
        List<HodResponse> hodResponses=new ArrayList<>();
        for(Hod hod:hodList){
            HodResponse hodResponse=toHodResponseDto(hod);
            hodResponses.add(hodResponse);
        }
        return hodResponses;
    }

    public HodResponse getHodProfile(Long hodId) {
        Hod hod=hodJpaRepo.findById(hodId).
                orElseThrow(() -> new ResourceNotFoundException("Hod not found with id " + hodId));
        return toHodResponseDto(hod);
    }
    public HodResponse getHodById(Long id) {
        Hod hod=hodJpaRepo.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Hod not found with id " + id));
        return toHodResponseDto(hod);
    }

    public List<RequestResponse> getPendingRequests(Long hodId) {

        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        // HOD sees only faculty-approved requests of their branch
        List<Request> list = requestJpaRepo.findByStudent_BranchAndStatus(hod.getBranch(), RequestStatus.CLASSTEACHER_APPROVED);

        List<RequestResponse> responseList = new ArrayList<>();
        for (Request request : list) {
            responseList.add(toResponseDto(request));
        }

        return responseList;
    }

    public void deleteHod(Long currentId,Long id) {
        User user=userJpaRepo.findById(currentId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        Role role=user.getRole();
        Hod hod = hodJpaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));
        if(role==Role.ADMIN){
            hodJpaRepo.delete(hod);
            userJpaRepo.delete(hod.getUser());
        }else{
            throw new AccessDeniedException("You are not authorized to delete the resource");
        }
    }

    public HodResponse getHodForStudent(Long studentId) {
        Student student=studentJpaRepo.findByStudentId(studentId).orElseThrow(()->new ResourceNotFoundException("The student is not found"));
        Hod hod=hodJpaRepo.findByBranch(student.getBranch());
        if (hod == null) {
            throw new ResourceNotFoundException("HOD is not assigned for this branch yet");
        }
        return toHodResponseDto(hod);

    }

    public RequestResponse handleHodAction(Long hodId, HodActionRequest dto) {

        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        Request request = requestJpaRepo.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        // Ensure request belongs to HOD's branch
        if (!request.getStudent().getBranch().equals(hod.getBranch())) {
            throw new BadRequestException("You cannot act on requests from another branch");
        }

        // Ensure request is pending for HOD
        if (request.getStatus() != RequestStatus.CLASSTEACHER_APPROVED) {
            throw new BadRequestException("Request already processed");
        }

        // Validate remarks for rejection
        if (!dto.getApproved()) {
            if (dto.getRemarks() == null || dto.getRemarks().isBlank()) {
                throw new BadRequestException("Remarks required when rejecting");
            }
        }

        // Save remarks
        if (dto.getApproved()) {
            request.setHodRemarks("Approved by HOD");
        } else {
            request.setHodRemarks(dto.getRemarks());
        }

        request.setUpdatedAt(LocalDateTime.now());

        // HOD Reject
        if (!dto.getApproved()) {
            request.setStatus(RequestStatus.HOD_REJECTED);
            requestJpaRepo.save(request);
            return toResponseDto(request);
        }

        // HOD Approve
        request.setStatus(RequestStatus.HOD_APPROVED);
        requestJpaRepo.save(request);

        // Attendance Notification Broadcast

        if (request.getRequestType() == RequestType.ATTENDANCE) {
            Student student = request.getStudent();
            // 1. Subject Teachers
            List<SubjectTeacher> subjectTeachers = subjectTeacherJpaRepo.findByBranchAndSectionAndYear(student.getBranch(), student.getSection(), student.getYear());
            // 2. CLASS TEACHERS
            ClassTeacher classTeacher = classTeacherJpaRepo.findByBranchAndSectionAndYear(student.getBranch(), student.getSection(), student.getYear());
            // 3. Combine + remove duplicates
            Set<Faculty> allTeachers = new HashSet<>();

            for (SubjectTeacher st : subjectTeachers) {
                allTeachers.add(st.getFaculty());
            }
            allTeachers.add(classTeacher.getFaculty());

            // 4. Prepare message
            String message = "Attendance approved for " +
                    student.getName() + " (" + student.getRollNumber() + ")";

            // 5. Send notification to ALL
            for (Faculty faculty : allTeachers) {
                notificationService.sendNotification(faculty, message);
            }
        }

        return toResponseDto(request);
    }

    public HodResponse updateHodDetails(HodUpdateReq req,Long id){
        if(req==null || req.isBlank()){
            throw new IllegalArgumentException("At least one field must be provided");
        }
        Hod hod=hodJpaRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Hod not found"));
        if(req.getName()!=null && !req.getName().isBlank()){
            hod.setName(WordUtils.capitalizeFully(req.getName().trim()));
        }
        if(req.getBranch()!=null && !req.getBranch().isBlank()){
            hod.setBranch(req.getBranch().trim().toUpperCase());
        }
        hodJpaRepo.save(hod);
        return toHodResponseDto(hod);
    }

    private RequestResponse toResponseDto(Request request) {
        return new RequestResponse(request.getId(), request.getRequestType(), request.getStatus(), request.getDescription(), request.getStudent().getName(), request.getFaculty().getName(), request.getFromDate(), request.getToDate(), request.getFacultyRemarks(), request.getHodRemarks(), request.getCreatedAt(), request.getUpdatedAt());
    }

    public HodResponse toHodResponseDto(Hod hod){
        return new HodResponse(hod.getId(),hod.getName(), hod.getBranch(),hod.getHodId());
    }

}
