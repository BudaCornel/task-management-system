package com.example.pt2025_30422_buda_cornel_assignment1.dataModel;

public final class SimpleTask extends Task{
    private int startHour;
    private int endHour;

    public SimpleTask(int idTask, boolean status, int startHour, int endHour) {
        super(idTask, status);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }



    @Override
    public int estimatedDuration() {
        return endHour - startHour;
    }

    @Override
    public String toString() {
        return "Simple Task " + getIdTask() + " [" + startHour + "-" + endHour + "]";
    }
}
