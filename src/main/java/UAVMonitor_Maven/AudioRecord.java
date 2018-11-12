package UAVMonitor_Maven;

import org.bytedeco.javacv.FFmpegFrameRecorder;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pengfei Jin on 2018/11/10.
 */
public class AudioRecord {
	public static void main(String[] args) throws LineUnavailableException, InterruptedException {
		test2();
	}

	public static void test2() throws InterruptedException, LineUnavailableException {
		int FRAME_RATE = 25;
		ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
		Runnable crabAudio = recordMicroPhone(4, "D:\\localAudio.flv",FRAME_RATE);//对应上面的方法体
		ScheduledFuture tasker = exec.scheduleAtFixedRate(crabAudio, 0, (long) 1000 / FRAME_RATE,
				TimeUnit.MILLISECONDS);
		Thread.sleep(20 * 1000);
		tasker.cancel(true);
		if (!exec.isShutdown()) {
			exec.shutdownNow();
		}
	}

	private static Runnable recordMicroPhone(final int AUDIO_DEVICE_INDEX, String outputFile, int frame_rate) {
		/**
		 * 设置音频编码器 最好是系统支持的格式，否则getLine() 会发生错误
		 * 采样率:44.1k;采样率位数:16位;立体声(stereo);是否签名;true:
		 * big-endian字节顺序,false:little-endian字节顺序(详见:ByteOrder类)
		 */
		AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);
		System.out.println("准备开启音频！");
		// 通过AudioSystem获取本地音频混合器信息
		Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
		// 通过AudioSystem获取本地音频混合器
		Mixer mixer = AudioSystem.getMixer(minfoSet[AUDIO_DEVICE_INDEX]);
		// 通过设置好的音频编解码器获取数据线信息
		DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

		// 打开并开始捕获音频
		// 通过line可以获得更多控制权
		// 获取设备：TargetDataLine line
		// =(TargetDataLine)mixer.getLine(dataLineInfo);
		Line dataline = null;
		try {
			dataline = AudioSystem.getLine(dataLineInfo);
		} catch (LineUnavailableException e2) {
			System.err.println("开启失败...");
			return null;
		}
		final TargetDataLine line = (TargetDataLine) dataline;
		try {
			line.open(audioFormat);
		} catch (LineUnavailableException e1) {
			line.stop();
			try {
				line.open(audioFormat);
			} catch (LineUnavailableException e) {
				System.err.println("按照指定音频编码器打开失败...");
				return null;
			}
		}
		line.start();
		System.out.println("已经开启音频！");
		// 获得当前音频采样率
		final int sampleRate = (int) audioFormat.getSampleRate();
		// 获取当前音频通道数量
		final int numChannels = audioFormat.getChannels();
		// 初始化音频缓冲区(size是音频采样率*通道数)
		int audioBufferSize = sampleRate * numChannels;
		final byte[] audioBytes = new byte[audioBufferSize];
		final FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, numChannels);
		Runnable crabAudio = new Runnable() {
			ShortBuffer sBuff = null;
			int nBytesRead;
			int nSamplesRead;

			@Override
			public void run() {
				System.out.println("读取音频数据...");
				// 非阻塞方式读取
				nBytesRead = line.read(audioBytes, 0, line.available());
				// 因为我们设置的是16位音频格式,所以需要将byte[]转成short[]
				nSamplesRead = nBytesRead / 2;
				short[] samples = new short[nSamplesRead];
				/**
				 * ByteBuffer.wrap(audioBytes)-将byte[]数组包装到缓冲区
				 * ByteBuffer.order(ByteOrder)-按little-endian修改字节顺序，解码器定义的
				 * ByteBuffer.asShortBuffer()-创建一个新的short[]缓冲区
				 * ShortBuffer.get(samples)-将缓冲区里short数据传输到short[]
				 */
				ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
				// 将short[]包装到ShortBuffer
				sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
				// 按通道录制shortBuffer
				try {
					System.out.println("录制音频数据...");
					recorder.recordSamples(sampleRate, numChannels, sBuff);
				} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
					// do nothing
				}
			}

			@Override
			protected void finalize() throws Throwable {
				sBuff.clear();
				sBuff = null;
				super.finalize();
			}
		};
		return crabAudio;
	}
}
