package src.repositories;

import src.models.Employee;
import src.models.EmployeeSearchResult;
import java.util.List;

/**
 * IEmployeeRepository defines the contract for employee data operations.
 * Follows Interface Segregation Principle - focused on employee data access.
 */
public interface IEmployeeRepository {
    /**
     * Searches for employees by name or ID.
     * @param searchTerm the search query
     * @return list of matching employee search results
     */
    List<EmployeeSearchResult> searchEmployee(String searchTerm);

    /**
     * Retrieves an employee by ID.
     * @param empId the employee ID
     * @return Employee if found, null otherwise
     */
    Employee getEmployeeById(int empId);

    /**
     * Searches for an employee by Social Security Number (SSN).
     * @param ssn the employee's SSN (e.g., "123-45-6789")
     * @return list of matching employee search results
     */
    List<EmployeeSearchResult> searchBySsn(String ssn);
}
