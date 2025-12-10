# S.O.L.I.D Quick Reference Card

## The Five Principles

### 1. Single Responsibility Principle (SRP)
**One reason to change**

```java
// ❌ BAD - Multiple responsibilities
public class EmployeeDAO {
    public authenticate() { }
    public search() { }
    public updateSalary() { }
}

// ✅ GOOD - One responsibility each
public class AuthRepository { public authenticate() { } }
public class EmployeeRepository { public search() { } }
public class PayrollRepository { public updateSalary() { } }
```

---

### 2. Open/Closed Principle (OCP)
**Open for extension, closed for modification**

```java
// ❌ BAD - Must modify existing class to add new role
if (role.equals("ADMIN")) { ... }
else if (role.equals("EMPLOYEE")) { ... }
else if (role.equals("MANAGER")) { ... }  // Had to modify!

// ✅ GOOD - Extend without modifying
public abstract class User { }
public class AdminUser extends User { }
public class EmployeeUser extends User { }
public class ManagerUser extends User { }  // Just add new class!
```

---

### 3. Liskov Substitution Principle (LSP)
**Subtypes must be substitutable for base type**

```java
// ❌ BAD - Subtypes don't honor contract
User user = (role == "admin") ? new AdminUser() : null;
user.logout();  // NullPointerException!

// ✅ GOOD - All subtypes work interchangeably
User user = createUser(role);  // Always returns valid User
user.logout();  // Safe for all user types
```

---

### 4. Interface Segregation Principle (ISP)
**Clients depend only on what they use**

```java
// ❌ BAD - One fat interface
public interface IEmployeeDAO {
    void authenticate();
    List<Employee> search();
    void updateSalary();
    void getPayHistory();
    // And 10 more methods...
    // Every service needs ALL methods!
}

// ✅ GOOD - Segregated interfaces
public interface IAuthRepository {
    void authenticate();
}
public interface IEmployeeRepository {
    List<Employee> search();
}
public interface IPayrollRepository {
    void updateSalary();
    void getPayHistory();
}
// Each service uses only what it needs!
```

---

### 5. Dependency Inversion Principle (DIP)
**Depend on abstractions, not concretions**

```java
// ❌ BAD - Depends on concrete class
public class AuthService {
    private AuthRepository repo = new AuthRepository();  // Concrete!
}

// ✅ GOOD - Depends on interface
public class AuthService {
    private IAuthRepository repo;
    public AuthService(IAuthRepository repo) {  // Abstraction!
        this.repo = repo;
    }
}

// Easy to swap
authService = new AuthService(new AuthRepository());        // MySQL
authService = new AuthService(new PostgresAuthRepository()); // PostgreSQL
```

---

## Your Project's Layers

```
┌─────────────────────────────────┐
│   Presentation Layer            │
│   (MainApp.java)                │
│   - UI Display                  │
│   - Event Handling              │
└──────────────┬──────────────────┘
               │ depends on
┌──────────────▼──────────────────┐
│   User Role Layer               │
│   (User, AdminUser, Employee)   │
│   - Role-specific Logic         │
│   - Polymorphic Behavior        │
└──────────────┬──────────────────┘
               │ depends on
┌──────────────▼──────────────────┐
│   Service Layer                 │
│   (AuthService, Employee,       │
│    PayrollService)              │
│   - Business Logic              │
│   - Validation                  │
│   - Coordination                │
└──────────────┬──────────────────┘
               │ depends on
┌──────────────▼──────────────────┐
│   Repository Layer (Interfaces) │
│   (IAuth/Employee/Payroll)      │
│   - Data Access Contract        │
└──────────────┬──────────────────┘
               │ implements
┌──────────────▼──────────────────┐
│   Repository Layer (Concrete)   │
│   (Auth/Employee/Payroll        │
│    Repository)                  │
│   - SQL Queries                 │
│   - Database Operations         │
└──────────────┬──────────────────┘
               │ uses
┌──────────────▼──────────────────┐
│   Database Layer                │
│   (DBConnection)                │
│   - Connection Management       │
│   - SQL Execution               │
└─────────────────────────────────┘
```

---

## Dependency Rules

**Golden Rule: Depend only on abstraction, never on concrete implementation**

```
UI → Services (depend on IRepository interfaces)
Services → Repositories (implement interfaces)
Repositories → Database
Database ← Repositories
Repositories ← Services
Services ← UI

NO BACKWARDS DEPENDENCIES!
Database should NEVER call Services!
```

---

## How to Extend

### Adding a New User Role

