// backend/src/main/java/com/example/complaintsystem/repository/ComplaintRepository.java
package com.example.complaintsystem.repository;
import com.example.complaintsystem.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentId(Long studentId);
    List<Complaint> findByCategoryAndHostel(Complaint.Category category, String hostel);
    List<Complaint> findByCategory(Complaint.Category category);
    List<Complaint> findByCategoryAndDepartment(Complaint.Category category, String department);
    List<Complaint> findByAssignedToId(Long assignedToId);
    List<Complaint> findByStatus(Complaint.Status status);
}
