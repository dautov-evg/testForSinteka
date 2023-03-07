package org.example;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        associateStringFromFile("input.txt");
    }

//
    public static void associateStringFromFile(String inputFileName) {
        try (Scanner scanner = new Scanner(new File(inputFileName))) {

            String[] values1 = readValuesFromFile(scanner);
            String[] values2 = readValuesFromFile(scanner);

            List<Integer> maxi = maximumWordMatch(values1, values2);
            writeToFile(values1, values2, maxi);

        } catch (FileNotFoundException e) {
            System.out.println("Ошибка, файл для чтения не найден!");
        }
    }

//    чтение из файла и запись массива строк
    public static String[] readValuesFromFile(Scanner scanner) {
        int rowCount = scanner.nextInt();
        scanner.nextLine();
        String[] values = new String[rowCount];
        for (int i = 0; i < values.length; i++) {
            values[i] = scanner.nextLine();
        }
        return values;
    }

//    запись в файл
    public static void writeToFile(String[] values1, String[] values2, List<Integer> maxi) {
        String[] longArray;
        String[] shortArray;
        if (values1.length >= values2.length) {
            longArray = values1;
            shortArray = values2;
        } else {
            longArray = values2;
            shortArray = values1;
        }
        try (PrintWriter writer = new PrintWriter("output.txt")) {
            for (int i = 0; i < longArray.length; i++) {
                if (maxi.get(i) < 0) {
                    writer.println(longArray[i] + ":" + "??");
                } else {
                    writer.println(longArray[i] + ":" + shortArray[maxi.get(i)]);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка записи в файл");
        }
    }

//    построение матрицы совпадений между словами
    public static int[][] getMatchMatrix(String[] values1, String[] values2) {
        String[] longArray;
        String[] shortArray;
        if (values1.length >= values2.length) {
            longArray = values1;
            shortArray = values2;
        } else {
            longArray = values2;
            shortArray = values1;
        }
        int[][] matrix = new int[longArray.length][shortArray.length];
        for (int i = 0; i < longArray.length; i++) {
            for (int j = 0; j < shortArray.length; j++) {
                matrix[i][j] = getMaximalSpellingMatch(longArray[i], shortArray[j]);
            }
        }
        return matrix;
    }

//    поиск количества совпадений между словами
    public static int getMaximalSpellingMatch(String line1, String line2) {
        String longLine;
        String shortLine;
        if (line1.length() < line2.length()) {
            longLine = line2;
            shortLine = line1;
        } else {
            longLine = line1;
            shortLine = line2;
        }
        int maximalSpellingMatch = 0;
        for (int longLineIndex = 0; longLineIndex < longLine.length(); longLineIndex++) {
            int maxMatch;
            for (int shortLineIndex = 0; shortLineIndex < shortLine.length(); shortLineIndex++) {
                if (longLine.charAt(longLineIndex) == shortLine.charAt(shortLineIndex)) {
                    maxMatch = searchMaxMatchInSubstring(shortLine, longLine, shortLineIndex, longLineIndex);
                    if (maxMatch > maximalSpellingMatch) {
                        maximalSpellingMatch = maxMatch;
                    }
                }
            }
        }
        return maximalSpellingMatch;
    }

//    при совпадении символов вызывается этот метод, который проверяет оставшуюся строку
    public static int searchMaxMatchInSubstring(String shortLine, String longLine, int shortLineIndex, int longLineIndex) {
        int maxMatch = 0;
        int maxIndex = Math.min(shortLine.length() - shortLineIndex, longLine.length() - longLineIndex);
        for (int k = 0; k < maxIndex; k++) {
            if (longLine.charAt(longLineIndex + k) == shortLine.charAt(shortLineIndex + k)) {
                maxMatch++;
            } else {
                break;
            }
        }
        return maxMatch;
    }

//    этот метод из матрицы возвращает массив максимальных совпадений
    public static List<Integer> maximumWordMatch(String[] values1, String[] values2) {
        int[][] matrix = getMatchMatrix(values1, values2);
        List<Integer> finalSheet = new ArrayList<>();
        for (int[] ints : matrix) {
            int max = ints[0];
            int index = 0;
            for (int j = 0; j < ints.length; j++) {
                if (max < ints[j]) {
                    max = ints[j];
                    index = j;
                }
            }
            finalSheet.add(index);
        }

        while (hasDuplicates(finalSheet)) {
            for (int i = 0; i < finalSheet.size(); i++) {
                for (int j = i + 1; j < finalSheet.size(); j++) {
                    if (finalSheet.get(i).equals(finalSheet.get(j))) {
                        if (matrix[i][finalSheet.get(i)] > matrix[j][finalSheet.get(j)]) {
                            matrixEditing(matrix, finalSheet, j);
                        } else {
                            matrixEditing(matrix, finalSheet, i);
                        }
                    }
                }
            }
        }
        return finalSheet;
    }

//    это метод нужен для редактировании матрицы (если два и более варинтов выбора)
    public static void matrixEditing(int[][] matrix, List<Integer> finalSheet, int j) {
        matrix[j][finalSheet.get(j)] = 0;
        int maxInLine = matrix[j][0];
        for (int k = 0; k < matrix[j].length; k++) {
            if (maxInLine < matrix[j][k]) {
                maxInLine = matrix[j][k];
                finalSheet.set(j, k);
            }
        }
        if (maxInLine == 0) {
            finalSheet.set(j, -1);
        }
    }

//    этот метод проверки массива максимальных пересечений
    public static boolean hasDuplicates(List<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).equals(list.get(j))) {
                    return true;
                }
            }
        }
        return false;
    }
}