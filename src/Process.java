
import javax.swing.*;

public class Process {
    public String name;
    public int burstTime;
    public int waitingTime;
    public int turnaroundTime;
    public JProgressBar progressBar;

    public Process(String name, int burstTime) {
        this.name = name;
        this.burstTime = burstTime;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.progressBar = new JProgressBar(0, burstTime);
        this.progressBar.setStringPainted(true);
        this.progressBar.setString(name);
    }
}
