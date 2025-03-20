package com.example.pt2025_30422_buda_cornel_assignment1.userInterface.tabs;

import com.example.pt2025_30422_buda_cornel_assignment1.businessLogic.TasksManager;
import com.example.pt2025_30422_buda_cornel_assignment1.businessLogic.Utility;
import com.example.pt2025_30422_buda_cornel_assignment1.dataAccess.SerializationUtility;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Employee;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.*;

public class HomeTab extends VBox {

    private TasksManager tasksManager;
    private TextArea statsTextArea;
    private final String SERIALIZATION_FILE = "manager1.ser";

    public HomeTab(TasksManager tasksManager) {
        this.tasksManager = tasksManager;
        setPadding(new Insets(10));
        setSpacing(15);

        getChildren().addAll(
                createAssignmentSection(),
                new Separator(),
                createStatsSection(),
                new Separator(),
                createSaveButton()
        );
    }


    private Pane createAssignmentSection() {
        VBox assignmentBox = new VBox(10);
        Label label = new Label("Assignment / Modification:");

        HBox wizardBox = new HBox(15);
        Button assignTaskBtn = new Button("Assign Task");
        assignTaskBtn.setOnAction(e -> showAssignWizard());

        Button modifyStatusBtn = new Button("Modify Task Status");
        modifyStatusBtn.setOnAction(e -> showModifyWizard());

        wizardBox.getChildren().addAll(assignTaskBtn, modifyStatusBtn);
        assignmentBox.getChildren().addAll(label, wizardBox);
        return assignmentBox;
    }


    private void showAssignWizard() {
        Stage wizardStage = new Stage();
        wizardStage.setTitle("Assign Task Wizard");


        final Employee[] chosenEmployee = new Employee[1];
        final Task[] chosenTask = new Task[1];


        TableView<Employee> empTableWizard = new TableView<>();
        empTableWizard.getColumns().addAll(createEmployeeIdCol(), createEmployeeNameCol());
        empTableWizard.getItems().setAll(tasksManager.getMapTasks().keySet());

        Button nextBtn = new Button("Next");
        nextBtn.setOnAction(e -> {
            Employee sel = empTableWizard.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("Selection Error", "Please select an employee.");
                return;
            }
            chosenEmployee[0] = sel;

            wizardStage.getScene().setRoot(createAssignStep2Pane(wizardStage, chosenEmployee, chosenTask));
        });

