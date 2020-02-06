package ca.concordia.operatingsystems.defectivelightbulbs;

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.concurrent.locks.*;

public class App {
    /**
     * Mutex used to lock the list of defective bulbs.
     */
    private final static Lock lightBulbLock = new ReentrantLock(true);
    /**
     * Mutex used to lock the total number of threads counter.
     */
    private final static Lock threadNumberLock = new ReentrantLock(true);

    public static String listOfDefectiveBulbs = "The defective bulbs are in the following positions: ";
    public static int numberOfThreads = 1;

    /**
     * Determine if a light bulb is on or off.
     * 
     * @param inputArray array from which the light bulb values are read
     * @return false if the light bulb is off, otherwise true
     */
    public static boolean isLightOn(int[] inputArray) {
        for (int i = 0; i < inputArray.length; i++) {
            if (inputArray[i] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Recursively find the defective light bulbs.
     * 
     * @param inputArray array from which the light bulb values are read
     * @param start      starting index of the array to be queried
     * @param end        last index of the array to be queried
     */
    public static void findDefective(int[] inputArray, int start, int end) {
        boolean lightOn = isLightOn(inputArray);

        if (lightOn == true) {
            return;
        }

        if (inputArray.length == 1) {
            lightBulbLock.lock();
            try {
                listOfDefectiveBulbs = listOfDefectiveBulbs + Integer.toString(start) + " ";
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lightBulbLock.unlock();
            }
            return;
        }

        int pivot = (inputArray.length + 1) / 2;
        int[] leftArray = Arrays.copyOfRange(inputArray, 0, pivot);
        int[] rightArray = Arrays.copyOfRange(inputArray, pivot, inputArray.length);

        Thread leftThread = new Thread(new Runnable() {

            @Override
            public void run() {
                findDefective(leftArray, start, start + pivot - 1);
            }
        });
        Thread rightThread = new Thread(new Runnable() {

            @Override
            public void run() {
                findDefective(rightArray, start + pivot, end);
            }
        });

        threadNumberLock.lock();
        try {
            numberOfThreads += 2;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadNumberLock.unlock();
        }

        leftThread.start();
        rightThread.start();

        try {
            leftThread.join();
            rightThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return;
    }

    /**
     * Print the list of defective bulbs and number of threads used to find them.
     */
    public static void printResults() {
        String message = "The number of threads created for this problem was: ";
        System.out.println(listOfDefectiveBulbs);
        System.out.println(message + Integer.toString(numberOfThreads));
    }

    /**
     * Build the input array from a text file length.
     * 
     * @return integer array populated with input text file values
     * @throws FileNotFoundException if input file not found
     */
    public static int[] buildInputArray(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        int[] inputArray = convertTextFileToIntegerArray(scanner);

        scanner.close();
        return inputArray;
    }

    /**
     * Convert the text file to an array of values, using the first value as the
     * array length.
     * 
     * @param scanner parses primitive types from text to integer
     * @return array of {@code int} primitive types
     */
    public static int[] convertTextFileToIntegerArray(Scanner scanner) {
        int length = scanner.nextInt();
        int[] inputArray = new int[length];
        int i = 0;

        try {
            while (scanner.hasNextInt()) {
                inputArray[i++] = scanner.nextInt();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("The array length is less than the number of input items");
            e.printStackTrace();
        }
        return inputArray;
    }

    // TODO handle values that are not 0 or 1
    // TODO handle index value larger than number of items in input text file
    
    public static void main(String[] args) {
        int startIndex = 1;
        String filePath = "Input.txt";

        try {
            int[] input = buildInputArray(filePath);
            findDefective(input, startIndex, input.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        printResults();
    }
}