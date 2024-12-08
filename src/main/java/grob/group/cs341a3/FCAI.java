package grob.group.cs341a3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FCAI {
    public static void schedule(List<Process> processes) {
        List<String> executionHistory = new ArrayList<>();
        Queue<Process> readyQueue = new LinkedList<>();
        int time = 0;
        double V1 = processes.stream().mapToInt(Process::getArrivalTime).max().orElse(1) / 10.0;
        double V2 = processes.stream().mapToInt(Process::getBurstTime).max().orElse(1) / 10.0;

        int totalWaitingTime = 0, totalTurnaroundTime = 0;

        //bnrmy el process fel ready queue awl ma ygy wa2to
        while (processes.stream().anyMatch(p -> !p.isComplete) || !readyQueue.isEmpty()) {
            for (Process p : processes) {
                if (p.getArrivalTime() == time && !readyQueue.contains(p) && !p.isComplete) {
                    readyQueue.add(p);
                }
            }

            //law mafeesh haga bn5lyh idle
            if (readyQueue.isEmpty()) {
                executionHistory.add("idle");
                time++;
                continue;
            }

            // b-update el FCAI factors w n-sort el ready queue
            for (Process p : readyQueue) {
                p.updateFcaiFactor(V1, V2);
            }
            readyQueue = new LinkedList<>(readyQueue.stream()
                    .sorted((p1, p2) -> {
                        if (p1.fcaiFactor != p2.fcaiFactor) {
                            return Double.compare(p1.fcaiFactor, p2.fcaiFactor);
                        }
                        return Integer.compare(p1.getArrivalTime(), p2.getArrivalTime());
                    })
                    .toList());

            Process currentProcess = readyQueue.poll(); //awl process fel ready queue
            int quantum = currentProcess.getQuantum();
            int quantum40 = (int) Math.ceil(quantum * 0.4);
            boolean preempted = false;

            for (int i = 0; i < quantum; i++) {
                executionHistory.add(currentProcess.getName());
                currentProcess.decrementRemainingTime();
                time++; //bzawed el clock


                for (Process p : processes) {
                    if (p.getArrivalTime() == time && !readyQueue.contains(p) && !p.isComplete) {
                        readyQueue.add(p); //law process gdeeda wslt
                    }
                }

                // bashoof law 3dt 40% men el quantum
                if (i >= quantum40) {
                    for (Process p : readyQueue) {
                        if (!p.isComplete && p.updateFcaiFactor(V1, V2) < currentProcess.updateFcaiFactor(V1, V2)) {
                            //law fy process el fcai factor bta3ha a2al mn el process ely 3ndy
                            currentProcess.updateQuantum(quantum - i); // Update unused quantum
                            readyQueue.add(currentProcess);
                            preempted = true;
                            break;
                        }
                    }
                }

                if (preempted || currentProcess.getRemainingTime() == 0) {
                    break;
                }
            }

            //law process 5lst hat ely b3daha
            if (currentProcess.getRemainingTime() == 0) {
                currentProcess.setCompleted(true);
                currentProcess.setTurnaroundTime(time - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                totalWaitingTime += currentProcess.getWaitingTime();
                totalTurnaroundTime += currentProcess.getTurnaroundTime();
            } else if (!preempted) {
                currentProcess.updateQuantum(0); // Add 2 to quantum
                readyQueue.add(currentProcess);
            }
        }


        double avgWT = totalWaitingTime / (double) processes.size();
        double avgTAT = totalTurnaroundTime / (double) processes.size();


        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }

        System.out.println("\nProcess Details:");
        for (Process p : processes) {
            System.out.printf("Process %s:\n", p.getName());
            System.out.printf(" - Waiting Time: %d\n", p.getWaitingTime());
            System.out.printf(" - Turnaround Time: %d\n", p.getTurnaroundTime());
            System.out.println(" - Quantum History: " + p.quantumHistory);
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWT);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTAT);

        // Draw graph
        CPUSchedulerGraph.draw(executionHistory, (ArrayList<Process>) processes, (float) avgWT,(float) avgTAT);
    }
}
