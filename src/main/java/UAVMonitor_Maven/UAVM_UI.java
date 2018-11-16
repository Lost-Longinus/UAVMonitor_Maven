package UAVMonitor_Maven;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;

/**
 * Created by Pengfei Jin on 2018/11/11.
 */

public class UAVM_UI {

	public static void main( String[] args ) throws Exception {
		//String inputFile = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
		String inputFile = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
		String outputFile = "D:\\record.flv";
		UAVM_UI uavm_ui = new UAVM_UI();
		uavm_ui.TVshow(inputFile, outputFile);
	}

	public void TVshow(String inputFile, String outputFile)
			throws Exception {
		Loader.load(opencv_objdetect.class);
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
		//FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, 1);
		grabber.start();
		//recorder.start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				String inputFile = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
				App app = new App();
				try {
					app.getAudio(inputFile);
				} catch (FrameGrabber.Exception e) {
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		}).start();

		MyFrame myframe = new MyFrame("camera", MyCanvasFrame.getDefaultGamma() / grabber.getGamma());
		//withOperatePanel(myframe);
		System.out.println("MyFrame is loaded...");
		myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myframe.setAlwaysOnTop(true);
		Frame frame;
		while (myframe.isVisible() && (frame = grabber.grabFrame()) != null) {
			myframe.showImage(frame);
			//recorder.record(frame);
		}
		myframe.dispose();
		grabber.stop();
		//recorder.stop();
	}
	public static void withOperatePanel(MyCanvasFrame myframe){
		FrameUtil.initFrame(myframe, 1200, 600);
		myframe.setResizable(false);

		//使用BorderLayout布局方式，无法自定义按钮大小
		/*JPanel EjPanel = new JPanel();
		EjPanel.setPreferredSize(new Dimension(300, 100));
		EjPanel.setLayout(new BorderLayout());
		myframe.add(EjPanel, BorderLayout.EAST);

		JButton CjButton = new JButton("中");
		CjButton.setBounds(1000, 300, 80, 80);
		CjButton.setPreferredSize(new Dimension(80, 80));
		EjPanel.add(CjButton);

		JPanel SjPanel = new JPanel();
		SjPanel.setPreferredSize(new Dimension(300, 100));
		SjPanel.setBackground(Color.RED);
		myframe.add(SjPanel, BorderLayout.SOUTH);*/





	}
	public static void setIcon(String file,JButton com){
		ImageIcon ico=new ImageIcon(file);
		Image temp=ico.getImage().getScaledInstance(com.getWidth(),com.getHeight(),ico.getImage().SCALE_DEFAULT);
		ico=new ImageIcon(temp);
		com.setIcon(ico);
	}
}
