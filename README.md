# Ekart-Ecommerce-Project
🛒 Ekart - E-commerce Backend Application
📌 Overview

Ekart is a Spring Boot–based monolithic e-commerce backend application designed to handle core functionalities such as customer management, product catalog, order processing, and payments. The system follows a modular structure within a single deployable unit.

🚀 Features
🔐 Authentication & Authorization using JWT
👤 Customer registration & login
📦 Product management (CRUD operations)
🛍️ Order creation and tracking
💳 Payment integration (internal service)
⚡ Redis caching for performance optimization
🚦 Rate limiting to prevent abuse
🌐 RESTful APIs for all operations
🏗️ Tech Stack
Backend: Java, Spring Boot
Security: Spring Security, JWT
Caching: Redis
Database: (Add your DB here: MySQL / PostgreSQL / H2)
Build Tool: Maven
API Testing: Postman
📂 Project Structure
ekart/
│── controller/        # REST Controllers
│── service/           # Business logic
│── repository/        # Data access layer
│── model/             # Entity classes
│── dto/               # Request/Response objects
│── config/            # Security & app configurations
│── util/              # Utility classes
⚙️ Setup & Installation
1️⃣ Clone the repository
git clone https://github.com/your-username/ekart.git
cd ekart
2️⃣ Configure application properties

Update application.properties:

spring.datasource.url=your_db_url
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.redis.host=localhost
spring.redis.port=6379
3️⃣ Run Redis (if not running)
redis-server
4️⃣ Build & Run the application
mvn clean install
mvn spring-boot:run
🔗 API Endpoints (Sample)
Authentication
POST /customer-api/register
POST /customer-api/login
Products
GET /products
POST /products
Orders
POST /orders
GET /orders/{id}
🧪 Testing
Use Postman to test APIs
Import collection (if available)
📈 Future Enhancements
Convert to Microservices architecture
Add API Gateway & Service Discovery
Integrate external payment gateway
Implement Docker & Kubernetes deployment
Add CI/CD pipeline
👨‍💻 Author

Promise
(Java Backend Developer)
