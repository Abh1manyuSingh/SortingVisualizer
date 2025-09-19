public class Main {
    public static void main(String[] args) {
        // Launch the visualizer
        javax.swing.SwingUtilities.invokeLater(() -> {
            Visualizer visualizer = new Visualizer();
            visualizer.setVisible(true);
        });
    }
}