        VBox step1 = new VBox(10, new Label("Step 1: Select an Employee"), empTableWizard, nextBtn);
        step1.setPadding(new Insets(10));
        wizardStage.setScene(new Scene(step1, 400, 300));
        wizardStage.show();
    }


    private Pane createAssignStep2Pane(Stage wizardStage, Employee[] chosenEmployee, Task[] chosenTask) {
        TableView<Task> taskTableWizard = new TableView<>();
        taskTableWizard.getColumns().addAll(
                createTaskIdCol(), createTaskTypeCol(), createTaskStatusCol(), createTaskDurationCol()
        );
        taskTableWizard.getItems().setAll(getAllTasks());

        Button finishBtn = new Button("Finish");
        finishBtn.setOnAction(e -> {
            Task selTask = taskTableWizard.getSelectionModel().getSelectedItem();
            if (selTask == null) {
                showAlert("Selection Error", "Please select a task.");
                return;
            }
            chosenTask[0] = selTask;

            tasksManager.assignTaskToEmployee(chosenEmployee[0].getIdEmployee(), chosenTask[0]);
            tasksManager.removeUnassignedTask(chosenTask[0]);


            wizardStage.close();

            refreshStats();
        });

        VBox step2 = new VBox(10, new Label("Step 2: Select a Task"), taskTableWizard, finishBtn);
        step2.setPadding(new Insets(10));
        return step2;
    }


    private void showModifyWizard() {
        Stage wizardStage = new Stage();
        wizardStage.setTitle("Modify Task Status Wizard");

        final Employee[] chosenEmployee = new Employee[1];
        final Task[] chosenTask = new Task[1];

        TableView<Employee> empTableWizard = new TableView<>();
        empTableWizard.getColumns().addAll(createEmployeeIdCol(), createEmployeeNameCol());


        List<Employee> employeesWithTasks = new ArrayList<>();
        tasksManager.getMapTasks().forEach((emp, taskList) -> {
            if (taskList != null && !taskList.isEmpty()) {
                employeesWithTasks.add(emp);
            }
        });
        empTableWizard.getItems().setAll(employeesWithTasks);

        Button nextBtn = new Button("Next");
        nextBtn.setOnAction(e -> {
            Employee sel = empTableWizard.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("Selection Error", "Please select an employee.");
                return;
            }
            chosenEmployee[0] = sel;
            wizardStage.getScene().setRoot(createModifyStep2Pane(wizardStage, chosenEmployee, chosenTask));
        });

        VBox step1 = new VBox(10, new Label("Step 1: Select an Employee"), empTableWizard, nextBtn);
        step1.setPadding(new Insets(10));
        wizardStage.setScene(new Scene(step1, 400, 300));
        wizardStage.show();
    }

    private Pane createModifyStep2Pane(Stage wizardStage, Employee[] chosenEmployee, Task[] chosenTask) {
        TableView<Task> taskTableWizard = new TableView<>();
        taskTableWizard.getColumns().addAll(
                createTaskIdCol(), createTaskTypeCol(), createTaskStatusCol(), createTaskDurationCol()
        );
        List<Task> empTasks = tasksManager.getMapTasks().get(chosenEmployee[0]);
        taskTableWizard.getItems().setAll(empTasks);

        Button finishBtn = new Button("Toggle Status");
        finishBtn.setOnAction(e -> {
            Task sel = taskTableWizard.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("Selection Error", "Please select a task.");
                return;
            }
            chosenTask[0] = sel;
            tasksManager.modifyTaskStatus(chosenEmployee[0].getIdEmployee(), chosenTask[0].getIdTask());
            wizardStage.close();
            refreshStats();
        });

        VBox step2 = new VBox(10, new Label("Step 2: Select a Task to Toggle Status"), taskTableWizard, finishBtn);
        step2.setPadding(new Insets(10));
        return step2;
    }

    private Pane createStatsSection() {
        VBox statsBox = new VBox(5);
        Label label = new Label("Employee Task Stats:");
        statsTextArea = new TextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setPrefHeight(150);

        Button refreshStatsBtn = new Button("Refresh Stats");
        refreshStatsBtn.setOnAction(e -> refreshStats());

        statsBox.getChildren().addAll(label, statsTextArea, refreshStatsBtn);
        return statsBox;
    }

    private Button createSaveButton() {
        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                SerializationUtility.saveTasksManager(SERIALIZATION_FILE, tasksManager);
                showAlert("Save Successful", "Data saved to " + SERIALIZATION_FILE);
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Save Error", "Error saving data.");
            }
        });
        return saveBtn;
    }

    private void refreshStats() {
        statsTextArea.clear();
        Map<String, Map<String, Integer>> stats = new Utility().completedUncompletedEmployees(tasksManager);
        stats.forEach((empName, counts) -> {
            int completed = counts.getOrDefault("Completed", 0);
            int uncompleted = counts.getOrDefault("Uncompleted", 0);
            statsTextArea.appendText(empName + " -> Completed: " + completed +
                    ", Uncompleted: " + uncompleted + "\n");
        });
    }

    private TableColumn<Employee, Number> createEmployeeIdCol() {
        TableColumn<Employee, Number> col = new TableColumn<>("ID");
        col.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getIdEmployee()));
        return col;
    }

    private TableColumn<Employee, String> createEmployeeNameCol() {
        TableColumn<Employee, String> col = new TableColumn<>("Name");
        col.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleStringProperty(cd.getValue().getName()));
        return col;
    }

    private TableColumn<Task, Number> createTaskIdCol() {
        TableColumn<Task, Number> col = new TableColumn<>("Task ID");
        col.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getIdTask()));
        return col;
    }

    private TableColumn<Task, String> createTaskTypeCol() {
        TableColumn<Task, String> col = new TableColumn<>("Type");
        col.setCellValueFactory(cd -> {
            Task t = cd.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    t.getClass().getSimpleName().replace("Task", "")
            );
        });
        return col;
    }

    private TableColumn<Task, Boolean> createTaskStatusCol() {
        TableColumn<Task, Boolean> col = new TableColumn<>("Completed?");
        col.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleBooleanProperty(cd.getValue().isStatus()));
        return col;
    }

    private TableColumn<Task, Number> createTaskDurationCol() {
        TableColumn<Task, Number> col = new TableColumn<>("Est. Duration (h)");
        col.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().estimatedDuration()));
        return col;
    }

    private List<Task> getAllTasks() {
        Set<Task> assigned = new HashSet<>();
        tasksManager.getMapTasks().forEach((emp, list) -> {
            if (list != null) {
                assigned.addAll(list);
            }
        });
        List<Task> unassigned = tasksManager.getUnassignedTasks();
        if (unassigned == null) {
            unassigned = new ArrayList<>();
        }
        Set<Task> all = new HashSet<>(assigned);
        all.addAll(unassigned);
        return new ArrayList<>(all);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
