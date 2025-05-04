package com.CourseManagerV1.CourseManagerV1.dto;

import lombok.Data;

@Data
public class EnrollmentInfo {
    private String studentEmail;
    private String courseName;
    private String session;

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
