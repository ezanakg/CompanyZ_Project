# CompanyZ Employee Management System - Group 29

A professional JavaFX-based employee management application with role-based access control, payroll management, and reporting capabilities.

## 🎯 Features

### Admin Dashboard
- **Employee Search** — Search employees by name or ID with real-time results
- **Bulk Salary Updates** — Apply percentage raises to employees within a salary range
- **Payroll Reports** — Generate summary reports by job title and division
- **Secure Access** — Role-based authentication and authorization

### Employee Self-Service
- **Pay History** — View personal payroll records and pay statements
- **Salary Information** — Access salary details securely
- **Logout** — Secure session termination

### System Features
- **Database Connectivity** — Integrated MySQL database with fallback demo mode
- **Offline Support** — Automatic fallback to mock data if database is unavailable
- **Clean Architecture** — Layered design following SOLID principles
- **Extensible Design** — Easy to add new features and repositories

## 🏗️ Architecture

The application follows a **6-layer architecture**:

```
┌─────────────────────────────────┐
│  UI Layer (JavaFX)              │
├─────────────────────────────────┤
│  Users Layer (Role Classes)     │
├─────────────────────────────────┤
│  Services Layer (Business Logic)│
├─────────────────────────────────┤
│  Repositories Layer (Data Access)
├─────────────────────────────────┤
│  Models Layer (DTOs)            │
├─────────────────────────────────┤
│  Database Layer (JDBC)          │
└─────────────────────────────────┘
```

**Key Design Patterns:**
- **Repository Pattern** — Abstract database access with interfaces
- **Dependency Injection** — Services injected at startup
- **Strategy Pattern** — Switch between real/mock repositories
- **Template Method** — Base `User` class with polymorphic subclasses
- **DTO Pattern** — Decouple layers from database schema

For detailed architecture explanation, see [CODE_WALKTHROUGH.md](CODE_WALKTHROUGH.md).

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (tested with Java 25)
- **MySQL Server** (optional — app works in demo mode without it)
- **Bash** (for `run.sh` script)

### Setup

#### 1. Clone or Extract the Project

#### 2. (Optional) Configure Database
If you have a MySQL database set up:

1. Edit `src/database/DBConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/employeeData";
   private static final String USER = "your_username";      // Change this
   private static final String PASSWORD = "your_password";  // Change this
   ```

2. Ensure the database `employeeData` exists with the required tables.

#### 3. Run the Application

**Option A: Using the provided script**
```bash
chmod +x run.sh
./run.sh
```

**Option B: Manual compilation and execution**
```bash
# Compile
javac -d bin src/**/*.java

# Run
java -cp bin src.AppLauncher
```

### Demo Login Credentials

If the database is unavailable, the app automatically switches to **demo mode** with mock data:

| Role     | Username | Password  |
|----------|----------|-----------|
| Admin    | admin    | admin123  |
| Employee | employee | emp123    |

## 📁 Project Structure

```
CompanyZ_Project/
├── src/
│   ├── AppLauncher.java              # Entry point
│   ├── MainApp.java                  # JavaFX UI
│   ├── database/
│   │   └── DBConnection.java         # JDBC connectivity
│   ├── models/                       # Data Transfer Objects
│   │   ├── Employee.java
│   │   ├── PayrollRecord.java
│   │   ├── EmployeeSearchResult.java
│   │   ├── Report.java
│   │   └── UserCredentials.java
│   ├── repositories/                 # Data Access Layer
│   │   ├── IAuthRepository.java
│   │   ├── IEmployeeRepository.java
│   │   ├── IPayrollRepository.java
│   │   ├── AuthRepository.java
│   │   ├── EmployeeRepository.java
│   │   ├── PayrollRepository.java
│   │   ├── MockAuthRepository.java
│   │   ├── MockEmployeeRepository.java
│   │   └── MockPayrollRepository.java
│   ├── services/                     # Business Logic
│   │   ├── AuthService.java
│   │   ├── EmployeeService.java
│   │   └── PayrollService.java
│   └── users/                        # Role-based Classes
│       ├── User.java
│       ├── AdminUser.java
│       └── EmployeeUser.java
├── bin/                              # Compiled classes
├── lib/                              # External libraries
├── CLASS_DIAGRAM.puml                # UML class diagram
├── CODE_WALKTHROUGH.md               # Detailed code explanation
├── ARCHITECTURE_DIAGRAMS.md          # Architecture documentation
├── README.md                         # This file
└── run.sh                            # Build and run script
```

