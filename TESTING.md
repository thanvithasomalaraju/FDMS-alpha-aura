# Testing Guide for FDMS

## Unit Testing

### Service Layer Tests
```bash
mvn test
```

## Integration Testing

### 1. Customer Flow
```
1. Register as customer
   - Valid email & password
   - Verify profile created

2. Browse restaurants
   - Load restaurant list
   - View restaurant details

3. Place order
   - Add items to cart
   - Apply location
   - Confirm payment
   - Verify order created

4. Track order
   - View order status
   - See delivery partner
   - Confirm delivery
```

### 2. Restaurant Flow
```
1. Register restaurant
   - Upload logo/banner
   - Set cuisine type
   - Verify approval pending

2. Add menu items
   - Create food item
   - Set price & description
   - Upload image
   - Verify in menu

3. Manage orders
   - View incoming orders
   - Accept order
   - Update status to "Preparing"
   - Change to "Ready"
```

### 3. Admin Flow
```
1. Login as admin

2. Approve restaurant
   - Find pending restaurant
   - Approve registration
   - Verify status changed

3. Manage users
   - View all users
   - Block/Unblock user
   - Delete user if needed

4. View analytics
   - Total users
   - Total restaurants
   - Total orders
   - Total revenue
```

## API Testing with Postman

### 1. Import Collection
- Create new Postman collection
- Add requests for each endpoint

### 2. Test Authentication
```
POST /api/auth/login
Body: {
  "email": "customer1@madfoods.com",
  "password": "customer123",
  "role": "CUSTOMER"
}

Expected: 200 OK with token
```

### 3. Test Protected Endpoints
```
GET /api/customers
Header: Authorization: Bearer <TOKEN>

Expected: 200 OK with customer list
```

## Load Testing

### Using Apache JMeter
1. Create test plan
2. Add HTTP requests
3. Set thread group to simulate users
4. Run test and analyze results

## Performance Testing

### Database Queries
```sql
-- Check slow queries
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- View slow query log
TAIL -f /var/log/mysql/slow-query.log
```

## Security Testing

### 1. SQL Injection
- Test with: `' OR '1'='1`
- Should return error, not data

### 2. XSS (Cross-Site Scripting)
- Try: `<script>alert('test')</script>` in input
- Should be escaped/sanitized

### 3. JWT Token Validation
- Modify token and test
- Should reject invalid token

## End-to-End Testing

### Scenario: Complete Order
1. Customer registers
2. Customer logs in
3. Customer browses restaurants
4. Customer views menu
5. Customer adds items to cart
6. Customer places order
7. Restaurant receives order
8. Restaurant accepts order
9. Customer receives notification
10. Order status updates in real-time
11. Delivery partner accepts
12. Order marked delivered
13. Customer receives confirmation

## Automated Testing with Selenium

```javascript
// Example Selenium test
const { Builder, By, until } = require('selenium-webdriver');

let driver = new Builder()
    .forBrowser('chrome')
    .build();

async function testCustomerLogin() {
    await driver.get('http://localhost:8000/customer/newone.html');
    await driver.findElement(By.id('loginEmail')).sendKeys('customer1@madfoods.com');
    await driver.findElement(By.id('loginPassword')).sendKeys('customer123');
    await driver.findElement(By.id('loginBtn')).click();
    
    await driver.wait(until.urlContains('dashboard'), 5000);
    console.log('Login test passed!');
}

testCustomerLogin().finally(() => driver.quit());
```

## Test Results Tracking

| Test Case | Status | Date | Notes |
|-----------|--------|------|-------|
| Customer Login | ✅ | 2024-07-08 | Working |
| Register Customer | ✅ | 2024-07-08 | Working |
| Place Order | ⏳ | - | In Progress |
| Track Order | ❌ | - | Need Fix |

## Regression Testing

After each update:
1. Run full test suite
2. Compare with baseline
3. Document differences
4. Update tests if needed

## CI/CD Pipeline

```yaml
# GitHub Actions workflow
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: mvn test
```
