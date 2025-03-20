package com.example.pt2025_30422_buda_cornel_assignment1.userInterface.tabs;

import com.example.pt2025_30422_buda_cornel_assignment1.businessLogic.TasksManager;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Employee;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class UtilityTab extends VBox {

    private TasksManager tasksManager;
    private TableView<EmployeeDurationRow> durationTable;
    private ListView<String> sortedListView;

    public UtilityTab(TasksManager tasksManager) {
        this.tasksManager = tasksManager;
        setPadding(new Insets(10));
        setSpacing(10);
        initializeUI();
    }

    private void initializeUI() {
        Label durationLabel = new Label("Employee Work Duration");
        durationTable = new TableView<>();
        TableColumn<EmployeeDurationRow, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getId()));
        TableColumn<EmployeeDurationRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        TableColumn<EmployeeDurationRow, Number> workCol = new TableColumn<>("Work Duration");
        workCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getWorkDuration()));
        durationTable.getColumns().addAll(idCol, nameCol, workCol);
        durationTable.setItems(FXCollections.observableArrayList(buildDurationRows()));

        Label sortedLabel = new Label("Employees with >40h (Sorted Ascending by Work Duration)");
        sortedListView = new ListView<>();
        sortedListView.setItems(FXCollections.observableArrayList(buildStringsFromRows(buildOver40Rows())));

        Button refreshBtn = new Button("Refresh Utility Data");
        refreshBtn.setOnAction(e -> {
            durationTable.setItems(FXCollections.observableArrayList(buildDurationRows()));
            sortedListView.setItems(FXCollections.observableArrayList(buildStringsFromRows(buildOver40Rows())));
        });

        getChildren().addAll(durationLabel, durationTable,
                new Separator(),
                sortedLabel, sortedListView,
                refreshBtn);
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
            result.add("ID=" + row.getId() + ", Name=" + row.getName() + ", Duration=" + row.getWorkDuration());
        }
        return result;
    }

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
}
