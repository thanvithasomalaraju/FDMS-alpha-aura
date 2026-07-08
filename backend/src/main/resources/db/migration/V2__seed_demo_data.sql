-- V2__seed_demo_data.sql
-- Note: Replace the password_hash values with bcrypt hashes generated on the server or via tool.
INSERT INTO users (name, email, password_hash, phone, role, status)
VALUES ('Admin','admin@madfoods.com','$2a$10$REPLACE_WITH_BCRYPT_HASH','+911234567890','ADMIN','ACTIVE');

-- Demo restaurant user
INSERT INTO users (name, email, password_hash, phone, role, status)
VALUES ('Demo Restaurant Owner', 'owner@demo.com', '$2a$10$REPLACE_WITH_BCRYPT_HASH', '+919900112233', 'RESTAURANT','ACTIVE');

-- create restaurant (assumes user id 2)
INSERT INTO restaurants (user_id, name, description, address, phone, earnings)
VALUES (2, 'Demo Burger', 'Tasty burgers and sides', '42 Food St, City', '+919900112233', 0);

-- sample foods (replace restaurant id as appropriate)
INSERT INTO foods (restaurant_id, name, description, price, image_path, is_veg, available)
VALUES (1, 'Classic Burger', 'Beef patty, lettuce, tomato', 199.00, '/uploads/foods/burger1.jpg', false, true),
       (1, 'Veggie Wrap', 'Seasonal veggies and sauce', 129.00, '/uploads/foods/wrap1.jpg', true, true);
