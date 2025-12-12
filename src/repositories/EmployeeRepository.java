package src.repositories;

import src.models.Employee;
import src.models.EmployeeSearchResult;
import src.database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * EmployeeRepository implements IEmployeeRepository.
 * Single Responsibility: handles only employee data access operations.
 * Returns typed objects instead of ResultSet for better encapsulation.
 */
public class EmployeeRepository implements IEmployeeRepository {
    private static final String SEARCH_QUERY = 
        "SELECT e.empid, e.name, p.salary FROM employees e " +
        "JOIN payroll p ON e.empid = p.empid " +
        "WHERE e.name LIKE ? OR e.empid = ?";
    
    private static final String SEARCH_BY_SSN_QUERY = 
        "SELECT e.empid, e.name, p.salary FROM employees e " +
        "JOIN payroll p ON e.empid = p.empid " +
        "WHERE e.ssn = ?";
    
    private static final String GET_BY_ID_QUERY = 
        "SELECT empid, name, job_title_id, division_id FROM employees WHERE empid = ?";

    @Override
    public List<EmployeeSearchResult> searchEmployee(String searchTerm) {
        List<EmployeeSearchResult> results = new ArrayList<>();
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return results;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_QUERY)) {
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, searchTerm);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EmployeeSearchResult result = new EmployeeSearchResult(
                        rs.getInt("empid"),
                        rs.getString("name"),
                        rs.getDouble("salary")
                    );
                    results.add(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Employee search failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    @Override
    public Employee getEmployeeById(int empId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GET_BY_ID_QUERY)) {
            stmt.setInt(1, empId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Employee(
                        rs.getInt("empid"),
                        rs.getString("name"),
                        rs.getInt("job_title_id"),
                        rs.getInt("division_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve employee: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<EmployeeSearchResult> searchBySsn(String ssn) {
        List<EmployeeSearchResult> results = new ArrayList<>();
        
        if (ssn == null || ssn.trim().isEmpty()) {
            return results;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SEARCH_BY_SSN_QUERY)) {
            stmt.setString(1, ssn);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EmployeeSearchResult result = new EmployeeSearchResult(
                        rs.getInt("empid"),
                        rs.getString("name"),
                        rs.getDouble("salary")
                    );
                    results.add(result);
                }
            }
        } catch (SQLException e) {
            System.err.println("Employee SSN search failed: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
}
