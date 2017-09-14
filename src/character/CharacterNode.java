package character;

import java.io.Serializable;

public class CharacterNode implements Serializable {
	private static final long serialVersionUID=1L;
	private String name;
	private int x;
	private int y;
	
	public CharacterNode(String name, int x, int y) {
		this.name=name;
		this.x=x;
		this.y=y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String getName() {
		return name;
	}
}
