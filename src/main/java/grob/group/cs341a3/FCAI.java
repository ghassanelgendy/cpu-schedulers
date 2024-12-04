package grob.group.cs341a3;

import java.util.*;

public class FCAI {
    public static void schedule(List<Process> processes) {
        // Calculate V1 and V2
        double v1 = calculateV1(processes);
        double v2 = calculateV2(processes);

        // Initialize ready queue (sorted by FCAI factor)
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingDouble(p -> p.updateFcaiFactor(v1, v2)));
        List<String> executionHistory = new ArrayList<>(); // Execution history tracker
        Map<String, Process> allProcesses = new HashMap<>();
        processes.forEach(p -> allProcesses.put(p.name, p));

        int currentTime = 0;
        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            // Add newly arrived processes to the ready queue
            for (Iterator<Process> it = processes.iterator(); it.hasNext(); ) {
                Process p = it.next();
                if (p.arrivalTime <= currentTime) {
                    p.updateFcaiFactor(v1, v2);
                    readyQueue.add(p);
                    it.remove();
                }
            }

            if (!readyQueue.isEmpty()) {
                Process current = readyQueue.poll();
                int executionTime = Math.min(current.quantum, current.remainingTime);

                // Execute process and update current time
                for (int t = 0; t < executionTime; t++) {
                    executionHistory.add(current.name);
                }
                currentTime += executionTime;
                current.remainingTime -= executionTime;

                // Check if the process is completed
                if (current.remainingTime == 0) {
                    current.turnaroundTime = currentTime - current.arrivalTime;
                    current.waitingTime = current.turnaroundTime - current.burstTime;
                } else {
                    // Preemption or continuation
                    if (executionTime >= (0.4 * current.quantum)) {
                        current.quantum += executionTime < current.quantum
                                ? current.quantum - executionTime
                                : 2; // Adjust quantum
                    }
                    current.updateFcaiFactor(v1, v2);
                    readyQueue.add(current);
                }
            } else {
                // No process ready to execute, increment time
                executionHistory.add("Idle");
                currentTime++;
            }
        }

        // Display results
        displayResults(allProcesses.values(), executionHistory);
    }

    private static double calculateV1(List<Process> processes) {
        return processes.stream().mapToInt(p -> p.arrivalTime).max().orElse(1);
    }

    private static double calculateV2(List<Process> processes) {
        return processes.stream().mapToInt(p -> p.burstTime).max().orElse(1);
    }

    private static void displayResults(Collection<Process> processes, List<String> executionHistory) {
        System.out.println("\nProcesses " +
                " Burst Time " +
                " Arrival Time " +
                " Waiting Time " +
                " Turnaround Time");

        for (Process process : processes) {
            System.out.printf("%-10s %-12d %-12d %-12d %-14d\n",
                    process.name, process.burstTime, process.arrivalTime,
                    process.waitingTime, process.turnaroundTime);
        }

        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }
    }
}
