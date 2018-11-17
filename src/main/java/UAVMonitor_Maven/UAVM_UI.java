package UAVMonitor_Maven;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;

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
				Audio audio = new Audio();
				try {
					audio.getAudio(inputFile);
				} catch (FrameGrabber.Exception e) {
					e.printStackTrace();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		}).start();

		MyFrame myframe = new MyFrame("camera");
		System.out.println("MyFrame is loaded...");
		myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myframe.setAlwaysOnTop(true);
		Frame frame;
		FrameUtil.initFrame(myframe, 1300, 580);
		FrameUtil.addControlPanel(myframe);
		while (myframe.isVisible() && (frame = grabber.grabFrame()) != null) {
			myframe.showImage(frame);
			//recorder.record(frame);
		}
		myframe.dispose();
		grabber.stop();
		//recorder.stop();
	}
}
