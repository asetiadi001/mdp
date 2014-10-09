//package ;

import javax.swing.*;

import java.awt.*;
import java.util.Random;
import java.util.Scanner;


public class TwoDimGrid extends JPanel {
    public static int explored_percent = 301;
    // Size of the maze
    public static final int NUM_ROWS = 22;
    public static final int NUM_COLS = 17;
    // for % solver
    public static int NUM_UNEXPLORED = 300;
    // Indicates a wall
    private static final Color WALL = Color.BLACK;
    private static final Color SENSED = Color.GREEN;
    // Indicates a cell that was searched but did not lead to a path
    private static final Color FAILED = new Color(182, 27, 32);

    // Indicates an unexplored area
    private static final Color UNEXPLORED = Color.LIGHT_GRAY;

    // Indicates a cell known to be on the path out
    private static final Color PATH = new Color(13, 156, 252);

    // Indicates a cell on a path that is being explored.
    private static final Color TENTATIVE = Color.YELLOW;

    private static final Color TRAVERSED = Color.BLUE;
    private static final Color SAFEMARGIN = Color.DARK_GRAY;

    // Indicates a cell that has been found but not yet explored
    //private static final Color FOUND = new Color(183, 252, 166);

    // The cells that make up the maze
    JPanel[][] grid = new JPanel[NUM_ROWS][NUM_COLS];

    protected JLabel[][] labels = new JLabel[NUM_ROWS][NUM_COLS];

    // Random number generator to help decide where to put the walls.
    // setWall()
    private Random colorGen = new Random();

    private boolean showLabels = false;

    /**
     * Creates and displays a new maze.
     */
    public TwoDimGrid() {
        initGrid();
        newMaze();
    }

//	public TwoDimGrid(TwoDimGrid original) {
//		initGrid();
//		copyMaze(original);
//	}

    private void initGrid() {
        setLayout(new GridLayout(NUM_ROWS, NUM_COLS));
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                ColorGridCell newCell = new ColorGridCell(i, j);
                newCell.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
                grid[i][j] = newCell;
                labels[i][j] = new JLabel();
                grid[i][j].add(labels[i][j]);
                add(newCell);

            }
        }
    }

    /**
     * Generates a new maze
     */

    public void newMaze() {
        for (int i = 0; i < NUM_COLS; i++) {
            grid[0][i].setBackground(WALL);
        }
        for (int i = 0; i < NUM_COLS; i++) {
            grid[NUM_ROWS - 1][i].setBackground(WALL);
        }
        for (int i = 0; i < NUM_ROWS; i++) {
            grid[i][0].setBackground(WALL);
        }
        for (int i = 0; i < NUM_ROWS; i++) {
            grid[i][NUM_COLS - 1].setBackground(WALL);
        }
    }

