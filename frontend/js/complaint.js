// frontend/js/complaint.js
class ComplaintManager {
    static API_BASE = Auth.API_BASE;

    static createComplaint(complaintData) {
        return fetch(`${this.API_BASE}/complaints`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...Auth.getAuthHeader()
            },
            body: JSON.stringify(complaintData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to create complaint');
            }
            return response.json();
        });
    }

    static getUser Complaints() {
        return fetch(`${this.API_BASE}/complaints`, {
            headers: Auth.getAuthHeader()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch complaints');
            }
            return response.json();
        });
    }

    static getAllComplaints() {
        return fetch(`${this.API_BASE}/complaints/all`, {
            headers: Auth.getAuthHeader()
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch all complaints');
            }
            return response.json();
        });
    }

    static updateStatus(complaintId, status, comment) {
        return fetch(`${this.API_BASE}/complaints/${complaintId}/status`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...Auth.getAuthHeader()
            },
            body: JSON.stringify({ status, comment })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to update status');
            }
            return response.json();
        });
    }

    static renderComplaint(complaint, isAdmin = false) {
        const statusBadge = Utils.getStatusBadge(complaint.status);
        const categoryBadge = Utils.getCategoryBadge(complaint.category);
        const createdAt = Utils.formatDate(complaint.createdAt);
        const updatesHtml = complaint.updates ? complaint.updates.map(update => 
            `<small class="text-muted d-block">${Utils.formatDate(update.updatedAt)} - ${update.status} by ${update.updatedBy?.username || 'Unknown'}: ${update.comment || ''}</small>`
        ).join('') : '';

        let actionsHtml = '';
        if (Auth.getRole() !== 'STUDENT' && !isAdmin) {
            actionsHtml = `
                <div class="mt-2">
                    <select class="form-select form-select-sm d-inline w-auto" onchange="ComplaintManager.updateStatusQuick(${complaint.id}, this.value, 'Status updated')">
                        <option value="PENDING" ${complaint.status === 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="IN_PROGRESS" ${complaint.status === 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                        <option value="RESOLVED" ${complaint.status === 'RESOLVED' ? 'selected' : ''}>Resolved</option>
                    </select>
                    <button class="btn btn-sm btn-outline-primary ms-2" onclick="ComplaintManager.showUpdateModal(${complaint.id})">Update</button>
                </div>
            `;
        }

        return `
            <div class="card complaint-card">
                <div class="card-body">
                    <h6 class="card-title">${complaint.title}</h6>
                    <p class="card-text">${complaint.description}</p>
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span>${categoryBadge}</span>
                        <span>${statusBadge}</span>
                    </div>
                    <small class="text-muted">Created: ${createdAt} by ${complaint.student?.username || 'Unknown'}</small>
                    ${updatesHtml}
                    ${actionsHtml}
                </div>
            </div>
        `;
    }

    static updateStatusQuick(id, status, comment) {
        this.updateStatus(id, status, comment)
            .then(() => {
                Utils.showMessage('successMessage', 'Status updated successfully!');
                setTimeout(() => location.reload(), 1000);
            })
            .catch(error => Utils.showMessage('errorMessage', error.message, true));
    }

    static showUpdateModal(complaintId) {
        // Simple modal implementation (you can enhance with Bootstrap modal)
        const comment = prompt('Enter update comment:');
        if (comment) {
            const status = prompt('Enter new status (PENDING, IN_PROGRESS, RESOLVED):');
            if (status) {
                this.updateStatus(complaintId, status, comment)
                    .then(() => {
                        Utils.showMessage('successMessage', 'Complaint updated!');
                        setTimeout(() => location.reload(), 1000);
                    })
                    .catch(error => Utils.showMessage('errorMessage', error.message, true));
            }
        }
    }
}

// Global functions for onclick events
window.ComplaintManager = ComplaintManager;
