

import javax.swing.*;
import java.util.List;

public interface SchedulingAlgorithm {
    void schedule(List<Process> processes, JTextArea processInfoArea, JLabel currentProcessLabel, JLabel avgWaitingTimeLabel, JLabel avgTurnaroundTimeLabel, JLabel totalExecutionTimeLabel, JPanel progressPanel);
}
