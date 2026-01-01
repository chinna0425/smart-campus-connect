package com.example.SmartCampusConnect.service;

import com.example.SmartCampusConnect.dtos.AdminDto.AdminCreateDto;
import com.example.SmartCampusConnect.dtos.AdminDto.AdminResponseDto;
import com.example.SmartCampusConnect.dtos.facultyDto.FacultyRegistrationRequest;
import com.example.SmartCampusConnect.dtos.facultyDto.FacultyRegistrationResponse;
import com.example.SmartCampusConnect.dtos.hodDto.HodRegistrationRequest;
import com.example.SmartCampusConnect.dtos.hodDto.HodRegistrationResponse;
import com.example.SmartCampusConnect.dtos.loginDto.LoginRequest;
import com.example.SmartCampusConnect.dtos.studentDto.StudentRegistrationRequest;
import com.example.SmartCampusConnect.dtos.studentDto.StudentRegistrationResponse;
import com.example.SmartCampusConnect.enums.Role;
import com.example.SmartCampusConnect.exception.BadRequestException;
import com.example.SmartCampusConnect.exception.ConflictException;
import com.example.SmartCampusConnect.model.*;
import com.example.SmartCampusConnect.respository.*;
import jakarta.validation.Valid;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
public class AuthService{
    @Autowired
    private UserJpaRepo userJpaRepo;

    @Autowired
    private StudentJpaRepo studentJpaRepo;

    @Autowired
    private FacultyJpaRepo facultyJpaRepo;

    @Autowired
    private HodJpaRepo hodJpaRepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AdminJpaRepo adminJpaRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public AdminResponseDto registerAdmin(@Valid AdminCreateDto adminCreateReq) {
        if(userJpaRepo.existsByEmail(adminCreateReq.getEmail().trim().toLowerCase())){
            throw new ConflictException("Email already exists");
        }
        User user=new User(adminCreateReq.getEmail().trim().toLowerCase(), passwordEncoder.encode(adminCreateReq.getPassword()), Role.ADMIN,true);
        userJpaRepo.save(user);

        Admin admin=new Admin();
        String adminId=generateUniqueAdminId();
        admin.setName(WordUtils.capitalizeFully(adminCreateReq.getName().trim()));
        admin.setUser(user);
        admin.setAdminId(adminId);
        adminJpaRepo.save(admin);
        AdminResponseDto response=new AdminResponseDto(admin.getId(),admin.getName(),admin.getAdminId());
        return response;
    }

    public StudentRegistrationResponse registerStudent(StudentRegistrationRequest studentReq) {
        if(userJpaRepo.existsByEmail(studentReq.getEmail().trim().toLowerCase())){
            throw new ConflictException("Email already exists");
        }
        if(studentJpaRepo.existsByRollNumber(studentReq.getRollNumber().trim().toUpperCase())){
            throw new ConflictException("RollNumber already exists");
        }
        User user=new User(studentReq.getEmail().trim().toLowerCase(), passwordEncoder.encode(studentReq.getPassword()), Role.STUDENT,true);
        userJpaRepo.save(user);
        Student student=new Student(WordUtils.capitalizeFully(studentReq.getName().trim()),studentReq.getBranch().trim().toUpperCase(),studentReq.getYear(),studentReq.getRollNumber().trim().toUpperCase(),user,studentReq.getSection().trim().toUpperCase());

        studentJpaRepo.save(student);

        StudentRegistrationResponse response = new StudentRegistrationResponse(student.getStudentId(), student.getName(), student.getSection(),student.getRollNumber(),student.getBranch(),student.getYear(),"Student registered successfully");
        return response;
    }

    public FacultyRegistrationResponse registerFaculty(FacultyRegistrationRequest facultyReq) {
        if(userJpaRepo.existsByEmail(facultyReq.getEmail().trim().toLowerCase())){
            throw new ConflictException("Email already exists");
        }
        User user=new User(facultyReq.getEmail().trim().toLowerCase(),passwordEncoder.encode(facultyReq.getPassword()),Role.FACULTY,true);
        userJpaRepo.save(user);
        String employeeId= generateUniqueEmployeeId();

        Faculty faculty=new Faculty(facultyReq.getBranch().trim().toUpperCase(),WordUtils.capitalizeFully(facultyReq.getName().trim()),user,employeeId);
        facultyJpaRepo.save(faculty);
        return  new FacultyRegistrationResponse(faculty.getId(),faculty.getName(),faculty.getEmployeeId(),faculty.getBranch(),"Faculty Created Successfully");
    }

    public HodRegistrationResponse registerHod(@Valid HodRegistrationRequest hodReq) {
        if (hodJpaRepo.existsByBranch(hodReq.getBranch())) {
            throw new ConflictException("HOD already exists for branch: " + hodReq.getBranch());
        }

        if(userJpaRepo.existsByEmail(hodReq.getEmail().trim().toLowerCase())){
            throw new ConflictException("Email already exists");
        }
        User user=new User(hodReq.getEmail().trim().toLowerCase(),passwordEncoder.encode(hodReq.getPassword()),Role.HOD,true);
        userJpaRepo.save(user);

        String hodId=generateUniqueHodId();
        Hod hod=new Hod();
        hod.setHodId(hodId);
        hod.setBranch(hodReq.getBranch().trim().toUpperCase());
        hod.setUser(user);
        hod.setName(WordUtils.capitalizeFully(hodReq.getName().trim()));
        hodJpaRepo.save(hod);
        return new HodRegistrationResponse(hod.getId(), hod.getBranch(), hod.getName(),"HOD created Successfully",hod.getHodId());
    }

    public String login(LoginRequest req) {

        User user = userJpaRepo.findByEmail(req.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }
        if (!user.getRole().name().equalsIgnoreCase(req.getRole())) {
            throw new BadCredentialsException("You are not allowed to login as " + req.getRole());
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        return jwtService.generateToken(userDetails);
    }

    public String generateUniqueEmployeeId() {
        SecureRandom random = new SecureRandom();
        String empId;

        do {
            int num = random.nextInt(9000) + 1000; // 1000-9999
            empId = "EMP" + num;
        } while (facultyJpaRepo.existsByEmployeeId(empId));

        return empId;
    }
    public String generateUniqueHodId() {
        SecureRandom random = new SecureRandom();
        String hodId;

        do {
            int num = random.nextInt(9000) + 1000; // 1000-9999
            hodId = "HOD" + num;
        } while (hodJpaRepo.existsByHodId(hodId));
        return hodId;
    }

    public String generateUniqueAdminId() {
        SecureRandom random = new SecureRandom();
        String adminId;

        do {
            int num = random.nextInt(9000) + 1000; // 1000-9999
            adminId = "ADMIN" + num;
        } while (adminJpaRepo.existsByAdminId(adminId));
        return adminId;
    }

}
