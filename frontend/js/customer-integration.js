(function () {
  'use strict';

  /* --- Auth helper --- */
  const AUTH_KEY = 'mf_token';
  window.authStore = {
    setToken(token) { localStorage.setItem(AUTH_KEY, token); },
    getToken() { return localStorage.getItem(AUTH_KEY); },
    clear() { localStorage.removeItem(AUTH_KEY); },
    isAuthenticated() { return !!localStorage.getItem(AUTH_KEY); }
  };
  window.fetchWithAuth = async (url, opts = {}) => {
    opts = Object.assign({}, opts);
    opts.headers = Object.assign({}, opts.headers || {});
    const token = authStore.getToken();
    if (token) opts.headers['Authorization'] = 'Bearer ' + token;
    const res = await fetch(url, opts);
    if (res.status === 401) {
      authStore.clear();
      alert('Session expired — please log in again.');
      location.reload();
      throw new Error('Unauthorized');
    }
    return res;
  };

  function toast(msg) {
    const t = document.querySelector('.toast') || document.querySelector('.toast-container .toast');
    if (!t) { console.log(msg); return; }
    t.classList.add('success');
    t.querySelector('.toast-msg')?.textContent && (t.querySelector('.toast-msg').textContent = msg);
    t.classList.add('active');
    setTimeout(()=>t.classList.remove('active'), 3500);
  }

  document.addEventListener('DOMContentLoaded', () => {
    // Bind add-to-cart on menu grid
    const menuItems = document.querySelectorAll('.menu-grid .menu-item');
    menuItems.forEach(item => {
      // Prefer explicit data food id attribute
      const foodId = item.dataset.foodId || item.getAttribute('data-food-id') || item.getAttribute('data-id');
      // Find add button heuristically
      let addBtn = item.querySelector('[data-action="add-to-cart"], .add-to-cart, .btn-add');
      if (!addBtn) {
        addBtn = Array.from(item.querySelectorAll('button')).find(b => /add\s*(to)?\s*cart/i.test(b.textContent));
      }
      if (!addBtn) return;
      addBtn.addEventListener('click', async () => {
        const fid = foodId || item.getAttribute('data-food') || null;
        if (!fid) { alert('Food id not found'); return; }
        try {
          const res = await fetchWithAuth('/api/cart', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ foodId: fid, qty: 1 })
          });
          const json = await res.json();
          if (res.ok && json.success) {
            toast('Added to cart');
            // optionally update cart counter
          } else alert(json.message || 'Add to cart failed');
        } catch (err) { alert('Network error: ' + err.message); }
      });
    });

    // Bind checkout/place order button by text content
    const placeBtn = Array.from(document.querySelectorAll('button')).find(b => /place\s*order|checkout/i.test(b.textContent));
    if (placeBtn) {
      placeBtn.addEventListener('click', async () => {
        try {
          // Page should provide cartId and addressId; adapt selectors to find them
          const cartId = window.CURRENT_CART_ID || document.querySelector('[data-cart-id]')?.getAttribute('data-cart-id');
          const addressId = window.SELECTED_ADDRESS_ID || document.querySelector('[data-selected-address]')?.getAttribute('data-selected-address');
          if (!cartId || !addressId) { alert('Select an address and ensure cart is not empty'); return; }
          const res = await fetchWithAuth('/api/orders', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ cartId, addressId, paymentMethod: 'COD' })
          });
          const json = await res.json();
          if (res.ok && json.success) {
            toast('Order placed successfully');
            // optionally redirect to order details or refresh order history
          } else alert(json.message || 'Order failed');
        } catch (err) { alert('Network error: ' + err.message); }
      });
    }

    // Optionally auto-refresh cart indicator
    async function refreshCartCount() {
      try {
        const res = await fetchWithAuth('/api/cart');
        const json = await res.json();
        if (res.ok && json.success) {
          const count = json.data.items?.reduce((s,i)=>s+i.qty,0) || 0;
          const badge = document.querySelector('.badge.cart-count');
          if (badge) badge.textContent = count;
        }
      } catch (err) { /* ignore */ }
    }
    refreshCartCount();
  });
})();
