package com.CourseManagerV1.CourseManagerV1.Repository;

import com.CourseManagerV1.CourseManagerV1.Model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    void deleteByCourse_Id(Long courseId);

    void deleteByStudentEmail(String email);
    List<Enrollment> findByStudentEmail(String email);

    boolean existsByStudentEmailAndCourse_Id(String email, Long courseId);

    void deleteByStudentEmailAndCourse_Id(String email, Long courseId);

    boolean existsByCourse_Id(Long courseId);
}
