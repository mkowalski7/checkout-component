CREATE TABLE products (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
);

CREATE TABLE checkout_sessions (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    payment_status VARCHAR(20) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    total_discount DECIMAL(10, 2) NOT NULL,
    final_amount DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE checkout_products (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    checkout_session_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(10, 2) NOT NULL,
    final_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (checkout_session_id) REFERENCES checkout_sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE promotions (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY
);

CREATE TABLE promotion_products (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    promotion_id UUID NOT NULL,
    product_id UUID NOT NULL,
    required_quantity INT NOT NULL,
    discount_amount DECIMAL(10, 2) NOT NULL
);