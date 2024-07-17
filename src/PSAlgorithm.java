
import javax.swing.*;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

public class PSAlgorithm implements SchedulingAlgorithm {
    
    @Override
    public void schedule(List<Process> processes, JTextArea processInfoArea, JLabel currentProcessLabel, JLabel avgWaitingTimeLabel, JLabel avgTurnaroundTimeLabel, JLabel totalExecutionTimeLabel, JPanel progressPanel) {
        final int[] currentTime = {0};
        final int[] totalWaitingTime = {0};
        final int[] totalTurnaroundTime = {0};

        PriorityQueue<Process> pq = new PriorityQueue<>(Comparator.comparingInt(p -> p.burstTime));

        for (Process process : processes) {
            pq.add(process);
        }

        processInfoArea.setText("");
        new Thread(() -> {
            while (!pq.isEmpty()) {
                Process currentProcess = pq.poll();
                currentProcessLabel.setText("Current Process: " + currentProcess.name);
                processInfoArea.append("Processing: " + currentProcess.name + "\n");

                int remainingTime = currentProcess.burstTime;
                while (remainingTime > 0) {
                    if (!Scheduler.isRunning()) {
                        return;
                    }
                    remainingTime--;
                    currentProcess.progressBar.setValue(currentProcess.burstTime - remainingTime);
                    try {
                        Thread.sleep(1000); // Simulate one second of processing time
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                currentProcess.waitingTime = currentTime[0];
                currentTime[0] += currentProcess.burstTime;
                currentProcess.turnaroundTime = currentTime[0];

                totalWaitingTime[0] += currentProcess.waitingTime;
                totalTurnaroundTime[0] += currentProcess.turnaroundTime;
            }

            int processCount = processes.size();
            avgWaitingTimeLabel.setText("Average Waiting Time: " + (totalWaitingTime[0] / processCount));
            avgTurnaroundTimeLabel.setText("Average Turnaround Time: " + (totalTurnaroundTime[0] / processCount));
            totalExecutionTimeLabel.setText("Total Execution Time: " + currentTime[0]);

            Scheduler.setRunning(false);
        }).start();
    }
}