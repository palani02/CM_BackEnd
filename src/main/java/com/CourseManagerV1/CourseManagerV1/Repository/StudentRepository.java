package com.CourseManagerV1.CourseManagerV1.Repository;
import com.CourseManagerV1.CourseManagerV1.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String> {
    Student findByEmail(String email);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);
}
