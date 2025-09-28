// frontend/js/auth.js
class Auth {
    static API_BASE = 'http://localhost:8080/api';

    static login(username, password) {
        return fetch(`${this.API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Invalid credentials');
            }
            return response.json();
        })
        .then(data => {
            localStorage.setItem('token', data.token);
            localStorage.setItem('user', JSON.stringify(data.user));
            return data.user;
        });
    }

    static logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '../html/login.html';
    }

    static getToken() {
        return localStorage.getItem('token');
    }

    static getUser () {
        return JSON.parse(localStorage.getItem('user') || '{}');
    }

    static isAuthenticated() {
        const token = this.getToken();
        return !!token && !this.isTokenExpired();
    }

    static isTokenExpired() {
        const token = this.getToken();
        if (!token) return true;
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.exp * 1000 < Date.now();
        } catch {
            return true;
        }
    }

    static getAuthHeader() {
        const token = this.getToken();
        return token ? { 'Authorization': `Bearer ${token}` } : {};
    }

    static getRole() {
        const user = this.getUser ();
        return user.role;
    }
}
