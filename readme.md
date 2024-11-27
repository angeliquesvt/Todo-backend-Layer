# Todo Layer

## Project Objective

This project provides backend APIs for managing a to-do list application.

The goal of this project is to implement the principles of **Layered Architecture**, a widely adopted architectural style that separates an application into layers based on responsibility.

### Technologies

    Java 19
    Spring Boot 2.6.5

### API Documentation

The API specifications are available in the form of [API Blueprint](https://todosbackend.docs.apiary.io/), offering a
clear and structured reference for all
endpoints. Please refer to the documentation to understand how to interact with the API.

## Layered Architecture Overview
Layered Architecture organizes the application into the following layers:

### Presentation Layer (UI):
Handles HTTP requests and responses. This layer contains controllers and is responsible for user interaction, input validation, and delegating tasks to the service layer.

### Service Layer (Business Logic):
Implements the core business logic. It processes input, applies business rules, and coordinates between the presentation and data layers.

### Data Access Layer (Persistence):
Manages interactions with the database. This layer contains repositories or DAOs to handle CRUD operations and database queries.
