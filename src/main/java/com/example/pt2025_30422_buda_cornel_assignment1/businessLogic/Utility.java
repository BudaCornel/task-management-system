package com.example.pt2025_30422_buda_cornel_assignment1.businessLogic;

import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Employee;
import com.example.pt2025_30422_buda_cornel_assignment1.dataModel.Task;

import java.util.*;
import java.util.stream.Collectors;

public class Utility {

    public static void sortInAscendingOrder40h(TasksManager manager) {
        Map<Employee, List<Task>> mapTasks = manager.getMapTasks();

        List<Employee> filteredEmployees = mapTasks.keySet().stream()
                .filter(emp -> manager.calculateEmployeeWorkDuration(emp.getIdEmployee()) > 40)
                .sorted(Comparator.comparingInt(employee -> manager.calculateEmployeeWorkDuration(employee.getIdEmployee())))
                .toList();

        filteredEmployees.forEach(emp -> System.out.println(emp.getName()));

    }

    public Map<String, Map<String, Integer>> completedUncompletedEmployees (TasksManager manager)
    {
        Map<Employee, List<Task>> mapTasks= manager.getMapTasks();
        Map<String, Map<String, Integer>> completedEmployees = new HashMap<>();

        for(Map.Entry<Employee, List<Task>> entry : mapTasks.entrySet()){
            Employee employee = entry.getKey();
            List<Task> tasks = entry.getValue();

            int completedCount = 0;
            int uncompletedCount = 0;

            for(Task task : tasks){
                if(task.isStatus())
                    completedCount++;
                else
                    uncompletedCount++;

            }
            Map<String, Integer> statusCount = new HashMap<>();
            statusCount.put("Completed", completedCount);
            statusCount.put("Uncompleted", uncompletedCount);

            completedEmployees.put(employee.getName(), statusCount);
        }




        return completedEmployees;
    }

}
