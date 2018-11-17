package UAVMonitor_Maven.history;

import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;

import javax.swing.*;

/**
 * Hello world!
 *
 */
public class PushStream_onlyImage
{
    public static void main( String[] args ) throws Exception {
        //录制的mp4文件无法播放，格式问题，录制为flv格式即可！！！
        recordCamera("D:\\recorde.flv",25);
    }

    public static void recordCamera(String outputFile, double frameRate)
            throws Exception {
        Loader.load(opencv_objdetect.class);
        String inputFile = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        //FrameGrabber grabber = FrameGrabber.createDefault(0);//本机摄像头默认0，这里使用javacv的抓取器，至于使用的是ffmpeg还是opencv，请自行查看源码
        grabber.start();//开启抓取器

        //OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();//转换器
        //opencv_core.IplImage grabbedImage = converter.convert(grabber.grab());//抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加

        //int width = grabbedImage.width();
        //int height = grabbedImage.height();
        //FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, 1);
        //FrameRecorder recorder = FrameRecorder.createDefault(outputFile, width, height);
        //recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264，编码
        //recorder.setFormat("flv");//封装格式，如果是推送到rtmp就必须是flv封装格式
        //recorder.setFrameRate(frameRate);

        //recorder.start();//开启录制器
        long startTime=0;
        long videoTS=0;

        //界面布局
        //BorderLayout borderLayout = new BorderLayout();
        //JFrame jFrame = new JFrame("UAVM system");
        //jFrame.setLayout(borderLayout);

        MyCanvasFrame myframe = new MyCanvasFrame("camera", MyCanvasFrame.getDefaultGamma() / grabber.getGamma());
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myframe.setAlwaysOnTop(true);
        //jFrame.add(frame, BorderLayout.WEST);
        //jFrame.add(new JButton("操控面板"), BorderLayout.EAST);
        //Frame rotatedFrame=converter.convert(grabbedImage);//不知道为什么这里不做转换就不能推到rtmp
        Frame rotatedFrame = null;
        while (myframe.isVisible() && (rotatedFrame = grabber.grabFrame()) != null) {//&& (grabbedImage = converter.convert(grabber.grab())) != null
            //rotatedFrame = converter.convert(grabbedImage);
            myframe.showImage(rotatedFrame);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            //videoTS = 1000 * (System.currentTimeMillis() - startTime);
            //recorder.setTimestamp(videoTS);
            //recorder.record(rotatedFrame);
            //Thread.sleep(10);
        }
        myframe.dispose();
        //recorder.stop();
        //recorder.release();
        grabber.stop();

    }
}
