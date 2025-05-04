package com.CourseManagerV1.CourseManagerV1.Service;

import com.CourseManagerV1.CourseManagerV1.Model.Admin;
import com.CourseManagerV1.CourseManagerV1.Model.Student;
import com.CourseManagerV1.CourseManagerV1.Repository.AdminRepository;
import com.CourseManagerV1.CourseManagerV1.Repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

// Login and signin Service/ business logic
@Service
public class AuthService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    // For Login
    public Map<String, String> authenticate(String email, String password, String role) {
        Map<String, String> response = new HashMap<>();
        email = email.trim().toLowerCase();
        password = password.trim();

        if (role == null || role.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Role is required (admin/student)");
            return response;
        }

        switch (role.toLowerCase()) {
            case "student":
                Student student = studentRepository.findByEmail(email);
                if (student != null && student.getPassword().equals(password)) {
                    response.put("status", "success");
                    response.put("message", "Student authenticated successfully");
                    response.put("role", "student");
                } else {
                    response.put("status", "error");
                    response.put("message", "Invalid student credentials");
                }
                break;

            case "admin":
                Admin admin = adminRepository.findByEmail(email);
                if (admin != null && admin.getPassword().equals(password)) {
                    response.put("status", "success");
                    response.put("message", "Admin authenticated successfully");
                    response.put("role", "admin");
                } else {
                    response.put("status", "error");
                    response.put("message", "Invalid admin credentials");
                }
                break;

            default:
                response.put("status", "error");
                response.put("message", "Unsupported role: " + role);
        }

        return response;
    }

    // for New student
    public Map<String, String> registerStudent(String email, String password) {
        Map<String, String> response = new HashMap<>();
        email = email.trim().toLowerCase();
        password = password.trim();

        if (studentRepository.findByEmail(email) != null) {
            response.put("status", "error");
            response.put("message", "Email already exists. Please login.");
            return response;
        }

        Student newStudent = new Student();
        newStudent.setEmail(email);
        newStudent.setPassword(password);
        studentRepository.save(newStudent);

        response.put("status", "success");
        response.put("message", "Student registered successfully");
        return response;
    }


    // Forgot password
    public Map<String, String> forgotPassword(String email, String currentPassword, String newPassword, String role) {
        Map<String, String> response = new HashMap<>();
        email = email.trim().toLowerCase();
        currentPassword = currentPassword.trim();
        newPassword = newPassword.trim();

        switch (role.toLowerCase()) {
            case "student":
                Student student = studentRepository.findByEmail(email);
                if (student != null) {
                    if (student.getPassword().equals(currentPassword)) {
                        student.setPassword(newPassword);
                        studentRepository.save(student);
                        response.put("status", "success");
                        response.put("message", "Password updated successfully for student.");
                    } else {
                        response.put("status", "error");
                        response.put("message", "Current password is incorrect.");
                    }
                } else {
                    response.put("status", "error");
                    response.put("message", "Student not found with this email.");
                }
                break;

            case "admin":
                Admin admin = adminRepository.findByEmail(email);
                if (admin != null) {
                    if (admin.getPassword().equals(currentPassword)) {
                        admin.setPassword(newPassword);
                        adminRepository.save(admin);
                        response.put("status", "success");
                        response.put("message", "Password updated successfully for admin.");
                    } else {
                        response.put("status", "error");
                        response.put("message", "Current password is incorrect.");
                    }
                } else {
                    response.put("status", "error");
                    response.put("message", "Admin not found with this email.");
                }
                break;

            default:
                response.put("status", "error");
                response.put("message", "Unsupported role: " + role);
        }

        return response;
    }


}
