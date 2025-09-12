
# 🎫 Real-Time Event Ticketing System

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)
![Angular](https://img.shields.io/badge/Angular-18.2.9-red.svg)
![Node.js](https://img.shields.io/badge/Node.js-20.9.0-brightgreen.svg)

**A comprehensive, real-time event ticketing system with modern web technologies**

[🚀 Demo](https://tinyurl.com/eventTicketingSystem24) • [📖 Documentation](http://localhost:8080/ticket-system/swagger-ui.html) • [🐛 Report Bug](https://github.com/Peenaka-official/Real-Time_Event_Ticketing_System/issues) • [✨ Request Feature](https://github.com/Peenaka-official/Real-Time_Event_Ticketing_System/issues)

</div>

## 📋 Table of Contents

- [✨ Features](#-features)
- [🏗️ System Architecture](#️-system-architecture)
- [🖼️ Screenshots](#️-screenshots)
- [🚀 Getting Started](#-getting-started)
- [⚙️ Configuration](#️-configuration)
- [🔧 Development](#-development)
- [📚 API Documentation](#-api-documentation)
- [🧪 Testing](#-testing)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [👥 Contact](#-contact)

## ✨ Features

### 🎯 Core Functionality
- **Real-time ticket booking** with live availability updates
- **Multi-user support** (Vendors and Customers)
- **Event management** with comprehensive CRUD operations
- **Configuration management** for ticket pools and rates
- **Producer-Consumer pattern** for efficient ticket processing
- **Secure authentication** and authorization

### 🎨 Modern UI/UX
- **Responsive design** that works on all devices
- **Beautiful gradient backgrounds** for each section
- **Intuitive navigation** with clear visual feedback
- **Real-time notifications** for success/error states
- **Glassmorphism effects** for modern aesthetics

### 🔧 Technical Features
- **RESTful API** architecture
- **Real-time data synchronization**
- **Database versioning** with Liquibase
- **Comprehensive logging** and monitoring
- **Interactive API documentation** with Swagger
- **Command-line interface** for system administration

## 🏗️ System Architecture

```
Real-Time_Event_Ticketing_System/
├── 🖥️  Frontend/          # Angular 18 Web Application
├── ⚙️  Backend/           # Spring Boot REST API
└── 💻 CLI/               # Java Command Line Interface
```

### Technology Stack

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Frontend** | Angular | 18.2.9 | Modern, responsive web interface |
| **Backend** | Spring Boot | 3.x | RESTful API and business logic |
| **Database** | MySQL | 8.0+ | Data persistence and management |
| **CLI** | Java | 17+ | System administration and monitoring |
| **Documentation** | Swagger | 3.x | Interactive API documentation |

## 🖼️ Screenshots

### 🏠 Home Page
The welcoming home page with modern gradient design and easy navigation.

![Home Page](https://github.com/user-attachments/assets/home-page-preview.png)

*Features: Centered layout, gradient background, responsive design*

---

### 📅 Events Management
Comprehensive event management with search, create, update, and delete functionalities.

![Events Management](https://github.com/user-attachments/assets/events-management-preview.png)

*Features: Event listing, search functionality, status tracking, configuration status*

---

### ⚙️ Configuration Panel
Intuitive configuration interface for setting up ticket parameters.

![Configuration Panel](https://github.com/user-attachments/assets/configuration-panel-preview.png)

*Features: Event configuration, ticket limits, release rates, customer retrieval settings*

---

### 🎫 Ticket Management
Advanced ticket management system with real-time statistics and notifications.

![Ticket Management](https://github.com/user-attachments/assets/ticket-management-preview.png)

*Features: Real-time statistics, success notifications, ticket details, search functionality*

---

## 🚀 Getting Started

### 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** 17 or higher
- **Node.js** 20.9.0 or higher
- **Angular CLI** 18.2.9
- **Maven** 3.6 or higher
- **MySQL** 8.0 or higher
- **Git** for version control

### 🔧 Installation

#### 1️⃣ Clone the Repository
```bash
git clone https://github.com/Peenaka-official/Real-Time_Event_Ticketing_System.git
cd Real-Time_Event_Ticketing_System
```

#### 2️⃣ Database Setup
```sql
-- Create database
CREATE DATABASE ticketing_system;

-- Create user (optional)
CREATE USER 'ticketing_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON ticketing_system.* TO 'ticketing_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 3️⃣ Backend Setup
```bash
cd Backend
# Update application.properties with your database credentials
mvn clean install
mvn spring-boot:run
```
**Backend will run on:** `http://localhost:8080`

#### 4️⃣ Frontend Setup
```bash
cd Frontend
npm install
ng serve
```
**Frontend will run on:** `http://localhost:4200`

#### 5️⃣ CLI Setup
```bash
cd CLI
mvn clean install
java -jar target/Real-Time_Event_Ticketing_System_CLI-1.0-SNAPSHOT.jar
```

### 🎯 Quick Start Guide

1. **Start the Backend** → Navigate to `http://localhost:8080/swagger-ui.html` for API docs
2. **Launch the Frontend** → Open `http://localhost:4200` in your browser
3. **Register an Account** → Create vendor or customer account
4. **Create Events** → Use the events management panel
5. **Configure Tickets** → Set up ticket pools and rates
6. **Start Booking** → Experience real-time ticket booking!

## ⚙️ Configuration

### Backend Configuration (`application.properties`)
```properties
# Application Name
spring.application.name=Real-Time_Event_Ticketing_System

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/ticketing_system
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Server Configuration
server.port=8080

# Swagger Documentation
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/api-docs
```

### Frontend Configuration (`environment.ts`)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  wsUrl: 'ws://localhost:8080/ws'
};
```

## 🔧 Development

### 📁 Project Structure

```
Real-Time_Event_Ticketing_System/
├── 📱 Frontend/                    # Angular Web Application
│   ├── src/app/components/         # Reusable UI Components
│   ├── src/app/services/           # API and Business Logic Services
│   ├── src/app/models/             # TypeScript Data Models
│   └── src/environments/           # Environment Configurations
├── 🚀 Backend/                     # Spring Boot API
│   ├── src/main/java/              # Java Source Code
│   │   ├── controllers/            # REST API Controllers
│   │   ├── services/               # Business Logic Services
│   │   ├── repositories/           # Data Access Layer
│   │   ├── models/                 # JPA Entity Models
│   │   └── config/                 # Spring Configuration
│   └── src/main/resources/         # Application Resources
└── 💻 CLI/                         # Command Line Interface
    ├── src/main/java/              # Java CLI Source
    │   ├── cli/                    # CLI Implementation
    │   ├── config/                 # System Configuration
    │   └── ticket/                 # Ticket Processing Logic
    └── src/test/java/              # Unit Tests
```

### 🔨 Development Commands

#### Frontend Development
```bash
# Install dependencies
npm install

# Start development server
ng serve

# Build for production
ng build --configuration production

# Run tests
ng test

# Run e2e tests
ng e2e
```

#### Backend Development
```bash
# Run in development mode
mvn spring-boot:run

# Run with profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build JAR file
mvn clean package

# Run tests
mvn test

# Generate test coverage
mvn jacoco:report
```

#### CLI Development
```bash
# Compile and run
mvn clean compile exec:java

# Package executable JAR
mvn clean package

# Run tests
mvn test

# Run with custom config
java -jar target/CLI-1.0-SNAPSHOT.jar --config=custom-config.json
```

## 📚 API Documentation

### 🌐 REST API Endpoints

Our API follows RESTful principles and provides comprehensive documentation through Swagger UI.

**Base URL:** `http://localhost:8080/api`

#### 🔐 Authentication Endpoints
- `POST /auth/login` - User login
- `POST /auth/register/vendor` - Vendor registration
- `POST /auth/register/customer` - Customer registration
- `POST /auth/logout` - User logout

#### 📅 Event Management
- `GET /events` - Get all events
- `GET /events/{id}` - Get event by ID
- `POST /events` - Create new event
- `PUT /events/{id}` - Update event
- `DELETE /events/{id}` - Delete event

#### 🎫 Ticket Operations
- `GET /tickets` - Get all tickets
- `GET /tickets/stats/{eventId}` - Get ticket statistics
- `POST /tickets` - Create ticket booking
- `GET /tickets/{id}` - Get ticket details

#### ⚙️ Configuration Management
- `GET /config/{eventId}` - Get event configuration
- `POST /config` - Create configuration
- `PUT /config/{id}` - Update configuration
- `DELETE /config/{id}` - Delete configuration

### 📖 Interactive Documentation
Visit the **Swagger UI** for interactive API documentation:
- **Local:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **API Docs:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## 🧪 Testing

### Unit Testing
```bash
# Backend Tests
cd Backend
mvn test

# Frontend Tests
cd Frontend
npm test

# CLI Tests
cd CLI
mvn test
```

### Integration Testing
```bash
# Run all integration tests
mvn verify -P integration-tests
```

### Test Coverage
- **Backend:** JaCoCo reports available in `target/site/jacoco/`
- **Frontend:** Coverage reports in `coverage/`

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### 🌟 How to Contribute

1. **🍴 Fork the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/Real-Time_Event_Ticketing_System.git
   ```

2. **🌿 Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```

3. **✨ Make your changes**
   - Follow our coding standards
   - Add tests for new features
   - Update documentation

4. **💾 Commit your changes**
   ```bash
   git commit -m "✨ Add amazing feature"
   ```

5. **🚀 Push to your branch**
   ```bash
   git push origin feature/amazing-feature
   ```

6. **🔀 Create a Pull Request**

### 📝 Development Guidelines

- **Code Style:** Follow existing patterns and conventions
- **Testing:** Write tests for new features and bug fixes
- **Documentation:** Update README and inline documentation
- **Commit Messages:** Use conventional commit format

### 🐛 Reporting Issues

Found a bug? Please create an issue with:
- **Clear description** of the problem
- **Steps to reproduce** the issue
- **Expected behavior** vs actual behavior
- **Screenshots** if applicable
- **Environment details** (OS, browser, versions)

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Peenaka-official

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

## 👥 Contact

### 👨‍💻 Project Maintainer
- **GitHub:** [@Peenaka](https://github.com/Peenaka)
- **Email:** Contact via GitHub issues

### 🔗 Links
- **🌟 Repository:** [Real-Time Event Ticketing System](https://github.com/Peenaka/Real-Time_Event_Ticketing_System.git)
- **🚀 Live Demo:** [Demo Link](https://tinyurl.com/eventTicketingSystem24)
- **📚 Documentation:** [API Docs](http://localhost:8080/swagger-ui.html)
- **🐛 Issues:** [Report Issues](https://github.com/Peenaka/Real-Time_Event_Ticketing_System/issues)

---

<div align="center">

**⭐ Star this repository if you found it helpful!**

Made with ❤️ by [Peenaka](https://github.com/Peenaka)

</div>
