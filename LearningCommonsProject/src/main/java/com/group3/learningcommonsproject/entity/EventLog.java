package com.group3.learningcommonsproject.entity;

import com.group3.learningcommonsproject.entities.StaffInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "Event_Log")
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private StaffInfo staff;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "point_event_id", nullable = false)
    private PointEvent pointEvent;

    @Column(name = "points_applied", nullable = false)
    private int pointsApplied;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "comments", length = 255)
    private String comments;

    public EventLog() {
    }

    public EventLog(Long id, StaffInfo staff, PointEvent pointEvent, int pointsApplied, LocalDate eventDate, String comments) {
        this.id = id;
        this.staff = staff;
        this.pointEvent = pointEvent;
        this.pointsApplied = pointsApplied;
        this.eventDate = eventDate;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StaffInfo getStaff() {
        return staff;
    }

    public void setStaff(StaffInfo staff) {
        this.staff = staff;
    }

    public PointEvent getPointEvent() {
        return pointEvent;
    }

    public void setPointEvent(PointEvent pointEvent) {
        this.pointEvent = pointEvent;
    }

    public int getPointsApplied() {
        return pointsApplied;
    }

    public void setPointsApplied(int pointsApplied) {
        this.pointsApplied = pointsApplied;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}