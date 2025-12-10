package src.models;

/**
 * EmployeeSearchResult data transfer object for search results.
 * Encapsulates search result data independently of database layer.
 */
public class EmployeeSearchResult {
    private int empId;
    private String name;
    private double salary;

    public EmployeeSearchResult(int empId, String name, double salary) {
        this.empId = empId;
        this.name = name;
        this.salary = salary;
    }

    public int getEmpId() { return empId; }
    public void setEmpId(int empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    @Override
    public String toString() {
        return name + " (ID: " + empId + ") - $" + String.format("%.2f", salary);
    }
}
