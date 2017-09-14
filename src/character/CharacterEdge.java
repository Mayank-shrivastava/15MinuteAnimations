package character;

import java.io.Serializable;

public class CharacterEdge implements Serializable {
	private static final long serialVersionUID=1L;
	private CharacterNode leftNode;
	private CharacterNode rightNode;
	
	public CharacterEdge(CharacterNode leftNode, CharacterNode rightNode) {
		this.leftNode=leftNode;
		this.rightNode=rightNode;
	}
	
	public CharacterNode getFirstNode() {
		return leftNode;
	}
	
	public CharacterNode getSecondNode() {
		return rightNode;
	}
}
