package src.repositories;

import src.models.PayrollRecord;
import src.models.Report;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MockPayrollRepository provides demo payroll operations for testing.
 * Useful when database is not available.
 */
public class MockPayrollRepository implements IPayrollRepository {

    @Override
    public List<PayrollRecord> getPayHistory(int empId) {
        List<PayrollRecord> history = new ArrayList<>();
        
        // Mock pay history
        history.add(new PayrollRecord(empId, 75000.00, LocalDate.now()));
        history.add(new PayrollRecord(empId, 73500.00, LocalDate.now().minusMonths(1)));
        history.add(new PayrollRecord(empId, 72000.00, LocalDate.now().minusMonths(2)));
        history.add(new PayrollRecord(empId, 70000.00, LocalDate.now().minusMonths(3)));
        
        return history;
    }

    @Override
    public int updateSalaryRange(double min, double max, double percentIncrease) {
        // Mock: simulate updating 5 employees
        System.out.println("MOCK: Updated 5 employees with salary range $" + min + "-$" + max + " by " + percentIncrease + "%");
        return 5;
    }

    @Override
    public List<Report> getTotalPayByJobTitle() {
        List<Report> reports = new ArrayList<>();
        
        // Mock report data
        reports.add(new Report("Senior Developer", 350000.00));
        reports.add(new Report("Junior Developer", 180000.00));
        reports.add(new Report("Project Manager", 420000.00));
        reports.add(new Report("Business Analyst", 240000.00));
        
        return reports;
    }

    @Override
    public List<Report> getTotalPayByDivision() {
        List<Report> reports = new ArrayList<>();
        
        // Mock report data
        reports.add(new Report("Engineering", 570000.00));
        reports.add(new Report("Management", 420000.00));
        reports.add(new Report("Operations", 240000.00));
        
        return reports;
    }
}
