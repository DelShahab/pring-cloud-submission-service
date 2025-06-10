# Spring Cloud Agent Portal Service

A Spring Boot 3.x microservice that handles the Agent Portal submission flow using Spring Cloud OpenFeign for external API integrations.

## Features

- REST endpoint `/submission` to accept ACORD file upload requests
- API Key authentication via a custom filter
- Integration with Origami API for creating and updating submissions
- Integration with Roots.ai API for ACORD file parsing
- Notification system for Agent Portal
- Security with Spring Security
- Health checks with Spring Boot Actuator
- OpenAPI documentation

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Cloud OpenFeign
- Spring Security
- Lombok
- SpringDoc OpenAPI

## Project Structure

```
com.springcloud.agentportal
├── client        # Feign clients for external API integration
├── config        # Configuration classes
├── controller    # REST controllers
├── dto           # Data Transfer Objects
├── exception     # Exception handling
├── filter        # Security filters
├── service       # Business logic
└── AgentPortalServiceApplication.java  # Main application class
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Configuration

Configure your external APIs and security settings in `application.yml`. The following environment variables can be set:

- `ORIGAMI_API_KEY` - API key for Origami service
- `ROOTSAI_API_KEY` - API key for Roots.ai service
- `AGENT_PORTAL_API_KEY` - API key for Agent Portal
- `API_KEY` - API key for securing this service

### Running Locally

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run

# Or run the JAR file
java -jar target/agent-portal-service-1.0.0-SNAPSHOT.jar
```

### Endpoints

- **POST /submission** - Upload ACORD file for processing
  - Requires `X-API-KEY` header for authentication
  - Request parts:
    - `request`: JSON with emailId and userId
    - `file`: ACORD file

- **POST /notifyme/{userId}** - Receive notifications (simulated endpoint)
  - Open endpoint, no API key required

- **Actuator Endpoints**
  - `/actuator/health` - Health status
  - `/actuator/info` - Application info
  - `/actuator/metrics` - Application metrics

- **OpenAPI Documentation**
  - `/swagger-ui.html` - Swagger UI
  - `/api-docs` - OpenAPI JSON

## API Usage Example

### Submission Request

```bash
curl -X POST http://localhost:8080/submission \
  -H "X-API-KEY: your-secure-api-key" \
  -F "request={\"emailId\":\"agent@example.com\",\"userId\":\"agent123\",\"agentId\":\"A001\",\"clientName\":\"ACME Corp\"}" \
  -F "file=@/path/to/acordfile.xml"
```

## Deployment

The service is designed to be deployed in containerized environments like AWS ECS, Kubernetes, or similar platforms.

### Docker Build

```bash
# Build Docker image
docker build -t agent-portal-service:1.0.0 .
```

### Environment Variables

When deploying, set the following environment variables:

```
ORIGAMI_API_KEY=your-origami-key
ROOTSAI_API_KEY=your-rootsai-key
AGENT_PORTAL_API_KEY=your-agent-portal-key
API_KEY=your-service-api-key
```

## Future Enhancements

- Add circuit breaker patterns for external API calls using Resilience4j or Spring Cloud Circuit Breaker
- Implement retry mechanism for failed requests with exponential backoff
- Add distributed tracing with Spring Cloud Sleuth and Zipkin
- Implement API rate limiting and throttling
- Add caching layer for frequently accessed data
- Implement message queuing with Kafka or RabbitMQ for asynchronous processing
- Add comprehensive metrics collection with Micrometer and Prometheus
- Implement canary deployments and A/B testing capabilities
- Add support for blue-green deployments
- Enhance security with OAuth2/OpenID Connect integration
- Implement data validation and sanitization layers
- Add support for internationalization and localization
- Implement configurable data retention policies
- Integrate with Spring Cloud Config for centralized configuration management
- Implement service discovery with Spring Cloud Eureka or Netflix Eureka
- Add support for load balancing with Spring Cloud LoadBalancer
- Implement fault tolerance with Spring Cloud Hystrix
- Add support for API gateway with Spring Cloud Gateway
