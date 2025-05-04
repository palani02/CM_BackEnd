package com.CourseManagerV1.CourseManagerV1.Repository;

import com.CourseManagerV1.CourseManagerV1.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, String> {
    Admin findByEmail(String email);
}
