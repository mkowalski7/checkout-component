# Checkout Component

A Spring Boot-based checkout component designed for e-commerce applications, providing robust order processing and payment handling capabilities.

## 🏗️ Technology Stack

- **Java 21** 
- **Spring Boot 3.5.5** 
- **Spring Data JPA** 
- **Spring Validation** 
- **Flyway** 
- **H2 Database** 
- **Lombok** 
- **Gradle** 

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- Git

### Building the Project

```bash
# Clone the repository
git clone https://github.com/mkowalski7/checkout-component.git
cd checkout-component

# Build the project
./gradlew build
```

### Running the Application

```bash
# Run with default profile
./gradlew bootRun

# Run with development profile
./gradlew dev
```

The application will start on `http://localhost:8080`

### Running Tests

```bash
./gradlew test
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── pl/checkout/    # Main application code
│   └── resources/
│       ├── application.properties    # Configuration
│       ├── application-dev.properties    # Dev Configuration
│       └── db/migration/            # Flyway database migrations
└── test/
    └── java/                        # Unit and integration tests
```

## 🏛️ Architecture Overview

### Key Components

- **Controllers** - REST API endpoints handling HTTP requests
- **Services** - Business logic implementation
- **Repositories** - Data access layer using Spring Data JPA
- **Entities** - JPA entities representing domain models
- **DTOs** - Data transfer objects for API communication
  
### Design Principles

- **Domain-Driven Design (DDD)**
- **Clean Architecture**
- **RESTful API Design** 
- **Database-First Approach** 

## 🔧 Configuration

### Profiles

- **Default** - Production-ready configuration
- **Development** (`dev`) - Enhanced logging and debugging features

### Database Configuration

The application uses H2 in-memory database by default. Database schema is managed through Flyway migrations located in `src/main/resources/db/migration/`.

## 📝 API Documentation

The checkout controller exposes RESTful endpoints for:

- Order creation and management
- Payment processing

#### Create New Checkout Session
Initializes a new checkout session for a customer.

**Endpoint:** `POST /api/v3/checkout/initialize`

**Request:**
```bash
curl -X POST http://localhost:8080/api/v3/checkout/initialize \
  -H "Content-Type: application/json"
```
**Response:**
```json
{
  "id": "00000000-0000-0000-0000-000000000001",
  "products": [],
  "totalAmount": 0.00,
  "totalDiscount": 0.00,
  "finalAmount": 0.00
}
```

#### Get Checkout Session
Retrieves details of an existing checkout session.

**Endpoint:** `GET /api/v3/checkout/{sessionId}`

**Request:**
```bash
curl -X GET http://localhost:8080/api/v3/checkout/00000000-0000-0000-0000-000000000001 \
  -H "Content-Type: application/json"
```
**Response:**
```json
{
  "id": "00000000-0000-0000-0000-000000000001",
  "products": [],
  "totalAmount": 0.00,
  "totalDiscount": 0.00,
  "finalAmount": 0.00
}
```

---

### 📦 **Product Management**

#### Add Product to Session
Adds a product to the checkout session.

**Endpoint:** `PUT /api/v3/checkout/{sessionId}/products`

**Request:**
```bash
curl -X PUT http://localhost:8080/api/v3/checkout/00000000-0000-0000-0000-000000000001/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "EXAMPLE-001",
    "quantity": 1
  }'
```
**Request Body:**
```json
{
  "sku": "EXAMPLE-001",
  "quantity": 1
}
```
**Response:**
```json
{
  "sessionId": "00000000-0000-0000-0000-000000000001",
  "products": [
    {
      "sku": "EXAMPLE-001",
      "name": "Example Product",
      "quantity": 1,
      "price": 29.99
    }
  ],
  "totalAmount": 59.98,
  "totalDiscount": 30.00,
  "finalAmount": 90.00
}
```

#### Remove Product from Session
Removes a product from the checkout session.

**Endpoint:** `DELETE /api/v3/checkout/{sessionId}/products`

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/v3/checkout/00000000-0000-0000-0000-000000000001/products \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "EXAMPLE-001",
    "removeAll": false
  }'
```
**Request Body:**
```json
{
  "productId": "PROD-001",
  "removeAll": false
}
```

---

### 💳 **Payment Processing**

#### Process Payment
Processes payment for the checkout session and generates a receipt.

**Endpoint:** `POST /api/v3/checkout/{sessionId}/payment`

**Request:**
```bash
curl -X POST http://localhost:8080/api/v3/checkout/00000000-0000-0000-0000-000000000001/payment \
  -H "Content-Type: application/json" \
  -d '{
    "paymentStatus": "SUCCESS"
  }'
```
**Request Body:**
```json
{
  "paymentStatus": "SUCCESS"
}
```
**Response:**
```json
{
  "id": "00000000-0000-0000-0000-000000000001",
  "paymentStatus": "SUCCESS"
  "products": [
    {
      "productId": "EXAMPLE-001",
      "name": "Example Product",
      "quantity": 1,
      "price": 50.00,
      "discount": 5.00,
      "finalPrice": 45.00,
      "totalPrice": 90.00
    }
  ],
  "totalAmount": 100.00,
  "totalDiscount": 10.00,
  "finalAmount": 90.00
}
```


