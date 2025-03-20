package com.example.pt2025_30422_buda_cornel_assignment1.userInterface.tabs;

import com.example.pt2025_30422_buda_cornel_assignment1.businessLogic.TasksManager;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Employee;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Task;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeTab extends VBox {

    private TasksManager tasksManager;
    private TableView<Employee> employeeTable;

    public EmployeeTab(TasksManager tasksManager) {
        this.tasksManager = tasksManager;
        setPadding(new Insets(10));
        setSpacing(10);
        getChildren().addAll(createEmployeeInputSection(), createEmployeeTableSection());
    }

    private Pane createEmployeeInputSection() {
        HBox inputBox = new HBox(10);
        TextField idField = new TextField();
        idField.setPromptText("Employee ID (int)");
        TextField nameField = new TextField();
        nameField.setPromptText("Employee Name");
        Button addEmpBtn = new Button("Add Employee");
        addEmpBtn.setOnAction(e -> {
            try {
                int empId = Integer.parseInt(idField.getText().trim());
                String empName = nameField.getText().trim();
                if (!empName.isEmpty()) {
                    tasksManager.addEmployee(new Employee(empId, empName));
                    refreshEmployeeTable();
                    idField.clear();
                    nameField.clear();
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter a valid integer for Employee ID.");
            } catch (IllegalArgumentException ex) {
                showAlert("Duplicate Employee", ex.getMessage());
            }
        });
        inputBox.getChildren().addAll(idField, nameField, addEmpBtn);
        return inputBox;
    }

    private Pane createEmployeeTableSection() {
        employeeTable = new TableView<>();
        TableColumn<Employee, Number> empIdCol = new TableColumn<>("ID");
        empIdCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getIdEmployee()));
        TableColumn<Employee, String> empNameCol = new TableColumn<>("Name");
        empNameCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getName()));
        TableColumn<Employee, String> tasksCol = new TableColumn<>("Tasks");
        tasksCol.setCellValueFactory(cd -> {
            Employee e = cd.getValue();
            List<Task> tasks = tasksManager.getMapTasks().get(e);
            String tasksString = (tasks != null && !tasks.isEmpty())
                    ? tasks.stream()
                    .map(t -> t.getIdTask() + ":" + (t.isStatus() ? "Completed" : "Not Completed"))
                    .collect(Collectors.joining(", "))
                    : "None";
            return new javafx.beans.property.SimpleStringProperty(tasksString);
        });
        employeeTable.getColumns().addAll(empIdCol, empNameCol, tasksCol);
        refreshEmployeeTable();

        Button refreshButton = new Button("Refresh Employee Data");
        refreshButton.setOnAction(e -> refreshEmployeeTable());

        VBox box = new VBox(10, new Label("All Employees:"), employeeTable, refreshButton);
        return box;
    }

    private void refreshEmployeeTable() {
        List<Employee> employees = new ArrayList<>(tasksManager.getMapTasks().keySet());
        employeeTable.getItems().setAll(employees);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
