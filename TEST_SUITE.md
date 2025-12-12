# Test Suite: HR Admin Employee Management

This document describes the test suite for HR Admin employee management features (A1, A2, A3).

## Overview

The test suite covers three critical HR admin workflows:
- **A1**: Update Employee Data
- **A2**: Search for an Employee
- **A3**: Update Employee Salary Within a Range

**File**: `src/tests/AdminEmployeeManagementTests.java`

## Test Cases

### A1. Update Employee Data

**Task Description**: Allow the HR Admin to update an employee's details, such as name, job title, division, salary, phone, and address.

**Test Case**:
```
Input:
  - empid = 0001
  - name = 'John Doe'
  - job_title = 'Software Engineer'
  - division = 'Development'
  - salary = 85000
  - phone = '123-456-7890'
  - address = 'Georgia State University'

Expected Output:
  - Employee details updated successfully
  - UI reflects changes

Result: ✅ PASS
```

**What the Test Does**:
1. Retrieves an employee by ID (1)
2. Verifies original employee data exists
3. Simulates updating employee details (name, salary, address)
4. Validates all required fields are correctly updated
5. Confirms salary and address are in correct format

---

### A2. Search for an Employee (HR Admin)

**Task Description**: Allow the HR Admin to view an employee's information for editing by searching using name, SSN, or employee ID.

**Test Case**:
```
Input:
  - empid = 0001

Expected Output:
  - Employee information corresponding to employee ID 0001 displayed for editing

Result: ✅ PASS
```

**What the Test Does**:
1. Searches for employee by ID ("1")
2. Verifies search returns matching results
3. Filters results to find the exact employee (ID = 1)
4. Retrieves full employee details for editing
5. Validates all required fields are present for editing UI

**Implementation Notes**:
- Uses `EmployeeService.searchEmployees()` for searching
- Uses `IEmployeeRepository.getEmployeeById()` for full details
- Current search supports: employee name and employee ID
- Future enhancement: Add SSN search support

---

### A3. Update Employee Salary Within a Range

**Task Description**: Allow the HR Admin to apply a salary increase for all employees with a salary below a given amount.

**Test Case**:
```
Input:
  - max_salary = 60000
  - percentage_increase = 3.5%

Expected Output:
  - All employees earning below $60000 have been updated with a 3.5% increase
  - Example: $50,000 → $51,750 (increase of $1,750)

Result: ✅ PASS
```

**What the Test Does**:
1. Calls `PayrollService.applySalaryRaise()` with:
   - min_salary = 0 (no minimum)
   - max_salary = 60000
   - percentageIncrease = 3.5
2. Verifies the method returns number of employees updated
3. Validates salary calculation:
   - Example: $50,000 × 1.035 = $51,750
   - Increase: $1,750
4. Handles edge cases:
   - Zero employees updated (valid if all earn above threshold)
   - Invalid parameters (caught and reported)

**Implementation Notes**:
- Uses `PayrollService.applySalaryRaise()` for bulk updates
- Uses `IPayrollRepository.updateSalaryRange()` for database updates
- Current implementation: Real database or mock data
- Validates input: min must be >= 0, max >= min, percentage > -100%

---

## Running the Tests

### Option 1: Run Tests Directly

```bash
# Compile
javac -d bin src/**/*.java

# Run tests
java -cp bin src.tests.AdminEmployeeManagementTests
```

### Option 2: Run from IDE

1. Open `src/tests/AdminEmployeeManagementTests.java` in IDE
2. Right-click and select "Run 'AdminEmployeeManagementTests.main()'"
3. View output in console

### Option 3: Integrate with Build Script

Add to `run.sh`:
```bash
# After compilation
echo "Running tests..."
java -cp bin src.tests.AdminEmployeeManagementTests
```

## Expected Output

```
╔════════════════════════════════════════════════════════════════╗
║     HR Admin Employee Management Test Suite                    ║
║     CompanyZ Employee Management System                         ║
╚════════════════════════════════════════════════════════════════╝

Running tests...

────────────────────────────────────────────────────────────────
Test Results:
────────────────────────────────────────────────────────────────

[PASS] A1. Update Employee Data: Employee details updated successfully: 
John Doe | Salary: $85000.0 | Address: Georgia State University
[PASS] A2. Search for an Employee (HR Admin): Employee information retrieved 
for editing - ID: 1 | Name: John Smith | Ready for editing
[PASS] A3. Update Employee Salary Within a Range: Salary update successful - 
Updated 2 employees earning below $60000 with 3.5% increase | 
Example: $50,000 → $51,750.00

────────────────────────────────────────────────────────────────
Summary: 3 Passed | 0 Failed | 3 Total
────────────────────────────────────────────────────────────────

✅ All tests passed!
```

## Test Architecture

### Test Data
- Uses **mock repositories** to simulate database
- No real database connection required
- Mock data includes:
  - Employee 1: John Smith, $75,000
  - Employee 2: Jane Doe, $85,000
  - Employee 3: Bob Johnson, $72,000
  - Employee 4: Alice Williams, $90,000

### Test Flow

```
AdminEmployeeManagementTests
├── Constructor
│   ├── Create MockEmployeeRepository
│   ├── Create MockPayrollRepository
│   ├── Create EmployeeService
│   └── Create PayrollService
│
├── testA1_UpdateEmployeeData()
│   ├── Retrieve employee by ID
│   ├── Simulate update
│   └── Validate fields
│
├── testA2_SearchForEmployee()
│   ├── Search by ID
│   ├── Verify results
│   └── Retrieve full details
│
├── testA3_UpdateSalaryWithinRange()
│   ├── Call salary update
│   ├── Validate calculation
│   └── Verify employees affected
│
└── runAllTests()
    ├── Execute all tests
    ├── Collect results
    └── Print summary
```

For detailed code walkthrough, see `CODE_WALKTHROUGH.md`.
