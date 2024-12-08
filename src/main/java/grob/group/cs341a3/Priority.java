package grob.group.cs341a3;

import java.util.*; //3shan el list w scanner

public class Priority {

    public static void schedule(int n, List<Process> processes, int CS) {
        // Sort bl priority, w ba3den bl arrival time
        processes.sort((p1, p2) -> {
            if (p1.priority != p2.priority)
                return Integer.compare(p1.priority, p2.priority); // el rakam el soghyr 3ndo el priority el kebra
            return Integer.compare(p1.arrivalTime, p2.arrivalTime);//earlier processes are scheduled first if priorities are equal
        });

        int currentTime = 0, totalWaitingTime = 0, totalTurnAroundTime = 0;
        List<String> executionHistory = new ArrayList<>(); // To store execution history

        System.out.println("\nExecution Order:");
        for (Process p : processes) {
            // If CPU is idle, fast-forward time to the arrival time of the next process
            if (currentTime < p.arrivalTime) {
                while (currentTime < p.arrivalTime) {
                    executionHistory.add("idle");
                    currentTime++;
                }
            }

            // Waiting time
            p.waitingTime = currentTime - p.arrivalTime;
            totalWaitingTime += p.waitingTime;

            // Turnaround time
            for (int i = 0; i < p.burstTime; i++) {
                executionHistory.add(p.name); // Add the process name to the execution history
            }
            currentTime += p.burstTime;
            p.turnaroundTime = currentTime - p.arrivalTime;
            totalTurnAroundTime += p.turnaroundTime;

            //  execution order
            System.out.println(p.name + " (Priority: " + p.priority + ", Burst: " + p.burstTime + ")");

            // Add context switch time talama da msh akher process
            if (processes.indexOf(p) != processes.size() - 1) {
                for (int i = 0; i < CS; i++) {
                    executionHistory.add("CS");
                }
                currentTime += CS;
            }
        }

        System.out.println("\nProcess\tWaiting Time\tTurnaround Time");
        for (Process p : processes) {
            System.out.println(p.name + "\t" + p.waitingTime + "\t\t" + p.turnaroundTime);
        }

        float avgWT = (float) totalWaitingTime / n;
        float avgTAT = (float) totalTurnAroundTime / n;

        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWT);
        System.out.printf("Average Turn around Time: %.2f\n", avgTAT);

        // Output execution history and draw the graph
        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }

        CPUSchedulerGraph.draw(executionHistory, (ArrayList<Process>) processes, avgWT, avgTAT);
    }
}
