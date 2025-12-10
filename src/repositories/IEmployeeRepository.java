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
}
