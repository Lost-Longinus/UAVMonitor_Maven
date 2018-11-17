package UAVMonitor_Maven;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FrameUtil {

	
	public static void initFrame(JFrame frame,int width , int height){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension d = toolkit.getScreenSize();
		
		int x = (int) d.getWidth();
		int y = (int) d.getHeight();
		
		frame.setBounds((x-width)/2, (y-height)/2, width, height);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void addControlPanel(Frame frame){
		int x = 980, y=580;
		int w = 80, h = 80;
		int gap = 10;
		//设置图标大小
		ImageIcon Nicon = new ImageIcon("D:\\Github\\UAVMonitor_Maven\\src\\main\\java\\sources\\N.png");
		Nicon = new ImageIcon(Nicon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
		ImageIcon Sicon = new ImageIcon("D:\\Github\\UAVMonitor_Maven\\src\\main\\java\\sources\\S.png");
		Sicon = new ImageIcon(Sicon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
		ImageIcon Wicon = new ImageIcon("D:\\Github\\UAVMonitor_Maven\\src\\main\\java\\sources\\W.png");
		Wicon = new ImageIcon(Wicon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
		ImageIcon Eicon = new ImageIcon("D:\\Github\\UAVMonitor_Maven\\src\\main\\java\\sources\\E.png");
		Eicon = new ImageIcon(Eicon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
		ImageIcon Cicon = new ImageIcon("D:\\Github\\UAVMonitor_Maven\\src\\main\\java\\sources\\C.png");
		Cicon = new ImageIcon(Cicon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));

		JButton NjButton = new JButton(Nicon);
		NjButton.setBounds(x + 20 + w + gap, y / 2 - 3 * w / 2 - gap, w, h);
		NjButton.setBorderPainted(false);
		frame.add(NjButton);
		JButton SjButton = new JButton(Sicon);
		SjButton.setBounds(x + 20 + w + gap, y / 2 + w / 2 + gap, w, h);
		SjButton.setBorderPainted(false);
		frame.add(SjButton);
		JButton EjButton = new JButton(Wicon);
		EjButton.setBounds(x + 20, y / 2 - w / 2, w, h);
		EjButton.setBorderPainted(false);
		frame.add(EjButton);
		JButton WjButton = new JButton(Eicon);
		WjButton.setBounds(x + 20 + 2 * w + 2 * gap, y / 2 - w / 2, w, h);
		WjButton.setBorderPainted(false);
		frame.add(WjButton);
		JButton CjButton = new JButton(Cicon);
		CjButton.setBounds(x + 20 + w + gap, y / 2 - w / 2, w, h);
		CjButton.setBorderPainted(false);
		frame.add(CjButton);

	}
	
}
