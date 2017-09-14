package character;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class Sprite implements Comparable<Sprite>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID=1L;
	private CharacterEdge myEdge;
	private transient BufferedImage image;
	private Point nodeOneLocationOnImage, nodeTwoLocationOnImage;
	private double distanceFromCamera;
	private boolean scaleHorizonatallyWithDistanceChange;
	private boolean scaleVerticallyWithDistanceChange;
	
	public Sprite(CharacterEdge myEdge, BufferedImage image, Point nodeOneLocationOnImage, Point nodeTwoLocationOnImage, 
			double distanceFromCamera2, boolean scaleHorizontallyWithDistanceChange, boolean scaleVerticallyWithDistanceChange) {
		this.myEdge=myEdge;
		this.image=image;
		this.nodeOneLocationOnImage=nodeOneLocationOnImage;
		this.nodeTwoLocationOnImage=nodeTwoLocationOnImage;
		this.distanceFromCamera=distanceFromCamera2;
		this.scaleHorizonatallyWithDistanceChange=scaleHorizontallyWithDistanceChange;
		this.scaleVerticallyWithDistanceChange=scaleVerticallyWithDistanceChange;
	}
	
	public int compareTo(Sprite o) {
		double result=distanceFromCamera-o.distanceFromCamera;
		if (result==0) {
			return 0;
		}
		return result>0?1:-1;
	}
	
	public void draw(Graphics2D g) {
		BufferedImage toDraw=image;
		double distanceOnImage=Math.hypot(nodeOneLocationOnImage.x-nodeTwoLocationOnImage.x, 
				nodeOneLocationOnImage.y-nodeTwoLocationOnImage.y);
		double distanceIRL=Math.hypot(myEdge.getFirstNode().getX()-myEdge.getSecondNode().getX(),
				myEdge.getFirstNode().getY()-myEdge.getSecondNode().getY());
		double xScale=scaleHorizonatallyWithDistanceChange?distanceIRL/(double)distanceOnImage:1;
		double yScale=scaleVerticallyWithDistanceChange?distanceIRL/(double)distanceOnImage:1;
		toDraw=getScaledImage(xScale, yScale, image);
		
		double onImageAngle=Math.atan2(nodeTwoLocationOnImage.y-nodeOneLocationOnImage.y,
				nodeTwoLocationOnImage.x-nodeOneLocationOnImage.x);
		double irlAngle=Math.atan2(myEdge.getSecondNode().getY()-myEdge.getFirstNode().getY(), 
				myEdge.getSecondNode().getX()-myEdge.getFirstNode().getX());
		double changeInAngle=irlAngle-onImageAngle;
		if (changeInAngle>Math.PI) {
			changeInAngle=2*Math.PI-changeInAngle;
		}
		
		Point midpoint=new Point((nodeOneLocationOnImage.x+nodeTwoLocationOnImage.x)/2,
				(nodeOneLocationOnImage.y+nodeTwoLocationOnImage.y)/2);
		toDraw=getRotatedImage(changeInAngle, toDraw, midpoint.x*xScale, midpoint.y*yScale);
		Point toDrawAt=new Point((myEdge.getFirstNode().getX()+myEdge.getSecondNode().getX())/2,
				(myEdge.getFirstNode().getY()+myEdge.getSecondNode().getY())/2);
		Point toDrawTopLeft=new Point((int)(toDrawAt.x-midpoint.x*xScale), (int)(toDrawAt.y-midpoint.y*yScale));
		g.drawImage(toDraw, toDrawTopLeft.x, toDrawTopLeft.y, null);
		
	}
	
	public static BufferedImage getRotatedImage(double radians, BufferedImage image, double xCenter, double yCenter) {
		AffineTransform transform = new AffineTransform();
	    transform.rotate(radians, xCenter, yCenter);
	    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
	    return op.filter(image, null);
	}
	
	public static BufferedImage getScaledImage(double xScale, double yScale, BufferedImage image) {
		AffineTransform transform = new AffineTransform();
	    transform.scale(xScale, yScale);
	    AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
	    return op.filter(image, null);
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		System.out.println("Writting Image...");
        out.defaultWriteObject();
        out.writeInt(1); // how many images are serialized?
        ImageIO.write(image, "png", out); // png is lossless
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	System.out.println("Reading Image...");
        in.defaultReadObject();
        final int imageCount = in.readInt();
        for (int i=0; i<imageCount; i++) {
            image=(ImageIO.read(in));
        }
        System.out.println("Image dimensions: "+image.getWidth()+" by "+image.getHeight());
    }
}
