# FDMS Backend - Complete Testing Guide

## Testing the Backend Locally

### Prerequisites
- Backend running on http://localhost:8080
- Postman, cURL, or any API client
- MySQL database connected

---

## Method 1: Using Postman (Recommended)

### Step 1: Download and Install Postman
- Download: https://www.postman.com/downloads/
- Install on your system

### Step 2: Create a New Collection
1. Open Postman
2. Click **"New"** → **"Collection"**
3. Name it: `FDMS-Backend-Testing`
4. Click **"Create"**

### Step 3: Set Up Environment Variables
1. Click **Environment** icon (top right)
2. Click **"Create New Environment"**
3. Name it: `FDMS-Local`
4. Add variables:
   ```
   Variable: base_url
   Value: http://localhost:8080/api
   
   Variable: token
   Value: (will be set after login)
   
   Variable: customerId
   Value: (will be set after registration)
   
   Variable: restaurantId
   Value: (will be set after restaurant creation)
   
   Variable: menuItemId
   Value: (will be set after menu item creation)
   
   Variable: orderId
   Value: (will be set after order creation)
   ```
5. Click **Save**

### Step 4: Test Each Endpoint

#### Test 1: Health Check
```
Method: GET
URL: {{base_url}}/health
Auth: None
```
**Expected Response:**
```json
{
  "status": "UP",
  "message": "FDMS Backend is running"
}
```

#### Test 2: Register Customer
```
Method: POST
URL: {{base_url}}/auth/register
Auth: None
Body (JSON):
```
```json
{
  "email": "customer1@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Customer",
  "phoneNumber": "9876543210",
  "address": "123 Main Street",
  "city": "New York",
  "postalCode": "10001",
  "role": "CUSTOMER"
}
```

**Expected Response (201):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "user": {
    "id": 1,
    "email": "customer1@example.com",
    "firstName": "John",
    "lastName": "Customer",
    "roles": ["CUSTOMER"]
  },
  "message": "User registered successfully"
}
```

**Save token to environment:**
- After request, go to Tests tab
- Add:
  ```javascript
  pm.environment.set("token", pm.response.json().token);
  pm.environment.set("customerId", pm.response.json().user.id);
  ```

#### Test 3: Login
```
Method: POST
URL: {{base_url}}/auth/login
Auth: None
Body (JSON):
```
```json
{
  "email": "customer1@example.com",
  "password": "password123"
}
```

#### Test 4: Register Restaurant Owner
```
Method: POST
URL: {{base_url}}/auth/register
Auth: None
Body (JSON):
```
```json
{
  "email": "owner1@example.com",
  "password": "password123",
  "firstName": "Pizza",
  "lastName": "Owner",
  "phoneNumber": "9876543211",
  "address": "456 Oak Avenue",
  "city": "New York",
  "postalCode": "10002",
  "role": "RESTAURANT_OWNER"
}
```

#### Test 5: Create Restaurant
```
Method: POST
URL: {{base_url}}/restaurants
Auth: Bearer {{token}}
Headers:
  Authorization: Bearer {{token}}
  Content-Type: application/json

Body (JSON):
```
```json
{
  "name": "Pizza Palace",
  "description": "Best pizzas in town",
  "address": "456 Oak Ave",
  "city": "New York",
  "postalCode": "10002",
  "phoneNumber": "9876543211",
  "email": "pizzapalace@example.com",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "cuisineType": "Italian"
}
```

**Expected Response (201):**
```json
{
  "id": 1,
  "name": "Pizza Palace",
  "description": "Best pizzas in town",
  "ownerId": 2,
  "cuisineType": "Italian",
  "isActive": true
}
```

#### Test 6: Create Menu Item
```
Method: POST
URL: {{base_url}}/menu?restaurantId=1
Auth: Bearer {{token}}
Body (JSON):
```
```json
{
  "name": "Margherita Pizza",
  "description": "Fresh mozzarella and basil",
  "price": 250.00,
  "category": "Pizzas",
  "imageUrl": "https://example.com/margherita.jpg",
  "isVegetarian": true,
  "preparationTime": 15
}
```

#### Test 7: Get All Restaurants
```
Method: GET
URL: {{base_url}}/restaurants
Auth: None
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "name": "Pizza Palace",
    "city": "New York",
    "cuisineType": "Italian",
    "rating": 0.0,
    "isActive": true
  }
]
```

#### Test 8: Get Restaurant Menu
```
Method: GET
URL: {{base_url}}/menu/restaurant/1
Auth: None
```

#### Test 9: Create Order
```
Method: POST
URL: {{base_url}}/orders
Auth: Bearer {{token}} (customer token)
Body (JSON):
```
```json
{
  "restaurantId": 1,
  "deliveryAddress": "789 Elm Street",
  "deliveryCity": "New York",
  "deliveryPostalCode": "10003",
  "specialInstructions": "Extra cheese please",
  "orderItems": [
    {
      "menuItemId": 1,
      "quantity": 2,
      "specialInstructions": "Extra spicy"
    }
  ]
}
```

**Expected Response (201):**
```json
{
  "id": 1,
  "orderNumber": "ORD-1720305600000",
  "userId": 1,
  "restaurantId": 1,
  "totalAmount": 555.00,
  "deliveryFee": 50.00,
  "taxAmount": 50.00,
  "status": "PENDING",
  "orderItems": [
    {
      "menuItemId": 1,
      "menuItemName": "Margherita Pizza",
      "quantity": 2,
      "price": 250.00,
      "subtotal": 500.00
    }
  ]
}
```

#### Test 10: Process Payment
```
Method: POST
URL: {{base_url}}/payments/process
Auth: Bearer {{token}} (customer token)
Body (JSON):
```
```json
{
  "orderId": 1,
  "paymentMethod": "CREDIT_CARD"
}
```

#### Test 11: Update Order Status
```
Method: PUT
URL: {{base_url}}/orders/1/status?status=CONFIRMED
Auth: Bearer {{token}} (restaurant owner token)
```

#### Test 12: Get Order Details
```
Method: GET
URL: {{base_url}}/orders/1
Auth: Bearer {{token}}
```

---

## Method 2: Using cURL (Command Line)

### Prerequisites
- Terminal/Command Prompt
- cURL installed

### Test Commands

#### 1. Health Check
```bash
curl -X GET http://localhost:8080/api/health
```

#### 2. Register Customer
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "9876543210",
    "address": "123 Main St",
    "city": "New York",
    "postalCode": "10001",
    "role": "CUSTOMER"
  }'
```

