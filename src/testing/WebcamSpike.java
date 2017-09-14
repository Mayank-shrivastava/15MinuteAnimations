package testing;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class WebcamSpike {

	public static JFrame frame;
	public static JPanel mainPanel;
	
	public static void main(String[] args) {
		createJFrame();
		cameraLoop();

	}
	
	private static void cameraLoop() {
		VideoCapture camera=new VideoCapture(0);
		
		Mat frame=new Mat();
		camera.read(frame);

		if (!camera.isOpened()) {
			System.out.println("Error.");
			camera.release();
		}
		else {
			while (true) {
				if (camera.read(frame)) {
					BufferedImage image=MatToBufferedImage(frame);
					Graphics2D g=(Graphics2D)mainPanel.getGraphics();
					g.drawImage(image, 0, 0, null);
				}
			}
		}
		
	}

	public static BufferedImage MatToBufferedImage(Mat frame) {
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
	
	private static void createJFrame() {
		frame=new JFrame("Webcam Spike");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPanel=new JPanel();
		mainPanel.setPreferredSize(new Dimension(1000, 600));
		frame.add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

}
