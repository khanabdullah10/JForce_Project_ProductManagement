# Admin API Documentation

**Flow:** Login -> Create Products -> Manage Products.
**Prerequisite:** Categories must exist (created by Super Admin).

## 1. Authentication

### Login
**GET** `http://localhost:8080/api/auth/me`
**Headers:**
- `Authorization`: `Basic YWRtaW46YWRtaW4xMjM=`
  *(Base64 of `admin:admin123`)*

## 2. Product Management

### Create Product (Laptop)
**POST** `http://localhost:8080/api/products`
**Body:**
```json
{
  "name": "Gaming Laptop",
  "description": "High performance gaming laptop",
  "price": 1299.99,
  "stockQuantity": 50,
  "categoryId": 1,
  "imageUrl": "http://example.com/laptop.jpg"
}
```
*(Assumes Category ID 1 exists)*

### Create Product (Smartphone)
**POST** `http://localhost:8080/api/products`
**Body:**
```json
{
  "name": "Smartphone Pro",
  "description": "Latest model smartphone",
  "price": 999.99,
  "stockQuantity": 100,
  "categoryId": 1,
  "imageUrl": "http://example.com/phone.jpg"
}
```

### Update Product
**PUT** `http://localhost:8080/api/products/{id}`
**Body:**
```json
{
  "name": "Gaming Laptop Pro",
  "description": "Upgraded RAM and SSD",
  "price": 1399.99,
  "stockQuantity": 45,
  "categoryId": 1,
  "imageUrl": "http://example.com/laptop_pro.jpg"
}
```

### Delete Product
**DELETE** `http://localhost:8080/api/products/{id}`
