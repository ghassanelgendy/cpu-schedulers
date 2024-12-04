package grob.group.cs341a3;

import java.util.ArrayList;
import java.util.List;

public class SRTF {
    static final int AGING_FACTOR = 4; // aging factor for starvation

    // Hesbet elTAT
    static void findTurnAroundTime(List<Process> processes) {
        for (Process p : processes) {
            p.setTurnaroundTime(p.getBurstTime() + p.getWaitingTime());
        }
    }

    //main
    static void schedule(List<Process> processes, int contextSwitchingTime) {
        int currentTime = 0; // start time of simu
        int completedProcesses = 0; // Number of processes completed
        int totalWaitingTime = 0, totalTurnaroundTime = 0;
        Process currentProcess = null; // elprocess el3aleha eldor
        List<String> executionHistory = new ArrayList<>(); // history of exec.

        System.out.println("\nProcesses " +
                " Burst Time " +
                " Arrival Time " +
                " Waiting Time " +
                " Turnaround Time");

        while (completedProcesses < processes.size()) {
            // Badawar 3la a2al remaining time
            Process nextProcess = null;
            int minRemainingTime = Integer.MAX_VALUE;

            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && !p.isComplete && p.getRemainingTime() < minRemainingTime) {
                    minRemainingTime = p.getRemainingTime();
                    nextProcess = p;
                }
            }

            // bazawed CS lw feh felinput
            if (nextProcess != currentProcess) {
                if (currentProcess != null && nextProcess != null) {
                    // bazawedo 3altime
                    for (int i = 0; i < contextSwitchingTime; i++) {
                        executionHistory.add("CS"); // bahoto felhistory
                        currentTime++;
                    }
                }
                currentProcess = nextProcess;
            }

            if (currentProcess == null) {
                // elprocessor a3ed fady
                executionHistory.add("Idle");
                currentTime++;
                continue;
            }

            // ba exec elprocess el3aleha eldor
            executionHistory.add(currentProcess.getName());
            currentProcess.decrementRemainingTime();
            currentTime++;

            // lw khelst ba2felha w aflag it (is complete : True)
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompleted(true);
                completedProcesses++;

                // Calculate waiting time for the completed process
                currentProcess.setWaitingTime(currentTime - currentProcess.getBurstTime() - currentProcess.getArrivalTime());
            }
        }

        // bahseb elTAT le kol elprocesses
        findTurnAroundTime(processes);

        // averages for stats
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

        // elgrantt chart
        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }
    }
}
