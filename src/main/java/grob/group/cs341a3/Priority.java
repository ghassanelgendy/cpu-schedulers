package grob.group.cs341a3;

import java.util.*; //3shan el list w scanner

public class Priority {

    public static void schedule(int n, List<Process> processes, int CS) {
        // Sort processes fl awl bl arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0, totalWaitingTime = 0, totalTurnAroundTime = 0;
        List<String> executionHistory = new ArrayList<>(); // To store execution history
        Queue<Process> readyQueue = new PriorityQueue<>((p1, p2) -> {
            // Priority queue to dynamically select processes by priority and arrival time
            if (p1.priority != p2.priority)
                return Integer.compare(p1.priority, p2.priority); // Higher priority = lower number
            return Integer.compare(p1.arrivalTime, p2.arrivalTime); //earlier processes are scheduled first if priorities are equal
        });

        int index = 0; // To track processes added to the ready queue

        System.out.println("\nExecution Order:");
        while (!readyQueue.isEmpty() || index < processes.size()) {
            // Add processes that have arrived to the ready queue
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(index));
                index++;
            }

            if (readyQueue.isEmpty()) {
                // lw mafesh process ready to execute ,CPU is idle
                executionHistory.add("idle");
                currentTime++;
            } else {
                // Execute the highest priority process fl queue
                Process p = readyQueue.poll();

                // Waiting time
                p.waitingTime = currentTime - p.arrivalTime;
                totalWaitingTime += p.waitingTime;

                // Turnaround time
                for (int i = 0; i < p.burstTime; i++) {
                    executionHistory.add(p.name); // Add the process name fl execution history
                }
                currentTime += p.burstTime;
                p.turnaroundTime = currentTime - p.arrivalTime;
                totalTurnAroundTime += p.turnaroundTime;

                // Execution order
                System.out.println(p.name + " (Priority: " + p.priority + ", Burst: " + p.burstTime + ")");

                // Add context switch time talama da msh akher process
                if (!readyQueue.isEmpty() || index < processes.size()) {
                    for (int i = 0; i < CS; i++) {
                        executionHistory.add("CS");
                    }
                    currentTime += CS;
                }
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