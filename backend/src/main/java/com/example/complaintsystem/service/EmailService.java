// backend/src/main/java/com/example/complaintsystem/service/EmailService.java
package com.example.complaintsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to: " + to + ", Error: " + e.getMessage());
        }
    }

    @Async
    public void sendComplaintStatusUpdate(String studentEmail, String complaintTitle, String newStatus, String comment) {
        String subject = "Complaint Status Update: " + complaintTitle;
        String text = "Dear Student,\n\n" +
                     "Your complaint '" + complaintTitle + "' status has been updated.\n" +
                     "New Status: " + newStatus + "\n" +
                     (comment != null ? "Comment: " + comment + "\n" : "") +
                     "\nThank you for using our complaint system.\n\n" +
                     "Best regards,\nComplaint Management System";
        
        sendEmail(studentEmail, subject, text);
    }

    @Async
    public void sendNewComplaintAssignment(String authorityEmail, String complaintTitle, String category, String studentName) {
        String subject = "New Complaint Assigned: " + complaintTitle;
        String text = "Dear Authority,\n\n" +
                     "A new complaint has been assigned to you:\n" +
                     "Title: " + complaintTitle + "\n" +
                     "Category: " + category + "\n" +
                     "Submitted by: " + studentName + "\n" +
                     "\nPlease log in to the system to review and take action.\n\n" +
                     "Best regards,\nComplaint Management System";
        
        sendEmail(authorityEmail, subject, text);
    }

    @Async
    public void sendComplaintCreatedConfirmation(String studentEmail, String complaintTitle, String category) {
        String subject = "Complaint Submitted Successfully: " + complaintTitle;
        String text = "Dear Student,\n\n" +
                     "Your complaint has been successfully submitted:\n" +
                     "Title: " + complaintTitle + "\n" +
                     "Category: " + category + "\n" +
                     "\nWe will keep you updated on the progress.\n\n" +
                     "Best regards,\nComplaint Management System";
        
        sendEmail(studentEmail, subject, text);
    }
}