#### 3. Login and Save Token
```bash
# Login and capture token
RESPONSE=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "customer@example.com",
    "password": "password123"
  }')

# Extract token (requires jq: https://stedolan.github.io/jq/)
TOKEN=$(echo $RESPONSE | jq -r '.token')
echo "Token: $TOKEN"
```

#### 4. Get Restaurants
```bash
curl -X GET http://localhost:8080/api/restaurants
```

#### 5. Create Order (With Token)
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": 1,
    "deliveryAddress": "789 Elm St",
    "deliveryCity": "New York",
    "deliveryPostalCode": "10003",
    "orderItems": [
      {
        "menuItemId": 1,
        "quantity": 2
      }
    ]
  }'
```

#### 6. Get User Profile
```bash
curl -X GET http://localhost:8080/api/auth/user/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Method 3: Using ThunderClient (VS Code Extension)

### Installation
1. Open VS Code
2. Go to Extensions (Ctrl+Shift+X)
3. Search "Thunder Client"
4. Click Install

### Usage
1. Open Thunder Client from sidebar
2. Create new collection
3. Add requests similar to Postman
4. Set authorization header for protected endpoints

---

## Method 4: Using REST Client Extension (VS Code)

### Installation
1. Install "REST Client" extension by Huachao Mao

### Create test.http file

Create `backend/test.http`:

```http
### Health Check
GET http://localhost:8080/api/health

### Register Customer
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "customer@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "9876543210",
  "role": "CUSTOMER"
}

### Login
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "customer@example.com",
  "password": "password123"
}

### Get All Restaurants
GET http://localhost:8080/api/restaurants

### Get Restaurants by City
GET http://localhost:8080/api/restaurants/city/New%20York

### Search Restaurants
GET http://localhost:8080/api/restaurants/search?query=pizza

### Create Order (Replace token)
POST http://localhost:8080/api/orders
Authorization: Bearer YOUR_TOKEN_HERE
Content-Type: application/json

{
  "restaurantId": 1,
  "deliveryAddress": "789 Elm St",
  "deliveryCity": "New York",
  "deliveryPostalCode": "10003",
  "orderItems": [
    {
      "menuItemId": 1,
      "quantity": 2
    }
  ]
}

### Get My Orders (Replace token)
GET http://localhost:8080/api/orders/my-orders
Authorization: Bearer YOUR_TOKEN_HERE
```

Then click "Send Request" above each request.

---

## Testing Checklist

### Authentication Tests
- [ ] Register as Customer
- [ ] Register as Restaurant Owner
- [ ] Register as Delivery Partner
- [ ] Login with valid credentials
- [ ] Login with invalid credentials (should fail)
- [ ] Get user profile with valid token
- [ ] Get user profile with invalid token (should fail)
- [ ] Update user profile

### Restaurant Tests
- [ ] Get all restaurants
- [ ] Get restaurants by city
- [ ] Search restaurants
- [ ] Filter by cuisine type
- [ ] Create restaurant (as RESTAURANT_OWNER)
- [ ] Get restaurant details
- [ ] Update restaurant (as owner)
- [ ] Get my restaurant (as RESTAURANT_OWNER)

### Menu Tests
- [ ] Get restaurant menu
- [ ] Get menu item details
- [ ] Create menu item (as RESTAURANT_OWNER)
- [ ] Update menu item (as owner)
- [ ] Delete menu item (as owner)
- [ ] Search menu items
- [ ] Check vegetarian filter

### Order Tests
- [ ] Create order (as CUSTOMER)
- [ ] Get order details
- [ ] Get my orders (as CUSTOMER)
- [ ] Get restaurant orders (as RESTAURANT_OWNER)
- [ ] Update order status (as RESTAURANT_OWNER)
- [ ] Cancel order (as CUSTOMER)
- [ ] Check order status flow

