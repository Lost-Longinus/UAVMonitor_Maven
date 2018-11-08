package UAVMonitor_Maven;

import org.bytedeco.javacv.*;
import org.junit.Test;

import javax.swing.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {
        AppTest appTest = new AppTest();
        appTest.testCamera();
    }
    public void testCamera() throws InterruptedException, FrameGrabber.Exception {
        /*以下源测试可用
        CCTV1高清：http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8
        CCTV3高清：http://ivi.bupt.edu.cn/hls/cctv3hd.m3u8
        CCTV5高清：http://ivi.bupt.edu.cn/hls/cctv5hd.m3u8
        CCTV5+高清：http://ivi.bupt.edu.cn/hls/cctv5phd.m3u8
        CCTV6高清：http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8
        苹果提供的测试源（点播）：http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/gear2/prog_index.m3u8
        */

        String inputFile = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        //OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();   //开始获取摄像头数据
        CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
        canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);
        while (true) {
            if (!canvas.isDisplayable()) {//窗口是否关闭
                grabber.stop();//停止抓取
                System.exit(-1);//退出
            }

            Frame frame = grabber.grab();

            canvas.showImage(frame);//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像
            Thread.sleep(10);//50毫秒刷新一次图像
        }
    }
}
