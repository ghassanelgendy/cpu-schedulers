package grob.group.cs341a3;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

class SJF {
    public static void schedule(int n, List<Process> processes, int CS) {
        List<String> executionHistory = new ArrayList<>();
        int currentTime = processes.getFirst().getArrivalTime();// awl arrival time m4 0 cuz obv we start from the first process's arrival time
        List<Process> readyQueue = new ArrayList<>();
        List<Process> waitingQueue = new ArrayList<>(processes);
        processes.clear();// 3shan a3rf a-store el execution sequence
        int waitingTime = 0;// initialize waiting time 3shan a7sb el average
        int turnaroundTime = 0;// w nfs el kalam for the TAT


        while (!waitingQueue.isEmpty() || !readyQueue.isEmpty()) {

            for (int i = 0; i < waitingQueue.size(); i++) {
                waitingQueue.get(i).setpriority(waitingQueue.get(i).getBurstTime());
                if(currentTime - waitingQueue.get(i).getArrivalTime() >= 10){
                    waitingQueue.get(i).setpriority(-50);
                }
            }

            for (int i = 0; i < readyQueue.size(); i++) {
                readyQueue.get(i).setpriority(readyQueue.get(i).getBurstTime());
                if(currentTime - readyQueue.get(i).getArrivalTime() >= 10){
                    readyQueue.get(i).setpriority(-50);
                }
            }

            for (int i = 0; i < waitingQueue.size(); i++) {
                if (waitingQueue.get(i).getArrivalTime() <= currentTime) {
                    readyQueue.add(waitingQueue.get(i));
                    waitingQueue.remove(i);
                    i--; // 3shan lw 7atet el deletion by8yr el index
                    // after deletion the next element will take the same index
                }
            }

            // Sort ready queue by burst time
            readyQueue.sort(Comparator.comparingInt(Process::getPriority).thenComparingInt(Process::getArrivalTime));

            if (!readyQueue.isEmpty()) {
                processes.add(readyQueue.getFirst()); // add the process to the execution sequence
                Process currentProcess = readyQueue.removeFirst(); // remove el process ely 3aleha el door
                currentProcess.setWaitingTime(currentTime - currentProcess.getArrivalTime());
                waitingTime += currentProcess.getWaitingTime(); // sum of waiting time
                currentProcess.setTurnaroundTime(currentProcess.getWaitingTime() + currentProcess.getBurstTime());
                turnaroundTime += currentProcess.getTurnaroundTime(); // sum of turnaround time
                for (int i = 0; i < currentProcess.getBurstTime(); i++) {
                    executionHistory.add(currentProcess.getName());// for every time unit add the process name to the execution history
                }                                                  // to save the execution sequence
                currentTime += currentProcess.getBurstTime();
            } else {
                processes.add(new Process("Idle", currentTime, 1, 0, 0, Color.BLACK));
                executionHistory.add("Idle");
                currentTime++;
            }
        }
        System.out.println("Execution sequence: ");
        for (Process process : processes) {
            System.out.print(process.getName() + " ");
        }

        processes.sort(Comparator.comparing(Process::getName));// 34an a-print el Process in order

        System.out.println();
        for (Process process : processes) {
            System.out.println("Process: " + process.getName());
            System.out.println("Waiting time: " + process.getWaitingTime());
            System.out.println("Turnaround time: " + process.getTurnaroundTime());
            System.out.println();
        }

        System.out.println("Average waiting time: " + (float) waitingTime / n);
        System.out.println("Average turnaround time: " + (float) turnaroundTime / n);

        float avgWT = (float) waitingTime / n;
        float avgTAT = (float) turnaroundTime / n;

        // Display execution history
        System.out.println("\nExecution History:");
        for (int i = 0; i < executionHistory.size(); i++) {
            System.out.printf("Time %d: %s\n", i, executionHistory.get(i));
        }

        // Draw the CPU scheduler graph
        CPUSchedulerGraph.draw(executionHistory, (ArrayList<Process>) processes, avgWT, avgTAT);
    }

}