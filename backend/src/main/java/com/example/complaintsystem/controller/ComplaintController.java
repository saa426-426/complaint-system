// backend/src/main/java/com/example/complaintsystem/controller/ComplaintController.java
package com.example.complaintsystem.controller;

import com.example.complaintsystem.entity.Complaint;
import com.example.complaintsystem.entity.User;
import com.example.complaintsystem.repository.UserRepository;
import com.example.complaintsystem.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createComplaint(@RequestBody Complaint complaint, @AuthenticationPrincipal User user) {
        try {
            Complaint createdComplaint = complaintService.createComplaint(complaint, user);
            return ResponseEntity.ok(createdComplaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Complaint>> getUserComplaints(@AuthenticationPrincipal User user) {
        List<Complaint> complaints = complaintService.getComplaintsForUser(user);
        return ResponseEntity.ok(complaints);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Complaint>> getAllComplaints(@AuthenticationPrincipal User user) {
        if (user.getRole() != User.Role.ADMIN) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> update, 
                                         @AuthenticationPrincipal User user) {
        try {
            Complaint.Status status = Complaint.Status.valueOf(update.get("status"));
            String comment = update.get("comment");
            
            Complaint updatedComplaint = complaintService.updateStatus(id, status, comment, user);
            return ResponseEntity.ok(updatedComplaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaint(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Optional<Complaint> complaint = complaintService.getComplaintById(id);
        if (complaint.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Authorization check
        Complaint comp = complaint.get();
        if (user.getRole() == User.Role.STUDENT && !comp.getStudent().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        if (user.getRole() != User.Role.ADMIN && !comp.getAssignedTo().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(comp);
    }
}
