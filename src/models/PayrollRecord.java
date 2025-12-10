package src.models;

import java.time.LocalDate;

/**
 * PayrollRecord entity representing payroll information.
 * Encapsulates salary and payment history data.
 */
public class PayrollRecord {
    private int empId;
    private double salary;
    private LocalDate payDate;

    public PayrollRecord(int empId, double salary, LocalDate payDate) {
        this.empId = empId;
        this.salary = salary;
        this.payDate = payDate;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }

    @Override
    public String toString() {
        return "PayrollRecord{" +
                "empId=" + empId +
                ", salary=" + String.format("$%.2f", salary) +
                ", payDate=" + payDate +
                '}';
    }
}
