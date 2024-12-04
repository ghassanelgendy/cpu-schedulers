// SRTF with aging factor to handle starvation
package grob.group.cs341a3;

import java.util.ArrayList;
import java.util.List;

public class SRTF {
    static final int AGING_FACTOR = 4; // aging factor for starvation

    // Calculate Turnaround Time
    static void findTurnAroundTime(List<Process> processes) {
        for (Process p : processes) {
            p.setTurnaroundTime(p.getBurstTime() + p.getWaitingTime());
            System.out.println("start time: " + p.startTime + "\nEnd Time: "+ p.endTime);
        }
    }

    // Main Scheduler with Aging Factor
    static void schedule(List<Process> processes, int contextSwitchingTime) {
        int currentTime = 0; // simulation start time
        int completedProcesses = 0; // completed processes count
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        Process currentProcess = null; // current running process
        List<String> executionHistory = new ArrayList<>(); // execution history

        System.out.println("\nProcesses " +
                " Burst Time " +
                " Arrival Time " +
                " Waiting Time " +
                " Turnaround Time");

        while (completedProcesses < processes.size()) {



            // Select the next process with the minimum effective remaining time
            Process nextProcess = null;
            int minEffectiveTime = Integer.MAX_VALUE;

            // Apply aging to all waiting processes
            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && !p.isComplete && p.getEffectiveRemainingTime(currentTime, AGING_FACTOR) < minEffectiveTime) {
                    minEffectiveTime = p.getEffectiveRemainingTime(currentTime, AGING_FACTOR);
                    nextProcess = p;
                }
            }

            // Handle context switching
            if (nextProcess != currentProcess) {
                if (currentProcess != null && nextProcess != null) {
                    for (int i = 0; i < contextSwitchingTime; i++) {
                        executionHistory.add("CS");
                        currentTime++;
                    }
                }
                currentProcess = nextProcess;
            }

            if (currentProcess == null) {
                // Idle time if no process is available
                executionHistory.add("Idle");
                currentTime++;
                continue;
            }

            // Execute the selected process
            executionHistory.add(currentProcess.getName());
            currentProcess.decrementRemainingTime();
            currentTime++;

            // If the process completes, mark it as complete
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompleted(true);
                completedProcesses++;
                currentProcess.setWaitingTime(currentTime - currentProcess.getBurstTime() - currentProcess.getArrivalTime());
            }
        }

        // Calculate Turnaround Times
        findTurnAroundTime(processes);

        // Calculate averages and display process stats
        float avgWT = 0, avgTAT = 0;
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();

            System.out.println(" " + p.getName() + "\t\t\t"
                    + p.getBurstTime() + "\t\t\t "
                    + p.getArrivalTime() + "\t\t\t "
                    + p.getWaitingTime() + "\t\t\t\t"
                    + p.getTurnaroundTime());
        }
        avgWT = (float) totalWaitingTime / processes.size();
        avgTAT = (float) totalTurnaroundTime / processes.size();

        System.out.println("\nAverage Waiting Time = " + avgWT);
        System.out.println("Average Turnaround Time = " + avgTAT);

        // Display execution history
        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }

        // Draw the CPU scheduler graph
        CPUSchedulerGraph.draw(executionHistory, (ArrayList<Process>) processes, avgWT, avgTAT);
    }
}
