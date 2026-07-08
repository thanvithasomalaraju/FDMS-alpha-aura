# Mad Food - Food Delivery Management System (FDMS)

## 🍔 Project Overview

Mad Food is a comprehensive **Food Delivery Management System** built with:
- **Frontend**: HTML5, CSS3, JavaScript (Responsive & Modern UI)
- **Backend**: Spring Boot 3.2.0 with MySQL
- **Architecture**: MVC with JWT Authentication & Role-Based Authorization

---

## 🏗️ Project Structure

```
FDMS-alpha-aura/
├── frontend/
│   ├── customer/
│   │   └── newone.html          # Customer Portal
│   ├── restaurant/
│   │   └── deepseek_html_...    # Restaurant Dashboard
│   ├── delivery/
│   │   └── deepseek_html_...    # Delivery Partner Portal
│   ├── admin/
│   │   └── admin1.html          # Admin Dashboard
│   └── js/
│       └── api.js               # API Client
│
├── backend/
│   ├── pom.xml                  # Maven Dependencies
│   ├── src/main/java/com/madfood/
│   │   ├── FdmsApplication.java
│   │   ├── entity/              # JPA Entities
│   │   ├── repository/          # Data Access Layer
│   │   ├── controller/          # REST Controllers
│   │   ├── service/             # Business Logic
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── util/                # Utilities (JWT)
│   │   └── config/              # Security & Configuration
│   └── src/main/resources/
│       └── application.properties
│
└── database/
    ├── schema.sql               # Database Tables
    └── sample_data.sql          # Sample Data
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven 3.8+
- Node.js (optional, for running frontend locally)

### Backend Setup

1. **Clone Repository**
   ```bash
   git clone https://github.com/thanvithasomalaraju/FDMS-alpha-aura.git
   cd FDMS-alpha-aura
   ```

2. **Create Database**
   ```bash
   mysql -u root -p < database/schema.sql
   mysql -u root -p mad_food_db < database/sample_data.sql
   ```

3. **Configure Database**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/mad_food_db
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD
   ```

4. **Build & Run Backend**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   Backend will run on `http://localhost:8080/api`

### Frontend Setup

1. **Open in Browser**
   - Customer Portal: `customer/newone.html`
   - Restaurant Dashboard: `restaurant/deepseek_html_20260626_b410ce (1).html`
   - Delivery Partner: `delivery/deepseek_html_20260625_e8121b.html`
   - Admin Dashboard: `admin/admin1.html`

2. **Or use Live Server (VS Code)**
   ```bash
   # Install Live Server extension
   # Right-click on HTML file → Open with Live Server
   ```

---

## 👥 User Roles & Features

### 🧑 Customer
- ✅ Register & Login
- ✅ Browse Restaurants
- ✅ Search & Filter Foods
- ✅ Add to Cart & Place Orders
- ✅ Track Order Status
- ✅ View Order History
- ✅ Rate & Review
- ✅ Manage Addresses
- ✅ Update Profile

### 🏪 Restaurant
- ✅ Register & Login
- ✅ Add/Edit/Delete Foods
- ✅ Upload Food Images
- ✅ View Incoming Orders
- ✅ Accept/Reject Orders
- ✅ Update Order Status
- ✅ View Earnings & Analytics
- ✅ Manage Menu

### 🏍️ Delivery Partner
- ✅ Register with Documents (License, RC, Aadhar)
- ✅ Login & Dashboard
- ✅ Accept Delivery Orders
- ✅ Real-time Location Tracking
- ✅ View Earnings
- ✅ Delivery History

### 🛡️ Admin
- ✅ Login to Dashboard
- ✅ Manage Customers
- ✅ Manage Restaurants
- ✅ Manage Delivery Partners
- ✅ Approve/Reject Registrations
- ✅ View Analytics & Reports
- ✅ Block/Unblock Users

---

## 🔐 Authentication & Security

- **JWT Tokens**: Secure API endpoints with JSON Web Tokens
- **Password Encryption**: BCrypt hashing for passwords
- **CORS**: Configured for frontend integration
- **Role-Based Authorization**: Different access levels for roles

---

## 📡 API Endpoints

### Auth
```
POST /api/auth/login
POST /api/auth/register
```

### Customers
```
GET /api/customers
GET /api/customers/{id}
POST /api/customers
PUT /api/customers/{id}
```

### Restaurants
```
GET /api/restaurants
GET /api/restaurants/{id}
POST /api/restaurants
PUT /api/restaurants/{id}
```

### Foods
```
GET /api/foods
GET /api/foods/{id}
GET /api/foods/restaurant/{restaurantId}
POST /api/foods
PUT /api/foods/{id}
DELETE /api/foods/{id}
```

### Orders
```
GET /api/orders
GET /api/orders/{id}
GET /api/orders/customer/{customerId}
POST /api/orders
PUT /api/orders/{id}
```

---

## 🗄️ Database Schema

### Core Tables
- `users` - All users (customers, restaurants, delivery partners, admins)
- `customers` - Customer profiles
- `restaurants` - Restaurant details
- `delivery_partners` - Delivery partner information
- `foods` - Food/Menu items
- `orders` - Customer orders
- `order_items` - Items in each order
- `reviews` - Customer reviews
- `cart` - Shopping cart
- `addresses` - Customer delivery addresses

---

## 📦 Dependencies

### Backend
- Spring Boot 3.2.0
- Spring Data JPA
- Spring Security
- JWT (jsonwebtoken)
- MySQL Connector
- Lombok
- ModelMapper

### Frontend
- Leaflet (Maps)
- Chart.js (Analytics)
- Font Awesome (Icons)
- Animate.css (Animations)
- QR Code JS

---

## 🧪 Testing

### Demo Credentials
```
Admin:
  Email: admin@madfoods.com
  Password: admin123

Customer:
  Email: customer1@madfoods.com
  Password: customer123

Restaurant:
  Email: restaurant1@madfoods.com
  Password: restaurant123

Delivery Partner:
  Email: delivery1@madfoods.com
  Password: delivery123
```

---

## 🐛 Troubleshooting

### Backend Won't Start
```bash
# Check if port 8080 is in use
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Database Connection Error
```bash
# Verify MySQL is running
mysql -u root -p -e "SELECT 1;"

# Check credentials in application.properties
```

### CORS Issues
Ensure `api.js` has correct `API_BASE_URL` and backend CORS is configured.

---

## 📝 Future Enhancements

- [ ] Payment Gateway Integration (Razorpay/Stripe)
- [ ] Real-time Order Tracking with WebSockets
- [ ] Push Notifications
- [ ] Multi-language Support
- [ ] Advanced Analytics
- [ ] Mobile App (React Native)
- [ ] Promotional Coupons
- [ ] Referral System

---

## 📄 License

This project is open source and available under the MIT License.

---

## 👨‍💻 Developer

**Thanvitha Somalaraju**
- GitHub: [@thanvithasomalaraju](https://github.com/thanvithasomalaraju)
- Email: thanvithasomalaraju2@gmail.com

---

## 🙏 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

---

**Made with ❤️ for the Food Delivery Industry**
