# Code Walkthrough: CompanyZ Employee Management System

## Overview

This is a JavaFX-based Employee Management System built with a layered architecture. The application provides role-based access (Admin and Employee) with features for employee search, payroll management, and reporting.

---

## Architecture Layers (Top to Bottom)

### 1. **UI Layer** (`src/`)
The presentation layer using JavaFX.

**Files:**
- `AppLauncher.java` — Entry point that launches the application
- `MainApp.java` — Main JavaFX application class

**Key Responsibilities:**
- Renders the login screen, admin dashboard, and employee self-service dashboard
- Handles user input and button clicks
- Tests database connectivity on startup
- Delegates business logic to the Service layer

**Flow Example:**
```
User enters credentials → MainApp.handleLogin() 
  → calls AuthService.login() 
  → displays appropriate dashboard
```

---

### 2. **Users Layer** (`src/users/`)
Role-based user classes that represent logged-in users.

**Files:**
- `User.java` (abstract) — Base class for all user types
- `AdminUser.java` — Administrator with access to employee search, salary updates, and reports
- `EmployeeUser.java` — Regular employee with access to personal pay history

**Key Methods:**
- `AdminUser.searchEmployees(String)` — Search by name or ID
- `AdminUser.applySalaryRaise(min, max, percent)` — Bulk update salaries
- `AdminUser.getJobTitleReport()` — Generate payroll report by job title
- `AdminUser.getDivisionReport()` — Generate payroll report by division
- `EmployeeUser.viewPayHistory()` — Retrieve personal pay records
- `EmployeeUser.getSalaryInfo()` — Retrieve salary information

**Design Pattern:** Template Method (base `User` class) with polymorphic subclasses

---

### 3. **Services Layer** (`src/services/`)
Business logic and input validation.

**Files:**
- `AuthService.java` — Authentication and role validation
- `EmployeeService.java` — Employee search operations
- `PayrollService.java` — Payroll, salary, and report operations

**Key Responsibilities:**

#### AuthService
- `login(username, password)` — Validates credentials via repository
- `isAdmin(credentials)` — Checks if user is an administrator
- `isEmployee(credentials)` — Checks if user is an employee

#### EmployeeService
- `searchEmployees(searchTerm)` — Queries repository and formats results
- `formatSearchResults(results)` — Converts results to readable string

#### PayrollService
- `getPayHistory(empId)` — Retrieves employee's pay history
- `applySalaryRaise(min, max, percent)` — Validates input and updates salaries
- `getJobTitleReport()` — Aggregates payroll by job title
- `getDivisionReport()` — Aggregates payroll by division
- `formatPayHistory(records)` — Formats pay records as string
- `formatReport(reports, title)` — Formats report data as string

**Design Pattern:** Single Responsibility Principle (each service has one purpose)

---

### 4. **Models Layer** (`src/models/`)
Data Transfer Objects (DTOs) that carry data between layers.

**Files:**
- `Employee.java` — Employee information (ID, name)
- `PayrollRecord.java` — Individual pay record (employee ID, salary, date)
- `EmployeeSearchResult.java` — Search result (ID, name, salary)
- `Report.java` — Report data (category, total payment)
- `UserCredentials.java` — User login data (username, role, and role validation methods)

**Design Pattern:** DTO (decouples layers from database schema)

---

### 5. **Repositories Layer** (`src/repositories/`)
Data access abstraction using the Repository pattern.

#### Interfaces
- `IAuthRepository.java` — Authentication queries
- `IEmployeeRepository.java` — Employee search and retrieval
- `IPayrollRepository.java` — Payroll, salary, and report queries

#### Concrete Implementations
- `AuthRepository.java` — Real database queries for authentication
- `EmployeeRepository.java` — Real database queries for employee search
- `PayrollRepository.java` — Real database queries for payroll and reports

#### Mock Implementations (for testing without a database)
- `MockAuthRepository.java` — Demo credentials (admin/admin123, employee/emp123)
- `MockEmployeeRepository.java` — Hardcoded employee data
- `MockPayrollRepository.java` — Simulated payroll data

**Design Pattern:** Repository Pattern + Strategy Pattern (swap real/mock at runtime)

**Fallback Logic:**
```
MainApp.initializeServices():
  → Try DBConnection.testConnection()
  → If success: use real repositories
  → If fail: use mock repositories for demo mode
```

---

### 6. **Database Layer** (`src/database/`)
Centralized database connectivity.

**File:**
- `DBConnection.java` — JDBC connection management

**Key Methods:**
- `getConnection()` — Returns a MySQL connection with timeout
- `testConnection()` — Validates database accessibility (used by MainApp on startup)

**Configuration:**
```java
private static final String URL = "jdbc:mysql://localhost:3306/employeeData";
private static final String USER = "XXX";      // Configure with your credentials
private static final String PASSWORD = "XXX";  // Configure with your credentials
```

---

## Data Flow Example: Admin Login and Employee Search