//	public void randomMaze() {
//		 int mazecounter = 9;
//	        clearmap();
//
//	        for (int i = 1; i < NUM_ROWS-1; i++) {
//	            for (int j = 1; j < NUM_COLS-1; j++) {
//	                if ( mazecounter != 0)
//	                {
//	                    if( getRandomColor(i,j) == WALL )
//	                    {
//	                        mazecounter --;
//	                    }
//
//	                    else if  (grid[i][j].getBackground() != WALL)
//	                    {
//	                        grid[i][j].setBackground(UNEXPLORED);
//	                    }
//	                }
//	                else
//	                {
//	                    grid[i][j].setBackground(UNEXPLORED);
//	                }
//	            }
//	        }
//
//
//	        // Make sure starting and ending cells are visitable
//	        grid[1][2].setBackground(UNEXPLORED);
//	        grid[1][1].setBackground(UNEXPLORED);
//	        grid[1][3].setBackground(UNEXPLORED);
//	        grid[2][2].setBackground(UNEXPLORED);
//	        grid[2][1].setBackground(UNEXPLORED);
//	        grid[2][3].setBackground(UNEXPLORED);
//	        grid[3][2].setBackground(UNEXPLORED);
//	        grid[3][1].setBackground(UNEXPLORED);
//	        grid[3][3].setBackground(UNEXPLORED);
//	        grid[15][20].setBackground(UNEXPLORED);
//	        grid[15][19].setBackground(UNEXPLORED);
//	        grid[15][18].setBackground(UNEXPLORED);
//	        grid[14][20].setBackground(UNEXPLORED);
//	        grid[14][19].setBackground(UNEXPLORED);
//	        grid[14][18].setBackground(UNEXPLORED);
//	        grid[13][20].setBackground(UNEXPLORED);
//	        grid[13][19].setBackground(UNEXPLORED);
//	        grid[13][18].setBackground(UNEXPLORED);
//
//
//	    }

    public void clearmap() {
        for (int i = 1; i < NUM_ROWS - 1; i++) {
            for (int j = 1; j < NUM_COLS - 1; j++) {

                grid[i][j].setBackground(UNEXPLORED);

            }
        }
    }


    public void copyMaze(TwoDimGrid original) {
        for (int i = 0; i < NUM_ROWS; i++) {
            for (int j = 0; j < NUM_COLS; j++) {
                grid[i][j].setBackground(original.grid[i][j].getBackground());
                labels[i][j].setText("");
            }
        }

        // Make sure starting and ending cells are visitable
        grid[1][1].setBackground(UNEXPLORED);
        grid[NUM_ROWS - 1][NUM_COLS - 1].setBackground(UNEXPLORED);

    }

    /**
     * Randomly select whether to put a wall or hallway
     *
     * @return
     */
    private Color getRandomColor(int row, int col) {
        // 70% of the time we select a hallway
        int next = colorGen.nextInt(30);
        if (next <= 28) {
            return UNEXPLORED;
        }

        int mazetype = colorGen.nextInt(5);
        int mazedirect = colorGen.nextInt(99);
        int border = colorGen.nextInt(1);
        if (mazedirect % 2 == 1) {

            for (int i = 0; i <= mazetype; i++) {
                try {
                    grid[row + i][col + border].setBackground(WALL);
                } catch (Exception e) {

                }
            }

        } else {

            for (int i = 0; i <= mazetype; i++) {
                try {
                    grid[row + border][col + i].setBackground(WALL);
                } catch (Exception e) {
                    continue;
                }
            }

        }

        return WALL;
    }


    public void setWall(int x, int y){
        grid[x][y].setBackground(WALL);
    }

    /**
     * @return number of rows in the maze
     */
    public int getNumRows() {
        return NUM_ROWS;
    }

    /**
     * @return number of columns in the maze
     */
    public int getNumCols() {
        return NUM_COLS;
    }

    /**
     * @param x row of maze
     * @param y column of maze
     * @return true if there is a wall at (row, column)
     */
    public void isSensed(int x, int y) {
        if (grid[x][y].getBackground() == UNEXPLORED) {
            if (NUM_UNEXPLORED >= (300 - explored_percent * 3))
                NUM_UNEXPLORED--;
            else new java.util.Scanner(System.in).next();
            //System.out.println("NUM_UNEXPLORED="+NUM_UNEXPLORED);
        }
        grid[x][y].setBackground(SENSED);

    }

    public boolean getSensed(int x, int y) {
        return grid[x][y].getBackground() == SENSED;
    }

    public boolean isWall(int x, int y) {
        if ((x > NUM_ROWS - 2) || (x < 1) || (y > NUM_COLS - 2) || (y < 1)) {
            return true;
        }
        return grid[x][y].getBackground() == WALL;
    }

    public boolean isWalkable(int direction, int x, int y) {
        switch (direction) {
            case 1:
                try {
                    return !isWall(x + 2, y - 1) && !isWall(x + 2, y + 1) && !isWall(x + 2, y);
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            case 2:
                try {
                    return !isWall(x - 1, y + 2) && !isWall(x, y + 2) && !isWall(x + 1, y + 2);
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            case 3:
                try {
                    return !isWall(x - 2, y - 1) && !isWall(x - 2, y) && !isWall(x - 2, y + 1);
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            case 4:
                try {
                    return !isWall(x - 1, y - 2) && !isWall(x, y - 2) && !isWall(x + 1, y - 2);
                } catch (ArrayIndexOutOfBoundsException e) {

                }
        }
        return true;
    }

    public boolean isTraversed(int direction, int x, int y) {
        switch (direction) {
            case 3:
                return ((grid[x - 2][y - 1].getBackground() == TRAVERSED) &&
                        (grid[x - 2][y].getBackground() == TRAVERSED) &&
                        (grid[x - 2][y + 1].getBackground() == TRAVERSED));

        }
        return false;
    }

    public boolean isUnexplored(int x, int y) {
        return grid[x][y].getBackground() == UNEXPLORED;
    }

    /**
     * @param x row of maze
     * @param y column of maze
     * @return true if the cell at (x, y) has already been explored
     */
    public boolean alreadyVisited(int x, int y) {
        return grid[x][y].getBackground() == PATH || grid[x][y].getBackground() == TENTATIVE
                || grid[x][y].getBackground() == FAILED /*|| grid[x][y].getBackground() == FOUND*/;
    }

    /**
     * Mark the cell at (x, y) as being on the solution path
     *
     * @param x row of maze
     * @param y column of maze
     */
    public void onPath(int x, int y) {
        if (grid[x][y].getBackground() == UNEXPLORED) {
            if (NUM_UNEXPLORED >= (300 - explored_percent * 3))
                NUM_UNEXPLORED--;
            else new java.util.Scanner(System.in).next();
            ;
            //System.out.println("NUM_UNEXPLORED="+NUM_UNEXPLORED);
        }
        grid[x][y].setBackground(PATH);
    }

    /**
     * Mark the cell at (x, y) as being under exploration
     *
     * @param x row of maze
     * @param y column of maze
     */

    public void explore(int x, int y) {
        if (grid[x][y].getBackground() == UNEXPLORED) {
            if (NUM_UNEXPLORED >= (300 - explored_percent * 3))
                NUM_UNEXPLORED--;
            else new java.util.Scanner(System.in).next();
            ;
            //System.out.println("NUM_UNEXPLORED="+NUM_UNEXPLORED);
        }
        grid[x][y].setBackground(TENTATIVE);
    }

    public void directionExplore(int x, int y) {
        grid[x][y].setBackground(Color.red);
    }

    public void updateCurrentPosition(int direction, int x, int y) {
        explore(x - 1, y - 1);
        explore(x, y - 1);
        explore(x + 1, y - 1);
        explore(x - 1, y);
        explore(x, y);
        explore(x + 1, y);
        explore(x - 1, y + 1);
        explore(x, y + 1);
        explore(x + 1, y + 1);
        switch (direction) {
            case 1:
                directionExplore(x + 1, y);
                break;
            case 2:
                directionExplore(x, y + 1);
                break;
            case 3:
                directionExplore(x - 1, y);
                break;
            case 4:
                directionExplore(x, y - 1);
                break;
        }
    }

    public void traversed(int x, int y) {
        if (grid[x][y].getBackground() == UNEXPLORED) {
            if (NUM_UNEXPLORED >= (300 - explored_percent * 3))
                NUM_UNEXPLORED--;
            else new java.util.Scanner(System.in).next();
            //System.out.println("NUM_UNEXPLORED="+NUM_UNEXPLORED);
        }
        grid[x][y].setBackground(TRAVERSED);
    }

    public void startPointColor(int x, int y) {
        grid[x][y].setBackground(Color.magenta);
    }

    public void traversedPath(int x, int y) {
        traversed(x - 1, y - 1);
        traversed(x, y - 1);
        traversed(x + 1, y - 1);
        traversed(x - 1, y);
        traversed(x, y);
        traversed(x + 1, y);
        traversed(x - 1, y + 1);
        traversed(x, y + 1);
        traversed(x + 1, y + 1);

    }

    public boolean gettraversedPath(int x, int y) {
        return grid[x][y].getBackground() == TRAVERSED;
    }

    public boolean isExplored(int x, int y) {
        return grid[x][y].getBackground() == TENTATIVE;
    }

    public void safeMargin(int x, int y) {
        if (grid[x][y].getBackground() == UNEXPLORED) {
            if (NUM_UNEXPLORED >= (300 - explored_percent * 3))
                NUM_UNEXPLORED--;
            else new java.util.Scanner(System.in).next();
            ;
            //System.out.println("NUM_UNEXPLORED="+NUM_UNEXPLORED);
        }
        grid[x][y].setBackground(SAFEMARGIN);
    }

    /**
     * Mark the cell at (x, y) as being a dead end
     *
     * @param x row of maze
     * @param y column of maze
     */

    public void deadEnd(int x, int y) {
        grid[x][y].setBackground(FAILED);
    }

    /**
     * Mark the cell at (x, y) as being found but not yet explored
     *
     * @param x row of the maze
     * @param y column of the maze
     */

    public void setLabel(int x, int y, String s) {
        if (showLabels) {
            labels[x][y].setText(s);
        }
    }

    public void showLabels(boolean show) {
        showLabels = show;
    }

    public boolean Sensed(int x, int y) {
        return grid[x][y].getBackground() == SENSED;
    }


    public void tracer(int x, int y)//, int x2,int y2) 	// trace back route
    {
        try {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    grid[x + i][y + j].setBackground(Color.cyan);
                }
            }
            //	grid[x2][y2].setBackground(Color.cyan);
            Thread.sleep(100);
        } catch (Exception e) {

        }
    }

    public void speeding(int x, int y)//, int x2,int y2) 	// trace back route
    {
        try {


            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    grid[x + i][y + j].setBackground(Color.orange);
                }
            }

            //	grid[x2][y2].setBackground(Color.orange);
            Thread.sleep(210);
        } catch (Exception e) {

        }
    }

}
