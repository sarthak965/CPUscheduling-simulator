import javax.swing.*;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class RRAlgorithm implements SchedulingAlgorithm {
    
    private final int timeSlice;

    public RRAlgorithm(int timeSlice) {
        this.timeSlice = timeSlice;
    }

    @Override
    public void schedule(List<Process> processes, JTextArea processInfoArea, JLabel currentProcessLabel, JLabel avgWaitingTimeLabel, JLabel avgTurnaroundTimeLabel, JLabel totalExecutionTimeLabel, JPanel progressPanel) {
        final int[] currentTime = {0};
        final int[] totalWaitingTime = {0};
        final int[] totalTurnaroundTime = {0};
        final int[] remainingProcesses = {processes.size()};

        Queue<Process> queue = new LinkedList<>(processes);

        processInfoArea.setText("");
        new Thread(() -> {
            while (!queue.isEmpty()) {
                Process currentProcess = queue.poll();
                currentProcessLabel.setText("Current Process: " + currentProcess.name);
                processInfoArea.append("Processing: " + currentProcess.name + "\n");

                int remainingTime = Math.min(timeSlice, currentProcess.burstTime - currentProcess.progressBar.getValue());
                while (remainingTime > 0) {
                    if (!Scheduler.isRunning()) {
                        return;
                    }
                    remainingTime--;
                    currentProcess.progressBar.setValue(currentProcess.progressBar.getValue() + 1);
                    try {
                        Thread.sleep(1000); // Simulate one second of processing time
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (currentProcess.progressBar.getValue() < currentProcess.burstTime) {
queue.add(currentProcess);
} else {
currentProcess.waitingTime = currentTime[0] - (currentProcess.burstTime - currentProcess.progressBar.getValue());
currentTime[0] += currentProcess.burstTime - currentProcess.progressBar.getValue();
currentProcess.turnaroundTime = currentTime[0];


                totalWaitingTime[0] += currentProcess.waitingTime;
                totalTurnaroundTime[0] += currentProcess.turnaroundTime;
                remainingProcesses[0]--;
            }
        }

        int processCount = processes.size();
        avgWaitingTimeLabel.setText("Average Waiting Time: " + (totalWaitingTime[0] / processCount));
        avgTurnaroundTimeLabel.setText("Average Turnaround Time: " + (totalTurnaroundTime[0] / processCount));
        totalExecutionTimeLabel.setText("Total Execution Time: " + currentTime[0]);

        Scheduler.setRunning(false);
    }).start();
}
}