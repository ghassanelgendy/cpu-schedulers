import java.util.ArrayList;
import java.util.List;

public class SRTF { //shortest remaining time first
    public static void srtfSchedule(List<Process> processes) {
        int time = 0; // Simulation time
        int completedProcesses = 0; // Number of completed processes
        int totalProcesses = processes.size();
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        // Process Execution Order
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
                time++;
                continue;
            }

            // Execute the current process for 1 unit of time
            executionOrder.add(currentProcess.getName());
            currentProcess.remainingTime--;

            // If the process finishes execution
            if (currentProcess.getRemainingTime() == 0) {
                completedProcesses++;
                int turnaroundTime = time + 1 - currentProcess.getArrivalTime();
                int waitingTime = turnaroundTime - currentProcess.getBurstTime();
                currentProcess.setTurnaroundTime(turnaroundTime);
                currentProcess.setWaitingTime(waitingTime);
                totalWaitingTime += waitingTime;
                totalTurnaroundTime += turnaroundTime;
            }

            // Increment simulation time
            time++;
        }

    }
}