## 🔑 Key Classes

### Main Application
- **AppLauncher** — Entry point; launches JavaFX application
- **MainApp** — Main UI controller; manages screens and user authentication

### Authentication & Users
- **AuthService** — Handles login validation and role checking
- **User** — Abstract base class for role-based access
- **AdminUser** — Administrator with full system access
- **EmployeeUser** — Regular employee with limited access

### Business Logic
- **EmployeeService** — Employee search and retrieval
- **PayrollService** — Salary, payroll, and reporting operations
- **AuthService** — User authentication and authorization

### Data Access
- **IAuthRepository** — Interface for authentication queries
- **IEmployeeRepository** — Interface for employee queries
- **IPayrollRepository** — Interface for payroll queries
- **AuthRepository, EmployeeRepository, PayrollRepository** — Real database implementations
- **MockAuthRepository, MockEmployeeRepository, MockPayrollRepository** — Demo implementations

### Models
- **Employee** — Employee information (ID, name)
- **PayrollRecord** — Individual pay record
- **EmployeeSearchResult** — Search result data
- **Report** — Aggregated report data
- **UserCredentials** — User login information

## 🔗 Data Flow Example

### Login Flow
```
1. User enters credentials in login screen
                ↓
2. MainApp.handleLogin() validates input
                ↓
3. AuthService.login() queries repository
                ↓
4. Repository (real or mock) validates credentials
                ↓
5. MainApp checks role via AuthService.isAdmin() or isEmployee()
                ↓
6. Appropriate dashboard displayed (AdminUser or EmployeeUser)
```

### Employee Search Flow (Admin)
```
1. Admin enters search term and clicks "Search"
                ↓
2. AdminUser.searchEmployees() calls EmployeeService
                ↓
3. EmployeeService.searchEmployees() queries repository
                ↓
4. Repository searches database (or mock data)
                ↓
5. Results formatted and displayed in UI
```

### Demo Mode (No Database Required)

If the database is unavailable:
- App automatically uses mock repositories
- Demo credentials are used: `admin/admin123`, `employee/emp123`
- Mock data is hardcoded in `MockAuthRepository`, `MockEmployeeRepository`, `MockPayrollRepository`

## 🧪 Testing

### Without Database (Recommended for Quick Testing)
```bash
./run.sh
# Use demo credentials: admin/admin123 or employee/emp123
```

### With Database
1. Ensure MySQL is running
2. Configure credentials in `DBConnection.java`
3. Run the app — it will automatically detect and use the real database

### Troubleshooting

| Issue | Solution |
|-------|----------|
| "Database connection failed" | Ensure MySQL is running; check credentials in `DBConnection.java` |
| "Mock repositories in use" | Database test failed; app is running in demo mode (normal if no DB) |
| "Invalid credentials" | Use demo credentials if database unavailable |
| Compilation errors | Ensure Java 17+ is installed; check file paths |

## 📚 Documentation

- **[CODE_WALKTHROUGH.md](CODE_WALKTHROUGH.md)** — Detailed code explanation with examples
- **[ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md)** — Architecture and design patterns
- **[CLASS_DIAGRAM.puml](CLASS_DIAGRAM.puml)** — UML class diagram (PlantUML format)


## 🔐 Security Notes

- Passwords are validated against the repository (mock or real)
- Session is managed per logged-in user
- Role-based access control prevents unauthorized operations
- Logout clears the current user session

## 📝 License

This project is part of the Fall 2025 Software Development course.

## 👨‍💻 Development

**Built with:**
- Java 17+
- JavaFX (UI framework)
- JDBC (database access)
- MySQL (optional database)

**Development Practices:**
- SOLID principles
- Layered architecture
- Dependency injection
- Repository pattern
- Clean code principles

---

**For detailed code walkthrough, see [CODE_WALKTHROUGH.md](CODE_WALKTHROUGH.md)**

**For architecture details, see [ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md)**
