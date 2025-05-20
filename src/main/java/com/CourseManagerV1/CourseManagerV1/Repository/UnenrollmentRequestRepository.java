package com.CourseManagerV1.CourseManagerV1.Repository;

import com.CourseManagerV1.CourseManagerV1.Model.UnenrollmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UnenrollmentRequestRepository extends JpaRepository<UnenrollmentRequest, Long> {
    List<UnenrollmentRequest> findByStatus(String status);
}
