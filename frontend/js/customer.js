// ===== CUSTOMER DASHBOARD INTEGRATION =====

// Login Handler
async function handleCustomerLogin() {
    const email = document.getElementById('loginEmail')?.value;
    const password = document.getElementById('loginPassword')?.value;
    
    if (!email || !password) {
        alert('Please fill all fields');
        return;
    }

    try {
        const response = await Auth.login(email, password, 'CUSTOMER');
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
        alert('Login failed. Please try again.');
    }
}

// Register Handler
async function handleCustomerRegister() {
    const email = document.getElementById('registerEmail')?.value;
    const password = document.getElementById('registerPassword')?.value;
    const fullName = document.getElementById('registerName')?.value;
    
    if (!email || !password || !fullName) {
        alert('Please fill all fields');
        return;
    }

    try {
        const response = await Auth.register(email, password, 'CUSTOMER');
        if (response.success) {
            alert('Registration successful! Please login.');
            window.location.href = 'newone.html';
        } else {
            alert(response.message || 'Registration failed');
        }
    } catch (error) {
        console.error('Registration error:', error);
        alert('Registration failed. Please try again.');
    }
}

// Load Restaurants
async function loadRestaurants() {
    try {
        const restaurants = await Restaurants.getAll();
        displayRestaurants(restaurants);
    } catch (error) {
        console.error('Error loading restaurants:', error);
    }
}

// Display Restaurants
function displayRestaurants(restaurants) {
    const container = document.getElementById('restaurantContainer');
    if (!container) return;
    
    container.innerHTML = '';
    restaurants.forEach(restaurant => {
        const card = `
            <div class="restaurant-card">
                <img src="${restaurant.banner || 'default-banner.jpg'}" alt="${restaurant.restaurantName}">
                <h3>${restaurant.restaurantName}</h3>
                <p>${restaurant.cuisineType}</p>
                <p>⭐ ${restaurant.rating}</p>
                <button onclick="viewRestaurant(${restaurant.id})">View Menu</button>
            </div>
        `;
        container.innerHTML += card;
    });
}

// Load Foods by Restaurant
async function viewRestaurant(restaurantId) {
    try {
        const foods = await Foods.getByRestaurant(restaurantId);
        displayFoods(foods, restaurantId);
    } catch (error) {
        console.error('Error loading foods:', error);
    }
}

// Display Foods
function displayFoods(foods, restaurantId) {
    const container = document.getElementById('foodContainer');
    if (!container) return;
    
    container.innerHTML = '';
    foods.forEach(food => {
        const card = `
            <div class="food-card">
                <img src="${food.image || 'default-food.jpg'}" alt="${food.foodName}">
                <h4>${food.foodName}</h4>
                <p>${food.description}</p>
                <p>₹${food.price}</p>
                <p>${food.dietType === 'veg' ? '🟢 Veg' : '🔴 Non-Veg'}</p>
                <button onclick="addToCart(${food.id}, '${food.foodName}', ${food.price})">Add to Cart</button>
            </div>
        `;
        container.innerHTML += card;
    });
}

// Add to Cart
function addToCart(foodId, foodName, price) {
    let cart = JSON.parse(localStorage.getItem('cart')) || [];
    const item = cart.find(item => item.foodId === foodId);
    
    if (item) {
        item.quantity += 1;
    } else {
        cart.push({ foodId, foodName, price, quantity: 1 });
    }
    
    localStorage.setItem('cart', JSON.stringify(cart));
    alert(`${foodName} added to cart!`);
    updateCartCount();
}

// Update Cart Count
function updateCartCount() {
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const count = cart.reduce((total, item) => total + item.quantity, 0);
    const cartBadge = document.getElementById('cartCount');
    if (cartBadge) {
        cartBadge.textContent = count;
    }
}

// Place Order
async function placeOrder() {
    const userId = localStorage.getItem('userId');
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    
    if (cart.length === 0) {
        alert('Cart is empty');
        return;
    }

    const totalAmount = cart.reduce((total, item) => total + (item.price * item.quantity), 0) + 40; // 40 is delivery fee
    
    const order = {
        customerId: userId,
        restaurantId: parseInt(document.getElementById('restaurantId')?.value || 1),
        totalAmount: totalAmount,
        deliveryFee: 40,
        paymentMethod: 'CASH',
        deliveryAddress: document.getElementById('deliveryAddress')?.value,
        status: 'PENDING'
    };

    try {
        const response = await Orders.create(order);
        if (response.id) {
            alert('Order placed successfully!');
            localStorage.removeItem('cart');
            window.location.href = 'order-tracking.html';
        }
    } catch (error) {
        console.error('Order error:', error);
        alert('Failed to place order');
    }
}

// Load Order History
async function loadOrderHistory() {
    const userId = localStorage.getItem('userId');
    try {
        const orders = await Orders.getByCustomer(userId);
        displayOrderHistory(orders);
    } catch (error) {
        console.error('Error loading orders:', error);
    }
}

// Display Order History
function displayOrderHistory(orders) {
    const container = document.getElementById('orderHistoryContainer');
    if (!container) return;
    
    container.innerHTML = '';
    orders.forEach(order => {
        const card = `
            <div class="order-card">
                <p>Order #${order.id}</p>
                <p>Total: ₹${order.totalAmount}</p>
                <p>Status: <span class="status ${order.status.toLowerCase()}">${order.status}</span></p>
                <p>Date: ${new Date(order.createdAt).toLocaleDateString()}</p>
                <button onclick="trackOrder(${order.id})">Track Order</button>
            </div>
        `;
        container.innerHTML += card;
    });
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    updateCartCount();
    loadRestaurants();
    loadOrderHistory();
});
