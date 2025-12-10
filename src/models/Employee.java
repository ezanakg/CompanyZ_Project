package src.models;

/**
 * Employee entity class representing an employee record.
 * Encapsulates employee data with proper getter/setter for maintainability.
 */
public class Employee {
    private int empId;
    private String name;
    private int jobTitleId;
    private int divisionId;

    public Employee(int empId, String name, int jobTitleId, int divisionId) {
        this.empId = empId;
        this.name = name;
        this.jobTitleId = jobTitleId;
        this.divisionId = divisionId;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getJobTitleId() { return jobTitleId; }
    public void setJobTitleId(int jobTitleId) { this.jobTitleId = jobTitleId; }

    public int getDivisionId() { return divisionId; }
    public void setDivisionId(int divisionId) { this.divisionId = divisionId; }

    @Override
    public String toString() {
        return "Employee{" +
                "empId=" + empId +
                ", name='" + name + '\'' +
                ", jobTitleId=" + jobTitleId +
                ", divisionId=" + divisionId +
                '}';
    }
}
