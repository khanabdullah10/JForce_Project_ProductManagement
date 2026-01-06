# Product Management System - Interview Preparation Guide

## 1. Project Overview (The "Elevator Pitch")

**Script:**
"Hi, I'd like to walk you through my **Product Management System**. It's a robust backend REST API built with **Spring Boot** that handles the core operations of an e-commerce platform.

It features **Role-Based Access Control (RBAC)** with three distinct roles: Users, Admins, and Super Admins.
- **Users** can browse products, manage their cart, and place orders.
- **Admins** manage the product inventory.
- **Super Admins** oversee the entire system, including user roles and categories.

Technically, I used **Spring Security** for authentication, **Spring Data JPA** for database interactions with **MySQL**, and **Hibernate** for ORM. I also implemented comprehensive error handling and input validation to ensure data integrity."

---

## 2. Technical Architecture

*   **Framework**: Spring Boot 3.x (Java 17+)
*   **Security**: Spring Security 6.x (Stateless, Basic Auth for simplicity, easily upgradeable to JWT).
*   **Database**: MySQL (Relational DB).
*   **ORM**: Hibernate / Spring Data JPA.
*   **Build Tool**: Maven.
*   **Key Libraries**: Lombok (to reduce boilerplate code like getters/setters).

---

## 3. Key Challenges & Solutions (The "Star" Stories)

*Interviwers love hearing about problems you solved.*

### Challenge 1: Circular Dependencies (StackOverflowError)
*   **Problem**: My `User` entity had a `Cart`, and `Cart` had a `User`. When Lombok generated `toString()` or `hashCode()`, they kept calling each other infinitely, crashing the app.
*   **Solution**: I used `@ToString.Exclude` and `@EqualsAndHashCode.Exclude` on the relationship fields (e.g., `@OneToOne`, `@OneToMany`) to break the loop.

### Challenge 2: Lazy Initialization Exception
*   **Problem**: When the application started, it tried to access the `users` collection inside the `Role` entity during a logging step, but the database session was already closed.
*   **Solution**: I realized `Role` doesn't need to load all its `Users` every time (it's inefficient). I excluded the `users` collection from `hashCode()` methods so it wouldn't be accessed unnecessarily.

### Challenge 3: Security Configuration
*   **Problem**: Configuring different access levels for different endpoints (e.g., only Admins can delete products).
*   **Solution**: I used `SecurityConfig` with `requestMatchers` and method-level security (`@PreAuthorize("hasRole('ADMIN')")`) to enforce strict access control.

---

## 4. Common Interview Questions

### Basic Level
1.  **What is Spring Boot?**
    *   *Answer*: It's an extension of the Spring Framework that simplifies setup. It provides "starters" for dependencies and auto-configuration to get an app running quickly without complex XML files.
2.  **What is Dependency Injection (DI)?**
    *   *Answer*: It's a design pattern where Spring manages object creation. Instead of saying `new UserService()`, I let Spring inject the `UserService` instance into my Controller using the `@Autowired` annotation or constructor injection.
3.  **What is the difference between `@Entity` and `@Table`?**
    *   *Answer*: `@Entity` marks a class as a JPA entity (mapped to a database). `@Table` specifies the exact name of the table in the DB (useful if the table name differs from the class name).

### Intermediate Level
1.  **How does Authentication work in your app?**
    *   *Answer*: I use `DaoAuthenticationProvider`. It loads user details from the database using my `CustomUserDetailsService` and compares the hashed password using `BCryptPasswordEncoder`.
2.  **What is the "N+1 Problem" in Hibernate?**
    *   *Answer*: It happens when fetching a list of entities (1 query) and then iterating over them to fetch related data (N queries). I avoided this by using `FetchType.LAZY` where appropriate or using `JOIN FETCH` in custom queries if needed.
3.  **Why do you use DTOs (Data Transfer Objects)?**
    *   *Answer*: To decouple my internal database entities from the external API. It allows me to hide sensitive fields (like passwords) and validate input (like checking if a price is positive) before it reaches my business logic.
4.  **How do you handle Transactions?**
    *   *Answer*: I use the `@Transactional` annotation on my Service methods. This ensures that if one step fails (e.g., saving an order), the previous steps (e.g., reducing inventory) are rolled back to prevent data inconsistency.

---

## 5. Project Walkthrough for the Interviewer

If asked to demo, follow this flow:
1.  **Show `pom.xml`**: "Here are my dependencies..."
2.  **Show `application.properties`**: "Here is my DB config and configurable admin credentials..."
3.  **Show `SecurityConfig`**: "This is where I define who can access what..."
4.  **Show an Entity (e.g., `Product`)**: "This maps to my database..."
5.  **Show a Controller (e.g., `AuthController`)**: "This handles the HTTP requests..."
6.  **Run the App**: Show the console logs starting up successfully.
7.  **Open Postman**: Run the `Login` request you saved to prove it works.
