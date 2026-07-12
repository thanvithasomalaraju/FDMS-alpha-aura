// Restaurant Portal API Integration
document.addEventListener("DOMContentLoaded", () => {
    // ────────────────────────────────────────────────────────────────────────
    // 1. DATA SYNC HELPER
    // ────────────────────────────────────────────────────────────────────────
    
    async function syncRestaurantData(restaurantId) {
        try {
            // A. Fetch Profile
            const profile = await apiFetch(`/restaurant/${restaurantId}/profile`);
            localStorage.setItem('restaurant_profile', JSON.stringify(profile));
            
            // Render profile inputs if in view profile
            if (document.getElementById('profileName')) {
                document.getElementById('profileName').value = profile.nameString || '';
                document.getElementById('profileOwner').value = profile.ownerName || '';
                document.getElementById('profilePhone').value = profile.phone || '';
                document.getElementById('profileAddress').value = profile.address || '';
                document.getElementById('profileEmail').value = profile.email || '';
            }
            if (document.getElementById('settingsRestaurantName')) document.getElementById('settingsRestaurantName').value = profile.nameString || '';
            if (document.getElementById('settingsEmail')) document.getElementById('settingsEmail').value = profile.email || '';
            if (document.getElementById('settingsPhone')) document.getElementById('settingsPhone').value = profile.phone || '';
            if (document.getElementById('settingsAddress')) document.getElementById('settingsAddress').value = profile.address || '';
            if (document.getElementById('settingsFssai')) document.getElementById('settingsFssai').value = profile.docFssai || '';
            if (document.getElementById('settingsCuisine')) document.getElementById('settingsCuisine').value = profile.docMenu || '';

            // Dynamically bind header, sidebar, and profile elements to db values
            if (typeof updateSidebarName === 'function') updateSidebarName(profile.nameString);
            if (typeof updateDropdownName === 'function') updateDropdownName(profile.ownerName, profile.email);

            if (document.getElementById('profileOwnerName')) document.getElementById('profileOwnerName').textContent = profile.ownerName || '';
            if (document.getElementById('profileOwnerTitle')) document.getElementById('profileOwnerTitle').textContent = `Owner · ${profile.nameString || ''}`;
            if (document.getElementById('profileOwnerContact')) document.getElementById('profileOwnerContact').textContent = `${profile.email || ''} · ${profile.phone || ''}`;
            if (document.getElementById('profileFssaiBadge')) document.getElementById('profileFssaiBadge').innerHTML = `<i class="fa-solid fa-id-card"></i> ${profile.docFssai || 'No FSSAI'}`;
            if (document.getElementById('profileRestName')) document.getElementById('profileRestName').textContent = profile.nameString || '';
            if (document.getElementById('profileCuisine')) document.getElementById('profileCuisine').textContent = profile.docMenu || 'Multi-Cuisine';
            if (document.getElementById('profileAddressVal')) document.getElementById('profileAddressVal').textContent = profile.address || '';
            if (document.getElementById('profileStatusVal')) {
                const s = (profile.status || '').toUpperCase();
                document.getElementById('profileStatusVal').textContent = (s === 'APPROVED' ? '🟢 ' : (s === 'REJECTED' ? '🔴 ' : '🟡 ')) + s;
                document.getElementById('profileStatusVal').style.color = s === 'APPROVED' ? 'var(--md-green)' : (s === 'REJECTED' ? 'var(--md-red)' : 'var(--md-tertiary)');
            }
            
            // Check application status and show appropriate message if PENDING or REJECTED
            updateApplicationStatusUI(profile);

            // If APPROVED, fetch categories, foods, orders, stats
            if (profile.status.toUpperCase() === 'APPROVED') {
                // Fetch Categories
                const categories = await apiFetch(`/restaurant/${restaurantId}/categories`);
                categoriesData = categories.map(c => ({
                    id: c.id,
                    name: c.name,
                    items: c.itemsCount,
                    status: c.status
                }));
                if (typeof renderCategories === 'function') renderCategories();

                // Fetch Menu Items
                const foods = await apiFetch(`/restaurant/${restaurantId}/food-items`);
                menuItems = foods.map(f => ({
                    id: f.numericId,
                    name: f.nameString,
                    category: f.category,
                    price: f.price,
                    description: f.descriptionString,
                    imageUrl: (f.imageUrl && f.imageUrl !== 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c') ? f.imageUrl : (typeof getFoodImage === 'function' ? getFoodImage(f.nameString) : ''),
                    img: f.img || '🍔',
                    isAvailable: f.isAvailable
                }));
                
                // Sync inventory
                inventoryData = menuItems.map((item, idx) => ({
                    id: idx + 1,
                    name: item.name,
                    category: item.category,
                    stock: item.isAvailable ? 45 : 0
                }));
                
                if (typeof renderMenu === 'function') renderMenu();
                if (typeof renderInventory === 'function') renderInventory();

                // Fetch Orders
                const ordersList = await apiFetch(`/restaurant/${restaurantId}/orders`);
                orders = ordersList.map(o => ({
                    id: o.id,
                    numericId: o.numericId,
                    customer: o.customerName,
                    items: o.itemsSummary,
                    total: o.amount,
                    status: o.status,
                    time: o.date
                }));

                // Map orders to calendar data
                orderData = orders.map(o => {
                    let oDate = new Date();
                    if (o.time) {
                        try { oDate = new Date(o.time); } catch (e) {}
                    }
                    return {
                        date: oDate,
                        id: o.id,
                        customer: o.customer,
                        items: o.items,
                        total: o.total,
                        status: o.status.toLowerCase()
                    };
                });

                // Generate Support Tickets dynamically from active/preparing/ready orders
                ticketsData = orders.filter(o => ['new', 'preparing', 'ready'].includes(o.status.toLowerCase())).map((o, idx) => ({
                    id: `#TKT-${1000 + idx + 1}`,
                    subject: o.status.toLowerCase() === 'new' ? 'New Order Support' : 'Order Delayed Check',
                    from: `Customer: ${o.customer}`,
                    status: o.status.toLowerCase() === 'new' ? 'open' : 'in-progress',
                    priority: 'high',
                    created: 'Just now',
                    desc: `Customer placed order ${o.id}. Items: ${o.items}. Status is ${o.status.toUpperCase()}.`
                }));

                // Fetch real Customer Reviews
                try {
                    const reviewsList = await apiFetch(`/customer/restaurants/${restaurantId}/reviews`);
                    reviews = reviewsList.map(r => {
                        let reviewDateStr = 'Just now';
                        if (r.createdAt) {
                            try {
                                const diffMs = new Date() - new Date(r.createdAt);
                                const diffMins = Math.floor(diffMs / 60000);
                                const diffHours = Math.floor(diffMins / 60);
                                if (diffHours > 24) {
                                    reviewDateStr = `${Math.floor(diffHours / 24)} days ago`;
                                } else if (diffHours > 0) {
                                    reviewDateStr = `${diffHours} hours ago`;
                                } else if (diffMins > 0) {
                                    reviewDateStr = `${diffMins} mins ago`;
                                }
                            } catch (dateErr) {}
                        }
                        return {
                            id: r.id,
                            customer: r.customerName || 'Anonymous',
                            rating: r.rating || 5.0,
                            comment: r.comments || '',
                            date: reviewDateStr
                        };
                    });
                } catch (reviewErr) {
                    console.error("Failed to fetch reviews: ", reviewErr);
                    reviews = [];
                }
                
                // Filter active, completed, cancelled orders for dashboards
                if (typeof renderRecentOrders === 'function') renderRecentOrders();
                if (typeof renderOrders === 'function') renderOrders();
                if (typeof renderTickets === 'function') renderTickets();
                if (typeof renderReviews === 'function') renderReviews();
                if (typeof renderCalendar === 'function') renderCalendar();

                // Fetch Dashboard Stats
                const stats = await apiFetch(`/restaurant/${restaurantId}/stats`);
                updateStatsUI(stats);
                
                if (typeof initAllCharts === 'function') initAllCharts();
            }
        } catch (err) {
            console.error("Sync Error: ", err);
        }
    }

    function updateStatsUI(stats) {
        // Map stats to HTML UI elements
        const todayRevenueEl = document.getElementById('kpi-today-revenue');
        const totalOrdersEl = document.getElementById('kpi-total-orders');
        const pendingOrdersEl = document.getElementById('kpi-pending-orders');
        const ratingsEl = document.getElementById('kpi-ratings');

        if (todayRevenueEl) todayRevenueEl.textContent = '₹' + parseFloat(stats.earnings || 0).toLocaleString('en-IN');
        if (totalOrdersEl) totalOrdersEl.textContent = stats.totalOrders || 0;
        if (pendingOrdersEl) pendingOrdersEl.textContent = stats.activeOrders || 0;
        if (ratingsEl) ratingsEl.textContent = parseFloat(stats.rating || 4.0).toFixed(1);

        // Also update the Revenue view KPI cards
        const totalRevenueEl = document.getElementById('kpi-revenue-total');
        const monthRevenueEl = document.getElementById('kpi-revenue-month');
        if (totalRevenueEl) totalRevenueEl.textContent = '₹' + parseFloat(stats.earnings || 0).toLocaleString('en-IN');
        if (monthRevenueEl) monthRevenueEl.textContent = '₹' + parseFloat(stats.earnings || 0).toLocaleString('en-IN');

        // Update the Sales view KPI cards (Today's Sales & Weekly Sales)
        const salesTodayEl = document.getElementById('kpi-sales-today');
        const salesWeekEl = document.getElementById('kpi-sales-week');
        if (salesTodayEl) salesTodayEl.textContent = '₹' + parseFloat(stats.earnings || 0).toLocaleString('en-IN');
        if (salesWeekEl) salesWeekEl.textContent = '₹' + parseFloat(stats.earnings || 0).toLocaleString('en-IN');

        // Update the Ratings view KPI cards (Overall, Positive, Reviews count)
        const ratingsOverallEl = document.getElementById('kpi-ratings-overall');
        const ratingsPositiveEl = document.getElementById('kpi-ratings-positive');
        const ratingsReviewsEl = document.getElementById('kpi-ratings-reviews');
        if (ratingsOverallEl) ratingsOverallEl.textContent = parseFloat(stats.rating || 4.0).toFixed(1);
        if (ratingsPositiveEl) ratingsPositiveEl.textContent = (stats.rating >= 4.0 ? '95%' : '80%');
        if (ratingsReviewsEl) ratingsReviewsEl.textContent = stats.totalOrders || 0;

        // Fallback reports elements
        const earningsEl = document.getElementById('reportOrders'); // Revenue
        const ordersEl = document.getElementById('reportAvgOrder'); // Total orders count
        const ratingEl = document.getElementById('reportStatus'); // Rating
        
        if (earningsEl) earningsEl.textContent = '₹' + parseFloat(stats.earnings || 0).toFixed(2);
        if (ordersEl) ordersEl.textContent = stats.totalOrders || 0;
        if (ratingEl) ratingEl.textContent = parseFloat(stats.rating || 4.0).toFixed(1) + ' ★';
    }

    function updateApplicationStatusUI(profile) {
        const overlay = document.getElementById('pendingApprovalOverlay');
        if (!overlay) return;

        const status = profile.status.toUpperCase();
        if (status === 'APPROVED') {
            overlay.style.display = 'none';
        } else {
            overlay.style.display = 'flex';
            const iconEl = document.getElementById('pendingIcon');
            const titleEl = document.getElementById('pendingTitle');
            const messageEl = document.getElementById('pendingMessage');
            const badgeEl = document.getElementById('pendingStatusBadge');
            const reasonContainer = document.getElementById('pendingRejectionReasonContainer');
            const reasonText = document.getElementById('pendingRejectionReason');

            if (status === 'REJECTED') {
                if (iconEl) iconEl.textContent = '❌';
                if (titleEl) titleEl.textContent = 'Application Rejected';
                if (messageEl) messageEl.textContent = 'We regret to inform you that your restaurant application was rejected. Please review the reason below.';
                if (badgeEl) {
                    badgeEl.textContent = '🔴 REJECTED';
                    badgeEl.style.color = 'var(--md-red)';
                }
                if (reasonContainer) reasonContainer.style.display = 'block';
                if (reasonText) reasonText.textContent = profile.rejectionReason || 'No reason provided.';
            } else {
                // PENDING or other
                if (iconEl) iconEl.textContent = '⏳';
                if (titleEl) titleEl.textContent = 'Application Under Review';
                if (messageEl) messageEl.textContent = 'Your restaurant application has been submitted successfully and is currently under review by our administration team. You will get full access to the dashboard once approved.';
                if (badgeEl) {
                    badgeEl.textContent = '🟡 PENDING';
                    badgeEl.style.color = 'var(--md-tertiary)';
                }
                if (reasonContainer) reasonContainer.style.display = 'none';
            }
        }
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
            border: 1px solid var(--md-outline, #d8b894);
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
                    color: var(--md-on-surface, #1e3a2f);
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
    
    window.handleLogin = async function(event) {
        if (event) event.preventDefault();
        
        const email = document.getElementById('loginEmail').value.trim();
        const password = document.getElementById('loginPassword').value;
        
        if (!email || !password) {
            showToast('Please fill all credentials.', '❌');
            return;
        }
        
        try {
            const data = await apiFetch('/auth/login', {
                method: 'POST',
                body: { email, password }
            });
            
            if (data.role !== 'RESTAURANT') {
                showToast('Access Denied: Restaurant accounts only.', '❌');
                return;
            }
            
            localStorage.setItem('mad_token', data.token);
            localStorage.setItem('mad_user', JSON.stringify(data));
            localStorage.setItem('last_restaurant_email', email);
            localStorage.setItem('last_restaurant_password', password);
            saveAccount(email, password, 'RESTAURANT');
            
            await syncRestaurantData(data.profileId);
            
            // Switch screen to Dashboard
            if (document.getElementById('authGateway')) document.getElementById('authGateway').classList.remove('show');
            if (document.getElementById('app-container')) document.getElementById('app-container').classList.add('visible');
            if (typeof switchView === 'function') switchView('dashboard');
            if (typeof updateSidebarName === 'function') updateSidebarName(data.name);
            
            showToast('Logged in successfully!', '✓');
        } catch (err) {
            showToast('Login failed: ' + err.message, '❌');
        }
    };

    window.handleRegister = async function(event) {
        if (event) event.preventDefault();
        
        const name = document.getElementById('regName').value.trim();
        const email = document.getElementById('regEmail').value.trim();
        const phone = document.getElementById('regPhone').value.trim();
        const password = document.getElementById('regPassword').value;
        const regAddress = document.getElementById('regAddress').value.trim();
        const regFssai = document.getElementById('regFssai').value.trim();
        const regCuisine = document.getElementById('regCuisine').value.trim();
        const regGst = document.getElementById('regGst').value.trim();
        
        if (!name || !email || !phone || !password || !regAddress || !regFssai || !regCuisine) {
            showToast('Please fill in all register fields.', '❌');
            return;
        }
        
        try {
            const data = await apiFetch('/auth/register', {
                method: 'POST',
                body: { name, email, phone, password, role: 'RESTAURANT' }
            });
            
            localStorage.setItem('mad_token', data.token);
            localStorage.setItem('mad_user', JSON.stringify(data));
            localStorage.setItem('last_restaurant_email', email);
            localStorage.setItem('last_restaurant_password', password);
            saveAccount(email, password, 'RESTAURANT');
            
            // Immediately submit registration details to populate profile
            try {
                await apiFetch(`/restaurant/${data.profileId}/apply`, {
                    method: 'POST',
                    body: {
                        ownerName: name,
                        address: regAddress,
                        phone: phone,
                        docFssai: regFssai,
                        docGst: regGst,
                        docMenu: regCuisine
                    }
                });
            } catch (applyErr) {
                console.error("Immediate application profile population failed: ", applyErr);
            }
            
            await syncRestaurantData(data.profileId);
            
            // Move to Application Form view
            if (document.getElementById('authGateway')) document.getElementById('authGateway').classList.remove('show');
            if (document.getElementById('app-container')) document.getElementById('app-container').classList.add('visible');
            if (typeof switchView === 'function') switchView('profile'); // profile view houses the application submission form
            if (typeof updateSidebarName === 'function') updateSidebarName(data.name);
            
            showToast('Account created! Submit application documents.', '✓');
        } catch (err) {
            showToast('Register failed: ' + err.message, '❌');
        }
    };

    window.logout = function() {
        const userStr = localStorage.getItem('mad_user');
        if (userStr) {
            const user = JSON.parse(userStr);
            apiFetch(`/auth/logout?email=${encodeURIComponent(user.email)}`, { method: 'POST' }).catch(() => {});
        }
        
        localStorage.removeItem('mad_token');
        localStorage.removeItem('mad_user');
        localStorage.removeItem('restaurant_profile');
        
        if (document.getElementById('app-container')) document.getElementById('app-container').classList.remove('visible');
        if (document.getElementById('authGateway')) document.getElementById('authGateway').classList.add('show');
        if (document.getElementById('pendingApprovalOverlay')) document.getElementById('pendingApprovalOverlay').style.display = 'none';
        showToast('Logged out successfully', '✓');
    };

    // ────────────────────────────────────────────────────────────────────────
    // 3. APPLICATION FOR SUBMITTING DOCUMENTS
    // ────────────────────────────────────────────────────────────────────────
    
    // Handles uploading files and submitting application details
    window.submitRestaurantApplication = async function(event) {
        if (event) event.preventDefault();
        const user = JSON.parse(localStorage.getItem('mad_user'));
        if (!user) return;
        
        const ownerName = document.getElementById('appOwnerName').value.trim();
        const address = document.getElementById('appAddress').value.trim();
        const phone = document.getElementById('appPhone').value.trim();
        
        const logoFile = document.getElementById('appLogoFile').files[0];
        const gstFile = document.getElementById('appGstFile').files[0];
        const fssaiFile = document.getElementById('appFssaiFile').files[0];
        const panFile = document.getElementById('appPanFile').files[0];
        const menuFile = document.getElementById('appMenuFile').files[0];
        
        if (!ownerName || !address || !phone) {
            showToast('Please fill in owner name, address, and phone.', '❌');
            return;
        }
        
        try {
            showToast('Uploading application files...', '⏳');
            
            let logoUrl = null, gstUrl = null, fssaiUrl = null, panUrl = null, menuUrl = null;
            if (logoFile) logoUrl = await apiUploadFile(logoFile, 'restaurants/logos');
            if (gstFile) gstUrl = await apiUploadFile(gstFile, 'restaurants/docs');
            if (fssaiFile) fssaiUrl = await apiUploadFile(fssaiFile, 'restaurants/docs');
            if (panFile) panUrl = await apiUploadFile(panFile, 'restaurants/docs');
            if (menuFile) menuUrl = await apiUploadFile(menuFile, 'restaurants/docs');
            
            const appData = {
                ownerName,
                address,
                phone,
                logoUrl,
                docGst: gstUrl,
                docFssai: fssaiUrl,
                docPan: panUrl,
                docMenu: menuUrl
            };
            
            const updatedProfile = await apiFetch(`/restaurant/${user.profileId}/apply`, {
                method: 'POST',
                body: appData
            });
            
            updateApplicationStatusUI(updatedProfile);
            showToast('Application submitted to Admin successfully!', '✓');
        } catch (err) {
            showToast('Failed to submit application: ' + err.message, '❌');
        }
    };

    // ────────────────────────────────────────────────────────────────────────
    // 4. OVERWRITE FOOD ITEMS CRUD
    // ────────────────────────────────────────────────────────────────────────
    
    window.addFoodItem = async function() {
        const user = JSON.parse(localStorage.getItem('mad_user'));
        if (!user) return;
        
        const name = document.getElementById('foodName').value.trim();
        const category = document.getElementById('foodCategory').value;
        const diet = document.getElementById('foodDiet').value;
        const price = parseFloat(document.getElementById('foodPrice').value);
        const desc = document.getElementById('foodDesc').value.trim();
        
        if (!name || isNaN(price)) {
            showToast('Please fill food name and price.', '❌');
            return;
        }
        
        try {
            const newItem = await apiFetch(`/restaurant/${user.profileId}/food-items`, {
                method: 'POST',
                body: {
                    nameString: name,
                    category: category,
                    diet: diet,
                    price: price,
                    descriptionString: desc || 'Delicious food item'
                }
            });
            
            // Reload menu
            await syncRestaurantData(user.profileId);
            closeModal('addFoodModal');
            showToast(`${name} added!`, '✓');
        } catch (err) {
            showToast('Failed to add food item: ' + err.message, '❌');
        }
    };

    window.editFood = function(id) {
        const item = menuItems.find(i => i.id === id);
        if (!item) return;
        
        document.getElementById('foodName').value = item.name;
        document.getElementById('foodCategory').value = item.category;
        document.getElementById('foodPrice').value = item.price;
        document.getElementById('foodDesc').value = item.description;
        
        document.getElementById('addFoodModal').classList.add('show');
        
        // Setup custom save listener for updates
        window._addFoodOverride = async function() {
            const user = JSON.parse(localStorage.getItem('mad_user'));
            const uName = document.getElementById('foodName').value.trim();
            const uCategory = document.getElementById('foodCategory').value;
            const uPrice = parseFloat(document.getElementById('foodPrice').value);
            const uDesc = document.getElementById('foodDesc').value.trim();
            
            try {
                await apiFetch(`/restaurant/${user.profileId}/food-items/${id}`, {
                    method: 'PUT',
                    body: {
                        nameString: uName,
                        category: uCategory,
                        price: uPrice,
                        descriptionString: uDesc
                    }
                });
                
                await syncRestaurantData(user.profileId);
                closeModal('addFoodModal');
                showToast(`${uName} updated!`, '✓');
                window._addFoodOverride = null;
            } catch (err) {
                showToast('Failed to update: ' + err.message, '❌');
            }
        };
        
        const addBtn = document.querySelector('#addFoodModal .auth-btn');
        addBtn.onclick = function() {
            if (window._addFoodOverride) {
                window._addFoodOverride();
            } else {
                addFoodItem();
            }
        };
    };

    window.confirmDelete = async function() {
        if (deleteTarget) {
            const user = JSON.parse(localStorage.getItem('mad_user'));
            try {
                await apiFetch(`/restaurant/${user.profileId}/food-items/${deleteTarget}`, {
                    method: 'DELETE'
                });
                
                await syncRestaurantData(user.profileId);
                showToast('Item deleted successfully!', '✓');
                deleteTarget = null;
                closeModal('deleteModal');
            } catch (err) {
                showToast('Failed to delete item: ' + err.message, '❌');
            }
        }
    };

    // ────────────────────────────────────────────────────────────────────────
    // 5. OVERWRITE ORDER STATE CHANGES
    // ────────────────────────────────────────────────────────────────────────
    
    window.updateOrderStatusInDb = async function(orderId, newStatus) {
        const user = JSON.parse(localStorage.getItem('mad_user'));
        if (!user) return;
        
        try {
            await apiFetch(`/restaurant/${user.profileId}/orders/${orderId}/status?status=${newStatus}`, {
                method: 'PUT'
            });
            await syncRestaurantData(user.profileId);
            showToast(`Order status updated to ${newStatus.toUpperCase()}`, '✓');
        } catch (err) {
            showToast('Failed to update status: ' + err.message, '❌');
        }
    };

    // Hijack status select changes in table views
    document.addEventListener('change', (e) => {
        if (e.target && e.target.classList.contains('status-select')) {
            const row = e.target.closest('tr');
            if (row) {
                // Find order ID
                const orderIdCell = row.querySelector('td b');
                if (orderIdCell) {
                    const orderIdStr = orderIdCell.textContent; // e.g. ORD-1001 or standard string
                    // Find actual order
                    const matched = orders.find(o => o.id === orderIdStr);
                    if (matched) {
                        updateOrderStatusInDb(matched.numericId, e.target.value);
                    }
                }
            }
        }
    });

    // ────────────────────────────────────────────────────────────────────────
    // 5.5 SETTINGS PANEL OVERRIDES
    // ────────────────────────────────────────────────────────────────────────
    window.saveSetting = async function(inputId, msg) {
        const input = document.getElementById(inputId);
        const suffix = inputId.replace('settings', '');
        const editBtn = document.getElementById('edit' + suffix + 'Btn');
        const saveBtn = document.getElementById('save' + suffix + 'Btn');
        const cancelBtn = document.getElementById('cancel' + suffix + 'Btn');
        if (!input || !editBtn || !saveBtn || !cancelBtn) return;
        const value = input.value.trim();
        if (!value) { showToast('Cannot be empty', '⚠️'); return; }
        
        try {
            const user = JSON.parse(localStorage.getItem('mad_user'));
            if (!user) return;
            
            const updatedData = {
                nameString: document.getElementById('settingsRestaurantName').value.trim(),
                email: document.getElementById('settingsEmail').value.trim(),
                phone: document.getElementById('settingsPhone').value.trim(),
                address: document.getElementById('settingsAddress').value.trim(),
                docFssai: document.getElementById('settingsFssai').value.trim(),
                docMenu: document.getElementById('settingsCuisine').value.trim()
            };
            
            const keyMap = { 
                'settingsRestaurantName': 'nameString', 
                'settingsEmail': 'email', 
                'settingsPhone': 'phone',
                'settingsAddress': 'address',
                'settingsFssai': 'docFssai',
                'settingsCuisine': 'docMenu'
            };
            if (keyMap[inputId]) {
                updatedData[keyMap[inputId]] = value;
            }
            
            await apiFetch(`/restaurant/${user.profileId}/profile`, {
                method: 'PUT',
                body: updatedData
            });
            
            await syncRestaurantData(user.profileId);
            
            input.disabled = true;
            editBtn.classList.remove('hidden'); 
            saveBtn.classList.add('hidden'); 
            cancelBtn.classList.add('hidden');
            document.querySelectorAll('.setting-item .btn-icon:not(.save):not(.cancel)').forEach(btn => btn.classList.remove('hidden'));
            showToast(msg || 'Updated in database!', '✅');
        } catch (err) {
            showToast('Failed to update setting: ' + err.message, '❌');
        }
    };

    window.saveAllSettings = async function() {
        try {
            const user = JSON.parse(localStorage.getItem('mad_user'));
            if (!user) return;
            
            const updatedData = {
                nameString: document.getElementById('settingsRestaurantName').value.trim(),
                email: document.getElementById('settingsEmail').value.trim(),
                phone: document.getElementById('settingsPhone').value.trim(),
                address: document.getElementById('settingsAddress').value.trim(),
                docFssai: document.getElementById('settingsFssai').value.trim(),
                docMenu: document.getElementById('settingsCuisine').value.trim()
            };
            
            await apiFetch(`/restaurant/${user.profileId}/profile`, {
                method: 'PUT',
                body: updatedData
            });
            
            await syncRestaurantData(user.profileId);
            
            document.querySelectorAll('.setting-item .value-row input').forEach(inp => inp.disabled = true);
            document.querySelectorAll('.setting-item .btn-icon.save, .setting-item .btn-icon.cancel').forEach(btn => btn.classList.add('hidden'));
            document.querySelectorAll('.setting-item .btn-icon:not(.save):not(.cancel)').forEach(btn => btn.classList.remove('hidden'));
            
            showToast('All settings saved to database!', '✅');
        } catch (err) {
            showToast('Failed to save settings: ' + err.message, '❌');
        }
    };

    window.cancelSetting = function(inputId) {
        const input = document.getElementById(inputId);
        const suffix = inputId.replace('settings', '');
        const editBtn = document.getElementById('edit' + suffix + 'Btn');
        const saveBtn = document.getElementById('save' + suffix + 'Btn');
        const cancelBtn = document.getElementById('cancel' + suffix + 'Btn');
        if (!input || !editBtn || !saveBtn || !cancelBtn) return;
        
        const profile = JSON.parse(localStorage.getItem('restaurant_profile')) || {};
        const keyMap = { 
            'settingsRestaurantName': 'nameString', 
            'settingsEmail': 'email', 
            'settingsPhone': 'phone',
            'settingsAddress': 'address',
            'settingsFssai': 'docFssai',
            'settingsCuisine': 'docMenu'
        };
        
        input.value = profile[keyMap[inputId]] || '';
        input.disabled = true;
        
        editBtn.classList.remove('hidden'); 
        saveBtn.classList.add('hidden'); 
        cancelBtn.classList.add('hidden');
        document.querySelectorAll('.setting-item .btn-icon:not(.save):not(.cancel)').forEach(btn => btn.classList.remove('hidden'));
    };

    // ────────────────────────────────────────────────────────────────────────
    // 6. INITIALIZATION & SILENT LOGIN
    // ────────────────────────────────────────────────────────────────────────
    const savedToken = localStorage.getItem('mad_token');
    const savedUserStr = localStorage.getItem('mad_user');
    
    if (savedToken && savedUserStr) {
        const user = JSON.parse(savedUserStr);
        if (user.role === 'RESTAURANT') {
            if (document.getElementById('authGateway')) document.getElementById('authGateway').classList.remove('show');
            if (document.getElementById('app-container')) document.getElementById('app-container').classList.add('visible');
            if (typeof updateSidebarName === 'function') updateSidebarName(user.name);
            
            // Sync database data on load
            syncRestaurantData(user.profileId);
        }
    } else {
        const savedEmail = localStorage.getItem('last_restaurant_email');
        const savedPass = localStorage.getItem('last_restaurant_password');
        if (savedEmail && savedPass) {
            if (document.getElementById('loginEmail')) document.getElementById('loginEmail').value = savedEmail;
            if (document.getElementById('loginPassword')) document.getElementById('loginPassword').value = savedPass;
        }
    }
    initSavedAccountsDropdown('loginEmail', 'loginPassword', 'RESTAURANT');
});
