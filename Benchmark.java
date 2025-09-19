public class Benchmark {
    public static void testPerformance() {
        int[] arr = new int[10000];
        for(int i = 0; i < arr.length; i++) arr[i] = (int)(Math.random() * 10000);

        long start = System.nanoTime();
        SortingAlgorithms.quickSort(arr.clone());
        long end = System.nanoTime();
        System.out.println("Quick Sort: " + (end - start)/1e6 + " ms");

        start = System.nanoTime();
        SortingAlgorithms.mergeSort(arr.clone());
        end = System.nanoTime();
        System.out.println("Merge Sort: " + (end - start)/1e6 + " ms");
    }
}