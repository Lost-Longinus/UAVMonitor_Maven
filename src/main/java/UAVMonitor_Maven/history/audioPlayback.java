package UAVMonitor_Maven.history;

import org.bytedeco.javacpp.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import javax.sound.sampled.*;
import javax.swing.*;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

/**
 * 播放视频里面的音频
 */
public class audioPlayback {
	private AudioFormat af = null;
	private SourceDataLine sourceDataLine;
	private DataLine.Info dataLineInfo;
	Buffer[] buf;
	FloatBuffer leftData,rightData;
	ShortBuffer ILData,IRData;
	ByteBuffer TLData,TRData;
	float vol = 1;//音量
	int sampleFormat;
	byte[] tl,tr;
	byte[] combine;
	public audioPlayback(String musicPath) throws FrameGrabber.Exception {
		FFmpegFrameGrabber fg = new FFmpegFrameGrabber(musicPath);
		int sec = 60;
		Frame f;
		fg.start();
		//fg.setTimestamp(sec*1000000);//纯音频文件设置了时间戳有问题，视频没问题。
		sampleFormat = fg.getSampleFormat();
		System.out.println(sampleFormat);
		printMusicInfo(fg);
		initSourceDataLine(fg);
		while(true){
			f = fg.grabSamples();
			if(f == null){
				fg.stop();
				System.exit(0);
			}
			processAudio(f.samples);
		}
	}
	public void processAudio(Buffer[] samples) {
		int k;
		buf = samples;
		switch(sampleFormat){
			case avutil.AV_SAMPLE_FMT_FLTP://平面型左右声道分开。
				leftData = (FloatBuffer)buf[0];
				TLData = floatToByteValue(leftData,vol);
				rightData = (FloatBuffer)buf[1];
				TRData = floatToByteValue(leftData,vol);
				tl = TLData.array();
				tr = TRData.array();
				combine = new byte[tl.length+tr.length];
				k = 0;
				for(int i=0;i<tl.length;i=i+2) {//混合两个声道。
					for (int j = 0; j < 2; j++) {
						combine[j+4*k] = tl[i + j];
						combine[j + 2+4*k] = tr[i + j];
					}
					k++;
				}
				sourceDataLine.write(combine,0,combine.length);
				break;
			case avutil.AV_SAMPLE_FMT_S16://非平面型左右声道在一个buffer中。
				ILData = (ShortBuffer)buf[0];
				TLData = shortToByteValue(ILData,vol);
				tl = TLData.array();
				sourceDataLine.write(tl,0,tl.length);
				break;
			case avutil.AV_SAMPLE_FMT_FLT://float非平面型
				leftData = (FloatBuffer)buf[0];
				TLData = floatToByteValue(leftData,vol);
				tl = TLData.array();
				sourceDataLine.write(tl,0,tl.length);
				break;
			case avutil.AV_SAMPLE_FMT_S16P://平面型左右声道分开
				ILData = (ShortBuffer)buf[0];
				IRData = (ShortBuffer)buf[1];
				TLData = shortToByteValue(ILData,vol);
				TRData = shortToByteValue(IRData,vol);
				tl = TLData.array();
				tr = TRData.array();
				combine = new byte[tl.length+tr.length];
				k = 0;
				for(int i=0;i<tl.length;i=i+2) {
					for (int j = 0; j < 2; j++) {
						combine[j+4*k] = tl[i + j];
						combine[j + 2+4*k] = tr[i + j];
					}
					k++;
				}
				sourceDataLine.write(combine,0,combine.length);
				break;
			default:
				JOptionPane.showMessageDialog(null,"unsupport audio format","unsupport audio format",JOptionPane.ERROR_MESSAGE);
				System.exit(0);
				break;
		}
	}

	private void initSourceDataLine(FFmpegFrameGrabber fg) {
		switch(fg.getSampleFormat()){
			case avutil.AV_SAMPLE_FMT_U8://无符号short 8bit
				break;
			case avutil.AV_SAMPLE_FMT_S16://有符号short 16bit
				af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,fg.getSampleRate(),16,fg.getAudioChannels(),fg.getAudioChannels()*2,fg.getSampleRate(),true);
				break;
			case avutil.AV_SAMPLE_FMT_S32:
				break;
			case avutil.AV_SAMPLE_FMT_FLT:
				af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,fg.getSampleRate(),16,fg.getAudioChannels(),fg.getAudioChannels()*2,fg.getSampleRate(),true);
				break;
			case avutil.AV_SAMPLE_FMT_DBL:
				break;
			case avutil.AV_SAMPLE_FMT_U8P:
				break;
			case avutil.AV_SAMPLE_FMT_S16P://有符号short 16bit,平面型
				af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,fg.getSampleRate(),16,fg.getAudioChannels(),fg.getAudioChannels()*2,fg.getSampleRate(),true);
				break;
			case avutil.AV_SAMPLE_FMT_S32P://有符号short 32bit，平面型，但是32bit的话可能电脑声卡不支持，这种音乐也少见
				af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,fg.getSampleRate(),32,fg.getAudioChannels(),fg.getAudioChannels()*2,fg.getSampleRate(),true);
				break;
			case avutil.AV_SAMPLE_FMT_FLTP://float 平面型 需转为16bit short
				af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,fg.getSampleRate(),16,fg.getAudioChannels(),fg.getAudioChannels()*2,fg.getSampleRate(),true);
				break;
			case avutil.AV_SAMPLE_FMT_DBLP:
				break;
			case avutil.AV_SAMPLE_FMT_S64://有符号short 64bit 非平面型
				break;
			case avutil.AV_SAMPLE_FMT_S64P://有符号short 64bit平面型
				break;
			default:
				System.out.println("不支持的音乐格式");
				System.exit(0);
		}
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
	public static ByteBuffer shortToByteValue(ShortBuffer arr,float vol) {
		int len  = arr.capacity();
		ByteBuffer bb = ByteBuffer.allocate(len * 2);
		for(int i = 0;i<len;i++){
			bb.putShort(i*2,(short)((float)arr.get(i)*vol));
		}
		return bb; // 默认转为大端序
	}
	public static ByteBuffer floatToByteValue(FloatBuffer arr,float vol){
		int len = arr.capacity();
		float f;
		float v;
		ByteBuffer res = ByteBuffer.allocate(len*2);
		v = 32768.0f*vol;
		for(int i=0;i<len;i++){
			f = arr.get(i)*v;//Ref：https://stackoverflow.com/questions/15087668/how-to-convert-pcm-samples-in-byte-array-as-floating-point-numbers-in-the-range
			if(f>v) f = v;
			if(f<-v) f = v;
			//默认转为大端序
			res.putShort(i*2,(short)f);//注意乘以2，因为一次写入两个字节。
		}
		return res;
	}
	private void printMusicInfo(FFmpegFrameGrabber fg) {
		System.out.println("音频采样率"+fg.getSampleRate());
		System.out.println("音频通道数"+fg.getAudioChannels());
	}
}

