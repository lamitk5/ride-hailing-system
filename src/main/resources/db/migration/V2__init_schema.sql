CREATE TABLE IF NOT EXISTS vehicle_types (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    base_fare DECIMAL(10,2) NOT NULL,
    price_per_km DECIMAL(10,2) NOT NULL
);

-- Khuyến mãi thêm dữ liệu giả để test
INSERT INTO vehicle_types (id, name, base_fare, price_per_km, is_active, created_at, updated_at) 
VALUES ('33333333-1111-1111-1111-111111111111', 'GrabBike', 15000, 5000, true, NOW(), NOW()) ON CONFLICT DO NOTHING;
