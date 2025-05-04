package com.CourseManagerV1.CourseManagerV1.dto;

public class CourseDTO {
    private Long id;
    private String name;
    private String duration;
    private String session;
    private int totalSlots;
    private int filledSlots;


    public CourseDTO() {
    }


    public CourseDTO(Long id, String name, String duration, String session, int totalSlots, int filledSlots) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.session = session;
        this.totalSlots = totalSlots;
        this.filledSlots = filledSlots;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public int getFilledSlots() {
        return filledSlots;
    }

    public void setFilledSlots(int filledSlots) {
        this.filledSlots = filledSlots;
    }
}
