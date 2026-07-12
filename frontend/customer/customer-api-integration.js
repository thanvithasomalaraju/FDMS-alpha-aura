// Customer Portal API Integration
document.addEventListener("DOMContentLoaded", () => {
    // ────────────────────────────────────────────────────────────────────────
    // 1. HELPER TO SYNC DATA FROM SPRING BOOT TO INLINE SCRIPT
    // ────────────────────────────────────────────────────────────────────────
    
    function getFoodImage(name) {
        const lower = String(name).toLowerCase();
        if (lower.includes('pizza')) {
            return 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500';
        }
        if (lower.includes('burger')) {
            return 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500';
        }
        if (lower.includes('biryani') || lower.includes('rice') || lower.includes('pulao')) {
            return 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=500';
        }
        if (lower.includes('cake') || lower.includes('pastry') || lower.includes('muffin') || lower.includes('cupcake') || lower.includes('sweet') || lower.includes('dessert') || lower.includes('cheesecake')) {
            return 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=500';
        }
        if (lower.includes('chicken') || lower.includes('mutton') || lower.includes('tikka') || lower.includes('kebab') || lower.includes('meat')) {
            return 'https://images.unsplash.com/photo-1604503468506-a8da13d82791?w=500';
        }
        if (lower.includes('juice') || lower.includes('shake') || lower.includes('drink') || lower.includes('beverage') || lower.includes('tea') || lower.includes('coffee') || lower.includes('mojito')) {
            return 'https://images.unsplash.com/photo-1497534446932-c925b458314e?w=500';
        }
        return 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500';
    }

    function getRestaurantImage(name, idx) {
        const lower = String(name || '').toLowerCase().trim();
        
        // Exact name matching first for highest precision
        if (lower.includes('bistro')) {
            // Differentiate identical names using their indices
            if (idx === 0) {
                return 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600'; // Modern Bistro
            } else {
                return 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=600'; // Fine Dining Bistro
            }
        }
        if (lower === 'the food') {
            return 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=600'; // Gourmet Pizza/Burger layout
        }
        if (lower.includes('swaad')) {
            return 'https://images.unsplash.com/photo-1589301760014-d929f3979dbc?w=600'; // Traditional Indian Swaad curry
        }
        if (lower.includes('rajahamsa')) {
            return 'https://images.unsplash.com/photo-1544025162-d76694265947?w=600'; // Royal platter
        }
        if (lower.includes('morning') || lower.includes('breakfast')) {
            return 'https://images.unsplash.com/photo-1533089860892-a7c6f0a88666?w=600'; // Morning breakfast & coffee
        }
        if (lower.includes('biryani') || lower.includes('paradise')) {
            return 'https://images.unsplash.com/photo-1563379091339-03b21ab4a4f8?w=600'; // Biryani
        }
        
        // Curated defaults fallback list
        const defaults = [
            'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=600',
            'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600',
            'https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=600'
        ];
        const index = (idx !== undefined ? idx : (name || '').length) % defaults.length;
        return defaults[index];
    }

    async function syncDatabaseData(customerId) {
        try {
            // A. Fetch Catalog (Approved Restaurants & Menus)
            const restaurants = await apiFetch('/customer/restaurants');
            const mappedRestaurants = restaurants.map((r, idx) => {
                const name = r.nameString || (r.name ? (r.name.en || '') : '');
                const fallbackImg = getRestaurantImage(name, idx);
                return {
                    ...r,
                    img: (r.img && r.img.trim() !== '' && r.img !== 'null') ? r.img : fallbackImg,
                    logoUrl: (r.logoUrl && r.logoUrl.trim() !== '' && r.logoUrl !== 'null') ? r.logoUrl : fallbackImg,
                    reviews: r.reviews || []
                };
            });
            RESTAURANT_DATA = mappedRestaurants;
            RESTAURANT_INVENTORY = mappedRestaurants;
            
            // Fetch menus for each restaurant and build full menu catalog
            let fullMenu = [];
            for (let rest of restaurants) {
                const menuItems = await apiFetch(`/customer/restaurants/${rest.numericId}/menu`);
                const mapped = menuItems.map(item => {
                    const fallbackImg = getFoodImage(item.nameString);
                    if (!item.img || item.img.trim() === '' || item.img === 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c') item.img = fallbackImg;
                    if (!item.imageUrl || item.imageUrl.trim() === '' || item.imageUrl === 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c') item.imageUrl = fallbackImg;
                    return item;
                });
                fullMenu.push(...mapped);
            }
            MENU_DATA = fullMenu;
            MENU_INVENTORY = fullMenu;
            
            // Trigger UI catalog rendering
            if (typeof renderMadanapalleHubs === 'function') renderMadanapalleHubs();
            if (typeof renderDynamicCatalog === 'function') renderDynamicCatalog();
            
            // B. Fetch Profile Details
            const profile = await apiFetch(`/customer/${customerId}/profile`);
            localStorage.setItem('profile_name', profile.name || '');
            localStorage.setItem('profile_email', profile.email || '');
            localStorage.setItem('profile_phone', profile.phone || '');
            
            if (profile.profileImage) {
                localStorage.setItem('profilePic', profile.profileImage);
            } else {
                localStorage.removeItem('profilePic');
            }
            
            // C. Sync Addresses
            if (profile.addresses && profile.addresses.length > 0) {
                const addr = profile.addresses[0];
                localStorage.setItem('mad_home_lat', addr.latitude);
                localStorage.setItem('mad_home_lng', addr.longitude);
                if (typeof userHomeCoordinates !== 'undefined') {
                    userHomeCoordinates = [addr.latitude, addr.longitude];
                }
            }
            
            if (typeof loadSavedProfileAddress === 'function') loadSavedProfileAddress();
            if (typeof loadProfilePic === 'function') loadProfilePic();
            
            // D. Fetch Cart
            const cartData = await apiFetch(`/customer/${customerId}/cart`);
            syncLocalCart(cartData);
            
            // E. Fetch Order History
            await refreshOrdersHistory(customerId);
            
        } catch (err) {
            console.error("Sync error: ", err);
        }
    }

    function syncLocalCart(cartData) {
        if (!cartData) return;
        runtimeCart = cartData.items.map(ci => {
            const itemDto = ci.foodItem;
            return {
                id: itemDto.id,
                name: itemDto.name,
                price: itemDto.price,
                diet: itemDto.diet,
                img: itemDto.imageUrl,
                qty: ci.quantity,
                restaurant: itemDto.restaurant
            };
        });
        
        // Save in localStorage for the UI to use if needed
        localStorage.setItem('mad_cart', JSON.stringify(runtimeCart));
        
        if (typeof updateCartUIState === 'function') updateCartUIState();
        if (typeof renderFloatingCartSidebar === 'function') renderFloatingCartSidebar();
    }

    async function refreshOrdersHistory(customerId) {
        const dbOrders = await apiFetch(`/customer/${customerId}/orders`);
        runtimeOrders = dbOrders.map(o => {
            // Map completed orders status to UI "Delivered ?" to show checked
            let statusStr = o.status;
            if (statusStr.toLowerCase() === 'completed') {
                statusStr = 'Delivered ✓';
            } else if (statusStr.toLowerCase() === 'new') {
                statusStr = 'Placed';
            } else if (statusStr.toLowerCase() === 'preparing') {
                statusStr = 'Preparing';
            } else if (statusStr.toLowerCase() === 'ready') {
                statusStr = 'Ready for Pickup';
            } else if (statusStr.toLowerCase() === 'delivery') {
                statusStr = 'Out for Delivery';
            }
            
            return {
                id: o.id,
                numericId: o.numericId,
                restaurant: o.restaurantName,
                amount: o.amount,
                status: statusStr,
                payment: o.payment || 'Paid',
                summary: o.itemsSummary,
                date: o.date,
                items: o.items
            };
        });
        
        localStorage.setItem('mad_orders', JSON.stringify(runtimeOrders));
        if (typeof refreshDashboardSystem === 'function') refreshDashboardSystem();
    }

    // ────────────────────────────────────────────────────────────────────────
    // 2. OVERWRITE AUTHENTICATION FUNCTIONS
    // ────────────────────────────────────────────────────────────────────────

    function saveAccount(email, password, role) {
        const raw = localStorage.getItem('mad_saved_accounts') || '[]';
        let accounts = JSON.parse(raw);
        accounts = accounts.filter(a => !(a.email.toLowerCase() === email.toLowerCase() && a.role === role));
        accounts.unshift({ email, password, role });
        if (accounts.length > 10) accounts.pop();
        localStorage.setItem('mad_saved_accounts', JSON.stringify(accounts));
    }

    function initSavedAccountsDropdown(inputId, passwordId, role) {
        const emailInput = document.getElementById(inputId);
        const passInput = document.getElementById(passwordId);
        if (!emailInput || !passInput) return;
        
        const formGroup = emailInput.closest('.form-group') || emailInput.parentElement;
        formGroup.style.position = 'relative';
        
        // Remove existing dropdown if any
        const existing = formGroup.querySelector('.custom-creds-dropdown');
        if (existing) existing.remove();
        
        const dropdown = document.createElement('div');
        dropdown.className = 'custom-creds-dropdown';
        dropdown.style.cssText = `
            position: absolute;
            top: 100%;
            left: 0;
            width: 100%;
            background: #fff;
            border: 1px solid var(--border-color, #d8b894);
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            z-index: 10000;
            display: none;
            max-height: 150px;
            overflow-y: auto;
            margin-top: 4px;
        `;
        formGroup.appendChild(dropdown);
        
        function renderAccountsList() {
            const rawAccounts = localStorage.getItem('mad_saved_accounts') || '[]';
            const accounts = JSON.parse(rawAccounts).filter(a => a.role === role);
            
            if (accounts.length === 0) {
                dropdown.style.display = 'none';
                return;
            }
            
            dropdown.innerHTML = '';
            accounts.forEach(acc => {
                const item = document.createElement('div');
                item.style.cssText = `
                    padding: 8px 12px;
                    font-size: 0.85rem;
                    color: var(--text-dark, #1e3a2f);
                    cursor: pointer;
                    font-weight: 600;
                    border-bottom: 1px solid #f5f0eb;
                    transition: background 0.2s;
                    text-align: left;
                `;
                item.textContent = acc.email;
                
                item.addEventListener('mouseover', () => {
                    item.style.background = '#fcf8f2';
                });
                item.addEventListener('mouseout', () => {
                    item.style.background = '#fff';
                });
                
                item.addEventListener('mousedown', (e) => {
                    e.preventDefault();
                    emailInput.value = acc.email;
                    passInput.value = acc.password;
                    dropdown.style.display = 'none';
                });
                dropdown.appendChild(item);
            });
        }
        
        emailInput.addEventListener('focus', () => {
            renderAccountsList();
            const rawAccounts = localStorage.getItem('mad_saved_accounts') || '[]';
            const accounts = JSON.parse(rawAccounts).filter(a => a.role === role);
            if (accounts.length > 0) {
                dropdown.style.display = 'block';
            }
        });
        
        emailInput.addEventListener('blur', () => {
            setTimeout(() => {
                dropdown.style.display = 'none';
            }, 200);
        });
        
        emailInput.addEventListener('input', () => {
            const val = emailInput.value.trim().toLowerCase();
            
            // Auto-fill password on exact email match
            const rawAccounts = localStorage.getItem('mad_saved_accounts') || '[]';
            const accounts = JSON.parse(rawAccounts).filter(a => a.role === role);
            const match = accounts.find(a => a.email.toLowerCase() === val);
            if (match) {
                passInput.value = match.password;
            }
            
            const items = dropdown.children;
            let visibleCount = 0;
            for (let item of items) {
                if (item.textContent.toLowerCase().includes(val)) {
                    item.style.display = 'block';
                    visibleCount++;
                } else {
                    item.style.display = 'none';
                }
            }
            dropdown.style.display = visibleCount > 0 ? 'block' : 'none';
        });

        emailInput.addEventListener('change', () => {
            const val = emailInput.value.trim().toLowerCase();
            const rawAccounts = localStorage.getItem('mad_saved_accounts') || '[]';
            const accounts = JSON.parse(rawAccounts).filter(a => a.role === role);
            const match = accounts.find(a => a.email.toLowerCase() === val);
            if (match) {
                passInput.value = match.password;
            }
        });
    }
    
    window.handleAuthLogin = async function(event) {
        event.preventDefault();
        const email = document.getElementById('login-email').value.trim();
        const password = document.getElementById('login-pass').value;
        
        if (!email || !password) {
            showToast('Please enter both email and password.', 'error');
            return;
        }
        
        try {
            const data = await apiFetch('/auth/login', {
                method: 'POST',
                body: { email, password }
            });
            
            if (data.role !== 'CUSTOMER') {
                showToast('Access Denied: This is a customer portal.', 'error');
                return;
            }
            
            localStorage.setItem('mad_token', data.token);
            localStorage.setItem('mad_user', JSON.stringify(data));
            localStorage.setItem('last_customer_email', email);
            localStorage.setItem('last_customer_password', password);
            saveAccount(email, password, 'CUSTOMER');
            
            // Pull catalog, profile, orders and cart
            await syncDatabaseData(data.profileId);
            
            // Call original UI unlock
            document.getElementById('auth-portal').style.display = 'none';
            document.getElementById('app-container').style.display = 'flex';
            document.getElementById('floatingCart').style.display = 'flex';
            if (typeof loadTheme === 'function') loadTheme();
            if (typeof applyLanguage === 'function') applyLanguage(currentLanguage);
            
            showToast('Welcome back, ' + data.name + '!', 'success', 3000);
        } catch (err) {
            showToast('Login Failed: ' + err.message, 'error', 3000);
        }
    };

    window.handleAuthSignup = async function(event) {
        event.preventDefault();
        const name = document.getElementById('reg-name').value.trim();
        const email = document.getElementById('reg-email').value.trim();
        const phone = document.getElementById('reg-phone').value.trim();
        const password = document.getElementById('reg-pass').value;
        const confirmPass = document.getElementById('reg-confirm-pass').value;
        
        if (!name || !email || !phone || !password) {
            showToast('Please fill all fields.', 'error');
            return;
        }
        if (password !== confirmPass) {
            showToast('Passwords do not match.', 'error');
            return;
        }
        
        try {
            const data = await apiFetch('/auth/register', {
                method: 'POST',
                body: { name, email, phone, password, role: 'CUSTOMER' }
            });
            
            localStorage.setItem('mad_token', data.token);
            localStorage.setItem('mad_user', JSON.stringify(data));
            localStorage.setItem('last_customer_email', email);
            localStorage.setItem('last_customer_password', password);
            saveAccount(email, password, 'CUSTOMER');
            
            await syncDatabaseData(data.profileId);
            
            document.getElementById('auth-portal').style.display = 'none';
            document.getElementById('app-container').style.display = 'flex';
            document.getElementById('floatingCart').style.display = 'flex';
            if (typeof loadTheme === 'function') loadTheme();
            if (typeof applyLanguage === 'function') applyLanguage(currentLanguage);
            
            showToast('Registration Successful! Welcome to Mad Food.', 'success', 3000);
        } catch (err) {
            showToast('Signup Failed: ' + err.message, 'error', 3000);
        }
    };

    window.logoutUser = function() {
        const userStr = localStorage.getItem('mad_user');
        if (userStr) {
            const user = JSON.parse(userStr);
            apiFetch(`/auth/logout?email=${encodeURIComponent(user.email)}`, { method: 'POST' }).catch(() => {});
        }
        
        localStorage.removeItem('mad_token');
        localStorage.removeItem('mad_user');
        localStorage.removeItem('mad_cart');
        localStorage.removeItem('mad_orders');
        localStorage.removeItem('profile_name');
        localStorage.removeItem('profile_email');
        localStorage.removeItem('profile_phone');
        localStorage.removeItem('profilePic');
        localStorage.removeItem('mad_home_lat');
        localStorage.removeItem('mad_home_lng');
        
        document.getElementById('app-container').style.display = 'none';
        document.getElementById('floatingCart').style.display = 'none';
        document.getElementById('auth-portal').style.display = 'flex';
        switchAuthTab('login');
        showToast('You have been logged out.', 'info', 3000);
        if (typeof closeCartSidebar === 'function') closeCartSidebar();
        document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));
    };

    // ────────────────────────────────────────────────────────────────────────
    // 3. OVERWRITE CART FUNCTIONS
    // ────────────────────────────────────────────────────────────────────────
    
    window.addToCart = async function(itemId) {
        const userStr = localStorage.getItem('mad_user');
        if (!userStr) {
            showToast('Please login first', 'warning');
            return;
        }
        const user = JSON.parse(userStr);
        const numericId = typeof itemId === 'string' ? parseInt(itemId.replace('item_', '')) : itemId;
        
        try {
            const cartData = await apiFetch(`/customer/${user.profileId}/cart?foodItemId=${numericId}&quantity=1`, {
                method: 'POST'
            });
            syncLocalCart(cartData);
            
            const targetItem = MENU_INVENTORY.find(i => i.id === itemId);
            const name = targetItem ? (targetItem.name[currentLanguage] || targetItem.name.en) : 'Item';
            showToast('Added ' + name + ' to cart!', 'success', 1500);
        } catch (err) {
            showToast('Failed to add to cart: ' + err.message, 'error', 3000);
        }
    };

    window.adjustQty = async function(itemId, amount) {
        const userStr = localStorage.getItem('mad_user');
        if (!userStr) return;
        const user = JSON.parse(userStr);
        const numericId = typeof itemId === 'string' ? parseInt(itemId.replace('item_', '')) : itemId;
        
        const existing = runtimeCart.find(i => i.id === itemId);
        if (!existing) return;
        
        const newQty = existing.qty + amount;
        
        try {
            let cartData;
            if (newQty <= 0) {
                cartData = await apiFetch(`/customer/${user.profileId}/cart?foodItemId=${numericId}`, {
                    method: 'DELETE'
                });
            } else {
                cartData = await apiFetch(`/customer/${user.profileId}/cart?foodItemId=${numericId}&quantity=${newQty}`, {
                    method: 'PUT'
                });
            }
            syncLocalCart(cartData);
        } catch (err) {
            showToast('Failed to update cart: ' + err.message, 'error', 3000);
        }
    };

    // ────────────────────────────────────────────────────────────────────────
    // 4. OVERWRITE CHECKOUT / PLACE ORDER
    // ────────────────────────────────────────────────────────────────────────
    
    window.placeOrder = async function(paymentMethod) {
        const userStr = localStorage.getItem('mad_user');
        if (!userStr) return;
        const user = JSON.parse(userStr);
        
        try {
            // Check addresses
            const profile = await apiFetch(`/customer/${user.profileId}/profile`);
            let addressId = null;
            if (profile.addresses && profile.addresses.length > 0) {
                addressId = profile.addresses[0].id;
            } else {
                // Add a default Kadapa-Madanapalle hub address if missing
                const defaultAddr = await apiFetch(`/customer/${user.profileId}/addresses`, {
                    method: 'POST',
                    body: {
                        addressLine: "CTM Road, Madanapalle, Andhra Pradesh, India",
                        latitude: 13.6288,
                        longitude: 78.5009
                    }
                });
                addressId = defaultAddr.id;
            }
            
            // Post order
            const orderDto = await apiFetch(`/customer/${user.profileId}/orders?paymentMethod=${paymentMethod}&addressId=${addressId}`, {
                method: 'POST'
            });
            
            // Set the tracking order ID in the UI so beginLiveTrackingSimulation knows it
            if (document.getElementById('track-order-id')) {
                document.getElementById('track-order-id').textContent = orderDto.id;
            }
            
            // Show Success UI
            showPaymentSuccess(paymentMethod);
            
            // Sync cart and history
            runtimeCart = [];
            localStorage.setItem('mad_cart', JSON.stringify([]));
            if (typeof updateCartUIState === 'function') updateCartUIState();
            if (typeof renderFloatingCartSidebar === 'function') renderFloatingCartSidebar();
            
            await refreshOrdersHistory(user.profileId);
        } catch (err) {
            showToast('Failed to place order: ' + err.message, 'error', 3000);
        }
    };

    // ────────────────────────────────────────────────────────────────────────
    // 5. OVERWRITE PROFILE AND ADDRESS FUNCTIONS
    // ────────────────────────────────────────────────────────────────────────
    
    window.saveProfile = async function(event) {
        event.preventDefault();
        const userStr = localStorage.getItem('mad_user');
        if (!userStr) return;
        const user = JSON.parse(userStr);
        
        const name = document.getElementById('prof-name').value.trim();
        const phone = document.getElementById('prof-phone').value.trim();
        const email = document.getElementById('prof-email').value.trim();
        
        try {
            // Update Account info
            const updatedProfile = await apiFetch(`/customer/${user.profileId}/profile`, {
                method: 'PUT',
                body: { name, phone, email }
            });
            
            localStorage.setItem('profile_name', updatedProfile.name);
            localStorage.setItem('profile_phone', updatedProfile.phone);
            document.getElementById('sidebar-user-name').textContent = updatedProfile.name;
            
            // Update address
            const latInput = document.getElementById('home-lat');
            const lngInput = document.getElementById('home-lng');
            const lat = parseFloat(latInput.value);
            const lng = parseFloat(lngInput.value);
            
            if (!isNaN(lat) && !isNaN(lng)) {
                // Post address to DB
                const newAddress = await apiFetch(`/customer/${user.profileId}/addresses`, {
                    method: 'POST',
                    body: {
                        addressLine: "CTM Road, Madanapalle, Coordinates: [" + lat.toFixed(4) + ", " + lng.toFixed(4) + "]",
                        latitude: lat,
                        longitude: lng
                    }
                });
                
                localStorage.setItem('mad_home_lat', lat);
                localStorage.setItem('mad_home_lng', lng);
                if (typeof userHomeCoordinates !== 'undefined') {
                    userHomeCoordinates = [lat, lng];
                }
                
                if (typeof updateRoutePath === 'function') updateRoutePath([lat, lng]);
                
                const status = document.getElementById('geo-status-indicator');
                if (status) {
                    status.textContent = '✓ Updated: [' + lat.toFixed(4) + ', ' + lng.toFixed(4) + ']';
                    status.className = 'geo-status-text success';
                }
            }
            
            showToast('Profile and address updated in MySQL successfully!', 'success');
        } catch (err) {
            showToast('Failed to update profile: ' + err.message, 'error', 3000);
        }
    };

    // Profile photo upload helper
    const photoInput = document.getElementById('profile-pic-file');
    if (photoInput) {
        photoInput.addEventListener('change', async function() {
            const file = this.files[0];
            if (!file) return;
            
            const userStr = localStorage.getItem('mad_user');
            if (!userStr) return;
            const user = JSON.parse(userStr);
            
            try {
                showToast('Uploading profile picture...', 'info', 1000);
                const fileUrl = await apiUploadFile(file, 'customers');
                
                // Update profile image in DB
                await apiFetch(`/customer/${user.profileId}/profile`, {
                    method: 'PUT',
                    body: { profileImage: fileUrl, name: localStorage.getItem('profile_name'), phone: localStorage.getItem('profile_phone') }
                });
                
                localStorage.setItem('profilePic', fileUrl);
                if (typeof loadProfilePic === 'function') loadProfilePic();
                showToast('Profile picture updated successfully!', 'success');
            } catch (err) {
                showToast('Upload failed: ' + err.message, 'error', 3000);
            }
        });
    }

    // Rate / review submission helper
    window.submitReview = async function() {
        const userStr = localStorage.getItem('mad_user');
        if (!userStr) return;
        const user = JSON.parse(userStr);
        
        const rating = typeof restaurantRating !== 'undefined' ? restaurantRating : 5;
        const comment = document.getElementById('review-text-modal').value.trim();
        
        // Find order restaurant
        const order = runtimeOrders.find(o => o.id === pendingOrderId);
        if (!order) return;
        
        try {
            // Find restaurant numeric ID by matching name
            const rest = RESTAURANT_DATA.find(r => r.nameString === order.restaurant || r.name.en === order.restaurant);
            if (rest) {
                await apiFetch(`/customer/${user.profileId}/restaurants/${rest.numericId}/reviews`, {
                    method: 'POST',
                    body: { rating, comment }
                });
            }
            
            // Mock delivery complete in frontend
            order.status = 'Delivered ?';
            localStorage.setItem('mad_orders', JSON.stringify(runtimeOrders));
            if (typeof refreshDashboardSystem === 'function') refreshDashboardSystem();
            
            closeModal('delivery-review-modal');
            showToast('Review submitted successfully!', 'success');
        } catch (err) {
            showToast('Failed to submit review: ' + err.message, 'error', 3000);
        }
    };

    // ────────────────────────────────────────────────────────────────────────
    // 6. INITIAL SYNC / SILENT SIGNIN
    // ────────────────────────────────────────────────────────────────────────
    
    const savedToken = localStorage.getItem('mad_token');
    const savedUserStr = localStorage.getItem('mad_user');
    
    if (savedToken && savedUserStr) {
        const user = JSON.parse(savedUserStr);
        // Switch page elements immediately
        document.getElementById('auth-portal').style.display = 'none';
        document.getElementById('app-container').style.display = 'flex';
        document.getElementById('floatingCart').style.display = 'flex';
        
        // Load data in background
        syncDatabaseData(user.profileId);
    } else {
        const savedEmail = localStorage.getItem('last_customer_email');
        const savedPass = localStorage.getItem('last_customer_password');
        if (savedEmail && savedPass) {
            if (document.getElementById('login-email')) document.getElementById('login-email').value = savedEmail;
            if (document.getElementById('login-pass')) document.getElementById('login-pass').value = savedPass;
        }
        
        // Just load catalogs for guest browsing
        apiFetch('/customer/restaurants').then(restaurants => {
            const mappedRestaurants = restaurants.map((r, idx) => {
                const name = r.nameString || (r.name ? (r.name.en || '') : '');
                const fallbackImg = getRestaurantImage(name, idx);
                return {
                    ...r,
                    img: (r.img && r.img.trim() !== '' && r.img !== 'null') ? r.img : fallbackImg,
                    logoUrl: (r.logoUrl && r.logoUrl.trim() !== '' && r.logoUrl !== 'null') ? r.logoUrl : fallbackImg,
                    reviews: r.reviews || []
                };
            });
            RESTAURANT_DATA = mappedRestaurants;
            RESTAURANT_INVENTORY = mappedRestaurants;
            let fullMenu = [];
            const promises = mappedRestaurants.map(rest => {
                return apiFetch(`/customer/restaurants/${rest.numericId}/menu`).then(items => {
                    fullMenu.push(...items);
                });
            });
            Promise.all(promises).then(() => {
                MENU_DATA = fullMenu;
                MENU_INVENTORY = fullMenu;
                if (typeof renderMadanapalleHubs === 'function') renderMadanapalleHubs();
                if (typeof renderDynamicCatalog === 'function') renderDynamicCatalog();
            });
        }).catch(err => console.error("Guest catalog loading failed: ", err));
    }
    initSavedAccountsDropdown('login-email', 'login-pass', 'CUSTOMER');
});
