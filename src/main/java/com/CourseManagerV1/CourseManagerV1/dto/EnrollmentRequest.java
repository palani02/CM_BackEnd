package com.CourseManagerV1.CourseManagerV1.dto;

public class EnrollmentRequest {
    private String email;
    private Long courseId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
