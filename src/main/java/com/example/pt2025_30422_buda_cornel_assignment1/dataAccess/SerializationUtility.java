package com.example.pt2025_30422_buda_cornel_assignment1.dataAccess;

import com.example.pt2025_30422_buda_cornel_assignment1.businessLogic.TasksManager;

import java.io.*;

public class SerializationUtility {
    public static void saveTasksManager(String filePath, TasksManager manager) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(manager);
        }
    }

    public static TasksManager loadTasksManager(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (TasksManager) ois.readObject();
        }
    }
}
