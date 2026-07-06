# FDMS Backend API Quick Reference

## Base URL
```
http://localhost:8080/api
```

## Authentication
All protected endpoints require JWT token in header:
```
Authorization: Bearer <jwt_token>
```

## Quick API Reference

### Authentication (Public)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | /auth/register | Register new user | ❌ |
| POST | /auth/login | Login user | ❌ |
| GET | /auth/user/{id} | Get user profile | ✅ |
| PUT | /auth/user/{id} | Update user profile | ✅ |

### Restaurants
| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| POST | /restaurants | Create restaurant | ✅ | RESTAURANT_OWNER |
| GET | /restaurants | List all restaurants | ❌ | - |
| GET | /restaurants/{id} | Get restaurant | ❌ | - |
| GET | /restaurants/city/{city} | Filter by city | ❌ | - |
| GET | /restaurants/search | Search restaurants | ❌ | - |
| GET | /restaurants/cuisine/{type} | Filter by cuisine | ❌ | - |
| GET | /restaurants/owner/my-restaurant | Get my restaurant | ✅ | RESTAURANT_OWNER |
| PUT | /restaurants/{id} | Update restaurant | ✅ | RESTAURANT_OWNER |

### Menu
| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| POST | /menu?restaurantId={id} | Add menu item | ✅ | RESTAURANT_OWNER |
| GET | /menu/{id} | Get menu item | ❌ | - |
| GET | /menu/restaurant/{id} | Get menu | ❌ | - |
| GET | /menu/restaurant/{id}/all | Get all items | ❌ | - |
| GET | /menu/search | Search items | ❌ | - |
| PUT | /menu/{id} | Update item | ✅ | RESTAURANT_OWNER |
| DELETE | /menu/{id} | Delete item | ✅ | RESTAURANT_OWNER |

### Orders
| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| POST | /orders | Create order | ✅ | CUSTOMER |
| GET | /orders/{id} | Get order | ✅ | - |
| GET | /orders/number/{number} | Get by order number | ✅ | - |
| GET | /orders/my-orders | My orders | ✅ | CUSTOMER |
| GET | /orders/restaurant/{id} | Restaurant orders | ✅ | RESTAURANT_OWNER |
| GET | /orders/delivery-partner/my-orders | My deliveries | ✅ | DELIVERY_PARTNER |
| PUT | /orders/{id}/status | Update status | ✅ | RESTAURANT_OWNER, DELIVERY_PARTNER |
| PUT | /orders/{id}/assign-delivery | Assign delivery | ✅ | ADMIN |
| PUT | /orders/{id}/cancel | Cancel order | ✅ | CUSTOMER |

### Payments
| Method | Endpoint | Description | Auth | Role |
|--------|----------|-------------|------|------|
| POST | /payments/process | Process payment | ✅ | CUSTOMER |
| GET | /payments/order/{id} | Get by order | ✅ | - |
| GET | /payments/{id} | Get payment | ✅ | - |
| PUT | /payments/{id}/refund | Refund payment | ✅ | ADMIN |

### Health
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | /health | Server status | ❌ |

## User Roles
```
ADMIN - Full system access
CUSTOMER - Place orders, pay
RESTAURANT_OWNER - Manage restaurant & menu
DELIVERY_PARTNER - Deliver orders
```

## Order Status Flow
```
PENDING → CONFIRMED → PREPARING → READY_FOR_PICKUP → OUT_FOR_DELIVERY → DELIVERED
         ↓
      CANCELLED/FAILED
```

## Example Requests

### Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "9876543210",
    "role": "CUSTOMER"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Get Restaurants
```bash
curl http://localhost:8080/api/restaurants
```

### Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": 1,
    "deliveryAddress": "123 Main St",
    "deliveryCity": "NYC",
    "deliveryPostalCode": "10001",
    "orderItems": [
      {
        "menuItemId": 1,
        "quantity": 2
      }
    ]
  }'
```

## Response Format

### Success (200)
```json
{
  "id": 1,
  "name": "Pizza Palace",
  "email": "pizzapalace@example.com"
}
```

### Error (400/401/500)
```json
{
  "timestamp": "2024-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already in use",
  "path": "/api/auth/register"
}
```
