import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.LinkedList;

public class WordNet {
    private final HashMap<String, LinkedList<Integer>> synsetMap;
    private String[] synsetArray;
    private final String synset;
    private final Digraph G;
    private final SAP diGraphSAP;
    private final String hypernym;
    private int synsetCount; // number of words in synsets.txt

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        synsetCount = 0;
        synsetMap
                = new HashMap<String, LinkedList<Integer>>(); // store key and value (key is the word and value is the linkedlist of synsets)
        synsetArray = new String[1];
        this.synset = synsets;
        this.hypernym = hypernyms;
        // synsetQueue = new Queue<String>();
        synsetsRead(); // read and store the whole synsets.txt file
        G = new Digraph(synsetCount); // to form the Digraph from hypernyms.txt
        hypernymsRead();
        diGraphSAP = new SAP(G);
    }

    private void synsetsRead() { // store the whole synsets.txt file into the String[]
        In in = new In(synset);
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] tokens = line.split(","); // split by ","
            // tokens[0] is the id and tokens[1] is the synset
            int synsetId = Integer.parseInt(tokens[0]);
            if (synsetCount == synsetArray.length) {
                synsetArray = resize(synsetArray, synsetArray.length * 2);
            }
            synsetArray[synsetId] = tokens[1]; // store the sysnet inside the synsetArray
            String[] tokensWord = tokens[1].split(" "); // split the synset into individual words
            // now i need to insert the all the words in the synset as key and the corresponding value is the synset id
            for (int i = 0; i < tokensWord.length; i++) {
                if (synsetMap.containsKey(tokensWord[i])) {
                    synsetMap.get(tokensWord[i]).push(synsetId);
                }
                else {
                    LinkedList<Integer> value = new LinkedList<Integer>();
                    value.push(synsetId);
                    synsetMap.put(tokensWord[i], value);
                }
            }
            synsetCount++;
        }
    }

    private String[] resize(String[] array, int capacity) {
        String[] copy = new String[capacity];
        for (int j = 0; j < array.length; j++) {
            copy[j] = array[j];
        }
        array = copy;
        return array;
    }

    private void hypernymsRead() {
        In in = new In(hypernym);
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] tokens = line.split(","); // the size of tokens is unknown
            int w = Integer.parseInt(tokens[0]);
            for (int i = 1; i < tokens.length; i++) {
                // in the form 34, 47569, 48084 where the 1st int is the id and the subsequent integers are the hypernyms
                int v = Integer.parseInt(tokens[i]);
                G.addEdge(w, v); // w -> v and v is the hypernym
            }
        }
        // check whether the graph is a rooted DAG
        // when all the vertices are not-rooted -> not rooted DAG -> throw exception
        int rootCount = 0;
        for (int i = 0; i < G.V(); i++) {
            if (G.indegree(i) == 0) { // not rooted if G.indegree(i) > 0
                rootCount += 1; // 1 root detected
            }
        }
        if (rootCount == 0) {
            throw new IllegalArgumentException("Cannot input not rooted DAG");
        }
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) { // use binary search
        if (word == null) {
            throw new IllegalArgumentException("Cannot have null input");
        }
        return synsetMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Cannot have null items");
        }
        if (isNoun(nounA) && isNoun(nounB)) {
            LinkedList<Integer> v = synsetMap
                    .get(nounA); // retrieve the set of synsets that nounA appear in
            LinkedList<Integer> w = synsetMap
                    .get(nounB); // retrieve the set of synsets that nounB appear in
            return diGraphSAP.length(v, w);
        }
        else throw new IllegalArgumentException("noun is not a WordNet word");
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null) {
            throw new IllegalArgumentException("Cannot have null items");
        }
        if (isNoun(nounA) && isNoun(nounB)) {
            LinkedList<Integer> v = synsetMap
                    .get(nounA); // retrieve the set of synsets that nounA appear in
            LinkedList<Integer> w = synsetMap
                    .get(nounB); // retrieve the set of synsets that nounB appear in
            int ancestor = diGraphSAP.ancestor(v, w);
            return synsetArray[ancestor];
        }
        else throw new IllegalArgumentException("noun is not a WordNet word");
    }

    // do unit testing of this class
    public static void main(String[] args) {
        // empty
    }
}
