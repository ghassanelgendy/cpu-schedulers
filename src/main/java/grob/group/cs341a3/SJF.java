package grob.group.cs341a3;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

class SJF {
    public static void schedule(int n, List<Process> processes, int CS) {

        int currentTime = processes.getFirst().getArrivalTime();// awl arrival time m4 0 cuz obv we start from the first process's arrival time
        List<Process> readyQueue = new ArrayList<>();
        List<Process> waitingQueue = new ArrayList<>(processes);
        processes.clear();// 3shan a3rf a-store el execution sequence
        int waitingTime = 0;// initialize waiting time 3shan a7sb el average
        int turnaroundTime = 0;// w nfs el kalam for the TAT
        while (!waitingQueue.isEmpty() || !readyQueue.isEmpty()) {

            for (int i = 0; i < waitingQueue.size(); i++) {
                if (waitingQueue.get(i).getArrivalTime() <= currentTime) {
                    readyQueue.add(waitingQueue.get(i));
                    waitingQueue.remove(i);
                    i--; // 3shan lw 7atet el deletion by8yr el index
                    // after deletion the next element will take the same index
                }
            }

            // Sort ready queue by burst time
            readyQueue.sort(Comparator.comparingInt(Process::getBurstTime));

            if (!readyQueue.isEmpty()) {
                processes.add(readyQueue.getFirst()); // add the process to the execution sequence
                Process currentProcess = readyQueue.removeFirst(); // remove el process ely 3aleha el door
                currentProcess.setWaitingTime(currentTime - currentProcess.getArrivalTime());
                waitingTime += currentProcess.getWaitingTime(); // sum of waiting time
                currentProcess.setTurnaroundTime(currentProcess.getWaitingTime() + currentProcess.getBurstTime());
                turnaroundTime += currentProcess.getTurnaroundTime(); // sum of turnaround time
                currentTime += currentProcess.getBurstTime();
            } else {
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

        System.out.println("Average waiting time: " + (double) waitingTime / n);
        System.out.println("Average turnaround time: " + (double) turnaroundTime / n);

    }
}