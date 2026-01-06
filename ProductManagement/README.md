# Product Management & Order Processing System

A comprehensive Spring Boot application for managing products, orders, and user roles with role-based access control.

## Features

- **Role-Based Access Control**: USER, ADMIN, SUPER_ADMIN roles with different permissions
- **Product Management**: CRUD operations for products with inventory management
- **Shopping Cart**: Add, update, remove items with inventory validation
- **Order Processing**: Place orders with automatic inventory reduction
- **Category Management**: Organize products by categories
- **Address Management**: Multiple addresses per user
- **HTTP Basic Authentication**: Secure basic authentication

## Tech Stack

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Hibernate**
- **MySQL**
- **Spring Security**
- **HTTP Basic Authentication**
- **Maven**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Database Schema

### Tables

1. **users**
   - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
   - `username` (VARCHAR, UNIQUE, NOT NULL)
   - `password` (VARCHAR, NOT NULL)
   - `email` (VARCHAR, UNIQUE, NOT NULL)
   - `first_name` (VARCHAR, NOT NULL)
   - `last_name` (VARCHAR, NOT NULL)
   - `enabled` (BOOLEAN, DEFAULT TRUE)

2. **roles**
   - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
   - `name` (ENUM: USER, ADMIN, SUPER_ADMIN, UNIQUE, NOT NULL)

3. **user_roles** (Join Table)
   - `user_id` (BIGINT, FOREIGN KEY -> users.id)
   - `role_id` (BIGINT, FOREIGN KEY -> roles.id)

4. **categories**
   - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
   - `name` (VARCHAR, UNIQUE, NOT NULL)
   - `description` (VARCHAR)

5. **products**
   - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
   - `name` (VARCHAR, NOT NULL)
   - `description` (VARCHAR)
   - `price` (DECIMAL(10,2), NOT NULL)
   - `enabled` (BOOLEAN, DEFAULT TRUE)
   - `category_id` (BIGINT, FOREIGN KEY -> categories.id)

6. **inventory**
   - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
   - `product_id` (BIGINT, FOREIGN KEY -> products.id, UNIQUE)
   - `quantity` (INTEGER, NOT NULL, DEFAULT 0)

7. **cart**
   - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
   - `user_id` (BIGINT, FOREIGN KEY -> users.id, UNIQUE)

8. **cart_items**
   - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
   - `cart_id` (BIGINT, FOREIGN KEY -> cart.id)
   - `product_id` (BIGINT, FOREIGN KEY -> products.id)
   - `quantity` (INTEGER, NOT NULL)

9. **addresses**
   - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
   - `user_id` (BIGINT, FOREIGN KEY -> users.id)
   - `street` (VARCHAR, NOT NULL)
   - `city` (VARCHAR, NOT NULL)
   - `state` (VARCHAR, NOT NULL)
   - `zip_code` (VARCHAR, NOT NULL)
   - `country` (VARCHAR, NOT NULL)

10. **orders**
    - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
    - `user_id` (BIGINT, FOREIGN KEY -> users.id)
    - `address_id` (BIGINT, FOREIGN KEY -> addresses.id)
    - `total_amount` (DECIMAL(10,2), NOT NULL)
    - `status` (ENUM: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED)
    - `order_date` (DATETIME, NOT NULL)

11. **order_items**
    - `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT)
    - `order_id` (BIGINT, FOREIGN KEY -> orders.id)
    - `product_id` (BIGINT, FOREIGN KEY -> products.id)
    - `quantity` (INTEGER, NOT NULL)
    - `price` (DECIMAL(10,2), NOT NULL)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ProductManagement
```

### 2. Configure Database

Update `src/main/resources/application.properties` with your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/product_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Database Initialization

The application automatically creates the database schema and initializes the roles (USER, ADMIN, SUPER_ADMIN) on first startup.

## API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication

All endpoints except `/api/auth/**` require HTTP Basic Authentication. Include credentials in the Authorization header:
```
Authorization: Basic <base64(username:password)>
```

