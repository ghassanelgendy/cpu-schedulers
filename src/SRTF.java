import java.util.ArrayList;
import java.util.List;

public class SRTF {
    public static void srtfSchedule(List<Process> processes) {
        int time = 0; // Simulation time
        int completedProcesses = 0; // Number of completed processes
        int totalProcesses = processes.size();
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        // Gantt Chart Data
        List<String> executionOrder = new ArrayList<>();

        while (completedProcesses < totalProcesses) {
            // Find the process with the shortest remaining time that's ready to execute
            Process currentProcess = null;
            for (Process process : processes) {
                if (process.getArrivalTime() <= time && process.getRemainingTime() > 0) {
                    if (currentProcess == null || process.getRemainingTime() < currentProcess.getRemainingTime()) {
                        currentProcess = process;
                    }
                }
            }

            if (currentProcess == null) {
                // No process is ready, increment time
                executionOrder.add("-");
                time++;
                continue;
            }

            // Record the response time if this is the first execution of the process
            if (currentProcess.getStartTime() == -1) {
                currentProcess.setStartTime(time); // Start time is the first time it executes
                currentProcess.setResponseTime(time - currentProcess.getArrivalTime());
            }

            // Execute the current process for 1 unit of time
            executionOrder.add(currentProcess.getName());
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);

            // If the process finishes execution
            if (currentProcess.getRemainingTime() == 0) {
                completedProcesses++;
                int completionTime = time + 1;
                int turnaroundTime = completionTime - currentProcess.getArrivalTime();
                int waitingTime = turnaroundTime - currentProcess.getBurstTime();

                currentProcess.setCompletionTime(completionTime);
                currentProcess.setTurnaroundTime(turnaroundTime);
                currentProcess.setWaitingTime(waitingTime);

                totalWaitingTime += waitingTime;
                totalTurnaroundTime += turnaroundTime;
            }

            // Increment simulation time
            time++;
        }
        // Display results
        displayResults(processes, executionOrder, totalWaitingTime, totalTurnaroundTime);
    }

    private static void displayResults(List<Process> processes, List<String> executionOrder,
                                       int totalWaitingTime, int totalTurnaroundTime) {
        System.out.println("\nGantt Chart:");
        int currentTime = 0;
        for (String process : executionOrder) {
            System.out.print("| " + process + " ");
        }
        System.out.println("|");
        for (int i = 0; i < executionOrder.size(); i++) {
            System.out.print(currentTime + " ");
            currentTime++;
        }
        System.out.println(currentTime);

        System.out.println("\nProcess Table:");
        System.out.printf("%-10s %-15s %-10s %-15s %-15s %-15s %-15s\n",
                "Process", "Arrival Time", "Burst Time", "Completion Time",
                "Turnaround Time", "Waiting Time", "Response Time");
        for (Process process : processes) {
            System.out.printf("%-10s %-15d %-10d %-15d %-15d %-15d %-15d\n",
                    process.getName(), process.getArrivalTime(), process.getBurstTime(),
                    process.getCompletionTime(), process.getTurnaroundTime(),
                    process.getWaitingTime(), process.getResponseTime());
        }

        // Calculate averages
        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();

        System.out.println("\nAverage Waiting Time: " + avgWaitingTime);
        System.out.println("Average Turnaround Time: " + avgTurnaroundTime);
    }

    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 7,1,1));
        processes.add(new Process("P2", 2, 4,1,1));
        processes.add(new Process("P3", 4, 1,1,1));
        processes.add(new Process("P4", 5, 4,1,1));

        srtfSchedule(processes);
    }
}
