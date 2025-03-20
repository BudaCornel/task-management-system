package com.example.pt2025_30422_buda_cornel_assignment1.userInterface;

import com.example.pt2025_30422_buda_cornel_assignment1.businessLogic.TasksManager;
import com.example.pt2025_30422_buda_cornel_assignment1.dataAccess.SerializationUtility;
import com.example.pt2025_30422_buda_cornel_assignment1.userInterface.tabs.EmployeeTab;
import com.example.pt2025_30422_buda_cornel_assignment1.userInterface.tabs.HomeTab;
import com.example.pt2025_30422_buda_cornel_assignment1.userInterface.tabs.TaskTab;
import com.example.pt2025_30422_buda_cornel_assignment1.userInterface.tabs.UtilityTab;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import java.io.File;

public class MainApp extends Application {

    private static final String SERIALIZATION_FILE = "manager1.ser";
    private TasksManager tasksManager;

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

        Tab homeTab = new Tab("Home", new HomeTab(tasksManager));
        homeTab.setClosable(false);

        Tab employeeTab = new Tab("Employee", new EmployeeTab(tasksManager));
        employeeTab.setClosable(false);

        Tab taskTab = new Tab("Task", new TaskTab(tasksManager));
        taskTab.setClosable(false);

        Tab utilityTab = new Tab("Utility", new UtilityTab(tasksManager));
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

    public static void main(String[] args) {
        launch(args);
    }
}
