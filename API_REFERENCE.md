# API Reference - MAD FOOD FDMS

## Base URL
```
http://localhost:8080/api
```

## Authentication

All protected endpoints require JWT token in header:
```
Authorization: Bearer <token>
```

---

## Authentication Endpoints

### 1. Login
```
POST /auth/login

Request:
{
  "email": "customer1@madfoods.com",
  "password": "customer123",
  "role": "CUSTOMER"
}

Response: 200 OK
{
  "success": true,
  "message": "Login successful",
  "token": "eyJhbGc...",
  "userId": 2,
  "role": "CUSTOMER"
}
```

### 2. Register
```
POST /auth/register

Request:
{
  "email": "newuser@madfoods.com",
  "password": "password123",
  "role": "CUSTOMER"
}

Response: 200 OK
{
  "success": true,
  "message": "Registration successful",
  "userId": 10
}
```

---

## Customer Endpoints

### 1. Get All Customers
```
GET /customers

Response: 200 OK
[
  {
    "id": 1,
    "userId": 2,
    "dietPreference": "veg",
    "totalOrders": 5,
    "totalSpent": 2500
  }
]
```

### 2. Get Customer by ID
```
GET /customers/{id}

Response: 200 OK
{
  "id": 1,
  "userId": 2,
  "dietPreference": "veg",
  "latitude": 28.6139,
  "longitude": 77.2090,
  "totalOrders": 5,
  "totalSpent": 2500
}
```

### 3. Create Customer
```
POST /customers

Request:
{
  "userId": 2,
  "dietPreference": "both",
  "latitude": 28.6139,
  "longitude": 77.2090
}

Response: 201 Created
```

### 4. Update Customer
```
PUT /customers/{id}

Request:
{
  "dietPreference": "nonveg",
  "latitude": 28.6200,
  "longitude": 77.2100
}

Response: 200 OK
```

---

## Restaurant Endpoints

### 1. Get All Restaurants
```
GET /restaurants

Response: 200 OK
[
  {
    "id": 1,
    "restaurantName": "Taj Express",
    "cuisineType": "Indian",
    "rating": 4.5,
    "isApproved": true,
    "isOpen": true
  }
]
```

### 2. Get Restaurant by ID
```
GET /restaurants/{id}

Response: 200 OK
{
  "id": 1,
  "restaurantName": "Taj Express",
  "cuisineType": "Indian",
  "address": "Mumbai, India",
  "rating": 4.5,
  "totalOrders": 100,
  "revenue": 50000
}
```

### 3. Create Restaurant
```
POST /restaurants

Request:
{
  "userId": 4,
  "restaurantName": "New Restaurant",
  "cuisineType": "Chinese",
  "address": "Bangalore, India"
}

Response: 201 Created
```

### 4. Update Restaurant
```
PUT /restaurants/{id}

Request:
{
  "restaurantName": "Updated Name",
  "isOpen": false
}

Response: 200 OK
```

---

## Food Endpoints

### 1. Get All Foods
```
GET /foods

Response: 200 OK
[
  {
    "id": 1,
    "foodName": "Butter Chicken",
    "price": 250,
    "dietType": "nonveg",
    "isAvailable": true
  }
]
```

### 2. Get Foods by Restaurant
```
GET /foods/restaurant/{restaurantId}

Response: 200 OK
[
  {
    "id": 1,
    "foodName": "Butter Chicken",
    "price": 250,
    "category": "Main Course",
    "preparationTime": 20
  }
]
```

### 3. Get Food by ID
```
GET /foods/{id}

Response: 200 OK
{
  "id": 1,
  "foodName": "Butter Chicken",
  "description": "Creamy butter chicken with rice",
  "price": 250,
  "category": "Main Course",
  "dietType": "nonveg",
  "preparationTime": 20,
  "rating": 4.5
}
```

### 4. Create Food
```
POST /foods

Request:
{
  "restaurantId": 1,
  "foodName": "Biryani",
  "description": "Fragrant biryani",
  "price": 300,
  "category": "Main Course",
  "dietType": "nonveg",
  "preparationTime": 25
}

Response: 201 Created
```

