

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private static boolean isRunning = false;
    private JFrame frame;
    private JComboBox<String> algorithmComboBox;
    private JTextArea processInfoArea;
    private JLabel currentProcessLabel;
    private JLabel avgWaitingTimeLabel;
    private JLabel avgTurnaroundTimeLabel;
    private JLabel totalExecutionTimeLabel;
    private JPanel progressPanel;
    private JTextArea processDetailsArea;

    private List<Process> processes;

    public Scheduler() {
        processes = new ArrayList<>();
        frame = new JFrame("CPU Scheduler");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Processes");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (processes.size() < 10) {
                    JTextField numProcessesField = new JTextField();
                    Object[] inputFields = {
                        "Number of Processes:", numProcessesField
                    };
                    int option = JOptionPane.showConfirmDialog(null, inputFields, "Number of Processes", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        int numProcesses;
                        try {
                            numProcesses = Integer.parseInt(numProcessesField.getText().trim());
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame, "Invalid number of processes. Please enter an integer.");
                            return;
                        }
                        processDetailsArea.setText(""); // Clear previous process details
                        for (int i = 0; i < numProcesses && processes.size() < 10; i++) {
                            JTextField nameField = new JTextField();
                            JTextField burstField = new JTextField();
                            Object[] processFields = {
                                "Process Name:", nameField,
                                "Burst Time:", burstField
                            };
                            int processOption = JOptionPane.showConfirmDialog(null, processFields, "Add Process", JOptionPane.OK_CANCEL_OPTION);
                            if (processOption == JOptionPane.OK_OPTION) {
                                String name = nameField.getText().trim();
                                int burstTime;
                                try {
                                    burstTime = Integer.parseInt(burstField.getText().trim());
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(frame, "Invalid burst time input. Please enter an integer.");
                                    i--;
                                    continue;
                                }
                                if (name.isEmpty()) {
                                    JOptionPane.showMessageDialog(frame, "Process name cannot be empty.");
                                    i--;
                                    continue;
                                }
                                Process newProcess = new Process(name, burstTime);
                                processes.add(newProcess);

                                // Display process name and burst time in the text area
                                processDetailsArea.append("Process Name: " + name + ", Burst Time: " + burstTime + "\n");

                                // Create a panel to display process name, burst time and progress bar
                                JPanel processPanel = new JPanel(new BorderLayout());
                                JLabel processLabel = new JLabel(name + " (Burst Time: " + burstTime + ")");
                                processPanel.add(processLabel, BorderLayout.NORTH);
                                processPanel.add(newProcess.progressBar, BorderLayout.CENTER);
                                progressPanel.add(processPanel);

                                progressPanel.revalidate();
                                progressPanel.repaint();
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Process limit reached. Maximum 10 processes allowed.");
                }
            }
        });
        topPanel.add(addButton);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isRunning && !processes.isEmpty()) {
                    isRunning = true;
                    String selectedAlgorithm = (String) algorithmComboBox.getSelectedItem();
                    SchedulingAlgorithm algorithm;
                    switch (selectedAlgorithm) {
                        case "FCFS":
                            algorithm = new FCFSAlgorithm();
                            break;
                        case "SJF":
                            algorithm = new SJFAlgorithm();
                            break;
                        case "Priority":
                            algorithm = new PSAlgorithm();
                            break;
                        case "Round Robin":
                            int timeSlice = 2; // Example time slice value, can be adjusted
                            algorithm = new RRAlgorithm(timeSlice);
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown Algorithm Selected");
                    }
                    algorithm.schedule(processes, processInfoArea, currentProcessLabel, avgWaitingTimeLabel, avgTurnaroundTimeLabel, totalExecutionTimeLabel, progressPanel);
                }
            }
        });
        topPanel.add(startButton);

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRunning = false;
            }
        });
        topPanel.add(stopButton);

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isRunning = false;
                processes.clear();
                progressPanel.removeAll();
                progressPanel.revalidate();
                progressPanel.repaint();
                processInfoArea.setText("");
                processDetailsArea.setText(""); // Clear process details
                currentProcessLabel.setText("Current Process: ");
                avgWaitingTimeLabel.setText("Average Waiting Time: ");
                avgTurnaroundTimeLabel.setText("Average Turnaround Time: ");
                totalExecutionTimeLabel.setText("Total Execution Time: ");
            }
        });
        topPanel.add(resetButton);

        algorithmComboBox = new JComboBox<>(new String[]{"FCFS", "SJF", "Priority", "Round Robin"});
        topPanel.add(algorithmComboBox);

        frame.add(topPanel, BorderLayout.NORTH);

        processInfoArea = new JTextArea(10, 30);
        processInfoArea.setEditable(false);
        frame.add(new JScrollPane(processInfoArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(4, 1));
        currentProcessLabel = new JLabel("Current Process: ");
        avgWaitingTimeLabel = new JLabel("Average Waiting Time: ");
        avgTurnaroundTimeLabel = new JLabel("Average Turnaround Time: ");
        totalExecutionTimeLabel = new JLabel("Total Execution Time: ");
        bottomPanel.add(currentProcessLabel);
        bottomPanel.add(avgWaitingTimeLabel);
        bottomPanel.add(avgTurnaroundTimeLabel);
        bottomPanel.add(totalExecutionTimeLabel);
        frame.add(bottomPanel, BorderLayout.EAST);

        progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        frame.add(new JScrollPane(progressPanel), BorderLayout.SOUTH);

        processDetailsArea = new JTextArea(10, 30);
        processDetailsArea.setEditable(false);
        processDetailsArea.setBackground(Color.WHITE);
        frame.add(new JScrollPane(processDetailsArea), BorderLayout.WEST);

        frame.setVisible(true);
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static void setRunning(boolean isRunning) {
        Scheduler.isRunning = isRunning;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Scheduler::new);
    }
}
