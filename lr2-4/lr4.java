import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class lr4{

    public static void main(String[] args) {
        final var scanner = new Scanner(System.in);
        System.out.println("1ая матрица");
        int[][] matrix1 = generateMatrix(scanner);
        System.out.println("2ая матрица");
        int[][] matrix2 = generateMatrix(scanner);


        // Вычисление с использованием 1 потока
        long start1 = System.nanoTime();
        int[][] result1 = multiply(matrix1, matrix2, 1);
        long end1 =  System.nanoTime();
        System.out.println("Время выполнения с 1 потоком: " + (end1 - start1) + " нс");

        // Вычисление с использованием 2 потоков
        long start2 = System.nanoTime();
        int[][] result2 = multiply(matrix1, matrix2, 2);
        long end2 = System.nanoTime();
        System.out.println("Время выполнения с 2 потоками: " + (end2 - start2) + " нс");

        // Вычисление с использованием 3 потоков
        long start3 = System.nanoTime();
        int[][] result3 = multiply(matrix1, matrix2, 3);
        long end3 = System.nanoTime();
        System.out.println("Время выполнения с 3 потоками: " + (end3 - start3) + " нс");

        // Вывод результирующей матрицы
        System.out.println("Результирующая матрица:");
        for (int[] row : result1) {
            for (int element : row) {
                System.out.printf("%4d", element);
            }
            System.out.println();
        }


    }
    private static int[][] multiply(int[][] matrix1, int[][] matrix2, int numThreads) {
        int size = matrix1.length;
        int[][] result = new int[size][size];

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        java.util.List<Future<int[][]>> futures = new java.util.ArrayList<>();

        int rowsPerThread = size / numThreads;
        int remainder = size % numThreads;

        for (int i = 0; i < numThreads; i++) {
            int start = i * rowsPerThread;
            int end = (i + 1) * rowsPerThread;
            if (i == numThreads - 1) {
                end += remainder;
            }
            final int end1=end;
            Callable<int[][]> task = () -> multiplyMatricesRange(matrix1, matrix2, start, end1);
            futures.add(executorService.submit(task));
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                int[][] partialResult = futures.get(i).get();
                for (int row = 0; row < partialResult.length; row++) {
                    for (int col = 0; col < partialResult[0].length; col++) {
                        result[row + i * rowsPerThread][col] = partialResult[row][col];
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        return result;
    }

    private static int[][] multiplyMatricesRange(int[][] matrix1, int[][] matrix2, int startRow, int endRow) {
        int size = matrix1.length;
        int[][] result = new int[endRow - startRow][size];

        for (int i = startRow; i < endRow; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i - startRow][j] += matrix1[i][k] * matrix2[k][j];
                }
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

}