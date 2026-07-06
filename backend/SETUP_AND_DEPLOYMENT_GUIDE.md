# FDMS Backend - Complete Setup and Deployment Guide

## Project Overview
Food Delivery Management System (FDMS) Backend - A comprehensive Spring Boot REST API for a multi-role food delivery platform.

**Built with:** Spring Boot 3.1.5 | Spring Security | JWT Authentication | MySQL | JPA/Hibernate

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    FDMS Backend Architecture                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Frontend (React/Angular)                                  │
│         ↓                                                   │
│  Controllers (REST Endpoints)                              │
│         ↓                                                   │
│  Services (Business Logic)                                 │
│         ↓                                                   │
│  Repositories (Data Access)                                │
│         ↓                                                   │
│  MySQL Database                                            │
│                                                             │
│  Cross-cutting: JWT Authentication | CORS | Exception     │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## Folder Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/fdms/
│   │   │   ├── FdmsBackendApplication.java          (Entry point)
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java              (Authentication APIs)
│   │   │   │   ├── RestaurantController.java        (Restaurant APIs)
│   │   │   │   ├── MenuController.java              (Menu Management APIs)
│   │   │   │   ├── OrderController.java             (Order APIs)
│   │   │   │   ├── PaymentController.java           (Payment APIs)
│   │   │   │   └── HealthController.java            (Health Check)
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java                 (Auth Logic)
│   │   │   │   ├── RestaurantService.java           (Restaurant Logic)
│   │   │   │   ├── MenuService.java                 (Menu Logic)
│   │   │   │   ├── OrderService.java                (Order Logic)
│   │   │   │   └── PaymentService.java              (Payment Logic)
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RoleRepository.java
│   │   │   │   ├── RestaurantRepository.java
│   │   │   │   ├── MenuItemRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── OrderItemRepository.java
│   │   │   │   └── PaymentRepository.java
│   │   │   ├── entity/
│   │   │   │   ├── User.java                        (User Entity)
│   │   │   │   ├── Role.java                        (Role Entity)
│   │   │   │   ├── Restaurant.java                  (Restaurant Entity)
│   │   │   │   ├── MenuItem.java                    (MenuItem Entity)
│   │   │   │   ├── Order.java                       (Order Entity)
│   │   │   │   ├── OrderItem.java                   (OrderItem Entity)
│   │   │   │   └── Payment.java                     (Payment Entity)
│   │   │   ├── dto/
│   │   │   │   ├── AuthRequest.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── UserDto.java
│   │   │   │   ├── RestaurantDto.java
│   │   │   │   ├── MenuItemDto.java
│   │   │   │   ├── OrderDto.java
│   │   │   │   ├── OrderItemDto.java
│   │   │   │   ├── PaymentDto.java
│   │   │   │   ├── CreateOrderRequest.java
│   │   │   │   └── ProcessPaymentRequest.java
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java            (JWT Token Generation)
│   │   │   │   ├── JwtAuthenticationFilter.java     (JWT Filter)
│   │   │   │   ├── CustomUserDetailsService.java    (User Details)
│   │   │   │   └── SecurityUtils.java               (Security Utilities)
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java              (Spring Security Config)
│   │   │   └── exception/
│   │   │       ├── GlobalExceptionHandler.java      (Exception Handling)
│   │   │       └── ErrorResponse.java               (Error Response DTO)
│   │   └── resources/
│   │       └── application.properties               (Configuration)
│   └── test/
│       └── java/com/fdms/                           (Test Classes)
├── database/
│   └── init.sql                                     (Database Schema)
├── pom.xml                                          (Maven Dependencies)
└── README.md                                        (Documentation)
```

---

## Prerequisites

1. **Java Development Kit (JDK)**
   - Version: 17 or higher
   - Download: https://www.oracle.com/java/technologies/downloads/

2. **Maven**
   - Version: 3.6 or higher
   - Download: https://maven.apache.org/download.cgi

3. **MySQL Database**
   - Version: 8.0 or higher
   - Download: https://dev.mysql.com/downloads/mysql/

4. **Git**
   - Download: https://git-scm.com/

5. **IDE (Optional but Recommended)**
   - IntelliJ IDEA: https://www.jetbrains.com/idea/
   - Eclipse: https://www.eclipse.org/
   - VS Code: https://code.visualstudio.com/

---

## Installation Steps

### Step 1: Clone the Repository
```bash
git clone https://github.com/thanvithasomalaraju/FDMS-alpha-aura.git
cd FDMS-alpha-aura
git checkout prasad
```

### Step 2: Set Up MySQL Database

1. Open MySQL Command Line or MySQL Workbench
2. Execute the database initialization script:
   ```bash
   mysql -u root -p < backend/database/init.sql
   ```
3. Verify database creation:
   ```sql
   USE fdms_db;
   SHOW TABLES;
   ```

### Step 3: Configure Application Properties

Edit `backend/src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/fdms_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# JWT Configuration
jwt.secret=your-super-secret-key-change-this-in-production-environment-12345678901234567890
jwt.expiration=86400000
```

### Step 4: Install Dependencies and Build

```bash
cd backend
mvn clean install
```

### Step 5: Run the Application

```bash
mvn spring-boot:run
```

Or:

```bash
java -jar target/fdms-backend-1.0.0.jar
```

The application will start at: `http://localhost:8080`

