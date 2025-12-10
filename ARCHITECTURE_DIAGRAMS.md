# Architecture Diagrams

## Overall Layered Architecture

```
┌────────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                         │
│                                                                │
│                    ┌──────────────┐                            │
│                    │   MainApp    │  ◄─── JavaFX UI            │
│                    │   (Views)    │                            │
│                    └──────┬───────┘                            │
└───────────────────────────┼─────────────────────────────────────┘
                            │
                            │ calls
                            │
┌───────────────────────────▼─────────────────────────────────────┐
│              APPLICATION LAYER (User Roles)                     │
│                                                                │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│   │    User      │  │              │  │              │         │
│   │  (Abstract)  │  │   AdminUser  │  │ EmployeeUser │         │
│   │              │  │              │  │              │         │
│   │ - logout()   │  │ - search()   │  │ - viewPay()  │         │
│   │ - display()  │  │ - raiseSal() │  │              │         │
│   └──────────────┘  │ - reports()  │  └──────────────┘         │
│                     └──────────────┘                           │
└───────────────────────┬──────────────────────────────────────────┘
                        │
                        │ depends on
                        │
┌───────────────────────▼─────────────────────────────────────────┐
│              SERVICE LAYER (Business Logic)                     │
│                                                                │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│   │  AuthService │  │EmployeeServ. │  │PayrollServ.  │         │
│   │              │  │              │  │              │         │
│   │ - login()    │  │ - search()   │  │ - getHistory │         │
│   │ - isAdmin()  │  │ - format()   │  │ - raiseSal() │         │
│   │ - isEmp()    │  │              │  │ - reports()  │         │
│   └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                │
│  (Validates, formats, applies business rules, coordinates)    │
└───────────────────────┬──────────────────────────────────────────┘
                        │
                        │ depends on
                        │
┌───────────────────────▼─────────────────────────────────────────┐
│           REPOSITORY LAYER (Data Access Interfaces)            │
│                                                                │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│   │IAuthRepository│ │IEmployeeRepo. │  │IPayrollRepo. │         │
│   │              │  │              │  │              │         │
│   │ - validate() │  │ - search()   │  │ - getHistory │         │
│   │              │  │ - getById()  │  │ - update()   │         │
│   │              │  │              │  │ - reports()  │         │
│   └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                │
│           (Contracts - interfaces only, no implementation)    │
└───────────────────────┬──────────────────────────────────────────┘
                        │
                        │ implemented by
                        │
┌───────────────────────▼─────────────────────────────────────────┐
│      REPOSITORY LAYER (Data Access Implementations)            │
│                                                                │
│   ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│   │AuthRepository│ │EmployeeRepository│ │PayrollRepository        │
│   │              │  │              │  │              │         │
│   │ - SQL auth   │  │ - SQL search │  │ - SQL update │         │
│   │ - Map result │  │ - Map result │  │ - Map result │         │
│   └──────────────┘  └──────────────┘  └──────────────┘         │
│                                                                │
│   (SQL queries, ResultSet mapping to typed objects)           │
└───────────────────────┬──────────────────────────────────────────┘
                        │
                        │ uses
                        │
┌───────────────────────▼─────────────────────────────────────────┐
│                   DATABASE LAYER                               │
│                                                                │
│            ┌────────────────────────────┐                      │
│            │     DBConnection           │                      │
│            │                            │                      │
│            │ - getConnection()          │                      │
│            │ - testConnection()         │                      │
│            └────────┬───────────────────┘                      │
│                     │                                          │
│                     ▼                                          │
│            ┌────────────────────┐                              │
│            │  MySQL Database    │                              │
│            │  (localhost:3306)  │                              │
│            └────────────────────┘                              │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

---

## Data Flow: Employee Search Example

```
User clicks "Search" button
         │
         ▼
┌─────────────────────────────────────────┐
│  MainApp.searchBtn.setOnAction(e)       │
│                                         │
│  String results =                       │
│    admin.searchEmployees(searchTerm)    │  ◄── UI Layer
│                                         │
│  output.setText(results)                │
└──────────────┬──────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  AdminUser.searchEmployees(term)         │
│                                          │
│  return employeeService                  │  ◄── User Role
│    .formatSearchResults(                 │
│      employeeService                     │
│        .searchEmployees(term)            │
│    )                                     │
└──────────────┬───────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  EmployeeService.searchEmployees(term)   │
│                                          │
│  if (term == null || term.isEmpty())     │
│      return List.of()                    │  ◄── Service Layer
│                                          │
│  return employeeRepository               │      (Business Logic)
│    .searchEmployee(term)                 │
└──────────────┬───────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  IEmployeeRepository.searchEmployee()    │
│  (Interface Contract)                    │  ◄── Repository
└──────────────┬───────────────────────────┘      Interface
               │
               ▼
