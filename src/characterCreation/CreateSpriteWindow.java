package characterCreation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import character.Character;
import character.CharacterEdge;
import character.Sprite;

public class CreateSpriteWindow {
	
	private JFrame frame;
	private JPanel wrapperPanel;
	private JPanel rightPanePanel;
	private JPanel canvas;
	private JLabel statusLabel;
	private JButton repositionFirstPoint;
	private JButton repositionSecondPoint;
	private JTextField distanceFromCamera;
	private JCheckBox scaleVertically;
	private JCheckBox scaleHorizonatlly;
	private JButton flipHorizonatlly;
	private JButton flipVertically;
	private JButton increaseSize5Percent;
	private JButton decreaseSize5Percent;
	private JButton cancelButton;
	private JButton doneButton;
	
	private boolean done=false;
	private boolean stopEverything=false;
	
	private CreateSpriteWindowEnum status;
	
	private CharacterEdge myEdge;
	private BufferedImage image;
	private Character character;
	private Point firstNodeOnImage, secondNodeOnImage;
	private CreateNewCharacterWindow callback;
	
	public CreateSpriteWindow(Character character, CreateNewCharacterWindow callback) {
		this.character=character;
		this.callback=callback;
		selectImage();
		if (!stopEverything) {
			selectEdge();
		}
		if (!stopEverything) {			
			createJframe();
		}
	}
	
	public void selectImage() {
		JFileChooser fileChooser=new JFileChooser();
		int result=fileChooser.showOpenDialog(frame);
		if (result!=JFileChooser.APPROVE_OPTION) {
			onCancelClicked();
			return;
		}
		try {
			image=ImageIO.read(fileChooser.getSelectedFile());
			image=addPadding(image);
		} catch (IOException e) {
			selectImage();
		}
	}
	
	public BufferedImage addPadding(BufferedImage original) {
		int addition=Math.max(original.getWidth(), original.getHeight());
		BufferedImage toReturn=new BufferedImage(original.getWidth()+addition*2, original.getHeight()+addition*2, 
				BufferedImage.TYPE_INT_ARGB);
		int clear=new Color(0, 0, 0, 0).getRGB();
		for (int x=0; x<toReturn.getWidth(); x++) {
			for (int y=0; y<toReturn.getHeight(); y++) {
				toReturn.setRGB(x, y, clear);
			}
		}
		for (int x=0; x<original.getWidth(); x++) {
			for (int y=0; y<original.getHeight(); y++) {
				toReturn.setRGB(x+addition, y+addition, original.getRGB(x, y));
			}
		}
		return toReturn;
	}
	
	public void selectEdge() {
		String[] choices=new String[character.getEdges().size()];
		for (int i=0; i<choices.length; i++) {
			CharacterEdge e=character.getEdges().get(i);
			choices[i]="Edge connecting "+e.getFirstNode().getName()+" to "+e.getSecondNode().getName(); 
		}
		JComboBox<String> comboBox=new JComboBox<>(choices);
		JOptionPane.showMessageDialog(frame, comboBox, "Select and edge for this sprite.", JOptionPane.PLAIN_MESSAGE);
		int indexSelected=comboBox.getSelectedIndex();
		if (indexSelected==-1) {
			onCancelClicked();
			return;
		}
		 myEdge=character.getEdges().get(indexSelected);
	}
	
