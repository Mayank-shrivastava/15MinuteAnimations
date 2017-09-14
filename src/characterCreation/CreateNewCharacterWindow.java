package characterCreation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import animationScene.StartAnimatingWindow;
import character.Character;
import character.CharacterEdge;
import character.CharacterNode;
import character.Sprite;

public class CreateNewCharacterWindow {
	private JFrame frame;
	private JLabel statusLabel;
	private JPanel wrapperPanel;
	private JPanel rightPanel;
	private JPanel canvas;
	private JButton doneButton;
	private JButton addNode, addEdge, addSprite, changeCenterNode;

	private CreateCharacterStatusEnum status=CreateCharacterStatusEnum.ADDING_NODE;
	private CharacterNode selectedNode=null;
	private Character character=new Character();
	private static boolean doneWaiting=false;

	public CreateNewCharacterWindow() {
		createJFrame();
		
		new Thread(new Runnable () {
			public void run() {
				while (frame.isVisible()) {
					redrawEverything();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();
	}

	public void createJFrame() {
		frame=new JFrame("Create your character");
		wrapperPanel=new JPanel();
		rightPanel=new JPanel();
		canvas=new JPanel();
		rightPanel=new JPanel();
		statusLabel=new JLabel("Click a button below to do an action");
		doneButton=new JButton("Done.");
		addNode=new JButton("Add Node");
		addEdge=new JButton("Add Edge");
		addSprite=new JButton("Add Sprite");
		changeCenterNode=new JButton("Change Center Node");
		frame.add(wrapperPanel);
		wrapperPanel.setPreferredSize(new Dimension(1100, 600));
		canvas.setPreferredSize(new Dimension(800, 600));
		canvas.setBackground(Color.white);
		wrapperPanel.add(canvas);
		rightPanel.add(statusLabel);
		rightPanel.add(addNode);
		rightPanel.add(addEdge);
		rightPanel.add(addSprite);
		rightPanel.add(changeCenterNode);
		rightPanel.add(doneButton);
		wrapperPanel.add(rightPanel);
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setPreferredSize(new Dimension(280, 400));

		setupListeners();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void setupListeners() {
		canvas.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				onCanvasClick(e.getX(), e.getY());
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

		addNode.addActionListener(e -> {
			status=CreateCharacterStatusEnum.ADDING_NODE;
			statusLabel.setText("Click on the canvas to add a node.");
		});
		addEdge.addActionListener(e -> {
			status=CreateCharacterStatusEnum.ADDING_EDGE_NONE_SELECTED;
			statusLabel.setText("Click on the first of the two nodes.");
		});
		addSprite.addActionListener(e -> {
			frame.setEnabled(false);				
			new CreateSpriteWindow(character, this);
		});
		changeCenterNode.addActionListener(e->{
			status=CreateCharacterStatusEnum.CHANGE_CENTER_NODE;
			statusLabel.setText("Click on the new center node.");
		});
		doneButton.addActionListener(e -> {
			System.out.println("Done button clicked");
			JFileChooser fileChooser=new JFileChooser();
			int result=fileChooser.showSaveDialog(frame);
			if (result==JFileChooser.APPROVE_OPTION) {
				
				try {
					FileOutputStream fos=new FileOutputStream(fileChooser.getSelectedFile());
					ObjectOutputStream oos=new ObjectOutputStream(fos);
					oos.writeObject(character);
					oos.close();
					frame.dispose();
					new StartAnimatingWindow(character);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public void onCanvasClick(int x, int y) {
		switch (status) {
			case ADDING_NODE:
				String name=promptForName();
				character.addNode(new CharacterNode(name, x, y));
				break;
			case ADDING_EDGE_NONE_SELECTED:
				CharacterNode closest=getClosestNode(x, y);
				if (closest!=null) {
					selectedNode=closest;
					status=CreateCharacterStatusEnum.ADDING_EDGE_ONE_SELECTED;
					statusLabel.setText("Click on the second node of the edge.");
				}
				break;
			case ADDING_EDGE_ONE_SELECTED:
				CharacterNode closestSecond=getClosestNode(x, y);
				if (closestSecond!=null) {
					character.addEdge(new CharacterEdge(selectedNode, closestSecond));
					status=CreateCharacterStatusEnum.ADDING_EDGE_NONE_SELECTED;
					statusLabel.setText("Edge Added. Click on a node to start a new edge.");
				}
				break;
			case CHANGE_CENTER_NODE:
				CharacterNode newCenter=getClosestNode(x, y);
				if (newCenter!=null) {
					character.changeCenterNode(newCenter);
					statusLabel.setText("Center node changed.");
				}
				break;

		}
		redrawEverything();
	}
	
	public CharacterNode getClosestNode(int x, int y) {
		final double MAX_DISTANCE=15;
		CharacterNode closest=null;
		for (CharacterNode n:character.getNodes()) {
			if (closest==null) {
				if (Math.hypot(x-n.getX(), y-n.getY())<MAX_DISTANCE) {
					closest=n;
				}
			}
			else {
				if (Math.hypot(x-n.getX(), y-n.getY())<Math.hypot(x-closest.getX(), y-closest.getY())) {
					closest=n;
				}
			}
		}
		return closest;
	}
	
	public String promptForName() {
		JTextField textArea=new JTextField();
		JOptionPane.showMessageDialog(frame, textArea, "Enter a name for the Node.", JOptionPane.PLAIN_MESSAGE);
		textArea.requestFocus(true);
		return textArea.getText();
	}
	
	public void redrawEverything() {
		ArrayList<CharacterNode> nodes=character.getNodes();
		ArrayList<CharacterEdge> edges=character.getEdges();
		ArrayList<Sprite> sprites=character.getSprites();
		Graphics2D g=(Graphics2D)canvas.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		for (int i=0; i<sprites.size(); i++) {
			sprites.get(i).draw(g);
		}
		g.setColor(Color.blue);
		for (int i=0; i<edges.size(); i++) {
			CharacterEdge edge=edges.get(i);
			g.drawLine(edge.getFirstNode().getX(), edge.getFirstNode().getY(), 
					edge.getSecondNode().getX(), edge.getSecondNode().getY());
		}
		g.setColor(Color.black);
		for (int i=0; i<nodes.size(); i++) {
			CharacterNode node=nodes.get(i);
			if (node==character.getCenterNode()) {
				g.drawString("<-- Center Node", node.getX()+10, node.getY()+5);
				g.setColor(Color.green);
			}
			g.fillOval(node.getX()-5, node.getY()-5, 10, 10);
			g.setColor(Color.black);
			g.drawString(node.getName(), node.getX()-15, node.getY()-15);
		}
		if (status==CreateCharacterStatusEnum.ADDING_EDGE_ONE_SELECTED) {
			g.setColor(Color.orange);
			g.fillOval(selectedNode.getX()-7, selectedNode.getY()-7, 14, 14);
		}
		
		g.dispose();
	}
	
	public void onCreateSpriteWindowCompleted(Sprite createdSprite) {
		if (createdSprite!=null) {
			character.addSprite(createdSprite);
		}
		frame.setEnabled(true);
		frame.requestFocus();
	}


}

enum CreateCharacterStatusEnum {
	ADDING_NODE, ADDING_EDGE_NONE_SELECTED, ADDING_EDGE_ONE_SELECTED, CHANGE_CENTER_NODE, 
}
