import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        char[] ascii256 = new char[256]; // last to store the char found
        for (char i = 0; i < ascii256.length; i++) {
            ascii256[i] = i; // initialize the array
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            // StdOut.println("reading char" + c);
            char value = 0;
            for (char i = 0; i < ascii256.length; i++) {
                if (c == ascii256[i]) {
                    value = i;
                    BinaryStdOut.write(value);
                    break;
                }
            }
            // shift all other char
            for (char j = value; j >= 1; j--) {
                ascii256[j] = ascii256[j - 1];
            }
            // move the just-read char to the front
            ascii256[0] = c;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        char[] ascii256 = new char[256]; // last to store the char found
        for (char i = 0; i < 256; i++) {
            ascii256[i] = i; // initialize the array
        }
        while (!BinaryStdIn.isEmpty()) {
            char charLocation = BinaryStdIn.readChar();
            char moveFront = ascii256[charLocation];
            for (char i = charLocation; i >= 1; i--) {
                ascii256[i] = ascii256[i - 1];
            }
            BinaryStdOut.write(moveFront);
            ascii256[0] = moveFront;

        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        String sign = args[0];
        if (sign.equals("-")) {
            MoveToFront.encode();
        }
        else if (sign.equals("+")) {
            MoveToFront.decode();
        }
        else throw new IllegalArgumentException("Invalid input");

    }

}
