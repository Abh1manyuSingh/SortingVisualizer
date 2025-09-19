import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Visualizer extends JFrame {
    private int[] array;
    private JPanel panel;
    private JTextArea descriptionArea;
    private javax.swing.Timer timer;

    private int i, j;
    private String currentAlgo;
    private boolean isPaused = false;

    private int compareA = -1, compareB = -1;  
    private int sortedUpto = -1;               

    private JSlider speedSlider;
    private JTextField arrayInput;
    private JLabel statsLabel;                 

    private long startTime;                    
    private int comparisons, swaps;            

    private Deque<int[]> quickTasks;
    private Deque<int[]> mergeTasks;
    private int partitionIndex;

    public Visualizer() {
        setTitle("Algorithm Explorer: Sorting Visualizer with Analysis Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        array = generateArray(50, "random");

        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Sorting Buttons
        JPanel sortButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton bubbleBtn = new JButton("Bubble Sort");
        bubbleBtn.addActionListener(e -> startSort("bubble"));
        JButton insertionBtn = new JButton("Insertion Sort");
        insertionBtn.addActionListener(e -> startSort("insertion"));
        JButton selectionBtn = new JButton("Selection Sort");
        selectionBtn.addActionListener(e -> startSort("selection"));
        JButton quickBtn = new JButton("Quick Sort");
        quickBtn.addActionListener(e -> startSort("quick"));
        JButton mergeBtn = new JButton("Merge Sort");
        mergeBtn.addActionListener(e -> startSort("merge"));
        sortButtonPanel.add(bubbleBtn);
        sortButtonPanel.add(insertionBtn);
        sortButtonPanel.add(selectionBtn);
        sortButtonPanel.add(quickBtn);
        sortButtonPanel.add(mergeBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        controlPanel.add(sortButtonPanel, gbc);

        // Array Generation Buttons
        JPanel generatePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton randomBtn = new JButton("Random Array");
        randomBtn.addActionListener(e -> {
            resetState();
            array = generateArray(50, "random");
            repaint();
            updateStats("Random array generated");
        });
        JButton sortedBtn = new JButton("Sorted Array");
        sortedBtn.addActionListener(e -> {
            resetState();
            array = generateArray(50, "sorted");
            repaint();
            updateStats("Sorted array generated");
        });
        JButton reverseBtn = new JButton("Reverse Array");
        reverseBtn.addActionListener(e -> {
            resetState();
            array = generateArray(50, "reverse");
            repaint();
            updateStats("Reverse array generated");
        });
        generatePanel.add(randomBtn);
        generatePanel.add(sortedBtn);
        generatePanel.add(reverseBtn);

        gbc.gridy = 1;
        controlPanel.add(generatePanel, gbc);

        // Array Input Bar
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        arrayInput = new JTextField(20);
        JButton setArrayBtn = new JButton("Set Array");
        setArrayBtn.addActionListener(e -> {
            try {
                String[] parts = arrayInput.getText().trim().split(",");
                int[] custom = new int[parts.length];
                for (int k = 0; k < parts.length; k++) {
                    custom[k] = Integer.parseInt(parts[k].trim());
                }
                resetState();
                array = custom;
                repaint();
                updateStats("Custom array set");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input! Use comma-separated numbers.");
            }
        });
        inputPanel.add(new JLabel("Custom Array (e.g., 5,3,8,1):"));
        inputPanel.add(arrayInput);
        inputPanel.add(setArrayBtn);

        gbc.gridy = 2;
        controlPanel.add(inputPanel, gbc);

        // Playback Controls
        JPanel playbackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton pauseBtn = new JButton("Pause");
        pauseBtn.addActionListener(e -> {
            if (timer != null) {
                timer.stop();
                isPaused = true;
            }
        });
        JButton resumeBtn = new JButton("Resume");
        resumeBtn.addActionListener(e -> {
            if (timer != null && isPaused) {
                timer.start();
                isPaused = false;
            }
        });
        JButton stepBtn = new JButton("Step");
        stepBtn.addActionListener(e -> {
            if (timer != null && timer.isRunning()) {
                timer.stop();
            }
            isPaused = true;
            stepSort();
        });
        playbackPanel.add(pauseBtn);
        playbackPanel.add(resumeBtn);
        playbackPanel.add(stepBtn);
        
        gbc.gridy = 3;
        controlPanel.add(playbackPanel, gbc);
        
        // Speed Slider
        speedSlider = new JSlider(JSlider.HORIZONTAL, 10, 500, 80);
        speedSlider.setMajorTickSpacing(100);
        speedSlider.setMinorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setBorder(BorderFactory.createTitledBorder("Speed (Fast → Slow)"));
        speedSlider.addChangeListener(e -> {
            if (timer != null) {
                timer.setDelay(speedSlider.getValue());
            }
        });
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        controlPanel.add(speedSlider, gbc);

        add(controlPanel, BorderLayout.NORTH);

        // Visualization Panel
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawArray(g);
            }
        };
        panel.setBackground(Color.WHITE);
        add(panel, BorderLayout.CENTER);

        // Analysis Dashboard & Description Panel
        JPanel southPanel = new JPanel(new GridBagLayout());
        
        statsLabel = new JLabel("Ready");
        JPanel statsPanel = new JPanel();
        statsPanel.setBorder(BorderFactory.createTitledBorder("Analysis Dashboard"));
        statsPanel.add(statsLabel);
        
        descriptionArea = new JTextArea(5, 40);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Algorithm Description"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        southPanel.add(statsPanel, gbc);

        gbc.gridx = 1;
        southPanel.add(scrollPane, gbc);

        add(southPanel, BorderLayout.SOUTH);
    }
    
    private void resetState() {
        if (timer != null) {
            timer.stop();
        }
        i = 0;
        j = 0;
        compareA = compareB = -1;
        sortedUpto = -1;
        isPaused = false;
        comparisons = 0;
        swaps = 0;
    }

    private void startSort(String algo) {
        resetState();
        currentAlgo = algo;
        startTime = System.nanoTime();
        updateDescription(algo);

        if (algo.equals("quick")) {
            quickTasks = new ArrayDeque<>();
            quickTasks.push(new int[]{0, array.length - 1});
        }
        
        if (algo.equals("merge")) {
            mergeTasks = new ArrayDeque<>();
            mergeTasks.push(new int[]{0, array.length - 1});
        }
        
        isPaused = false;
        timer = new javax.swing.Timer(speedSlider.getValue(), e -> stepSort());
        timer.start();
    }
    
    private void stepSort() {
        if (currentAlgo == null) return;
        
        long currentTime = System.nanoTime();
        double timeMs = (currentTime - startTime) / 1_000_000.0;
        updateStats("Algorithm: " + currentAlgo.toUpperCase() + 
                " | Comparisons: " + comparisons + 
                " | Swaps: " + swaps + 
                " | Time: " + String.format("%.2f ms", timeMs));

        switch (currentAlgo) {
            case "bubble": bubbleStep(); break;
            case "insertion": insertionStep(); break;
            case "selection": selectionStep(); break;
            case "quick": quickStep(); break;
            case "merge": mergeStep(); break;
        }
        panel.repaint();
    }
    
    private void bubbleStep() {
        if (i < array.length - 1) {
            if (j < array.length - i - 1) {
                compareA = j;
                compareB = j + 1;
                comparisons++;
                if (array[j] > array[j + 1]) {
                    swap(j, j + 1);
                }
                j++;
            } else {
                sortedUpto = array.length - i - 1;
                j = 0;
                i++;
            }
        } else {
            sortedUpto = array.length - 1;
            timer.stop();
            finishSort();
        }
    }

    private void insertionStep() {
        if (i < array.length) {
            if (j == 0) {
                j = i - 1;
                while (j >= 0 && array[j] > array[i]) {
                    j--;
                }
                int key = array[i];
                for (int k = i; k > j + 1; k--) {
                    array[k] = array[k - 1];
                    swaps++;
                }
                array[j + 1] = key;
                swaps++;
                sortedUpto = i;
                i++;
                comparisons += (i - j - 1);
            }
        }
        if (i >= array.length) {
            sortedUpto = array.length - 1;
            timer.stop();
            finishSort();
        }
    }
    
    private void selectionStep() {
        if (i < array.length - 1) {
            int minIndex = i;
            for (int k = i + 1; k < array.length; k++) {
                compareA = minIndex;
                compareB = k;
                comparisons++;
                if (array[k] < array[minIndex]) {
                    minIndex = k;
                }
            }
            if (minIndex != i) {
                swap(i, minIndex);
            }
            sortedUpto = i;
            i++;
        } else {
            sortedUpto = array.length - 1;
            timer.stop();
            finishSort();
        }
    }
    
    private void quickStep() {
        if (!quickTasks.isEmpty()) {
            int[] range = quickTasks.peek();
            int low = range[0], high = range[1];
            if (low < high) {
                int pivot = partition(low, high);
                quickTasks.pop();
                quickTasks.push(new int[]{pivot + 1, high});
                quickTasks.push(new int[]{low, pivot - 1});
            } else {
                quickTasks.pop();
            }
        } else {
            sortedUpto = array.length - 1;
            timer.stop();
            finishSort();
        }
    }
    
    private int partition(int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            compareA = j;
            compareB = high;
            comparisons++;
            if (array[j] < pivot) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }
    
    private void mergeStep() {
        if (!mergeTasks.isEmpty()) {
            int[] range = mergeTasks.pop();
            int left = range[0], right = range[1];
            if (left < right) {
                int mid = (left + right) / 2;
                mergeTasks.push(new int[]{mid + 1, right});
                mergeTasks.push(new int[]{left, mid});
                merge(left, mid, right);
            }
        } else {
            sortedUpto = array.length - 1;
            timer.stop();
            finishSort();
        }
    }

    private void merge(int left, int mid, int right) {
        int n1 = mid - left + 1;
        int n2 = right - mid;

        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(array, left, L, 0, n1);
        System.arraycopy(array, mid + 1, R, 0, n2);

        int p = 0, q = 0, k = left;
        while (p < n1 && q < n2) {
            compareA = left + p;
            compareB = mid + 1 + q;
            comparisons++;
            if (L[p] <= R[q]) {
                array[k++] = L[p++];
            } else {
                array[k++] = R[q++];
                swaps++;
            }
        }
        while (p < n1) array[k++] = L[p++];
        while (q < n2) array[k++] = R[q++];
    }

    private void swap(int a, int b) {
        if (a != b) {
            swaps++;
            int temp = array[a];
            array[a] = array[b];
            array[b] = temp;
        }
    }

    private void drawArray(Graphics g) {
        if (panel.getWidth() <= 0 || array.length == 0) return;
        
        int width = Math.max(1, panel.getWidth() / array.length);
        int maxValue = Arrays.stream(array).max().orElse(1);
        int maxHeight = panel.getHeight() - 20; 
        
        Font font = new Font("Arial", Font.BOLD, 10);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);

        for (int idx = 0; idx < array.length; idx++) {
            int height = Math.max(5, (array[idx] * maxHeight) / maxValue);
            int x = idx * width;
            int y = panel.getHeight() - height;
            
            if (idx == compareA || idx == compareB) {
                g.setColor(Color.RED);
            } else if (idx <= sortedUpto) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.BLUE);
            }

            g.fillRect(x, y, width - 1, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width - 1, height);
            
            String numStr = String.valueOf(array[idx]);
            int strWidth = fm.stringWidth(numStr);
            g.setColor(Color.BLACK);
            if (height > 20) {
                g.drawString(numStr, x + (width - strWidth) / 2, y + 15);
            }
        }
    }

    private int[] generateArray(int size, String type) {
        int[] arr = new int[size];
        Random rand = new Random();

        switch (type) {
            case "sorted":
                for (int i = 0; i < size; i++) arr[i] = i + 1;
                break;
            case "reverse":
                for (int i = 0; i < size; i++) arr[i] = size - i;
                break;
            default:
                for (int i = 0; i < size; i++) arr[i] = rand.nextInt(100) + 1;
        }
        return arr;
    }

    private void updateStats(String text) {
        statsLabel.setText(text);
    }
    
    private void updateDescription(String algo) {
        String description = "";
        switch (algo) {
            case "bubble":
                description = "Bubble Sort: Repeatedly steps through the list, compares adjacent elements, and swaps them if they are in the wrong order.\n" +
                              "Big-O: Best O(n), Avg O(n²), Worst O(n²)";
                break;
            case "insertion":
                description = "Insertion Sort: Builds the final sorted array one item at a time. It iterates through the input elements, growing the sorted list at each iteration.\n" +
                              "Big-O: Best O(n), Avg O(n²), Worst O(n²)";
                break;
            case "selection":
                description = "Selection Sort: Divides the input list into two parts: a sorted and an unsorted sublist. It repeatedly finds the minimum element from the unsorted sublist and puts it at the beginning of the sorted sublist.\n" +
                              "Big-O: Best O(n²), Avg O(n²), Worst O(n²)";
                break;
            case "quick":
                description = "Quick Sort: A divide-and-conquer algorithm. It picks an element as a pivot and partitions the given array around the picked pivot.\n" +
                              "Big-O: Best O(n log n), Avg O(n log n), Worst O(n²)";
                break;
            case "merge":
                description = "Merge Sort: A divide-and-conquer algorithm that divides the unsorted list into n sublists, each containing one element, and repeatedly merges sublists to produce new sorted sublists until there is only one sublist remaining.\n" +
                              "Big-O: Best O(n log n), Avg O(n log n), Worst O(n log n)";
                break;
        }
        descriptionArea.setText(description);
    }

    private void finishSort() {
        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;
        updateStats("Algorithm: " + currentAlgo.toUpperCase() +
                " | Comparisons: " + comparisons +
                " | Swaps: " + swaps +
                " | Time: " + String.format("%.2f ms", timeMs) +
                " | Status: Complete!");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getLookAndFeel());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Visualizer().setVisible(true);
        });
    }
}