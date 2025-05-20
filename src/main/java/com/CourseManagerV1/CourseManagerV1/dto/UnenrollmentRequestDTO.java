
package com.CourseManagerV1.CourseManagerV1.dto;

public class UnenrollmentRequestDTO {
    private Long id;
    private String studentEmail;
    private Long courseId;
    private String status;



    public UnenrollmentRequestDTO(Long id, String studentEmail, Long courseId, String status) {
        this.id = id;
        this.studentEmail = studentEmail;
        this.courseId = courseId;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getStatus() {
        return status;
    }




}
