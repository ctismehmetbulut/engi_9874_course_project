# ENGI 9874: Course Project - Merchant Customer Management System

## Overview
A Java Swing desktop application demonstrating enterprise software architecture patterns including MVC, Observer, DAO, and Singleton patterns with SQLite database integration.

## What This Application Achieves

### Functional Requirements
**Customer Management:**
- User registration with secure 4-digit PIN authentication
- Balance tracking and transaction history
- Purchase products from available inventory
- Participate in deposit bonus campaigns

**Merchant Operations:**
- Admin authentication (hardcoded: username="admin", password="password")
- Create and manage promotional campaigns (deposit + bonus)
- Add products with name, price, and stock quantity
- Real-time inventory management (+1/-1 stock operations)
- Remove campaigns (products auto-deleted when stock reaches 0)

**Transaction Processing:**
- Secure purchase transactions with balance validation
- Automatic stock deduction on successful purchases
- Campaign-based deposits with bonus calculations
- Complete transaction audit trail

### Non-Functional Requirements
**Performance:**
- In-memory caching for fast data access
- Concurrent data structures for thread safety
- Efficient database operations with prepared statements

**Reliability:**
- Transaction integrity with proper error handling
- Database persistence ensures data durability
- Input validation prevents invalid data entry

**Usability:**
- Intuitive GUI with clear navigation
- Real-time updates across all windows
- User-friendly error messages and confirmations

**Maintainability:**
- Clean architecture with separation of concerns
- Modular design allows easy feature additions
- Design patterns ensure code extensibility

### Use Cases
**Customer Use Cases:**
1. **Register/Login:** New customers sign up, existing customers authenticate
2. **View Balance:** Check current account balance and transaction history
3. **Make Deposit:** Participate in campaigns to deposit money and receive bonuses
4. **Purchase Products:** Browse available products and make purchases
5. **View History:** Access complete transaction history (deposits and purchases)

**Merchant Use Cases:**
1. **Admin Login:** Authenticate with hardcoded credentials (admin/password)
2. **Manage Campaigns:** Create promotional campaigns with deposit/bonus amounts
3. **Manage Inventory:** Add new products and adjust stock levels (+1/-1)
4. **Monitor Operations:** View real-time updates of customer activities
5. **Remove Items:** Delete campaigns (products auto-removed when stock=0)

**System Use Cases:**
1. **Data Synchronization:** Automatic updates across all open windows
2. **Transaction Processing:** Validate balances, update inventory, record transactions
3. **Error Handling:** Graceful handling of invalid inputs and system errors

## Features
- **Multi-role Authentication** (Customer/Merchant)
- **Campaign Management** (Deposit bonus campaigns)
- **Product Inventory** (Stock management)
- **Transaction Processing** (Purchases, deposits)
- **Real-time UI Updates** (Observer pattern)
- **Transaction History** (Complete audit trail)

## Architecture
- **Model-View-Controller** pattern
- **Data Access Object** (DAO) pattern  
- **Observer** pattern for UI synchronization
- **Singleton** pattern for frame management
- **Repository** pattern with in-memory caching
- **Design by Contract** (DbC) for robust business logic

## Design Patterns Implemented
**1. Singleton Pattern:**
- `MerchantFrame.getInstance()` - Single merchant dashboard instance
- `CustomerFrame.getInstance(customerId)` - Customer-specific singleton instances
- `Database.getConnection()` - Single database connection management

**2. Observer Pattern:**
- `MemoryChangeNotifier` - Central notification hub
- `MemoryChangeListener` - Observer interface
- Real-time UI updates when data changes

**3. Data Access Object (DAO) Pattern:**
- `ProductDAO` - Product CRUD operations
- `CustomerDAO` - Customer authentication and management
- `CampaignDAO` - Campaign lifecycle management
- `TransactionDAO` - Purchase and deposit transactions

**4. Repository Pattern:**
- `MemoryStore` - In-memory caching with `ConcurrentHashMap`
- Synchronization between database and memory cache

**5. Command Pattern (Implicit):**
- GUI event handlers encapsulate user actions
- Action listeners trigger business operations

**6. Factory Pattern (Implicit):**
- DAO classes create model objects from database data

## Design by Contract Implementation
**Preconditions:** Input validation using assertions
- Product: `price > 0`, `stock >= 0`, non-empty names
- Customer: valid 4-digit PIN (`password.matches("\\d{4}")`), non-negative balance
- Campaign: positive deposit amounts, non-negative bonuses
- Transactions: positive amounts, valid customer IDs

