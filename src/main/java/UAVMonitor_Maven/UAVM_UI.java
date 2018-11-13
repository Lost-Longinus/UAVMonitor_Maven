package UAVMonitor_Maven;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Pengfei Jin on 2018/11/11.
 */

public class UAVM_UI {

	//音频
	private AudioFormat af = null;
	private SourceDataLine sourceDataLine;
	private DataLine.Info dataLineInfo;
	Buffer[] buf;
	ShortBuffer ILData;
	ByteBuffer TLData;
	float vol = 1;//音量
	int sampleFormat;
	byte[] tl;

	public static void main( String[] args ) throws Exception {
		//String inputFile = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
		String inputFile = "D:\\recordef.flv";
		String outputFile = "D:\\recorde.flv";
		UAVM_UI uavm_ui = new UAVM_UI();
		uavm_ui.TVshow(inputFile, outputFile);
	}

	public void TVshow(String inputFile, String outputFile)
			throws Exception {
		Loader.load(opencv_objdetect.class);
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, 1);
		grabber.start();
		sampleFormat = grabber.getSampleFormat();
		initSourceDataLine(grabber);
		recorder.start();
		long startTime=0;


		MyCanvasFrame myframe = new MyCanvasFrame("camera", MyCanvasFrame.getDefaultGamma() / grabber.getGamma());
		myframe.add(new JButton("飞行器控制面板"),BorderLayout.EAST);
		myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myframe.setAlwaysOnTop(true);
		Frame frame, aframe;
		while (myframe.isVisible() && (frame = grabber.grabFrame()) != null) {
			myframe.showImage(frame);
			recorder.record(frame);
			aframe = grabber.grabSamples();
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}
			if(aframe == null){
				grabber.stop();
				System.exit(0);
			}
			processAudio(aframe.samples);
			//Thread.sleep(10);
		}
		myframe.dispose();
		grabber.stop();
		recorder.stop();
	}
	private void initSourceDataLine(FFmpegFrameGrabber fg) {
		af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,fg.getSampleRate(),16,fg.getAudioChannels(),fg.getAudioChannels()*2,fg.getSampleRate(),true);
		dataLineInfo = new DataLine.Info(SourceDataLine.class,
				af, AudioSystem.NOT_SPECIFIED);
		try {
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(af);
			sourceDataLine.start();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	public void processAudio(Buffer[] samples) {
		buf = samples;
		ILData = (ShortBuffer)buf[0];
		TLData = shortToByteValue(ILData,vol);
		tl = TLData.array();
		sourceDataLine.write(tl,0,tl.length);
	}
	public static ByteBuffer shortToByteValue(ShortBuffer arr,float vol) {
		int len  = arr.capacity();
		ByteBuffer bb = ByteBuffer.allocate(len * 2);
		for(int i = 0;i<len;i++){
			bb.putShort(i*2,(short)((float)arr.get(i)*vol));
		}
		return bb;
	}
}
