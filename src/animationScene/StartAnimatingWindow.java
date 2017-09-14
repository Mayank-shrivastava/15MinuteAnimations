package animationScene;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import character.Character;

public class StartAnimatingWindow {
	private Character character;
	
	private JFrame frame;
	private JPanel mainPanel;
	private JLabel waitBeforeStartLabel;
	private JTextField waitBeforeStartTextBox;
	private JLabel animationLengthLabel;
	private JTextField animationLengthTextBox;
	private JButton start;
	double secondsLeft=-1;
	
	public StartAnimatingWindow(Character character) {
		this.character=character;
		setupJFrame();
	}
	
	public void setupJFrame() {
		frame=new JFrame("Record animation");
		mainPanel=new JPanel();
		waitBeforeStartLabel=new JLabel("Seconds before recording starts: ");
		mainPanel.add(waitBeforeStartLabel);
		waitBeforeStartTextBox=new JTextField("3.0", 4);
		mainPanel.add(waitBeforeStartTextBox);
		animationLengthLabel=new JLabel("Animation length (seconds): ");
		mainPanel.add(animationLengthLabel);
		animationLengthTextBox=new JTextField("3.0", 4);
		mainPanel.add(animationLengthTextBox);
		start=new JButton("Start");
		mainPanel.add(start);
		mainPanel.setPreferredSize(new Dimension(250, 100));
		
		start.addActionListener(e->{
			new Thread(new Runnable() {
				public void run() {
					secondsLeft=Double.parseDouble(waitBeforeStartTextBox.getText());
					int tenthsLeft=(int)secondsLeft*10-1;
					while (tenthsLeft>=1) {
						start.setText(tenthsLeft/10+1+"");
						tenthsLeft--;
						try {
							Thread.sleep(1000/10);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					frame.dispose();
					new RecordAnimationWindow(character, Double.parseDouble(animationLengthTextBox.getText()));
				}
			}).start();
		});
		
		frame.add(mainPanel);
		
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
