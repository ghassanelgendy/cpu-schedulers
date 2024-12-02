import java.util.*;

public class FCAIScheduler {
    static List<String> scheduleFCAI(List<Process> processes, int contextSwitching, double V1, double V2) {
        Queue<Process> queue = new LinkedList<>();
        List<Process> completedProcesses = new ArrayList<>();
        List<String> executionOrder = new ArrayList<>();
        int currentTime = 0;

        System.out.println("\nDetailed Execution Timeline:");
        while (!processes.isEmpty() || !queue.isEmpty()) {
            // Add processes to the queue if their arrival time has been reached
            Iterator<Process> iterator = processes.iterator();
            while (iterator.hasNext()) {
                Process process = iterator.next();
                if (process.getArrivalTime() <= currentTime) {
                    queue.add(process);
                    iterator.remove();
                }
            }

            // If there are processes in the ready queue
            if (!queue.isEmpty()) {
                Process current = queue.poll();
                current.setFcaiFactor(current.updateFcaiFactor(V1, V2)); // Update FCAI factor

                // Execute 40% of the quantum initially
                int executionTime = (int) Math.ceil(current.quantum * 0.4);
                if (current.remainingTime < executionTime) {
                    executionTime = current.remainingTime;  // If less than quantum, execute the remaining time
                }

                // Update current time and remaining time of the process
                currentTime += executionTime;
                current.remainingTime -= executionTime;

                System.out.printf("Time %d–%d: Process %s executed, Remaining Burst Time: %d, Quantum: %d → %d\n",
                        currentTime - executionTime, currentTime, current.getName(),
                        current.getRemainingTime(), current.quantum, current.getRemainingTime() > 0 ? current.quantum + 2 : 0);

                // If the process still has remaining burst time, update its quantum and re-add it to the queue
                if (current.getRemainingTime() > 0) {
                    current.quantum += 2;
                    current.getQuantumHistory().add(current.getQuantum());
                    queue.offer(current);
                } else {
                    // If the process has finished, calculate its turnaround and waiting time
                    current.setTurnaroundTime(currentTime - current.getArrivalTime());
                    current.setWaitingTime(current.getTurnaroundTime() - current.getBurstTime());
                    completedProcesses.add(current);
                }

                executionOrder.add(current.getName());  // Add executed process to execution order
                currentTime += contextSwitching;  // Add context switching time
            } else {
                // If no processes are ready, increment the time until a process arrives
                currentTime++;
            }
        }

        processes.addAll(completedProcesses);  // Add completed processes back to the list

        return executionOrder;  // Return the execution order
    }


    static void displayResults(List<Process> processes, List<String> executionOrder) {
        System.out.println("\nExecution Order: " + executionOrder);

        double totalWaitingTime = 0, totalTurnaroundTime = 0;

        // Print results for each process
        for (Process process : processes) {
            System.out.println("\nProcess " + process.getName() + ":");
            System.out.println(" - Waiting Time: " + process.getWaitingTime());
            System.out.println(" - Turnaround Time: " + process.getTurnaroundTime());
            System.out.println(" - Quantum History: " + process.getQuantumHistory());

            totalWaitingTime += process.getWaitingTime();
            totalTurnaroundTime += process.getTurnaroundTime();
        }

        // Calculate and print average waiting and turnaround times
        System.out.println("\nAverage Waiting Time: " + (totalWaitingTime / processes.size()));
        System.out.println("Average Turnaround Time: " + (totalTurnaroundTime / processes.size()));
    }
}
