const API_BASE_URL = 'http://localhost:8080/api';

// Helper function to handle fetch calls with Authorization header
async function apiFetch(endpoint, options = {}) {
    const token = localStorage.getItem('mad_token');
    
    // Set headers
    const headers = {
        'Accept': 'application/json',
        ...options.headers,
    };
    
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }
    
    // If body is an object (not FormData), serialize it as JSON
    let body = options.body;
    if (body && !(body instanceof FormData) && typeof body === 'object') {
        body = JSON.stringify(body);
        headers['Content-Type'] = 'application/json';
    }
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...options,
        headers,
        body
    });
    
    if (response.status === 401) {
        localStorage.removeItem('mad_token');
        localStorage.removeItem('mad_user');
        // Let application login handlers take care of re-login if needed
    }
    
    if (!response.ok) {
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || `Request failed with status ${response.status}`);
    }
    
    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
        return await response.json();
    }
    return null;
}

// Upload file to backend and return its relative URL
async function apiUploadFile(file, subDir) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('subDir', subDir);
    
    const response = await fetch(`${API_BASE_URL}/files/upload`, {
        method: 'POST',
        body: formData
    });
    
    if (!response.ok) {
        throw new Error('File upload failed');
    }
    
    const res = await response.json();
    return res.url;
}
