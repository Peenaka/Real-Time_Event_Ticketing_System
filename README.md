
# Project Title

A brief description of what this project does and who it's for

# Real-Time Event Ticketing System

Welcome to the **Real-Time Event Ticketing System**! This project is designed to manage and facilitate event ticketing operations with a real-time approach. The repository contains three main components: CLI, Backend, and Frontend, each dedicated to specific functionalities of the system.

---

## Repository Structure

```
Real-Time_Event_Ticketing_System
├── CLI
├── Backend
└── Frontend
```

### 1. CLI
The Command Line Interface (CLI) handles configuration, command execution, and real-time monitoring.

#### Key Features:
- Developed using Java.
- Implements the Producer-Consumer pattern for real-time monitoring.

#### Dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.18.0</version>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.11.0</version>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>RELEASE</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

#### Requirements:
- **JDK Version:** 17
- **Build Tool:** Maven

---

### 2. Backend
The backend is built with **Spring Boot** to manage data operations, business logic, and APIs.

#### Key Features:
- RESTful API Development.
- Data persistence with MySQL.
- Secure endpoints using Spring Security.
- Liquibase for database version control.
- Integrated Swagger for API documentation.

#### Configuration:
**application.properties:**
```properties
spring.application.name=Real-Time_Event_Ticketing_System
spring.datasource.url=jdbc:mysql://localhost:3306/ticketing_system
spring.datasource.username=root
spring.datasource.password=Ev@123#tick
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
server.port=8080
springdoc.swagger-ui.path=/swagger-ui.html
```

#### Dependencies:
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
</dependencies>
```

#### API Documentation:
- **Swagger UI Link:** [Swagger UI](http://localhost:8080/ticket-system/swagger-ui.html)

---

### 3. Frontend
The frontend is developed with **Angular** to provide an interactive user interface for end-users.

#### Key Features:
- Developed using Angular CLI version 18.2.9.
- Implements TypeScript for clean and scalable development.

#### Configuration:
- **Node Version:** 20.9.0
- **Package Manager:** npm 10.5.0

#### Dependencies:
| Package                  | Version  |
|--------------------------|----------|
| @angular-devkit/architect| 0.1802.9 |
| @angular-devkit/core     | 18.2.9   |
| @angular-devkit/schematics | 18.2.9 |
| @schematics/angular      | 18.2.9   |

---

## Getting Started

### Prerequisites
1. **Java Development Kit (JDK):** Version 17
2. **Node.js:** Version 20.9.0
3. **Angular CLI:** Version 18.2.9
4. **Maven:** For building CLI and Backend components.

### Setup Instructions

#### 1. Clone the Repository
```bash
git clone https://github.com/Peenaka-official/Real-Time_Event_Ticketing_System.git
cd Real-Time_Event_Ticketing_System
```

#### 2. Setting up CLI
```bash
cd CLI
mvn clean install
java -jar target/Real-Time_Event_Ticketing_System_CLI-1.0-SNAPSHOT.jar
```

#### 3. Setting up Backend
```bash
cd Backend
mvn spring-boot:run
```

#### 4. Setting up Frontend
```bash
cd Frontend
npm install
ng serve
```

---

## Contributing
Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a new branch for your feature (`git checkout -b feature-name`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature-name`).
5. Open a pull request.

---

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Contact
For questions or feedback, please contact the repository owner at [Peenaka-official](https://github.com/Peenaka-official).

---


## Demo

https://tinyurl.com/eventTicketingSystem24