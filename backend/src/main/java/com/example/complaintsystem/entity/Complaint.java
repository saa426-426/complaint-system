// backend/src/main/java/com/example/complaintsystem/entity/Complaint.java
package com.example.complaintsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Data
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    private String department;
    private String hostel;
    private String imagePath;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "complaint_id")
    private List<Update> updates = new ArrayList<>();

    public enum Category { HOSTEL, ACADEMIC, MAINTENANCE }
    public enum Status { PENDING, IN_PROGRESS, RESOLVED }
}

@Entity
@Table(name = "updates")
@Data
class Update {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Complaint.Status status;

    private String comment;

    @ManyToOne
    private User updatedBy;

    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "complaint_id")
    private Long complaintId;
}
