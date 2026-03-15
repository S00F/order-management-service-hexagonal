--liquibase formatted sql
--changeset trae:2

INSERT INTO orders (id, order_number, customer_id, total_amount, status, created_at, updated_at)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'ORD-2023-001', 'CUST001', 299.98, 'CREATED', '2023-11-14 10:00:00', '2023-11-14 10:00:00');

INSERT INTO order_items (id, item_number, order_id, product_id, quantity, unit_price, subtotal)
VALUES ('b2c3d4e5-f6a7-8901-bcde-f12345678901', 'ITEM-001-001', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'PROD001', 2, 149.99, 299.98);

INSERT INTO orders (id, order_number, customer_id, total_amount, status, created_at, updated_at)
VALUES ('c3d4e5f6-a7b8-9012-cdef-123456789012', 'ORD-2023-002', 'CUST002', 499.95, 'CONFIRMED', '2023-11-14 11:00:00', '2023-11-14 11:30:00');

INSERT INTO order_items (id, item_number, order_id, product_id, quantity, unit_price, subtotal)
VALUES ('d4e5f6a7-b8c9-0123-defa-234567890123', 'ITEM-002-001', 'c3d4e5f6-a7b8-9012-cdef-123456789012', 'PROD002', 3, 166.65, 499.95);