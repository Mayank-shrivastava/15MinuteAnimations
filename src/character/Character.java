package character;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Character implements Serializable {
	
	private static final long serialVersionUID=1L;
	private CharacterNode centerNode;
	private ArrayList<CharacterNode> nodes=new ArrayList<>();
	private ArrayList<CharacterEdge> edges=new ArrayList<>();
	private ArrayList<Sprite> sprites=new ArrayList<>();
	
	public Character() {
		
	}
	
	public static Character loadCharacterFromFile(File toLoadFrom) {
		try {
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(toLoadFrom));
			Character toReturn=(Character) ois.readObject();
			ois.close();
			System.out.println("Loaded character with "+toReturn.nodes.size()+" nodes and "+toReturn.edges.size()+" edges.");
			return toReturn;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addNode(CharacterNode toAdd) {
		if (nodes.isEmpty()) {
			centerNode=toAdd;
		}
		nodes.add(toAdd);
	}
	
	public void addEdge(CharacterEdge toAdd) {
		CharacterNode a=toAdd.getFirstNode(), b=toAdd.getSecondNode();
		if (a==b) {
			return;
		}
		for (CharacterEdge e:edges) {
			if (e.getFirstNode()==a&&e.getSecondNode()==b)
				return;
			if (e.getFirstNode()==b&&e.getSecondNode()==a)
				return;
		}
		edges.add(toAdd);
	}
	
	public void addSprite(Sprite s) {
		sprites.add(s);
	}
	
	public void changeCenterNode(CharacterNode newCenter) {
		centerNode=newCenter;
	}
	
	public ArrayList<CharacterNode> getNodes() {
		return nodes;
	}
	
	public ArrayList<CharacterEdge> getEdges() {
		return edges;
	}
	
	public ArrayList<Sprite> getSprites() {
		return sprites;
	}
	
	public CharacterNode getCenterNode() {
		return centerNode;
	}

}
