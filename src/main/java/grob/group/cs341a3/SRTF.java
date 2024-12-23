package grob.group.cs341a3;

import java.util.ArrayList;
import java.util.List;

public class SRTF {
    static final int starvationThreshold = 10; // elprocess testana ad eh lehad maoul enha btestarve?

    // tat
    static void findTurnAroundTime(List<Process> processes) {
        for (Process p : processes) {
            p.setTurnaroundTime(p.getBurstTime() + p.getWaitingTime());
        }
    }

    // Scheduler
    static void schedule(List<Process> processes, int contextSwitchingTime) {
        int currentTime = 0; // Simulated clock
        int completedProcesses = 0;
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        Process currentProcess = null;
        List<String> executionHistory = new ArrayList<>();

        // Header
        System.out.println("\nProcesses\tBurst Time\tArrival Time\tWaiting Time\tTurnaround Time");

        while (completedProcesses < processes.size()) {
            Process nextProcess = null;
            int minRemainingTime = 99999;
            boolean starvationOccurred = false;
            Process starvedProcess = null;

            // Check for starvation
            for (Process p : processes) {
                if (!p.isComplete && p.getArrivalTime() <= currentTime) {
                    int waitingTime = currentTime - p.getArrivalTime() - (p.getBurstTime() - p.getRemainingTime());
                    if (waitingTime > starvationThreshold) {
                        starvationOccurred = true;
                        if (starvedProcess == null || p.getArrivalTime() < starvedProcess.getArrivalTime()) {
                            starvedProcess = p; // Pick the earliest-arriving starved process
                        }
                    }
                }
            }

            // Select the next process
            if (starvationOccurred && starvedProcess != null) {
                nextProcess = starvedProcess; // Override elprocees elgya belstarved process
            } else {
                for (Process p : processes) {
                    if (p.getArrivalTime() <= currentTime && !p.isComplete) {
                        if (p.getRemainingTime() < minRemainingTime) {
                            minRemainingTime = p.getRemainingTime();
                            nextProcess = p;
                        }
                    }
                }
            }

            // Context Switching
            if (nextProcess != currentProcess) {
                if (currentProcess != null && nextProcess != null) {
                    for (int j = 0; j < contextSwitchingTime; j++) {
                        executionHistory.add("CS");
                        currentTime++;
                    }
                }
                currentProcess = nextProcess;
            }

            // CPU Idle
            if (currentProcess == null) {
                executionHistory.add("Idle");
                currentTime++;
                continue;
            }

            // Execute the process
            executionHistory.add(currentProcess.getName());
            currentProcess.decrementRemainingTime();
            currentTime++;

            // Mark process as complete
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompleted(true);
                completedProcesses++;
                currentProcess.setWaitingTime(currentTime - currentProcess.getBurstTime() - currentProcess.getArrivalTime());
            }
        }

        // Calculate turnaround times and print stats
        findTurnAroundTime(processes);
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
            System.out.println(p.getName() + "\t\t\t" + p.getBurstTime() + "\t\t\t" + p.getArrivalTime()
                    + "\t\t\t" + p.getWaitingTime() + "\t\t\t" + p.getTurnaroundTime());
        }

        float avgWT = (float) totalWaitingTime / processes.size();
        float avgTAT = (float) totalTurnaroundTime / processes.size();

        System.out.println("\nAverage Waiting Time = " + avgWT);
        System.out.println("Average Turnaround Time = " + avgTAT);

        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }

        // Graph
        CPUSchedulerGraph.draw(executionHistory, (ArrayList<Process>) processes, avgWT, avgTAT);
    }
}
