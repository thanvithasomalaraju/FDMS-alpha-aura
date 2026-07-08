// ===== ADMIN DASHBOARD INTEGRATION =====

// Load All Users
async function loadAllUsers() {
    try {
        const response = await apiRequest('/admin/users', 'GET');
        displayUsers(response);
    } catch (error) {
        console.error('Error loading users:', error);
    }
}

// Display Users
function displayUsers(users) {
    const container = document.getElementById('usersContainer');
    if (!container) return;
    
    container.innerHTML = '';
    users.forEach(user => {
        const row = `
            <tr>
                <td>${user.id}</td>
                <td>${user.fullName}</td>
                <td>${user.email}</td>
                <td>${user.phone}</td>
                <td>${user.role}</td>
                <td>${user.isActive ? '✓' : '✗'}</td>
                <td>
                    <button onclick="blockUser(${user.id})">Block</button>
                    <button onclick="unblockUser(${user.id})">Unblock</button>
                    <button onclick="deleteUser(${user.id})">Delete</button>
                </td>
            </tr>
        `;
        container.innerHTML += row;
    });
}

// Block User
async function blockUser(userId) {
    try {
        await apiRequest(`/admin/users/${userId}/block`, 'PUT');
        alert('User blocked!');
        loadAllUsers();
    } catch (error) {
        console.error('Error blocking user:', error);
    }
}

// Unblock User
async function unblockUser(userId) {
    try {
        await apiRequest(`/admin/users/${userId}/unblock`, 'PUT');
        alert('User unblocked!');
        loadAllUsers();
    } catch (error) {
        console.error('Error unblocking user:', error);
    }
}

// Delete User
async function deleteUser(userId) {
    if (confirm('Are you sure you want to delete this user?')) {
        try {
            await apiRequest(`/admin/users/${userId}`, 'DELETE');
            alert('User deleted!');
            loadAllUsers();
        } catch (error) {
            console.error('Error deleting user:', error);
        }
    }
}

// Load Dashboard Analytics
async function loadDashboardAnalytics() {
    try {
        const users = await apiRequest('/admin/users', 'GET');
        const restaurants = await Restaurants.getAll();
        const orders = await Orders.getAll();
        
        displayAnalytics(users, restaurants, orders);
    } catch (error) {
        console.error('Error loading analytics:', error);
    }
}

// Display Analytics
function displayAnalytics(users, restaurants, orders) {
    const totalUsers = users.length;
    const totalCustomers = users.filter(u => u.role === 'CUSTOMER').length;
    const totalRestaurants = restaurants.length;
    const totalOrders = orders.length;
    const totalRevenue = orders.reduce((sum, order) => sum + order.totalAmount, 0);

    document.getElementById('totalUsers').textContent = totalUsers;
    document.getElementById('totalCustomers').textContent = totalCustomers;
    document.getElementById('totalRestaurants').textContent = totalRestaurants;
    document.getElementById('totalOrders').textContent = totalOrders;
    document.getElementById('totalRevenue').textContent = `₹${totalRevenue}`;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    loadAllUsers();
    loadDashboardAnalytics();
});
