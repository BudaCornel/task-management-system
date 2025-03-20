package com.example.pt2025_30422_buda_cornel_assignment1.dataModel;

import java.io.Serializable;

public sealed abstract class Task implements Serializable permits SimpleTask, ComplexTask {
    private static final long serialVersionUID = 1L;
    private int idTask;
    private boolean status;

    public Task(int idTask, boolean status) {
        this.idTask = idTask;
        this.status = status;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public abstract int estimatedDuration();
}
