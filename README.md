# 🏬 Store Management API

This is a backend API built with Java and Spring Boot that manages users and products in a simple store system.  
It includes full CRUD operations, DTO mapping, password change logic, and basic category support.

This project was built as part of my journey learning Java backend development. It gave me hands-on experience with layered architecture, RESTful APIs, entity relationships, and data transfer patterns.

---

## 🚀 Features

### 🧑‍💼 User Management
- Get all users (with sorting by name or email)
- Get a user by ID
- Create a new user
- Update existing user
- Delete a user
- Change user password (with basic validation)

### 🛒 Product Management
- Get all products (optionally filter by category ID)
- Get a product by ID
- Create a new product (with category association)
- Update existing product
- Delete a product

---

## Project Structure

```bash
    com.example.store
    ├── controllers/     # REST controllers (UserController, ProductController)
    ├── dtos/            # UserDto, ProductDto, RegisterUserRequest, UpdateUserRequest, etc.
    ├── entities/        # User, Product, Category, Address, Profile
    ├── mappers/         # UserMapper, ProductMapper (MapStruct)
    ├── repositories/    # JPA Repositories for User, Product, Category, Address, Profile
```

---

## ⚙️ Tech Stack & Dependencies
* Java 21
* Maven
* Spring Boot 3.5.0
* Spring Data JPA
* Microsoft SQL Server
* MapStruct
* Lombok
* SpringDoc (Swagger UI)

---

## 🧪 How to Run

1. Clone the project:
    ```bash
    git clone https://github.com/ogunkapc/Store.git
    cd Store
    ```
2. Make sure Java 21 and Maven are installed

3. Run it:
    ```bash
    ./mvnw spring-boot:run
    ```
4. API Endpoints:
    - Users: http://localhost:8080/api/users
    - Products: http://localhost:8080/api/products
5. Check the Swagger UI at `http://localhost:8080/swagger-ui.html` for API documentation

---

## 📁 Status

🔧 Still in progress — this is a personal learning project I improve from time to time.