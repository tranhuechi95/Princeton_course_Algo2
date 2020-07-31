import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;

public class BoggleSolver {
    private final Trie26<Integer> trie;
    private int m; // rows
    private int n; // cols
    private HashSet<String> words = new HashSet<String>(); // to store all the valid words
    private BoggleBoard mainBoard;

    public BoggleSolver(String[] dictionary) {
        trie = new Trie26<Integer>(); // default constructor
        for (int i = 0; i < dictionary.length; i++) {
            trie.put(dictionary[i], i); // store the word in dict into trie
        }
    }

    // implement 26way Trie
    private static class Trie26<Value> {
        private static final int R = 26; // A to Z
        private Node root = new Node();
        private Node currentNode = root; // initially

        private static class Node {
            private Object value;
            private Node[] next = new Node[R];
            private Node prev = null;
        }

        public void put(String key, Value val) {
            root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, Value val, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) { // take note
                x.value = val;
                return x;
            } // value is set at end of the word
            char c = key.charAt(d);
            x.next[c - 65] = put(x.next[c - 65], key, val, d + 1);
            return x;
        }

        public boolean contains(String key) {
            return get(key) != null;
        }

        public Value get(String key) {
            Node x = get(root, key, 0);
            if (x == null)
                return null;
            return (Value) x.value; // casting
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;
            char c = key.charAt(d);
            return get(x.next[c - 65], key, d + 1);
        }

        public boolean checkPrefix(char c) {
            return (currentNode.next[c - 65] != null);
        }

        public void updatePrefix(char c) {
            Node oldCurrent = currentNode;
            currentNode = currentNode.next[c - 65];
            currentNode.prev = oldCurrent;
        }

        public void undoPrefix() {
            currentNode = currentNode.prev;
        }

        public boolean checkWord() {
            return (currentNode.value != null);
        }
    }

    private boolean checkWord(String word) {
        return words.contains(word);
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Cannot have null input");
        }
        words.clear();
        this.m = board.rows();
        this.n = board.cols();
        this.mainBoard = board;
        // need to generate possible word using dfs
        // generate all the possible path from 1 letter to all other letters
        boolean[][] marked = new boolean[m][n];
        StringBuilder letterString = new StringBuilder();
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                dfs(r, c, marked, letterString); // enumerate all the possible paths
            }
        }
        return words;
    }

    /* My algo to check all the words
    To generate all the possible words using dfs
    1. Have two for loops to loop through all the letters in the board i.e Each letter will the the start of a dfs
    2. Inside the dfs
    2.a Marked the selected letter as true
    2.b Loop through all of its adjacent letters (all 4 directions)
    2.c Inside the loop, if !marked[][], recursively dfs at that letter
    2.d After every search is done, need to unmarked that letter, so the algo will continue to explore that letter in another path
    3. Checking part
    when the current path corresponds to a string that is not a prefix of any word in the dictionary,
    there is no need to expand the path further
    3.a whenever a next letter is consider, add the letter to the stack -> form the prefix
    3.b if the prefix lead to some words -> continue the dfs
    else continue with the for loop, skip that path
    before the unmark of the letter,
    3.c check from the stack whether the letters in the stack form the words (if no of element in stack is more than 3)
    3.d pop that letter from the queue
    3.e unmark that letter as in 2.d
    */

    /* all the neighbors
    top, down, left, right, dia-1, dia-2, dia-3, dia-4
    top: [r - 1][c]; down: [r + 1][c]; left: [r][c - 1]
    right: [r][c + 1]; dia-1: [r-1][c + 1]; dia-2: [r+1][c+1]
    dia-3: [r-1][c-1]; dia-4: [r+1][c-1]
    */

    private void dfs(int row, int col, boolean[][] marked, StringBuilder letterString) {
        char toAdd = mainBoard.getLetter(row, col);

        if (trie.checkPrefix(toAdd)) {
            trie.updatePrefix(toAdd);
            letterString.append(toAdd);
            if (toAdd == 'Q' && !trie.checkPrefix('U')) {
                trie.undoPrefix();
                letterString.deleteCharAt(letterString.length() - 1);
                return;
            }
            else if (toAdd != 'Q') {
                // do nothing
            }
            else { // both toAdd == 'Q' and checkPrefix('U')
                trie.updatePrefix('U');
                letterString.append('U');
            }
        }
        else
            return;

        if (letterString.length() >= 3 && trie.checkWord())
            words.add(letterString.toString());

        // loop through all the possible neighbor
        // dia-1, right and dia-2, top and bottom, dia-3, left and dia-4
        int[] dr = new int[] { -1, 0, 1, -1, 1, -1, 0, 1 };
        int[] dc = new int[] { 1, 1, 1, 0, 0, -1, -1, -1 };

        // mark so that children can never visit this cell again
        marked[row][col] = true;
        for (int i = 0; i < 8; i++) {
            int cR = row + dr[i];
            int cC = col + dc[i];
            if (cR < 0 || cC < 0 || cR >= m || cC >= n) {
                // do nothing
            }
            else {
                if (!marked[cR][cC])
                    dfs(cR, cC, marked, letterString);
            }
        }
        // unmark so that another path is found
        marked[row][col] = false;

        for (int i = 0; i < 1 + (toAdd == 'Q' ? 1 : 0); ++i) {
            trie.undoPrefix();
            letterString.deleteCharAt(letterString.length() - 1); // pop the last character
        }
    }

    public int scoreOf(String word) {
        // check whether the word is valid
        if (word == null) {
            throw new IllegalArgumentException("Cannot have null input");
        }
        if (!checkWord(word))
            return 0;
        int a = word.length();
        if (a == 3 || a == 4) return 1;
        else if (a == 5) return 2;
        else if (a == 6) return 3;
        else if (a == 7) return 5;
        else return 11;
    }

    public static void main(String[] args) { // unit test the code
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
