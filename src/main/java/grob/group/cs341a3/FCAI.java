package grob.group.cs341a3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.*;

public class FCAI {

    public static void schedule(List<Process> processes) {
        double lastArrivalTime = processes.stream().mapToDouble(p -> p.arrivalTime).max().orElse(1);
        double maxBurstTime = processes.stream().mapToDouble(p -> p.burstTime).max().orElse(1);
        double v1 = Math.ceil(lastArrivalTime / 10);
        double v2 = Math.ceil(maxBurstTime / 10);
        System.out.println("v1: " + v1 + ", v2: " + v2);

        for (Process process : processes) {
            process.updateFcaiFactor(v1, v2);
        }

        Deque<Process> readyQueue = new LinkedList<>();
        List<Process> completedProcesses = new ArrayList<>();
        List<String> executionHistory = new ArrayList<>();
        int currentTime = 0;
        double totalWaitingTime = 0, totalTurnaroundTime = 0;

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            Iterator<Process> iterator = processes.iterator();
            while (iterator.hasNext()) {
                Process process = iterator.next();
                if (process.arrivalTime <= currentTime) {
                    readyQueue.addLast(process);
                    iterator.remove();
                }
            }

            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.pollFirst();
                double executeTime = Math.min(Math.ceil(currentProcess.quantum * 0.4), currentProcess.remainingTime);
                double unusedQuantum = currentProcess.quantum - executeTime;

                currentTime += executeTime;
                currentProcess.remainingTime -= executeTime;
                for (int i = 0; i < executeTime; i++) {
                    executionHistory.add(currentProcess.name);
                }
                currentProcess.updateFcaiFactor(v1, v2);


                iterator = processes.iterator();
                while (iterator.hasNext()) {
                    Process process = iterator.next();
                    if (process.arrivalTime <= currentTime) {
                        readyQueue.addLast(process);
                        iterator.remove();
                    }
                }

                Process pre = null ;
                for (Process process : readyQueue) {
                    if (process.getFcaiFactor() <= currentProcess.getFcaiFactor()) {
                        pre = process;
                    }
                }

                if (pre != null) {
                    currentProcess.updateQuantum(currentProcess.getQuantum() + unusedQuantum);
                    readyQueue.addLast(currentProcess);
                    currentProcess.updateFcaiFactor(v1, v2);

                    Process process = readyQueue.stream()
                            .min(Comparator.comparingDouble(Process::getFcaiFactor))
                            .orElse(null);

                    readyQueue.remove(process);
                    readyQueue.addFirst(process);

                    System.out.println("Process " + pre.getName() + " preempted " + currentProcess.getName());
                    continue;
                }
                
                if (currentProcess.remainingTime <= 0) {
                    currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                    totalWaitingTime += currentProcess.waitingTime;
                    totalTurnaroundTime += currentProcess.turnaroundTime;
                    completedProcesses.add(currentProcess);
                }
                while (currentProcess.remainingTime > 0 && unusedQuantum > 0) {
                    // Execute the process for 1 unit of time
                    int remainTime = currentProcess.remainingTime - 1;
                    currentProcess.remainingTime = remainTime;
                    executionHistory.add(currentProcess.name);
                    unusedQuantum--;
                    currentTime++;

                    // Update FCAI factor for the current process
                    currentProcess.updateFcaiFactor(v1, v2);

                    if (currentProcess.remainingTime <= 0) {
                        System.out.println("Process " + currentProcess.getName() + " completed at " + currentTime);
                        currentProcess.setTurnaroundTime(currentProcess.getBurstTime() - currentProcess.getArrivalTime());
                        currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                        System.out.printf("Time %d: Process %s completed.\n\n", currentTime, currentProcess.name);
                        break;
                    }

//                    public void display(){}

                    // Check for newly arriving processes and add them to the ready queue
                    iterator = processes.iterator();
                    while (iterator.hasNext()) {
                        Process newProcess = iterator.next();
                        if (newProcess.arrivalTime <= currentTime) {
                            readyQueue.addLast(newProcess);
                            iterator.remove();
                            System.out.printf("Time %d: Process %s arrived and added to the ready queue.\n", currentTime, newProcess.name);
                        }
                    }

                    pre = null ;
                    for (Process process : readyQueue) {
                        if (process.getFcaiFactor() <= currentProcess.getFcaiFactor()) {
                            pre = process;
                        }
                    }


                    if (pre != null) {
                        currentProcess.updateQuantum((int)(currentProcess.getQuantum() + unusedQuantum));
                        readyQueue.addLast(currentProcess);
                        currentProcess.updateFcaiFactor(v1, v2);

                        Process process = readyQueue.stream()
                                .min(Comparator.comparingDouble(Process::getFcaiFactor))
                                .orElse(null);

                        readyQueue.remove(process);
                        readyQueue.addFirst(process);
                        break;
                    }

                    // If the process completes, print completion message
                    if (currentProcess.remainingTime <= 0) {
                        System.out.println("Process " + currentProcess.getName() + " completed at " + currentTime);
                        currentProcess.setTurnaroundTime(currentProcess.getBurstTime() - currentProcess.getArrivalTime());
                        currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                        System.out.printf("Time %d: Process %s completed.\n\n", currentTime, currentProcess.name);
                        break;
                    } else if (unusedQuantum <= 0) {
                        currentProcess.updateQuantum(currentProcess.getQuantum() + 2);
                        readyQueue.addLast(currentProcess);
                        break;
                    }
                }
            } else {
                executionHistory.add("Idle");
                currentTime++;
            }
        }


        printResults(completedProcesses, executionHistory, totalWaitingTime, totalTurnaroundTime);
    }

    private static void printResults(List<Process> completedProcesses, List<String> executionHistory, double totalWaitingTime, double totalTurnaroundTime) {
        float avgWaitingTime = (float)totalWaitingTime / completedProcesses.size();
        float avgTurnaroundTime = (float)totalTurnaroundTime / completedProcesses.size();

        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }

        System.out.println("\nProcess Details:");
        for (Process p : completedProcesses) {
            System.out.printf("Process %s:\n", p.name);
            System.out.printf(" - Waiting Time: %d\n", p.waitingTime);
            System.out.printf(" - Turnaround Time: %d\n", p.turnaroundTime);
        }

        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaroundTime);

        CPUSchedulerGraph.draw(executionHistory, (ArrayList<Process>) completedProcesses, avgWaitingTime, avgTurnaroundTime);
    }
}