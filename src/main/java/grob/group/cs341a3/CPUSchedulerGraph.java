package grob.group.cs341a3;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CPUSchedulerGraph extends Application {
    static ArrayList<Process> processList;
    private static List<String> executionHistory;
    private static float averageWaitingTime; // Changed to float
    private static float averageTurnaroundTime; // Changed to float

    public static void draw(List<String> history, ArrayList<Process> processLis, float awt, float atat) { // Accept float parameters
        executionHistory = history;
        averageWaitingTime = awt;
        processList = processLis;
        averageTurnaroundTime = atat;
        launch(); // Launch the JavaFX application
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a canvas for drawing the graph
        Canvas canvas = new Canvas(800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw the graph
        drawGraph(gc, executionHistory, processList, averageWaitingTime, averageTurnaroundTime);

        // Wrap the canvas in a ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true); // Allow scrolling by dragging

        // Create a VBox for the sidebar with process details and stats
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-background-color: #f4f4f4;");
        Label header = new Label("Process Details");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        sidebar.getChildren().add(header);

        for (Process process : processList) {
            Label processLabel = new Label("Name: " + process.name +
                    ", Waiting Time: " + process.getWaitingTime() +
                    ", Turnaround Time: " + process.getTurnaroundTime());
            sidebar.getChildren().add(processLabel);
        }

        // Add stats
        Label awtLabel = new Label("Average Waiting Time (AWT): " + averageWaitingTime);
        Label atatLabel = new Label("Average Turnaround Time (ATAT): " + averageTurnaroundTime);
        sidebar.getChildren().addAll(awtLabel, atatLabel);

        // Create a vertical layout for the scrollable graph and the sidebar
        VBox layout = new VBox(10, scrollPane, sidebar);
        layout.setPadding(new Insets(10));

        // Set up the scene
        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setTitle("CPU Scheduling Graph");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawGraph(GraphicsContext gc, List<String> executionHistory, ArrayList<Process> processList, float AWT, float ATAT) {
        int yOffset = 50; // Initial vertical position for the processes
        int xOffset = 10;
        int barHeight = 40; // Height of each process bar
        int xScale = 30; // Scale for time (1 unit = 30px)
        int processYOffset = 50; // Space between process bars
        int timeAxisYOffset = 30; // Space for the time axis at the bottom

        // Determine the unique processes/events from execution history
        ArrayList<String> uniqueProcesses = new ArrayList<>();
        for (String entry : executionHistory) {
            if (!uniqueProcesses.contains(entry)) {
                uniqueProcesses.add(entry);
            }
        }

        // Adjust canvas height dynamically based on number of processes/events
        gc.getCanvas().setHeight(yOffset + uniqueProcesses.size() * processYOffset + barHeight + timeAxisYOffset);

        // Adjust canvas width dynamically based on execution history
        gc.getCanvas().setWidth(executionHistory.size() * xScale);

        // Draw the processes/events from execution history
        for (int time = 0; time < executionHistory.size(); time++) {
            String event = executionHistory.get(time);

            // Determine Y position based on the process or event
            int y = yOffset + uniqueProcesses.indexOf(event) * processYOffset;

            // Set color
            Color color;
            if (event.equals("CS")) {
                color = Color.GRAY; // Color for context switch
            } else if (event.equals("Idle")) {
                color = Color.LIGHTGRAY; // Color for idle time
            } else {
                // Find the process object to get its color
                color = processList.stream()
                        .filter(p -> p.name.equals(event))
                        .findFirst()
                        .map(p -> p.color)
                        .orElse(Color.BLACK); // Default to black if not found
            }

            // Draw the rectangle for the time unit
            gc.setFill(color);
            gc.fillRect(time * xScale, y, xScale, barHeight);

            // Add the time label below the rectangle
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(time), time * xScale + 5, yOffset + uniqueProcesses.size() * processYOffset + barHeight);
        }

        // Draw Y-axis labels for processes/events
        for (int i = 0; i < uniqueProcesses.size(); i++) {
            String label = uniqueProcesses.get(i);
            int y = yOffset + i * processYOffset + barHeight / 2;
            gc.setFill(Color.BLACK);
            gc.fillText(label, 10, y); // Draw label to the left of the graph
        }

        // Draw X-axis (time) line at the bottom of the graph
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(0, yOffset + uniqueProcesses.size() * processYOffset + barHeight + timeAxisYOffset, executionHistory.size() * xScale, yOffset + uniqueProcesses.size() * processYOffset + barHeight + timeAxisYOffset);

        // Draw time unit labels on the X-axis
        for (int time = 0; time < executionHistory.size(); time++) {
            if (time % 5 == 0) {  // Add a label every 5 time units (adjust as needed)
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(time), time * xScale + 5, yOffset + uniqueProcesses.size() * processYOffset + barHeight + timeAxisYOffset + 15);
            }
        }
    }
}
