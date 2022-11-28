
/* *****************************************************************************
 *  Describe concisely your algorithm to find a horizontal (or vertical)
 *  seam.
 **************************************************************************** */
Essentially, we reimplement the Bellman-Ford algorthim. We do this by maintaining
distTo[][] and edgeTo[][] int arrays, with the  distTo array initialized to
infinity. distTo keeps track of the energy path to each pixel, while edgeTo keeps
track of the parent pixel. We then loop through every pixel, "relaxing" every
edge and updating the distTo and edgeTo arrays to find the minimum energy path.
We then traverse the edgeTo array in reverse order and return the seam index
array.


/* *****************************************************************************
 *  Describe what makes an image suitable to the seam-carving approach
 *  (in terms of preserving the content and structure of the original
 *  image, without introducing visual artifacts). Describe an image that
 *  would not work well.
 **************************************************************************** */
Images that have a lot of empty background or "low energy" space would be suitable
for seam carving. Images that have strong geometric (circles, rectangles) figures
would not work well with seam carving. Seams removed near the edges of the shapes
can cause distortion. Also, images with poor lighting or when important subjects
are the same energy as the background can cause the main subject to be distorted.



/* *****************************************************************************
 *  Perform computational experiments to estimate the running time to reduce
 *  a W-by-H image by one column and one row (i.e., one call each to
 *  findVerticalSeam(), removeVerticalSeam(), findHorizontalSeam(), and
 *  removeHorizontalSeam()). Use a "doubling" hypothesis, where you
 *  successively increase either W or H by a constant multiplicative
 *  factor (not necessarily 2).
 *
 *  To do so, fill in the two tables below. Each table must have 5-10
 *  data points, ranging in time from around 0.25 seconds for the smallest
 *  data point to around 30 seconds for the largest one.
 **************************************************************************** */

(keep W constant)
 W = 2000
 multiplicative factor (for H) = 2

 H           time (seconds)      ratio       log ratio
------------------------------------------------------
1000        0.682
2000        1.38                 2.02           1.02
4000        2.849                2.06           1.04
8000        5.555                1.95           0.963
16000       11.203               2.02           1.02
32000       21.521               1.92           0.941
64000       41.411               1.92           0.941

(keep H constant)
 H = 2000
 multiplicative factor (for W) = 2

 W           time (seconds)      ratio       log ratio
------------------------------------------------------
1000        0.728
2000        2.01                2.76            1.46
4000        2.626               1.31            0.39
8000        5.687               2.17            1.12
16000       11.093              1.95            0.963
32000       22.252              2.01            1.01
64000       44.206              1.99            0.993



/* *****************************************************************************
 *  Using the empirical data from the above two tables, give a formula
 *  (using tilde notation) for the running time (in seconds) as a function
 *  of both W and H, such as
 *
 *       ~ 5.3*10^-8 * W^5.1 * H^1.5
 *
 *  Briefly explain how you determined the formula for the running time.
 *  Recall that with tilde notation, you include both the coefficient
 *  and exponents of the leading term (but not lower-order terms).
 *  Round each coefficient and exponent to two significant digits.
 **************************************************************************** */


Running time (in seconds) to find and remove one horizontal seam and one
vertical seam, as a function of both W and H:


    ~  7.5*10^-4 * W^1.0 * 1.2*10^-3 * H^0.94
     = ~ 9.3*10^-7 * W^1.0 * H^0.94




