ByBud Backend Documentation
This document provides an overview of the ByBud backend, its structure, and how to run the individual services.

Overview
ByBud's backend is built as a microservices architecture. The backend contains the following services:

Auth Service: Handles authentication and authorization.
User Service: Manages user profiles and user-related data.
Delivery Service: Manages delivery-related operations and tracking.
Each service is a standalone Spring Boot application that communicates with the other services.

Prerequisites
Java: JDK 21 or higher is required.
Maven: Ensure Maven is installed and added to your system's PATH.
Database: The services use an in-memory H2 database for testing. For production, configure a proper database.
Git: Clone the repository to your local machine.
Repository Structure
The repository is organized as follows:

bybud/
├── auth-service/
├── user-service/
├── delivery-service/
├── common/ (shared libraries and configurations)
├── pom.xml (parent POM)
                        +-------------------+
                        |     COMMON        |
                        |-------------------|
                        | Models:           |
                        |   - User          |
                        |   - Role          |
                        |   - order         |
                        |   - CustomerReview|
                        |   - Delivery      |
                        |-------------------|
                        | Repositories:     |
                        |   - UserRepository|
                        |   - RoleRepository|
                        |-------------------|
                        | Shared Security:  |
                        |   - JwtTokenProvider|
                        |   - AuthTokenFilter|
                        +-------------------+
                                   |
      ----------------------------------------------------------
      |                         |                             |
+----------------+     +----------------+           +----------------+
| AUTH SERVICE   |     | USER SERVICE   |           | DELIVERY SERVICE|
|----------------|     |----------------|           |-----------------|
| Authentication |     | User Profiles  |           | Delivery        |
| - Login        |     | - Get Users    |           | Management      |
| - Register     |     | - Update Users |           | - Create Orders |
|----------------|     |----------------|           | - Track Orders  |
| Dependencies:  |     | Dependencies:  |           | Dependencies:   |
| - Common:      |     | - Common:      |           | - Common:       |
|   - Models     |     |   - Models     |           |   - Models      |
|   - Repos      |     |   - Repos      |           |   - Repos       |
|   - Security   |     |   - Security   |           |   - Security    |
+----------------+     +----------------+           +----------------+
                                   |
                            Shared Communication
                              (REST APIs)



Building the Project
Clone the repository:

code:
git clone https://github.com/nisjety/bybud.git
cd bybud
Build the project:

code:
mvn clean install
This will build all modules and install them into your local Maven repository.

Running the Services
Each service must be run with its respective profile. Use the following commands to start the services:

Auth Service
code:
java -Dspring.profiles.active=auth-service -jar target/auth-service-1.0.0.jar

User Service
code:
java -Dspring.profiles.active=user-service -jar target/user-service-1.0.0.jar

Delivery Service
code:
java -Dspring.profiles.active=delivery-service -jar target/delivery-service-1.0.0.jar

Endpoints
Auth Service
/auth/login – Login to the application.
/auth/register – Register a new user.
User Service
/users/{id} – Get user details by ID.
/users – List all users.
Delivery Service
/deliveries/{id} – Get delivery details by ID.
/deliveries – List all deliveries.

Debugging: Add the -Ddebug flag to enable debug logs:
eksempel code:
java -Dspring.profiles.active=delivery-service -Ddebug -jar target/delivery-service-1.0.0.jar

H2 Console: To view the in-memory H2 database, navigate to /h2-console while the service is running. The default credentials are:

JDBC URL: jdbc:h2:mem:{service_name}
Username: root
Password: password

Building Individual Modules
To build a specific module, navigate to the module's directory and run code:
mvn clean install
Example:
code:
cd delivery-service
mvn clean install

Contributions
For issues or feature requests, open a ticket on the repository: ByBud GitHub