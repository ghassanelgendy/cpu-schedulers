package grob.group.cs341a3;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.ceil;

class Process {
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

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.quantum = quantum;
        this.quantumHistory = new ArrayList<>();
        this.quantumHistory.add(quantum);


    }

    // Getters and Setters
    public void decrementRemainingTime(){remainingTime--;}
    public void setCompleted(boolean haa){isComplete = haa;}
    public void setEndTime(int t){endTime = t;}
    public int getStartTime() { return startTime; }
    public void setPriority(int p){priority = p;}
    public int getPriority(){return priority;}
    public int getCompletionTime() { return completionTime; }
    public int getResponseTime() { return responseTime; }
    public String getName() { return name; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }
    public void setFcaiFactor(double fcaiFactor) { this.fcaiFactor = fcaiFactor; }
    public int getQuantum() { return quantum; }
    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }
    public int getEndTime() { return endTime; }

    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }
    public List<Integer> getQuantumHistory() { return quantumHistory; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }
    public void setStartTime(int startTime) { this.startTime = startTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }
    public void setResponseTime(int responseTime) { this.responseTime = responseTime; }
    public int updateFcaiFactor(double V1, double V2) {
        return (int) ceil((10 - priority) + (arrivalTime / V1) + (remainingTime / V2));
    }
}
