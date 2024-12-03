package grob.group.cs341a3;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;


public class SRTF extends Application {

    private static int time = 0; // Simulation time
    private static List<Process> processes;
    private static List<String> executionOrder = new ArrayList<>();

    public static void main(String[] args) {
        // Create sample processes for testing
        processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 7, 1, 1));
        processes.add(new Process("P2", 2, 4, 1, 1));
        processes.add(new Process("P3", 4, 1, 1, 1));
        processes.add(new Process("P4", 5, 4, 1, 1));

        launch(args); // Start JavaFX Application
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a Canvas for drawing the Gantt chart
        Canvas canvas = new Canvas(800, 200); // Width and Height of the canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Setup the layout for the scene
        StackPane root = new StackPane();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root, 800, 300);

        // Timeline to run the SRTF algorithm and update the canvas every second
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            srtfSchedule();
            plotGraph(gc); // Update the Gantt chart on each iteration
        }));
        timeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
        timeline.play();

        // Display the JavaFX window
        primaryStage.setTitle("SRTF Scheduling Algorithm");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Shortest Remaining Time First Scheduling Algorithm
    public static void srtfSchedule() {
        int completedProcesses = 0; // Number of completed processes
        int totalProcesses = processes.size();

        while (completedProcesses < totalProcesses) {
            // Find the process with the shortest remaining time that is ready to execute
            Process currentProcess = null;
            for (Process process : processes) {
                if (process.getArrivalTime() <= time && process.getRemainingTime() > 0) {
                    if (currentProcess == null || process.getRemainingTime() < currentProcess.getRemainingTime()) {
                        currentProcess = process;
                    }
                }
            }

            if (currentProcess == null) {
                // No process is ready to execute, so idle
                executionOrder.add("-");
                time++; // Move to the next time unit
                continue;
            }

            // Record the process's execution
            executionOrder.add(currentProcess.getName());
            currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1); // Execute the process for 1 time unit

            // If the process finishes execution
            if (currentProcess.getRemainingTime() == 0) {
                completedProcesses++;
                int completionTime = time + 1;
                int turnaroundTime = completionTime - currentProcess.getArrivalTime();
                int waitingTime = turnaroundTime - currentProcess.getBurstTime();

                currentProcess.setCompletionTime(completionTime);
                currentProcess.setTurnaroundTime(turnaroundTime);
                currentProcess.setWaitingTime(waitingTime);

            }

            // Increment simulation time
            time++;
        }
    }

    // Function to plot the Gantt chart on the Canvas
    private static void plotGraph(GraphicsContext gc) {
        gc.clearRect(0, 0, 800, 200); // Clear the canvas

        int x = 50; // Starting X position for the first process
        int barWidth = 60; // Width of each process block
        int height = 100; // Height of the bars

        // Draw the Gantt chart (execution order)
        for (String process : executionOrder) {
            if (!process.equals("-")) {
                gc.setFill(Color.LIGHTBLUE); // Color for processes
                gc.fillRect(x, height, barWidth, 50);
                gc.setStroke(Color.BLACK); // Border color
                gc.strokeRect(x, height, barWidth, 50);

                // Display the process name inside the block
                gc.setFill(Color.BLACK);
                gc.fillText(process, x + 10, height + 25);
            } else {
                // Draw a gap (idle time) with grey color
                gc.setFill(Color.GRAY);
                gc.fillRect(x, height, barWidth, 50);
            }
            x += barWidth; // Move X position for the next block
        }

        // Update time on the X-axis
        x = 50;
        for (int i = 0; i < executionOrder.size(); i++) {
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(i), x + 20, height + 70); // Display time at the bottom
            x += barWidth;
        }
    }

    public static void passProcesses(List<Process> processes) {
    SRTF.processes = processes;
    }
}

