-- schema.sql
CREATE TABLE IF NOT EXISTS campaigns (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    deposit_amount REAL NOT NULL CHECK(deposit_amount > 0),
    bonus_amount REAL NOT NULL CHECK(bonus_amount >= 0)
);

CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    price REAL NOT NULL CHECK (price > 0),
    stock INTEGER NOT NULL DEFAULT 1 CHECK (stock >= 0)
);

CREATE TABLE IF NOT EXISTS customers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    password TEXT NOT NULL CHECK (LENGTH(password) = 4 AND password GLOB '[0-9][0-9][0-9][0-9]'),
    balance REAL NOT NULL DEFAULT 0.0
);

-- ✅ New: Purchases Table
CREATE TABLE IF NOT EXISTS purchases (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    product_name TEXT NOT NULL,
    product_price REAL NOT NULL CHECK(product_price >= 0),
    purchase_date TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- ✅ New: Deposits Table
CREATE TABLE IF NOT EXISTS deposits (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id INTEGER NOT NULL,
    total_deposit REAL NOT NULL CHECK(total_deposit >= 0),
    deposit_date TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);