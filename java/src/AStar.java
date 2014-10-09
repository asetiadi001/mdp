


import java.awt.Color;
import java.util.ArrayList;
import java.lang.Math;


public class AStar {


    // TODO Auto-generated method stub


    private static final int length = TwoDimGrid.NUM_COLS;
    private static final int breath = TwoDimGrid.NUM_ROWS;
    private static int[][] maze = new int[breath][length];
    private static Direction[][] directionCollection = new Direction[breath][length];
    private static int[][] count_check = new int[breath][length];
    private static int[][] gn = new int[breath][length];//what is gn?
    static int counter = 0;
    /*
      Legend: 0 -> unaccessible
                >0 -> accessible
                -1 -> obstacle
                -2 -> unaccessible
                -7 -> shortest path

      GUI: -1 -> wall /obstacle
              0 -> unaccessible
              1> -> empty
              -7 -> route
    */
    private static TwoDimGrid twoDimensionGrid = new TwoDimGrid();
    private static updatedMaze updatedMaze = new updatedMaze();
    private static int start_pt_row = TwoDimGrid.NUM_ROWS - 3;
    private static int start_pt_col = 2;
    private static int goal_pt_row = 2;
    private static int goal_pt_col = TwoDimGrid.NUM_COLS - 3;            // 14
    private static boolean firstgrid = true;
    private static int curr_row, curr_col;
    private static Direction face;

    public static String shortestPath = "SP ";

    public AStar(TwoDimGrid m, updatedMaze n) {
        this.twoDimensionGrid = m;
        this.updatedMaze = n;
        clonemap();
    }


    public AStar(TwoDimGrid m, updatedMaze n, int end_row, int end_col)                // for Termination function
    {
        this.twoDimensionGrid = m;
        this.updatedMaze = n;
        clonemap();
        goal_pt_row = end_row;
        goal_pt_col = end_col;
    }

    /*
     Clone explored map over to Astar algorithm
     */
    public static void clonemap() {
        String text;
        try {

            for (int i = 0; i < breath; i++) {
                for (int j = 0; j < length; j++) {
                    text = updatedMaze.labels[i][j].getText();
                    

                    if (i == 0 || i == breath - 1 || j == 0 || j == length - 1 || text == "1") {
                        maze[i][j] = -1;//Obstacle
                    } else if (text == "") {
                        maze[i][j] = -1;//??
                    } else if (text == "0") {
                        maze[i][j] = Integer.parseInt(text); //unaccessible
                    }

                }
            }

        } catch (Exception e)                    // in case label do not work
        {
            for (int i = 0; i < breath; i++) {
                for (int j = 0; j < length; j++) {

                    if (twoDimensionGrid.grid[i][j].getBackground() == Color.black) {
                        maze[i][j] = -1;
                    } else {
                        maze[i][j] = 0;
                    }

                }
            }
        }

        print();

    }


    public static void start_AStar() {

        int row, col;
        for (row = 0; row < breath - 1; row++) {
            for (col = 0; col < length - 1; col++) ;
            {
                gn[row][col] = 0;                    // fn declaration
                count_check[row][col] = 0;
            }
        }
        
        ArrayList<Integer> Al_row = new ArrayList<Integer>();
        ArrayList<Integer> Al_col = new ArrayList<Integer>();
        Al_row.add(start_pt_row);
        Al_col.add(start_pt_col);
        

        safety_grid();

        print();


        calculate(Al_row, Al_col);
        print1();
        print2();
        //safety_grid2();
        print();


        directioncheck();
        retreat(maze[curr_row][curr_col] + 1);            // trace back


        // turn back to be implemented


        if (face == Direction.South) {
            face = Direction.North;

        } else if (face == Direction.West) {
            face = Direction.East;
        }

        twoDimensionGrid.speeding(curr_row, curr_col);
        speedup(curr_row, curr_col);
    }


