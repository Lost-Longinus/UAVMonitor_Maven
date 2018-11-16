package UAVMonitor_Maven;

/**
 * Created by Pengfei Jin on 2018/11/16.
 */
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_ProfileRGB;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import javax.swing.JFrame;
public class MyFrame extends JFrame {
	public static MyFrame global = null;
	public static final long DEFAULT_LATENCY = 200L;
	private long latency;
	private KeyEvent keyEvent;
	private KeyEventDispatcher keyEventDispatch;
	protected Canvas canvas;
	protected boolean needInitialResize;
	protected double initialScale;
	protected double inverseGamma;
	private Color color;
	private Image image;
	private BufferedImage buffer;
	private Java2DFrameConverter converter;

	public MyFrame(String title, double gamma) {
		super(title);
		this.latency = 200L;
		this.keyEvent = null;
		this.keyEventDispatch = new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == 401) {
					MyFrame var2 = MyFrame.this;
					synchronized(MyFrame.this) {
						MyFrame.this.keyEvent = e;
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
		this.init(false, (DisplayMode)null, gamma);
	}

	private void init(final boolean fullScreen, final DisplayMode displayMode, final double gamma) {
		Runnable r = new Runnable() {
			public void run() {
				KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(MyFrame.this.keyEventDispatch);
				GraphicsDevice gd = MyFrame.this.getGraphicsConfiguration().getDevice();
				DisplayMode d = gd.getDisplayMode();
				DisplayMode d2 = null;
				if (displayMode != null && d != null) {
					int w = displayMode.getWidth();
					int h = displayMode.getHeight();
					int b = displayMode.getBitDepth();
					int r = displayMode.getRefreshRate();
					d2 = new DisplayMode(w > 0 ? w : d.getWidth(), h > 0 ? h : d.getHeight(),
							b > 0 ? b : d.getBitDepth(), r > 0 ? r : d.getRefreshRate());
				}

				if (fullScreen) {
					MyFrame.this.setUndecorated(true);
					MyFrame.this.getRootPane().setWindowDecorationStyle(0);
					MyFrame.this.setResizable(false);
					gd.setFullScreenWindow(MyFrame.this);
				} else {
					MyFrame.this.setLocationByPlatform(true);
				}

				if (d2 != null && !d2.equals(d)) {
					gd.setDisplayMode(d2);
				}

				double g = gamma == 0.0D ? MyFrame.getGamma(gd) : gamma;
				MyFrame.this.inverseGamma = g == 0.0D ? 1.0D : 1.0D / g;
				MyFrame.this.setVisible(true);
				MyFrame.this.initCanvas(fullScreen, displayMode, gamma);
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

	public static double getGamma(GraphicsDevice screen) {
		ColorSpace cs = screen.getDefaultConfiguration().getColorModel().getColorSpace();
		if (cs.isCS_sRGB()) {
			return 2.2D;
		} else {
			try {
				return (double)((ICC_ProfileRGB)((ICC_ColorSpace)cs).getProfile()).getGamma(0);
			} catch (RuntimeException var3) {
				return 0.0D;
			}
		}
	}

	protected void initCanvas(boolean fullScreen, DisplayMode displayMode, double gamma) {
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
		if (fullScreen) {
			this.canvas.setSize(this.getSize());
			this.needInitialResize = false;
		} else {
			this.canvas.setSize(10, 10);
			this.needInitialResize = true;
		}

		this.getContentPane().add(this.canvas);
		this.canvas.setVisible(true);
		this.canvas.createBufferStrategy(2);
	}

	public void showImage(Frame image) {
		this.showImage(image, false);
	}

	public void showImage(Frame image, boolean flipChannels) {
		Java2DFrameConverter var10003 = this.converter;
		this.showImage((Image)this.converter.getBufferedImage(image, Java2DFrameConverter.getBufferedImageType(image) == 0 ? 1.0D : this.inverseGamma, flipChannels, (ColorSpace)null));
	}


	public void showImage(Image image) {
		if (image != null) {
			if (this.isResizable() && this.needInitialResize) {
				int w = (int)Math.round((double)image.getWidth((ImageObserver)null) * this.initialScale);
				int h = (int)Math.round((double)image.getHeight((ImageObserver)null) * this.initialScale);
				this.setCanvasSize(w, h);
			}

			this.color = null;
			this.image = image;
			this.canvas.paint((Graphics)null);
		}
	}

	public void setCanvasSize(final int width, final int height) {
		Dimension d = this.getCanvasSize();
		if (d.width != width || d.height != height) {
			Runnable r = new Runnable() {
				public void run() {
					MyFrame.this.setExtendedState(0);
					MyFrame.this.canvas.setSize(width, height);
					MyFrame.this.pack();
					MyFrame.this.canvas.setSize(width + 1, height + 1);
					MyFrame.this.canvas.setSize(width, height);
					MyFrame.this.needInitialResize = false;
				}
			};
			if (EventQueue.isDispatchThread()) {
				r.run();
			} else {
				try {
					EventQueue.invokeAndWait(r);
				} catch (java.lang.Exception var6) {
					;
				}
			}

		}
	}

	public Dimension getCanvasSize() {
		return this.canvas.getSize();
	}
}
