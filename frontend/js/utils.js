// frontend/js/utils.js
class Utils {
    static showMessage(elementId, message, isError = false) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = message;
            element.style.display = 'block';
            element.className = isError ? 'alert alert-danger' : 'alert alert-success';
            setTimeout(() => {
                element.style.display = 'none';
            }, 5000);
        }
    }

    static showLoading(show = true) {
        const loading = document.querySelector('.loading');
        if (loading) {
            loading.style.display = show ? 'block' : 'none';
        }
    }

    static formatDate(dateString) {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    static getStatusBadge(status) {
        const classes = {
            'PENDING': 'badge bg-warning',
            'IN_PROGRESS': 'badge bg-info',
            'RESOLVED': 'badge bg-success'
        };
        return `<span class="${classes[status] || 'badge bg-secondary'}">${status}</span>`;
    }

    static getCategoryBadge(category) {
        const classes = {
            'HOSTEL': 'badge bg-secondary',
            'ACADEMIC': 'badge bg-primary',
            'MAINTENANCE': 'badge bg-danger'
        };
        return `<span class="${classes[category] || 'badge bg-secondary'}">${category}</span>`;
    }
}
