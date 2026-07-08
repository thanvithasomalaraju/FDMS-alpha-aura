// ===== MAD FOOD API CONFIGURATION =====
const API_BASE_URL = 'http://localhost:8080/api';

// Store JWT token in localStorage
function setAuthToken(token) {
    localStorage.setItem('authToken', token);
}

function getAuthToken() {
    return localStorage.getItem('authToken');
}

function clearAuthToken() {
    localStorage.removeItem('authToken');
}

// API Request Helper
async function apiRequest(endpoint, method = 'GET', data = null) {
    const headers = {
        'Content-Type': 'application/json',
    };

    const token = getAuthToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        method,
        headers,
    };

    if (data) {
        config.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        if (!response.ok) {
            if (response.status === 401) {
                clearAuthToken();
                window.location.href = '/customer/newone.html';
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('API Request Error:', error);
        throw error;
    }
}

// ===== AUTH ENDPOINTS =====
const Auth = {
    login: (email, password, role) => apiRequest('/auth/login', 'POST', { email, password, role }),
    register: (email, password, role) => apiRequest('/auth/register', 'POST', { email, password, role }),
};

// ===== CUSTOMER ENDPOINTS =====
const Customers = {
    getAll: () => apiRequest('/customers', 'GET'),
    getById: (id) => apiRequest(`/customers/${id}`, 'GET'),
    create: (data) => apiRequest('/customers', 'POST', data),
    update: (id, data) => apiRequest(`/customers/${id}`, 'PUT', data),
};

// ===== RESTAURANT ENDPOINTS =====
const Restaurants = {
    getAll: () => apiRequest('/restaurants', 'GET'),
    getById: (id) => apiRequest(`/restaurants/${id}`, 'GET'),
    create: (data) => apiRequest('/restaurants', 'POST', data),
    update: (id, data) => apiRequest(`/restaurants/${id}`, 'PUT', data),
};

// ===== FOOD ENDPOINTS =====
const Foods = {
    getAll: () => apiRequest('/foods', 'GET'),
    getById: (id) => apiRequest(`/foods/${id}`, 'GET'),
    getByRestaurant: (restaurantId) => apiRequest(`/foods/restaurant/${restaurantId}`, 'GET'),
    create: (data) => apiRequest('/foods', 'POST', data),
    update: (id, data) => apiRequest(`/foods/${id}`, 'PUT', data),
    delete: (id) => apiRequest(`/foods/${id}`, 'DELETE'),
};

// ===== ORDER ENDPOINTS =====
const Orders = {
    getAll: () => apiRequest('/orders', 'GET'),
    getById: (id) => apiRequest(`/orders/${id}`, 'GET'),
    getByCustomer: (customerId) => apiRequest(`/orders/customer/${customerId}`, 'GET'),
    create: (data) => apiRequest('/orders', 'POST', data),
    update: (id, data) => apiRequest(`/orders/${id}`, 'PUT', data),
};
