package src;

import java.sql.*;

public class EmployeeDAO {

    // --- FEATURE 1: LOGIN VALIDATION ---
    public String validateLogin(String username, String password) {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("role");
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // --- FEATURE 2: SEARCH EMPLOYEE (For Admin) ---
    public ResultSet searchEmployee(String searchTerm) {
        String query = "SELECT e.empid, e.name, p.salary FROM employees e " +
                       "JOIN payroll p ON e.empid = p.empid " +
                       "WHERE e.name LIKE ? OR e.empid = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchTerm + "%");
            stmt.setString(2, searchTerm);
            return stmt.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    // --- FEATURE 3: BULK SALARY UPDATE (For Admin) ---
    public int updateSalaryRange(double min, double max, double pct) {
        String select = "SELECT empid, salary FROM payroll WHERE salary >= ? AND salary < ?";
        String update = "UPDATE payroll SET salary = ? WHERE empid = ?";
        int count = 0;
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement sStmt = conn.prepareStatement(select);
            sStmt.setDouble(1, min);
            sStmt.setDouble(2, max);
            ResultSet rs = sStmt.executeQuery();
            
            while (rs.next()) {
                double newSal = rs.getDouble("salary") * (1 + (pct / 100));
                PreparedStatement uStmt = conn.prepareStatement(update);
                uStmt.setDouble(1, newSal);
                uStmt.setInt(2, rs.getInt("empid"));
                uStmt.executeUpdate();
                count++;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return count;
    }

    // --- REPORT 1: PAY HISTORY (For General Employee) ---
    public ResultSet getPayHistory(int empId) {
        String query = "SELECT pay_date, salary FROM payroll " +
                       "WHERE empid = ? " +
                       "ORDER BY pay_date DESC"; 
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, empId);
            return stmt.executeQuery();
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    // --- REPORT 2: TOTAL PAY BY JOB TITLE (For Admin) ---
    public ResultSet getTotalPayByJobTitle() {
        String query = "SELECT j.job_title_name, SUM(p.salary) as total_pay " +
                       "FROM employees e " +
                       "JOIN payroll p ON e.empid = p.empid " +
                       "JOIN job_titles j ON e.job_title_id = j.job_title_id " +
                       "GROUP BY j.job_title_name";
        try {
            Connection conn = DBConnection.getConnection();
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    // --- REPORT 3: TOTAL PAY BY DIVISION (For Admin) ---
    public ResultSet getTotalPayByDivision() {
        String query = "SELECT d.division_name, SUM(p.salary) as total_pay " +
                       "FROM employees e " +
                       "JOIN payroll p ON e.empid = p.empid " +
                       "JOIN division d ON e.division_id = d.division_id " +
                       "GROUP BY d.division_name";
        try {
            Connection conn = DBConnection.getConnection();
            return conn.createStatement().executeQuery(query);
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }
}