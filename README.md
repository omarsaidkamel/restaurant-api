# Restaurant Ordering System API

Backend REST API for a restaurant ordering system built with Spring Boot and PostgreSQL.

## Features

- Product management
- User management
- Order creation
- Stock reduction
- Payment processing
- Automatic notifications after payment
- Reports API
- Search and pagination
- Validation and global exception handling
- Soft delete and restore
- Database constraints
- Pessimistic locking for stock protection

## Technologies

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Gradle
- Bean Validation

## Main APIs

### Products
- GET /api/products
- POST /api/products
- PUT /api/products/{id}
- DELETE /api/products/{id}
- PATCH /api/products/{id}/activate
- GET /api/products/search
- GET /api/products/inactive

### Users
- GET /api/users
- POST /api/users
- PUT /api/users/{id}
- DELETE /api/users/{id}
- PATCH /api/users/{id}/activate
- GET /api/users/search
- GET /api/users/inactive

### Orders
- GET /api/orders
- GET /api/orders/{id}
- POST /api/orders
- GET /api/orders/search

### Payments
- GET /api/payments
- POST /api/payments
- GET /api/payments/search

### Notifications
- GET /api/notifications
- GET /api/notifications/order/{orderId}
- GET /api/notifications/search

### Reports
- GET /api/reports/summary
- GET /api/reports/product-sales
- GET /api/reports/low-stock
