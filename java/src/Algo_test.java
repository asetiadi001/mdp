import java.math.BigInteger;
import java.util.Arrays;
import java.util.Stack;

/**
 * Created by mersin on 6/9/14.
 */

// Direction
// 4=west 1=south 2=east 3=north
// Turn left: (x+1 % 4)+1, Turn right: (x+3 mod 4)+1


public class Algo_test implements Runnable {
    protected static int SLEEP_TIME =210;//210
    private TwoDimGrid maze;
    private updatedMaze updateMaze;
    private simulator mazeSimulator;
    private int explored_percent;
    private static boolean reachLeftWall=false;
    private static boolean reachTopWall=false;
    private static int lastDirection = 0;
    private String robot="";
    private String sendingMsg="";
    Stack<Integer> stackX = new Stack<Integer>();
	Stack<Integer> stackY = new Stack<Integer>();
	Stack<Integer> stackD = new Stack<Integer>();

    public Algo_test(TwoDimGrid m, updatedMaze n){
        this.maze = m;
        this.updateMaze = n;
    }
   
    public Algo_test(TwoDimGrid m, updatedMaze n, int speed){
        this.maze = m;
        this.updateMaze = n;
        SLEEP_TIME=SLEEP_TIME/speed;
    }

    public void run(){

		System.out.println("Nothing wrong till now");
		UDP.buildSocket();
		System.out.println("built Socket in main");
		UDP.send("ready");
		System.out.println("wifi ready to use");

        try {
            findMazePath();
        } catch (InterruptedException e) {
            // Thread stops.
        }
    }

    public void findMazePath() throws InterruptedException {
        mazeTraverse0(updateMaze, maze, 4, 10, 9);
        
        // to return no path only when both two no path
    }
  
    //public void turnRight
    public boolean rightHandTraversed(TwoDimGrid maze2, int direction, int x, int y){
		switch (direction){
		case 3:
			for  (int i = 0; i <= TwoDimGrid.NUM_ROWS-3; i++){
				if (maze2. isWall(x+i,y+2))  break;
				if (maze2.gettraversedPath(x+i,y+2)) {
					System.out.println("get traversed 3 x y: "+(x+i) + " "+ (y+2));
					return true;
				}
			}
			break;
		case 2:
			for  (int i = 0; i <= TwoDimGrid.NUM_ROWS-3; i++){
				if (maze2. isWall(x+2,y-i))  break;
				if (maze2.gettraversedPath(x+2,y-i)) {
					System.out.println("get traversed 2 x y: "+(x+2) + " "+ (y-i));
					return true;
				}
			}
			break;
		case 1:
			for  (int i = 0; i <= TwoDimGrid.NUM_ROWS-3; i++){
				if (maze2. isWall(x-i,y-2))  break;
				if (maze2.gettraversedPath(x-i,y-2)) {
					System.out.println("get traversed 1 x y: "+(x-i) + " "+ (y-2));
					return true;
				}
			}
			break;
		case 4:
			for  (int i = 0; i <= TwoDimGrid.NUM_ROWS-3; i++){
				if (maze2. isWall(x-2,y+i))  break;
				if (maze2.gettraversedPath(x-2,y+i)) {
					System.out.println("get traversed 4 x y: "+(x-2) + " "+ (y+i));
					return true;
				}
			}
			break;
			
		}
			
		return false;
	}

    public void mazeTraverseTurn0(updatedMaze n, TwoDimGrid maze2, int direction,
                                 int x, int y) throws InterruptedException {
    	
        //UDP.send("     ");
        directionSend(direction);

        updateMap(n, maze2, direction, x, y);

        switch (direction){
            case 1:
                mazeTraverse0(n, maze, direction,x+1, y);
                break;
            case 3:
                mazeTraverse0(n, maze, direction,x-1, y);
                break;
            case 2:
                mazeTraverse0(n, maze, direction,x, y+1);
                break;
            case 4:
                mazeTraverse0(n, maze, direction,x, y-1);
                break;
        }
    }