┌──────────────────────────────────────────┐
│  EmployeeRepository                      │
│    .searchEmployee(term)                 │
│                                          │
│  try {                                   │  ◄── Repository
│    PreparedStatement stmt =              │      Implementation
│      conn.prepareStatement(query)        │
│    stmt.setString(1, "%" + term + "%")   │      (Data Access)
│    stmt.setString(2, term)               │
│    ResultSet rs = stmt.executeQuery()    │
│                                          │
│    while (rs.next()) {                   │
│      results.add(                        │
│        new EmployeeSearchResult(         │
│          rs.getInt("empid"),             │
│          rs.getString("name"),           │
│          rs.getDouble("salary")          │
│        )                                 │
│      )                                   │
│    }                                     │
│    return results                        │
│  } catch (SQLException e) { ... }        │
└──────────────┬───────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────┐
│  DBConnection.getConnection()            │
│                                          │  ◄── Database Layer
│  DriverManager.getConnection(URL, U, P) │      (Connectivity)
└──────────────┬───────────────────────────┘
               │
               ▼
         MySQL Database
              │
         Executes query
              │
         Returns ResultSet
              │
         ◄────────────────────────────────
               │
     ResultSet mapped to
     EmployeeSearchResult objects
               │
               ▼
         Back up the stack
               │
         EmployeeService formats results
               │
         AdminUser wraps in UI strings
               │
         MainApp displays in TextArea
               │
               ▼
            User sees results!
```

---

## Class Hierarchy: User Roles

```
┌─────────────────────────────────────┐
│          User (Abstract)            │
├─────────────────────────────────────┤
│  Properties:                        │
│  - username: String                 │
│  - role: String                     │
│                                     │
│  Abstract Methods:                  │
│  + displayDashboard()               │
│                                     │
│  Concrete Methods:                  │
│  + getUsername(): String            │
│  + getRole(): String                │
│  + logout(): void                   │
│  + toString(): String               │
└────────────────┬──────────────────┬─┘
                 │                  │
                 │                  │
    ┌────────────▼────┐   ┌────────▼─────────┐
    │   AdminUser     │   │  EmployeeUser    │
    ├─────────────────┤   ├──────────────────┤
    │  Properties:    │   │  Properties:     │
    │  - empService   │   │  - payrollServ.  │
    │  - payService   │   │                  │
    │                 │   │  Methods:        │
    │  Methods:       │   │  + viewPayHist() │
    │  + search()     │   │  + getSalaryInfo│
    │  + raiseSalary()│   │                  │
    │  + jobReport()  │   │  Implements:     │
    │  + divReport()  │   │  + displayDash() │
    │                 │   │                  │
    │  Implements:    │   └──────────────────┘
    │  + displayDash()│
    └─────────────────┘

Both subclasses:
✓ Can be used wherever User is expected (LSP)
✓ Implement abstract methods (OCP)
✓ Encapsulate role-specific behavior (SRP)
✓ Easy to add new roles without modifying existing code
```

---

## Dependency Graph

```
Presentation Layer
      ▲
      │
      │ depends on
      │
Service Layer
      ▲
      │
      │ depends on
      │
Repository Interface Layer
      ▲
      │
      │ implemented by
      │
Repository Implementation Layer
      ▲
      │
      │ uses
      │
Database Layer

KEY RULE:
- Higher layers depend ONLY on abstractions (interfaces)
- Lower layers never depend on higher layers
- This prevents circular dependencies and tight coupling
```

---

## Repository Pattern Illustration

```
Before (Monolithic DAO):
┌────────────────────────────────────┐
│       EmployeeDAO                  │
│                                    │
│  + validateLogin()                 │
│  + searchEmployee()                │
│  + updateSalaryRange()             │
│  + getPayHistory()                 │
│  + getTotalPayByJobTitle()         │
│  + getTotalPayByDivision()         │
│                                    │
│  Everything in one class! ← BAD    │
└────────────────────────────────────┘

After (Repository Pattern):
┌─────────────────┐  ┌──────────────┐  ┌─────────────────┐
│IAuthRepository  │  │IEmployeeRepo │  │IPayrollRepository
├─────────────────┤  ├──────────────┤  ├─────────────────┤
│validateLogin()  │  │search()      │  │getPayHistory()  │
│                 │  │getById()     │  │updateSalary()   │
│                 │  │              │  │getJobReport()   │
│                 │  │              │  │getDivReport()   │
└────────┬────────┘  └──────┬───────┘  └────────┬────────┘
         │                  │                   │
    implemented by      implemented by     implemented by
         │                  │                   │
┌────────▼─────┐  ┌─────────▼────────┐  ┌──────▼────────┐
│AuthRepository│  │EmployeeRepository│  │PayrollRepository
└──────────────┘  └──────────────────┘  └───────────────┘

Benefits:
✓ Each repository focused on one domain
✓ Interfaces enable loose coupling
✓ Easy to mock for testing
✓ Easy to swap implementations
```

---

## Dependency Injection Pattern

```
Without Dependency Injection (Tight Coupling):

  ┌─────────────┐
  │ MainApp     │
  │             │
  │  AuthService│  ◄── creates directly
  │             │
  │  new AuthService(
  │    new AuthRepository()  ◄── can't be mocked
  │  )          │
  └─────────────┘


