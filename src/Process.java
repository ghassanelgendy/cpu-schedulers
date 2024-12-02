
import java.util.ArrayList;
import java.util.List;
import static java.lang.Math.ceil;

class Process {
    private String name;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    int remainingTime;
    private double fcaiFactor;
    int quantum;
    private int waitingTime;
    private int turnaroundTime;
    private final List<Integer> quantumHistory;

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
    public String getName() { return name; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }
    public void setFcaiFactor(double fcaiFactor) { this.fcaiFactor = fcaiFactor; }
    public int getQuantum() { return quantum; }
    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }
    public List<Integer> getQuantumHistory() { return quantumHistory; }

    public int updateFcaiFactor(double V1, double V2) {
        return (int) ceil((10 - priority) + (arrivalTime / V1) + (remainingTime / V2));
    }
}