	public void mazeTraverseTurn(updatedMaze n, TwoDimGrid maze2, int direction,
			int x, int y) throws InterruptedException {
		
		//UDP.send("     ");
        directionSend(direction);

        updateMap(n, maze2, direction, x, y);
		
		switch (direction){
		case 1:
			mazeTraverse(n, maze, direction,x+1, y);
			break;
		case 3:
			mazeTraverse(n, maze, direction,x-1, y);
			break;
		case 2:
			mazeTraverse(n, maze, direction,x, y+1);
			break;
		case 4:
			mazeTraverse(n, maze, direction,x, y-1);
			break;
		}
	}
	
	public void mazeTraverseTurn2(updatedMaze n, TwoDimGrid maze2, int direction,
			int x, int y) throws InterruptedException {
		
		//UDP.send("     ");
        directionSend(direction);

        updateMap(n, maze2, direction, x, y);
		
		switch (direction){
		case 1:
			mazeTraverse2(n, maze, direction,x+1, y);
			break;
		case 3:
			mazeTraverse2(n, maze, direction,x-1, y);
			break;
		case 2:
			mazeTraverse2(n, maze, direction,x, y+1);
			break;
		case 4:
			mazeTraverse2(n, maze, direction,x, y-1);
			break;
		}
	}

	public void reverseBack(TwoDimGrid maze2, int direction,
			int x, int y, int previousDirection, int previousX, int previousY) throws InterruptedException{
		//update the maze map
		

		//reverse the physical robot
		if (direction == previousDirection){
			maze2.updateCurrentPosition(direction, previousX, previousY);
			Thread.sleep(SLEEP_TIME);
			maze2.traversedPath(previousX, previousY);
//			Thread.sleep(SLEEP_TIME);
				//move the robot back
//			UDP.send("b010");
		}
		else {
			maze2.updateCurrentPosition(direction, previousX, previousY);
//			UDP.send("b010");
			Thread.sleep(SLEEP_TIME);
			
			maze2.updateCurrentPosition(previousDirection, previousX, previousY);
			Thread.sleep(SLEEP_TIME);
			maze2.traversedPath(previousX, previousY);
			//String[] directionArray = {"", "s", "e", "n","w"};
//			UDP.send(directionArray[previousDirection]+"000/");
			//System.out.println(directionArray[previousDirection]+"000/");    
		}
	}

    // From Center to the Leftest Part near the wall.
    public void mazeTraverse0(updatedMaze n, TwoDimGrid maze, int direction,
                              int row, int col) throws InterruptedException {
        directionSend(direction);

        updateMap(n, maze, direction, row, col);

        if (col <= 2)
            if (direction == 4 && maze.isWalkable(3,row,col))
                mazeTraverseTurn(n, maze, 3, row, col);
            else
                mazeTraverseTurn(n, maze, 2, row, col);
        else if (col <= 5)
            if (direction == 4 && !maze.isWalkable(4,row,col)) {
                if (maze.isWalkable(3,row,col))
                    mazeTraverseTurn(n, maze, 3, row, col);
                else
                    mazeTraverseTurn(n, maze, 2, row, col);
            }

        if (maze.gettraversedPath(1, TwoDimGrid.NUM_COLS-2)) {

            System.out.println("Test 1");
            maze.startPointColor(TwoDimGrid.NUM_ROWS-2, 1);
            mazeTraverse2(updateMaze, maze, 1, 2, TwoDimGrid.NUM_COLS-3);
            //printSolution(updatemaze);
            Thread.sleep(1000);
            return;
        }

        switch (direction) {
            case 1:
                if (maze.isWalkable(2, row, col))
                    mazeTraverseTurn0(n, maze, 2, row, col);
                else if (maze.isWalkable(1, row, col))
                    mazeTraverse0(n, maze, 1, row + 1, col);
                else if (maze.isWalkable(4, row, col))
                    mazeTraverseTurn0(n, maze, 4, row, col);
            case 2:
                if (maze.isWalkable(3, row, col))
                    mazeTraverseTurn0(n, maze, 3, row, col);
                else if (maze.isWalkable(2, row, col))
                    mazeTraverse0(n, maze, 2, row, col+1);
                else if (maze.isWalkable(1, row, col))
                    mazeTraverseTurn0(n, maze, 1, row, col);
                else
                    mazeTraverseTurn0(n, maze, 4, row, col);
            case 3:
                if (maze.isWalkable(4, row, col))
                    mazeTraverseTurn0(n, maze, 4, row, col);
                else if (maze.isWalkable(3, row, col))
                    mazeTraverse0(n, maze, 3, row - 1, col);
                else if (maze.isWalkable(2, row, col))
                    mazeTraverseTurn0(n, maze, 2, row, col);
                else
                    mazeTraverseTurn0(n, maze, 1, row, col);
                break;
            case 4:
                if (maze.isWalkable(4, row, col))
                    mazeTraverse0(n, maze, 4, row, col - 1);
                else if (maze.isWalkable(3, row, col) && !(maze.isTraversed(3, row, col)))
                    mazeTraverseTurn0(n, maze, 3, row, col);
                else if (maze.isWalkable(1, row, col))
                    mazeTraverseTurn0(n, maze, 1, row, col);
                else
                    mazeTraverseTurn0(n, maze, 2, row, col);
                break;
        }

    }

