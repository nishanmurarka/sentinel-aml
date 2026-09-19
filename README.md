# Sentinel AML

Sentinel AML is a real-time anti-money laundering (AML) detection system. It ingests financial transactions, analyzes them against a suite of detection rules, and generates alerts for suspicious activities.

## Architecture

The system is built with a modern Java stack designed for high throughput and reliability:

- **Backend Framework:** Java 17 with Spring Boot.
- **Event Streaming:** Apache Kafka for real-time transaction ingestion and asynchronous processing.
- **Database:** PostgreSQL for persistent storage of transactions, accounts, and alerts.
- **Data Access:** Spring Data JPA with Hibernate.
- **Database Migrations:** Flyway for version-controlled database schema management.
- **Frontend/UI:** Thymeleaf templates for case management and alert visualization.

### Flow
1. Transactions are ingested from a Kafka topic (`transactions-topic-0`).
2. A Kafka consumer reads the transactions and passes them to the Rule Engine.
3. The Rule Engine evaluates each transaction against all active `DetectionRule` components.
4. If a rule is triggered, an `Alert` is generated and saved to PostgreSQL.
5. Compliance officers can view and manage these alerts via the web UI.

## Setup Instructions

### Prerequisites
- Docker and Docker Compose
- Java 17 (JDK)
- Maven 3.8+

### 1. Start Infrastructure
The application relies on PostgreSQL and Kafka (with Zookeeper). You can start these services using the provided `docker-compose.yml` file:

```bash
docker-compose up -d
```
*This will start PostgreSQL on port 5432, Zookeeper on port 2181, and Kafka on port 9092.*

### 2. Build the Application
Compile the application and run unit tests to ensure everything is working:

```bash
mvn clean install
```

### 3. Run the Application
Start the Spring Boot backend:

```bash
mvn spring-boot:run
```
*The application will automatically apply database migrations via Flyway on startup.*

### 4. Access the Application
The web interface is available at `http://localhost:8080/`. You can view the OpenAPI/Swagger documentation at `http://localhost:8080/swagger-ui/index.html` (if configured).

## Rule Configuration Approach

Sentinel AML uses a modular, code-based rule engine.

### How Rules Work
All detection rules implement the `DetectionRule` interface and are annotated with Spring's `@Component`. This allows the application to automatically discover and inject all available rules into the evaluation engine.

```java
public interface DetectionRule {
    String getRuleName();
    Optional<Alert> evaluate(Transaction transaction);
}
```

### Adding a New Rule
To add a new rule:
1. Create a new class in the `com.meridiantrust.sentinel.rule` package.
2. Implement the `DetectionRule` interface.
3. Annotate the class with `@Component`.
4. Implement your logic in the `evaluate(Transaction transaction)` method. If the rule condition is met, return an `Optional.of(new Alert(...))`. Otherwise, return `Optional.empty()`.

Because rules are Spring components, you can easily inject repositories (e.g., `TransactionRepository`) to perform stateful evaluations, such as checking historical transaction data for structuring or rapid movement of funds.

## Enhancements

The platform is built with several powerful extensions for both technical and business users:

- **Swagger / OpenAPI Documentation:** The application integrates `springdoc-openapi` for automatic API documentation. When the application is running, you can explore and test the REST endpoints via the Swagger UI available at `http://localhost:8080/swagger-ui/index.html`.
- **Network Graph Visualization (Planned):** A visual network graph feature is in the roadmap. This will allow compliance analysts to view linked accounts, transaction flows, and identify complex laundering schemes such as layering rings or shell company networks.
- **Automated SAR Generation (Planned):** An upcoming feature that will automatically generate Suspicious Activity Report (SAR) drafts. It will summarize alert evidence in a narrative form ready for FIU/regulatory filing, saving analysts significant time.
