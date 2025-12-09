package src;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainApp extends Application {
    private EmployeeDAO dao = new EmployeeDAO();
    private Stage stage;

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("Company Z Employee System");
        showLogin();
    }

    // --- SCREEN 1: LOGIN ---
    private void showLogin() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10); grid.setHgap(10);

        TextField userIn = new TextField(); userIn.setPromptText("Username");
        PasswordField passIn = new PasswordField(); passIn.setPromptText("Password");
        Button loginBtn = new Button("Login");
        Label status = new Label();

        loginBtn.setOnAction(e -> {
            String role = dao.validateLogin(userIn.getText(), passIn.getText());
            if ("ADMIN".equalsIgnoreCase(role)) showAdmin();
            else if ("EMPLOYEE".equalsIgnoreCase(role)) showEmployee();
            else status.setText("Invalid Login");
        });

        grid.addRow(0, new Label("User:"), userIn);
        grid.addRow(1, new Label("Pass:"), passIn);
        grid.add(loginBtn, 1, 2);
        grid.add(status, 1, 3);
        stage.setScene(new Scene(grid, 300, 250));
        stage.show();
    }

    // --- SCREEN 2: ADMIN DASHBOARD ---
    private void showAdmin() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        
        Label lbl = new Label("ADMIN DASHBOARD");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // --- PART A: SEARCH ---
        TextField searchIn = new TextField(); searchIn.setPromptText("Search Name/ID");
        Button searchBtn = new Button("Search");
        TextArea output = new TextArea();
        output.setPrefHeight(80);

        searchBtn.setOnAction(e -> {
            ResultSet rs = dao.searchEmployee(searchIn.getText());
            StringBuilder sb = new StringBuilder();
            try {
                while(rs != null && rs.next()) 
                    sb.append(rs.getString("name")).append(" - $").append(rs.getDouble("salary")).append("\n");
            } catch(SQLException ex) { sb.append("Error"); }
            output.setText(sb.length() > 0 ? sb.toString() : "No results.");
        });

        // --- PART B: SALARY UPDATE ---
        TextField min = new TextField(); min.setPromptText("Min Salary");
        TextField max = new TextField(); max.setPromptText("Max Salary");
        TextField pct = new TextField(); pct.setPromptText("Percent %");
        Button updateBtn = new Button("Apply Bulk Raise");
        Label upMsg = new Label();

        updateBtn.setOnAction(e -> {
            try {
                int c = dao.updateSalaryRange(Double.parseDouble(min.getText()), Double.parseDouble(max.getText()), Double.parseDouble(pct.getText()));
                upMsg.setText("Updated " + c + " records.");
            } catch(Exception ex) { upMsg.setText("Invalid Input"); }
        });

        // --- PART C: REPORTS (NEW) ---
        Label reportLabel = new Label("Reports:");
        reportLabel.setStyle("-fx-font-weight: bold;");
        
        Button jobReportBtn = new Button("Total Pay by Job Title");
        Button divReportBtn = new Button("Total Pay by Division");
        TextArea reportOut = new TextArea();
        reportOut.setPrefHeight(100);

        jobReportBtn.setOnAction(e -> {
            try {
                ResultSet rs = dao.getTotalPayByJobTitle();
                StringBuilder sb = new StringBuilder("--- JOB TITLE REPORT ---\n");
                while(rs != null && rs.next()) 
                    sb.append(rs.getString("job_title_name")).append(": $").append(String.format("%.2f", rs.getDouble("total_pay"))).append("\n");
                reportOut.setText(sb.toString());
            } catch(Exception ex) { reportOut.setText("Error generating report"); }
        });

        divReportBtn.setOnAction(e -> {
            try {
                ResultSet rs = dao.getTotalPayByDivision();
                StringBuilder sb = new StringBuilder("--- DIVISION REPORT ---\n");
                while(rs != null && rs.next()) 
                    sb.append(rs.getString("division_name")).append(": $").append(String.format("%.2f", rs.getDouble("total_pay"))).append("\n");
                reportOut.setText(sb.toString());
            } catch(Exception ex) { reportOut.setText("Error generating report"); }
        });

        Button logout = new Button("Logout");
        logout.setOnAction(e -> showLogin());

        root.getChildren().addAll(
            lbl, 
            new Separator(), new Label("Search:"), searchIn, searchBtn, output, 
            new Separator(), new Label("Bulk Raise:"), min, max, pct, updateBtn, upMsg, 
            new Separator(), reportLabel, jobReportBtn, divReportBtn, reportOut,
            new Separator(), logout
        );
        stage.setScene(new Scene(root, 450, 800));
    }

    // --- SCREEN 3: EMPLOYEE VIEW ---
    private void showEmployee() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        Label lbl = new Label("EMPLOYEE SELF-SERVICE");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label info = new Label("Welcome! Your data is secure.");

        // --- REPORT: MY PAY HISTORY ---
        Button historyBtn = new Button("View My Pay Statement History");
        TextArea historyArea = new TextArea();

        historyBtn.setOnAction(e -> {
            int myEmpId = 1; // Simulated Logged-in User ID
            try {
                ResultSet rs = dao.getPayHistory(myEmpId);
                StringBuilder sb = new StringBuilder("--- MY PAY HISTORY ---\n");
                while(rs != null && rs.next()) {
                    sb.append("Date: ").append(rs.getDate("pay_date"))
                      .append(" | Amount: $").append(String.format("%.2f", rs.getDouble("salary"))).append("\n");
                }
                historyArea.setText(sb.toString());
            } catch(SQLException ex) { historyArea.setText("Error retrieving history."); }
        });

        Button logout = new Button("Logout");
        logout.setOnAction(e -> showLogin());

        root.getChildren().addAll(lbl, info, historyBtn, historyArea, logout);
        stage.setScene(new Scene(root, 400, 400));
    }
}