    public static void print1() {
        for (int i = 0; i < breath; i++) {
            for (int j = 0; j < length; j++) {
                if (j == length - 1) {
                    System.out.println(" " + gn[i][j]);
                } else {
                    if (gn[i][j] < 0 || gn[i][j] > 9) {
                        System.out.print(" " + gn[i][j]);
                    } else {
                        System.out.print("  " + gn[i][j]);
                    }
                }
            }
        }

        System.out.println("");
    }

    ;


    public static void print2() {
        for (int i = 0; i < breath; i++) {
            for (int j = 0; j < length; j++) {
                if (j == length - 1) {
                    System.out.format("%10s", directionCollection[i][j]);
                    System.out.println();
                    //System.out.println(" " + directionCollection[i][j]);
                } else {

                    //System.out.print("  " + directionCollection[i][j]);
                    System.out.format("%10s", directionCollection[i][j]);

                }
            }
        }

        System.out.println("");
    }

    ;


    private static int turncheck(int x, int y) {
        try {
            switch (directionCollection[curr_row + x][curr_col + y]) {
                case North:

                    if (x == 1 && y == 0) {
                        if (directionCollection[curr_row][curr_col] == Direction.South || directionCollection[curr_row][curr_col] == Direction.East || directionCollection[curr_row][curr_col] == Direction.West) {
                            directionCollection[curr_row][curr_col] = Direction.Special;
                        } else {
                            directionCollection[curr_row][curr_col] = Direction.North;
                        }


                        return (gn[curr_row + x][curr_col + y] + 1);                    // straight
                    } else {
                        if (directionCollection[curr_row][curr_col] == Direction.North) {
                            directionCollection[curr_row][curr_col] = Direction.Special;
                        } else {
                            if (x == 0 && y == -1) {

                                directionCollection[curr_row][curr_col] = Direction.East;
                            } else if (x == 0 && y == 1) {
                                directionCollection[curr_row][curr_col] = Direction.West;
                            }
                        }
                        return (gn[curr_row + x][curr_col + y] + 2);                    //turn
                    }


                case South:
                    if (x == -1 && y == 0) {

                        if (directionCollection[curr_row][curr_col] == Direction.North || directionCollection[curr_row][curr_col] == Direction.East || directionCollection[curr_row][curr_col] == Direction.West) {
                            directionCollection[curr_row][curr_col] = Direction.Special;
                        } else {
                            directionCollection[curr_row][curr_col] = Direction.South;
                        }


                        return (gn[curr_row + x][curr_col + y] + 1);                //straight
                    } else {

                        if (directionCollection[curr_row][curr_col] == Direction.South) {
                            directionCollection[curr_row][curr_col] = Direction.Special;
                        } else {
                            if (x == 0 && y == -1) {
                                directionCollection[curr_row][curr_col] = Direction.East;
                            } else if (x == 0 && y == 1) {
                                directionCollection[curr_row][curr_col] = Direction.West;
                            }
                        }
                        return (gn[curr_row + x][curr_col + y] + 2);                //turn
                    }

                case West:
                    if (x == 0 && y == 1) {
                        if (directionCollection[curr_row][curr_col] == Direction.South || directionCollection[curr_row][curr_col] == Direction.East || directionCollection[curr_row][curr_col] == Direction.North) {
                            directionCollection[curr_row][curr_col] = Direction.Special;
                        } else {
                            directionCollection[curr_row][curr_col] = Direction.West;
                        }

                        return (gn[curr_row + x][curr_col + y] + 1);                //straight
                    } else {
                        if (directionCollection[curr_row][curr_col] == Direction.West) {
                            directionCollection[curr_row][curr_col] = Direction.Special;
                        } else {
                            if (x == -1 && y == 0) {
                                directionCollection[curr_row][curr_col] = Direction.South;
                            } else if (x == 1 && y == 0) {
                                directionCollection[curr_row][curr_col] = Direction.North;
                            }
                        }
                        return (gn[curr_row + x][curr_col + y] + 2);                //turn
                    }

                case East:
                    if (x == 0 && y == -1) {
                        if (directionCollection[curr_row][curr_col] == Direction.South || directionCollection[curr_row][curr_col] == Direction.North || directionCollection[curr_row][curr_col] == Direction.West) {
                            directionCollection[curr_row][curr_col] = Direction.Special;
                        } else {
                            directionCollection[curr_row][curr_col] = Direction.East;
                        }
                        return (gn[curr_row + x][curr_col + y] + 1);                //straight
                    } else {
                        if (directionCollection[curr_row][curr_col] == Direction.East) {
                            directionCollection[curr_row][curr_col] = Direction.Special;
                        } else {
                            if (x == 1 && y == 0) {
                                directionCollection[curr_row][curr_col] = Direction.North;

                                //turn
                            } else if (x == -1 && y == 0) {
                                directionCollection[curr_row][curr_col] = Direction.South;
                            }
                        }
                        return (gn[curr_row + x][curr_col + y] + 2);
                    }


                case Special:
                    if (x == 0 && y == -1) {
                        directionCollection[curr_row][curr_col] = Direction.East;
                    } else if (x == 1 && y == 0) {
                        directionCollection[curr_row][curr_col] = Direction.North;
                    } else if (x == -1 && y == 0) {
                        directionCollection[curr_row][curr_col] = Direction.South;
                    } else if (x == 0 && y == 1) {
                        directionCollection[curr_row][curr_col] = Direction.West;
                    }
                    return (gn[curr_row + x][curr_col + y] + 1);

            }
        } catch (Exception e) {

        }
        return 3;
    }


