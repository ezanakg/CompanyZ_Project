package src.services;

import src.models.Report;
import src.models.PayrollRecord;
import src.repositories.IPayrollRepository;
import java.util.List;

/**
 * PayrollService implements payroll and salary business logic.
 * Single Responsibility: handles payroll, salary, and report operations.
 * Depends on abstraction (IPayrollRepository) for loose coupling.
 */
public class PayrollService {
    private final IPayrollRepository payrollRepository;

    public PayrollService(IPayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
    }

    /**
     * Retrieves pay history for an employee.
     * @param empId the employee ID
     * @return list of payroll records
     */
    public List<PayrollRecord> getPayHistory(int empId) {
        if (empId <= 0) {
            return List.of();
        }
        return payrollRepository.getPayHistory(empId);
    }

    /**
     * Applies a salary raise to employees within a range.
     * Validates input before passing to repository.
     * @param min minimum salary
     * @param max maximum salary
     * @param percentIncrease percentage increase
     * @return number of employees updated
     */
    public int applySalaryRaise(double min, double max, double percentIncrease) {
        if (min < 0 || max < 0 || min > max) {
            throw new IllegalArgumentException("Invalid salary range: min must be >= 0, max must be >= min");
        }
        if (percentIncrease < -100) {
            throw new IllegalArgumentException("Percent increase cannot be less than -100%");
        }
        
        return payrollRepository.updateSalaryRange(min, max, percentIncrease);
    }

    /**
     * Retrieves report of total pay by job title.
     * @return list of reports
     */
    public List<Report> getJobTitleReport() {
        return payrollRepository.getTotalPayByJobTitle();
    }

    /**
     * Retrieves report of total pay by division.
     * @return list of reports
     */
    public List<Report> getDivisionReport() {
        return payrollRepository.getTotalPayByDivision();
    }

    /**
     * Formats pay history as readable string.
     * @param records the payroll records
     * @return formatted string representation
     */
    public String formatPayHistory(List<PayrollRecord> records) {
        if (records == null || records.isEmpty()) {
            return "No pay history found.";
        }
        
        StringBuilder sb = new StringBuilder("--- PAY HISTORY ---\n");
        for (PayrollRecord record : records) {
            sb.append("Date: ").append(record.getPayDate())
              .append(" | Amount: $").append(String.format("%.2f", record.getSalary())).append("\n");
        }
        return sb.toString();
    }

    /**
     * Formats report data as readable string.
     * @param reports the report data
     * @param title the report title
     * @return formatted string representation
     */
    public String formatReport(List<Report> reports, String title) {
        if (reports == null || reports.isEmpty()) {
            return "No data available.";
        }
        
        StringBuilder sb = new StringBuilder("--- " + title + " ---\n");
        for (Report report : reports) {
            sb.append(report.toString()).append("\n");
        }
        return sb.toString();
    }
}
