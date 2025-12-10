package src.users;

import src.models.UserCredentials;
import src.services.EmployeeService;
import src.services.PayrollService;

/**
 * AdminUser class for administrator-specific functionality.
 * Extends User and provides admin-only operations.
 * Follows Liskov Substitution Principle: can be used wherever User is expected.
 */
public class AdminUser extends User {
    private final EmployeeService employeeService;
    private final PayrollService payrollService;

    public AdminUser(UserCredentials credentials, 
                    EmployeeService employeeService,
                    PayrollService payrollService) {
        super(credentials);
        this.employeeService = employeeService;
        this.payrollService = payrollService;
    }

    /**
     * Admin-specific dashboard action: search employees.
     */
    public String searchEmployees(String searchTerm) {
        var results = employeeService.searchEmployees(searchTerm);
        return employeeService.formatSearchResults(results);
    }

    /**
     * Admin-specific action: apply bulk salary raise.
     */
    public int applySalaryRaise(double min, double max, double percentIncrease) {
        return payrollService.applySalaryRaise(min, max, percentIncrease);
    }

    /**
     * Admin-specific action: view job title report.
     */
    public String getJobTitleReport() {
        var reports = payrollService.getJobTitleReport();
        return payrollService.formatReport(reports, "JOB TITLE REPORT");
    }

    /**
     * Admin-specific action: view division report.
     */
    public String getDivisionReport() {
        var reports = payrollService.getDivisionReport();
        return payrollService.formatReport(reports, "DIVISION REPORT");
    }

    @Override
    public void displayDashboard() {
        System.out.println("Admin Dashboard for " + username);
    }
}
