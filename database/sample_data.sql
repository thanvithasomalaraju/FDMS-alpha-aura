-- ===== SAMPLE DATA FOR TESTING =====
USE mad_food_db;

-- Insert Admin User
INSERT INTO users (email, password, full_name, phone, role, is_verified, is_active) 
VALUES ('admin@madfoods.com', '$2a$10$admin123hashed', 'Admin User', '9999999999', 'ADMIN', TRUE, TRUE);

-- Insert Sample Customers
INSERT INTO users (email, password, full_name, phone, role, is_verified, is_active) 
VALUES 
('customer1@madfoods.com', '$2a$10$customer123hashed', 'Rahul Kumar', '9876543210', 'CUSTOMER', TRUE, TRUE),
('customer2@madfoods.com', '$2a$10$customer123hashed', 'Priya Singh', '9876543211', 'CUSTOMER', TRUE, TRUE);

-- Insert Customer Records
INSERT INTO customers (user_id, diet_preference, total_orders, total_spent) 
VALUES 
(2, 'both', 5, 2500),
(3, 'veg', 3, 1200);

-- Insert Sample Restaurants
INSERT INTO users (email, password, full_name, phone, role, is_verified, is_active) 
VALUES 
('restaurant1@madfoods.com', '$2a$10$restaurant123hashed', 'Restaurant Admin', '9111111111', 'RESTAURANT', TRUE, TRUE),
('restaurant2@madfoods.com', '$2a$10$restaurant123hashed', 'Restaurant Admin 2', '9222222222', 'RESTAURANT', TRUE, TRUE);

INSERT INTO restaurants (user_id, restaurant_name, cuisine_type, description, address, is_approved, is_open) 
VALUES 
(4, 'Taj Express', 'Indian', 'Authentic Indian cuisine', 'Mumbai, India', TRUE, TRUE),
(5, 'Green Leaf Cafe', 'Vegetarian', 'Pure vegetarian restaurant', 'Bangalore, India', TRUE, TRUE);

-- Insert Sample Foods
INSERT INTO foods (restaurant_id, food_name, description, price, category, diet_type, preparation_time) 
VALUES 
(1, 'Butter Chicken', 'Creamy butter chicken with rice', 250, 'Main Course', 'nonveg', 20),
(1, 'Paneer Tikka', 'Grilled paneer with spices', 180, 'Starter', 'veg', 15),
(2, 'Masala Dosa', 'Crispy dosa with sambar', 120, 'Main Course', 'veg', 10),
(2, 'Idli', 'Steamed rice cakes', 80, 'Main Course', 'veg', 8);

-- Insert Sample Delivery Partners
INSERT INTO users (email, password, full_name, phone, role, is_verified, is_active) 
VALUES 
('delivery1@madfoods.com', '$2a$10$delivery123hashed', 'Delivery Partner 1', '9333333333', 'DELIVERY_PARTNER', TRUE, TRUE),
('delivery2@madfoods.com', '$2a$10$delivery123hashed', 'Delivery Partner 2', '9444444444', 'DELIVERY_PARTNER', TRUE, TRUE);

INSERT INTO delivery_partners (user_id, license_number, rc_number, aadhar_number, is_approved, is_active, total_deliveries) 
VALUES 
(6, 'DL001', 'RC001', 'AADHAR001', TRUE, TRUE, 50),
(7, 'DL002', 'RC002', 'AADHAR002', TRUE, TRUE, 30);
