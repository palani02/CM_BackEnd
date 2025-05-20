package com.CourseManagerV1.CourseManagerV1.Service;

import com.CourseManagerV1.CourseManagerV1.Model.Enrollment;
import com.CourseManagerV1.CourseManagerV1.Model.Student;
import com.CourseManagerV1.CourseManagerV1.Model.UnenrollmentRequest;
import com.CourseManagerV1.CourseManagerV1.Repository.StudentRepository;
import com.CourseManagerV1.CourseManagerV1.Repository.UnenrollmentRequestRepository;
import com.CourseManagerV1.CourseManagerV1.dto.CourseDTO;
import com.CourseManagerV1.CourseManagerV1.Model.Course;
import com.CourseManagerV1.CourseManagerV1.Repository.CourseRepository;
import com.CourseManagerV1.CourseManagerV1.Repository.EnrollmentRepository;
import com.CourseManagerV1.CourseManagerV1.dto.EnrollmentInfo;
import com.CourseManagerV1.CourseManagerV1.dto.EnrollmentRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private UnenrollmentRequestRepository unenrollmentRequestRepository;

    @Autowired
    private CourseRepository courseRepository;


    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;


    // Get all course
    public List<CourseDTO> getAvailableCourses() {
        return courseRepository.findAll().stream()
                .map(c -> new CourseDTO(
                        c.getId(),
                        c.getName(),
                        c.getDuration(),
                        c.getSession(),
                        c.getTotalSlots(),
                        c.getFilledSlots()
                ))
                .collect(Collectors.toList());
    }

    // enroll to new course by email and course id
    public boolean enrollCourse(String studentEmail, Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();
            if (course.getFilledSlots() < course.getTotalSlots()) {
                course.setFilledSlots(course.getFilledSlots() + 1);
                courseRepository.save(course);

                Enrollment enrollStudent = new Enrollment();
                enrollStudent.setStudentEmail(studentEmail);
                enrollStudent.setCourse(course);
                enrollmentRepository.save(enrollStudent);
                return true;
            }
        }
        return false;
    }


    // fetch enroll course by email
    public List<CourseDTO> getEnrolledCourses(String studentEmail) {
        return enrollmentRepository.findByStudentEmail(studentEmail).stream()
                .map(e -> {
                    Course c = e.getCourse();
                    return new CourseDTO(
                            c.getId(),
                            c.getName(),
                            c.getDuration(),
                            c.getSession(),
                            c.getTotalSlots(),
                            c.getFilledSlots()
                    );
                })
                .collect(Collectors.toList());
    }

    // unenroll course by email and course id
    @Transactional
    // Student requests to unenroll
    public String requestUnenrollment(String studentEmail, Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return "Course not found.";
        }

        boolean alreadyRequested = unenrollmentRequestRepository
                .findAll()
                .stream()
                .anyMatch(req -> req.getStudentEmail().equals(studentEmail)
                        && req.getCourse().getId().equals(courseId)
                        && req.getStatus().equals("PENDING"));

        if (alreadyRequested) return "Unenrollment request already submitted.";

        UnenrollmentRequest request = new UnenrollmentRequest();
        request.setStudentEmail(studentEmail);
        request.setCourse(courseOpt.get());
        request.setStatus("PENDING");
        request.setRequestedAt(LocalDateTime.now());

        unenrollmentRequestRepository.save(request);
        return "Unenrollment request submitted.";
    }


    @Transactional
    public String processUnenrollmentRequest(Long requestId, boolean approve) {
        Optional<UnenrollmentRequest> reqOpt = unenrollmentRequestRepository.findById(requestId);
        if (reqOpt.isEmpty()) return "Request not found.";

        UnenrollmentRequest req = reqOpt.get();

        if (!req.getStatus().equals("PENDING")) return "Request already processed.";

        if (approve) {
            // Remove student from course
            enrollmentRepository.deleteByStudentEmailAndCourse_Id(req.getStudentEmail(), req.getCourse().getId());

            Course course = req.getCourse();
            course.setFilledSlots(course.getFilledSlots() - 1);
            courseRepository.save(course);

            req.setStatus("APPROVED");
        } else {
            req.setStatus("REJECTED");
        }

        unenrollmentRequestRepository.save(req);
        return "Request " + (approve ? "approved and student unenrolled." : "rejected.");
    }


    // Add new course from Admin Side access only
    public CourseDTO addCourse(CourseDTO courseDTO) {
        Course course = new Course();
        course.setName(courseDTO.getName());
        course.setDuration(courseDTO.getDuration());
        course.setSession(courseDTO.getSession());
        course.setTotalSlots(courseDTO.getTotalSlots());
        course.setFilledSlots(courseDTO.getFilledSlots());

        Course savedCourse = courseRepository.save(course);
        return new CourseDTO(
                savedCourse.getId(),
                savedCourse.getName(),
                savedCourse.getDuration(),
                savedCourse.getSession(),
                savedCourse.getTotalSlots(),
                savedCourse.getFilledSlots()
        );
    }

    // Admin Only access, get all the enrolled student List
    public List<EnrollmentInfo> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(enrollment -> {
                    EnrollmentInfo info = new EnrollmentInfo();
                    info.setStudentEmail(enrollment.getStudentEmail());
                    info.setCourseName(enrollment.getCourse().getName());
                    info.setSession(enrollment.getCourse().getSession());
                    return info;
                })
                .collect(Collectors.toList());
    }


    // Admin only access, remove the course list from the course DB itself

    @Transactional
    public boolean deleteCourseById(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();

            // Check if any students are enrolled
            boolean hasEnrollments = enrollmentRepository.existsByCourse_Id(courseId);

            if (hasEnrollments) {
                return false; // Don't delete if students are enrolled
            }

            courseRepository.deleteById(courseId);
            return true;
        }
        return false;
    }




    // Admin only access, Remove respective student from the DB
    @Transactional
    public boolean removeStudentFromSystem(String studentEmail) {
        if (!studentRepository.existsByEmail(studentEmail)) {
            return false;
        }

        // First Delete the Student Enrollments in Enrollemnt DB
        enrollmentRepository.deleteByStudentEmail(studentEmail);
        // Second Delete the Student form the Student DB itself
        studentRepository.deleteByEmail(studentEmail);
        return true;
    }

    @Transactional
    public String updateCourse(Long courseId, CourseDTO updatedCourseDTO) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return "Course not found.";
        }

        Course course = courseOpt.get();

        // Update fields
        course.setName(updatedCourseDTO.getName());
        course.setDuration(updatedCourseDTO.getDuration());
        course.setSession(updatedCourseDTO.getSession());
        course.setTotalSlots(updatedCourseDTO.getTotalSlots());

        // Ensure filledSlots doesn't exceed totalSlots
        int newFilledSlots = Math.min(updatedCourseDTO.getFilledSlots(), updatedCourseDTO.getTotalSlots());
        course.setFilledSlots(newFilledSlots);

        courseRepository.save(course);
        return "Course updated successfully.";
    }

}
