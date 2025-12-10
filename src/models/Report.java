package src.models;

/**
 * Report entity for storing aggregated report data.
 * Encapsulates data for different report types (job title, division).
 */
public class Report {
    private String category;      // Job title name or division name
    private double totalPayment;  // Total compensation for the category

    public Report(String category, double totalPayment) {
        this.category = category;
        this.totalPayment = totalPayment;
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getTotalPayment() { return totalPayment; }
    public void setTotalPayment(double totalPayment) { this.totalPayment = totalPayment; }

    @Override
    public String toString() {
        return category + ": $" + String.format("%.2f", totalPayment);
    }
}