    private static void safety_grid() {
        //check 3X3 grids
        for (int i = 1; i < breath - 1; i++) {
            for (int j = 1; j < length - 1; j++) {
                if (maze[i][j] == -1) {
                    for (int k = -1; k < 2; k = k + 2) {

                        if (maze[i + k][j] != -1) {
                            maze[i + k][j] = -2;
                        }
                        if (maze[i][j + k] != -1) {
                            maze[i][j + k] = -2;
                        }
                    }
                    if (maze[i - 1][j - 1] != -1) {
                        maze[i - 1][j - 1] = -2;
                    }
                    if (maze[i - 1][j + 1] != -1) {
                        maze[i - 1][j + 1] = -2;
                    }
                    if (maze[i + 1][j - 1] != -1) {
                        maze[i + 1][j - 1] = -2;
                    }
                    if (maze[i + 1][j + 1] != -1) {
                        maze[i + 1][j + 1] = -2;
                    }

                }
            }
        }

        for (int j = 1; j < length - 1; j++) {
            if (maze[1][j] != -1) {
                maze[1][j] = -2;
            }
            if (maze[breath - 2][j] != -1) {
                maze[breath - 2][j] = -2;
            }
        }

        for (int i = 1; i < breath - 1;i++) {
            if (maze[i][1] != -1) {
                maze[i][1] = -2;
            }
            if (maze[i][length - 2] != -1) {
                maze[i][length - 2] = -2;
            }
        }

//        for (int k = 3; k > 0; k--) {
//            if (maze[k][1] != -1) {
//                maze[k][1] = -2;
//            }
//
//            if (maze[breath - 2][length - 1 - k] != -1) {
//                maze[breath - 2][length - 1 - k] = -2;
//            }
//
//            if (maze[1][k] != -1) {
//                maze[1][k] = -2;
//            }
//
//            if (maze[breath - 1 - k][length - 2] != -1) {
//                maze[breath - 1 - k][length - 2] = -2;
//            }
//
//        }

    }

//    private static void safety_grid2() {
//        for (int i = 3; i > 0; i--) {
//            maze[i][1] = -2;
//            maze[1][i] = -2;
//            maze[breath - (i + 1)][length - 2] = -2;
//            maze[breath - 2][length - (i + 1)] = -2;
//
//
//        }
//
//    }


