import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException("Cannot have null Digraph");
        }
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || w < 0 || v >= G.V() || w >= G.V()) {
            throw new IllegalArgumentException("Cannot have out of range vertices");
        }
        BreadthFirstPaths bfpV = new BreadthFirstPaths();
        BreadthFirstPaths bfpW = new BreadthFirstPaths();
        bfpV.bfs(v); // for vertex v
        bfpW.bfs(w); // for vertex w

        int minSAP = G.V();
        int flag = 0;
        for (int i = 0; i < G.V(); i++) { // loop through all vertices
            if (bfpV.getMarked(i) && bfpW.getMarked(i)) {
                flag = 1;
                int currentDist = bfpV.getDistTo(i) + bfpW.getDistTo(i);
                // StdOut.print("Bla Bla");
                if (currentDist < minSAP) {
                    minSAP = currentDist;
                }
            }
        }
        if (flag == 1) {
            return minSAP;
        }
        else return -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || w < 0 || v >= G.V() || w >= G.V()) {
            throw new IllegalArgumentException("Cannot have out of range vertices");
        }
        BreadthFirstPaths bfpV = new BreadthFirstPaths();
        BreadthFirstPaths bfpW = new BreadthFirstPaths();
        bfpV.bfs(v); // for vertex v
        bfpW.bfs(w); // for vertex w

        int minAncestor = 0;
        int minSAP = G.V();
        int flag = 0;
        for (int i = 0; i < G.V(); i++) { // loop through all vertices
            if (bfpV.getMarked(i) && bfpW.getMarked(i)) {
                flag = 1;
                int currentDist = bfpV.getDistTo(i) + bfpW.getDistTo(i);
                if (currentDist < minSAP) {
                    minSAP = currentDist;
                    minAncestor = i;
                }
            }
        }
        if (flag == 1) {
            return minAncestor;
        }
        else return -1;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("Cannot have null item");
        }

        for (Integer i : v) {
            if (i < 0 || i >= G.V() || i == null) {
                throw new IllegalArgumentException("Cannot have vertex out of bound");
            }
        }
        for (Integer i : w) {
            if (i < 0 || i >= G.V() || i == null) {
                throw new IllegalArgumentException("Cannot have vertex out of bound");
            }
        }
        BreadthFirstPaths bfpV = new BreadthFirstPaths();
        BreadthFirstPaths bfpW = new BreadthFirstPaths();
        bfpV.bfsIter(v); // for vertex v
        bfpW.bfsIter(w); // for vertex w

        int minSAP = G.V();
        int flag = 0;
        for (int i = 0; i < G.V(); i++) { // loop through all vertices
            if (bfpV.getMarked(i) && bfpW.getMarked(i)) {
                flag = 1;
                int currentDist = bfpV.getDistTo(i) + bfpW.getDistTo(i);

                if (currentDist < minSAP) {
                    minSAP = currentDist;
                }
            }
        }
        if (flag == 1) {
            return minSAP;
        }
        else return -1;

    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException("Cannot have null item");
        }
        BreadthFirstPaths bfpV = new BreadthFirstPaths();
        BreadthFirstPaths bfpW = new BreadthFirstPaths();
        bfpV.bfsIter(v); // for vertex v
        bfpW.bfsIter(w); // for vertex w

        int minAncestor = 0;
        int minSAP = G.V();
        int flag = 0;
        for (int i = 0; i < G.V(); i++) { // loop through all vertices
            if (bfpV.getMarked(i) && bfpW.getMarked(i)) {
                flag = 1;
                int currentDist = bfpV.getDistTo(i) + bfpW.getDistTo(i);
                if (currentDist < minSAP) {
                    minSAP = currentDist;
                    minAncestor = i;
                    // StdOut.print("Bla Bla");
                }
            }
        }
        if (flag == 1) {
            return minAncestor;
        }
        else return -1;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }

    }

    private class BreadthFirstPaths {
        private boolean[] marked;
        private int[] edgeTo;
        private int[] distTo;

        public BreadthFirstPaths() {
            marked = new boolean[G.V()];
            for (int i = 0; i < marked.length; i++) {
                marked[i] = false;
            }
            edgeTo = new int[G.V()];
            distTo = new int[G.V()];
        }

        public int getEdgeTo(int v) {
            return edgeTo[v];
        }

        public int getDistTo(int v) {
            return distTo[v];
        }

        public boolean getMarked(int v) {
            return marked[v];
        }

        public void bfs(int s) {
            Queue<Integer> q = new Queue<Integer>();
            q.enqueue(s);
            marked[s] = true;
            distTo[s] = 0;
            while (!q.isEmpty()) {
                int v = q.dequeue();
                for (int w : G.adj(v)) {
                    if (!marked[w]) {
                        q.enqueue(w);
                        marked[w] = true;
                        edgeTo[w] = v;
                        distTo[w] = distTo[v] + 1;
                    }
                }
            }
        }

        public void bfsIter(Iterable<Integer> s) {
            Queue<Integer> q = new Queue<Integer>();
            for (int i : s) {
                if (i < 0) {
                    throw new IllegalArgumentException("Cannot have out of range vertices");
                }
                q.enqueue(i);
                marked[i] = true;
                distTo[i] = 0;
            }
            while (!q.isEmpty()) {
                int v = q.dequeue();
                for (int w : G.adj(v)) {
                    if (!marked[w]) {
                        q.enqueue(w);
                        marked[w] = true;
                        edgeTo[w] = v;
                        distTo[w] = distTo[v] + 1;
                    }
                }
            }
        }
    }
}
