import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wordnet;

    public Outcast(WordNet wordnet) { // constructor takes a WordNet object
        this.wordnet = wordnet;
    }

    public String outcast(String[] nouns) { // given an array of WordNet nouns, return an outcast
        int maxDist = 0;
        String maxDistNoun = nouns[0];
        // StdOut.print("array" + nouns.length);
        for (int i = 0; i < nouns.length; i++) {
            int currDist = 0;
            for (int j = 0; j < nouns.length; j++) {
                currDist += wordnet.distance(nouns[i], nouns[j]);
                // StdOut.println("currDist:" + currDist + " inDist: " + wordnet.distance(nouns[i], nouns[j]));
            }
            if (currDist > maxDist) {
                maxDist = currDist;
                maxDistNoun = nouns[i];
            }
        }
        return maxDistNoun;
    }

    public static void main(String[] args) { // see test client below
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            // print out the outcast noun from the outcast.txt file
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