For example, if username is `john_doe` and password is `password123`, the header would be:
```
Authorization: Basic am9obl9kb2U6cGFzc3dvcmQxMjM=
```

Most HTTP clients (Postman, curl, etc.) will automatically encode the credentials when you select "Basic Auth" authentication type.

---

## Authentication Endpoints

### 1. Register User
**POST** `/api/auth/register`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "enabled": true,
      "roles": ["USER"]
    },
    "message": "User registered successfully"
  }
}
```

### 2. Get Current User
**GET** `/api/auth/me`

**Authentication:** Required (HTTP Basic Auth)

**Response:**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "enabled": true,
    "roles": ["USER"]
  }
}
```

**Note:** With HTTP Basic Authentication, you don't need a separate login endpoint. Authentication happens automatically when you include credentials in the Authorization header for any protected endpoint.

---

## Product Endpoints

### 1. Get All Products (Public)
**GET** `/api/products`

**Response:**
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Laptop",
      "description": "High-performance laptop",
      "price": 999.99,
      "enabled": true,
      "categoryId": 1,
      "categoryName": "Electronics",
      "inventoryQuantity": 50
    }
  ]
}
```

### 2. Get Products by Category (Public)
**GET** `/api/products/category/{categoryId}`

### 3. Get Product by ID (Public)
**GET** `/api/products/{id}`

### 4. Create Product (ADMIN, SUPER_ADMIN)
**POST** `/api/products`

**Request Body:**
```json
{
  "name": "Smartphone",
  "description": "Latest smartphone model",
  "price": 699.99,
  "categoryId": 1,
  "quantity": 100
}
```

### 5. Update Product (ADMIN, SUPER_ADMIN)
**PUT** `/api/products/{id}`

**Request Body:**
```json
{
  "name": "Updated Smartphone",
  "price": 649.99,
  "enabled": true,
  "quantity": 150
}
```

### 6. Delete Product (ADMIN, SUPER_ADMIN)
**DELETE** `/api/products/{id}`

---

## Category Endpoints

### 1. Get All Categories (Public)
**GET** `/api/categories`

### 2. Get Category by ID (Public)
**GET** `/api/categories/{id}`

### 3. Create Category (SUPER_ADMIN)
**POST** `/api/categories`

**Request Body:**
```json
{
  "name": "Electronics",
  "description": "Electronic devices and gadgets"
}
```

### 4. Update Category (SUPER_ADMIN)
**PUT** `/api/categories/{id}`

### 5. Delete Category (SUPER_ADMIN)
**DELETE** `/api/categories/{id}`

---

## Cart Endpoints (USER only)

### 1. Get Cart
**GET** `/api/cart`

**Response:**
```json
{
  "success": true,
  "message": "Cart retrieved successfully",
  "data": {
    "cartId": 1,
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Laptop",
        "price": 999.99,
        "quantity": 2,
        "subtotal": 1999.98
      }
    ],
    "totalAmount": 1999.98
  }
}
```

### 2. Add Item to Cart
**POST** `/api/cart/items`

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

### 3. Update Cart Item
**PUT** `/api/cart/items/{cartItemId}?quantity=3`

### 4. Remove Cart Item
**DELETE** `/api/cart/items/{cartItemId}`

### 5. Clear Cart
**DELETE** `/api/cart`

---

## Address Endpoints (USER only)

### 1. Add Address
**POST** `/api/addresses`

**Request Body:**
```json
{
  "street": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "country": "USA"
}
```

### 2. Get User Addresses
**GET** `/api/addresses`

### 3. Get Address by ID
**GET** `/api/addresses/{id}`

### 4. Update Address
**PUT** `/api/addresses/{id}`

### 5. Delete Address
**DELETE** `/api/addresses/{id}`

---

## Order Endpoints

### 1. Place Order (USER)
**POST** `/api/orders/checkout`

**Request Body:**
```json
{
  "addressId": 1
}
```

**Response:**
```json
{
  "success": true,
  "message": "Order placed successfully",
  "data": {
    "id": 1,
    "userId": 1,
    "username": "john_doe",
    "address": {
      "id": 1,
      "street": "123 Main St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA"
    },
    "totalAmount": 1999.98,
    "status": "CONFIRMED",
    "orderDate": "2024-01-15T10:30:00",
    "items": [
      {
        "id": 1,
        "productId": 1,
        "productName": "Laptop",
        "quantity": 2,
        "price": 999.99,
        "subtotal": 1999.98
      }
    ]
  }
}
```

### 2. Get User Orders (USER)
**GET** `/api/orders`

### 3. Get Order by ID (USER)
**GET** `/api/orders/{id}`

### 4. Get All Orders (SUPER_ADMIN)
**GET** `/api/orders/all`

---

## User Management Endpoints (SUPER_ADMIN only)

### 1. Get All Users
**GET** `/api/users`

### 2. Get User by ID
**GET** `/api/users/{id}`

### 3. Update User Role
**PUT** `/api/users/{id}/role`

**Request Body:**
```json
{
  "role": "ADMIN"
}
```

**Valid roles:** `USER`, `ADMIN`, `SUPER_ADMIN`

### 4. Delete User
**DELETE** `/api/users/{id}`

---

## Role Permissions

### USER
- View products by category
- Add/update/remove cart items
- Add/update/delete addresses
- Place orders (checkout)
- View own orders

### ADMIN
- All USER permissions
- Add/update/delete products
- Manage product price and inventory
- Enable/disable products
- Assign categories to products

### SUPER_ADMIN
- All ADMIN permissions
- Manage users and roles
- Create/update/delete categories
- View all orders

---

## Inventory Rules

1. **Inventory Reduction**: Inventory is automatically reduced when an order is successfully placed
2. **Checkout Validation**: Cart is validated before order placement - checks for:
   - Cart is not empty
   - All products are enabled
   - Sufficient inventory for all items
3. **Cart Validation**: Cart items are validated when:
   - Adding items to cart
   - Updating cart items
   - Before checkout

---

## Error Handling

The application uses a global exception handler that returns consistent error responses:

```json
{
  "success": false,
  "message": "Error message",
  "data": null
}
```

**Common HTTP Status Codes:**
- `200 OK`: Success
- `201 Created`: Resource created successfully
- `400 Bad Request`: Validation error or invalid operation
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `409 Conflict`: Duplicate resource
- `500 Internal Server Error`: Server error

---

## Sample API Requests

### Complete Flow Example

1. **Register a user:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "password123",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

2. **Get products (public endpoint):**
```bash
curl -X GET http://localhost:8080/api/products
```

3. **Add item to cart (with Basic Auth):**
```bash
curl -X POST http://localhost:8080/api/cart/items \
  -H "Content-Type: application/json" \
  -u john_doe:password123 \
  -d '{
    "productId": 1,
    "quantity": 2
  }'
```

4. **Add address (with Basic Auth):**
```bash
curl -X POST http://localhost:8080/api/addresses \
  -H "Content-Type: application/json" \
  -u john_doe:password123 \
  -d '{
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001",
    "country": "USA"
  }'
```

5. **Place order (with Basic Auth):**
```bash
curl -X POST http://localhost:8080/api/orders/checkout \
  -H "Content-Type: application/json" \
  -u john_doe:password123 \
  -d '{
    "addressId": 1
  }'
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/productmanagement/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── exception/      # Custom exceptions
│   │   ├── repository/     # JPA repositories
│   │   ├── service/        # Business logic
│   │   └── util/           # Utility classes
│   └── resources/
│       └── application.properties
└── pom.xml
```

---

## Notes

- The application uses HTTP Basic Authentication for secure access
- Database schema is automatically created/updated on startup (Hibernate DDL auto)
- Roles are automatically initialized on first startup
- All passwords are encrypted using BCrypt
- Inventory validation happens at multiple stages to ensure data integrity
- For Basic Auth, credentials are sent with each request in the Authorization header

---

## License

This project is for educational purposes.

