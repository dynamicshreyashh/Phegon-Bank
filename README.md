# Phegon Bank — Full-Stack FinTech Application

A production-style banking application built with **Spring Boot 4, Spring Security/JWT, MySQL, React, Docker and GitHub Actions**.

## Features

- JWT authentication and BCrypt password hashing
- Customer, Auditor and Admin roles
- Automatic savings-account creation during registration
- Multiple accounts per user
- Deposit, withdrawal and atomic transfer APIs
- Pessimistic locking for concurrent balance updates
- Paginated transaction history
- Password reset through SMTP email
- Transaction/account creation email notifications
- Profile picture uploads through AWS S3
- Admin role management
- Auditor APIs for users, accounts and transactions
- React dashboard for login, registration, accounts and transactions
- Docker Compose for MySQL + Spring Boot API + React/Nginx
- GitHub Actions build/test pipeline
- Environment-based configuration; no application secrets are committed

## Architecture

```
React + Nginx
     |
     v
Spring Boot REST API
  |       |       |
 Auth   Accounts  Transactions
  |       |       |
 JWT    MySQL    Notifications
             |
          AWS S3
```

## Run locally

### 1. Configure environment

Copy `.env.example` to `.env` and provide your MySQL credentials and a long random JWT secret.

For admin access, optionally configure `ADMIN_EMAIL` and `ADMIN_PASSWORD`. The application creates the admin account on startup when both are present.

SMTP and S3 are optional for basic banking functionality. They become active when their environment variables are configured.

### 2. Start everything with Docker

```bash
docker compose up --build
```

Frontend: http://localhost:5173  
Backend: http://localhost:8080

### 3. Run backend without Docker

Create the MySQL database and configure the values in `.env`, then:

```bash
./mvnw spring-boot:run
```

### 4. Run frontend without Docker

```bash
cd frontend
npm install
npm run dev
```

## Main API endpoints

### Authentication
- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `PUT /api/auth/password`
- `POST /api/auth/forgot-password?email=...`
- `POST /api/auth/reset-password`

### Accounts
- `POST /api/accounts`
- `GET /api/accounts`
- `GET /api/accounts/{accountNumber}`
- `PATCH /api/accounts/{accountNumber}/status`

### Transactions
- `POST /api/transactions`
- `GET /api/transactions/{accountNumber}?page=0&size=20`

Example transaction request:

```json
{
  "transactionType": "TRANSFER",
  "amount": 100.00,
  "accountNumber": "123456789012345",
  "destinationAccountNumber": "987654321098765",
  "description": "Rent"
}
```

### Users
- `GET /api/users/me`
- `GET /api/users?page=0&size=20` — Admin/Auditor
- `PUT /api/users/password`
- `POST /api/users/profile-picture`

### Roles
- `GET /api/roles`
- `POST /api/roles`
- `PUT /api/roles`
- `DELETE /api/roles/{id}`

### Auditor
- `GET /api/auditor/users`
- `GET /api/auditor/accounts`
- `GET /api/auditor/transactions`

## Security notes

- Public registration always creates a `ROLE_CUSTOMER`; clients cannot self-register as Admin or Auditor.
- Passwords are stored using BCrypt.
- Banking mutations require an authenticated account owner.
- Transfers lock both account rows and run inside one database transaction.
- Cross-currency transfers are rejected rather than silently applying an exchange rate.
- Closed accounts cannot be reused and can only be closed with a zero balance.
- Secrets belong in environment variables, never in source control.

## Course alignment

The implementation intentionally follows the same broad feature set as the Udemy project—banking APIs, JWT security, SMTP notifications, roles/auditing, S3 storage, React, Docker and CI/CD—but uses an original implementation and some stricter safeguards. The course currently describes these as core parts of its curriculum. 