### Payment Tests
- [ ] Process payment
- [ ] Get payment details
- [ ] Refund payment (as ADMIN)

### Error Cases
- [ ] Invalid email format (registration)
- [ ] Password too short
- [ ] Duplicate email
- [ ] Non-existent restaurant ID
- [ ] Unauthorized access
- [ ] Expired token
- [ ] Missing required fields

---

## Expected Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already in use",
  "path": "/api/auth/register"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials",
  "path": "/api/auth/login"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/api/orders"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Restaurant not found with ID: 999",
  "path": "/api/restaurants/999"
}
```

---

## Performance Testing

### Using Apache JMeter

1. Download: https://jmeter.apache.org/
2. Create test plan with:
   - Thread Group: 10 users
   - Ramp-up time: 10 seconds
   - Loop count: 5
3. Add HTTP Sampler for each endpoint
4. Run test and analyze results

---

## Database Verification

### Check if data is saved correctly

```sql
-- View all users
SELECT * FROM users;

-- View all orders
SELECT * FROM orders;

-- View restaurants
SELECT * FROM restaurants;

-- View menu items
SELECT * FROM menu_items;

-- View order items
SELECT oi.*, mi.name, o.order_number 
FROM order_items oi
JOIN menu_items mi ON oi.menu_item_id = mi.id
JOIN orders o ON oi.order_id = o.id;

-- View payments
SELECT * FROM payments;
```

---

## Debugging Tips

### Check Backend Logs
```bash
# If running with mvn spring-boot:run
# Look for ERROR, WARN, or DEBUG messages

# Common issues:
# - Database connection failed
# - Port already in use
# - JWT validation failed
# - Authentication error
```

### Enable Debug Mode

Add to `application.properties`:
```properties
logging.level.com.fdms=DEBUG
logging.level.org.springframework.security=DEBUG
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Test Database Connection
```bash
mysql -u root -p fdms_db
SHOW TABLES;
SELECT * FROM roles;
```

---

## Test Data Sets

### Customer User
```json
{
  "email": "customer@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Customer",
  "phoneNumber": "9876543210",
  "role": "CUSTOMER"
}
```

### Restaurant Owner
```json
{
  "email": "owner@example.com",
  "password": "password123",
  "firstName": "Pizza",
  "lastName": "Owner",
  "phoneNumber": "9876543211",
  "role": "RESTAURANT_OWNER"
}
```

### Delivery Partner
```json
{
  "email": "delivery@example.com",
  "password": "password123",
  "firstName": "Quick",
  "lastName": "Delivery",
  "phoneNumber": "9876543212",
  "role": "DELIVERY_PARTNER"
}
```

### Admin User
```json
{
  "email": "admin@example.com",
  "password": "password123",
  "firstName": "System",
  "lastName": "Admin",
  "phoneNumber": "9876543213",
  "role": "ADMIN"
}
```

---

## Integration Testing (Frontend)

### Connect Frontend to Backend

Update your frontend API calls:

**Before:**
```javascript
const API_URL = 'http://mock-api.example.com';
```

**After:**
```javascript
const API_URL = 'http://localhost:8080/api';
```

### Example Frontend Integration

```javascript
// Login function
async function login(email, password) {
  try {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, password })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      // Save token
      localStorage.setItem('token', data.token);
      console.log('Login successful', data.user);
    } else {
      console.error('Login failed', data);
    }
  } catch (error) {
    console.error('Error:', error);
  }
}

// Get restaurants
async function getRestaurants() {
  try {
    const response = await fetch('http://localhost:8080/api/restaurants');
    const restaurants = await response.json();
    console.log('Restaurants:', restaurants);
    return restaurants;
  } catch (error) {
    console.error('Error:', error);
  }
}

// Create order (with token)
async function createOrder(orderData) {
  const token = localStorage.getItem('token');
  
  try {
    const response = await fetch('http://localhost:8080/api/orders', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(orderData)
    });
    
    const order = await response.json();
    console.log('Order created:', order);
    return order;
  } catch (error) {
    console.error('Error:', error);
  }
}
```

---

## Recommended Testing Flow

1. **Start Backend**
   ```bash
   mvn spring-boot:run
   ```

2. **Health Check**
   - Test `/api/health` endpoint

3. **Authentication Flow**
   - Register customer
   - Register restaurant owner
   - Login both users
   - Save tokens

4. **Restaurant Setup**
   - Create restaurant (as owner)
   - Add menu items (as owner)

5. **Order Flow**
   - Get restaurants (as customer)
   - Get menu (as customer)
   - Create order (as customer)
   - Get order (both customer and owner)

6. **Payment Flow**
   - Process payment (as customer)
   - Verify payment created

7. **Order Status Update**
   - Update status to CONFIRMED (as owner)
   - Update status to PREPARING (as owner)
   - Check status changes

8. **Error Cases**
   - Test invalid credentials
   - Test unauthorized access
   - Test validation errors

---

**Happy Testing! 🧪**
