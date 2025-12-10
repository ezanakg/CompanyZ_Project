package src;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import src.models.UserCredentials;
import src.users.User;
import src.users.AdminUser;
import src.users.EmployeeUser;
import src.services.AuthService;
import src.services.EmployeeService;
import src.services.PayrollService;
import src.repositories.*;

/**
 * MainApp - JavaFX entry point for the Employee Management System.
 * 
 * Refactored to follow S.O.L.I.D principles:
 * - Dependency Injection: services injected via constructor
 * - Separation of Concerns: presentation layer separated from business logic
 * - Loose Coupling: depends on abstractions (IAuthRepository, etc)
 * - Single Responsibility: handles UI only, delegates logic to services
 */
public class MainApp extends Application {
    private Stage stage;
    
    // Service layer - injected at startup
    private AuthService authService;
    private EmployeeService employeeService;
    private PayrollService payrollService;
    
    // Current logged-in user
    private User currentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        initializeServices();
        primaryStage.setTitle("Company Z Employee System");
        
        // Test database connection on startup
        if (!testDatabaseConnection()) {
            showDatabaseErrorAlert();
        }
        
        showLogin();
    }
    
    /**
     * Tests if database connection is available
     */
    private boolean testDatabaseConnection() {
        try {
            return src.database.DBConnection.testConnection();
        } catch (Exception e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Shows error alert if database is not available
     */
    private void showDatabaseErrorAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Database Connection Warning");
        alert.setHeaderText("Database Connection Failed");
        alert.setContentText(
            "Could not connect to the database.\n\n" +
            "Please ensure:\n" +
            "1. MySQL Server is running\n" +
            "2. Database 'employeeData' exists\n" +
            "3. Credentials in DBConnection.java are correct\n\n" +
            "For testing, you can use:\n" +
            "Username: admin or employee\n" +
            "Password: test123"
        );
        alert.showAndWait();
    }

    /**
     * Initializes service layer with concrete implementations.
     * This is the Dependency Injection point - could be replaced with
     * a DI framework like Spring or Guice for larger projects.
     * 
     * Uses mock repositories if database connection fails.
     */
    private void initializeServices() {
        // Try to use real repositories with database
        // Fall back to mock repositories if database is unavailable
        IAuthRepository authRepository;
        IEmployeeRepository employeeRepository;
        IPayrollRepository payrollRepository;
        
        try {
            if (src.database.DBConnection.testConnection()) {
                System.out.println("Database connected. Using real repositories.");
                authRepository = new AuthRepository();
                employeeRepository = new EmployeeRepository();
                payrollRepository = new PayrollRepository();
            } else {
                throw new Exception("Database test failed");
            }
        } catch (Exception e) {
            System.out.println("Database unavailable, using mock repositories for demo: " + e.getMessage());
            authRepository = new MockAuthRepository();
            employeeRepository = new MockEmployeeRepository();
            payrollRepository = new MockPayrollRepository();
        }
        
        // Service layer
        authService = new AuthService(authRepository);
        employeeService = new EmployeeService(employeeRepository);
        payrollService = new PayrollService(payrollRepository);
    }

    // ============ SCREEN 1: LOGIN ============
    private void showLogin() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        TextField userIn = new TextField();
        userIn.setPromptText("Username");
        
        PasswordField passIn = new PasswordField();
        passIn.setPromptText("Password");
        
        Button loginBtn = new Button("Login");
        Label status = new Label();
        status.setStyle("-fx-text-fill: red;");

        loginBtn.setOnAction(e -> handleLogin(userIn.getText(), passIn.getText(), status));

        grid.addRow(0, new Label("User:"), userIn);
        grid.addRow(1, new Label("Pass:"), passIn);
        grid.add(loginBtn, 1, 2);
        grid.add(status, 1, 3);
        
        stage.setScene(new Scene(grid, 300, 250));
        stage.show();
    }

    /**
     * Handles login by delegating to AuthService.
     * Creates appropriate user role object on successful authentication.
     */
    private void handleLogin(String username, String password, Label status) {
        // Validate input
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            status.setText("Username and password required");
            status.setStyle("-fx-text-fill: red;");
            return;
        }
        
        try {
            UserCredentials credentials = authService.login(username, password);
            
            if (authService.isAdmin(credentials)) {
                currentUser = new AdminUser(credentials, employeeService, payrollService);
                showAdmin();
            } else if (authService.isEmployee(credentials)) {
                currentUser = new EmployeeUser(credentials, payrollService);
                showEmployee();
            } else {
                status.setText("Invalid credentials");
                status.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            status.setText("Login error: " + e.getMessage());
            status.setStyle("-fx-text-fill: red;");
        }
    }

    // ============ SCREEN 2: ADMIN DASHBOARD ============
    private void showAdmin() {
        AdminUser admin = (AdminUser) currentUser;
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        
        Label lbl = new Label("ADMIN DASHBOARD");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // --- PART A: SEARCH ---
        Label searchLabel = new Label("Search:");
        searchLabel.setStyle("-fx-font-weight: bold;");
        
        TextField searchIn = new TextField();
        searchIn.setPromptText("Search Name/ID");
        
        Button searchBtn = new Button("Search");
        TextArea output = new TextArea();
        output.setPrefHeight(80);
        output.setEditable(false);

        searchBtn.setOnAction(e -> {
            String results = admin.searchEmployees(searchIn.getText());
            output.setText(results);
        });

        // --- PART B: SALARY UPDATE ---
        Label raiseLabel = new Label("Bulk Raise:");
        raiseLabel.setStyle("-fx-font-weight: bold;");
        
        TextField min = new TextField();
        min.setPromptText("Min Salary");
        
        TextField max = new TextField();
        max.setPromptText("Max Salary");
        
        TextField pct = new TextField();
        pct.setPromptText("Percent %");
        
        Button updateBtn = new Button("Apply Bulk Raise");
        Label upMsg = new Label();

        updateBtn.setOnAction(e -> {
            try {
                int count = admin.applySalaryRaise(
                    Double.parseDouble(min.getText()),
                    Double.parseDouble(max.getText()),
                    Double.parseDouble(pct.getText())
                );
                upMsg.setText("Updated " + count + " records.");
                upMsg.setStyle("-fx-text-fill: green;");
            } catch (NumberFormatException ex) {
                upMsg.setText("Invalid Input: Please enter valid numbers");
                upMsg.setStyle("-fx-text-fill: red;");
            } catch (IllegalArgumentException ex) {
                upMsg.setText("Error: " + ex.getMessage());
                upMsg.setStyle("-fx-text-fill: red;");
            }
        });

        // --- PART C: REPORTS ---
        Label reportLabel = new Label("Reports:");
        reportLabel.setStyle("-fx-font-weight: bold;");
        
        Button jobReportBtn = new Button("Total Pay by Job Title");
        Button divReportBtn = new Button("Total Pay by Division");
        TextArea reportOut = new TextArea();
        reportOut.setPrefHeight(100);
        reportOut.setEditable(false);

        jobReportBtn.setOnAction(e -> {
            String report = admin.getJobTitleReport();
            reportOut.setText(report);
        });

        divReportBtn.setOnAction(e -> {
            String report = admin.getDivisionReport();
            reportOut.setText(report);
        });

        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            currentUser.logout();
            showLogin();
        });

        root.getChildren().addAll(
            lbl,
            new Separator(),
            searchLabel, searchIn, searchBtn, output,
            new Separator(),
            raiseLabel, min, max, pct, updateBtn, upMsg,
            new Separator(),
            reportLabel, jobReportBtn, divReportBtn, reportOut,
            new Separator(),
            logout
        );
        
        stage.setScene(new Scene(root, 500, 900));
    }

    // ============ SCREEN 3: EMPLOYEE VIEW ============
    private void showEmployee() {
        EmployeeUser employee = (EmployeeUser) currentUser;
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        
        Label lbl = new Label("EMPLOYEE SELF-SERVICE");
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label info = new Label("Welcome! Your data is secure.");
        info.setStyle("-fx-text-fill: green;");

        // --- PAY HISTORY ---
        Button historyBtn = new Button("View My Pay Statement History");
        TextArea historyArea = new TextArea();
        historyArea.setPrefHeight(250);
        historyArea.setEditable(false);

        historyBtn.setOnAction(e -> {
            String history = employee.viewPayHistory();
            historyArea.setText(history);
        });

        // --- SALARY INFO ---
        Button salaryBtn = new Button("Salary Information");
        Label salaryInfo = new Label();

        salaryBtn.setOnAction(e -> {
            salaryInfo.setText(employee.getSalaryInfo());
            salaryInfo.setStyle("-fx-text-fill: blue;");
        });

        Button logout = new Button("Logout");
        logout.setOnAction(e -> {
            currentUser.logout();
            showLogin();
        });

        root.getChildren().addAll(
            lbl,
            info,
            new Separator(),
            historyBtn,
            historyArea,
            new Separator(),
            salaryBtn,
            salaryInfo,
            new Separator(),
            logout
        );
        
        stage.setScene(new Scene(root, 450, 600));
    }
}