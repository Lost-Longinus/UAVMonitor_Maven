package UAVMonitor_Maven.history;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

/**
 * Created by Pengfei Jin on 2018/11/8.
 */
public class CollectFlow {
	public static void main(String[] args) throws Exception {
		//海康和大华IP摄像头rtsp地址格式参考--https://blog.csdn.net/xiejiashu/article/details/38523437
		String inputFile = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
		// Decodes-encodes
		//录制的mp4文件无法播放，格式问题，录制为flv格式即可！！！
		String outputFile = "D:\\recorde.flv";
		frameRecord(inputFile, outputFile,1);
	}

	public static void frameRecord(String inputFile, String outputFile, int audioChannel)
			throws Exception {

		boolean isStart=true;//该变量建议设置为全局控制变量，用于控制录制结束
		// 获取视频源
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
		System.out.println("音频： "+grabber.hasAudio()+" 视频： "+grabber.hasVideo());
		// 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);
		// 开始取视频源
		recordByFrame(grabber, recorder, isStart);
	}

	private static void recordByFrame(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, Boolean status)
			throws Exception {
		try {
			grabber.start();
			recorder.start();
			Frame frame = null;
			while (status&& (frame = grabber.grabFrame()) != null) {
				System.out.println(frame.samples);
				recorder.record(frame);
			}
			recorder.stop();
			grabber.stop();
		} finally {
			if (grabber != null) {
				grabber.stop();
			}
		}
	}
}

