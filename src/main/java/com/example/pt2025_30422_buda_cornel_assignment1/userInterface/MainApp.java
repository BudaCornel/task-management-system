package com.example.pt2025_30422_buda_cornel_assignment1.userInterface;

import com.example.pt2025_30422_buda_cornel_assignment1.businessLogic.TasksManager;
import com.example.pt2025_30422_buda_cornel_assignment1.businessLogic.Utility;
import com.example.pt2025_30422_buda_cornel_assignment1.dataAccess.SerializationUtility;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.ComplexTask;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Employee;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.SimpleTask;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Task;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.stream.Collectors;


import java.io.File;
import java.util.*;

public class MainApp extends Application {

    private static final String SERIALIZATION_FILE = "manager.ser";


    private TasksManager tasksManager;


    private TextArea statsTextArea;
    private TableView<Employee> employeeTable;
    private TableView<Task> taskTable;


    public static class EmployeeDurationRow {
        private final int id;
        private final String name;
        private final int workDuration;

        public EmployeeDurationRow(int id, String name, int workDuration) {
            this.id = id;
            this.name = name;
            this.workDuration = workDuration;
        }
        public int getId() { return id; }
        public String getName() { return name; }
        public int getWorkDuration() { return workDuration; }
    }

    @Override
    public void start(Stage primaryStage) {
       File file = new File(SERIALIZATION_FILE);
        if (file.exists()) {
            try {
                tasksManager = SerializationUtility.loadTasksManager(SERIALIZATION_FILE);
                System.out.println("TasksManager loaded from " + SERIALIZATION_FILE);
            } catch (Exception e) {
                e.printStackTrace();
                tasksManager = new TasksManager();
            }
        } else {
            tasksManager = new TasksManager();
        }

        TabPane tabPane = new TabPane();

        Tab homeTab = new Tab("Home", createHomeTabContent());
        homeTab.setClosable(false);

        Tab employeeTab = new Tab("Employee", createEmployeeTabContent());
        employeeTab.setClosable(false);

        Tab taskTab = new Tab("Task", createTaskTabContent());
        taskTab.setClosable(false);

        Tab utilityTab = new Tab("Utility", createUtilityTabContent());
        utilityTab.setClosable(false);

        tabPane.getTabs().addAll(homeTab, employeeTab, taskTab, utilityTab);

        Scene scene = new Scene(tabPane, 1000, 600);
        primaryStage.setTitle("Task Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        try {
            SerializationUtility.saveTasksManager(SERIALIZATION_FILE, tasksManager);
            System.out.println("TasksManager saved to " + SERIALIZATION_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Pane createHomeTabContent() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(10));


        HBox wizardBox = new HBox(15);
        Button assignTaskBtn = new Button("Assign Task");
        assignTaskBtn.setOnAction(e -> showAssignWizard());
        Button modifyStatusBtn = new Button("Modify Task Status");
        modifyStatusBtn.setOnAction(e -> showModifyWizard());
        wizardBox.getChildren().addAll(assignTaskBtn, modifyStatusBtn);


        Button refreshStatsBtn = new Button("Refresh Stats");
        refreshStatsBtn.setOnAction(e -> refreshStats());
        statsTextArea = new TextArea();
        statsTextArea.setEditable(false);
        statsTextArea.setPrefHeight(150);
        VBox statsSection = new VBox(5, new Label("Employee Task Stats:"), statsTextArea, refreshStatsBtn);


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

        root.getChildren().addAll(new Label("Assignment / Modification:"), wizardBox,
                new Separator(), statsSection, new Separator(), saveBtn);
        return root;
    }

    private void refreshStats() {
        statsTextArea.clear();
        Map<String, Map<String, Integer>> stats = new Utility().completedUncompletedEmployees(tasksManager);
        for (String empName : stats.keySet()) {
            Map<String, Integer> counts = stats.get(empName);
            int completed = counts.getOrDefault("Completed", 0);
            int uncompleted = counts.getOrDefault("Uncompleted", 0);
            statsTextArea.appendText(empName + " -> Completed: " + completed +
                    ", Uncompleted: " + uncompleted + "\n");
        }
    }


    private Pane createUtilityTabContent() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));


        Label durationLabel = new Label("Employee Work Duration");
        TableView<EmployeeDurationRow> durationTable = new TableView<>();

        TableColumn<EmployeeDurationRow, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()));
        TableColumn<EmployeeDurationRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        TableColumn<EmployeeDurationRow, Number> workCol = new TableColumn<>("Work Duration");
        workCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getWorkDuration()));
        durationTable.getColumns().addAll(idCol, nameCol, workCol);
        durationTable.getItems().setAll(buildDurationRows());

        Label sortedLabel = new Label("Employees with >40h (Sorted Ascending by Work Duration)");
        ListView<String> sortedListView = new ListView<>();
        List<EmployeeDurationRow> over40Rows = buildOver40Rows();
        List<String> over40Strings = buildStringsFromRows(over40Rows);
        sortedListView.getItems().setAll(over40Strings);

        Button refreshBtn = new Button("Refresh Utility Data");
        refreshBtn.setOnAction(e -> {
            durationTable.getItems().setAll(buildDurationRows());
            List<EmployeeDurationRow> newOver40 = buildOver40Rows();
            List<String> newStrings = buildStringsFromRows(newOver40);
            sortedListView.getItems().setAll(newStrings);
        });

        root.getChildren().addAll(durationLabel, durationTable,
                new Separator(),
                sortedLabel, sortedListView,
                refreshBtn);
        return root;
    }

    private List<EmployeeDurationRow> buildDurationRows() {
        List<EmployeeDurationRow> rows = new ArrayList<>();
        for (Employee emp : tasksManager.getMapTasks().keySet()) {
            int workDuration = tasksManager.calculateEmployeeWorkDuration(emp.getIdEmployee());
            rows.add(new EmployeeDurationRow(emp.getIdEmployee(), emp.getName(), workDuration));
        }
        return rows;
    }

    private List<EmployeeDurationRow> buildOver40Rows() {
        List<EmployeeDurationRow> all = buildDurationRows();
        List<EmployeeDurationRow> over40 = new ArrayList<>();
        for (EmployeeDurationRow row : all) {
            if (row.getWorkDuration() > 40) {
                over40.add(row);
            }
        }
        over40.sort(Comparator.comparingInt(EmployeeDurationRow::getWorkDuration));
        return over40;
    }

    private List<String> buildStringsFromRows(List<EmployeeDurationRow> rows) {
        List<String> result = new ArrayList<>();
        for (EmployeeDurationRow row : rows) {
            result.add("ID=" + row.getId() +
                    ", Name=" + row.getName() +
                    ", Duration=" + row.getWorkDuration());
        }
        return result;
    }

    private Pane createEmployeeTabContent() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

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
            String tasksString;
            if (tasks != null && !tasks.isEmpty()) {
                tasksString = tasks.stream()
                        .map(t -> t.getIdTask() + ":" + (t.isStatus() ? "Completed" : "Not Completed"))
                        .collect(Collectors.joining(", "));
            } else {
                tasksString = "None";
            }
            return new javafx.beans.property.SimpleStringProperty(tasksString);
        });

        employeeTable.getColumns().addAll(empIdCol, empNameCol, tasksCol);
        refreshEmployeeTable();

        Button refreshButton = new Button("Refresh Employee Data");
        refreshButton.setOnAction(e -> refreshEmployeeTable());

        root.getChildren().addAll(new Label("Add Employee:"), inputBox,
                new Label("All Employees:"), employeeTable, refreshButton);
        return root;
    }



    private void refreshEmployeeTable() {
        List<Employee> employees = new ArrayList<>(tasksManager.getMapTasks().keySet());
        employeeTable.getItems().setAll(employees);
    }


    private Pane createTaskTabContent() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        taskTable = new TableView<>();
        TableColumn<Task, Number> taskIdCol = new TableColumn<>("Task ID");
        taskIdCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().getIdTask()));
        TableColumn<Task, String> taskTypeCol = new TableColumn<>("Type");
        taskTypeCol.setCellValueFactory(cd -> {
            Task t = cd.getValue();
            String typeName = (t instanceof SimpleTask) ? "Simple" : "Complex";
            return new javafx.beans.property.SimpleStringProperty(typeName);
        });
        TableColumn<Task, Boolean> taskStatusCol = new TableColumn<>("Completed?");
        taskStatusCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleBooleanProperty(cd.getValue().isStatus()));
        TableColumn<Task, Number> taskDurationCol = new TableColumn<>("Est. Duration (h)");
        taskDurationCol.setCellValueFactory(cd ->
                new javafx.beans.property.SimpleIntegerProperty(cd.getValue().estimatedDuration()));
        taskTable.getColumns().addAll(taskIdCol, taskTypeCol, taskStatusCol, taskDurationCol);
        refreshTaskTable();

        HBox btnBox = new HBox(10);
        Button addSimpleTaskBtn = new Button("Create Simple Task");
        addSimpleTaskBtn.setOnAction(e -> showAddSimpleTaskDialog());
        Button addComplexTaskBtn = new Button("Create Complex Task");
        addComplexTaskBtn.setOnAction(e -> showAddComplexTaskDialog());
        btnBox.getChildren().addAll(addSimpleTaskBtn, addComplexTaskBtn);

        root.getChildren().addAll(new Label("Tasks:"), taskTable, btnBox);
        return root;
    }

    private void refreshTaskTable() {
        List<Task> allTasks = getAllTasks();
        taskTable.getItems().setAll(allTasks);
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

    private void showAssignWizard() {
        Stage wizardStage = new Stage();
        wizardStage.setTitle("Assign Task Wizard");

        final Employee[] chosenEmployee = new Employee[1];
        final Task[] chosenTask = new Task[1];

        TableView<Employee> empTableWizard = new TableView<>();
        empTableWizard.getColumns().addAll(createEmployeeIdCol(), createEmployeeNameCol());
        empTableWizard.getItems().setAll(new ArrayList<>(tasksManager.getMapTasks().keySet()));
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
        taskTableWizard.getColumns().addAll(createTaskIdCol(), createTaskTypeCol(), createTaskStatusCol(), createTaskDurationCol());
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
            refreshTaskTable();
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
        tasksManager.getMapTasks().forEach((emp, list) -> {
            if (list != null && !list.isEmpty()) {
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
        taskTableWizard.getColumns().addAll(createTaskIdCol(), createTaskTypeCol(), createTaskStatusCol(), createTaskDurationCol());
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
            refreshTaskTable();
        });
        VBox step2 = new VBox(10, new Label("Step 2: Select a Task to Toggle Status"), taskTableWizard, finishBtn);
        step2.setPadding(new Insets(10));
        return step2;
    }

    private void showAddSimpleTaskDialog() {
        Dialog<SimpleTask> dialog = new Dialog<>();
        dialog.setTitle("Create Simple Task");
        dialog.setHeaderText("Enter details for a new Simple Task");

        Label idLabel = new Label("Task ID:");
        TextField idField = new TextField();
        Label startLabel = new Label("Start Hour:");
        TextField startField = new TextField();
        Label endLabel = new Label("End Hour:");
        TextField endField = new TextField();
        CheckBox statusCheck = new CheckBox("Completed?");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(idLabel, 0, 0);
        grid.add(idField, 1, 0);
        grid.add(startLabel, 0, 1);
        grid.add(startField, 1, 1);
        grid.add(endLabel, 0, 2);
        grid.add(endField, 1, 2);
        grid.add(statusCheck, 1, 3);

        dialog.getDialogPane().setContent(grid);
        ButtonType addBtn = new ButtonType("Add", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);
        dialog.setResultConverter(button -> {
            if (button == addBtn) {
                try {
                    int taskId = Integer.parseInt(idField.getText().trim());
                    int start = Integer.parseInt(startField.getText().trim());
                    int end = Integer.parseInt(endField.getText().trim());
                    if (start <= 0 || end <= 0) {
                        showAlert("Input Error", "Start and End hours must be positive.");
                        return null;
                    }
                    if (start >= end) {
                        showAlert("Input Error", "Start hour must be less than End hour.");
                        return null;
                    }
                    SimpleTask task = new SimpleTask(taskId, statusCheck.isSelected(), start, end);
                    tasksManager.addUnassignedTask(task);
                } catch (NumberFormatException ex) {
                    showAlert("Input Error", "Please enter valid numbers.");
                } catch (IllegalArgumentException ex) {
                    showAlert("Duplicate Error", ex.getMessage());
                }
            }
            return null;
        });
        Optional<SimpleTask> result = dialog.showAndWait();
        if(result.isPresent()){
            refreshTaskTable();
        }
    }

    private void showAddComplexTaskDialog() {
        Dialog<ComplexTask> dialog = new Dialog<>();
        dialog.setTitle("Create Complex Task");
        dialog.setHeaderText("Select sub-tasks to add to the complex task");

        Label idLabel = new Label("Complex Task ID:");
        TextField idField = new TextField();
        CheckBox statusCheck = new CheckBox("Completed?");
         List<Task> allTasks = getAllTasks();
        ListView<Task> listView = new ListView<>();
        listView.getItems().setAll(allTasks);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        VBox vbox = new VBox(10,
                new HBox(10, idLabel, idField),
                statusCheck,
                new Label("Select Tasks (Ctrl+Click for multiple):"),
                listView);
        vbox.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(vbox);
        ButtonType createBtn = new ButtonType("Create", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);
        dialog.setResultConverter(button -> {
            if (button == createBtn) {
                try {
                    int complexId = Integer.parseInt(idField.getText().trim());
                    boolean status = statusCheck.isSelected();
                    List<Task> selected = listView.getSelectionModel().getSelectedItems();
                    if (selected.isEmpty()) {
                        showAlert("Selection Error", "Please select at least one sub-task.");
                        return null;
                    }
                    ComplexTask complexTask = new ComplexTask(complexId, status, new ArrayList<>(selected));
                    tasksManager.addUnassignedTask(complexTask);
                    return complexTask;
                } catch (NumberFormatException ex) {
                    showAlert("Input Error", "Please enter a valid integer ID.");
                } catch (IllegalArgumentException ex) {
                    showAlert("Duplicate Error", ex.getMessage());
                }
            }
            return null;
        });
        Optional<ComplexTask> result = dialog.showAndWait();
        if(result.isPresent()){
            refreshTaskTable();
        }
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
            String typeName = (t instanceof SimpleTask) ? "Simple" : "Complex";
            return new javafx.beans.property.SimpleStringProperty(typeName);
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

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