With Dependency Injection (Loose Coupling):

  ┌──────────────────────┐
  │ AuthService          │
  │                      │
  │  private             │
  │    IAuthRepository   │
  │    repo              │
  │                      │
  │  public              │
  │    AuthService(      │
  │      IAuthRepository │◄── injectable parameter
  │      repo            │
  │    ) {               │
  │      this.repo = repo│
  │    }                 │
  └──────────────────────┘
         ▲
         │ injected by
         │
  ┌──────────────────┐
  │ MainApp          │
  │                  │
  │  AuthService s = │
  │    new           │
  │    AuthService(  │
  │      new         │
  │      AuthRepository  ◄── production
  │    )             │
  │                  │
  │  or              │
  │                  │
  │  AuthService s = │
  │    new           │
  │    AuthService(  │
  │      mock()      │◄─── testing (mocked!)
  │    )             │
  └──────────────────┘

Benefits:
✓ Can pass any IAuthRepository implementation
✓ Can use real repositories in production
✓ Can use mocked repositories in tests
✓ Loose coupling enables flexibility
```

---

## Layering Principles

```
Each layer has specific responsibilities:

┌─────────────────────────────────────────┐
│ Presentation Layer                      │
│ ✓ Display UI                            │
│ ✓ Handle user input                     │
│ ✗ NO database access                    │
│ ✗ NO business logic                     │
└─────────────────────────────────────────┘
         │
         │ calls
         ▼
┌─────────────────────────────────────────┐
│ Service Layer                           │
│ ✓ Business rules                        │
│ ✓ Validation                            │
│ ✓ Data formatting                       │
│ ✗ NO raw SQL                            │
│ ✗ NO UI logic                           │
└─────────────────────────────────────────┘
         │
         │ calls
         ▼
┌─────────────────────────────────────────┐
│ Repository Layer                        │
│ ✓ SQL queries                           │
│ ✓ Database operations                   │
│ ✓ Result mapping                        │
│ ✗ NO business logic                     │
│ ✗ NO UI code                            │
└─────────────────────────────────────────┘
         │
         │ uses
         ▼
┌─────────────────────────────────────────┐
│ Database Layer                          │
│ ✓ Connection management                 │
│ ✓ SQL execution                         │
│ ✗ NO application logic                  │
└─────────────────────────────────────────┘

Violating these rules:
✗ Calling repository from UI = coupling, hard to test
✗ SQL queries in services = mixing concerns
✗ Business logic in repository = unclear responsibility
```

---

## Polymorphism in Action

```
Monolithic Approach (No Polymorphism):

  if (role.equals("ADMIN")) {
      // Admin code
      dao.searchEmployee();
      dao.updateSalaryRange();
  } else if (role.equals("EMPLOYEE")) {
      // Employee code
      dao.getPayHistory();
  }

  // String-based, error-prone, tight coupling


Polymorphic Approach (Using Inheritance):

  User currentUser;
  
  if (authService.isAdmin(credentials)) {
      currentUser = new AdminUser(credentials, empService, payService);
  } else {
      currentUser = new EmployeeUser(credentials, payService);
  }
  
  // Now use currentUser polymorphically:
  currentUser.displayDashboard();  // Works for all types!
  currentUser.logout();            // Works for all types!
  
  // Type-specific methods:
  if (currentUser instanceof AdminUser) {
      AdminUser admin = (AdminUser) currentUser;
      admin.searchEmployees("term");
  }

Benefits:
✓ Type-safe (compiler checks types)
✓ Extensible (add new types without changing existing code)
✓ Cleaner code (no massive if-else chains)
✓ Follows OOP principles
```

---

## Testing Architecture

```
Production Use:
┌────────────────────────────────────┐
│   Real Repositories                │
├────────────────────────────────────┤
│  AuthRepository                    │
│  - Executes real SQL               │
│  - Connects to real MySQL          │
│  - Returns real data               │
└────────────────────────────────────┘


Testing Use:
┌────────────────────────────────────┐
│   Mocked Repositories              │
├────────────────────────────────────┤
│  Mock<IAuthRepository>             │
│  - Returns predefined data         │
│  - No database needed              │
│  - Fast and reliable               │
└────────────────────────────────────┘

Service layer works identically:

  AuthService service = 
    new AuthService(realRepo);     // Production
  
  AuthService service = 
    new AuthService(mockRepo);     // Testing

  // Service code unchanged in both cases!

Benefits:
✓ Tests don't need real database
✓ Tests run fast
✓ Tests are deterministic (same results every time)
✓ Tests can simulate error conditions easily
```

---

## Conclusion

These diagrams illustrate how the refactored architecture achieves:
- **Clear separation of concerns** - each layer has one job
- **Loose coupling** - layers depend on interfaces, not concretions
- **Easy testing** - repositories can be mocked
- **Easy extension** - new features added without modifying existing code
- **Professional code quality** - follows industry best practices

The layered architecture with dependency injection is a proven approach used in enterprise applications worldwide.