```java
// 1. Create new role class
public class ManagerUser extends User {
    private final EmployeeService empService;
    private final PayrollService payrollService;
    
    public ManagerUser(UserCredentials creds, 
                      EmployeeService empService,
                      PayrollService payrollService) {
        super(creds);
        this.empService = empService;
        this.payrollService = payrollService;
    }
    
    @Override
    public void displayDashboard() {
        System.out.println("Manager Dashboard");
    }
    
    // Manager-specific features
    public String approveRaises() { ... }
}

// 2. Update MainApp initialization
private void handleLogin(String user, String pass, Label status) {
    UserCredentials creds = authService.login(user, pass);
    
    if (authService.isAdmin(creds)) {
        currentUser = new AdminUser(creds, empService, payrollService);
    } else if ("MANAGER".equalsIgnoreCase(creds.getRole())) {
        currentUser = new ManagerUser(creds, empService, payrollService);
    } else if (authService.isEmployee(creds)) {
        currentUser = new EmployeeUser(creds, payrollService);
    }
}

// That's it! No existing code needs to change!
```

### Adding a New Database Operation

```java
// 1. Add to interface
public interface IPayrollRepository {
    List<PayrollRecord> getPayHistory(int empId);
    List<Report> getTotalPayByJobTitle();
    // NEW METHOD
    List<PayslipRecord> generatePayslips(int monthYear);
}

// 2. Implement in concrete class
public class PayrollRepository implements IPayrollRepository {
    @Override
    public List<PayslipRecord> generatePayslips(int monthYear) {
        String query = "SELECT * FROM payslips WHERE month_year = ?";
        // Execute and map to objects
        return results;
    }
}

// 3. Add to service
public class PayrollService {
    public List<PayslipRecord> generatePayslips(int monthYear) {
        return payrollRepository.generatePayslips(monthYear);
    }
}

// 4. Use in UI or user roles
public class AdminUser extends User {
    public String viewPayslips(int monthYear) {
        var payslips = payrollService.generatePayslips(monthYear);
        return formatPayslips(payslips);
    }
}
```

---

## Testing Pattern

```java
// Mock the repository
IPayrollRepository mockRepo = mock(IPayrollRepository.class);
when(mockRepo.getPayHistory(1))
    .thenReturn(List.of(
        new PayrollRecord(1, 50000, LocalDate.now()),
        new PayrollRecord(1, 52000, LocalDate.now().minusMonths(1))
    ));

// Inject into service
PayrollService service = new PayrollService(mockRepo);

// Test
List<PayrollRecord> history = service.getPayHistory(1);
assertEquals(2, history.size());
assertEquals(50000, history.get(0).getSalary(), 0.01);

// No database needed!
```

---

## Design Patterns Used

| Pattern | Location | Purpose |
|---------|----------|---------|
| **Dependency Injection** | Services | Loose coupling, testability |
| **Repository** | Data Access Layer | Abstract database operations |
| **Strategy** | User Roles | Different behaviors for different types |
| **Template Method** | User abstract class | Define skeleton, let subclasses fill in |
| **DTO** | Models | Clean data transfer between layers |
| **Singleton** | DBConnection | Single instance of connection |
| **Factory** | (could add) | Create user instances based on role |
| **Builder** | (could add) | Complex object creation |

---

## Key Takeaways

✅ **Each class has one job**
✅ **Depend on interfaces, not concretions**
✅ **Easy to test with mocked dependencies**
✅ **Easy to extend with new features**
✅ **Easy to maintain and modify**
✅ **Professional, production-ready code**

---

## Common Mistakes to Avoid

❌ **Don't**: Create dependencies inside classes
```java
public class Service {
    private Repo repo = new Repo();  // TIGHT COUPLING!
}
```

✅ **Do**: Inject dependencies
```java
public class Service {
    private IRepo repo;
    public Service(IRepo repo) { this.repo = repo; }
}
```

---

❌ **Don't**: Use ResultSet in services/UI
```java
ResultSet rs = getEmployees();
while(rs.next()) { ... }  // Manual parsing, error-prone
```

✅ **Do**: Use typed objects
```java
List<Employee> employees = getEmployees();
for (Employee e : employees) { ... }  // Type-safe, clean
```

---

❌ **Don't**: One interface for everything
```java
public interface IDAO {
    void search();
    void update();
    void delete();
    void authenticate();
    // Everything!
}
```

✅ **Do**: Segregate interfaces
```java
public interface ISearchRepository { void search(); }
public interface IUpdateRepository { void update(); }
public interface IAuthRepository { void authenticate(); }
// Each interface has one job!
```

---

## Resources for Learning

- **S.O.L.I.D Principles**: https://en.wikipedia.org/wiki/SOLID
- **Clean Code**: Robert C. Martin book
- **Design Patterns**: Gang of Four classic
- **Dependency Injection**: Spring Framework docs
- **Repository Pattern**: Microsoft Architecture Guide

---

## File Organization Cheat Sheet

```
src/
├── models/           → Data Transfer Objects (Employee, PayrollRecord, etc)
├── repositories/     → Data Access Layer (interfaces + implementations)
├── services/         → Business Logic (AuthService, EmployeeService, etc)
├── users/            → Role-based User Hierarchy (User, AdminUser, etc)
├── database/         → Database Connectivity (DBConnection)
├── MainApp.java      → Entry point, UI
└── AppLauncher.java  → Launcher wrapper
```

**Remember**: Code flows top-to-bottom, but dependencies flow bottom-to-top!
