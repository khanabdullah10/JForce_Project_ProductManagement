# Super Admin API Documentation

**Flow:** Login -> Create Categories -> Manage Users -> View System Orders.

## 1. Authentication

### Login
**GET** `http://localhost:8080/api/auth/me`
**Headers:**
- `Authorization`: `Basic c3VwZXJhZG1pbjpzdXBlcmFkbWluMTIz`
  *(Base64 of `superadmin:superadmin123`)*

## 2. Category Management
*Categories are required before Admins can create products.*

### Create Category (Electronics)
**POST** `http://localhost:8080/api/categories`
**Body:**
```json
{
  "name": "Electronics",
  "description": "Devices and Gadgets"
}
```

### Create Category (Books)
**POST** `http://localhost:8080/api/categories`
**Body:**
```json
{
  "name": "Books",
  "description": "Paperback and Hardcover books"
}
```

### Update Category
**PUT** `http://localhost:8080/api/categories/{id}`
**Body:**
```json
{
  "name": "Consumer Electronics",
  "description": "Personal electronic devices"
}
```

### Delete Category
**DELETE** `http://localhost:8080/api/categories/{id}`

## 3. User Management

### Get All Users
**GET** `http://localhost:8080/api/users`

### Get User by ID
**GET** `http://localhost:8080/api/users/{id}`

### Promote User to Admin
**PUT** `http://localhost:8080/api/users/{id}/role`
**Body:**
```json
{
  "role": "ADMIN"
}
```

### Delete User
**DELETE** `http://localhost:8080/api/users/{id}`

## 4. System Management

### Get All Orders (System-wide)
**GET** `http://localhost:8080/api/orders/all`
