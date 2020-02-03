package ca.concordia.operatingsystems.defectivelightbulbs;

import java.io.*;
import java.util.Scanner;
import java.util.Arrays;
import java.util.concurrent.locks.*;

public class App {
    /**
     * 
     */
    private final static Lock lightBulbLock = new ReentrantLock(true);
    /**
     * 
     */
    private final static Lock threadNumberLock = new ReentrantLock(true);

    public static String listOfDefectiveBulbs = "The Defective bulbs are in the following positions: ";
    public static int numberOfThreads = 1;

    /**
     * Determines if a light bulb is on or off.
     * 
     * @param inputArray array from which the light bulb values are read
     * @return false if the light bulb is off, otherwise true
     */
    public static boolean isLightOn(int[] inputArray) {
        for (int i = 0; i <= inputArray.length - 1; i++) {
            if (inputArray[i] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Recursively finds the defective light bulbs.
     * 
     * @param inputArray array from which the light bulb values are read
     * @param start      starting postion of the array to be queried
     * @param end        last position of the array to be queried
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
        System.out.println(listOfDefectiveBulbs);
        System.out.println("The number of threads created for this problem was: " + Integer.toString(numberOfThreads));
    }

    public static void main(String[] args) throws Exception {
        String filePath = "/Users/stephen/Documents/vs-code/java-projects/defective-lightbulbs/Input.txt";
        File file = new File(filePath);
        Scanner scanner = new Scanner(file);

        int length = scanner.nextInt();
        int[] input = new int[length];
        int i = 0;

        while (scanner.hasNextInt()) {
            input[i++] = scanner.nextInt();
        }

        scanner.close();

        // call recursive function
        // note: changing the second argument to 0 will give us bulb positions starting
        // at 0
        findDefective(input, 1, input.length);

        printResults();
    }
}