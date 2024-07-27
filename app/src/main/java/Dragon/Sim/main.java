package Dragon.Sim;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.entity.boss.dragon.phase.PhaseType;
import Dragon.Sim.net.minecraft.world.end.DragonFightManager;
import Dragon.Sim.net.minecraft.util.math.BlockPos;
import kaptainwutax.mcutils.block.Blocks;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class main {

    // GUI Components
    private static JTextArea outputArea;
    private static JTextField filePathField, numSimsField;
    private static JTextArea seedArea;
    private static volatile boolean running = false;

    // Buffer size for handling ticks beyond the expected range
    private static final int TICK_BUFFER = 300;

    public static void main(String[] args) {
        // Initialize the main frame
        JFrame frame = new JFrame("Dragon Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Set up the root panel with border layout
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(root);

        // Create and add the input panel to the root panel
        JPanel inputPanel = createInputPanel();
        root.add(inputPanel, BorderLayout.NORTH);

        // Initialize and configure the output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        root.add(outputScrollPane, BorderLayout.CENTER);

        // Set font for UI components
        setUIFont(new Font("Arial", Font.PLAIN, 14));

        // Make the frame visible
        frame.setVisible(true);
    }

    // Method to create the input panel with various controls
    private static JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Label for File Path and its text field
        JLabel filePathLabel = new JLabel("File Path:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(filePathLabel, gbc);

        filePathField = new JTextField(System.getProperty("user.home") + File.separator + "New folder" + File.separator + "SSGOneShotRawPerchData.csv");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(filePathField, gbc);

        // Label for Number of Simulations and its text field
        JLabel numSimsLabel = new JLabel("Number of Simulations:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        inputPanel.add(numSimsLabel, gbc);

        numSimsField = new JTextField("1000");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(numSimsField, gbc);

        // Label for Seeds and text area for seeds
        JLabel seedLabel = new JLabel("Seeds (comma separated) or Load from File:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        inputPanel.add(seedLabel, gbc);

        seedArea = new JTextArea(3, 20);
        seedArea.setLineWrap(true);
        seedArea.setWrapStyleWord(true);
        JScrollPane seedScrollPane = new JScrollPane(seedArea);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        inputPanel.add(seedScrollPane, gbc);

        // Buttons for loading seeds, starting, and stopping simulations
        JButton loadSeedsButton = new JButton("Load Seeds from CSV");
        loadSeedsButton.addActionListener(e -> loadSeeds());
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        inputPanel.add(loadSeedsButton, gbc);

        JButton startButton = new JButton("Start Simulation");
        startButton.addActionListener(e -> runSimulation());
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        inputPanel.add(startButton, gbc);

        JButton stopButton = new JButton("Stop Simulation");
        stopButton.addActionListener(e -> stopSimulation());
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        inputPanel.add(stopButton, gbc);

        return inputPanel;
    }

    // Method to start the simulation
    private static void runSimulation() {
        // Check if simulation is already running
        if (running) {
            outputArea.append("Simulation is already running.\n");
            return;
        }

        outputArea.append("Starting simulation...\n");

        String filePath = filePathField.getText();
        int numSims;
        // Validate and parse the number of simulations
        try {
            numSims = Integer.parseInt(numSimsField.getText());
        } catch (NumberFormatException e) {
            outputArea.append("Invalid number of simulations.\n");
            return;
        }

        List<Long> seeds = new ArrayList<>();
        // Parse the seeds from the input text area
        String[] seedStrings = seedArea.getText().split(",");
        for (String seedStr : seedStrings) {
            try {
                seeds.add(Long.parseLong(seedStr.trim()));
            } catch (NumberFormatException e) {
                outputArea.append("Invalid seed: " + seedStr + "\n");
                return;
            }
        }

        running = true;
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)))) {
                    int arraySize = 1600 + TICK_BUFFER;
                    int[][] bedData = new int[seeds.size()][arraySize];

                    int totalSimulations = seeds.size() * numSims;
                    int progressInterval = totalSimulations / 20;  // 5% increment for progress updates
                    int simulationsCompleted = 0;

                    for (int seedNum = 0; seedNum < seeds.size() && running; ++seedNum) {
                        try {
                            // Initialize the DragonFightManager for the current seed
                            DragonFightManager dragonFight = new DragonFightManager(seeds.get(seedNum));
                            DragonFightManager.world.put(DragonFightManager.preHash(new BlockPos(0, dragonFight.FountainHeight, 0)), Blocks.OBSIDIAN);
                            for (int count = 0; count < numSims && running; ++count) {
                                // Create a new dragon and simulate its behavior
                                EnderDragonEntity dragon = dragonFight.createNewDragon();
                                int tick = 20;
                                while (!dragon.getPhaseManager().getCurrentPhase().getType().equals(PhaseType.TAKEOFF)) {
                                    dragon.livingTick();
                                    ++tick;
                                }
                                // Record data or publish a warning if index is out of bounds
                                if (tick + 202 < bedData[seedNum].length) {
                                    ++bedData[seedNum][tick + 202];
                                } else {
                                    publish("Warning: Tick index " + (tick + 202) + " out of bounds.");
                                }

                                // Update progress
                                simulationsCompleted++;
                                if (simulationsCompleted % progressInterval == 0) {
                                    int percentCompleted = (int) ((double) simulationsCompleted / totalSimulations * 100);
                                    int simulationsLeft = totalSimulations - simulationsCompleted;
                                    publish(String.format("%d%% of the total number of simulations have been completed (%d simulations still left to complete)", percentCompleted, simulationsLeft));
                                }
                            }
                        } catch (Exception e) {
                            publish("Error during simulation for seed " + seeds.get(seedNum) + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    // Write the results to the output file
                    for (int ss = 30; ss < 67; ++ss) {
                        for (int cs = 0; cs < 100; cs += 5) {
                            out.printf("%02d.%02d,", ss, cs);
                        }
                    }
                    out.println("67.00");
                    for (int seedNum = 0; seedNum < seeds.size(); ++seedNum) {
                        for (int tick = 600; tick <= 1340; ++tick) {
                            if (tick < bedData[seedNum].length) {
                                out.printf("%f,", bedData[seedNum][tick] / (double) numSims);
                            } else {
                                out.print("0,");  // Default value if out of bounds
                            }
                        }
                        out.println();
                    }

                    // Indicate that the simulation is complete and the data has been saved
                    publish("Simulation complete. Data saved to " + filePath);
                } catch (IOException e) {
                    publish("Error saving data to file: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                // Update the output area with messages during simulation
                for (String message : chunks) {
                    outputArea.append(message + "\n");
                }
            }

            @Override
            protected void done() {
                // Mark the simulation as finished and update the output area
                running = false;
                outputArea.append("Simulation finished.\n");
            }
        };

        // Execute the SwingWorker to start the simulation in a background thread
        worker.execute();
    }

    // Method to stop the currently running simulation
    private static void stopSimulation() {
        // Check if there is a running simulation to stop
        if (!running) {
            outputArea.append("No simulation is currently running.\n");
            return;
        }
        // Set the running flag to false in order to stop the simulation
        running = false;
        outputArea.append("Stopping simulation...\n");
    }

    // Method to load seeds from a CSV file
    private static void loadSeeds() {
        // Create a file chooser for selecting the CSV file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // If a file is selected, read the seeds from the file
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder seeds = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    seeds.append(line).append(",");
                }
                // Set the loaded seeds to the text area for seeds
                seedArea.setText(seeds.toString());
            } catch (IOException e) {
                // Handle any I/O exceptions that occur during file reading
                outputArea.append("Error loading seeds from file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    // Method to set the font for all UI components
    private static void setUIFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
    }
}