    private static void calculate(ArrayList<Integer> Al_row, ArrayList<Integer> Al_col) {
        int temp_fn;
        while (!Al_row.isEmpty()) {

            temp_fn = 100;
            curr_row = Al_row.get(0);
            curr_col = Al_col.get(0);
 //           print();

            if ((count_check[curr_row][curr_col] < 2))

            //	 if (maze[curr_row][curr_col]== 0)			// skip duplicate
            {
                if (squeeze()) {

                    for (int i = -1; i <= 1; i = i + 2) {

                        // System.out.println(fn[curr_row +i][curr_col] < temp_fn);

                        if (gn[curr_row + i][curr_col] > 0 && gn[curr_row + i][curr_col] <= temp_fn) {
                            temp_fn = turncheck(i, 0);
                        }

                        //System.out.println( fn[curr_row][curr_col+i] < temp_fn );

                        if (gn[curr_row][curr_col + i] > 0 && gn[curr_row][curr_col + i] <= temp_fn) {
                            temp_fn = turncheck(0, i);
                        }

                    }

                    if (firstgrid) {
                        firstgrid = false;
                        gn[curr_row][curr_col] = 1;
                        directionCollection[curr_row][curr_col] = Direction.North;
                    } else {
                        gn[curr_row][curr_col] = temp_fn;
                    }

                    if (maze[curr_row][curr_col] == 0) {
                        maze[curr_row][curr_col] = gn[curr_row][curr_col] + calculate_hn(curr_row, curr_col);
                    } else {
                        maze[curr_row][curr_col] = min(gn[curr_row][curr_col] + calculate_hn(curr_row, curr_col), maze[curr_row][curr_col]);
                    }

                    for (int i = -1; i <= 1; i = i + 2) {
                        if (maze[curr_row + i][curr_col] == 0 && count_check[curr_row + i][curr_col] < 2) {
                            Al_row.add(curr_row + i);
                            Al_col.add(curr_col);
                            //System.out.println(curr_row+i +"," + curr_col +" added");

                        }
                        if (maze[curr_row][curr_col + i] == 0 && count_check[curr_row][curr_col + i] < 2) {
                            Al_row.add(curr_row);
                            Al_col.add(curr_col + i);
                            //System.out.println(curr_row +"," + (curr_col+i) +" added");
                        }

                    }
                    count_check[curr_row][curr_col] += 1;
                }
            }

            Al_row.remove(0);
            Al_col.remove(0);

        }
    }


    private static boolean squeeze() {
        if (!firstgrid) {
            if (maze[curr_row][curr_col - 1] == -1 && maze[curr_row][curr_col + 1] == -1) // check left right <0 shows only 1 slot
            {
                maze[curr_row][curr_col] = -3;
                return false;

            } else if (maze[curr_row - 1][curr_col] == -1 && maze[curr_row + 1][curr_col] == -1)    //check up down <0 shows only 1 slot
            {
                maze[curr_row][curr_col] = -3;
                return false;
            }

            //

					/* else if (maze[curr_row - 1][curr_col+ 1]==-1 && maze[curr_row+ 1][curr_col -1]==-1)		// check diagonal< 0 (/) shows only 1 slot
					 {
						 maze[curr_row][curr_col] = -3;
						 return false;
					 }
					 else if (maze[curr_row-1][curr_col-1]==-1 && maze[curr_row +1][curr_col+1] ==-1)// check diagonal< 0 (\) shows only 1 slot
					 {
						 maze[curr_row][curr_col] = -3;
						 return false;
					 }*/
        }
        return true;
    }

    //calculate heuristic value
    private static int calculate_hn(int temp_row, int temp_col) {
        int hn = 0;

        hn = Math.abs(goal_pt_row - temp_row);
        hn += Math.abs(goal_pt_col - temp_col);

        return hn;
    }


