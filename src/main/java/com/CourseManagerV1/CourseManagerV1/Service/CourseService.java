package com.CourseManagerV1.CourseManagerV1.Service;

import com.CourseManagerV1.CourseManagerV1.Model.Enrollment;
import com.CourseManagerV1.CourseManagerV1.Model.Student;
import com.CourseManagerV1.CourseManagerV1.Repository.StudentRepository;
import com.CourseManagerV1.CourseManagerV1.dto.CourseDTO;
import com.CourseManagerV1.CourseManagerV1.Model.Course;
import com.CourseManagerV1.CourseManagerV1.Repository.CourseRepository;
import com.CourseManagerV1.CourseManagerV1.Repository.EnrollmentRepository;
import com.CourseManagerV1.CourseManagerV1.dto.EnrollmentInfo;
import com.CourseManagerV1.CourseManagerV1.dto.EnrollmentRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

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
    public boolean unenrollCourse(String studentEmail, Long courseId) {

        System.out.println("Attempting to unenroll. Email: " + studentEmail + ", Course ID: " + courseId);

        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isPresent()) {
            Course course = courseOpt.get();


            System.out.println("Found course: " + course.getName());

            boolean exists = enrollmentRepository.existsByStudentEmailAndCourse_Id(studentEmail, courseId);
            System.out.println("Enrollment exists: " + exists);

            if (!exists) {
                return false;
            }
            enrollmentRepository.deleteByStudentEmailAndCourse_Id(studentEmail, courseId);
            course.setFilledSlots(course.getFilledSlots() - 1);
            courseRepository.save(course);

            return true;
        } else {
            System.out.println("Course not found for ID: " + courseId);
            return false;
        }
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
            enrollmentRepository.deleteByCourse_Id(courseId);
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


}
