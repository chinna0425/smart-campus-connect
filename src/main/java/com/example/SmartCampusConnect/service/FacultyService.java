package com.example.SmartCampusConnect.service;

import com.example.SmartCampusConnect.dtos.facultyDto.FacultyProfileResponse;
import com.example.SmartCampusConnect.dtos.facultyDto.FacultyResponse;
import com.example.SmartCampusConnect.dtos.facultyDto.FacultyUpdateRequest;
import com.example.SmartCampusConnect.enums.Role;
import com.example.SmartCampusConnect.exception.ResourceNotFoundException;
import com.example.SmartCampusConnect.model.ClassTeacher;
import com.example.SmartCampusConnect.model.Faculty;
import com.example.SmartCampusConnect.model.Hod;
import com.example.SmartCampusConnect.model.User;
import com.example.SmartCampusConnect.respository.*;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacultyService {

    @Autowired
    private RequestJpaRepo requestJpaRepo;

    @Autowired
    private FacultyJpaRepo facultyJpaRepo;

    @Autowired
    private HodJpaRepo hodJpaRepo;

    @Autowired
    private ClassTeacherJpaRepo classTeacherJpaRepo;

    @Autowired
    private UserJpaRepo userJpaRepo;

    public FacultyProfileResponse getFacultyProfile(Long facultyId) {
        Faculty faculty=facultyJpaRepo.findById(facultyId).orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id "+facultyId));
        ClassTeacher classTeacher=classTeacherJpaRepo.findByFaculty_Id(facultyId);
        FacultyProfileResponse response = new FacultyProfileResponse();
        response.setId(faculty.getId());
        response.setName(faculty.getName());
        response.setEmployeeId(faculty.getEmployeeId());
        response.setBranch(faculty.getBranch());

        // If CT exists, attach class teacher details
        if (classTeacher != null) {
            response.setClassTeacher(true);
            response.setCtBranch(classTeacher.getBranch());
            response.setCtSection(classTeacher.getSection());
            response.setCtYear(classTeacher.getYear());
        } else {
            response.setClassTeacher(false);
        }
        return response;
    }

    public FacultyResponse getFacultyById(Long id) {
        Faculty faculty=facultyJpaRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id "+id));
        return toFacultyResponseDto(faculty);
    }


    public List<FacultyResponse> getAllFacultyDetails(Long currentId) {
        Hod hod=hodJpaRepo.findById(currentId).orElseThrow(()-> new ResourceNotFoundException("Hod Not Found"));

        List<Faculty> facultyList=facultyJpaRepo.findByBranch(hod.getBranch());

        List<FacultyResponse> facultyResponses=new ArrayList<>();
        for(Faculty faculty:facultyList){
            FacultyResponse facultyResponse=toFacultyResponseDto(faculty);
            facultyResponses.add(facultyResponse);
        }
        return facultyResponses;
    }

    public FacultyResponse updateFacultyById(Long hodId,Long id, FacultyUpdateRequest facultyUpdateRequest) {
        Hod hod=hodJpaRepo.findById(hodId).orElseThrow(()-> new ResourceNotFoundException("Hod Not Found"));

        if(facultyUpdateRequest==null || facultyUpdateRequest.isEmpty()){
            throw new ResourceNotFoundException("For updating requires at least one field");
        }

        Faculty faculty=facultyJpaRepo.findById(id).orElseThrow(()-> new ResourceNotFoundException("Hod Not Found"));
        if(!hod.getBranch().equals(faculty.getBranch())){
            throw new AccessDeniedException("Not authorized to update");
        }
        if(facultyUpdateRequest.getBranch()!=null && !facultyUpdateRequest.getBranch().isBlank()){
            faculty.setBranch(facultyUpdateRequest.getBranch().trim().toUpperCase());
        }
        if(facultyUpdateRequest.getName()!=null && !facultyUpdateRequest.getName().isBlank()){
            faculty.setName(WordUtils.capitalizeFully(facultyUpdateRequest.getName().trim()));
        }
        facultyJpaRepo.save(faculty);
        return  toFacultyResponseDto(faculty);
    }

    public void deleteFaculty(Long currentId,Long id) {
        User user=userJpaRepo.findById(currentId).orElseThrow(()->new ResourceNotFoundException("User not found"));
        Role role=user.getRole();
        Faculty faculty=facultyJpaRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id "+id));
        if(role==Role.ADMIN){
            facultyJpaRepo.delete(faculty);
            userJpaRepo.delete(faculty.getUser());
        }else if(role==Role.HOD){
            facultyJpaRepo.delete(faculty);
            userJpaRepo.delete(faculty.getUser());
        }else{
            throw new AccessDeniedException("You are not authorized to delete the resource");
        }
    }

    public FacultyResponse toFacultyResponseDto(Faculty faculty){
        return new FacultyResponse(faculty.getId(),faculty.getName(),faculty.getEmployeeId(),faculty.getBranch());
    }

}