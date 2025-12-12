package src.repositories;

import src.models.Employee;
import src.models.EmployeeSearchResult;
import java.util.ArrayList;
import java.util.List;

/**
 * MockEmployeeRepository provides demo employee search for testing.
 * Useful when database is not available.
 */
public class MockEmployeeRepository implements IEmployeeRepository {

    @Override
    public List<EmployeeSearchResult> searchEmployee(String searchTerm) {
        List<EmployeeSearchResult> results = new ArrayList<>();
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return results;
        }
        
        // Mock employee data
        String term = searchTerm.toLowerCase();
        
        if (term.contains("john") || term.contains("1")) {
            results.add(new EmployeeSearchResult(1, "John Smith", 75000.00));
        }
        if (term.contains("jane") || term.contains("2")) {
            results.add(new EmployeeSearchResult(2, "Jane Doe", 85000.00));
        }
        if (term.contains("bob") || term.contains("3")) {
            results.add(new EmployeeSearchResult(3, "Bob Johnson", 72000.00));
        }
        if (term.contains("alice") || term.contains("4")) {
            results.add(new EmployeeSearchResult(4, "Alice Williams", 90000.00));
        }
        
        return results;
    }

    @Override
    public Employee getEmployeeById(int empId) {
        switch(empId) {
            case 1:
                return new Employee(1, "John Smith", "123-45-6789", 1, 1);
            case 2:
                return new Employee(2, "Jane Doe", "234-56-7890", 2, 1);
            case 3:
                return new Employee(3, "Bob Johnson", "345-67-8901", 1, 2);
            case 4:
                return new Employee(4, "Alice Williams", "456-78-9012", 2, 2);
            default:
                return null;
        }
    }

    @Override
    public List<EmployeeSearchResult> searchBySsn(String ssn) {
        List<EmployeeSearchResult> results = new ArrayList<>();
        
        if (ssn == null || ssn.trim().isEmpty()) {
            return results;
        }
        
        // Mock employee data with SSNs
        if (ssn.equals("123-45-6789")) {
            results.add(new EmployeeSearchResult(1, "John Smith", 75000.00));
        } else if (ssn.equals("234-56-7890")) {
            results.add(new EmployeeSearchResult(2, "Jane Doe", 85000.00));
        } else if (ssn.equals("345-67-8901")) {
            results.add(new EmployeeSearchResult(3, "Bob Johnson", 72000.00));
        } else if (ssn.equals("456-78-9012")) {
            results.add(new EmployeeSearchResult(4, "Alice Williams", 90000.00));
        }
        
        return results;
    }
}