    public void mazeTraverse(updatedMaze n, TwoDimGrid maze2, int direction,
			int x, int y) throws InterruptedException {
		stackX.push(x);
		stackY.push(y);
		stackD.push(direction);


		//UDP.send("     ");
        directionSend(direction);

        updateMap(n, maze2, direction, x, y);
	
		// System.out.println(count);
		if (maze2.gettraversedPath(1, TwoDimGrid.NUM_COLS-2)) {

            System.out.println("Test 1");
			maze2.startPointColor(TwoDimGrid.NUM_ROWS-2, 1);
			mazeTraverse2(updateMaze, maze, 1, 2, TwoDimGrid.NUM_COLS-3);

			//printSolution(updatemaze);
			Thread.sleep(1000);
			// Astar algorithm
			return;
		}

		// traversedDirection[4] to mark whether traversed this direction
		Boolean[] traversedDirection = new Boolean[5]; // set the array together
														// with the cordinate
		Arrays.fill(traversedDirection, Boolean.FALSE);
		traversedDirection[(direction + 1) % 4 + 1] = true;// no need to go the
		// reverse direction
		
		
		// start recursion
		//mazeTraverse(n, maze2, 2, x, y - 1);
		//traversedDirection[3] = true;
		
        switch (direction) {
            case 1:
                if (maze2.isWalkable(2, x, y))
                    mazeTraverseTurn(n, maze2, 2, x, y);
                else if (maze2.isWalkable(1, x, y))
                    mazeTraverse(n, maze2, 1, x + 1, y);
                else if (maze2.isWalkable(4, x, y))
                    mazeTraverseTurn(n, maze2, 4, x, y);
                else
                    mazeTraverseTurn(n, maze2, 3, x, y);
                break;
            case 2:
                if (maze2.isWalkable(3, x, y))
                    mazeTraverseTurn(n, maze2, 3, x, y);
                else if (maze2.isWalkable(2, x, y))
                    mazeTraverse(n, maze2, 2, x, y + 1);
                else if (maze2.isWalkable(1, x, y))
                    mazeTraverseTurn(n, maze2, 1, x, y);
                else
                    mazeTraverseTurn(n, maze2, 4, x, y);
                break;
            case 3:
                if (maze2.isWalkable(4, x, y))
                    mazeTraverseTurn(n, maze2, 4, x, y);
                else if (maze2.isWalkable(3, x, y))
                    mazeTraverse(n, maze2, 3, x - 1, y);
                else if (maze2.isWalkable(2, x, y))
                    mazeTraverseTurn(n, maze2, 2, x, y);
                else
                    mazeTraverseTurn(n, maze2, 1, x, y);
                break;
            case 4:
                if (maze2.isWalkable(1, x, y))
                    mazeTraverseTurn(n, maze2, 1, x, y);
                else if (maze2.isWalkable(4, x, y))
                    mazeTraverse(n, maze2, 4, x, y - 1);
                else if (maze2.isWalkable(3, x, y))
                    mazeTraverseTurn(n, maze2, 3, x, y);
                else
                    mazeTraverseTurn(n, maze2, 2, x, y);
                break;
        }
		
		//check all direction see if traversed
		stackX.pop();
		stackY.pop();
		stackD.pop();
		reverseBack(maze2, direction, x, y, stackD.peek(), stackX.peek(), stackY.peek());

		
		// move back the robot position
		// mark it as traversed
		// remove from the StackArray

	}
	
