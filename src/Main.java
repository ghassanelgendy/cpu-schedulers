import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//main run for all schedulers
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nWelcome to the CPU Scheduler!");
        System.out.println("Select a Scheduling Algorithm:");
        System.out.println("1. Shortest Job First (SJF)");
        System.out.println("2. Shortest Remaining Time First (SRTF)");
        System.out.println("3. Priority Scheduling");
        System.out.println("4. FCAI Scheduling");
        System.out.print("\nEnter your choice > ");
        int choice = scanner.nextInt();

        System.out.print("Enter the number of processes: ");
        int numProcesses = scanner.nextInt();

        System.out.print("Enter the context-switching time: ");
        int contextSwitching = scanner.nextInt();

        List<Process> processes = new ArrayList<>();
        System.out.println("Enter process details:");

        for (int i = 0; i < numProcesses; i++) {
            System.out.println("Process " + (i + 1) + ":");
            System.out.print(" - Name: ");
            String name = scanner.next();
            System.out.print(" - Burst Time: ");
            int burstTime = scanner.nextInt();
            if (burstTime <= 0) {
                System.out.println("Burst time must be positive!");
                return;
            }
            System.out.print(" - Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            int quantum = 0;
            int priority = 0;
            if (choice == 4){
                System.out.print(" - Priority Number: ");
                priority = scanner.nextInt();
                System.out.print(" - Initial Quantum (for FCAI only): ");
                quantum = scanner.nextInt();
            }

            processes.add(new Process(name, arrivalTime, burstTime, priority, quantum));
        }

        switch (choice) {
            case 1:
                System.out.println("\nRunning Shortest Job First (SJF) Scheduling...");
                break;
            case 2:
                System.out.println("\nRunning Shortest Remaining Time First (SRTF) Scheduling...");
<<<<<<< Updated upstream

//                srtfSchedule(processes);
=======
                SRTF.srtfSchedule(processes);
>>>>>>> Stashed changes
                break;
            case 3:
                System.out.println("\nRunning Priority Scheduling...");
                break;
            case 4:
                System.out.println("\nRunning FCAI Scheduling...");
                FCAIScheduler.fcaiSchedule(processes, contextSwitching);
                break;
            default:
                System.out.println("Invalid choice! Please run the program again.");
        }
    }
}
