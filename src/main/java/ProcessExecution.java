package grob.group.cs341a3;

public class ProcessExecution {
    private String processName;
    private int startTime;
    private int endTime;

    public ProcessExecution(String processName, int startTime, int endTime) {
        this.processName = processName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getProcessName() {
        return processName;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }
}
