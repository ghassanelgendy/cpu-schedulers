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

//inherit mn Application for funcs like launch() and run()
public class CPUSchedulerGraph extends Application {
    static ArrayList<Process> processList; //array elprocesses (mn input elmain)
    private static List<String> executionHistory; //input elclass (use it to draw)
    private static float averageWaitingTime;
    private static float averageTurnaroundTime;

    public static void draw(List<String> history, ArrayList<Process> processLis, float awt, float atat) {
        executionHistory = history; //mn class elscheduler
        averageWaitingTime = Float.parseFloat(String.format("%.2f", awt));
        processList = processLis;
        averageTurnaroundTime = Float.parseFloat(String.format("%.2f", atat));
        launch(); // Launch el JavaFX application
    }

    @Override
    public void start(Stage primaryStage) {
        // Create canvas 3shan el graph
        Canvas canvas = new Canvas(800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Draw el graph
        drawGraph(gc, executionHistory, processList, averageWaitingTime, averageTurnaroundTime);

        // Wrap el canvas fe ScrollPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true); // Allow scrolling by dragging

        // Create VBox for el sidebar ma3 details w stats
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

        // Create vertical layout for el scrollable graph w el sidebar
        VBox layout = new VBox(10, scrollPane, sidebar);
        layout.setPadding(new Insets(10));

        // Set up el scene
        Scene scene = new Scene(layout, 900, 600);
        primaryStage.setTitle("CPU Scheduling Graph");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void drawGraph(GraphicsContext gc, List<String> executionHistory, ArrayList<Process> processList, float AWT, float ATAT) {
        int yOffset = 50; // El position bta3 vert.
        int barHeight = 40; // Height kol process bar
        int xScale = 30; // Scale bta3 el time
        int processYOffset = 50; // Space between el bars
        int timeAxisYOffset = 30; // Space for el time axis ta7t

        // Determine el unique processes mn execution history
        ArrayList<String> uniqueProcesses = new ArrayList<>();
        for (String entry : executionHistory) {
            if (!uniqueProcesses.contains(entry)) {
                uniqueProcesses.add(entry);
            }
        }

        // Adjust el canvas height dynamically 3la 7asb el processes
        gc.getCanvas().setHeight(yOffset + uniqueProcesses.size() * processYOffset + barHeight + timeAxisYOffset);

        // Adjust el canvas width dynamically 3la 7asb el execution history
        gc.getCanvas().setWidth(executionHistory.size() * xScale);

        // Draw el processes/events mn execution history
        for (int time = 0; time < executionHistory.size(); time++) {
            String event = executionHistory.get(time);

            // Determine Y position based 3la el process or event
            int y = yOffset + uniqueProcesses.indexOf(event) * processYOffset;

            // Set el color
            Color color;
            if (event.equals("CS")) {
                color = Color.GRAY; // Color lel context switch
            } else if (event.equals("Idle")) {
                color = Color.LIGHTGRAY; // Color lel idle time
            } else {
                // Find el process object to get its color
                color = processList.stream()
                        .filter(p -> p.name.equals(event))
                        .findFirst()
                        .map(p -> p.color)
                        .orElse(Color.BLACK); // Default black law malosh color
            }

            // Draw el rectangle for el time unit
            gc.setFill(color);
            gc.fillRect(time * xScale, y, xScale, barHeight);

            // Add el time label ta7t el rectangle
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(time), time * xScale + 5, yOffset + uniqueProcesses.size() * processYOffset + barHeight);
        }

        // Draw Y-axis labels lel processes/events
        for (int i = 0; i < uniqueProcesses.size(); i++) {
            String label = uniqueProcesses.get(i);
            int y = yOffset + i * processYOffset + barHeight / 2;
            gc.setFill(Color.BLACK);
            gc.fillText(label, 10, y); // Draw label 3alshmal
        }

        // Draw X-axis (time) line ta7t el graph
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeLine(0, yOffset + uniqueProcesses.size() * processYOffset + barHeight + timeAxisYOffset, executionHistory.size() * xScale, yOffset + uniqueProcesses.size() * processYOffset + barHeight + timeAxisYOffset);

        // Draw time unit labels 3la X-axis
        for (int time = 0; time < executionHistory.size(); time++) {
            if (time % 5 == 0) {  // Add label kol 5 units
                gc.setFill(Color.BLACK);
                gc.fillText(String.valueOf(time), time * xScale + 5, yOffset + uniqueProcesses.size() * processYOffset + barHeight + timeAxisYOffset + 15);
            }
        }
    }
}
