import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class lr3 {

    public static void main(String[] args) {
        final var scanner = new Scanner(System.in);
        int[][] matrix = generateMatrix(scanner);
        int[] vector = generateVector(scanner);

        // Вычисление с использованием 1 потока
        long start1 = System.nanoTime();
        int[] result1 = multiplyMatrixVector(matrix, vector, 1);
        long end1 =  System.nanoTime();
        System.out.println("Время выполнения с 1 потоком: " + (end1 - start1) + " нс");

        // Вычисление с использованием 2 потоков
        long start2 = System.nanoTime();
        int[] result2 = multiplyMatrixVector(matrix, vector, 2);
        long end2 = System.nanoTime();
        System.out.println("Время выполнения с 2 потоками: " + (end2 - start2) + " нс");

        // Вычисление с использованием 3 потоков
        long start3 = System.nanoTime();
        int[] result3 = multiplyMatrixVector(matrix, vector, 3);
        long end3 = System.nanoTime();
        System.out.println("Время выполнения с 3 потоками: " + (end3 - start3) + " нс");

        // Вывод результирующей матрицы
        System.out.println("Результирующая матрица:");
        for (double value : result1) {
            System.out.print(value + " ");
        }
        System.out.println();

    }


    private static int[] multiplyMatrixVector(int[][] matrix, int[] vector, int numThreads) {
        int rowCount = matrix.length;
        int[] result = new int[rowCount];

        //создаем пул потоков
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        java.util.List<Future<int[]>> futures = new java.util.ArrayList<>();

        int rowsPerThread = rowCount / numThreads;
        int remainder = rowCount % numThreads;
        //матрица разделяется на части и для каждой части создается задача
        //Callable для параллельного вычисления, задачи добавляются в список futures
        for (int i = 0; i < numThreads; i++) {
            int start = i * rowsPerThread;
            int end = (i + 1) * rowsPerThread;
            if (i == numThreads - 1) {
                end += remainder;
            }
            final int end1 = end;

            Callable<int[]> task = () -> multiplyMatrixVectorRange(matrix, vector, start, end1);
            futures.add(executorService.submit(task));
        }

        //результаты выполнения задач извлекаются из futures и объединяются в итоговый массив result
        for (int i = 0; i < numThreads; i++) {
            try {
                int[] partialResult = futures.get(i).get();
                for (int j = 0; j < partialResult.length; j++) {
                    result[j + i * rowsPerThread] = partialResult[j];
                }
            }  catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        return result;
    }

    private static int[] multiplyMatrixVectorRange(int[][] matrix, int[] vector, int start,  int end) {
        int[] result = new int[end - start];
        for (int i = start; i < end; i++) {
            for (int j = 0; j < vector.length; j++) {
                result[i - start] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }


    private static int[][] generateMatrix(final Scanner scanner) {

        System.out.println("Введите количество строк матрицы:");
        final var rows = scanner.nextInt();
        System.out.println("Введите количество столбцов матрицы:");
        final var cols = scanner.nextInt();
        final var matrixA = new int[rows][cols];
        System.out.println("Введите матрицу:");
        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < cols; j++) {
                matrixA[i][j] = scanner.nextInt();
            }
        }
        return matrixA;

    }

    private static int[] generateVector(final Scanner scanner) {
        System.out.println("Введите количество строк вектора:");
        final var Vrows = scanner.nextInt();
        int[] vector = new int[Vrows];
        System.out.println("Введите вектор:");
        for (int i = 0; i < Vrows; i++) {
            vector[i] = scanner.nextInt();
        }
        return vector;
    }
}