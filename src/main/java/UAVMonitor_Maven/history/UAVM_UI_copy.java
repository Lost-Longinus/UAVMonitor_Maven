package UAVMonitor_Maven.history;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Pengfei Jin on 2018/11/11.
 */

public class UAVM_UI_copy {
	public static void main( String[] args ) throws Exception {
		String inputFile = "D:\\recordef.flv";//"http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
		String outputFile = "D:\\recorde.flv";
		//showCamera(inputFile, outputFile);
		audioPlayback audioPlayback = new audioPlayback(inputFile);

	}

	public static void showCamera(String inputFile, String outputFile)
			throws Exception {
		Loader.load(opencv_objdetect.class);
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, 1);
		grabber.start();
		recorder.start();
		long startTime=0;


		MyCanvasFrame myframe = new MyCanvasFrame("camera", MyCanvasFrame.getDefaultGamma() / grabber.getGamma());
		myframe.add(new JButton("飞行器控制面板"),BorderLayout.EAST);
		myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myframe.setAlwaysOnTop(true);
		Frame frame = null;
		while (myframe.isVisible() && (frame = grabber.grabFrame()) != null) {
			myframe.showImage(frame);
			//audioPlayback.processAudio(frame.samples);
			recorder.record(frame);
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}
			//Thread.sleep(10);
		}
		myframe.dispose();
		grabber.stop();
		recorder.stop();
	}
}