---

## API Endpoints

### Authentication Endpoints

#### 1. Register User
- **URL:** `POST /api/auth/register`
- **Description:** Register a new user
- **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "9876543210",
    "address": "123 Main St",
    "city": "New York",
    "postalCode": "10001",
    "role": "CUSTOMER"
  }
  ```
- **Response:**
  ```json
  {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": null,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "roles": ["CUSTOMER"]
    },
    "message": "User registered successfully"
  }
  ```

#### 2. Login
- **URL:** `POST /api/auth/login`
- **Description:** Login user and get JWT token
- **Request Body:**
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Response:** Same as Register

#### 3. Get User Details
- **URL:** `GET /api/auth/user/{id}`
- **Description:** Get user profile details
- **Auth:** Required (JWT Token)
- **Response:**
  ```json
  {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "9876543210",
    "address": "123 Main St",
    "city": "New York",
    "postalCode": "10001",
    "isActive": true,
    "roles": ["CUSTOMER"],
    "createdAt": "2024-01-01T10:00:00"
  }
  ```

#### 4. Update User Profile
- **URL:** `PUT /api/auth/user/{id}`
- **Description:** Update user profile
- **Auth:** Required (JWT Token)
- **Request Body:** Same as above (partial fields allowed)
- **Response:** Updated User DTO

---

### Restaurant Endpoints

#### 1. Create Restaurant
- **URL:** `POST /api/restaurants`
- **Description:** Create a new restaurant (RESTAURANT_OWNER only)
- **Auth:** Required (JWT Token)
- **Request Body:**
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

#### 2. Get Restaurant by ID
- **URL:** `GET /api/restaurants/{id}`
- **Description:** Get restaurant details
- **Auth:** Not Required
- **Response:** Restaurant DTO

#### 3. Get All Restaurants
- **URL:** `GET /api/restaurants`
- **Description:** Get all active restaurants
- **Auth:** Not Required
- **Response:** Array of Restaurant DTOs

#### 4. Get Restaurants by City
- **URL:** `GET /api/restaurants/city/{city}`
- **Description:** Get restaurants in a specific city
- **Auth:** Not Required

#### 5. Search Restaurants
- **URL:** `GET /api/restaurants/search?query=pizza`
- **Description:** Search restaurants by name
- **Auth:** Not Required

#### 6. Get Restaurants by Cuisine Type
- **URL:** `GET /api/restaurants/cuisine/{cuisineType}`
- **Description:** Get restaurants by cuisine type
- **Auth:** Not Required

#### 7. Update Restaurant
- **URL:** `PUT /api/restaurants/{id}`
- **Description:** Update restaurant details (RESTAURANT_OWNER only)
- **Auth:** Required (JWT Token)

#### 8. Get My Restaurant
- **URL:** `GET /api/restaurants/owner/my-restaurant`
- **Description:** Get current user's restaurant (RESTAURANT_OWNER only)
- **Auth:** Required (JWT Token)

---

### Menu Endpoints

#### 1. Create Menu Item
- **URL:** `POST /api/menu?restaurantId={id}`
- **Description:** Add menu item to restaurant
- **Auth:** Required (RESTAURANT_OWNER)
- **Request Body:**
  ```json
  {
    "name": "Margherita Pizza",
    "description": "Fresh mozzarella and basil",
    "price": 250.00,
    "category": "Pizzas",
    "imageUrl": "https://example.com/pizza.jpg",
    "isVegetarian": true,
    "preparationTime": 15
  }
  ```

#### 2. Get Menu Item
- **URL:** `GET /api/menu/{id}`
- **Description:** Get menu item details
- **Auth:** Not Required

#### 3. Get Restaurant Menu
- **URL:** `GET /api/menu/restaurant/{restaurantId}`
- **Description:** Get available menu items for a restaurant
- **Auth:** Not Required

#### 4. Get All Menu Items
- **URL:** `GET /api/menu/restaurant/{restaurantId}/all`
- **Description:** Get all menu items (including unavailable)
- **Auth:** Not Required

#### 5. Search Menu Items
- **URL:** `GET /api/menu/search?restaurantId={id}&query=pizza`
- **Description:** Search menu items
- **Auth:** Not Required

#### 6. Update Menu Item
- **URL:** `PUT /api/menu/{id}`
- **Description:** Update menu item
- **Auth:** Required (RESTAURANT_OWNER)

#### 7. Delete Menu Item
- **URL:** `DELETE /api/menu/{id}`
- **Description:** Delete menu item
- **Auth:** Required (RESTAURANT_OWNER)

---

### Order Endpoints

#### 1. Create Order
- **URL:** `POST /api/orders`
- **Description:** Create a new order (CUSTOMER only)
- **Auth:** Required (JWT Token)
- **Request Body:**
  ```json
  {
    "restaurantId": 1,
    "deliveryAddress": "789 Elm St",
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
- **Response:** Created Order DTO with order number

#### 2. Get Order
- **URL:** `GET /api/orders/{id}`
- **Description:** Get order details
- **Auth:** Required (JWT Token)

#### 3. Get Order by Order Number
- **URL:** `GET /api/orders/number/{orderNumber}`
- **Description:** Get order by order number
- **Auth:** Required (JWT Token)

#### 4. Get My Orders
- **URL:** `GET /api/orders/my-orders`
- **Description:** Get current customer's orders (CUSTOMER only)
- **Auth:** Required (JWT Token)

#### 5. Get Restaurant Orders
- **URL:** `GET /api/orders/restaurant/{restaurantId}`
- **Description:** Get all orders for a restaurant (RESTAURANT_OWNER only)
- **Auth:** Required (JWT Token)

#### 6. Get Delivery Partner Orders
- **URL:** `GET /api/orders/delivery-partner/my-orders`
- **Description:** Get delivery partner's orders
- **Auth:** Required (JWT Token)

#### 7. Update Order Status
- **URL:** `PUT /api/orders/{id}/status?status=CONFIRMED`
- **Description:** Update order status
- **Auth:** Required (RESTAURANT_OWNER or DELIVERY_PARTNER)
- **Status Values:** PENDING, CONFIRMED, PREPARING, READY_FOR_PICKUP, OUT_FOR_DELIVERY, DELIVERED, CANCELLED, FAILED

#### 8. Assign Delivery Partner
- **URL:** `PUT /api/orders/{id}/assign-delivery?deliveryPartnerId={id}`
- **Description:** Assign delivery partner to order (ADMIN only)
- **Auth:** Required (JWT Token)

#### 9. Cancel Order
- **URL:** `PUT /api/orders/{id}/cancel`
- **Description:** Cancel order (CUSTOMER only)
- **Auth:** Required (JWT Token)

---

### Payment Endpoints

#### 1. Process Payment
- **URL:** `POST /api/payments/process`
- **Description:** Process payment for an order
- **Auth:** Required (CUSTOMER)
- **Request Body:**
  ```json
  {
    "orderId": 1,
    "paymentMethod": "CREDIT_CARD"
  }
  ```
- **Response:** Payment DTO

#### 2. Get Payment by Order
- **URL:** `GET /api/payments/order/{orderId}`
- **Description:** Get payment details for an order
- **Auth:** Required (JWT Token)

#### 3. Get Payment
- **URL:** `GET /api/payments/{id}`
- **Description:** Get payment details
- **Auth:** Required (JWT Token)

#### 4. Refund Payment
- **URL:** `PUT /api/payments/{id}/refund`
- **Description:** Refund a completed payment (ADMIN only)
- **Auth:** Required (JWT Token)

---

### Health Check

#### Health Check Endpoint
- **URL:** `GET /api/health`
- **Description:** Check if backend is running
- **Auth:** Not Required
- **Response:**
  ```json
  {
    "status": "UP",
    "message": "FDMS Backend is running"
  }
  ```

---

## Database Schema

### Tables Overview

1. **roles** - User roles (ADMIN, CUSTOMER, RESTAURANT_OWNER, DELIVERY_PARTNER)
2. **users** - User accounts with authentication details
3. **user_roles** - Junction table for user-role relationships (M2M)
4. **restaurants** - Restaurant information linked to owner user
5. **menu_items** - Menu items for each restaurant
6. **orders** - Customer orders with status tracking
7. **order_items** - Individual items in each order
8. **payments** - Payment transactions for orders

### Key Relationships

```
User (1) ──┬── (M) Orders
           ├── (M) UserRoles ── (M) Roles
           └── (1) Restaurant

Restaurant (1) ──┬── (M) MenuItems
                 └── (M) Orders

Order (1) ──┬── (M) OrderItems ── (M) MenuItems
            └── (1) Payment
```

---

## JWT Authentication

### How JWT Works in This Application

1. **Token Generation:** On login/register, JWT token is generated with user ID and roles
2. **Token Format:** `Bearer <token>` in Authorization header
3. **Token Validation:** JwtAuthenticationFilter validates token on every request
4. **Token Claims:** Contains user ID, email, and roles

### Using JWT in API Requests

Add to request header:
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

---

## User Roles and Permissions

### 1. CUSTOMER
- Register and login
- View restaurants and menus
- Create orders
- Process payments
- View own orders
- Cancel orders

### 2. RESTAURANT_OWNER
- Register and create restaurant
- Manage menu items
- View restaurant orders
- Update order status
- View restaurant details

### 3. DELIVERY_PARTNER
- Register account
- View assigned orders
- Update delivery status
- View delivery partner orders

### 4. ADMIN
- All permissions
- Assign delivery partners
- Refund payments
- User management

---

## Error Handling

All errors are returned in standard format:

```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descriptive error message",
  "path": "/api/endpoint",
  "validationErrors": {
    "field": "error message"
  }
}
```

### Common Error Codes
- **400:** Bad Request / Validation Error
- **401:** Unauthorized / Invalid JWT
- **403:** Forbidden / Insufficient Permissions
- **404:** Not Found
- **500:** Internal Server Error

---

## CORS Configuration

CORS is enabled for:
- `http://localhost:3000`
- `http://localhost:5173`

Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
Allowed Headers: * (All)

---

## Configuration File Details

### application.properties

```properties
# Server
server.port=8080
spring.application.name=fdms-backend

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/fdms_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# JWT
jwt.secret=your-secret-key
jwt.expiration=86400000

# CORS
spring.web.cors.allowed-origins=http://localhost:3000,http://localhost:5173
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*

# Logging
logging.level.root=INFO
logging.level.com.fdms=DEBUG
```

---

## Testing with Postman

1. Import Postman collection (if available)
2. Set up variables:
   - `base_url`: http://localhost:8080
   - `token`: Retrieved from login endpoint
3. Use `Bearer {{token}}` in Authorization tab for protected endpoints

---

## Troubleshooting

### Database Connection Issues
```
Error: Communications link failure
Solution: 
- Ensure MySQL is running
- Check database credentials in application.properties
- Verify database exists: mysql -u root -p fdms_db
```

### Port Already in Use
```
Error: Address already in use :8080
Solution: 
- Change port in application.properties: server.port=8081
- Or kill process using port 8080
```

### JWT Token Expired
```
Error: JWT expired
Solution:
- Login again to get new token
- Change jwt.expiration value if needed
```

### CORS Issues
```
Error: Cross-Origin Request Blocked
Solution:
- Update spring.web.cors.allowed-origins in application.properties
- Ensure frontend URL is in allowed list
```

---

## Git Workflow

### Push Backend to GitHub

```bash
# Navigate to project root
cd FDMS-alpha-aura

# Add all backend files
git add backend/

# Commit changes
git commit -m "Add complete Spring Boot backend for FDMS"

# Push to prasad branch
git push origin prasad
```

### Pull Latest Backend

```bash
git pull origin prasad
```

---

## Production Deployment Checklist

- [ ] Change JWT secret key to a strong value
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate`
- [ ] Enable HTTPS
- [ ] Configure proper CORS origins
- [ ] Set environment variables for sensitive data
- [ ] Configure database backups
- [ ] Set up monitoring and logging
- [ ] Enable rate limiting
- [ ] Configure database pool size
- [ ] Use strong database password
- [ ] Enable Spring Security features
- [ ] Test all endpoints

---

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT Documentation](https://jwt.io/)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Maven Documentation](https://maven.apache.org/)

---

## Support & Contact

For issues or questions, please create an issue in the GitHub repository.

---

## License

This project is proprietary and confidential.
