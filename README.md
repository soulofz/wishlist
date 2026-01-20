# 🎁 Wishlist — Spring Boot application for managing wish lists

**Wishlist** is a fully functional Spring Boot backend application that provides a REST API for managing user wish lists.

The project includes JWT-based authentication, image uploading via Cloudinary, Flyway migrations, data validation, global error handling, and a multi-tier architecture.

## 🚀 Key features

* User registration and authorization
* JWT authentication and authorization
* Creation, editing, and deletion of wishlists
* Management of wishlist items
* Uploading images to Cloudinary
* Validating incoming data
* Global error handling
* Logging and intercepting requests via Interceptor
* Automatic API documentation via Springdoc OpenAPI
* Database migrations via Flyway
* WAR packaging for deployment on Tomcat

## 🧱Project architecture
com.wishlist

├── **config**          _# Configurations (CORS, CloudImageService, Security, etc.)_

├── **controller**      _# REST controllers_

├── **exception**       _# Custom exceptions + global handler_

├── **interceptor**     _# Request interceptors_

├── **model**           _# JPA entities + DTOs_

├── **repository**      _# Spring Data JPA repositories_

├── **security**        _# JWT, filters, UserDetailsService_

├── **service**         _# Business logic_

└── WishlistApplication.java

## 🛠️ Technologies used

### Backend
* Java 21
* Spring Boot 4
* Spring Web / WebMVC
* Spring Security
* Spring Data JPA
* Hibernate
* Jakarta Validation
* Flyway
* JWT (jjwt)
* Lombok

### Database
* PostgreSQL

### Documentation API
* Cloudinary API

### Build
* Maven
* Packaging: WAR


## 🛡️ Security

### The project uses:

* JWT tokens
* **Filter** _JwtAuthFilter_
* **Service:** _SecurityService_
* Restricting access to other people's resources
* Interceptor for logging and checking permissions

## 📸 Working with images

**Cloudinary** is used:

* uploading images
* storing URLs in the database
* deleting them when updating

**Service:** _CloudImageService_

## 📡 API (brief description)

### 🔐 Authorization

* **POST** - /security/registration - registration
* **POST** - /security/jwt - getting JWT
* **POST** - /refresh - updating JWT

### 📘 Wishlist

* **GET** - /wishlists - list of all wishlists
* **POST** - /wishlists - create a new wishlist
* **GET** - /wishlists/username - list of user's wishlists
* **GET** - /wishlists/id - individual wishlist
* **PUT** - /wishlists/id - edit wishlist
* **DELETE** - /wishlists/id - delete wishlist

### 🎁 Items

* **POST** - /wishlists/id/items/item - add an item to a wishlist
* **PUT** - /wishlists/id/items/itemid - change an item
* **DELETE** - /wishlists/id/items/itemid - delete an item
* **PUT** - /wishlists/id/items/itemid/reserve - reserve an item
* **PUT** - /wishlists/id/items/itemid/unreserve - cancel a reservation
* **GET** - /info/recommendation - get a list of recommendations
* **GET** - /info/reserved - get a list of items you have reserved