package UAVMonitor_Maven;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import javax.sound.sampled.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 * Hello world!
 *
 */
public class App {
    private AudioFormat af = null;
    private SourceDataLine sourceDataLine;
    private DataLine.Info dataLineInfo;
    Buffer[] buf;
    ShortBuffer ILData;
    ByteBuffer TLData;
    float vol = 1;//音量
    byte[] tl;

    public void getAudio(String audioPath) throws FrameGrabber.Exception, LineUnavailableException {

        System.out.println("START...");
        FFmpegFrameGrabber fg = new FFmpegFrameGrabber(audioPath);
        //System.out.println("音频： "+fg.hasAudio()+" 视频： "+fg.hasVideo());
        Frame f;
        fg.start();
        //System.out.println("TimeStamp is "+fg.getTimestamp());
        int sampleFormat = fg.getSampleFormat();
        //System.out.println("audio format is "+sampleFormat);
        initSourceDataLine(fg);
        while(true){
            f = fg.grabFrame();
            if(f.samples != null){
                processAudio(f.samples);
            }
        }
    }
    private void initSourceDataLine(FFmpegFrameGrabber fg) throws LineUnavailableException {
        //System.out.println("SampleRate : "+fg.getSampleRate());
        //System.out.println("AudioChannels : "+fg.getAudioChannels());
        af = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                fg.getSampleRate()/2,16,2,
                2*2,fg.getSampleRate(),true);
        dataLineInfo = new DataLine.Info(SourceDataLine.class,
                af, AudioSystem.NOT_SPECIFIED);

            sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(af);
            sourceDataLine.start();
    }
    public void processAudio(Buffer[] samples) {
        buf = samples;
        ILData = (ShortBuffer)buf[0];
        TLData = shortToByteValue(ILData,vol);
        tl = TLData.array();
        //System.out.println("length of samples is "+tl.length);
        sourceDataLine.write(tl,0,tl.length);
        //System.out.println("audio is running...");
    }
    public static ByteBuffer shortToByteValue(ShortBuffer arr,float vol) {
        int len  = arr.capacity();
        ByteBuffer bb = ByteBuffer.allocate(len / 3);
        for(int i = 0;i<len/6;i++){
            short arrtemp = (short)((float)arr.get(i*6)*vol);
            bb.putShort(i*2,arrtemp);
        }
        /*ByteBuffer bb = ByteBuffer.allocate(len * 2);
        for(int i = 0;i<len;i++){
            short arrtemp = (short)((float)arr.get(i)*vol);
            bb.putShort(i*2,arrtemp);
        }*/
        return bb; // 默认转为大端序
    }
}
