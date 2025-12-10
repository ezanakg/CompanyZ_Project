# Migration Guide: From Old to Refactored Code

## Quick Reference

### Old Code Pattern (Before)
```java
// MainApp.java - Everything directly coupled
private EmployeeDAO dao = new EmployeeDAO();

LoginBtn.setOnAction(e -> {
    String role = dao.validateLogin(user, pass);  // Direct DAO call
    if ("ADMIN".equalsIgnoreCase(role)) showAdmin();
});

searchBtn.setOnAction(e -> {
    ResultSet rs = dao.searchEmployee(term);  // Direct DAO call, returns ResultSet
    while(rs != null && rs.next()) { ... }
});
```

### New Code Pattern (After)
```java
// MainApp.java - Dependency injection, service layer
private AuthService authService;
private EmployeeService employeeService;

// Initialize in start()
initializeServices();

handleLogin(user, pass) {
    UserCredentials creds = authService.login(user, pass);  // Service call
    if (authService.isAdmin(creds)) { ... }
}

searchBtn.setOnAction(e -> {
    String results = admin.searchEmployees(term);  // User role method
    output.setText(results);  // String, not ResultSet
});
```

---

## Detailed Class Mapping

### Authentication

| Old | New | Change |
|-----|-----|--------|
| `EmployeeDAO.validateLogin()` | `IAuthRepository.validateLogin()` → `AuthRepository` | Extracted to dedicated repository |
| Returns `String` (role) | Returns `UserCredentials` object | Encapsulates credentials |
| Direct call from MainApp | Called via `AuthService` | Added service layer |
| — | `AuthService.isAdmin()`, `.isEmployee()` | Added helper methods |

### Employee Search

| Old | New | Change |
|-----|-----|--------|
| `EmployeeDAO.searchEmployee()` | `IEmployeeRepository.searchEmployee()` → `EmployeeRepository` | Extracted to dedicated repository |
| Returns `ResultSet` | Returns `List<EmployeeSearchResult>` | Typed collection, not ResultSet |
| Direct call from MainApp | Via `EmployeeService` → `AdminUser` | Added service & role layers |
| Manual parsing in UI | `EmployeeService.formatSearchResults()` | Formatting centralized in service |

### Payroll Operations

| Old | New | Change |
|-----|-----|--------|
| `EmployeeDAO.updateSalaryRange()` | `IPayrollRepository.updateSalaryRange()` → `PayrollRepository` | Extracted to dedicated repository |
| Direct call from MainApp | Via `PayrollService` → `AdminUser` | Added service & role layers |
| `EmployeeDAO.getPayHistory()` | `IPayrollRepository.getPayHistory()` → returns `List<PayrollRecord>` | Returns typed list |
| `EmployeeDAO.getTotalPayByJobTitle()` | `IPayrollRepository.getTotalPayByJobTitle()` → returns `List<Report>` | Returns typed list |
| `EmployeeDAO.getTotalPayByDivision()` | `IPayrollRepository.getTotalPayByDivision()` → returns `List<Report>` | Returns typed list |

---

## Layer-by-Layer Breakdown

### Presentation Layer (MainApp.java)

**Old:**
```java
private EmployeeDAO dao = new EmployeeDAO();
public void start() { showLogin(); }
// Handles UI AND business logic
```

**New:**
```java
private AuthService authService;
private EmployeeService employeeService;
private PayrollService payrollService;
private User currentUser;

public void start() {
    initializeServices();  // Dependency injection
    showLogin();           // UI only
}
```

**Improvement**: UI now only handles presentation; services handle logic.

---

### User Role Layer (New!)

**Old:** No role abstraction
```java
if ("ADMIN".equalsIgnoreCase(role)) {
    // Admin stuff
    dao.searchEmployee();
    dao.updateSalaryRange();
} else if ("EMPLOYEE".equalsIgnoreCase(role)) {
    // Employee stuff
    dao.getPayHistory();
}
```

**New:** Polymorphic user types
```java
if (authService.isAdmin(credentials)) {
    currentUser = new AdminUser(credentials, employeeService, payrollService);
} else if (authService.isEmployee(credentials)) {
    currentUser = new EmployeeUser(credentials, payrollService);
}

// Same code handles all user types!
currentUser.logout();  // Polymorphic call
```

**Improvement**: Each role encapsulates its own behavior; UI is simpler.

