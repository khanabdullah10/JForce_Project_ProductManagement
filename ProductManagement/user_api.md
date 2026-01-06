# User API Documentation

**Flow:** Register -> Login -> Browse Products -> Add to Cart -> Add Address -> Checkout.

## 1. Authentication

### Register
**POST** `http://localhost:8080/api/auth/register`
**Body:**
```json
{
  "username": "john_doe",
  "password": "password123",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Login (Test Credentials)
**GET** `http://localhost:8080/api/auth/me`
**Headers:**
- `Authorization`: `Basic am9obl9kb2U6cGFzc3dvcmQxMjM=`
  *(Base64 of `john_doe:password123`)*

## 2. Browsing

### Get All Products
**GET** `http://localhost:8080/api/products`

### Get Product by ID
**GET** `http://localhost:8080/api/products/{id}`

### Get All Categories
**GET** `http://localhost:8080/api/categories`

## 3. Shopping Cart

### Add Item to Cart
**POST** `http://localhost:8080/api/cart/items`
**Body:**
```json
{
  "productId": 1,
  "quantity": 1
}
```

### View Cart
**GET** `http://localhost:8080/api/cart`

### Update Cart Item Quantity
**PUT** `http://localhost:8080/api/cart/items/{cartItemId}?quantity=2`

### Remove Item from Cart
**DELETE** `http://localhost:8080/api/cart/items/{cartItemId}`

## 4. Checkout Preparation

### Add Shipping Address
**POST** `http://localhost:8080/api/addresses`
**Body:**
```json
{
  "street": "123 Tech Park",
  "city": "Silicon Valley",
  "state": "CA",
  "zipCode": "94000",
  "country": "USA"
}
```

### Get My Addresses
**GET** `http://localhost:8080/api/addresses`

## 5. Order Placement

### Place Order
**POST** `http://localhost:8080/api/orders/checkout`
**Body:**
```json
{
  "addressId": 1,
  "paymentMethod": "CREDIT_CARD"
}
```

### View My Orders
**GET** `http://localhost:8080/api/orders`
