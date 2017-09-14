package animationScene;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import character.Character;

public class RecordAnimationWindow {
	private Character character;
	private double lengthOfAnimation;
	
	private JFrame frame;
	private JPanel mainWindow;
	
	private ArrayList<BufferedImage> bufferedImages=new ArrayList<BufferedImage>();
	
	public RecordAnimationWindow(Character character, double lengthOfAnimation) {
		this.character=character;
		this.lengthOfAnimation=lengthOfAnimation;
		System.out.println("Recording animation");
		setupJframe();
		
		//new Thread(new Runnable() {
		//	public void run() {
				cameraLoop();
		//	}
		//}).start();
	}
	
	public void setupJframe() {
		frame=new JFrame("Recording...");
		mainWindow=new JPanel();
		frame.add(mainWindow);
		
		mainWindow.setPreferredSize(new Dimension(640, 420));
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void cameraLoop() {
		VideoCapture camera=new VideoCapture(0);
		
		Mat frame=new Mat();
		camera.read(frame);

		if (!camera.isOpened()) {
			System.out.println("Error.");
			camera.release();
		}
		else {
			long startTime=System.currentTimeMillis();
			long endTime=(long) (startTime+lengthOfAnimation*1000);
			while (System.currentTimeMillis()<=endTime) {
				if (camera.read(frame)) {
					BufferedImage image=MatToBufferedImage(frame);
					Graphics2D g=(Graphics2D)mainWindow.getGraphics();
					bufferedImages.add(image);
					//System.out.println(bufferedImages.size());
					g.drawImage(image, 0, 0, null);
				}
			}
			camera.release();
			this.frame.dispose();
			new ViewRecordingScene(bufferedImages, character);
		}
		
	}
	
	public BufferedImage MatToBufferedImage(Mat frame) {
		// Mat() to BufferedImage
		int type=0;
		if (frame.channels()==1) {
			type=BufferedImage.TYPE_BYTE_GRAY;
		}
		else if (frame.channels()==3) {
			type=BufferedImage.TYPE_3BYTE_BGR;
		}
		BufferedImage image=new BufferedImage(frame.width(), frame.height(), type);
		WritableRaster raster=image.getRaster();
		DataBufferByte dataBuffer=(DataBufferByte) raster.getDataBuffer();
		byte[] data=dataBuffer.getData();
		frame.get(0, 0, data);

		return image;
	}
}
