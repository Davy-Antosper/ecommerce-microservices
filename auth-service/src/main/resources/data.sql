-- Insert test users (password: password123)
INSERT INTO users (username, email, password, role, enabled, created_at, updated_at)
VALUES
    ('admin', 'admin@example.com', '$2a$10$xWKPPiGPeNGGU3u1xjELLeqj6L8WM3ZQdtZZQfFPH/U8O8K6JdvQa', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('user1', 'user1@example.com', '$2a$10$xWKPPiGPeNGGU3u1xjELLeqj6L8WM3ZQdtZZQfFPH/U8O8K6JdvQa', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);