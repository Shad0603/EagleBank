# EagleBank API

A secure and robust RESTful API for banking operations, allowing users to manage accounts and perform financial transactions.

## Features

- **User Management**: Register, retrieve, update, and delete user profiles
- **Account Management**: Create and manage bank accounts with proper validation
- **Transaction Processing**: Deposit and withdraw funds with transaction history
- **Authentication & Authorization**: Secure JWT-based authentication and ownership validation
- **Data Validation**: Comprehensive input validation for all API operations
- **Error Handling**: Detailed error responses with appropriate HTTP status codes

## Technologies Used

- **Java 17**
- **Spring Boot**: Core framework for building the application
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access and persistence
- **PostgreSQL**: Relational database for storing application data
- **JWT (JSON Web Tokens)**: Stateless authentication mechanism
- **Hibernate Validator**: Input validation
- **JUnit & Mockito**: Testing framework
- **OpenAPI/Swagger**: API documentation

## Project Structure

The project follows a standard Spring Boot architecture:

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── app/
│   │           └── eaglebank/
│   │               ├── config/         # Application configuration
│   │               ├── controller/     # REST controllers
│   │               ├── dto/            # Data Transfer Objects
│   │               ├── exception/      # Custom exceptions and error handling
│   │               ├── mapper/         # Object mappers
│   │               ├── model/          # JPA entities
│   │               ├── repository/     # Data access layer
│   │               ├── security/       # Security configuration
│   │               ├── service/        # Business logic
│   │               └── EagleBankApplication.java  # Main application class
│   └── resources/
│       └── application.properties      # Application configuration
└── test/
    └── java/
        └── com/
            └── app/
                └── eaglebank/
                    └── service/        # Service tests
```

## Setup and Installation

### Prerequisites

- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

### Database Setup

1. Create a PostgreSQL database named `eaglebank`
2. Set the following environment variables:
   - `DB_USERNAME`: Your PostgreSQL username
   - `DB_PASSWORD`: Your PostgreSQL password

### Building and Running

1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```
   mvn clean install
   ```
4. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080` by default.

## API Documentation

The API is documented using OpenAPI specification. The full specification can be found in the `openapi/openapi.yaml` file.

### Key Endpoints

- **Authentication**: `/v1/auth/login`
- **Users**: `/v1/users`
- **Accounts**: `/v1/accounts`
- **Transactions**: `/v1/accounts/{accountNumber}/transactions`

## Usage Examples

### Authentication

```bash
# Login
curl -X POST http://localhost:8080/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "password123"}'
```

### Creating a User

```bash
# Register a new user
curl -X POST http://localhost:8080/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com",
    "password": "securePassword123",
    "phoneNumber": "+447123456789",
    "address": {
      "line1": "123 Main St",
      "town": "London",
      "county": "Greater London",
      "postcode": "SW1A 1AA"
    }
  }'
```

### Creating an Account

```bash
# Create a new account (requires authentication)
curl -X POST http://localhost:8080/v1/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Personal Account",
    "accountType": "personal"
  }'
```

### Making a Transaction

```bash
# Deposit money (requires authentication)
curl -X POST http://localhost:8080/v1/accounts/01234567/transactions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "amount": 100.00,
    "currency": "GBP",
    "type": "deposit",
    "reference": "Salary payment"
  }'
```

## Testing

The project includes unit tests for the service layer. To run the tests:

```bash
mvn test
```

Key test classes:
- `UserServiceTest`: Tests for user management operations
- `AccountServiceTest`: Tests for account management operations
- `TransactionServiceTest`: Tests for transaction processing

## Security Considerations

- All endpoints (except user registration and login) require authentication
- Users can only access and modify their own resources
- Passwords are securely hashed before storage
- JWT tokens are used for stateless authentication
- Input validation is performed on all requests

## License

This project is licensed under the MIT License - see the LICENSE file for details.