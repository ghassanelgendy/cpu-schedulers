package grob.group.cs341a3;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.ceil;

class Process {

    Color color;
    String name;
    int arrivalTime;
    int burstTime;
    int priority;
    int remainingTime;
    double fcaiFactor;
    int quantum;
    int waitingTime;
    int turnaroundTime;
    List<Integer> quantumHistory;
    int startTime = -1; // Initialized to -1 to indicate not started
    int endTime;
    int completionTime;
    int responseTime;
    boolean isComplete;

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum, Color c) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.quantum = quantum;
        this.quantumHistory = new ArrayList<>();
        this.quantumHistory.add(quantum);
        this.color = c;


    }

    public int getEffectiveRemainingTime(int currentTime, int agingFactor) {
        int waitingTime = currentTime - arrivalTime;
        int agingAdjustment = waitingTime / agingFactor;
        return Math.max(remainingTime - agingAdjustment, 0); // Effective time cannot go below 0
    }

    // Getters and Setters
    public void decrementRemainingTime() {
        remainingTime--;
    }

    public void setCompleted(boolean haa) {
        isComplete = haa;
    }

    public int getPriority() {
        return priority;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getQuantum() {
        return quantum;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public List<Integer> getQuantumHistory() {
        return quantumHistory;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public double updateFcaiFactor(double V1, double V2) {
        System.out.println("FCAI Factor for " + name + ": " + (int) (ceil(10 - priority) + ceil(arrivalTime / V1) + ceil(remainingTime / V2)));
        fcaiFactor =  (int) (ceil(10 - priority) + ceil(arrivalTime / V1) + ceil(remainingTime / V2));
        return fcaiFactor;
    }
}
