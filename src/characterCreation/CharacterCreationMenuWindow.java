package characterCreation;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

import animationScene.StartAnimatingWindow;
import character.Character;

public class CharacterCreationMenuWindow {
	private JFrame frame;
	private JPanel mainPanel;
	private JButton createCharacterButton;
	private JButton loadCharacterButton;
	
	public CharacterCreationMenuWindow() {
		setUpJframe();
	}
	
	public void setUpJframe() {
		frame=new JFrame("Choose a Character to Animate");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel=new JPanel();
		mainPanel.setPreferredSize(new Dimension(500, 70));
		frame.add(mainPanel);
		createCharacterButton=new JButton("Create a new character");
		loadCharacterButton=new JButton("Load a character");
		createButtonActions();
		mainPanel.add(createCharacterButton);
		mainPanel.add(loadCharacterButton);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void createButtonActions() {
		createCharacterButton.addActionListener(e->{
			closeWindow();
			new CreateNewCharacterWindow();
		});
		loadCharacterButton.addActionListener(e->{
			findCharacterToLoad();
		});
	}
	
	public void closeWindow() {
		frame.dispose();
	}
	
	public void findCharacterToLoad() {
		final JFileChooser fileChooser=new JFileChooser();
		int result=fileChooser.showOpenDialog(frame);
		if (result==JFileChooser.APPROVE_OPTION) {
			File file=fileChooser.getSelectedFile();
			System.out.println("File returned: "+file.getPath());
			closeWindow();
			new StartAnimatingWindow(Character.loadCharacterFromFile(file));
		}
		else {
			System.out.println("User didn't approve file selection.");
		}
		
	}
}
