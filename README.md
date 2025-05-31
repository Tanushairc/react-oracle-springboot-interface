# User Management System

A full-stack web application for managing users, built with Spring Boot backend and React frontend.

## Project Structure

```
user-management-system/
├── backend/                    # Spring Boot REST API
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/userapi/
│   │       │       ├── UserManagementApiApplication.java
│   │       │       ├── entity/User.java
│   │       │       ├── repository/UserRepository.java
│   │       │       ├── service/UserService.java
│   │       │       └── controller/UserController.java
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
├── frontend/                   # React Application
│   ├── src/
│   │   ├── App.js
│   │   ├── index.js
│   │   └── index.css
│   ├── public/
│   │   └── index.html
│   ├── package.json
│   └── tailwind.config.js
└── README.md
```

## Technology Stack

### Backend
- **Java 11**
- **Spring Boot 2.7.14**
- **Spring Data JPA** - Database operations
- **Spring Validation** - Input validation
- **Oracle Database** - Data persistence
- **Spring Boot Actuator** - Application monitoring

### Frontend
- **React 18.2.0**
- **Tailwind CSS 3.4.0** - Styling framework
- **Create React App** - Build tooling

## Getting Started

### Prerequisites
- Docker Desktop installed and running
- Java 11 or higher
- Node.js 16+ with npm

### Step 1: Setup Oracle Database with Docker

#### Start Oracle Database Container
```bash
# Remove any existing oracle container
docker stop oracle-free 2>/dev/null || true
docker rm oracle-free 2>/dev/null || true

# Start Oracle Database 23ai Free container
docker run --name oracle-free \
  -p 1521:1521 \
  -p 5500:5500 \
  -e ORACLE_PWD=MyPassword123 \
  -e ORACLE_CHARACTERSET=AL32UTF8 \
  -v oracle-data:/opt/oracle/oradata \
  -d container-registry.oracle.com/database/free:latest

# Monitor container startup
docker logs -f oracle-free

# Check container health (should show STATUS as "Up X minutes (healthy)")
docker ps
```

#### Create Database User
```bash
# Connect as SYS to Oracle container
docker exec -it oracle-free sqlplus / as sysdba
```

```sql
-- Switch to the pluggable database
ALTER SESSION SET CONTAINER = FREEPDB1;

-- Create application user
CREATE USER userapi IDENTIFIED BY userapi123;

-- Grant necessary privileges
GRANT CONNECT TO userapi;
GRANT RESOURCE TO userapi;
GRANT CREATE SESSION TO userapi;
GRANT CREATE TABLE TO userapi;
GRANT CREATE SEQUENCE TO userapi;
GRANT CREATE TRIGGER TO userapi;
GRANT CREATE VIEW TO userapi;
GRANT CREATE INDEX TO userapi;
GRANT UNLIMITED TABLESPACE TO userapi;

-- Verify user creation
SELECT username, account_status FROM dba_users WHERE username = 'USERAPI';

-- Exit SYS session
EXIT;
```

#### Create Database Schema
```bash
# Connect as application user
docker exec -it oracle-free sqlplus userapi/userapi123@//localhost:1521/FREEPDB1
```

```sql
-- Create sequence for auto-generating user IDs
CREATE SEQUENCE USER_SEQ
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- Create users table
CREATE TABLE users (
    id NUMBER(19,0) NOT NULL,
    name VARCHAR2(100) NOT NULL,
    email VARCHAR2(150) NOT NULL,
    phone VARCHAR2(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT chk_users_email CHECK (email LIKE '%@%'),
    CONSTRAINT chk_users_name CHECK (LENGTH(TRIM(name)) > 0)
);

-- Create performance indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_name ON users(name);
CREATE INDEX idx_users_created_at ON users(created_at);

-- Create trigger for automatic ID generation
CREATE OR REPLACE TRIGGER trg_users_id
    BEFORE INSERT ON users
    FOR EACH ROW
    WHEN (NEW.id IS NULL)
BEGIN
    SELECT USER_SEQ.NEXTVAL INTO :NEW.id FROM DUAL;
END;
/

-- Insert sample data
INSERT INTO users (name, email, phone) VALUES ('John Doe', 'john.doe@example.com', '1234567890');
INSERT INTO users (name, email, phone) VALUES ('Jane Smith', 'jane.smith@example.com', '0987654321');
INSERT INTO users (name, email, phone) VALUES ('Mike Johnson', 'mike.johnson@example.com', '5555555555');

-- Commit changes
COMMIT;

-- Verify data
SELECT * FROM users;

-- Exit
EXIT;
```

### Step 2: Start Backend Application

#### Terminal 1 - Backend
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```
Backend will be available at `http://localhost:8080`

### Step 3: Start Frontend Application

#### Terminal 2 - Frontend
```bash
cd frontend
npm install
npm start
```
Frontend will be available at `http://localhost:3000`