package src.repositories;

import src.models.PayrollRecord;
import src.models.Report;
import java.util.List;

/**
 * IPayrollRepository defines the contract for payroll data operations.
 * Follows Interface Segregation Principle - focused on payroll and salary operations.
 */
public interface IPayrollRepository {
    /**
     * Retrieves pay history for an employee.
     * @param empId the employee ID
     * @return list of payroll records
     */
    List<PayrollRecord> getPayHistory(int empId);

    /**
     * Updates salary for employees within a range by a percentage.
     * @param min minimum salary threshold
     * @param max maximum salary threshold
     * @param percentIncrease percentage increase to apply
     * @return number of records updated
     */
    int updateSalaryRange(double min, double max, double percentIncrease);

    /**
     * Retrieves total pay aggregated by job title.
     * @return list of reports with job title and total pay
     */
    List<Report> getTotalPayByJobTitle();

    /**
     * Retrieves total pay aggregated by division.
     * @return list of reports with division and total pay
     */
    List<Report> getTotalPayByDivision();
}
