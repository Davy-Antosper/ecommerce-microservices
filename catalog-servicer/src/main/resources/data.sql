INSERT INTO products (product_id, name, description, price, image_url, category, stock_quantity, available, created_at, updated_at)
VALUES
    ('PROD-001', 'iPhone 15 Pro', 'Latest Apple smartphone', 999.99, 'https://example.com/iphone15.jpg', 'Electronics', 50, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PROD-002', 'MacBook Pro 16"', 'Powerful laptop ', 2499.99, 'https://example.com/macbook.jpg', 'Electronics', 25, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PROD-003', 'AirPods Pro', 'Airpods Pro', 249.99, 'https://example.com/airpods.jpg', 'Electronics', 100, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PROD-004', 'Nike Air Max', 'Comfortable running shoes', 129.99, 'https://example.com/nike.jpg', 'Shoes', 75, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PROD-005', 'Sony WH-1000XM5', 'Premium  headphones', 399.99, 'https://example.com/sony.jpg', 'Electronics', 40, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PROD-006', 'iPad Air', ' tablet', 599.99, 'https://example.com/ipad.jpg', 'Electronics', 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PROD-007', 'Samsung Galaxy S24', 'Android ', 799.99, 'https://example.com/samsung.jpg', 'Electronics', 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);