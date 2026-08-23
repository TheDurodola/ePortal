# ePortal Base — Nigerian School Management System Template

A robust, extensible, and modular backend base project built with **Java 21**, **Spring Boot 4**, and **MongoDB**. This project serves as a foundational blueprint for developing customized electronic school portals (**ePortals**) specifically tailored to Nigerian educational institutions (Creche, Nursery, Primary, Junior Secondary (JSS), and Senior Secondary (SSS)).

 [Frontend - Nestjs](https://github.com/TheDurodola/ePortal-Client)
---
## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Domain Structure & Classifications](#domain-structure--classifications)
- [Tech Stack](#tech-stack)
- [Getting Started & Setup](#getting-started--setup)
- [Environment Configuration](#environment-configuration)
- [Authentication & Security](#authentication--security)
- [API Documentation](#api-documentation)
  - [1. User Sign-In (Authentication)](#1-user-sign-in-authentication)
  - [2. Parent Registration](#2-parent-registration)
  - [3. Pre-Registration via Excel Upload](#3-pre-registration-via-excel-upload)
  - [4. Account Activation](#4-account-activation)
  - [5. Get User Profile](#5-get-user-profile)
- [Global Exception & Error Responses](#global-exception--error-responses)
- [Customization Guide for Schools](#customization-guide-for-schools)

---

## Overview

The **ePortal Base** simplifies the deployment of school management systems in Nigeria. It addresses common local requirements such as bulk student/teacher onboarding from Excel spreadsheets, strict grade-level structures (Nursery 1-2, Primary 1-6, JSS 1-3, SS 1-3), senior secondary department branching (Science, Art, Commercial), parent-student linkages, account activation workflows, and fee payment processing (Paystack integration).

---

## Key Features

- **Bulk Pre-Registration**: Easily parse and validate Excel (`.xlsx`) files containing rosters of students and teachers using Apache POI.
- **Account Activation Workflow**: Students and staff receive pre-assigned School IDs and activate their accounts by verifying their Date of Birth and establishing a secure password.
- **Parent Portal Linkage**: Parents register by validating their ward's School ID and Date of Birth to auto-link parent-child profiles.
- **Role-Based Security**: Access controls configured for `ADMIN`, `PRINCIPAL`, `TEACHER`, `STUDENT`, `PARENT`, and `ACCOUNTANT`.
- **JWT Authentication**: Stateless token generation with strict claim validation and account status checks (`ACTIVE`, `INACTIVE`, `BANNED`).
- **Paystack Integration Ready**: Models and configuration ready for processing tuition and administrative fees.

---

## Domain Structure & Classifications

Designed around the standard Nigerian educational framework:

| Category | Values |
| :--- | :--- |
| **Roles** | `ADMIN`, `PRINCIPAL`, `TEACHER`, `STUDENT`, `PARENT`, `ACCOUNTANT` |
| **Grades** | `CRECHE`, `PLAYGROUND`, `KG1`, `KG2`, `NURSERY_1`, `NURSERY_2`, `PRIMARY_1` to `PRIMARY_6`, `JSS1` to `JSS3`, `SS1` to `SS3` |
| **Departments** | `SCIENCE`, `ART`, `COMMERCIAL`, `NONE` |
| **Divisions** | `A`, `B`, `NONE` |
| **Account Status** | `ACTIVE`, `INACTIVE`, `BANNED` |

---

## Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 4.1.0 (Spring WebMVC, Spring Security, Spring Validation, Actuator)
- **Database**: MongoDB (via Spring Data MongoDB & Docker Compose)
- **Security & Tokens**: Auth0 `java-jwt` (v4.5.0)
- **Excel Parsing**: Apache POI (v5.4.1) & Apache Tika (v3.2.3)
- **Payment Gateway**: Paystack integration configuration
- **Build Tool**: Maven

---

## Getting Started & Setup

### Prerequisites

- Java 21 JDK
- Docker & Docker Compose
- Maven 3.8+ (or use the provided `./mvnw` wrapper)

### Installation & Execution

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/TheDurodola/ePortal.git
   cd ePortal
   ```

2. **Configure Environment Variables**:
   Copy `.env.example` to `.env` and populate the required parameters:
   ```bash
   cp .env.example .env
   ```

3. **Start MongoDB via Docker**:
   ```bash
   docker-compose up -d
   ```
   *Mongo Express will be available at `http://localhost:8081` (Credentials: `guest` / `guest`).*

4. **Build and Run the Application**:
   ```bash
   ./mvnw clean spring-boot:run
   ```

---

## Environment Configuration

Refer to [`.env.example`](file:///.env.example) for required configuration keys:

```env
MONGODB_URI=mongodb://admin:admin@localhost:27017/eportal_db?authSource=admin
JWT_SIGNING_KEY=your_secure_secret_key_here
REFRESH_TOKEN_SIGNING_KEY=your_secure_refresh_key_here
JWT_DURATION=86400
PAYSTACK_SECRET_KEY=<SECRET_KEY>
SERVER_PORT=8080
```

---

## Authentication & Security

- **Public Endpoints**: Sign-in, parent registration, and account activation do not require an authorization header.
- **Secured Endpoints**: Require a valid JWT passed in the HTTP `Authorization` header:
  ```http
  Authorization: Bearer <your_jwt_token>
  ```
- **Permission Requirements**:
  - `POST /api/v1/preregistration/excel` requires `PRINCIPAL` or `ADMIN` authority.
  - Profile retrieval requires an account with an `ACTIVE` status.

  

---

##  API Documentation & Testing
To test these endpoints directly, view the interactive [Postman Documentation]((https://durodola-abolaji-s-team.postman.co/workspace/Team-Workspace~38bda554-bdc0-443b-8f9a-9a7bd7b74acd/collection/47831221-8d493821-a1f9-4782-b2b4-bda0fc48cb9b?action=share&creator=47831221)). It includes complete request schemas, headers, and pre-configured environment examples.

### 1. User Sign-In (Authentication)
---


Authenticates users and returns a signed JWT token upon successful credential verification.

- **Endpoint**: `POST /api/v1/auth/signin`
- **Access**: Public
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "username": "STU/2026/001",
  "password": "mySecurePassword123"
}
```

#### Success Response (`200 OK`)
```json
{
  "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Error Responses

- **`401 Unauthorized`** (Invalid credentials):
  ```json
  {
    "error": "Bad credentials",
    "message": "Invalid Username or Password"
  }
  ```
- **`500 Internal Server Error`** (System error during authentication):
  ```json
  {
    "error": "Database connectivity failed",
    "message": "Something went wrong..."
  }
  ```

---

### 2. Parent Registration

Registers a parent user account and automatically links the parent to their child's profile using the child's pre-registered School ID and Date of Birth.

- **Endpoint**: `POST /api/v1/auth/registration`
- **Access**: Public
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "username": "parent.adebayo@example.com",
  "firstName": "Bamidele",
  "lastName": "Adebayo",
  "dateOfBirth": "1980-05-14",
  "childSchoolId": "STU/2026/001",
  "childDateOfBirth": "2010-08-22",
  "password": "ParentPassword123!"
}
```

#### Success Response (`201 Created`)
```json
{
  "parentFirstName": "Bamidele",
  "childFirstName": "Tunde"
}
```

#### Error Responses

- **`400 Bad Request`** (Validation failed):
  ```json
  {
    "type": "about:blank",
    "title": "Validation Failed",
    "status": 400,
    "detail": "One or more fields are invalid",
    "instance": "/api/v1/auth/registration",
    "errors": {
      "username": "Must be a valid Email Address",
      "childSchoolId": "Lastname cannot be blank"
    }
  }
  ```
- **`404 Not Found`** (Child record not found or date of birth mismatch):
  ```json
  {
    "status": 404,
    "message": "Child account with specified School ID and Date of Birth does not exist",
    "timestamp": "2026-08-05T21:20:00"
  }
  ```

---

### 3. Pre-Registration via Excel Upload

Enables Administrators or Principals to upload structured Excel spreadsheets (`.xlsx`) to pre-register students and teachers into the database.

- **Endpoint**: `POST /api/v1/preregistration/excel`
- **Access**: Secured (`ADMIN`, `PRINCIPAL`)
- **Content-Type**: `multipart/form-data`

#### Request Parameters
- `file` *(binary)*: The `.xlsx` file containing student and teacher rosters.

#### Success Response (`201 Created`)
```json
{
  "students": [
    {
      "firstName": "Chidi",
      "lastName": "Okonkwo",
      "schoolId": "STU/2026/012",
      "role": "STUDENT",
      "grade": "SS1",
      "division": "A",
      "department": "SCIENCE"
    }
  ],
  "teachers": [
    {
      "username": "tch_funke@school.edu.ng",
      "firstName": "Funke",
      "lastName": "Akindele",
      "role": "TEACHER",
      "grade": "SS1",
      "division": "A"
    }
  ]
}
```

#### Error Responses

- **`400 Bad Request`** (Invalid file format, empty cells, or bad data structure):
  ```json
  {
    "status": 400,
    "message": "Invalid Excel file format or unparseable columns at row 5",
    "timestamp": "2026-08-05T21:20:00"
  }
  ```
- **`401 Unauthorized` / `403 Forbidden`** (Missing token or insufficient role privileges):
  ```json
  {
    "error": "Invalid JWT"
  }
  ```

---

### 4. Account Activation

Activates a pre-registered student or teacher account by confirming their pre-assigned School ID/Username, verifying their Date of Birth, and setting their password.

- **Endpoint**: `PATCH /api/v1/preregistration/activation`
- **Access**: Public
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "username": "STU/2026/012",
  "dateOfBirth": "2011-03-15",
  "password": "NewStudentPassword123!"
}
```

#### Success Response (`200 OK`)
```json
{
  "firstName": "Chidi"
}
```

#### Error Responses

- **`400 Bad Request`** (Account already active or invalid Date of Birth):
  ```json
  {
    "status": 400,
    "message": "Account status is already ACTIVE or Date of Birth does not match pre-registered records",
    "timestamp": "2026-08-05T21:20:00"
  }
  ```
- **`404 Not Found`** (School ID / Username not found):
  ```json
  {
    "status": 404,
    "message": "Account not found for username: STU/2026/012",
    "timestamp": "2026-08-05T21:20:00"
  }
  ```

---

### 5. Get User Profile

Fetches profile information for the logged-in user based on the JWT token provided in the request headers.

- **Endpoint**: `GET /api/v1/profile`
- **Access**: Secured (Requires `ACTIVE` status)
- **Headers**: `Authorization: Bearer <jwt_token>`

#### Request Body
*None*

#### Success Response (`200 OK`)
```json
{
  "profile": {
    "firstName": "Bamidele",
    "lastName": "Adebayo",
    "username": "parent.adebayo@example.com",
    "role": "PARENT",
    "dateOfBirth": "1980-05-14",
    "students": [
      {
        "id": "STU/2026/001",
        "firstName": "Tunde",
        "lastName": "Adebayo",
        "grade": "JSS2",
        "division": "B",
        "department": "NONE"
      }
    ],
    "teachers": []
  }
}
```

#### Error Responses

- **`401 Unauthorized`** (Invalid, missing, or expired JWT):
  ```json
  {
    "error": "Invalid JWT"
  }
  ```
- **`403 Forbidden`** (Account inactive or suspended):
  ```json
  {
    "status": 403,
    "message": "Account status INACTIVE is not allowed to access this resource",
    "timestamp": "2026-08-05T21:20:00"
  }
  ```

---

## Global Exception & Error Responses

The system uses a centralized `@RestControllerAdvice` exception handler (`GlobalExceptionHandler`). Error responses follow two standard formats:

### 1. Standard Domain Error Response (`ExceptionResponse`)
Returned for custom application business exceptions (e.g. `AccountNotFoundException`, `InvalidClassroomException`, `ExcelParserException`).

```json
{
  "status": 400,
  "message": "Error description message explaining the precise failure",
  "timestamp": "2026-08-05T21:20:00.123456"
}
```

Common status codes returned in `ExceptionResponse`:
- `400 BAD REQUEST`: Invalid request payloads, parsing errors, invalid status transitions.
- `401 UNAUTHORIZED`: Invalid credentials or missing user context.
- `404 NOT FOUND`: Requested resource or account does not exist.

### 2. Spring RFC-7807 Validation Error (`ProblemDetail`)
Returned when request body validation fails (`MethodArgumentNotValidException`).

```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "One or more fields are invalid",
  "instance": "/api/v1/auth/registration",
  "errors": {
    "fieldName": "Validation constraint message"
  }
}
```

---

## Customization Guide for Schools

When deploying a new customized ePortal instance for a specific Nigerian school:

1. **Branding & Configuration**: Update application properties and JWT issuer string in [`CustomAuthenticationFilter.java`](file:///src/main/java/com/school/eportal/security/filters/CustomAuthenticationFilter.java).
2. **Class & Grade Extensions**: Modify [`Grade.java`](file:///src/main/java/com/school/eportal/data/models/enums/Grade.java) and [`Department.java`](file:///src/main/java/com/school/eportal/data/models/enums/Department.java) if the school uses custom arm names (e.g. Gold, Diamond instead of A, B).
3. **Paystack Credentials**: Set `PAYSTACK_SECRET_KEY` in environment variables for school fees transactions.
4. **Excel Template**: Ensure school administrators upload spreadsheets matching expected column headers for student and teacher pre-registration.
