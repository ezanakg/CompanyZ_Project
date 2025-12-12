package src.models;

/**
 * Employee entity class representing an employee record.
 * Encapsulates employee data with proper getter/setter for maintainability.
 */
public class Employee {
    private int empId;
    private String name;
    private String ssn;
    private int jobTitleId;
    private int divisionId;

    public Employee(int empId, String name, int jobTitleId, int divisionId) {
        this.empId = empId;
        this.name = name;
        this.ssn = null;  // Default constructor without SSN
        this.jobTitleId = jobTitleId;
        this.divisionId = divisionId;
    }

    public Employee(int empId, String name, String ssn, int jobTitleId, int divisionId) {
        this.empId = empId;
        this.name = name;
        this.ssn = ssn;
        this.jobTitleId = jobTitleId;
        this.divisionId = divisionId;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }

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
