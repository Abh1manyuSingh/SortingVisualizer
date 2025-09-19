# Java Sorting Algorithm Visualizer
A desktop application built with Java Swing to visualize popular sorting algorithms. It allows users to see how different algorithms work in real-time, with customizable arrays and real-time performance metrics.

Features

- Interactive Visualization**: Watch sorting algorithms like Bubble Sort, Merge Sort, and Quick Sort run step-by-step.
- Customizable Arrays**: Generate random, sorted, or reverse-sorted arrays, or input your own custom list of numbers.
- Real-time Metrics**: Track the number of comparisons and swaps, as well as the execution time, for each algorithm.
- Algorithm Descriptions**: Get a brief explanation of each algorithm's logic and its Big-O complexity.


How to Run

1.  Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/Sorting-Visualizer.git](https://github.com/your-username/Sorting-Visualizer.git)
    ```
2.  Navigate to the project directory:**
    ```bash
    cd Sorting-Visualizer
    ```
3.  Compile the Java files:**
    ```bash
    javac -d bin src/*.java
    ```
    *(Note: You might need to adjust this command based on your project structure if it's different from the one provided.)*
4.  Run the application:**
    ```bash
    java -cp bin Main
    ```


    📂 Project Structure

- `src/Main.java`: The main entry point of the application.
- `src/Visualizer.java`: Contains the GUI components and the core logic for the visualization and real-time updates.
- `src/SortingAlgorithms.java`: A collection of all the sorting algorithms implemented.
- `src/Benchmark.java`: A utility class for performance testing the algorithms.
- `src/DataHandler.java`: A class for loading data from CSV files (currently a placeholder for future features).


Technologies
- Java
- Java Swing
