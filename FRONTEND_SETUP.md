# FDMS Frontend - Quick Start Guide

## Prerequisites
- Any modern web browser (Chrome, Firefox, Safari, Edge)
- Live Server extension (optional but recommended)
- Backend running on http://localhost:8080/api

## Step 1: Backend Configuration
Make sure backend is running:
```bash
# In backend directory
mvn spring-boot:run
```

## Step 2: Check API Configuration
Open `frontend/js/api.js` and verify:
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## Step 3: Open Frontend

### Option A: Using Live Server (Recommended)

1. Install "Live Server" extension in VS Code
2. Right-click on HTML file → "Open with Live Server"
3. Browser opens automatically at `http://127.0.0.1:5500`

### Option B: Direct Browser

1. Open file directly in browser:
   - Customer: `file:///path/to/customer/newone.html`
   - Restaurant: `file:///path/to/restaurant/deepseek_html_20260626_b410ce (1).html`
   - Admin: `file:///path/to/admin/admin1.html`
   - Delivery: `file:///path/to/delivery/deepseek_html_20260625_e8121b.html`

### Option C: Python Simple Server

```bash
cd FDMS-alpha-aura
python -m http.server 8000

# Visit http://localhost:8000/customer/newone.html
```

## Step 4: Login with Demo Credentials

**Customer:**
- Email: customer1@madfoods.com
- Password: customer123

**Restaurant:**
- Email: restaurant1@madfoods.com
- Password: restaurant123

**Delivery Partner:**
- Email: delivery1@madfoods.com
- Password: delivery123

**Admin:**
- Email: admin@madfoods.com
- Password: admin123

## Step 5: Test Features

### Customer Portal
- [ ] Login/Register
- [ ] Browse restaurants
- [ ] Search foods
- [ ] Add to cart
- [ ] Place order
- [ ] Track order
- [ ] View history

### Restaurant Dashboard
- [ ] Login
- [ ] View orders
- [ ] Accept/Reject orders
- [ ] Update order status
- [ ] Add menu items
- [ ] Edit/Delete foods

### Admin Dashboard
- [ ] View all users
- [ ] Block/Unblock users
- [ ] View analytics
- [ ] Approve restaurants

## Troubleshooting

### API Calls Not Working
1. Check backend is running: `http://localhost:8080/api`
2. Check browser console for errors (F12)
3. Verify CORS is enabled
4. Check JWT token in localStorage

### CORS Error
```
Access to XMLHttpRequest at 'http://localhost:8080/api/...' from origin 
'http://127.0.0.1:5500' has been blocked by CORS policy
```

**Solution:**
1. Ensure backend is running
2. Check `application.properties` CORS settings
3. Restart backend

### Page Shows Blank
1. Check browser console (F12 → Console tab)
2. Verify API_BASE_URL in api.js
3. Check network requests (F12 → Network tab)

## Browser DevTools

Press `F12` to open DevTools:
- **Console**: See JavaScript errors
- **Network**: Monitor API calls
- **Application**: Check localStorage for token
- **Elements**: Inspect HTML structure

## Next Steps

1. Customize styles in HTML files
2. Add more food items in database
3. Test real orders flow
4. Deploy to production

## Support

For issues, check:
1. README.md - Full documentation
2. backend/BACKEND_SETUP.md - Backend setup
3. Console errors in browser (F12)
4. Network requests in browser DevTools
