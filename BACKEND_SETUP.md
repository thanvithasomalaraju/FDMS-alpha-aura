# FDMS Backend - Quick Start Guide

## Prerequisites
- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.8 or higher
- Git

## Step 1: Clone Repository
```bash
git clone https://github.com/thanvithasomalaraju/FDMS-alpha-aura.git
cd FDMS-alpha-aura
```

## Step 2: Create MySQL Database
```bash
# Login to MySQL
mysql -u root -p

# Run schema script
source database/schema.sql;

# Load sample data
source database/sample_data.sql;

# Verify
USE mad_food_db;
SHOW TABLES;
```

## Step 3: Configure Application
Edit `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/mad_food_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

# JWT
jwt.secret=MadFoodDeliverySystemSecretKeyFor2024VerificationAndSecurityPurpose
jwt.expiration=604800000
```

## Step 4: Build Project
```bash
mvn clean install
```

## Step 5: Run Backend
```bash
mvn spring-boot:run
```

✅ Backend will start at: `http://localhost:8080/api`

## Step 6: Test API

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "customer1@madfoods.com", "password": "customer123", "role": "CUSTOMER"}'
```

### Get All Restaurants
```bash
curl http://localhost:8080/api/restaurants
```

### Get Foods by Restaurant
```bash
curl http://localhost:8080/api/foods/restaurant/1
```

## Common Issues

### Port 8080 Already in Use
```bash
# Find process
lsof -i :8080

# Kill process
kill -9 <PID>
```

### MySQL Connection Error
```bash
# Check MySQL is running
service mysql status

# Start MySQL
service mysql start
```

### Database Not Created
```bash
# Verify database exists
mysql -u root -p -e "SHOW DATABASES;"

# Re-create if needed
mysql -u root -p < database/schema.sql
```

## Next Steps

1. Open frontend files in browser or Live Server
2. Login with demo credentials
3. Test all features
4. Customize as needed

## Documentation
- See README.md for full project documentation
- See API endpoints in README.md
