package src.users;

import src.models.UserCredentials;
import src.services.PayrollService;

/**
 * EmployeeUser class for regular employee functionality.
 * Extends User and provides employee-only operations.
 * Follows Liskov Substitution Principle: can be used wherever User is expected.
 */
public class EmployeeUser extends User {
    private final PayrollService payrollService;
    private static final int DEFAULT_EMPLOYEE_ID = 1;  // In real app, get from session

    public EmployeeUser(UserCredentials credentials, PayrollService payrollService) {
        super(credentials);
        this.payrollService = payrollService;
    }

    /**
     * Employee-specific action: view personal pay history.
     */
    public String viewPayHistory() {
        var history = payrollService.getPayHistory(DEFAULT_EMPLOYEE_ID);
        return payrollService.formatPayHistory(history);
    }

    /**
     * Employee-specific action: check salary info.
     * Can be extended to show personal salary data.
     */
    public String getSalaryInfo() {
        return "Your salary information is confidential and secure.";
    }

    @Override
    public void displayDashboard() {
        System.out.println("Employee Self-Service Dashboard for " + username);
    }
}
