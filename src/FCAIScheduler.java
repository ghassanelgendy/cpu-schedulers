import java.util.*;

public class FCAIScheduler {

    public static void fcaiSchedule(List<Process> processes, int contextSwitching) {
        // Calculate V1 and V2
        int lastArrivalTime = processes.stream().mapToInt(Process::getArrivalTime).max().orElse(1);
        int maxBurstTime = processes.stream().mapToInt(Process::getBurstTime).max().orElse(1);
        double V1 = (double) lastArrivalTime / 10;
        double V2 = (double) maxBurstTime / 10;

        System.out.printf("V1 = %.2f, V2 = %.2f%n%n", V1, V2);

        // Priority Queue based on FCAI Factor (Highest factor first)
        PriorityQueue<Process> readyQueue = new PriorityQueue<>((p1, p2) ->
                Double.compare(p2.updateFcaiFactor(V1, V2), p1.updateFcaiFactor(V1, V2)));

        List<Process> allProcesses = new ArrayList<>(processes);
        int currentTime = 0;

        System.out.println("Step-by-step Execution:");

        while (!allProcesses.isEmpty() || !readyQueue.isEmpty()) {
            // Move processes that have arrived to the ready queue
            Iterator<Process> it = allProcesses.iterator();
            while (it.hasNext()) {
                Process p = it.next();
                if (p.getArrivalTime() <= currentTime) {
                    readyQueue.offer(p);
                    it.remove();
                }
            }

            if (readyQueue.isEmpty()) {
                // If no process is ready, CPU is idle
                System.out.printf("Time %d: CPU is idle.%n", currentTime);
                currentTime++;
                continue;
            }

            // Select the process with the highest FCAI factor

            System.out.printf("\n\nReady Queue at time %d: %s%n \n", currentTime, readyQueue.stream().map(Process::getName).toList());
            Process currentProcess = readyQueue.poll();
            System.out.printf("Time %d: Executing process %s (FCAI Factor: %d)%n", currentTime,
                    currentProcess.getName(), currentProcess.updateFcaiFactor(V1, V2));

            int quantum = currentProcess.getQuantum();
            int quantumNonPreemptive = (int) Math.ceil(quantum * 0.4); // 40% of quantum
            int executionTime = 0;

            // Execute the non-preemptive portion (40%)
            for (int t = 0; t < quantumNonPreemptive; t++) {
                if (currentProcess.getRemainingTime() <= 0) break; // Process completed
                currentTime++;
                currentProcess.remainingTime--;
                executionTime++;

                // Check if new processes arrive
                it = allProcesses.iterator();
                while (it.hasNext()) {
                    Process newProcess = it.next();
                    if (newProcess.getArrivalTime() <= currentTime) {
                        readyQueue.offer(newProcess);
                        it.remove();
                    }
                }
            }

            // If process still has time left after the non-preemptive portion
            boolean preempted = false;
            for (int t = executionTime; t < quantum; t++) {
                if (currentProcess.getRemainingTime() <= 0) break; // Process completed
                currentTime++;
                currentProcess.remainingTime--;
                executionTime++;

                // Check for preemption
                if (!readyQueue.isEmpty() && readyQueue.peek().updateFcaiFactor(V1, V2) >
                        currentProcess.updateFcaiFactor(V1, V2)) {
                    System.out.printf("Time %d: Process %s preempted by process %s%n", currentTime,
                            currentProcess.getName(), readyQueue.peek().getName());
                    preempted = true;
                    break;
                }
            }

            // Update quantum
            if (currentProcess.getRemainingTime() > 0) {
                if (preempted) {
                    // Add unused quantum if preempted
                    currentProcess.quantum += (quantum - executionTime);
                } else {
                    // Add 2 if quantum completed but work remains
                    currentProcess.quantum += 2;
                }
                System.out.printf("Process %s has %d burst time left. Quantum updated to %d.%n",
                        currentProcess.getName(), currentProcess.getRemainingTime(), currentProcess.getQuantum());
                readyQueue.offer(currentProcess);
            }
            else {
                currentProcess.setTurnaroundTime(currentTime - currentProcess.getArrivalTime());
                currentProcess.setWaitingTime(currentProcess.getTurnaroundTime() - currentProcess.getBurstTime());
                System.out.printf("Process %s completed at time %d. Turnaround Time: %d, Waiting Time: %d%n",
                        currentProcess.getName(), currentTime, currentProcess.getTurnaroundTime(), currentProcess.getWaitingTime());
            }

            // Context switching time
            if (contextSwitching > 0 && !readyQueue.isEmpty()) {
                System.out.printf("Time %d: Context switching (time += %d)%n", currentTime, contextSwitching);
                currentTime += contextSwitching;
            }
        }

        System.out.println("\nFinal Results:");
        for (Process process : processes) {
            System.out.printf("Process: %s, Turnaround Time: %d, Waiting Time: %d%n",
                    process.getName(), process.getTurnaroundTime(), process.getWaitingTime());
        }

        // Calculate average waiting and turnaround times
        double avgWaitingTime = processes.stream().mapToDouble(Process::getWaitingTime).average().orElse(0);
        double avgTurnaroundTime = processes.stream().mapToDouble(Process::getTurnaroundTime).average().orElse(0);
        System.out.printf("Average Waiting Time: %.2f%n", avgWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f%n", avgTurnaroundTime);
    }
}
