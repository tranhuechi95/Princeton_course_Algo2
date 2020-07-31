import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String input = BinaryStdIn.readString();
        CircularSuffixArray cirSufArray = new CircularSuffixArray(input);
        for (int i = 0; i < cirSufArray.length(); i++) {
            if (cirSufArray.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < cirSufArray.length(); i++) {
            if (cirSufArray.index(i) == 0) {
                BinaryStdOut.write(input.charAt(input.length() - 1));
            }
            else {
                int toPrint = cirSufArray.index(i) - 1;
                BinaryStdOut.write(input.charAt(toPrint));
            }
        }
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        char[] t = BinaryStdIn.readString().toCharArray();
        char[] start = t.clone();
        char[] aux = new char[start.length];
        char[] count = new char[256 + 1];

        // key-indexed counting sort
        // 1. count the frequency
        for (int i = 0; i < start.length; i++) {
            count[start[i] + 1]++;
        }
        // 2. compute frequency cumulates
        for (int i = 0; i < count.length - 1; i++) {
            count[i + 1] += count[i];
        }
        // 3. Access cumulates using key as index to move items
        for (int i = 0; i < start.length; i++) {
            aux[count[start[i]]++] = start[i];
        }
        // 4. Copy back into original array
        for (int i = 0; i < start.length; i++) {
            start[i] = aux[i];
        }

        int[] next = new int[t.length];
        boolean[] flag = new boolean[t.length];
        Arrays.fill(flag, false);
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t.length; j++) {
                if (!flag[j] && start[i] == t[j]) {
                    next[i] = j;
                    flag[j] = true;
                    break;
                }
            }
        }
        // 1st char
        int nextChar = first;
        for (int i = 0; i < t.length; i++) {
            BinaryStdOut.write(start[nextChar]);
            nextChar = next[nextChar];
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String sign = args[0];
        if (sign.equals("-")) {
            BurrowsWheeler.transform();
        }
        else if (sign.equals("+")) {
            BurrowsWheeler.inverseTransform();
        }
        else throw new IllegalArgumentException("Invalid input!");
    }

}