```
1. User enters username/password → MainApp.handleLogin()

2. MainApp calls:
   → AuthService.login(username, password)
     → IAuthRepository.validateLogin()
       → (Real or Mock) queries database/demo data
       → Returns UserCredentials

3. MainApp checks role:
   → AuthService.isAdmin(credentials)
   → Creates AdminUser with services injected

4. AdminUser.searchEmployees(searchTerm) is called:
   → EmployeeService.searchEmployees(searchTerm)
     → IEmployeeRepository.searchEmployee(searchTerm)
       → (Real or Mock) queries database/demo data
       → Returns List<EmployeeSearchResult>
   → EmployeeService.formatSearchResults(results)
     → Converts to readable string
   → Returns formatted results to UI
   → MainApp displays results in TextArea
```

---

## Key Design Principles

### Dependency Injection (DI)
Services are injected into user role classes:
```java
// MainApp creates services with repositories
authService = new AuthService(authRepository);
employeeService = new EmployeeService(employeeRepository);
payrollService = new PayrollService(payrollRepository);

// User roles receive services
currentUser = new AdminUser(credentials, employeeService, payrollService);
```

### Loose Coupling
Services depend on interfaces, not concrete implementations:
```java
public class AuthService {
    private final IAuthRepository authRepository;  // Interface, not concrete class
    
    public AuthService(IAuthRepository authRepository) {
        this.authRepository = authRepository;
    }
}
```

### Single Responsibility
Each class has one reason to change:
- `AuthService` — Only if auth logic changes
- `EmployeeService` — Only if employee operations change
- `PayrollService` — Only if payroll logic changes

### Polymorphism
User roles can be treated uniformly:
```java
User currentUser;  // Could be AdminUser or EmployeeUser
currentUser = new AdminUser(...);  // Runtime decision
currentUser.logout();  // Works for any subclass
```

---

## Configuration & Runtime Setup

### Database Setup
1. Ensure MySQL Server is running
2. Create database: `employeeData`
3. Configure credentials in `src/database/DBConnection.java`:
   ```java
   private static final String USER = "your_username";
   private static final String PASSWORD = "your_password";
   ```

### Demo Mode
If database is unavailable, the app automatically uses mock repositories with demo credentials:
- **Admin:** username `admin`, password `admin123`
- **Employee:** username `employee`, password `emp123`

### Compilation & Execution
```bash
# Compile
javac -d bin src/**/*.java

# Run
java -cp bin src.AppLauncher
```

Or use the provided `run.sh` script:
```bash
./run.sh
```

---

## File Structure Summary

```
src/
├── AppLauncher.java           # Entry point
├── MainApp.java               # JavaFX UI
├── models/                    # DTOs
│   ├── Employee.java
│   ├── PayrollRecord.java
│   ├── EmployeeSearchResult.java
│   ├── Report.java
│   └── UserCredentials.java
├── services/                  # Business logic
│   ├── AuthService.java
│   ├── EmployeeService.java
│   └── PayrollService.java
├── repositories/              # Data access
│   ├── IAuthRepository.java
│   ├── IEmployeeRepository.java
│   ├── IPayrollRepository.java
│   ├── AuthRepository.java
│   ├── EmployeeRepository.java
│   ├── PayrollRepository.java
│   ├── MockAuthRepository.java
│   ├── MockEmployeeRepository.java
│   └── MockPayrollRepository.java
├── users/                     # Role classes
│   ├── User.java
│   ├── AdminUser.java
│   └── EmployeeUser.java
└── database/                  # Connectivity
    └── DBConnection.java
```

---

## Extending the System

### Adding a New Feature (e.g., Employee Report Generation)

1. **Add to Models** (`src/models/`)
   ```java
   public class EmployeeReport {
       private String employeeName;
       private List<PayrollRecord> records;
       // getters/setters
   }
   ```

2. **Add to Repository Interface** (`src/repositories/IPayrollRepository.java`)
   ```java
   List<EmployeeReport> getEmployeeReports(int empId);
   ```

3. **Implement in Concrete Repository** (`src/repositories/PayrollRepository.java`)
   ```java
   public List<EmployeeReport> getEmployeeReports(int empId) {
       // SQL query or logic
   }
   ```

4. **Add to Service** (`src/services/PayrollService.java`)
   ```java
   public List<EmployeeReport> getEmployeeReports(int empId) {
       return payrollRepository.getEmployeeReports(empId);
   }
   ```

5. **Add UI Button in MainApp** and call service method

---

## Testing & Debugging

### Without Database
Run the app without a database connection—it automatically falls back to mock repositories. This is useful for:
- UI testing without database setup
- Demo purposes
- Development without MySQL

### With Database
Ensure `DBConnection.testConnection()` returns `true`, and the app will use real repositories.

### Common Issues
- **"Database connection failed"**: Check MySQL is running and credentials are correct
- **"Mock repositories in use"**: Database test failed; check logs or use demo credentials
- **"Invalid credentials"**: Verify username/password in auth repository (real or mock)

---

## Next Steps for Enhancement

1. Add unit tests using JUnit 5
2. Implement connection pooling (HikariCP) in `DBConnection`
3. Add transaction management for multi-step operations
4. Implement pagination for large employee search results
5. Add audit logging for admin actions
6. Create CSV export for reports

