package com.example.SmartCampusConnect.service;

import com.example.SmartCampusConnect.enums.Role;
import com.example.SmartCampusConnect.exception.ResourceNotFoundException;
import com.example.SmartCampusConnect.model.*;
import com.example.SmartCampusConnect.respository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

    @Autowired
    private UserJpaRepo userJpaRepo;

    @Autowired
    private ClassTeacherJpaRepo classTeacherJpaRepo;

    @Autowired
    private SubjectTeacherJpaRepo subjectTeacherJpaRepo;

    @Autowired
    private HodJpaRepo hodJpaRepo;

    @Autowired
    private FacultyJpaRepo facultyJpaRepo;

    @Autowired
    private AdminJpaRepo adminJpaRepo;

    private Long currentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getUserId();
        }
        throw new RuntimeException("User authentication failed");
    }

    // Rule 1: CLASS TEACHER can access only their class students
    public boolean isClassTeacherOf(Student student) {

        Long currentId = currentUserId();

        Faculty faculty =facultyJpaRepo.findById(currentId)
                .orElse(null);
        if (faculty == null) return false;

        ClassTeacher classTeacher=classTeacherJpaRepo.findByFaculty_Id(currentId);
        if(classTeacher==null){
            return false;
        }

        return classTeacher.getBranch().equals(student.getBranch()) &&
                classTeacher.getSection().equals(student.getSection()) &&
                classTeacher.getYear().equals(student.getYear());
    }

    // Rule 2: SUBJECT TEACHER must be mapped to that class
    public boolean isSubjectTeacherOfClass(Student student) {

        Long currentId = currentUserId();

        Faculty faculty =facultyJpaRepo.findById(currentId)
                .orElse(null);
        if (faculty == null) return false;
        SubjectTeacher subjectTeacher=subjectTeacherJpaRepo.findByFaculty_Id(currentId);

       if(subjectTeacher==null){
           return false;
       }

        return subjectTeacherJpaRepo
                .existsByFaculty_IdAndBranchAndSectionAndYear(
                        currentId,
                        student.getBranch(),
                        student.getSection(),
                        student.getYear()
                );
    }

    // Rule 3: HOD can view only their department students
    public boolean isHodOf(Student student) {

        Long currentId = currentUserId();

        System.out.println(currentId);
        User user = userJpaRepo.findById(currentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRole().equals(Role.HOD)) return false;

        Hod hod = hodJpaRepo.findById(currentId).orElse(null);

        if (hod == null) return false;

        System.out.println(hod.getBranch()+student.getBranch());
        return hod.getBranch().equals(student.getBranch());
    }

    // FINAL CHECKER
    public void requireStudentViewAccess(Student student) throws AccessDeniedException {

        if (isClassTeacherOf(student)) return;

        if (isSubjectTeacherOfClass(student)) return;

        if (isHodOf(student)) return;

        throw new AccessDeniedException("You are not allowed to view this student");
    }
    public void requireStudentViewAccessForCTAndHodAndSTAndAdmin(Student student) throws AccessDeniedException {

        if(isAdmin(student)) return;
        if (isClassTeacherOf(student)) return;

        if (isSubjectTeacherOfClass(student)) return;

        if (isHodOf(student)) return;

        throw new AccessDeniedException("You are not allowed to view this student");
    }

    private boolean isAdmin(Student student) {
        Long currentId = currentUserId();

        System.out.println(currentId);
        User user = userJpaRepo.findById(currentId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRole().equals(Role.ADMIN)) return false;

        Admin admin = adminJpaRepo.findById(currentId).orElse(null);

        if (admin == null) return false;
        return true;
    }

    public void requireStudentAccessForHodAndCT(Student student) throws AccessDeniedException {

        if (isClassTeacherOf(student)) return;

        if (isHodOf(student)) return;

        throw new AccessDeniedException("You are not allowed to view this student");
    }
    public void requireStudentAccessForHodAndCTAndStudent(Student student) throws AccessDeniedException {
        Long currentId = currentUserId();

        if (student.getStudentId().equals(currentId)) {
            return;
        }
        if (isClassTeacherOf(student)) return;

        if (isHodOf(student)) return;

        throw new AccessDeniedException("You are not allowed to view this student");
    }

}
