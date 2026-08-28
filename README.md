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
  - [6. Upload School Fees Configuration Spreadsheet](#6-upload-school-fees-configuration-spreadsheet)
  - [7. Initiate School Fees Payment](#7-initiate-school-fees-payment)
  - [8. Paystack Payment Webhook](#8-paystack-payment-webhook)
  - [9. Verify School Fees Payment Eligibility](#9-verify-school-fees-payment-eligibility)
  - [10. Get School Fees Details & Breakdown](#10-get-school-fees-details--breakdown)
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
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Child account with specified School ID and Date of Birth does not exist"
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
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "Invalid Excel file format or unparseable columns at row 5"
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

- **`400 Bad Request`** (Account already active, inactive, or invalid Date of Birth):
  ```json
  {
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "Account status is already ACTIVE or Date of Birth does not match pre-registered records"
  }
  ```
- **`404 Not Found`** (School ID / Username not found):
  ```json
  {
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Account not found for username: STU/2026/012"
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
    "type": "about:blank",
    "title": "Forbidden",
    "status": 403,
    "detail": "Account status INACTIVE is not allowed to access this resource"
  }
  ```

---

### 6. Upload School Fees Configuration Spreadsheet

Enables Administrators or Principals to upload structured Excel spreadsheets (`.xlsx`) containing school fee breakdowns (tuition, total fees, and term minimum percentages) across grades and senior secondary departments.

- **Endpoint**: `POST /api/v1/schoolfee/excel`
- **Access**: Secured (`ACTIVE`, `ADMIN`, `PRINCIPAL`)
- **Content-Type**: `multipart/form-data`

#### Request Parameters
- `file` *(binary)*: The `.xlsx` workbook specifying fees configuration per grade/department.

#### Success Response (`201 Created`)
```json
{
  "count": 1,
  "data": [
    {
      "session": "2026/2027",
      "department": "SCIENCE",
      "grade": "SS1",
      "tuition": 150000.0,
      "total": 220000.0,
      "firstTermMinPercentage": 0.5,
      "secondTermMinPercentage": 0.8,
      "thirdTermMinPercentage": 1.0
    }
  ]
}
```

#### Error Responses

- **`400 Bad Request`** (Invalid percentage order, percentage > 100%, or bad Excel structure):
  ```json
  {
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "Row: 2 First term minimum percentage can't be greater than the Second term minimum percentage"
  }
  ```
- **`401 Unauthorized` / `403 Forbidden`** (Missing or insufficient permissions):
  ```json
  {
    "error": "Invalid JWT"
  }
  ```

---

### 7. Initiate School Fees Payment

Initiates a Paystack checkout transaction for a parent paying fees on behalf of their ward.

- **Endpoint**: `POST /api/v1/schoolfee/payment`
- **Access**: Secured (`ACTIVE`, `PARENT`)
- **Headers**: `Authorization: Bearer <jwt_token>`
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "studentId": "STU/2026/001",
  "amount": 100000.0
}
```

#### Success Response (`201 Created`)
```json
{
  "redirectUrl": "https://checkout.paystack.com/0123456789abcdef"
}
```

#### Error Responses

- **`400 Bad Request`** (Payment exceeds total outstanding amount or prior fees unpaid):
  ```json
  {
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "Exceeded total amount"
  }
  ```
- **`401 Unauthorized` / `403 Forbidden`**:
  ```json
  {
    "error": "Invalid JWT"
  }
  ```

---

### 8. Paystack Payment Webhook

Processes asynchronous transaction lifecycle events from Paystack, verifies HMAC signature, and updates fee transaction and ledger status.

- **Endpoint**: `POST /api/v1/schoolfee/webhook`
- **Access**: Public
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "signature": "a1b2c3d4e5f6...",
  "rawPayload": "{\"event\":\"charge.success\",\"data\":{\"reference\":\"TX-123456\",\"status\":\"success\"}}"
}
```

#### Success Response (`201 Created`)
*Empty Body*

#### Error Responses

- **`400 Bad Request`** (Invalid signature or malformed payload):
  ```json
  {
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "Invalid Webhook Signature"
  }
  ```
- **`409 Conflict`** (Duplicate transaction reference):
  ```json
  {
    "type": "about:blank",
    "title": "Conflict",
    "status": 409,
    "detail": "Transaction already processed"
  }
  ```

---

### 9. Verify School Fees Payment Eligibility

Verifies whether a student has met the required percentage thresholds to qualify for school term activities (such as examinations or registration) for a specific academic session.

- **Endpoint**: `GET /api/v1/schoolfee/verification`
- **Access**: Authenticated
- **Content-Type**: `application/json`

#### Request Body
```json
{
  "studentID": "STU/2026/001",
  "session": "2026/2027"
}
```

#### Success Response (`201 Created`)
```json
{
  "studentFirstName": "Tunde",
  "studentLastName": "Adebayo",
  "qualifiedForFirstTerm": true,
  "qualifiedForSecondTerm": true,
  "qualifiedForThirdTerm": false
}
```

#### Error Responses

- **`400 Bad Request`** (Invalid session format or student ID):
  ```json
  {
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "Input Session could not be parsed into Session Object"
  }
  ```
- **`404 Not Found`** (Fee ledger or classroom not found):
  ```json
  {
    "type": "about:blank",
    "title": "Not Found",
    "status": 404,
    "detail": "Fee Ledger does not exist for the student in this session"
  }
  ```

---

### 10. Get School Fees Details & Breakdown

Fetches comprehensive school fee details, tuition amount, total charges, and payment progress for all wards linked to the logged-in parent.

- **Endpoint**: `GET /api/v1/schoolfee`
- **Access**: Secured (`ACTIVE`, `PARENT`)
- **Headers**: `Authorization: Bearer <jwt_token>`

#### Request Body
*None*

#### Success Response (`201 Created`)
```json
{
  "data": [
    {
      "session": "2026/2027",
      "studentID": "STU/2026/001",
      "studentFirstName": "Tunde",
      "studentLastName": "Adebayo",
      "grade": "JSS2",
      "department": "NONE",
      "tuition": 150000.0,
      "total": 220000.0,
      "totalPaid": 120000.0
    }
  ]
}
```

#### Error Responses

- **`400 Bad Request`** (Parent has no linked child):
  ```json
  {
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "This parent currently has no child assigned"
  }
  ```
- **`401 Unauthorized` / `403 Forbidden`**:
  ```json
  {
    "error": "Invalid JWT"
  }
  ```

---

## Global Exception & Error Responses

The system uses a centralized `@RestControllerAdvice` exception handler ([`GlobalExceptionHandler`](file:///src/main/java/com/school/eportal/utils/GlobalExceptionHandler.java)). All exceptions are formatted and returned as standard RFC 7807 **`ProblemDetail`** objects.

### 1. Standard Domain Error Response (`ProblemDetail`)
Returned for custom application business exceptions (e.g. `InactiveAccountStatusException`, `InvalidPasswordException`, `AccountNotFoundException`, `InvalidClassroomException`, `ExcelParserException`).

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Error description message explaining the precise failure"
}
```

#### Domain Exception Classes & Status Code Mapping
| Exception Class | Status | Description / Trigger Condition |
| :--- | :--- | :--- |
| `InactiveAccountStatusException` *(New)* | `400 BAD REQUEST` | Attempting actions on a child or user account that has not yet been activated |
| `InvalidPasswordException` *(New)* | `400 BAD REQUEST` | Password change failed due to incorrect old password |
| `InvalidAccountStatusException` | `400 BAD REQUEST` | Account is already active or invalid status transition attempted |
| `InvalidAmountException` | `400 BAD REQUEST` | Payment amount is invalid or exceeds total required fees |
| `InvalidBulkRegistration` | `400 BAD REQUEST` | Bulk user onboarding failure due to duplicate usernames |
| `InvalidCellValueException` | `400 BAD REQUEST` | Excel cell value formatting error or missing mandatory cell |
| `InvalidClassroomException` | `400 BAD REQUEST` | Grade and division combination does not exist |
| `InvalidDateOfBirthException` | `400 BAD REQUEST` | Date of birth validation failure against registered records |
| `InvalidFileTypeException` | `400 BAD REQUEST` | Uploaded document is not a valid Microsoft Excel file |
| `InvalidPercentageException` | `400 BAD REQUEST` | Term fees percentage breakdown exceeds 100% or fails order checks |
| `InvalidPreRegistrationException` | `400 BAD REQUEST` | Excel sheet contains empty student/teacher rosters |
| `InvalidRoleException` | `400 BAD REQUEST` | Operation not applicable to the specified user role |
| `InvalidSchoolSessionException` | `400 BAD REQUEST` | Invalid school session year format |
| `InvalidSessionException` | `400 BAD REQUEST` | Academic session could not be parsed into a valid Session object |
| `InvalidUserException` | `400 BAD REQUEST` | User reference is invalid |
| `InvalidUsernameException` | `400 BAD REQUEST` | Username or email address already registered |
| `InvalidWebhookSignature` | `400 BAD REQUEST` | Paystack webhook verification failure |
| `OutstandingSchoolFeesException` | `400 BAD REQUEST` | Student has uncleared prior term or session fees |
| `ParentChildRelationshipException` | `400 BAD REQUEST` | Parent account has no linked child or invalid ward relationship |
| `ParsingException` | `400 BAD REQUEST` | Payload or payload structure parsing error |
| `SchoolFeesException` | `400 BAD REQUEST` | School fees configuration error |
| `ValidatorException` | `400 BAD REQUEST` | General validation error |
| `UserNotFoundException` | `401 UNAUTHORIZED` | User not found in authentication context |
| `AuthenticationNotSupportedException` | `401 UNAUTHORIZED` | Authentication mechanism not supported |
| `AcademicSessionDoesntExistException` | `404 NOT FOUND` | Academic session does not exist |
| `AccountNotFoundException` | `404 NOT FOUND` | Account record does not exist |
| `FeeLedgerDoesntExistException` | `404 NOT FOUND` | Fee ledger for student/session does not exist |
| `FeeTransactionDoesntExistException` | `404 NOT FOUND` | Fee transaction record does not exist |
| `NoSuchClassroomException` | `404 NOT FOUND` | Classroom does not exist |
| `TransactionAlreadyExistsException` | `409 CONFLICT` | Duplicate transaction reference detected |

### 2. Spring RFC 7807 Validation Error (`ProblemDetail`)
Returned when request body validation fails (`MethodArgumentNotValidException`). Includes a key-value mapping of field validation errors under the `errors` property.

```json
{
  "type": "about:blank",
  "title": "Validation Failed",
  "status": 400,
  "detail": "One or more fields are invalid",
  "instance": "/api/v1/auth/registration",
  "errors": {
    "username": "Must be a valid Email Address",
    "password": "Password must be at least 8 characters"
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
