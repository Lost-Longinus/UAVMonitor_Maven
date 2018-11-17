package UAVMonitor_Maven;

/**
 * Created by Pengfei Jin on 2018/11/16.
 */
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class MyFrame extends JFrame {
	private KeyEventDispatcher keyEventDispatch;
	protected Canvas canvas;
	protected boolean needInitialResize;
	protected double initialScale;
	protected double inverseGamma;
	private Color color;
	private Image image;
	private BufferedImage buffer;
	private Java2DFrameConverter converter;

	public MyFrame(String title) {
		super(title);
		this.keyEventDispatch = new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == 401) {
					synchronized(MyFrame.this) {
						MyFrame.this.notify();
					}
				}

				return false;
			}
		};
		this.canvas = null;
		this.needInitialResize = false;
		this.initialScale = 0.5D;
		this.inverseGamma = 1.0D;
		this.color = null;
		this.image = null;
		this.buffer = null;
		this.converter = new Java2DFrameConverter();
		this.init();
	}

	private void init() {
		Runnable r = new Runnable() {
			public void run() {
				KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(MyFrame.this.keyEventDispatch);
				MyFrame.this.setLocationByPlatform(true);
				MyFrame.this.setVisible(true);
				MyFrame.this.initCanvas();
			}
		};
		if (EventQueue.isDispatchThread()) {
			r.run();
		} else {
			try {
				EventQueue.invokeAndWait(r);
			} catch (java.lang.Exception var7) {
				;
			}
		}

	}


	protected void initCanvas() {
		this.canvas = new Canvas() {
			public void update(Graphics g) {
				this.paint(g);
			}

			public void paint(Graphics g) {
				try {
					if (MyFrame.this.canvas.getWidth() <= 0 || MyFrame.this.canvas.getHeight() <= 0) {
						return;
					}

					BufferStrategy strategy = MyFrame.this.canvas.getBufferStrategy();

					while(true) {
						g = strategy.getDrawGraphics();
						if (MyFrame.this.color != null) {
							g.setColor(MyFrame.this.color);
							g.fillRect(0, 0, this.getWidth(), this.getHeight());
						}

						if (MyFrame.this.image != null) {
							g.drawImage(MyFrame.this.image, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
						}

						if (MyFrame.this.buffer != null) {
							g.drawImage(MyFrame.this.buffer, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
						}

						g.dispose();
						if (!strategy.contentsRestored()) {
							strategy.show();
							if (!strategy.contentsLost()) {
								break;
							}
						}
					}
				} catch (NullPointerException var3) {
					;
				} catch (IllegalStateException var4) {
					;
				}

			}
		};
		this.canvas.setSize(10, 10);
		this.needInitialResize = true;

		this.getContentPane().add(this.canvas);
		this.canvas.setVisible(true);
		this.canvas.createBufferStrategy(2);
	}

	public void showImage(Frame image) {
		this.showImage(image, false);
	}

	public void showImage(Frame image, boolean flipChannels) {
		this.showImage((Image)this.converter.getBufferedImage(image, Java2DFrameConverter.getBufferedImageType(image) == 0 ? 1.0D : this.inverseGamma, flipChannels, (ColorSpace)null));
	}


	public void showImage(Image image) {
		if (image != null) {
			int w = (int)Math.round((double)image.getWidth((ImageObserver)null) * this.initialScale);
			int h = (int)Math.round((double)image.getHeight((ImageObserver)null) * this.initialScale);
			this.canvas.setSize(w, h);
			this.color = null;
			this.image = image;
			this.canvas.paint((Graphics)null);
		}
	}
}