	public void mazeTraverse2(updatedMaze n, TwoDimGrid maze2, int direction,
			int x, int y) throws InterruptedException {
		
		//UDP.send("     ");
        directionSend(direction);

		updateMap(n, maze2, direction, x, y);
	
		// System.out.println(count);
		if (maze2.gettraversedPath(TwoDimGrid.NUM_ROWS-2, 1)) {

			printSolution(updateMaze);
			Thread.sleep(2000);
            System.out.println("Test 2");
			// Astar algorithm
			AStar AS = new AStar(maze2,updateMaze);

			AS.start_AStar();
            simulator.solvingThread.stop();
			//mazeSimulator.solvingThread.stop();
			return;
		}

		// traversedDirection[4] to mark whether traversed this direction
		Boolean[] traversedDirection = new Boolean[5]; // set the array together
														// with the cordinate
		Arrays.fill(traversedDirection, Boolean.FALSE);
		traversedDirection[(direction + 1) % 4 + 1] = true;// no need to go the
		// reverse direction
		
		
		// start recursion
		//mazeTraverse(n, maze2, 2, x, y - 1);
		//traversedDirection[3] = true;
		
		
//		not handled right hand side yet
        switch (direction) {
        case 3:
            if (maze2.isWalkable(4, x, y))
                mazeTraverseTurn2(n, maze2, 4, x, y);
            else if (maze2.isWalkable(3, x, y))
                mazeTraverse2(n, maze2, 3, x - 1, y);
            else if (maze2.isWalkable(2, x, y))
                mazeTraverseTurn2(n, maze2, 2, x, y);
            else
                mazeTraverseTurn2(n, maze2, 1, x, y);
            break;
        case 4:
            if (maze2.isWalkable(1, x, y))
                mazeTraverseTurn2(n, maze2, 1, x, y);
            else if (maze2.isWalkable(4, x, y))
                mazeTraverse2(n, maze2, 4, x, y - 1);
            else if (maze2.isWalkable(3, x, y))
                mazeTraverseTurn2(n, maze2, 3, x, y);
            else
                mazeTraverseTurn2(n, maze2, 2, x, y);
            break;
        case 1:
            if (maze2.isWalkable(2, x, y))
                mazeTraverseTurn2(n, maze2, 2, x, y);
            else if (maze2.isWalkable(1, x, y))
                mazeTraverse2(n, maze2, 1, x + 1, y);
            else if (maze2.isWalkable(4, x, y))
                mazeTraverseTurn2(n, maze2, 4, x, y);
            else
                mazeTraverseTurn2(n, maze2, 3, x, y);
            break;
        case 2:
            if (maze2.isWalkable(3, x, y))
                mazeTraverseTurn2(n, maze2, 3, x, y);
            else if (maze2.isWalkable(2, x, y))
                mazeTraverse2(n, maze2, 2, x, y + 1);
            else if (maze2.isWalkable(1, x, y))
                mazeTraverseTurn2(n, maze2, 1, x, y);
            else
                mazeTraverseTurn2(n, maze2, 4, x, y);
            break;

        }
		
		//check all direction see if traversed
		
		
		// move back the robot position
		// mark it as traversed
		// remove from the StackArray

	}

    public void updateSensor(updatedMaze n, TwoDimGrid maze2, int direction, int x, int y) throws InterruptedException{
        int frontLeft, frontMiddle, frontRight, left, right;
        frontLeft = frontMiddle = frontRight = 2;
        left = 5;
        right = 5;

        //UDP.receive();

        //read from the arduino sensor signals
        front(n, maze2, direction, x, y, frontLeft,  frontMiddle, frontRight);
        leftSensor(n, maze2, direction, x, y, left);
        rightSensor(n, maze2, direction, x, y, right);
    }

