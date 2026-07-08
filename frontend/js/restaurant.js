// ===== RESTAURANT DASHBOARD INTEGRATION =====

// Login Handler
async function handleRestaurantLogin() {
    const email = document.getElementById('loginEmail')?.value;
    const password = document.getElementById('loginPassword')?.value;
    
    if (!email || !password) {
        alert('Please fill all fields');
        return;
    }

    try {
        const response = await Auth.login(email, password, 'RESTAURANT');
        if (response.success) {
            setAuthToken(response.token);
            localStorage.setItem('userId', response.userId);
            localStorage.setItem('userRole', response.role);
            window.location.href = 'dashboard.html';
        } else {
            alert(response.message || 'Login failed');
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('Login failed');
    }
}

// Add Food Item
async function addFoodItem() {
    const restaurantId = localStorage.getItem('restaurantId');
    const food = {
        restaurantId: restaurantId,
        foodName: document.getElementById('foodName')?.value,
        description: document.getElementById('foodDescription')?.value,
        price: parseFloat(document.getElementById('foodPrice')?.value),
        category: document.getElementById('foodCategory')?.value,
        dietType: document.getElementById('dietType')?.value,
        preparationTime: parseInt(document.getElementById('prepTime')?.value),
        isAvailable: true
    };

    try {
        const response = await Foods.create(food);
        if (response.id) {
            alert('Food item added successfully!');
            loadRestaurantMenu();
        }
    } catch (error) {
        console.error('Error adding food:', error);
        alert('Failed to add food item');
    }
}

// Load Restaurant Menu
async function loadRestaurantMenu() {
    const restaurantId = localStorage.getItem('restaurantId');
    try {
        const foods = await Foods.getByRestaurant(restaurantId);
        displayRestaurantMenu(foods);
    } catch (error) {
        console.error('Error loading menu:', error);
    }
}

// Display Restaurant Menu
function displayRestaurantMenu(foods) {
    const container = document.getElementById('menuContainer');
    if (!container) return;
    
    container.innerHTML = '';
    foods.forEach(food => {
        const row = `
            <tr>
                <td>${food.foodName}</td>
                <td>₹${food.price}</td>
                <td>${food.dietType}</td>
                <td>${food.preparationTime} min</td>
                <td>
                    <button onclick="editFood(${food.id})">Edit</button>
                    <button onclick="deleteFood(${food.id})">Delete</button>
                </td>
            </tr>
        `;
        container.innerHTML += row;
    });
}

// Load Incoming Orders
async function loadIncomingOrders() {
    const restaurantId = localStorage.getItem('restaurantId');
    try {
        const orders = await Orders.getByRestaurant(restaurantId);
        displayIncomingOrders(orders);
    } catch (error) {
        console.error('Error loading orders:', error);
    }
}

// Display Incoming Orders
function displayIncomingOrders(orders) {
    const container = document.getElementById('ordersContainer');
    if (!container) return;
    
    container.innerHTML = '';
    orders.filter(order => order.status === 'PENDING').forEach(order => {
        const card = `
            <div class="order-card">
                <p>Order #${order.id}</p>
                <p>Total: ₹${order.totalAmount}</p>
                <p>Status: ${order.status}</p>
                <button onclick="acceptOrder(${order.id})">Accept</button>
                <button onclick="rejectOrder(${order.id})">Reject</button>
            </div>
        `;
        container.innerHTML += card;
    });
}

// Accept Order
async function acceptOrder(orderId) {
    try {
        await Orders.update(orderId, { status: 'CONFIRMED' });
        alert('Order accepted!');
        loadIncomingOrders();
    } catch (error) {
        console.error('Error accepting order:', error);
    }
}

// Reject Order
async function rejectOrder(orderId) {
    try {
        await Orders.update(orderId, { status: 'CANCELLED' });
        alert('Order rejected!');
        loadIncomingOrders();
    } catch (error) {
        console.error('Error rejecting order:', error);
    }
}

// Update Order Status
async function updateOrderStatus(orderId, status) {
    try {
        await Orders.update(orderId, { status: status });
        alert('Order status updated!');
        loadIncomingOrders();
    } catch (error) {
        console.error('Error updating order:', error);
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    loadRestaurantMenu();
    loadIncomingOrders();
});
