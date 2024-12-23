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
    double quantum;
    int waitingTime;
    int turnaroundTime;
    List<Double> quantumHistory;
    int endTime;
    int remainingQuantum;
    boolean isComplete;

    public Process(String name, int arrivalTime, int burstTime, int priority, double quantum, Color c) {
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
    // Getters and Setters
    public void decrementRemainingTime() {
        remainingTime--;
    }

    public void setCompleted(boolean haa) {
        isComplete = haa;
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

    public double getQuantum() {
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

    public double getFcaiFactor() {return fcaiFactor;}

    public int getPriority() {return priority;}

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public void setpriority(int priority) {this.priority = priority;}

    public void updateFcaiFactor(double V1, double V2) {
        System.out.println("FCAI Factor for " + name + ": " + (int) (ceil(10 - priority) + ceil(arrivalTime / V1) + ceil(remainingTime / V2)));
        this.fcaiFactor =  (double) ceil((10 - priority) + ceil(arrivalTime / V1) + ceil(remainingTime / V2));
    }

//    public void updateQuantum(double newQuantum) {
//        if (remainingQuantum == 0) {
//            quantum = quantum + 2;
//        } else {
//            quantum = quantum + remainingQuantum;
//        }
//        quantumHistory.add(quantum);
//    }
    public void updateQuantum(double newQuantum) {
        quantum = newQuantum;
        quantumHistory.add(quantum);
    }

}
