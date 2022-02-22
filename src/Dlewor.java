import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import java.util.Scanner;

public class Dlewor {

    // constants to allow colored text and backgrounds
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    // compares target and attempt and stores if each letter matches
    public static int [] matchDelwor(String target, String attempt) {
        int [] match = new int [target.length()];
        for(int i = 0; i < attempt.length(); i++) {
            char charCheck = attempt.charAt(i);
            for (int j = 0; j < attempt.length(); j++) {
                if (target.charAt(j) == charCheck) {
                    if (j == i) {
                        match[i] = 2; // store 2 if char of attempt string is also found in target string
                        break;        // at the same index
                    } else {
                        match[i] = 1; // store 1 if char of attempt string is also found in target string
                    }                 // but at different index
                }
            }
            if ((match[i] != 2) && (match[i] != 1)) { // store 0 if char of attempt string is not found in
                match[i] = 0;                         // target string
            }
        }
        return match;
    }

    // prints attempted word based on match array
    // green = correct letter in correct location, yellow = correct letter in different location,
    // black = letter does not appear in word
    public static void printDelwor(String attempt, int [] match) {
        for(int i = 0; i < match.length; i++) {
            if(match[i] == 2) { // print green
                System.out.print(ANSI_GREEN_BACKGROUND + ANSI_BLACK + attempt.charAt(i) + ANSI_RESET);
            }
            else if (match[i] == 1) { // print yellow
                System.out.print(ANSI_YELLOW_BACKGROUND + ANSI_BLACK + attempt.charAt(i) + ANSI_RESET);
            }
            else { // print black
                System.out.print(ANSI_BLACK_BACKGROUND + ANSI_WHITE + attempt.charAt(i) + ANSI_RESET);
            }
        }
        System.out.println();
    }

    // returns boolean that indicates if match was found or not
    public static boolean foundMatch(int [] match) {
        boolean correctMatch = false;
        for (int i = 0; i < match.length; i++) {
            if(match[i] == 2) {
                correctMatch = true;
            }
            else {
                correctMatch = false;
                break; // if correctMatch != 2 at any point, loop breaks and false is returned
            }
        }
        return correctMatch;
    }

    // recursive method for finding word in dictionary for sorted ArrayList
    public static int binarySearch(ArrayList<String> list, int startIndex, int endIndex, String target) {
        int midIndex;
        int indexLocation;
        int searchRange = (endIndex - startIndex) + 1;

        midIndex = (startIndex + endIndex) / 2; // uses startIndex and endIndex to produce midIndex

        if (target.equals(list.get(midIndex))) { // if the value of target is equal the string at midIndex
            indexLocation = midIndex;            // return that index
        }
        else if (searchRange == 1) {  // returns -1 if endIndex equals startIndex to avoid infinite recursion
            indexLocation = -1;
        }
        else {
            if(target.compareTo(list.get(midIndex)) < 0) // if target is not equal, checks if target's string is
            {                                            // less than value at midIndex
                indexLocation = binarySearch(list, startIndex, midIndex, target); // if so, call binarySearch
            }                                                                     // for bottom half of list
            else // if target's string is greater than value at midIndex
            {    // calls binarySearch for top half of list
                indexLocation = binarySearch(list, midIndex + 1, endIndex, target);
            }
        }
        return indexLocation;

    }

    // recursive method for finding word in dictionary for random list
    public static int linearSearch(ArrayList<String> list, int startIndex, int endIndex, String target) {
        int indexLocation;
        int searchRange = (endIndex - startIndex) + 1;

        if (target.equals(list.get(startIndex))) { // checks if target is equal to the word at startIndex
            indexLocation = startIndex;
        }
        else if (target.equals(list.get(endIndex))) { // checks if target is equal to the word at endIndex
            indexLocation = endIndex;
        }
        else if(searchRange == 1) { // return -1 if endIndex equals startIndex to avoid infinite recursion
            indexLocation = -1;
        }
        else // if target is not equal to either index, calls linearSearch with startIndex incremented
        {    // by 1 and endIndex decremented by 1
            indexLocation = linearSearch(list, startIndex + 1, endIndex - 1, target);
        }
        return indexLocation;
    }

    public static void main(String[] args) {
        // read in command line argument, check for exceptions, exit if there is not exactly 1 argument
        String fileName = null;
        ArrayList<String>dictionaryList = new ArrayList<String>();
        FileInputStream inputFile = null;
        Scanner dictionaryFile = null;

        if (args.length == 1) {
            fileName = args[0];
            try {
                inputFile = new FileInputStream(fileName);
            }
            catch (FileNotFoundException e){
                System.err.println("Cannot open file.");
            }
        }
        else {
            System.err.println("Cannot read in more than one command line argument.");
            System.exit(-1);
        }

        // print welcome message
        System.out.println("Welcome to Dlewor(TM)");

        // read in dictionary file with one word per line
        // store the word into an ArrayList if the word is exactly 5 characters
        dictionaryFile = new Scanner(inputFile);
        while (dictionaryFile.hasNextLine()) {
            String word = dictionaryFile.nextLine();
            if(word.length() == 5) {
                dictionaryList.add(word);
            }
        }

        // use a random number generator to select one word from the ArrayList
        Random randGen = new Random();
        int wordLocation = randGen.nextInt(dictionaryList.size() - 1);
        String randomWord = dictionaryList.get(wordLocation);

        // ask user for 6 words and check if word exists in dictionary
        final int NUM_GUESSES = 6;
        boolean guess = false;
        int startingIndex = 0;
        int endingIndex = dictionaryList.size() - 1;
        int location;

        for (int i = 1; i < NUM_GUESSES + 1; i++) {
            System.out.print("Enter word (" + i + "): ");
            Scanner scnr = new Scanner(System.in);
            String userGuess = scnr.nextLine();
            boolean sorted = false;
            int[] determineMatch = new int[userGuess.length()];

            // check if userGuess exists in the dictionary
            if (!dictionaryList.contains(userGuess)) {
                System.out.println("The word \"" + userGuess + "\" is not a possible choice. Please try again.");
                i--;
                continue;
            }
            // checks if userGuess is in a sorted list and calls binarySearch if it is
            if (dictionaryList.contains(userGuess)) {
                for (int j = 0; j < dictionaryList.size() - 1; j++) {
                    if (dictionaryList.get(j).compareTo(dictionaryList.get(j + 1)) < 0) {
                        sorted = true;
                    } else {
                        sorted = false;
                        break;
                    }
                }
                if (sorted) { // if sorted call binarySearch
                    location = binarySearch(dictionaryList, startingIndex, endingIndex, randomWord);
                } else { // if random call linearSearch
                    location = linearSearch(dictionaryList, startingIndex, endingIndex, randomWord);
                }
                if (location != -1) {
                    determineMatch = matchDelwor(randomWord, userGuess);
                    printDelwor(userGuess, determineMatch);
                    guess = foundMatch(determineMatch);
                    if (guess) {
                        break;
                    }
                } else {
                    System.out.println("Invalid word.");
                    i--;
                    continue;
                }
            }
        }

        if (!guess) {
            System.out.println("Word not found in time. The word was " + randomWord);
        }
        else {
            System.out.println("You found the word!");
        }
    }
}
