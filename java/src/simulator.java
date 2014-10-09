import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

/**
 * Created by mersin on 5/9/14.
 */
public class simulator {

    public static Thread solvingThread;
    public static int result=0;
    public static void main(String[] args){
        JFrame f1 = new JFrame();
        f1.setTitle("Simulation");
        f1.setSize(new Dimension(600,750));

        // Create the panel that will display the maze
        final TwoDimGrid componentsPanel = new TwoDimGrid();
        final updatedMaze updated = new updatedMaze();

        Container contentPane = f1.getContentPane();
        contentPane.add(componentsPanel, BorderLayout.CENTER);

        componentsPanel.updateCurrentPosition(4,10,8); //Color the Starting position

        // Solve button
        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("Explore");
        startButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            	
            	selectSpeed();
            	JFrame g = new JFrame();
                g.setSize(new Dimension(400, 500));
                g.setLocation(1000, 100);
                Container contentPane1 = g.getContentPane();
                contentPane1.add(updated, BorderLayout.CENTER);
                // Create a new maze
                g.setVisible(true);
                updated.updatedMap();
                Algo_test solver = new Algo_test(componentsPanel,updated,result);
                solvingThread = new Thread(solver);
                solvingThread.start();
            }
        });
        buttonPanel.add(startButton);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        //time solver
        JButton updatedMap = new JButton ("Timer Solver");
		updatedMap.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				selectSpeed();
				
				String input = JOptionPane.showInputDialog("Exploration Time:",null); 
				int in=Integer.parseInt(input);
				new Reminder(in);
				
				
				JFrame g = new JFrame();
				g.setSize(new Dimension(400, 500));
				Container contentPane1 = g.getContentPane();
				contentPane1.add(updated, BorderLayout.CENTER);
				g.setVisible(true);
				updated.updatedMap();
				Algo_test solver = new Algo_test(componentsPanel,updated,result);
				solvingThread = new Thread(solver);
				solvingThread.start();
				//System.out.println("exploreEnds");
			}
			
		});
		buttonPanel.add(updatedMap);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
		// percentage solver
		
		JButton percentage = new JButton ("% Solve");
		percentage.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				selectSpeed();
				
				String input = JOptionPane.showInputDialog("Exploration %:",null); 
				int in=Integer.parseInt(input);
				componentsPanel.explored_percent=in;
				
				
				JFrame h = new JFrame();
				h.setSize(new Dimension(400, 500));
				Container contentPane1 = h.getContentPane();
				contentPane1.add(updated, BorderLayout.CENTER);
				h.setVisible(true);
				updated.updatedMap();
				Algo_test solver= new Algo_test(componentsPanel,updated,result);
				solvingThread = new Thread(solver);
				solvingThread.start();
				
					
					
				
				//System.out.println("exploreEnds");
			}
			
		});
		buttonPanel.add(percentage);
		contentPane.add(buttonPanel, BorderLayout.SOUTH);
        
        
        
        f1.setVisible(true);



    }
    public static void selectSpeed() {
    	 
    	
                JPanel panel = new JPanel();
                panel.add(new JLabel("Speed selection:"));
                DefaultComboBoxModel model = new DefaultComboBoxModel();
                model.addElement("X1");
                model.addElement("X2");
                model.addElement("X3");
                model.addElement("X5");
                JComboBox comboBox = new JComboBox(model);
                panel.add(comboBox);
                
                JOptionPane.showConfirmDialog(null, panel, "Speed", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
        String s = comboBox.getSelectedItem().toString();
        if (s.equals("X1")) {
            result = 1;

        } else if (s.equals("X2")) {
            result = 2;

        } else if (s.equals("X3")) {
            result = 3;

        } else if (s.equals("X5")) {
            result = 5;

        }

    }
    
}