	public void createJframe() {
		frame=new JFrame("Configure Sprite Settings");
		wrapperPanel=new JPanel();
		rightPanePanel=new JPanel();
		canvas=new JPanel();
		statusLabel=new JLabel("Click a button to do something");
		repositionFirstPoint=new JButton("Reposition "+myEdge.getFirstNode().getName()+" Location");
		repositionSecondPoint=new JButton("Reposition "+myEdge.getSecondNode().getName()+" Location");
		distanceFromCamera=new JTextField("50", 5);
		scaleVertically=new JCheckBox("Scale Vertically?");
		scaleHorizonatlly=new JCheckBox("Scale Horizonatlly?");
		flipHorizonatlly=new JButton("Flip Horizontally");
		flipVertically=new JButton("Flip Vertically");
		increaseSize5Percent=new JButton("Increase Size by 5%");
		decreaseSize5Percent=new JButton("Decrease Size by 5%");
		cancelButton=new JButton("Cancel");
		doneButton=new JButton("Done");
		
		rightPanePanel.setLayout(new BoxLayout(rightPanePanel, BoxLayout.Y_AXIS));
		rightPanePanel.add(statusLabel);
		rightPanePanel.add(repositionFirstPoint);
		rightPanePanel.add(repositionSecondPoint);
		
		//JPanel tempDistancePanel=new JPanel();
		rightPanePanel.add(new JLabel("Distance from Camera (0.0-100.0): "));
		rightPanePanel.add(distanceFromCamera);
		//tempDistancePanel.setPreferredSize(new Dimension(240, 100));
		//rightPanePanel.add(tempDistancePanel);
		
		rightPanePanel.add(scaleVertically);
		rightPanePanel.add(scaleHorizonatlly);
		rightPanePanel.add(flipHorizonatlly);
		rightPanePanel.add(flipVertically);
		rightPanePanel.add(increaseSize5Percent);
		rightPanePanel.add(decreaseSize5Percent);
		rightPanePanel.add(cancelButton);
		rightPanePanel.add(doneButton);
		
		scaleVertically.setSelected(true);
		scaleHorizonatlly.setSelected(true);
		
		wrapperPanel.setPreferredSize(new Dimension(900, 500));
		rightPanePanel.setPreferredSize(new Dimension(250, 320));
		canvas.setPreferredSize(new Dimension(600, 490));
		wrapperPanel.add(canvas);
		wrapperPanel.add(rightPanePanel);
		frame.add(wrapperPanel);
		
		addFunctionality();
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		firstNodeOnImage=new Point(canvas.getWidth()/3, canvas.getHeight()/2);
		secondNodeOnImage=new Point(canvas.getWidth()*2/3, canvas.getHeight()/2);
		
		new Thread(new Runnable() {
			public void run() {
				while (!done) {					
					redrawEverything();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();;
		
	}
	
	public void addFunctionality() {
		repositionFirstPoint.addActionListener(e-> {
			status=CreateSpriteWindowEnum.CHANGE_NODE_ONE_POSITION;
			statusLabel.setText("Click to reposition Node One");
		});
		repositionSecondPoint.addActionListener(e-> {
			status=CreateSpriteWindowEnum.CHANGE_NODE_TWO_POSITION;
			statusLabel.setText("Click to reposition Node Two");
		});
		flipVertically.addActionListener(e-> {
			BufferedImage newImage=new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			for (int x=0; x<image.getWidth(); x++) {
				for (int y=0; y<image.getHeight(); y++) {
					newImage.setRGB(x, y, image.getRGB(x, image.getHeight()-1-y));	
				}
			}
			image=newImage;
			redrawEverything();
		});
		flipHorizonatlly.addActionListener(e-> {
			BufferedImage newImage=new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			for (int x=0; x<image.getWidth(); x++) {
				for (int y=0; y<image.getHeight(); y++) {
					newImage.setRGB(x, y, image.getRGB(image.getWidth()-1-x, y));	
				}
			}
			image=newImage;
			redrawEverything();
		});
		increaseSize5Percent.addActionListener(e-> {
			image=Sprite.getScaledImage(1.05, 1.05, image);
			redrawEverything();
		});
		decreaseSize5Percent.addActionListener(e-> {
			image=Sprite.getScaledImage(.95, .95, image);
		});
		canvas.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				int x=e.getX();
				int y=e.getY();
				onCanvasClicked(x, y);
			}
			public void mousePressed(MouseEvent e) {
			}
			public void mouseReleased(MouseEvent e) {
			}
			public void mouseEntered(MouseEvent e) {
			}
			public void mouseExited(MouseEvent e) {
			}
		});
		cancelButton.addActionListener(e-> {
			onCancelClicked();
		});
		doneButton.addActionListener(e-> {
			onDoneClicked();
		});
	}
	
	public void onCancelClicked() {
		stopEverything=true;
		done=true;
		if (frame!=null) {			
			frame.dispose();
		}
		callback.onCreateSpriteWindowCompleted(null);
	}
	
	public void onDoneClicked() {
		done=true;
		if (frame!=null) {			
			frame.dispose();
		}
		double distanceFromCamera=Double.parseDouble(this.distanceFromCamera.getText());
		int bufferedImageDrawX=(canvas.getWidth()-image.getWidth())/2;
		int bufferedImageDrawY=(canvas.getHeight()-image.getHeight())/2;
		Point p1Adjusted=new Point(firstNodeOnImage.x-bufferedImageDrawX, firstNodeOnImage.y-bufferedImageDrawY);
		Point p2Adjusted=new Point(secondNodeOnImage.x-bufferedImageDrawX, secondNodeOnImage.y-bufferedImageDrawY);
		Sprite created=new Sprite(myEdge, image, p1Adjusted, p2Adjusted, distanceFromCamera, 
				scaleHorizonatlly.isSelected(), scaleVertically.isSelected());
		callback.onCreateSpriteWindowCompleted(created);
	}
	
	public void onCanvasClicked(int x, int y) {
		switch (status) {
			case CHANGE_NODE_ONE_POSITION:
				status=CreateSpriteWindowEnum.WAITING_FOR_BUTTON_PRESS;
				firstNodeOnImage=new Point(x, y);
				statusLabel.setText("Click a button to do something");
				break;
			case CHANGE_NODE_TWO_POSITION:
				status=CreateSpriteWindowEnum.WAITING_FOR_BUTTON_PRESS;
				secondNodeOnImage=new Point(x, y);
				statusLabel.setText("Click a button to do something");
				break;
			case WAITING_FOR_BUTTON_PRESS:
				//don't do anything
				break;
			
		}
		redrawEverything();
	}
	
	public void redrawEverything() {
		Graphics2D g=(Graphics2D)canvas.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		if (image!=null) {
			int x=(canvas.getWidth()-image.getWidth())/2;
			int y=(canvas.getHeight()-image.getHeight())/2;
			g.drawImage(image, x, y, null);
		}
		if (myEdge!=null) {			
			g.setColor(Color.black);
			g.fillOval(firstNodeOnImage.x-5, firstNodeOnImage.y-5, 10, 10);
			g.fillOval(secondNodeOnImage.x-5, secondNodeOnImage.y-5, 10, 10);
			g.drawString(myEdge.getFirstNode().getName(), firstNodeOnImage.x-15, firstNodeOnImage.y-15);
			g.drawString(myEdge.getSecondNode().getName(), secondNodeOnImage.x-15, secondNodeOnImage.y-15);
		}
	}
}

enum CreateSpriteWindowEnum {
	WAITING_FOR_BUTTON_PRESS, CHANGE_NODE_ONE_POSITION, CHANGE_NODE_TWO_POSITION, 
}
