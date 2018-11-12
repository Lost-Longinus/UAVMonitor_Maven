package UAVMonitor_Maven;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

//��ʼ������Ĺ�����
public class FrameUtil {

	
	public static void initFrame(JFrame frame,int width , int height){
		Toolkit toolkit = Toolkit.getDefaultToolkit(); //��ȡһ����ϵͳ��ع��������
		//��ȡ��Ļ�ķֱ���
		Dimension d = toolkit.getScreenSize();
		
		int x = (int) d.getWidth();
		int y = (int) d.getHeight();
		
		frame.setBounds((x-width)/2, (y-height)/2, width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
}
