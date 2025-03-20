package com.example.pt2025_30422_buda_cornel_assignment1.businessLogic;

import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Employee;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Task;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<Employee, List<Task>> mapTasks = new HashMap<>();
    private List<Task> unassignedTasks = new ArrayList<>();


    public boolean employeeExists(int id) {
        for (Employee e : mapTasks.keySet()) {
            if (e.getIdEmployee() == id) {
                return true;
            }
        }
        return false;
    }


    public boolean taskExists(int id) {

        for (List<Task> tasks : mapTasks.values()) {
            for (Task t : tasks) {
                if (t.getIdTask() == id) {
                    return true;
                }
            }
        }

        if (unassignedTasks != null) {
            for (Task t : unassignedTasks) {
                if (t.getIdTask() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addEmployee(Employee employee) {
        if (employeeExists(employee.getIdEmployee())) {
            throw new IllegalArgumentException("Employee with id " + employee.getIdEmployee() + " already exists.");
        }
        mapTasks.put(employee, new ArrayList<>());
    }


    public void assignTaskToEmployee(int idEmployee, Task task) {
        for (Map.Entry<Employee, List<Task>> entry : mapTasks.entrySet()) {
            Employee employee = entry.getKey();
            if (employee.getIdEmployee() == idEmployee) {
                List<Task> tasks = entry.getValue();

                for (Task t : tasks) {
                    if (t.getIdTask() == task.getIdTask()) {
                        throw new IllegalArgumentException("Task with id " + task.getIdTask()
                                + " is already assigned to employee " + employee.getIdEmployee());
                    }
                }
                tasks.add(task);
            }
        }
    }



    public int calculateEmployeeWorkDuration(int idEmployee) {
        for (Map.Entry<Employee, List<Task>> entry : mapTasks.entrySet()) {
            Employee employee = entry.getKey();
            if (employee.getIdEmployee() == idEmployee) {
                int estimatedWorkDurationEmployee = 0;
                for (Task task : entry.getValue()) {
                    if (task.isStatus()) {
                        estimatedWorkDurationEmployee += task.estimatedDuration();
                    }
                }
                return estimatedWorkDurationEmployee;
            }
        }
        return 0;
    }



    public void modifyTaskStatus(int idEmployee, int idTask) {
        for (Map.Entry<Employee, List<Task>> entry : mapTasks.entrySet()) {
            Employee employee = entry.getKey();
            if (employee.getIdEmployee() == idEmployee) {
                for (Task task : entry.getValue()) {
                    if (task.getIdTask() == idTask) {
                        task.setStatus(!task.isStatus());
                    }
                }
            }
        }
    }

    public Map<Employee, List<Task>> getMapTasks() {
        return mapTasks;
    }


    public void addUnassignedTask(Task t) {
        if (taskExists(t.getIdTask())) {
            throw new IllegalArgumentException("Task with id " + t.getIdTask() + " already exists.");
        }
        if (unassignedTasks == null) {
            unassignedTasks = new ArrayList<>();
        }
        unassignedTasks.add(t);
    }

    public void removeUnassignedTask(Task t) {
        if (unassignedTasks == null) {
            unassignedTasks = new ArrayList<>();
        }
        unassignedTasks.remove(t);
    }

    public List<Task> getUnassignedTasks() {
        if (unassignedTasks == null) {
            unassignedTasks = new ArrayList<>();
        }
        return unassignedTasks;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (unassignedTasks == null) {
            unassignedTasks = new ArrayList<>();
        }
    }
}
