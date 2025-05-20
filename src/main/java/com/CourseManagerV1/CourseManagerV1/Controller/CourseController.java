package com.CourseManagerV1.CourseManagerV1.Controller;

import com.CourseManagerV1.CourseManagerV1.Model.UnenrollmentRequest;
import com.CourseManagerV1.CourseManagerV1.Repository.UnenrollmentRequestRepository;
import com.CourseManagerV1.CourseManagerV1.dto.CourseDTO;
import com.CourseManagerV1.CourseManagerV1.Service.CourseService;
import com.CourseManagerV1.CourseManagerV1.dto.EnrollmentInfo;
import com.CourseManagerV1.CourseManagerV1.dto.EnrollmentRequest;
import com.CourseManagerV1.CourseManagerV1.dto.UnenrollmentRequestDTO;

//import com.CourseManagerV1.CourseManagerV1.dto.StudentEnrollmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

// student and admin controllers
@RestController
@RequestMapping("/api/courses")

public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    UnenrollmentRequestRepository  unenrollmentRequestRepository;
    @GetMapping("/available")
    public List<CourseDTO> getAvailableCourses() {
        return courseService.getAvailableCourses();
    }

    @GetMapping("/enrolled")
    public List<CourseDTO> getEnrolledCourses(@RequestParam String studentEmail) {
        return courseService.getEnrolledCourses(studentEmail);
    }

    // unenroll controllers
    @PostMapping("/unenroll")
    public String requestUnenrollment(@RequestBody EnrollmentRequest enrollmentRequest) {
        return courseService.requestUnenrollment(enrollmentRequest.getEmail(), enrollmentRequest.getCourseId());
    }

    @GetMapping("/unenroll-requests")
    public List<UnenrollmentRequestDTO> getPendingRequests() {
        return unenrollmentRequestRepository.findByStatus("PENDING").stream()
                .map(req -> new UnenrollmentRequestDTO(
                        req.getId(),
                        req.getStudentEmail(),
                        req.getCourse().getId(),
                        req.getStatus()
                ))
                .collect(Collectors.toList());
    }



    @PostMapping("/approve-unenroll")
    public String approveUnenrollment(@RequestParam Long requestId) {
        return courseService.processUnenrollmentRequest(requestId, true);
    }

    @PostMapping("/reject-unenroll")
    public String rejectUnenrollment(@RequestParam Long requestId) {
        return courseService.processUnenrollmentRequest(requestId, false);
    }


    @PostMapping
    public CourseDTO addCourse(@RequestBody CourseDTO courseDTO) {
        return courseService.addCourse(courseDTO);
    }

    @PutMapping("/update-course")
    public String updateCourse(@RequestParam Long courseId, @RequestBody CourseDTO updatedCourseDTO) {
        return courseService.updateCourse(courseId, updatedCourseDTO);
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
        return deleted ? "Course deleted successfully." : "Course cannot be deleted: students are enrolled or course not found.";
    }
    @DeleteMapping("/remove-student")
    public String removeStudent(@RequestParam String studentEmail) {
        boolean removed = courseService.removeStudentFromSystem(studentEmail);
        return removed ? "Student removed successfully." : "Student not found.";
    }


}
