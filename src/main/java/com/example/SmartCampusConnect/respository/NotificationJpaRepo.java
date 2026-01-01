package com.example.SmartCampusConnect.respository;

import com.example.SmartCampusConnect.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface NotificationJpaRepo extends JpaRepository<Notification,Long> {
    List<Notification> findByFaculty_Id(Long facultyId);
}
