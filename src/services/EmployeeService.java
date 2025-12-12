package src.services;

import src.models.EmployeeSearchResult;
import src.repositories.IEmployeeRepository;
import java.util.List;

/**
 * EmployeeService implements employee business logic.
 * Single Responsibility: handles employee-related operations.
 * Depends on abstraction (IEmployeeRepository) for loose coupling.
 */
public class EmployeeService {
    private final IEmployeeRepository employeeRepository;

    public EmployeeService(IEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Searches for employees by name or ID.
     * @param searchTerm the search query
     * @return list of matching employees
     */
    public List<EmployeeSearchResult> searchEmployees(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();  // Return empty list instead of null
        }
        return employeeRepository.searchEmployee(searchTerm);
    }

    /**
     * Searches for an employee by Social Security Number (SSN).
     * @param ssn the employee's SSN (e.g., "123-45-6789")
     * @return list of matching employees
     */
    public List<EmployeeSearchResult> searchBySsn(String ssn) {
        if (ssn == null || ssn.trim().isEmpty()) {
            return List.of();  // Return empty list instead of null
        }
        return employeeRepository.searchBySsn(ssn);
    }

    /**
     * Formats search results as readable strings.
     * @param results the search results
     * @return formatted string representation
     */
    public String formatSearchResults(List<EmployeeSearchResult> results) {
        if (results == null || results.isEmpty()) {
            return "No results found.";
        }
        
        StringBuilder sb = new StringBuilder();
        for (EmployeeSearchResult result : results) {
            sb.append(result.toString()).append("\n");
        }
        return sb.toString();
    }
}
