package animationScene;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import character.Character;

public class ProcessAnimationWindow {
	private Character character;
	private ArrayList<BufferedImage> frames;
	
	public ProcessAnimationWindow(Character character, ArrayList<BufferedImage> frames) {
		this.character=character;
		this.frames=frames;
	}
}
