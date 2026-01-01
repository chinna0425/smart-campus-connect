package com.example.SmartCampusConnect.service;

import com.example.SmartCampusConnect.dtos.subjectTeacherDto.AssignSubjectTeacherDto;
import com.example.SmartCampusConnect.dtos.subjectTeacherDto.SubjectTeacherResponseDto;
import com.example.SmartCampusConnect.dtos.subjectTeacherDto.SubjectTeacherUpdateRequest;
import com.example.SmartCampusConnect.exception.BadRequestException;
import com.example.SmartCampusConnect.exception.ResourceNotFoundException;
import com.example.SmartCampusConnect.model.Faculty;
import com.example.SmartCampusConnect.model.Hod;
import com.example.SmartCampusConnect.model.Student;
import com.example.SmartCampusConnect.model.SubjectTeacher;
import com.example.SmartCampusConnect.respository.FacultyJpaRepo;
import com.example.SmartCampusConnect.respository.HodJpaRepo;
import com.example.SmartCampusConnect.respository.StudentJpaRepo;
import com.example.SmartCampusConnect.respository.SubjectTeacherJpaRepo;
import jakarta.validation.Valid;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubjectTeacherService {

    @Autowired
    private FacultyJpaRepo facultyJpaRepo;

    @Autowired
    private SubjectTeacherJpaRepo subjectTeacherJpaRepo;

    @Autowired
    private StudentJpaRepo studentJpaRepo;

    @Autowired
    private HodJpaRepo hodJpaRepo;

    // Assign a faculty to a subject for a class
    public SubjectTeacherResponseDto assignSubjectTeacher(Long hodId,AssignSubjectTeacherDto dto) {

        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        Faculty faculty = facultyJpaRepo.findById(dto.getFacultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty not found"));

        boolean exists=subjectTeacherJpaRepo.
                existsBySubjectNameAndBranchAndSectionAndYear(
                        WordUtils.capitalizeFully(dto.getSubjectName().trim()),dto.getBranch().trim().toUpperCase(),dto.getSection().trim().toUpperCase(),dto.getYear());
        if(exists){
            throw new BadRequestException("Subject Teacher is already assigned");
        }

        // 1. HOD can assign only HIS department subjects
        if (!hod.getBranch().equalsIgnoreCase(dto.getBranch().trim().toUpperCase())) {
            throw new AccessDeniedException(
                    "You are not authorized to assign subject teachers for other departments"
            );
        }

        SubjectTeacher st = new SubjectTeacher();
        st.setFaculty(faculty);
        st.setSubjectName(WordUtils.capitalizeFully(dto.getSubjectName().trim()));
        st.setBranch(dto.getBranch().trim().toUpperCase());
        st.setSection(dto.getSection().trim().toUpperCase());
        st.setYear(dto.getYear());
        subjectTeacherJpaRepo.save(st);
        return toSubjectTeacherResponse(st);
    }

    public List<SubjectTeacherResponseDto> getSubjectTeachers(Long hodId, String branch, String section, Integer year) {

        // 1. Validate HOD
        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        String hodBranch = hod.getBranch();

        // 2. If branch filter is provided AND does not match HOD branch → DENY
        if (branch != null && !branch.equalsIgnoreCase(hodBranch.trim().toUpperCase())) {
            throw new AccessDeniedException("You are not authorized to access other department subject teachers");
        }

        // 3. If branch is NULL automatically using HOD's branch
        if (branch == null) {
            branch = hodBranch;
        }

        // 4. Now fetch only inside the valid branch
        List<SubjectTeacher> subjectTeachers =
                subjectTeacherJpaRepo.findByFilters(branch.trim().toUpperCase(), section.trim().toUpperCase(), year);

        // 5. Convert to response DTO
        return subjectTeachers.stream()
                .map(this::toSubjectTeacherResponse)
                .toList();
    }

    public SubjectTeacherResponseDto updateSubjectTeacher(Long hodId,Long id, SubjectTeacherUpdateRequest dto) {
        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        if(dto==null || dto.isEmpty()){
            throw new IllegalArgumentException("At least one field is required for update");
        }

        SubjectTeacher subjectTeacher=subjectTeacherJpaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject Teacher not found"));

        if(!hod.getBranch().equals(subjectTeacher.getBranch())){
            throw new AccessDeniedException("You are not authorized to update other department subject teachers details");
        }

        if(dto.getFacultyId()!=null){
            Faculty faculty = facultyJpaRepo.findById(dto.getFacultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty not found with id: " + dto.getFacultyId()));
            subjectTeacher.setFaculty(faculty);
        }

        if(dto.getSubjectName()!=null && !dto.getSubjectName().isBlank()){
            subjectTeacher.setSubjectName(WordUtils.capitalizeFully(dto.getSubjectName().trim()));
        }

        if(dto.getYear()!=null){
            subjectTeacher.setYear(dto.getYear());
        }

        if(dto.getSection()!=null && dto.getSection().isBlank()){
            subjectTeacher.setSection(dto.getSection().trim().toUpperCase());
        }

        if(dto.getBranch()!=null && dto.getBranch().isBlank()){
            subjectTeacher.setBranch(dto.getBranch().trim().toUpperCase());
        }
        subjectTeacherJpaRepo.save(subjectTeacher);
        return toSubjectTeacherResponse(subjectTeacher);
    }

    public List<SubjectTeacherResponseDto> getSubjectTeachersForStudent(Long studentId) {
        // 1. Get student details
        Student student = studentJpaRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // 2. Get subject teachers based on student's class
        List<SubjectTeacher> subjectTeachers = subjectTeacherJpaRepo.findByBranchAndSectionAndYear(
                student.getBranch(),
                student.getSection(),
                student.getYear()
        );

        if (subjectTeachers.isEmpty()) {
            throw new BadRequestException("No subject teachers assigned for your class");
        }

        // 3. Convert to DTO (recommended)
        List<SubjectTeacherResponseDto> responseDtos=new ArrayList<>();
        for(SubjectTeacher subjectTeacher:subjectTeachers){
            SubjectTeacherResponseDto response=toSubjectTeacherResponse(subjectTeacher);
            responseDtos.add(response);
        }
        return responseDtos;
    }

    public SubjectTeacherResponseDto getSubjectTeacherById(Long hodId, Long id) {
        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));
        SubjectTeacher subjectTeacher=subjectTeacherJpaRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject Teacher not found"));
        if(!hod.getBranch().equals(subjectTeacher.getBranch())){
            throw new AccessDeniedException("You are not authorized to access other department subject teacher details");
        }
        return toSubjectTeacherResponse(subjectTeacher);

    }

    public SubjectTeacherResponseDto toSubjectTeacherResponse(SubjectTeacher st){
        SubjectTeacherResponseDto responseDto=
                new SubjectTeacherResponseDto(st.getId(),st.getFaculty().getId(),st.getFaculty().getName(),st.getSubjectName(),st.getBranch(),st.getSection(),st.getYear());
        return responseDto;
    }

    public void deleteSubjectTeacherById(Long hodId,Long subjectId) {
        SubjectTeacher subjectTeacher=subjectTeacherJpaRepo.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject Teacher not found"));
        Hod hod = hodJpaRepo.findById(hodId)
                .orElseThrow(() -> new ResourceNotFoundException("HOD not found"));

        if (!subjectTeacher.getBranch().equals(hod.getBranch())) {
            throw new AccessDeniedException("You cannot delete ST from another department");
        }
        subjectTeacherJpaRepo.delete(subjectTeacher);
    }

}
