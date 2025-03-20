package com.example.pt2025_30422_buda_cornel_assignment1.userInterface.tabs;

import com.example.pt2025_30422_buda_cornel_assignment1.businessLogic.TasksManager;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.ComplexTask;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.SimpleTask;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Task;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.*;
import java.util.*;

public class TaskTab extends VBox {

    private TasksManager tasksManager;
    private TableView<Task> taskTable;

    public TaskTab(TasksManager tasksManager) {
        this.tasksManager = tasksManager;
        setPadding(new Insets(10));
        setSpacing(10);
        initializeUI();
    }

    private void initializeUI() {
        taskTable = new TableView<>();
        taskTable.getColumns().addAll(createTaskIdCol(), createTaskTypeCol(), createTaskStatusCol(), createTaskDurationCol());
        refreshTaskTable();

        HBox btnBox = new HBox(10);
        Button addSimpleTaskBtn = new Button("Create Simple Task");
        addSimpleTaskBtn.setOnAction(e -> showAddSimpleTaskDialog());
        Button addComplexTaskBtn = new Button("Create Complex Task");
        addComplexTaskBtn.setOnAction(e -> showAddComplexTaskDialog());
        btnBox.getChildren().addAll(addSimpleTaskBtn, addComplexTaskBtn);

        getChildren().addAll(new Label("Tasks:"), taskTable, btnBox);
    }

    private void refreshTaskTable() {
        List<Task> allTasks = getAllTasks();
        taskTable.setItems(FXCollections.observableArrayList(allTasks));
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
                    return task;
                } catch (NumberFormatException ex) {
                    showAlert("Input Error", "Please enter valid numbers.");
                } catch (IllegalArgumentException ex) {
                    showAlert("Duplicate Error", ex.getMessage());
                }
            }
            return null;
        });
        Optional<SimpleTask> result = dialog.showAndWait();
        if (result.isPresent()) {
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
        listView.setItems(FXCollections.observableArrayList(allTasks));
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
        if (result.isPresent()) {
            refreshTaskTable();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
