package Dragon.Sim;

import Dragon.Sim.net.minecraft.entity.boss.dragon.EnderDragonEntity;
import Dragon.Sim.net.minecraft.entity.boss.dragon.phase.PhaseType;
import Dragon.Sim.net.minecraft.util.math.BlockPos;
import Dragon.Sim.net.minecraft.world.end.DragonFightManager;
import kaptainwutax.mcutils.block.Blocks;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class main {

    private static JTextArea outputArea;
    private static JTextField filePathField, numSimsField;
    private static JTextArea seedArea;
    private static volatile boolean running = false;

    private static final int TICK_BUFFER = 300;  // Buffer size to handle ticks beyond expected range

    public static void main(String[] args) {
        JFrame frame = new JFrame("Dragon Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        frame.setContentPane(root);

        JPanel inputPanel = createInputPanel();
        root.add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        root.add(outputScrollPane, BorderLayout.CENTER);

        setUIFont(new Font("Arial", Font.PLAIN, 14));

        frame.setVisible(true);
    }

    private static JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel filePathLabel = new JLabel("File Path:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        inputPanel.add(filePathLabel, gbc);

        filePathField = new JTextField(System.getProperty("user.home") + File.separator + "New folder" + File.separator + "SSGOneShotPerchData.csv");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(filePathField, gbc);

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

    private static void runSimulation() {
        if (running) {
            outputArea.append("Simulation is already running.\n");
            return;
        }

        outputArea.append("Starting simulation...\n");

        String filePath = filePathField.getText();
        int numSims;
        try {
            numSims = Integer.parseInt(numSimsField.getText());
        } catch (NumberFormatException e) {
            outputArea.append("Invalid number of simulations.\n");
            return;
        }

        List<Long> seeds = new ArrayList<>();
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
                    int arraySize = 1600 + TICK_BUFFER;  // Adjust this size based on your needs
                    int[][] bedData = new int[seeds.size()][arraySize];

                    int totalSimulations = seeds.size() * numSims;
                    int progressInterval = totalSimulations / 20;  // 5% increment for progress updates
                    int simulationsCompleted = 0;

                    for (int seedNum = 0; seedNum < seeds.size() && running; ++seedNum) {
                        try {
                            DragonFightManager dragonFight = new DragonFightManager(seeds.get(seedNum));
                            DragonFightManager.world.put(DragonFightManager.preHash(new BlockPos(0, dragonFight.fountainHeight, 0)), Blocks.OBSIDIAN);
                            for (int count = 0; count < numSims && running; ++count) {
                                EnderDragonEntity dragon = dragonFight.createNewDragon();
                                int tick = 20;
                                while (!dragon.getPhaseManager().getCurrentPhase().getType().equals(PhaseType.TAKEOFF)) {
                                    dragon.livingTick();
                                    ++tick;
                                }
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

                    // Write data to file
                    for (int ss = 30; ss < 67; ++ss) { // Adjusted loop to start from 30 instead of 48
                        for (int cs = 0; cs < 100; cs += 5) {
                            out.printf("%02d.%02d,", ss, cs);
                        }
                    }
                    out.println("67.00");
                    for (int seedNum = 0; seedNum < seeds.size(); ++seedNum) {
                        for (int tick = 600; tick <= 1340; ++tick) { // Adjusted start tick to 600 to cover the range for 30 seconds
                            if (tick < bedData[seedNum].length) {
                                out.printf("%f,", bedData[seedNum][tick] / (double) numSims);
                            } else {
                                out.print("0,");  // Default value if out of bounds
                            }
                        }
                        out.println();
                    }
                } catch (IOException e) {
                    publish("Error writing to file: " + e.getMessage());
                    e.printStackTrace();
                }

                publish("Simulation complete. Data saved to " + filePath);
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    outputArea.append(message + "\n");
                }
            }

            @Override
            protected void done() {
                running = false;
                outputArea.append("Simulation finished.\n");
            }
        };

        worker.execute();
    }

    private static void stopSimulation() {
        if (!running) {
            outputArea.append("No simulation is currently running.\n");
            return;
        }
        running = false;
        outputArea.append("Stopping simulation...\n");
    }

    private static void loadSeeds() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder seeds = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    seeds.append(line).append(",");
                }
                seedArea.setText(seeds.toString());
            } catch (IOException e) {
                outputArea.append("Error loading seeds from file: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        }
    }

    private static void setUIFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
    }
}
