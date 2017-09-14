package animationScene;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import character.Character;

public class ViewRecordingScene {
	private ArrayList<BufferedImage> recording;
	private Character character;
	private JFrame frame;
	private JPanel wrapperPanel;
	private JPanel canvas;
	private JPanel rightPane;
	private JLabel firstFrameLabel;
	private JTextField firstTextField;
	private JLabel lastFrameLabel;
	private JTextField lastTextField;
	private JLabel currentFrame;
	private JButton playButton;
	private JButton recordAgainButton;
	private JButton processButton;

	private volatile int startIndex;
	private volatile int endIndex;
	private volatile int currentIndex;
	private volatile int updateCounter=0, myUpdateCounter=0;
	private boolean playing=false;
	private boolean stop=false;

	public ViewRecordingScene(ArrayList<BufferedImage> recording, Character character) {
		this.recording=recording;
		this.character=character;
		startIndex=0;
		currentIndex=startIndex;
		endIndex=recording.size()-1;
		setupJFrame();
		playLoop();
	}

	public void setupJFrame() {
		frame=new JFrame("Crop your video");
		wrapperPanel=new JPanel();
		canvas=new JPanel();
		rightPane=new JPanel();
		firstFrameLabel=new JLabel("First frame of video: (0-"+(recording.size()-1)+")");
		firstTextField=new JTextField(""+startIndex, 6);
		lastFrameLabel=new JLabel("Last frame of video: (0-"+(recording.size()-1)+")");
		lastTextField=new JTextField(""+endIndex, 6);
		currentFrame=new JLabel("Current Frame: "+startIndex);
		playButton=new JButton("Play Recording");
		recordAgainButton=new JButton("Record Again");
		processButton=new JButton("Process");

		canvas.setPreferredSize(new Dimension(640, 420));
		rightPane.setPreferredSize(new Dimension(200, 300));

		rightPane.setLayout(new BoxLayout(rightPane, BoxLayout.Y_AXIS));
		rightPane.add(firstFrameLabel);
		rightPane.add(firstTextField);
		rightPane.add(lastFrameLabel);
		rightPane.add(lastTextField);
		rightPane.add(currentFrame);
		rightPane.add(playButton);
		rightPane.add(recordAgainButton);
		rightPane.add(processButton);

		wrapperPanel.add(canvas);
		wrapperPanel.add(rightPane);
		frame.add(wrapperPanel);

		addFunctionality();

		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public void addFunctionality() {
		firstTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				warn();
			}
			public void removeUpdate(DocumentEvent e) {
				warn();
			}
			public void insertUpdate(DocumentEvent e) {
				warn();
			}

			public void warn() {
				try {
				updateCounter++;
				String newStartString=firstTextField.getText();
				int newStartInt=Integer.parseInt(newStartString);
				if (newStartInt>=0&&newStartInt<recording.size()) {
					startIndex=newStartInt;
					playing=false;
					currentIndex=startIndex;
					endIndex=Math.max(endIndex, startIndex);
					updateCurrentLabel();
				}
				}
				catch(Exception e) {
					
				}
			}
		});

		lastTextField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				warn();
			}
			public void removeUpdate(DocumentEvent e) {
				warn();
			}
			public void insertUpdate(DocumentEvent e) {
				warn();
			}

			public void warn() {
				try {
				updateCounter++;
				String newEndString=lastTextField.getText();
				int lastStartInt=Integer.parseInt(newEndString);
				if (lastStartInt>=0&&lastStartInt<recording.size()) {
					endIndex=lastStartInt;
					playing=false;
					currentIndex=startIndex;
					startIndex=Math.min(startIndex, endIndex);
					updateCurrentLabel();
				}
				}
				catch(Exception e) {
					
				}
			}
		});

		playButton.addActionListener(e -> {
			playing^=true;
		});

		recordAgainButton.addActionListener(e -> {
			stop=true;
			frame.dispose();
			new StartAnimatingWindow(character);
		});

		processButton.addActionListener(e -> {
			ArrayList<BufferedImage> newList=new ArrayList<BufferedImage>();
			for (int i=startIndex; i<=endIndex; i++) {
				newList.add(recording.get(i));
			}
			stop=true;
			frame.dispose();
			new ProcessAnimationWindow(character, newList);
		});
	}

	public void playLoop() {
		while (!stop) {
			Graphics2D g=(Graphics2D) canvas.getGraphics();
			g.drawImage(recording.get(currentIndex), 0, 0, canvas.getWidth(), canvas.getHeight(), null);
			if (playing) {
				currentIndex++;
				if (currentIndex>endIndex) {
					currentIndex=startIndex;
					playing=false;
				}
				updateCurrentLabel();
			}
			if (updateCounter!=myUpdateCounter) {
				myUpdateCounter++;
				currentIndex=startIndex;
				playing=false;
			}
			g.dispose();
			try {
				Thread.sleep(1000/30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateCurrentLabel() {
		currentFrame.setText("Current Frame: "+currentIndex);
	}

}
