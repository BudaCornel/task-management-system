package com.example.pt2025_30422_buda_cornel_assignment1.dataModel;

import java.util.List;

public final class ComplexTask extends Task{
    private List<Task> tasks;

    public ComplexTask(int idTask, boolean status, List tasks) {
        super(idTask, status);
        this.tasks = tasks;
    }


    @Override
    public int estimatedDuration() {
        int duration = 0;
        for(Task task : tasks)
            duration += task.estimatedDuration();
        return duration;
    }

    public void addTask(Task task)
    {
        tasks.add(task);
    }

    @Override
    public String toString() {
        return "Complex Task " + getIdTask() + " (Subtasks: " + tasks.size() + ")";
    }

}