    public static void print() {
        for (int i = 0; i < breath; i++) {
            for (int j = 0; j < length; j++) {
                if (j == length - 1) {
                    System.out.println(" " + maze[i][j]);
                } else {
                    if (maze[i][j] < 0 || maze[i][j] > 9) {
                        System.out.print(" " + maze[i][j]);
                    } else {
                        System.out.print("  " + maze[i][j]);
                    }
                }
            }
        }

        System.out.println("");
    }

    // sensor checking
    public static void directioncheck() {
					/* use of sensor to check direction
					 
					 This part will be modified again based on the sensor detections
					 
					*/


        curr_row = goal_pt_row;
        curr_col = goal_pt_col;                //center

        face = Direction.South;

        int temp0 = maze[curr_row][curr_col];
        int temp_N = maze[curr_row][curr_col];
        int temp_W = maze[curr_row][curr_col];
        int counter_a = 1;
        int counter_b = 1;

        while (true) {
            if (temp_N == temp0) {
                temp_N = maze[curr_row - counter][curr_col];
                break;
            }


            counter_a++;

        }

        while (true) {
            if (temp_W == temp0) {
                temp_W = maze[curr_row][curr_col - counter];
                break;
            }
            counter_b++;
        }

        if ((counter_a - counter_b) <= 2) {
            face = Direction.South;
        } else {
            if (temp_N < temp_W) {
                if (temp_N > 0) {
                    face = Direction.South;
                } else {
                    face = Direction.West;
                }
            } else if (temp_W < temp_N) {
                if (temp_W > 0) {
                    face = Direction.West;
                } else {
                    face = Direction.South;
                }
            } else {
                for (int i = 0; i < 8; i++) {
                    if (maze[curr_row][curr_col - i] == -2) {

                        face = Direction.South;
                        break;
                    } else if (maze[curr_row - i][curr_col] == -2) {
                        face = Direction.West;
                        break;
                    }

                }
            }
        }
        System.out.println(face);

    }


    public static void retreat(int cur_value) {
        int temp_value;
        if ((curr_row == start_pt_row && curr_col == start_pt_col))// stop recursive
        {
            route(face);
            print();

            System.out.println("Goal Reached");
            return;
        }

        switch (face) {

            case North:
                if (maze[curr_row][curr_col - 1] > -1 && maze[curr_row][curr_col - 1] <= cur_value)        //west
                {

                    route(Direction.West);
                    temp_value = maze[curr_row][curr_col - 1];
                    //print();
                    retreat(temp_value);
                } else if (maze[curr_row - 1][curr_col] > -1 && maze[curr_row - 1][curr_col] <= cur_value)            //north
                {
                    route(Direction.North);
                    temp_value = maze[curr_row - 1][curr_col];
                    curr_row -= 1;
                    // print();
                    retreat(temp_value);

                } else if (maze[curr_row][curr_col + 1] > -1 && maze[curr_row][curr_col + 1] <= cur_value)        // East
                {
                    route(Direction.East);
                    temp_value = maze[curr_row][curr_col + 1];
                    retreat(temp_value);
                } else if (maze[curr_row + 1][curr_col] > -1 && maze[curr_row + 1][curr_col] <= cur_value)            //  south (redundant)
                {


                    route(Direction.South);
                    temp_value = maze[curr_row + 1][curr_col];

                    retreat(temp_value);
                }

                break;


            case West:

                if (maze[curr_row][curr_col - 1] > -1 && maze[curr_row][curr_col - 1] <= cur_value)        //west
                {
                    route(Direction.West);
                    temp_value = maze[curr_row][curr_col - 1];
                    curr_col -= 1;
                    //	print();

                    retreat(temp_value);
                } else if (maze[curr_row + 1][curr_col] > -1 && maze[curr_row + 1][curr_col] <= cur_value)            //South
                {


                    route(Direction.South);
                    temp_value = maze[curr_row + 1][curr_col];
                    //print();
                    retreat(temp_value);

                }else if (maze[curr_row - 1][curr_col] > -1 && maze[curr_row - 1][curr_col] <= cur_value)            //north
                {
                    route(Direction.North);
                    temp_value = maze[curr_row - 1][curr_col];
                    //print();
                    retreat(temp_value);

                }
                break;
            case South:
                if (maze[curr_row + 1][curr_col] > -1 && maze[curr_row + 1][curr_col] <= cur_value)            //South
                {
                    route(Direction.South);
                    temp_value = maze[curr_row + 1][curr_col];
                    curr_row += 1;
                    //print();
                    retreat(temp_value);
                }   else if (maze[curr_row][curr_col - 1] > -1 && maze[curr_row][curr_col - 1] <= cur_value)   //west
                {
                    route(Direction.West);
                    temp_value = maze[curr_row][curr_col - 1];
                    //	print();

                    retreat(temp_value);
                }  else if (maze[curr_row][curr_col + 1] > -1 && maze[curr_row][curr_col + 1] <= cur_value)            //East							{

                {
                    route(Direction.East);
                    temp_value = maze[curr_row][curr_col + 1];
                    //print();
                    retreat(temp_value + 1);

                }

                break;
            case East:
                if (maze[curr_row + 1][curr_col] > -1 && maze[curr_row + 1][curr_col] <= cur_value)            //South
                {
                    route(Direction.South);
                    temp_value = maze[curr_row + 1][curr_col];
                    //print();
                    retreat(temp_value);

                } else if (maze[curr_row][curr_col + 1] > -1 && maze[curr_row][curr_col + 1] <= cur_value)            //East							{
                {
                    route(Direction.East);
                    temp_value = maze[curr_row][curr_col + 1];

                    //print();
                    curr_col += 1;
                    retreat(temp_value);

                } else if (maze[curr_row - 1][curr_col] > -1 && maze[curr_row - 1][curr_col] <= cur_value)        //north
                {
                    route(Direction.North);
                    temp_value = maze[curr_row - 1][curr_col];
                    //print();
                    retreat(temp_value);

                }

                break;


        }
    }


