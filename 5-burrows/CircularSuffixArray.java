import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {
    private final int length;
    private final int[] next; // to store the index in the original suffix of ith sorted suffix
    private final String inputString;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new IllegalArgumentException("Cannot have null input");
        }
        length = s.length();
        inputString = s;
        CircularSuffix[] cirSufArray = new CircularSuffix[length];
        for (int i = 0; i < length; i++) {
            cirSufArray[i] = new CircularSuffix(i);
        }
        Arrays.sort(cirSufArray);
        next = new int[length];
        for (int i = 0; i < length; i++) {
            next[i] = cirSufArray[i].pointer;
        }
    }

    // nested class CircularSuffix
    private class CircularSuffix implements Comparable<CircularSuffix> {
        private final int pointer; // points to the 1st char in the circular suffix

        public CircularSuffix(int j) {
            pointer = j;
        }

        public int compareTo(CircularSuffix that) {
            int i = pointer;
            int j = that.pointer;
            int count = 0;
            while (count < length) {
                if (inputString.charAt(i) < inputString.charAt(j)) {
                    return -1;
                }
                else if (inputString.charAt(i) > inputString.charAt(j)) {
                    return 1;
                }
                // increase i and j if the char at 2 strings are equal at i and j
                i = (i + 1) % length;
                j = (j + 1) % length;
                count++;
            }
            return 0;
        }
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i < 0 || i >= length) {
            throw new IllegalArgumentException("Invalid input!");
        }
        else return next[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String s = StdIn.readString();
        CircularSuffixArray testArray = new CircularSuffixArray(s);
        for (int i = 0; i < testArray.length; i++) {
            StdOut.println(testArray.next[i]);
        }
    }

}
