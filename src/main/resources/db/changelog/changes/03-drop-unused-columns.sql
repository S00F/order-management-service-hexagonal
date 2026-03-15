--liquibase formatted sql
--changeset trae:3

ALTER TABLE orders DROP COLUMN order_number;
ALTER TABLE order_items DROP COLUMN item_number;
