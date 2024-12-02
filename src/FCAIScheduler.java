import java.util.*;

public class FCAIScheduler {
    static List<String> scheduleFCAI(List<Process> processes, int contextSwitching, double V1, double V2) {
        Queue<Process> queue = new LinkedList<>();
        List<Process> completedProcesses = new ArrayList<>();
        List<String> executionOrder = new ArrayList<>();
        int currentTime = 0;

        System.out.println("\nDetailed Execution Timeline:");
        while (!processes.isEmpty() || !queue.isEmpty()) {
            Iterator<Process> iterator = processes.iterator();
            while (iterator.hasNext()) {
                Process process = iterator.next();
                if (process.arrivalTime <= currentTime) {
                    queue.offer(process);
                    iterator.remove();
                }
            }

            if (!queue.isEmpty()) {
                Process current = queue.poll();
                current.fcaiFactor = (10 - current.priority)
                        + (current.arrivalTime / V1)
                        + (current.remainingTime / V2);

                int executionTime = (int) Math.ceil(current.quantum * 0.4);
                if (current.remainingTime < executionTime) {
                    executionTime = current.remainingTime;
                }
                currentTime += executionTime;
                current.remainingTime -= executionTime;

                System.out.printf("Time %d–%d: Process %s executed, Remaining Burst Time: %d, Quantum: %d → %d\n",
                        currentTime - executionTime, currentTime, current.name,
                        current.remainingTime, current.quantum, current.remainingTime > 0 ? current.quantum + 2 : 0);

                if (current.remainingTime > 0) {
                    current.quantum += 2;
                    current.quantumHistory.add(current.quantum);
                    queue.offer(current);
                } else {
                    current.turnaroundTime = currentTime - current.arrivalTime;
                    current.waitingTime = current.turnaroundTime - current.burstTime;
                    completedProcesses.add(current);
                }

                executionOrder.add(current.name);
                currentTime += contextSwitching;
            } else {
                currentTime++;
            }
        }

        processes.clear();
        processes.addAll(completedProcesses);

        return executionOrder;
    }

    static void displayResults(List<Process> processes, List<String> executionOrder) {
        System.out.println("\nExecution Order: " + executionOrder);

        double totalWaitingTime = 0, totalTurnaroundTime = 0;

        for (Process process : processes) {
            System.out.println("\nProcess " + process.name + ":");
            System.out.println(" - Waiting Time: " + process.waitingTime);
            System.out.println(" - Turnaround Time: " + process.turnaroundTime);
            System.out.println(" - Quantum History: " + process.quantumHistory);

            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;
        }

        System.out.println("\nAverage Waiting Time: " + (totalWaitingTime / processes.size()));
        System.out.println("Average Turnaround Time: " + (totalTurnaroundTime / processes.size()));
    }
}