    public static void front(updatedMaze n, TwoDimGrid maze2,
                             int direction, int x, int y, int frontLeft,  int frontMiddle, int frontRight) throws InterruptedException {
        if (direction == 1) {// south
            try {
                for (int i = 1; i < frontLeft; i++){// left sensor
                    if (!updateExplorationArea(n, maze2, x+1+i, y+1)) break;
                }

                for (int i = 1; i < frontMiddle; i++){// middle sensor
                    if (!updateExplorationArea(n, maze2, x+1+i, y)) break;
                }

                for (int i = 1; i < frontRight; i++) {// right sensor
                    if (!updateExplorationArea(n, maze2, x+1+i, y-1)) break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }

        if (direction == 3) {// north
            try {
                for (int i = 1; i < frontLeft; i++){// left sensor
                    if (!updateExplorationArea(n, maze2, x-1-i, y-1)) break;
                }

                for (int i = 1; i < frontMiddle; i++){// middle sensor
                    if (!updateExplorationArea(n, maze2, x-1-i, y)) break;
                }

                for (int i = 1; i < frontRight; i++) {// right sensor
                    if (!updateExplorationArea(n, maze2, x-1-i, y+1)) break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }

        if (direction == 2) {// east
            try {
                for (int i = 1; i < frontLeft; i++){// left sensor
                    if (!updateExplorationArea(n, maze2, x-1, y+1+i)) break;
                }

                for (int i = 1; i < frontMiddle; i++){// middle sensor
                    if (!updateExplorationArea(n, maze2, x, y+1+i)) break;
                }

                for (int i = 1; i < frontRight; i++) {// right sensor
                    if (!updateExplorationArea(n, maze2, x+1, y+1+i)) break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }

        if (direction == 4) {//west
            try {
                for (int i = 1; i < frontLeft; i++){// left sensor
                    if (!updateExplorationArea(n, maze2, x+1, y-1-i)) break;
                }

                for (int i = 1; i < frontMiddle; i++){// middle sensor
                    if (!updateExplorationArea(n, maze2, x, y-1-i)) break;
                }

                for (int i = 1; i < frontRight; i++) {// right sensor
                    if (!updateExplorationArea(n, maze2, x-1, y-1-i)) break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
    }

    public static void leftSensor(updatedMaze n,TwoDimGrid maze2, int direction, int x, int y, int left){
        if(direction==1){//south
            try{
                for (int i=1;i<left;i++)
                {
                    if (!updateExplorationArea(n, maze2, x, y-1-i)) break;
                }
            }
            catch(ArrayIndexOutOfBoundsException e){
                return;
            }}
        if(direction==3){//north
            try{
                for (int i=1;i<left;i++)
                {
                    if (!updateExplorationArea(n, maze2, x-1, y-1-i)) break;
                }
            }
            catch(ArrayIndexOutOfBoundsException e){
                return;
            }
        }
        if (direction==2){//east
            try{
                for (int i=1;i<left;i++)
                {
                    if (!updateExplorationArea(n, maze2, x-i-1, y+1)) break;
                }
            }
            catch(ArrayIndexOutOfBoundsException e){
                return;
            }
        }
        if (direction==4){//west
            try{
                for (int i=1;i<left;i++)
                {
                    if (!updateExplorationArea(n, maze2, x+i+1, y-1)) break;
                }
            }
            catch(ArrayIndexOutOfBoundsException e){
                return;
            }
        }
    }

    public static void rightSensor(updatedMaze n, TwoDimGrid maze2,
                                   int direction, int x, int y, int right) {
        if (direction == 1) {// south
            try {
//                for (int i = 1; i < right; i++) {// right sensor
//                    //Right Middle
//                    if (!updateExplorationArea(n, maze2, x, y-1-i)) break;
//                }

                //Right Front
                for (int i = 1; i < right; i++) {
                    if (!updateExplorationArea(n, maze2, x+1, y-1-i)) break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
        if (direction == 3) {// north
            try {
                //Right Middle
//                for (int i = 1; i < right; i++) {
//                    if (!updateExplorationArea(n, maze2, x, y+1+i)) break;
//                }

                //Right Front
                for (int i = 1; i < right; i++){
                    if (!updateExplorationArea(n, maze2, x-1, y+1+i)) break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
        if (direction == 2) {// east
            try {
                //Right Middle
//                for (int i = 1; i < right; i++) {
//                    if (!updateExplorationArea(n, maze2, x+i+1, y)) break;
//                }

                //Right Front
                for (int i = 1; i < right; i++){
                    if (!updateExplorationArea(n, maze2, x+i+1, y+1)) break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
        if (direction == 4) {// west
            try {
//                for (int i = 1; i < right; i++) {
//                    //Right Middle
//                    if (!updateExplorationArea(n, maze2, x-i-1, y)) break;
//                }

                //Right Front
                for (int i = 1; i < right; i++){
                    if (!updateExplorationArea(n, maze2, x-i-1, y-1)) break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
    }

    private static boolean updateExplorationArea(updatedMaze n, TwoDimGrid maze2,
                                                 int x, int y){
        if (maze2.isWall(x, y)) {
            n.isWall(x, y);
            return false;
        }

        if (!maze2.isWall(x, y)
                && !maze2.gettraversedPath(x, y)) {
            maze2.isSensed(x, y);
            n.isExplored(x, y);
            // printField(field);
        }
        return true;
    }

    public void printSolution(updatedMaze n){

        System.out.println("Maze explored");

        for (int i = TwoDimGrid.NUM_ROWS-2; i > 0; i--)
            for (int j = 1; j < TwoDimGrid.NUM_COLS-1; j++)
                n.checkUnExplored(i, j);

        //Thread.sleep(5000);
        String s = "11";
        for (int i = TwoDimGrid.NUM_ROWS-2; i > 0; i--)
            for (int j = 1; j < TwoDimGrid.NUM_COLS-1; j++)
                s = s.concat(n.getText(i, j));

        s = s.concat("11");
        System.out.println(s);
        BigInteger b = new BigInteger(s, 2);
        // System.out.println(b.toString(6));
        // BigInteger toHex=new BigInteger(s,10);
        String toHex = b.toString(16);
        // int foo = Integer.parseInt(s, 2);
        System.out.println("The value in Hex is: " + toHex);
        // System.out.println(Integer.toHexString(Integer.parseInt(s,6)));
        // System.exit(0);
        for (int i = TwoDimGrid.NUM_ROWS-2; i > 0; i--)
            for (int j = 1; j < TwoDimGrid.NUM_COLS-1; j++)
                if (n.getExplored(i, j) == true)
                    n.setZero(i, j);
                else
                    n.clearUnExplored(i, j);

        String h = "";
        for (int i = TwoDimGrid.NUM_ROWS-2; i > 0; i--)
            for (int j = 1; j < TwoDimGrid.NUM_COLS-1; j++)
                h = h.concat(n.getText(i, j));

        System.out.println(h);

        int pad = (8 - (h.length()%8)) % 8;
        for (int i=0; i<pad; i++)
            h = h.concat("0");

        int numOf0 = h.indexOf('1') / 4;

        BigInteger toHex1 = new BigInteger(h, 2);
        String toHex2 = toHex1.toString(16);
        for (int i=0; i<numOf0; i++)
            toHex2 = "0"+toHex2;
        System.out.println("The value in Hex is: " + toHex2);

        System.out.println("exploreEnds");
    }

    private void directionSend(int direction){
        if (lastDirection != 0){
            if (lastDirection == direction)
                System.out.println("Forward");
            else if ((lastDirection+1)%4 == direction%4)
                System.out.println("Left");
            else if ((lastDirection+3)%4 == direction%4)
                System.out.println("Right");
            else System.out.println("backward");
            UDP.sendDirection(lastDirection, direction);
        }
        lastDirection = direction;
    }

    private void updateMap(updatedMaze n, TwoDimGrid maze2, int direction,
                           int row, int col) throws InterruptedException {
    	//sending string via rpi to android
        sendingMsg = "";
    	robot=formatter(row, col, direction);
        sendingMsg = robot;
        frontDecoder("010", maze2, row, col, direction);
        leftDecoder("001", maze2, row, col, direction);
        rightDecoder("001", maze2, row, col, direction);

        UDP.send(sendingMsg);
    	System.out.println(sendingMsg);
    	// set current robot position 2*2 to yellow
        maze2.updateCurrentPosition(direction, row, col);
        n.updateCurrentPosition(row, col);// set the number grid
        Thread.sleep(SLEEP_TIME);

        updateSensor(n, maze2, direction, row, col);

        Thread.sleep(SLEEP_TIME);
        maze2.traversedPath(row, col);
    }
    private String formatter(int row, int col, int direction){

        String str="";
        if (row<10)
        	str= "2_0"+row;
        else
        	str= "2_"+row;
        if (col<10)
        	str=str+"0"+col;
        else str+=col;

        str=str+"_"+direction+"_";
        return str;

        }

    private String appendObstaclePos(String str1, int obstacleX, int obstacleY){

        String str=str1;
        if (obstacleX<10)
            str= str+"0"+obstacleX;
        else
            str= str+obstacleX;

        if (obstacleY<10)
            str=str+"0"+obstacleY;
        else str+=obstacleY;
        str+=" ";

        return str;
    }
        
    private void frontDecoder(String received, TwoDimGrid maze2, int robotX, int robotY, int direction){
        for (int i=-1;i<2;i++){
            int c=Integer.parseInt(received.substring(0, 1));
            received=received.substring(1);
            if (c==1){
                frontObstaclePositioning(maze2, robotX, robotY, direction, i);
            }
        }
    }

    private void frontObstaclePositioning(TwoDimGrid maze2, int robotX, int robotY, int direction, int offset){
        int obstacleX=0,obstacleY=0;
        switch (direction){
            case 1:
                obstacleX=robotX+2;
                obstacleY=robotY-offset;
                break;
            case 2:
                obstacleX=robotX+offset;
                obstacleY=robotY+2;
                break;
            case 3:
                obstacleX=robotX-2;
                obstacleY=robotY+offset;
                break;
            case 4:
                obstacleX=robotX-offset;
                obstacleY=robotY-2;
                break;
        }
        maze2.setWall(obstacleX,obstacleY);
        sendingMsg = appendObstaclePos(sendingMsg, obstacleX,  obstacleY);
    }

    private void rightDecoder(String received, TwoDimGrid maze2, int robotX, int robotY, int direction){
        for (int i=1;i<5;i++){
            int c=Integer.parseInt(received.substring(0, 1));
            received=received.substring(1);
            if (c==1){
                rightObstaclePositioning(maze2, robotX, robotY, direction, i);
                return;
            }
        }
    }

    private void rightObstaclePositioning(TwoDimGrid maze2, int robotX, int robotY, int direction, int offset){
        int obstacleX=0,obstacleY=0;
        switch (direction){
            case 1:
                obstacleX=robotX+1;
                obstacleY=robotY-1-offset;
                break;
            case 2:
                obstacleX=robotX+1+offset;
                obstacleY=robotY+1;
                break;
            case 3:
                obstacleX=robotX-1;
                obstacleY=robotY+1+offset;
                break;
            case 4:
                obstacleX=robotX-1-offset;
                obstacleY=robotY-1;
                break;
        }
        maze2.setWall(obstacleX,obstacleY);
        sendingMsg = appendObstaclePos(sendingMsg, obstacleX,  obstacleY);
    }

    private void leftDecoder(String received, TwoDimGrid maze2, int robotX, int robotY, int direction){
        for (int i=1;i<5;i++){
            int c=Integer.parseInt(received.substring(0, 1));
            received=received.substring(1);
            if (c==1){
                leftObstaclePositioning(maze2, robotX, robotY, direction, i);
                return;
            }
        }
    }

    private void leftObstaclePositioning(TwoDimGrid maze2, int robotX, int robotY, int direction, int offset){
        int obstacleX=0,obstacleY=0;
        switch (direction){
            case 1:
                obstacleX=robotX+1;
                obstacleY=robotY+1-offset;
                break;
            case 2:
                obstacleX=robotX-1+offset;
                obstacleY=robotY+1;
                break;
            case 3:
                obstacleX=robotX-1;
                obstacleY=robotY-1+offset;
                break;
            case 4:
                obstacleX=robotX+1-offset;
                obstacleY=robotY-1;
                break;
        }
        maze2.setWall(obstacleX,obstacleY);
        sendingMsg = appendObstaclePos(sendingMsg, obstacleX,  obstacleY);
    }
}