### 5. Update Food
```
PUT /foods/{id}

Request:
{
  "price": 280,
  "isAvailable": false
}

Response: 200 OK
```

### 6. Delete Food
```
DELETE /foods/{id}

Response: 200 OK
{
  "message": "Food deleted successfully"
}
```

---

## Order Endpoints

### 1. Get All Orders
```
GET /orders

Response: 200 OK
[
  {
    "id": 1,
    "customerId": 1,
    "restaurantId": 1,
    "totalAmount": 650,
    "status": "DELIVERED"
  }
]
```

### 2. Get Orders by Customer
```
GET /orders/customer/{customerId}

Response: 200 OK
[
  {
    "id": 1,
    "restaurantId": 1,
    "totalAmount": 650,
    "status": "DELIVERED",
    "createdAt": "2024-07-08T10:30:00"
  }
]
```

### 3. Get Order by ID
```
GET /orders/{id}

Response: 200 OK
{
  "id": 1,
  "customerId": 1,
  "restaurantId": 1,
  "deliveryPartnerId": 1,
  "totalAmount": 650,
  "deliveryFee": 40,
  "status": "DELIVERED",
  "paymentMethod": "CASH",
  "deliveryAddress": "123 Main St, City",
  "createdAt": "2024-07-08T10:30:00",
  "deliveredAt": "2024-07-08T11:15:00"
}
```

### 4. Create Order
```
POST /orders

Request:
{
  "customerId": 1,
  "restaurantId": 1,
  "totalAmount": 650,
  "deliveryFee": 40,
  "paymentMethod": "CASH",
  "deliveryAddress": "123 Main St, City",
  "deliveryLatitude": 28.6139,
  "deliveryLongitude": 77.2090
}

Response: 201 Created
{
  "id": 1,
  "status": "PENDING"
}
```

### 5. Update Order Status
```
PUT /orders/{id}

Request:
{
  "status": "CONFIRMED"
}

Response: 200 OK
```

---

## Delivery Partner Endpoints

### 1. Get All Delivery Partners
```
GET /delivery-partners

Response: 200 OK
[
  {
    "id": 1,
    "userId": 6,
    "rating": 4.5,
    "totalDeliveries": 50,
    "isActive": true
  }
]
```

### 2. Get Active Delivery Partners
```
GET /delivery-partners/active

Response: 200 OK
[
  {
    "id": 1,
    "userId": 6,
    "latitude": 28.6139,
    "longitude": 77.2090,
    "isActive": true
  }
]
```

---

## Admin Endpoints

### 1. Get All Users
```
GET /admin/users
Authorization: Bearer <admin_token>

Response: 200 OK
[
  {
    "id": 1,
    "email": "user@madfoods.com",
    "fullName": "User Name",
    "role": "CUSTOMER",
    "isActive": true
  }
]
```

### 2. Block User
```
PUT /admin/users/{id}/block
Authorization: Bearer <admin_token>

Response: 200 OK
{
  "message": "User blocked successfully"
}
```

### 3. Unblock User
```
PUT /admin/users/{id}/unblock
Authorization: Bearer <admin_token>

Response: 200 OK
{
  "message": "User unblocked successfully"
}
```

### 4. Delete User
```
DELETE /admin/users/{id}
Authorization: Bearer <admin_token>

Response: 200 OK
{
  "message": "User deleted successfully"
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-07-08T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-07-08T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-07-08T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-07-08T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An error occurred"
}
```

---

## Rate Limiting

- **Limit**: 100 requests per minute
- **Headers**: 
  - `X-RateLimit-Limit: 100`
  - `X-RateLimit-Remaining: 95`
  - `X-RateLimit-Reset: 1720419000`

---

## Pagination

For list endpoints:
```
GET /foods?page=1&size=10&sort=id,desc

Query Parameters:
- page: Page number (0-based)
- size: Items per page (default: 10)
- sort: Sort field and direction
```

---

## Versioning

Current API Version: **v1**

Future endpoints will use `/api/v2/...`
