package ca.concordia.operatingsystems.defectivelightbulbs;

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.concurrent.locks.*;

public class App {
    /**
     * TODO add the variable description
     */
    private final static Lock lightBulbLock = new ReentrantLock(true);
    /**
     * TODO add the variable description
     */
    private final static Lock threadNumberLock = new ReentrantLock(true);

    public static String listOfDefectiveBulbs = "The Defective bulbs are in the following positions: ";
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

        // check to see if there is a defective bulb in array
        boolean lightOn = isLightOn(inputArray);

        // if there are no defective bulbs in this sub array, return
        if (lightOn == true) {
            return;
        }

        // if lenght = 1, we have found our defective bulb
        if (inputArray.length == 1) {
            // report found bulb, lock access to global
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

        // still multiple array entries, lets keep recurring
        // find pivot

        int pivot = (inputArray.length + 1) / 2;

        // now split array
        int[] leftArray = Arrays.copyOfRange(inputArray, 0, pivot);
        int[] rightArray = Arrays.copyOfRange(inputArray, pivot, inputArray.length);

        // define threads

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

        // increment thread counter, lock the global
        // note that we always create two threads at a time
        threadNumberLock.lock();
        try {
            numberOfThreads += 2;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadNumberLock.unlock();
        }

        // start both threads
        leftThread.start();
        rightThread.start();

        // try to join back finished threads
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

    public static int[] buildInputArray() throws FileNotFoundException {
        String filePath = "Input.txt";
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        int length = scanner.nextInt();
        int[] inputArray = new int[length];
        int i = 0;

        try {
            while (scanner.hasNextInt()) {
                inputArray[i++] = scanner.nextInt();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("The size of the array does not match the number of items");
            e.printStackTrace();
        }

        scanner.close();
        return inputArray;
    }

    public static void main(String[] args) {
        int startIndex = 1;

        try {
            int[] input = buildInputArray();
            findDefective(input, startIndex, input.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        printResults();
    }
}