---

### Service Layer (New!)

**Old:** No service layer
```java
// Business logic scattered in MainApp
try {
    int count = dao.updateSalaryRange(...);
    msg.setText("Updated " + count);
} catch(Exception e) { msg.setText("Error"); }
```

**New:** Services handle logic
```java
// AuthService
public UserCredentials login(String user, String pass) {
    if (user == null || pass == null) return null;
    return authRepository.validateLogin(user, pass);
}

// EmployeeService
public List<EmployeeSearchResult> searchEmployees(String term) {
    if (term == null || term.isEmpty()) return List.of();
    return employeeRepository.searchEmployee(term);
}

// PayrollService
public int applySalaryRaise(double min, double max, double pct) {
    if (min < 0 || max < 0 || min > max) 
        throw new IllegalArgumentException(...);
    return payrollRepository.updateSalaryRange(min, max, pct);
}
```

**Improvement**: Business logic centralized, reusable, testable.

---

### Repository Layer (Refactored)

**Old:** One monolithic DAO class
```java
public class EmployeeDAO {
    // 6 different features mixed together
    public String validateLogin(...) { }
    public ResultSet searchEmployee(...) { }
    public int updateSalaryRange(...) { }
    public ResultSet getPayHistory(...) { }
    public ResultSet getTotalPayByJobTitle(...) { }
    public ResultSet getTotalPayByDivision(...) { }
}
```

**New:** Three focused repositories with interfaces
```java
// IAuthRepository
public interface IAuthRepository {
    UserCredentials validateLogin(String username, String password);
}

// IEmployeeRepository
public interface IEmployeeRepository {
    List<EmployeeSearchResult> searchEmployee(String searchTerm);
    Employee getEmployeeById(int empId);
}

// IPayrollRepository
public interface IPayrollRepository {
    List<PayrollRecord> getPayHistory(int empId);
    int updateSalaryRange(double min, double max, double pct);
    List<Report> getTotalPayByJobTitle();
    List<Report> getTotalPayByDivision();
}

// Plus three implementations
public class AuthRepository implements IAuthRepository { }
public class EmployeeRepository implements IEmployeeRepository { }
public class PayrollRepository implements IPayrollRepository { }
```

**Improvement**: 
- Clear separation of concerns
- Each interface segregated for specific use case
- Easy to mock for testing
- Easy to swap implementations

---

### Data Models (New!)

**Old:** Raw ResultSet objects
```java
ResultSet rs = dao.searchEmployee(term);
while(rs.next()) {
    String name = rs.getString("name");
    double salary = rs.getDouble("salary");
}
```

**New:** Typed objects
```java
// Model classes encapsulate data
public class Employee {
    private int empId;
    private String name;
    private int jobTitleId;
    private int divisionId;
    // Getters/setters
}

public class PayrollRecord {
    private int empId;
    private double salary;
    private LocalDate payDate;
}

public class Report {
    private String category;
    private double totalPayment;
}

// Usage
List<EmployeeSearchResult> results = employeeService.searchEmployees(term);
for (EmployeeSearchResult result : results) {
    System.out.println(result.getName());  // Type-safe, IDE autocomplete
}
```

**Improvement**: Type-safe, self-documenting, easier to work with.

---

## Common Migration Tasks

### Task 1: Adding a New Admin Feature

**Old Approach:**
```java
// 1. Add method to EmployeeDAO
public class EmployeeDAO {
    public ResultSet getAnnualReview() { ... }
}

// 2. Call directly from MainApp
public void showAdmin() {
    button.setOnAction(e -> {
        ResultSet rs = dao.getAnnualReview();
        // Parse manually
    });
}
```

**New Approach:**
```java
// 1. Add to IPayrollRepository
public interface IPayrollRepository {
    List<Review> getAnnualReview();
}

// 2. Implement in PayrollRepository
public class PayrollRepository implements IPayrollRepository {
    public List<Review> getAnnualReview() { ... }
}

// 3. Add service method in PayrollService
public List<Review> getAnnualReview() { 
    return payrollRepository.getAnnualReview();
}

// 4. Add method to AdminUser
public class AdminUser extends User {
    public String viewAnnualReview() {
        var reviews = payrollService.getAnnualReview();
        return payrollService.formatReviews(reviews);
    }
}

// 5. Call from MainApp
reviewBtn.setOnAction(e -> {
    String reviews = admin.viewAnnualReview();
    outputArea.setText(reviews);
});
```

