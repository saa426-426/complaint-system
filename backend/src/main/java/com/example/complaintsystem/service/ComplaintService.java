// backend/src/main/java/com/example/complaintsystem/service/ComplaintService.java
package com.example.complaintsystem.service;

import com.example.complaintsystem.entity.Complaint;
import com.example.complaintsystem.entity.User;
import com.example.complaintsystem.repository.ComplaintRepository;
import com.example.complaintsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public Complaint createComplaint(Complaint complaint, User student) {
        complaint.setStudent(student);
        complaint.setDepartment(student.getDepartment());
        complaint.setHostel(student.getHostel());

        // Auto-assign based on category
        User assignedTo = null;
        if (complaint.getCategory() == Complaint.Category.HOSTEL) {
            List<User> hostelHeads = userRepository.findByRoleAndHostel(User.Role.HOSTEL_HEAD, student.getHostel());
            if (!hostelHeads.isEmpty()) {
                assignedTo = hostelHeads.get(0); // Assign to first available hostel head
            }
        } else if (complaint.getCategory() == Complaint.Category.MAINTENANCE) {
            List<User> maintenanceStaff = userRepository.findByRole(User.Role.MAINTENANCE_STAFF);
            if (!maintenanceStaff.isEmpty()) {
                assignedTo = maintenanceStaff.get(0);
            }
        } else if (complaint.getCategory() == Complaint.Category.ACADEMIC) {
            List<User> hods = userRepository.findByRoleAndDepartment(User.Role.HOD, student.getDepartment());
            if (!hods.isEmpty()) {
                assignedTo = hods.get(0);
            }
        }

        if (assignedTo == null) {
            // Fallback to admin if no specific authority found
            List<User> admins = userRepository.findByRole(User.Role.ADMIN);
            if (!admins.isEmpty()) {
                assignedTo = admins.get(0);
            } else {
                throw new RuntimeException("No authority available to assign complaint");
            }
        }

        complaint.setAssignedTo(assignedTo);
        Complaint savedComplaint = complaintRepository.save(complaint);

        // Send email notifications
        emailService.sendComplaintCreatedConfirmation(
            student.getEmail(), 
            complaint.getTitle(), 
            complaint.getCategory().toString()
        );

        emailService.sendNewComplaintAssignment(
            assignedTo.getEmail(),
            complaint.getTitle(),
            complaint.getCategory().toString(),
            student.getUsername()
        );

        return savedComplaint;
    }

    public List<Complaint> getComplaintsForUser(User user) {
        if (user.getRole() == User.Role.STUDENT) {
            return complaintRepository.findByStudentId(user.getId());
        } else if (user.getRole() == User.Role.HOSTEL_HEAD) {
            return complaintRepository.findByCategoryAndHostel(Complaint.Category.HOSTEL, user.getHostel());
        } else if (user.getRole() == User.Role.MAINTENANCE_STAFF) {
            return complaintRepository.findByCategory(Complaint.Category.MAINTENANCE);
        } else if (user.getRole() == User.Role.HOD) {
            return complaintRepository.findByCategoryAndDepartment(Complaint.Category.ACADEMIC, user.getDepartment());
        } else if (user.getRole() == User.Role.ADMIN) {
            return complaintRepository.findAll();
        }
        return List.of();
    }

    @Transactional
    public Complaint updateStatus(Long id, Complaint.Status status, String comment, User updater) {
        Optional<Complaint> optionalComplaint = complaintRepository.findById(id);
        if (optionalComplaint.isEmpty()) {
            throw new RuntimeException("Complaint not found");
        }
        Complaint complaint = optionalComplaint.get();

        // Role-based authorization
        if (updater.getRole() != User.Role.ADMIN && 
            !complaint.getAssignedTo().getId().equals(updater.getId())) {
            throw new RuntimeException("Not authorized to update this complaint");
        }

        complaint.setStatus(status);
        
        // Create update record
        Complaint.Update update = new Complaint.Update();
        update.setStatus(status);
        update.setComment(comment);
        update.setUpdatedBy(updater);
        update.setComplaintId(id);
        complaint.getUpdates().add(update);

        // Handle special case: Hostel head assigning to maintenance
        if (updater.getRole() == User.Role.HOSTEL_HEAD && status == Complaint.Status.IN_PROGRESS) {
            List<User> maintenanceStaff = userRepository.findByRole(User.Role.MAINTENANCE_STAFF);
            if (!maintenanceStaff.isEmpty()) {
                complaint.setAssignedTo(maintenanceStaff.get(0));
            }
        }

        Complaint updatedComplaint = complaintRepository.save(complaint);

        // Send email notification to student
        emailService.sendComplaintStatusUpdate(
            complaint.getStudent().getEmail(),
            complaint.getTitle(),
            status.toString(),
            comment
        );

        return updatedComplaint;
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    public List<Complaint> getComplaintsByStatus(Complaint.Status status) {
        return complaintRepository.findByStatus(status);
    }

    public List<Complaint> getComplaintsByAssignedTo(User user) {
        return complaintRepository.findByAssignedToId(user.getId());
    }
}
