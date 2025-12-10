package src.repositories;

import src.models.PayrollRecord;
import src.models.Report;
import src.database.DBConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * PayrollRepository implements IPayrollRepository.
 * Single Responsibility: handles all payroll and salary database operations.
 * Returns typed objects for better abstraction from database layer.
 */
public class PayrollRepository implements IPayrollRepository {
    private static final String PAY_HISTORY_QUERY = 
        "SELECT pay_date, salary FROM payroll WHERE empid = ? ORDER BY pay_date DESC";
    
    private static final String SELECT_SALARY_RANGE_QUERY = 
        "SELECT empid, salary FROM payroll WHERE salary >= ? AND salary < ?";
    
    private static final String UPDATE_SALARY_QUERY = 
        "UPDATE payroll SET salary = ? WHERE empid = ?";
    
    private static final String JOB_TITLE_REPORT_QUERY = 
        "SELECT j.job_title_name, SUM(p.salary) as total_pay " +
        "FROM employees e " +
        "JOIN payroll p ON e.empid = p.empid " +
        "JOIN job_titles j ON e.job_title_id = j.job_title_id " +
        "GROUP BY j.job_title_name";
    
    private static final String DIVISION_REPORT_QUERY = 
        "SELECT d.division_name, SUM(p.salary) as total_pay " +
        "FROM employees e " +
        "JOIN payroll p ON e.empid = p.empid " +
        "JOIN division d ON e.division_id = d.division_id " +
        "GROUP BY d.division_name";

    @Override
    public List<PayrollRecord> getPayHistory(int empId) {
        List<PayrollRecord> history = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(PAY_HISTORY_QUERY)) {
            stmt.setInt(1, empId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate payDate = rs.getDate("pay_date").toLocalDate();
                    PayrollRecord record = new PayrollRecord(
                        empId,
                        rs.getDouble("salary"),
                        payDate
                    );
                    history.add(record);
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to retrieve pay history: " + e.getMessage());
            e.printStackTrace();
        }
        return history;
    }

    @Override
    public int updateSalaryRange(double min, double max, double percentIncrease) {
        if (percentIncrease < -100) {
            throw new IllegalArgumentException("Percent increase cannot be less than -100");
        }
        
        int count = 0;
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);  // Transaction support for data consistency
            
            try (PreparedStatement selectStmt = conn.prepareStatement(SELECT_SALARY_RANGE_QUERY)) {
                selectStmt.setDouble(1, min);
                selectStmt.setDouble(2, max);
                
                try (ResultSet rs = selectStmt.executeQuery()) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(UPDATE_SALARY_QUERY)) {
                        while (rs.next()) {
                            double newSalary = rs.getDouble("salary") * (1 + (percentIncrease / 100));
                            updateStmt.setDouble(1, newSalary);
                            updateStmt.setInt(2, rs.getInt("empid"));
                            updateStmt.addBatch();
                            count++;
                        }
                        updateStmt.executeBatch();
                    }
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Salary update transaction rolled back: " + e.getMessage());
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Failed to update salary range: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public List<Report> getTotalPayByJobTitle() {
        List<Report> reports = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(JOB_TITLE_REPORT_QUERY);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Report report = new Report(
                    rs.getString("job_title_name"),
                    rs.getDouble("total_pay")
                );
                reports.add(report);
            }
        } catch (SQLException e) {
            System.err.println("Failed to generate job title report: " + e.getMessage());
            e.printStackTrace();
        }
        return reports;
    }

    @Override
    public List<Report> getTotalPayByDivision() {
        List<Report> reports = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DIVISION_REPORT_QUERY);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Report report = new Report(
                    rs.getString("division_name"),
                    rs.getDouble("total_pay")
                );
                reports.add(report);
            }
        } catch (SQLException e) {
            System.err.println("Failed to generate division report: " + e.getMessage());
            e.printStackTrace();
        }
        return reports;
    }
}
