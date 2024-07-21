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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class main {

    private static JTextArea outputArea;
    private static JTextField filePathField, numSimsField;
    private static JTextArea seedArea;
    private static JToggleButton toggleThemeButton;
    private static boolean darkMode = false;
    private static volatile boolean running = false;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
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

            applyTheme();

            frame.setVisible(true);
        });
    }

    private static JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel filePathLabel = new JLabel("File Path:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(filePathLabel, gbc);

        filePathField = new JTextField(System.getProperty("user.home") + "\\New folder\\SSGOneShotPerchData.csv");
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

        toggleThemeButton = new JToggleButton("Toggle Dark/Light Mode");
        toggleThemeButton.addActionListener(e -> toggleTheme());
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        inputPanel.add(toggleThemeButton, gbc);

        return inputPanel;
    }

    private static void runSimulation() {
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
                    int[][] bedData = new int[seeds.size()][1401];

                    for (int seedNum = 0; seedNum < seeds.size() && running; ++seedNum) {
                        DragonFightManager dragonFight = new DragonFightManager(seeds.get(seedNum));
                        DragonFightManager.world.put(DragonFightManager.preHash(new BlockPos(0, dragonFight.fountainHeight, 0)), Blocks.OBSIDIAN);
                        for (int count = 0; count < numSims && running; ++count) {
                            EnderDragonEntity dragon = dragonFight.createNewDragon();
                            int tick = 20;
                            while (!dragon.getPhaseManager().getCurrentPhase().getType().equals(PhaseType.TAKEOFF)) {
                                dragon.livingTick();
                                ++tick;
                            }
                            ++bedData[seedNum][tick + 202];
                        }
                    }

                    for (int ss = 48; ss < 67; ++ss) {
                        for (int cs = 0; cs < 100; cs += 5) {
                            if (ss < 10) {
                                out.print(0);
                            }
                            out.print(ss + ".");
                            if (cs < 10) {
                                out.print(0);
                            }
                            out.print(cs + ",");
                        }
                    }
                    out.println("67.00");
                    for (int seedNum = 0; seedNum < seeds.size(); ++seedNum) {
                        for (int tick = 960; tick <= 1340; ++tick) {
                            out.print(bedData[seedNum][tick] / (double) numSims + ",");
                        }
                        out.println();
                    }
                } catch (IOException e) {
                    publish("Error: " + e.getMessage());
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
        };

        worker.execute();
    }

    private static void stopSimulation() {
        running = false;
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
            } catch (IOException ex) {
                outputArea.append("Error reading seeds from file: " + ex.getMessage() + "\n");
            }
        }
    }

    private static void toggleTheme() {
        darkMode = !darkMode;
        applyTheme();
    }

    private static void applyTheme() {
        if (darkMode) {
            UIManager.put("Panel.background", Color.DARK_GRAY);
            UIManager.put("Label.foreground", Color.WHITE);
            UIManager.put("TextField.background", Color.BLACK);
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("TextArea.background", Color.BLACK);
            UIManager.put("TextArea.foreground", Color.WHITE);
            UIManager.put("Button.background", Color.LIGHT_GRAY);
            UIManager.put("Button.foreground", Color.BLACK);
        } else {
            UIManager.put("Panel.background", Color.LIGHT_GRAY);
            UIManager.put("Label.foreground", Color.BLACK);
            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", Color.BLACK);
            UIManager.put("TextArea.background", Color.WHITE);
            UIManager.put("TextArea.foreground", Color.BLACK);
            UIManager.put("Button.background", Color.LIGHT_GRAY);
            UIManager.put("Button.foreground", Color.BLACK);
        }
        SwingUtilities.updateComponentTreeUI(filePathField.getParent());
    }

    private static void setUIFont(Font font) {
        UIManager.put("Label.font", font);
        UIManager.put("Button.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
    }
}
