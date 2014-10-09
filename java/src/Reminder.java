//package src;
import java.lang.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

/**
 * Simple demo that uses java.util.Timer to schedule a task 
 * to execute once 5 seconds have passed.
 */

public class Reminder  {
    public Timer timer;
    private static Thread solvingThread;
	private static Thread solvingThread1;
	private static simulator mazemaze;
	private static TwoDimGrid maze2;
	final TwoDimGrid componentsPanel = new TwoDimGrid();
	 final updatedMaze updated = new updatedMaze();
    public Reminder(int seconds) {
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds*1000);
        
	}

    class RemindTask extends TimerTask implements Runnable{
       @SuppressWarnings("deprecation")
		public void run() {
        	System.out.format("Time's up!%n");
        	timer.cancel();
        	/*AStar AS = new AStar(maze2);
            
            AS.start_AStar();*/
			mazemaze.solvingThread.stop();
			JOptionPane.showMessageDialog(null, "Time's Up!");
			//mazemaze.solvingThread1.stop();
        	/*Exploration1 solver = new Exploration1(componentsPanel);
			solvingThread = new Thread(solver);
			solvingThread1.stop();
			
			Exploration1 solver1 = new Exploration1(updated);
			solvingThread1 = new Thread(solver1);
			solvingThread1.stop();*/
			
            
             //Terminate the timer thread
        }
    }
    public boolean isRunning(){
    	return false;
    }
	
}