**Postconditions:** State verification after operations
- Constructor postconditions verify object state initialization
- Setter postconditions confirm value updates
- Transaction postconditions ensure data consistency

**Class Invariants:** Maintain object integrity
- Products maintain valid pricing and stock levels
- Customers maintain valid balances and credentials
- Campaigns maintain valid deposit/bonus relationships

**Example Contract (Product.setStock):**
```java
/**
 * @requires stock >= 0
 * @ensures getStock() == stock
 */
public void setStock(int stock) {
    assert stock >= 0 : "Stock cannot be negative";
    this.stock = stock;
    assert this.stock == stock : "Stock not updated correctly";
}
```

## Technologies
- **Java Swing** (GUI framework)
- **SQLite** (Embedded database)
- **JDBC** (Database connectivity)
- **Java Assertions** (Design by Contract implementation)
- **ConcurrentHashMap & CopyOnWriteArrayList** (Thread-safe collections)
- **SwingUtilities.invokeLater()** (EDT thread safety)

## Project Structure
```
src/
├── Main.java                 # Application entry point
├── MainAppFrame.java         # Main window with CardLayout
├── MainMenuPanel.java        # Login selection screen
├── AuthPanel.java            # Authentication for both roles
├── MerchantFrame.java        # Merchant dashboard (Singleton)
├── CustomerFrame.java        # Customer dashboard (Singleton)
├── HistoryFrame.java         # Transaction history view
├── model/
│   ├── Product.java          # Product entity with DbC
│   ├── Customer.java         # Customer entity with DbC  
│   ├── Campaign.java         # Campaign entity with DbC
│   ├── Purchase.java         # Purchase transaction record
│   └── Deposit.java          # Deposit transaction record
├── dao/
│   ├── Database.java         # SQLite connection management
│   ├── MemoryStore.java      # In-memory cache (Repository pattern)
│   ├── ProductDAO.java       # Product data access
│   ├── CustomerDAO.java      # Customer data access
│   ├── CampaignDAO.java      # Campaign data access
│   ├── TransactionDAO.java   # Transaction processing
│   └── PurchaseResult.java   # Transaction result enumeration
└── observer/
    ├── MemoryChangeListener.java    # Observer interface
    └── MemoryChangeNotifier.java    # Subject (Observable)
```

## How to Run
```bash
# Compile and run with assertions enabled
make all

# Or manually:
javac -cp ".;sqlite-jdbc-3.8.9.1.jar" Main.java
java -ea -cp ".;sqlite-jdbc-3.8.9.1.jar" Main
```

## Database Schema
**Tables:**
- **customers** (id, name, password, balance)
- **products** (id, name, price, stock) 
- **campaigns** (id, deposit_amount, bonus_amount)
- **purchases** (id, customer_id, product_name, product_price, purchase_date)
- **deposits** (id, customer_id, total_deposit, deposit_date)

**Constraints:**
- Foreign key relationships (purchases/deposits → customers)
- Check constraints (price > 0, stock >= 0, password GLOB '[0-9][0-9][0-9][0-9]')
- Auto-increment primary keys

## Key Implementation Details
**Authentication:**
- Customer: Username + 4-digit PIN validation
- Merchant: Hardcoded credentials (admin/password)

**Transaction Flow:**
- Campaign Deposit: Customer gets deposit_amount + bonus_amount
- Product Purchase: Balance validation → Stock check → Deduct balance → Update stock
- Stock Management: +1/-1 operations, auto-delete when stock=0

**Real-time Updates:**
- Observer pattern ensures all open windows refresh automatically
- Memory cache synchronization with database
- Thread-safe UI updates via SwingUtilities.invokeLater()

## Business Logic Validation
**Purchase Validation Chain:**
1. Customer authentication required
2. Product must exist and have stock > 0
3. Customer balance must be >= product price
4. Transaction atomicity (balance deduction + stock update + audit trail)

**Campaign Participation:**
- Customer receives deposit_amount + bonus_amount in single transaction
- All deposits recorded with timestamp for audit trail

**Input Validation:**
- 4-digit PIN enforcement via regex `\\d{4}`
- Price/stock positivity constraints
- Username uniqueness verification
- Form validation with user-friendly error messages
