package com.example.SmartCampusConnect.respository;

import com.example.SmartCampusConnect.enums.RequestStatus;
import com.example.SmartCampusConnect.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestJpaRepo extends JpaRepository<Request,Long> {
    List<Request> findByStudent_StudentId(Long studentId);

    List<Request> findByStudent_BranchAndStatus(String branch, RequestStatus requestStatus);

    List<Request> findByStudent_Branch(String hodBranch);

    List<Request> findByStudent_BranchAndStudent_SectionAndStudent_Year(String branch, String section, Integer year);

    List<Request> findByStudent_BranchAndStudent_SectionAndStudent_YearAndStatus(String branch, String section, Integer year, RequestStatus requestStatus);
}
