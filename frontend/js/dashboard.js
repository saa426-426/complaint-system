// frontend/js/dashboard.js
class Dashboard {
    static init() {
        if (!Auth.isAuthenticated()) {
            window.location.href = '../html/login.html';
            return;
        }

        const role = Auth.getRole();
        document.title = `${role.toUpperCase()} Dashboard - Complaint System`;

        this.loadComplaints();
        this.startPolling();
        
        // Setup logout
        document.getElementById('logoutBtn')?.addEventListener('click', () => Auth.logout());
    }

    static loadComplaints() {
        Utils.showLoading(true);
        const role = Auth.getRole();
        let promise;

        if (role === 'ADMIN') {
            promise = ComplaintManager.getAllComplaints();
        } else {
            promise = ComplaintManager.getUser Complaints();
        }

        promise
            .then(complaints => {
                const container = document.getElementById('complaintsContainer');
                if (container) {
                    container.innerHTML = complaints.map(c => ComplaintManager.renderComplaint(c, role === 'ADMIN')).join('');
                }
                Utils.showLoading(false);
            })
            .catch(error => {
                Utils.showMessage('errorMessage', error.message, true);
                Utils.showLoading(false);
            });
    }

    static startPolling() {
        setInterval(() => {
            this.loadComplaints();
        }, 30000); // Poll every 30 seconds for updates
    }

    static handleComplaintForm() {
        const form = document.getElementById('complaintForm');
        if (!form) return;

        form.addEventListener('submit', (e) => {
            e.preventDefault();
            Utils.showLoading(true);

            const formData = {
                title: document.getElementById('title').value,
                description: document.getElementById('description').value,
                category: document.getElementById('category').value
            };

            ComplaintManager.createComplaint(formData)
                .then(() => {
                    Utils.showMessage('successMessage', 'Complaint created successfully! Email sent.');
                    form.reset();
                    Dashboard.loadComplaints();
                })
                .catch(error => Utils.showMessage('errorMessage', error.message, true))
                .finally(() => Utils.showLoading(false));
        });
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => Dashboard.init());
