import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;

public class SeamCarver {
    private Picture picture;
    private int width;
    private int height;
    private double[][] energy;
    // for now i will just do the slow way

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new IllegalArgumentException("Cannot input null picture");
        }
        this.picture = picture;
        this.width = picture.width();
        this.height = picture.height();
        this.energy = new double[width][height];
        // create the 2d array for energy
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                energy[col][row] = energy(col, row);
            }
        }
    }

    // current picture
    public Picture picture() {
        Picture currentPicture = new Picture(width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                currentPicture.set(col, row, picture.get(col, row));
            }
        }
        return currentPicture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > (width - 1) || y < 0 || y > (height - 1)) {
            throw new IllegalArgumentException("Cannot have values out of range");
        }
        double pixelEnergy;
        if (x == 0 || y == 0 || x == (width - 1) || y == (height - 1)) {
            pixelEnergy = 1000;
        }
        else {
            int rightRGB = picture.getRGB(x + 1, y);
            int leftRGB = picture.getRGB(x - 1, y);
            int topRGB = picture.getRGB(x, y - 1);
            int bottomRGB = picture.getRGB(x, y + 1);

            int Rx = ((rightRGB >> 16) & 0xFF) - ((leftRGB >> 16) & 0xFF);
            int Gx = ((rightRGB >> 8) & 0xFF) - ((leftRGB >> 8) & 0xFF);
            int Bx = ((rightRGB >> 0) & 0xFF) - ((leftRGB >> 0) & 0xFF);

            int deltaSquareX = Rx * Rx + Gx * Gx + Bx * Bx;

            int Ry = ((bottomRGB >> 16) & 0xFF) - ((topRGB >> 16) & 0xFF);
            int Gy = ((bottomRGB >> 8) & 0xFF) - ((topRGB >> 8) & 0xFF);
            int By = ((bottomRGB >> 0) & 0xFF) - ((topRGB >> 0) & 0xFF);

            int deltaSquareY = Ry * Ry + Gy * Gy + By * By;

            pixelEnergy = Math.sqrt(deltaSquareX + deltaSquareY);
        }

        return pixelEnergy;
    }

    private class Vertex {
        private int row;
        private int col;
        private double nodeEnergy;

        public Vertex(int col, int row, double energy) {
            this.col = col;
            this.row = row;
            this.nodeEnergy = energy;
        }

        public void setEnergy(double energy) {
            this.nodeEnergy = energy;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }

    private class Topological {
        private boolean[][] marked;
        private final Stack<Vertex> reversePost;

        public Topological(int direction) { // set horizontal to be 1 and vertical to be 0
            reversePost = new Stack<Vertex>();
            marked = new boolean[width][height];
            for (int col = 0; col < width; col++) {
                for (int row = 0; row < height; row++) {
                    if (direction == 1 && !marked[col][row]) {
                        dfsH(col, row);
                    }
                    else if (!marked[col][row]) {
                        dfsV(col, row);
                    }
                }
            }
        }

        private void dfsH(int col, int row) {
            marked[col][row] = true;
            if (row <= height - 1 && col <= width - 2) {
                int a = row - 1;
                int b = row + 2;
                if (a < 0) a = a + 1;
                if (b > height - 1) b = b - 1;
                // r is the row index
                // loop through the adjacent pixels
                for (int r = a; r < b; r++) {
                    if (!marked[col + 1][r]) dfsH(col + 1, r);
                }
                Vertex v = new Vertex(col, row, energy[col][row]);
                reversePost.push(v); // store the row index
            }

        }

        private void dfsV(int col, int row) {
            marked[col][row] = true;
            if (row <= height - 2 && col <= width - 1) {
                int a = col - 1;
                int b = col + 2;
                if (a < 0) a = a + 1;
                if (b > width - 1) b = b - 1;
                // c is the col index
                // loop through the adjacent pixels
                for (int c = a; c < b; c++) {
                    if (!marked[c][row + 1]) dfsV(c, row + 1);
                }
                Vertex v = new Vertex(col, row, energy[col][row]);
                reversePost.push(v); // store the col index
            }
        }

        public Iterable<Vertex> reversePost() {
            return reversePost;
        }
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // since the border pixels' energies are all 1000, i will start at the inner pixel and add in the border later
        int[] horizontalSeam = new int[width];
        PictureSP horizontalDir = new PictureSP(1);
        double minEnergy = Double.POSITIVE_INFINITY;
        int minRow = 0;
        for (int row = 0; row < height; row++) {
            double currentEnergy = horizontalDir.getDistTo(width - 1, row);
            if (minEnergy > currentEnergy) {
                minEnergy = currentEnergy;
                minRow = row;
            }
        }
        Vertex v = new Vertex(width - 1, minRow, energy[width - 1][minRow]);
        int count = width - 1;
        for (Vertex i = v; count >= 0; i = horizontalDir.getVertexTo(i)) {
            horizontalSeam[count] = i.getRow();
            count--;
        }
        return horizontalSeam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] verticalSeam = new int[height];
        PictureSP verticalDir = new PictureSP(0);
        double minEnergy = Double.POSITIVE_INFINITY;
        int minCol = 0;
        for (int col = 0; col < width; col++) {
            double currentEnergy = verticalDir.getDistTo(col, height - 1);
            if (minEnergy > currentEnergy) {
                minEnergy = currentEnergy;
                minCol = col;
            }
        }
        Vertex v = new Vertex(minCol, height - 1, energy[minCol][height - 1]);
        int count = height - 1;
        for (Vertex i = v; count >= 0; i = verticalDir.getVertexTo(i)) {
            verticalSeam[count] = i.getCol();
            count--;
        }
        return verticalSeam;
    }

    private class PictureSP {
        private Vertex[][] vertexTo;
        private double[][] distTo;

        public PictureSP(int direction) {
            vertexTo = new Vertex[width][height]; // all the vertices
            distTo = new double[width][height];

            for (int col = 0; col < width; col++) {
                for (int row = 0; row < height; row++) {
                    distTo[col][row] = Double.POSITIVE_INFINITY;
                }
            }
            if (direction == 1) {
                // horizontal
                for (int row = 0; row < height; row++) {
                    distTo[0][row] = energy[0][row];
                }
            }
            else { // vertical
                for (int col = 0; col < width; col++) {
                    distTo[col][0] = energy[col][0];
                }
            }

            Topological topological = new Topological(direction);
            for (Vertex v : topological.reversePost()) {
                if (direction == 1) {
                    // for check
                    int a = v.getRow() - 1;
                    int b = v.getRow() + 2;
                    if (a < 0) a = a + 1;
                    if (b > height - 1) b = b - 1; // for check
                    for (int r = a; r < b; r++) {
                        relax(v, v.getCol() + 1, r);
                    }

                }
                else {
                    int a = v.getCol() - 1;
                    int b = v.getCol() + 2;
                    if (a < 0) a = a + 1;
                    if (b > width - 1) b = b - 1; // for check
                    for (int c = a; c < b; c++) {
                        relax(v, c, v.getRow() + 1);
                    }
                }
            }
        }

        private void relax(Vertex v, int col, int row) { // need to store the initial Vertex v
            int iniCol = v.getCol();
            int iniRow = v.getRow();
            if (distTo[col][row] > distTo[iniCol][iniRow] + energy[col][row]) {
                distTo[col][row] = distTo[iniCol][iniRow] + energy[col][row];
                vertexTo[col][row] = v;
            }
        }

        public double getDistTo(int col, int row) {
            return distTo[col][row];
        }

        public Vertex getVertexTo(Vertex v) {
            int col = v.getCol();
            int row = v.getRow();
            return vertexTo[col][row];
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        // the height and picture attributes must be updated after Hseam removal
        // create a new picture
        if (seam == null) {
            throw new IllegalArgumentException("Cannot input null seam");
        }
        if (seam.length < width || seam.length > width) {
            throw new IllegalArgumentException("Cannot input invalid seam");
        }
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] > height - 1) {
                throw new IllegalArgumentException("Cannot input negative values");
            }
        }
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("Cannot input invalid seam");
            }
        }
        int oldHeight = height;
        height = height - 1; // after one Hseam removal
        Picture oldPicture = picture;
        Picture newPicture = new Picture(width, height);
        double[][] oldEnergy = energy;
        double[][] newEnergy = new double[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                // when the pixel to be removed is not reached yet, copy the pixel from initial picture to newPicture
                if (row < seam[col]) {
                    newPicture.set(col, row, oldPicture.get(col, row));
                    newEnergy[col][row] = oldEnergy[col][row];
                }
                // shift the pixel from the initial picture up to fill the removed pixel
                else {
                    newPicture.set(col, row, oldPicture.get(col, row + 1));
                    newEnergy[col][row] = oldEnergy[col][row + 1];
                }
            }
        }
        // update the energies for the pixels that fill the seam removed
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] == 0) {
                newEnergy[i][seam[i]] = energy(i, seam[i]);
            }
            else if (seam[i] < oldHeight - 1) {
                newEnergy[i][seam[i] - 1] = energy(i, seam[i] - 1);
                newEnergy[i][seam[i]] = energy(i, seam[i]);
            }
            else newEnergy[i][seam[i] - 1] = energy(i, seam[i] - 1);
        }
        energy = newEnergy;
        picture = newPicture;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new IllegalArgumentException("Cannot input null array");
        }
        if (seam.length < height || seam.length > height) {
            throw new IllegalArgumentException("Cannot input invalid seam");
        }
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] > width - 1) {
                throw new IllegalArgumentException("Cannot input negative values");
            }
        }
        for (int i = 0; i < seam.length - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException("Cannot input invalid seam");
            }
        }
        // need to check for corner case
        // the width and picture attributes must be updated after Vseam removal
        int oldWidth = width;
        width = width - 1; // after one Vseam removal
        Picture oldPicture = picture;
        Picture newPicture = new Picture(width, height);
        double[][] newEnergy = new double[width][height];
        double[][] oldEnergy = energy;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (col < seam[row]) {
                    newPicture.set(col, row, oldPicture.get(col, row));
                    newEnergy[col][row] = oldEnergy[col][row];
                }
                // shift the pixcel from the initial picture backward to fill the removed pixel
                else {
                    newPicture.set(col, row, oldPicture.get(col + 1, row));
                    newEnergy[col][row] = oldEnergy[col + 1][row];
                }
            }
        }
        // recalculate the energy of the pixels that fill the seam
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] == 0) {
                newEnergy[seam[i]][i] = energy(seam[i], i);
            }
            else if (seam[i] < oldWidth - 1) {
                newEnergy[seam[i] - 1][i] = energy(seam[i] - 1, i);
                newEnergy[seam[i]][i] = energy(seam[i], i);
            }
            else newEnergy[seam[i] - 1][i] = energy(seam[i] - 1, i);

        }
        picture = newPicture;
        energy = newEnergy;
    }

    //  unit testing (optional)
    public static void main(String[] args) {
        // testing using other java files
    }
}




