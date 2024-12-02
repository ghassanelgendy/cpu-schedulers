import java.util.ArrayList;
import java.util.List;

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int priority;
    int remainingTime;
    double fcaiFactor;
    int quantum;
    int waitingTime = 0;
    int turnaroundTime = 0;
    List<Integer> quantumHistory = new ArrayList<>();

    Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.quantum = quantum;
        this.quantumHistory.add(quantum);
    }
}
