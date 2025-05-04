package com.CourseManagerV1.CourseManagerV1.Controller;

import com.CourseManagerV1.CourseManagerV1.dto.CourseDTO;
import com.CourseManagerV1.CourseManagerV1.Service.CourseService;
import com.CourseManagerV1.CourseManagerV1.dto.EnrollmentInfo;
import com.CourseManagerV1.CourseManagerV1.dto.EnrollmentRequest;
//import com.CourseManagerV1.CourseManagerV1.dto.StudentEnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// student and admin controllers
@RestController
@RequestMapping("/api/courses")

public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/available")
    public List<CourseDTO> getAvailableCourses() {
        return courseService.getAvailableCourses();
    }

    @GetMapping("/enrolled")
    public List<CourseDTO> getEnrolledCourses(@RequestParam String studentEmail) {
        return courseService.getEnrolledCourses(studentEmail);
    }

    @PostMapping("/unenroll")
    public String unenrollCourse(@RequestBody EnrollmentRequest enrollmentRequest) {
        boolean success = courseService.unenrollCourse(enrollmentRequest.getEmail(), enrollmentRequest.getCourseId());
        return success ? "Unenrolled successfully." : "Not enrolled in this course.";
    }

    @PostMapping
    public CourseDTO addCourse(@RequestBody CourseDTO courseDTO) {
        return courseService.addCourse(courseDTO);
    }

    @PostMapping("/enroll")
    public String enrollCourse(@RequestBody EnrollmentRequest enrollmentRequest) {
        boolean success = courseService.enrollCourse(enrollmentRequest.getEmail(), enrollmentRequest.getCourseId());
        return success ? "Enrolled successfully." : "Failed to enroll in course.";
    }

    @GetMapping("/enrollments-list-all")
    public List<EnrollmentInfo> getAllEnrollments() {
        return courseService.getAllEnrollments();
    }


    @DeleteMapping("/delete-course-by-id")
    public String deleteCourseById(@RequestParam Long courseId) {
        boolean deleted = courseService.deleteCourseById(courseId);
        return deleted ? "Course deleted successfully." : "Course not found.";
    }
    @DeleteMapping("/remove-student")
    public String removeStudent(@RequestParam String studentEmail) {
        boolean removed = courseService.removeStudentFromSystem(studentEmail);
        return removed ? "Student removed successfully." : "Student not found.";
    }


}
