package grob.group.cs341a3;

import java.util.ArrayList;
import java.util.List;

public class SRTF {
    static final int p_factor = 5; // Priority factor lelstarvation
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

        // header
        System.out.println("\nProcesses\tBurst Time\tArrival Time\tWaiting Time\tTurnaround Time");

        while (completedProcesses < processes.size()) {
            Process nextProcess = null;
            int minRemainingTime = Integer.MAX_VALUE;
            boolean starvationOccurred = false;

            // Check for starvation
            for (Process p : processes) {
                if (!p.isComplete && p.getArrivalTime() <= currentTime) { //lw elprocess makhlstsh w gya mn badry
                    int waitingTime = currentTime - p.getArrivalTime() - (p.getBurstTime() - p.getRemainingTime()); //bahse elWT
                    if (waitingTime > starvationThreshold) { //lw estanet aktar melthreshold yebaa de betmoot
                        starvationOccurred = true;
                        break;
                    }
                }
            }

            // Select the next process
            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && !p.isComplete) { //baswitch lelprocess elba3dha
                    if (!starvationOccurred) {
                        // lw mafesh starvation bashtaghal STRF 3ady
                        if (p.getRemainingTime() < minRemainingTime) {
                            minRemainingTime = p.getRemainingTime();
                            nextProcess = p;
                        }
                    } else {
                        // lw feh babdaa addy priority lely betmoot elawal
                        int priority = p.getWaitingTime() * p_factor;
                        if (p.getRemainingTime() < minRemainingTime) {
                            minRemainingTime = p.getRemainingTime();
                            nextProcess = p;
                        }
                    }
                }
            }

            // lelCS
            if (nextProcess != currentProcess) { //lw baswitch
                if (currentProcess != null && nextProcess != null) { //baswitch w msh idle
                    for (int i = 0; i < contextSwitchingTime; i++) {
                        executionHistory.add("CS");
                        currentTime++; //increment time
                    }
                }
                currentProcess = nextProcess; //baswitch
            }

            // lw CPU Idle
            if (currentProcess == null) {
                executionHistory.add("Idle");
                currentTime++;
                continue;
            }

            // Execute the process
            executionHistory.add(currentProcess.getName());
            currentProcess.decrementRemainingTime();
            currentTime++;

            // Mark process as complete if finished
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompleted(true);
                completedProcesses++;
                currentProcess.setWaitingTime(currentTime - currentProcess.getBurstTime() - currentProcess.getArrivalTime());
            }
        }

        // Calculate and display stats
        findTurnAroundTime(processes);
        for (Process p : processes) {
            totalWaitingTime += p.getWaitingTime();
            totalTurnaroundTime += p.getTurnaroundTime();
            System.out.println(p.getName() + "\t\t\t" + p.getBurstTime() + "\t\t\t" + p.getArrivalTime()
                    + "\t\t\t" + p.getWaitingTime() + "\t\t\t" + p.getTurnaroundTime());
        }
        //elavgs
        float avgWT = (float) totalWaitingTime / processes.size();
        float avgTAT = (float) totalTurnaroundTime / processes.size();

        System.out.println("\nAverage Waiting Time = " + avgWT);
        System.out.println("Average Turnaround Time = " + avgTAT);

        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }

        // graph be class CPUScheduler
        CPUSchedulerGraph.draw(executionHistory, (ArrayList<Process>) processes, avgWT, avgTAT);
    }
}
