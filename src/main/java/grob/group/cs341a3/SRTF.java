package grob.group.cs341a3;

import java.util.ArrayList;
import java.util.List;

public class SRTF {
    static final int AGING_FACTOR = 4; // aging factor for starvation

    // Method to calculate turnaround time
    static void findTurnAroundTime(List<Process> processes) {
        for (Process p : processes) {
            p.setTurnaroundTime(p.getBurstTime() + p.getWaitingTime());
        }
    }

    // Method to implement SRTF with context switching and execution history
    static void schedule(List<Process> processes, int contextSwitchingTime) {
        int currentTime = 0; // Current time in simulation
        int completedProcesses = 0; // Number of processes completed
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        Process currentProcess = null; // Process currently being executed
        List<String> executionHistory = new ArrayList<>(); // Record of execution history

        System.out.println("\nProcesses " +
                " Burst Time " +
                " Arrival Time " +
                " Waiting Time " +
                " Turnaround Time");

        while (completedProcesses < processes.size()) {
            // Find the process with the shortest remaining time
            Process nextProcess = null;
            int minRemainingTime = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && !p.isComplete && p.getRemainingTime() < minRemainingTime) {
                    minRemainingTime = p.getRemainingTime();
                    nextProcess = p;
                }
            }

            // Check if context switch is needed
            if (nextProcess != currentProcess) {
                if (currentProcess != null && nextProcess != null) {
                    // Apply context switching time if there's a switch
                    for (int i = 0; i < contextSwitchingTime; i++) {
                        executionHistory.add("CS"); // Record context switching time
                        currentTime++;
                    }
                }
                currentProcess = nextProcess;
            }

            if (currentProcess == null) {
                // No process available, record idle time
                executionHistory.add("Idle");
                currentTime++;
                continue;
            }

            // Execute the current process for 1 unit of time
            executionHistory.add(currentProcess.getName());
            currentProcess.decrementRemainingTime();
            currentTime++;

            // Check if the process is completed
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompleted(true);
                completedProcesses++;

                // Calculate waiting time for the completed process
                currentProcess.setWaitingTime(currentTime - currentProcess.getBurstTime() - currentProcess.getArrivalTime());
            }
        }

        // Calculate turnaround time
        findTurnAroundTime(processes);

        // Print process details and calculate averages
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();

            System.out.println(" " + p.getName() + "\t\t\t"
                    + p.getBurstTime() + "\t\t\t "
                    + p.getArrivalTime() + "\t\t\t "
                    + p.getWaitingTime() + "\t\t\t\t"
                    + p.getTurnaroundTime());
        }

        System.out.println("\nAverage Waiting Time = " + (float) totalWaitingTime / processes.size());
        System.out.println("Average Turnaround Time = " + (float) totalTurnaroundTime / processes.size());

        // Print execution history
        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }
    }
}
