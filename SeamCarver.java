import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {
    private Picture pic; // Picture saved
    private int width; // Width of picture
    private int height; // Height of picture
    private boolean isTransposed = false; // Is current picture transposed?


    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("Null Picture.");
        this.pic = new Picture(picture);
        this.width = pic.width();
        this.height = pic.height();
    }

    // Return array of energy
    private double[][] buildEnergy() {
        double[][] energy = new double[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                energy[row][col] = specificEnergy(col, row);

            }
        }
        return energy;
    }

    // Calculate the energy of ONE spot
    private double specificEnergy(int col, int row) {
        int right;
        int left;
        if (width <= 1) {
            right = col;
            left = col;
        }
        else if (col == 0) {
            right = col + 1;
            left = width - 1;
        }
        else if (col == width - 1) {
            right = 0;
            left = col - 1;
        }
        else {
            left = col - 1;
            right = col + 1;
        }
        int bottom;
        int top;
        if (height <= 1) {
            top = 0;
            bottom = 0;
        }
        else if (row == 0) {
            top = height - 1;
            bottom = row + 1;
        }
        else if (row == height - 1) {
            bottom = 0;
            top = row - 1;
        }
        else {
            top = row - 1;
            bottom = row + 1;
        }

        // RGB values to use
        int rgbxRight = pic.getRGB(right, row);
        int rgbxLeft = pic.getRGB(left, row);
        int rgbyTop = pic.getRGB(col, top);
        int rgbyBottom = pic.getRGB(col, bottom);

        // find rgb diff for x values
        int rx = getRed(rgbxRight) - getRed(rgbxLeft);
        int gx = getGreen(rgbxRight) - getGreen(rgbxLeft);
        int bx = getBlue(rgbxRight) - getBlue(rgbxLeft);
        int xgrad = (rx * rx) + (gx * gx) + (bx * bx);
        // could use private helper method to calculate x and y gradient

        // y value calculations
        int ry = getRed(rgbyTop) - getRed(rgbyBottom);
        int gy = getGreen(rgbyTop) - getGreen(rgbyBottom);
        int by = getBlue(rgbyTop) - getBlue(rgbyBottom);
        int ygrad = (ry * ry) + (gy * gy) + (by * by);
        return Math.sqrt(xgrad + ygrad);
    }

    // helper method to get red component
    private int getRed(int rgbValue) {
        return (rgbValue >> 16) & 0xFF;
    }

    // helper method to get green component
    private int getGreen(int rgbValue) {
        return (rgbValue >> 8) & 0xFF;
    }

    // helper method to get blue component
    private int getBlue(int rgbValue) {
        return (rgbValue >> 0) & 0xFF;
    }

    // current picture
    public Picture picture() {
        return new Picture(pic);
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
        if (x < 0 || x >= width() || y < 0 || y >= height())
            throw new IllegalArgumentException("Pixel outside of range.");
        return specificEnergy(x, y);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] seam = findVerticalSeam();
        transpose();
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energy = buildEnergy();
        double[][] distTo = new double[height][width];
        int[][] edgeTo = new int[height][width];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                distTo[row][col] = Double.POSITIVE_INFINITY;
                if (row == 0) distTo[row][col] = energy[row][col];
            }
        }

        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width; col++) {
                double v = distTo[row][col];
                if (col > 0) {
                    double w = distTo[row + 1][col - 1];
                    double wenergy = energy[row + 1][col - 1];
                    if (w > v + wenergy) {
                        distTo[row + 1][col - 1] = v + wenergy;
                        edgeTo[row + 1][col - 1] = col;
                    }
                }
                if (col + 1 < width) {
                    double w = distTo[row + 1][col + 1];
                    double wenergy = energy[row + 1][col + 1];
                    if (w > v + wenergy) {
                        distTo[row + 1][col + 1] = v + wenergy;
                        edgeTo[row + 1][col + 1] = col;
                    }
                }
                double w = distTo[row + 1][col];
                double wenergy = energy[row + 1][col];
                if (w > v + wenergy) {
                    distTo[row + 1][col] = v + wenergy;
                    edgeTo[row + 1][col] = col;
                }
            }
        }

        // Find column in bottom row with minimum distance
        double minDist = Double.POSITIVE_INFINITY;
        int minCol = 0;
        for (int col = 0; col < width; col++) {
            if (distTo[height - 1][col] < minDist) {
                minDist = distTo[height - 1][col];
                minCol = col;
            }
        }
        int[] seam = new int[height];
        for (int row = height - 1; row >= 0; row--) {
            seam[row] = minCol;
            minCol = edgeTo[row][minCol];
        }
        return seam;
    }


    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if ((seam == null) || height == 1 || seam.length != width())
            throw new IllegalArgumentException("Null Picture.");
        if (seam.length == 1) {
            if (seam[0] >= height || seam[0] < 0)
                throw new IllegalArgumentException("Invalid Seam.");
        }
        else {
            // loop through to see if seam is valid
            for (int i = 0; i < seam.length; i++) {
                if (seam[i] >= height || seam[i] < 0)
                    throw new IllegalArgumentException("Invalid Seam.");
                if ((i == 0) && (seam[i + 1] > (seam[i] + 1) || (seam[i + 1] < (seam[i]
                        - 1)))) // first element , check next
                    throw new IllegalArgumentException("Invalid Seam.");
                else if ((i == seam.length - 1) && (seam[i - 1] > (seam[i] + 1)
                        || (seam[i - 1] < (seam[i] - 1)))) {
                    throw new
                            IllegalArgumentException("Invalid Seam.");
                }
                // last element, check before
                else if (i != 0 && (i != seam.length - 1)) {
                    if (seam[i + 1] > (seam[i] + 1) || seam[i + 1] < (seam[i] - 1) || (
                            seam[i - 1] > (
                                    seam[i] + 1) || (seam[i - 1] < (seam[i] - 1))))
                        throw new IllegalArgumentException("Invalid Seam.");
                }
            }
        }
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if ((seam == null) || width == 1 || seam.length != height())
            throw new IllegalArgumentException("Null Picture.");
        if (seam.length == 1) {
            if (seam[0] >= width || seam[0] < 0)
                throw new IllegalArgumentException("Invalid Seam.");
        }
        else {
            // loop through to see if seam is valid
            for (int i = 0; i < seam.length; i++) {
                if (seam[i] >= width || seam[i] < 0)
                    throw new IllegalArgumentException("Invalid Seam.");
                if ((i == 0) && (seam[i + 1] > (seam[i] + 1) || (seam[i + 1] < (seam[i]
                        - 1)))) // first element , check next
                    throw new IllegalArgumentException("Invalid Seam.");
                else if ((i == seam.length - 1) && (seam[i - 1] > (seam[i] + 1)
                        || (seam[i - 1] < (seam[i] - 1)))) {
                    throw new
                            IllegalArgumentException("Invalid Seam.");
                }
                // last element, check before
                else if (i != 0 && (i != seam.length - 1)) {
                    if (seam[i + 1] > (seam[i] + 1) || seam[i + 1] < (seam[i] - 1) || (
                            seam[i - 1] > (
                                    seam[i] + 1) || (seam[i - 1] < (seam[i] - 1))))
                        throw new IllegalArgumentException("Invalid Seam.");
                }
            }
        }
        Picture p = new Picture(width - 1, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width - 1; col++) {
                if (col < seam[row]) p.setRGB(col, row, pic.getRGB(col, row));
                else p.setRGB(col, row, pic.getRGB(col + 1, row));
            }
        }
        pic = p;
        width--;
    }

    // helper method to transpose image
    private void transpose() {
        Picture temp = new Picture(height, width);
        for (int col = 0; col < pic.height(); col++) {
            for (int row = 0; row < pic.width(); row++) {
                temp.setRGB(col, row, pic.getRGB(row, col));
            }
        }
        int tmp = width();
        width = height();
        height = tmp;
        pic = temp;
        isTransposed = !isTransposed;
    }

    //  unit testing (required)
    public static void main(String[] args) {
        Picture p = new Picture("8x1.png");
        SeamCarver sc = new SeamCarver(p);
        int[] vertSeam = sc.findVerticalSeam();
        sc.removeVerticalSeam(vertSeam);
        StdOut.println("Height: " + sc.height());
        StdOut.println("Width: " + sc.width());
        double en = sc.energy(0, 0);
        StdOut.println("Energy of (0,0) " + en);
        int[] horiSeam = sc.findHorizontalSeam();
        sc.removeHorizontalSeam(horiSeam);
        sc.picture();

        // Picture picture = new Picture("square.png");
        // SeamCarver carver = new SeamCarver(picture);
        // int[] seam = carver.findHorizontalSeam();
        // carver.removeHorizontalSeam(new int[] { 0, 0, 1, 0, 1, 1, 1, 0 });
        // carver.picture();
        // carver.removeVerticalSeam(new int[] { 2 });
    }

}