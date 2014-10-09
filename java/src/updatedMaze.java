//package src;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class updatedMaze extends JPanel{
	private static final int NUM_ROWS = 22;
	private static final int NUM_COLS = 17;
	
	// Indicates a wall
	private static final Color WALL = Color.BLACK;
	
	// Indicates a cell that was searched but did not lead to a path
	private static final Color FAILED = new Color (182, 27, 32);
	
	// Indicates an unexplored area
	private static final Color UNEXPLORED = Color.LIGHT_GRAY;
	
	// Indicates a cell known to be on the path out
	private static final Color PATH = new Color(13, 156, 252);
	
	// Indicates a cell on a path that is being explored.
	private static final Color TENTATIVE = Color.YELLOW;
	
	// Indicates a cell that has been found but not yet explored
	//private static final Color FOUND = new Color(183, 252, 166);
	
	// The cells that make up the maze
	private JPanel[][] grid = new JPanel[NUM_ROWS][NUM_COLS];
	
	protected JLabel[][] labels = new JLabel[NUM_ROWS][NUM_COLS];
	
	// Random number generator to help decide where to put the walls.
	// setWall()
	private Random colorGen = new Random();
	
	private boolean showLabels = false;

public updatedMaze(){
	initGrid();
	updatedMap();
}
private void initGrid() {
	setLayout (new GridLayout (NUM_ROWS, NUM_COLS));
	for (int i = 0; i < NUM_ROWS; i++) {
		for (int j = 0; j < NUM_COLS; j++) {
			JPanel newCell = new JPanel();
			newCell.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
			grid[i][j] = newCell;
			labels[i][j] = new JLabel();
			grid[i][j].add(labels[i][j]);
			add(newCell);
		}
	}
}

public void updatedMap() {
	for (int i = 0; i < NUM_ROWS; i++) {
		for (int j = 0; j < NUM_COLS; j++) {
			grid[i][j].setBackground(UNEXPLORED);
			labels[i][j].setText("");
		}
	}
	for (int i = 0; i < NUM_COLS; i++) {
		grid[0][i].setBackground(WALL);
	}
	for (int i = 0; i < NUM_COLS; i++) {
		grid[NUM_ROWS-1][i].setBackground(WALL);
	}
	for (int i = 0; i < NUM_ROWS; i++) {
		grid[i][0].setBackground(WALL);
	}
	for (int i = 0; i < NUM_ROWS; i++) {
		grid[i][NUM_COLS-1].setBackground(WALL);
	}
	// Make sure starting and ending cells are visitable
	grid[20][1].setBackground(UNEXPLORED);
	grid[19][1].setBackground(UNEXPLORED);
	grid[18][1].setBackground(UNEXPLORED);
	grid[20][2].setBackground(UNEXPLORED);
	grid[19][2].setBackground(UNEXPLORED);
	grid[18][2].setBackground(UNEXPLORED);
	grid[20][3].setBackground(UNEXPLORED);
	grid[19][3].setBackground(UNEXPLORED);
	grid[18][3].setBackground(UNEXPLORED);
	grid[1][15].setBackground(UNEXPLORED);
	grid[2][15].setBackground(UNEXPLORED);
	grid[3][15].setBackground(UNEXPLORED);
	grid[1][14].setBackground(UNEXPLORED);
	grid[2][14].setBackground(UNEXPLORED);
	grid[3][14].setBackground(UNEXPLORED);
	grid[1][13].setBackground(UNEXPLORED);
	grid[2][13].setBackground(UNEXPLORED);
	grid[3][13].setBackground(UNEXPLORED);

	
}

public void setZero(int x, int y) {
	labels[x][y].setText("0");
	//grid[x][y].setBackground(WALL);
}
public void checkUnExplored(int x, int y) {
    if (grid[x][y].getBackground()==UNEXPLORED&&labels[x][y].getText()=="")
        labels[x][y].setText("0");

	//grid[x][y].setBackground(WALL);
}
public void clearUnExplored(int x, int y) {
    if (grid[x][y].getBackground()==UNEXPLORED&&labels[x][y].getText()=="0")
        labels[x][y].setText("");
	//grid[x][y].setBackground(WALL);
}
public boolean getExplored(int x, int y) {
	if(labels[x][y].getText()=="1" && grid[x][y].getBackground()==UNEXPLORED)
		return true;
	return false;
}

public void isExplored(int x, int y) {
	labels[x][y].setText("1");
}

public void updateCurrentPosition(int x, int y) {
	isExplored(x-1, y-1);
	isExplored(x, y-1);
	isExplored(x+1, y-1);
	isExplored(x-1, y);
	isExplored(x, y);
	isExplored(x+1, y);
	isExplored(x-1, y+1);
	isExplored(x, y+1);
	isExplored(x+1, y+1);
}

public void isWall(int x, int y) {
	labels[x][y].setText("1");
	grid[x][y].setBackground(WALL);
}

public String getText(int x, int y) {
	return labels[x][y].getText();
}

}