    private static int max(int a, int b) {
        if (a >= b) {
            return a;
        } else
            return b;
    }

    private static int min(int a, int b) {
        if (a <= b) {
            return a;
        } else
            return b;
    }


    public static void route(Direction nextdirection)            // plot the route back
    {
				 
				 /* format
				  send(direction - no.ofblk - speed)
				  forward - f				no.of blk - 00/ 01  		speed - 0 (slow) / 1 (fast)
				  left - l
				  right - r
				  */
				/* 
				 if(face == nextdirection)
				 {
					 	// insert command to RPI
					 	// move straight
					 //send("f010");
				 }
				 else if (nextdirection == Direction.North ) 
				 {
					 //insert command to RPI
					 //turn right
					 //send("r010");
					 face = Direction.North;
				 }
				 else if( nextdirection == Direction.West)
				 {
					 //insert command to RPI
					 // turn left
					 //send("l010");
					 face = Direction.West;
				 }
				 */
        face = nextdirection;

        maze[curr_row][curr_col] = -7;

        twoDimensionGrid.tracer(curr_row, curr_col);// plot the GUI
    }

    public static void speedup(int temp_row, int temp_col) {
        if ((temp_row == goal_pt_row && temp_col == goal_pt_col)) {
            direc(face);
            System.out.println(shortestPath);
            UDP.send(shortestPath);
            System.out.println("Race Ended");
            return;
        }

        if (maze[temp_row + 1][temp_col] == -7 && face == Direction.South)        // go straight south
        {
            counter++;
            speedup(temp_row + 1, temp_col);

        } else if (maze[temp_row][temp_col + 1] == -7 && face == Direction.East)        // go straight east
        {
            counter++;
            speedup(temp_row, temp_col + 1);
        } else if (maze[temp_row - 1][temp_col] == -7 && face == Direction.North)        // go straight North
        {
            counter++;
            speedup(temp_row - 1, temp_col);
        } else if (maze[temp_row][temp_col - 1] == -7 && face == Direction.West)        // go straight west
        {
            counter++;
            speedup(temp_row, temp_col - 1);
        } else {
            if (face == Direction.South) {

                if (counter > 9) {
                    //rpi.send("f"+counter+"1");
                } else {
                    //rpi.send("f0"+counter+"1");
                }

                direc(face);
                if (maze[temp_row][temp_col + 1] == -7) {
                    // rpi.send("l000");
                    face = Direction.East;
                    shortestPath = shortestPath.concat("L");

                } else if (maze[temp_row][temp_col - 1] == -7) {
                    // rpi.send("r000");
                    face = Direction.West;
                    shortestPath = shortestPath.concat("R");
                }
                counter = 0;
                speedup(temp_row, temp_col);


            } else if (face == Direction.East) {
                if (counter > 9) {
                    //rpi.send("f"+counter+"1");

                } else {
                    //rpi.send("f0"+counter+"1");
                }

                direc(face);
                if (maze[temp_row - 1][temp_col] == -7) {
                    face = Direction.North;
                    shortestPath = shortestPath.concat("L");
                    // rpi.send("l000");

                } else if (maze[temp_row + 1][temp_col] == -7) {
                    face = Direction.South;
                    shortestPath = shortestPath.concat("R");
                    // rpi.send("r000");
                }

                counter = 0;


                speedup(temp_row, temp_col);//, temp_row2+1,temp_col2);
            } else if (face == Direction.North) {
                if (counter > 9) {
                    //rpi.send("f"+counter+"1");
                } else {
                    //rpi.send("f0"+counter+"1");
                }
                // rpi.send("r000");	//turn right on the spot
                direc(face);
                if (maze[temp_row][temp_col - 1] == -7) {
                    // rpi.send("r000");
                    face = Direction.West;
                    shortestPath = shortestPath.concat("L");
                } else if (maze[temp_row][temp_col + 1] == -7) {
                    // rpi.send("l000");
                    face = Direction.East;
                    shortestPath = shortestPath.concat("R");
                }

                counter = 0;

                speedup(temp_row, temp_col);//, temp_row2+1,temp_col2);
            } else if (face == Direction.West) {
                if (counter > 9) {
                    //rpi.send("f"+counter+"1");


                } else {
                    //rpi.send("f0"+counter+"1");
                }
                // rpi.send("r000");	//turn right on the spot


                direc(face);
                if (maze[temp_row + 1][temp_col] == -7) {
                    // rpi.send("r000");
                    face = Direction.South;
                    shortestPath = shortestPath.concat("L");

                } else if (maze[temp_row - 1][temp_col] == -7) {
                    // rpi.send("l000");
                    face = Direction.North;
                    shortestPath = shortestPath.concat("R");
                }

                counter = 0;


                speedup(temp_row, temp_col);
            }


        }


    }

    private static void direc(Direction inp) {
        if (inp == Direction.East) {
            for (int i = 1; i <= counter; i++) {
                twoDimensionGrid.speeding(curr_row, curr_col + i);
                shortestPath = shortestPath.concat("F");
            }
            curr_col += counter;
        } else if (inp == Direction.South) {

            for (int i = 1; i <= counter; i++) {
                twoDimensionGrid.speeding(curr_row + i, curr_col);
                shortestPath = shortestPath.concat("F");
            }
            curr_row += counter;
        } else if (inp == Direction.North) {

            for (int i = 1; i <= counter; i++) {
                twoDimensionGrid.speeding(curr_row - i, curr_col);
                shortestPath = shortestPath.concat("F");
            }
            curr_row -= counter;
        } else if (inp == Direction.West) {

            for (int i = 1; i <= counter; i++) {
                twoDimensionGrid.speeding(curr_row, curr_col - i);
                shortestPath = shortestPath.concat("F");
            }
            curr_col -= counter;
        }
    }

    private enum Direction {
        North, South, East, West, Special;
    }


}