**Key Difference**: Each layer added in proper order; no ResultSet parsing in UI.

---

### Task 2: Swapping Database Implementation (e.g., PostgreSQL)

**Old Approach:**
```java
// Would need to change EmployeeDAO, search for all hardcoded SQL
public class EmployeeDAO {
    public ResultSet searchEmployee(String term) {
        String query = "SELECT ... FROM employees WHERE ...";  // MySQL syntax
        // Change to PostgreSQL syntax everywhere
    }
}
```

**New Approach:**
```java
// 1. Create new PostgreSQL-specific repository
public class PostgresEmployeeRepository implements IEmployeeRepository {
    public List<EmployeeSearchResult> searchEmployee(String term) {
        String query = "SELECT ... FROM employees WHERE ...";  // PostgreSQL syntax
        // Implementation
    }
}

// 2. Swap in initialization (one place!)
private void initializeServices() {
    IEmployeeRepository repo = new PostgresEmployeeRepository();  // Changed!
    employeeService = new EmployeeService(repo);
}

// 3. No other code changes needed!
```

**Key Benefit**: Only change one line; rest of system unaffected.

---

### Task 3: Writing Unit Tests

**Old Approach:** Can't easily test
```java
// Hard to test - mixed concerns, direct DB calls
public void testSearch() {
    EmployeeDAO dao = new EmployeeDAO();  // Real DB!
    ResultSet rs = dao.searchEmployee("test");
    // Brittle - depends on actual database
}
```

**New Approach:** Easy to test with mocks
```java
// Mock the repository
IEmployeeRepository mockRepo = mock(IEmployeeRepository.class);
when(mockRepo.searchEmployee("test"))
    .thenReturn(List.of(new EmployeeSearchResult(1, "John", 50000)));

// Inject into service
EmployeeService service = new EmployeeService(mockRepo);

// Test
List<EmployeeSearchResult> results = service.searchEmployees("test");
assertEquals(1, results.size());
assertEquals("John", results.get(0).getName());
// No database needed!
```

---

## Performance Considerations

### ResultSet vs List

**Old:**
```java
ResultSet rs = dao.searchEmployee("test");  // Lazy - data fetched as needed
while(rs.next()) { ... }
```

**New:**
```java
List<EmployeeSearchResult> results = repo.searchEmployee("test");  // Eager - all loaded
for (EmployeeSearchResult r : results) { ... }
```

**Trade-off**: 
- Old: Lower memory for large result sets
- New: Simpler code, better for small/medium result sets

**Optimization**: For large datasets, consider pagination or streaming.

---

## Troubleshooting Migration Issues

### Issue: "Cannot find symbol" errors

**Solution**: Ensure imports are correct
```java
import src.repositories.IEmployeeRepository;
import src.services.EmployeeService;
import src.users.AdminUser;
```

### Issue: "NullPointerException" in services

**Solution**: Check if credentials null in login
```java
if (authService.isAdmin(credentials)) {  // Safe - handles null
    // credentials.isAdmin() already null-checked in AuthService
}
```

### Issue: Old EmployeeDAO still referenced

**Solution**: Remove old DAO and update imports
```java
// DELETE this import
// import src.EmployeeDAO;

// Use new services instead
private AuthService authService;
private EmployeeService employeeService;
```

---

## Summary of Changes

| Component | Old | New | Benefit |
|-----------|-----|-----|---------|
| Architecture | Monolithic | Layered | Organized, maintainable |
| Dependency | Direct | Injected | Testable, flexible |
| Data Transfer | ResultSet | Typed Objects | Type-safe, self-documenting |
| User Roles | String comparison | Polymorphic classes | Extensible, OOP-based |
| Business Logic | In UI | In Services | Reusable, centralized |
| Database Access | One DAO | Three Repositories | Single Responsibility |
| Error Handling | Scattered | Centralized | Consistent, reliable |
| Testing | Hard | Easy | Better quality |

---

## Next Steps

1. **Compile and test**: `javac` should find all classes
2. **Run the application**: Test login, search, salary raise
3. **Review the code**: Understand each layer
4. **Write unit tests**: Test services with mocked repositories
5. **Add new features**: Follow the established patterns
6. **Consider enhancements**: Spring Boot, JPA, REST API
