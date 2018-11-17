package UAVMonitor_Maven.history;

/**
 * Created by Pengfei Jin on 2018/11/16.
 */

import UAVMonitor_Maven.FrameUtil;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_ProfileRGB;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class MyFramecopy extends JFrame {
	private KeyEventDispatcher keyEventDispatch;
	protected Canvas canvas;
	protected boolean needInitialResize;
	protected double initialScale;
	protected double inverseGamma;
	private Color color;
	private Image image;
	private BufferedImage buffer;
	private Java2DFrameConverter converter;

	public MyFramecopy(String title, double gamma) {
		super(title);
		this.keyEventDispatch = new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == 401) {
					synchronized(MyFramecopy.this) {
						MyFramecopy.this.notify();
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
				KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(MyFramecopy.this.keyEventDispatch);
				GraphicsDevice gd = MyFramecopy.this.getGraphicsConfiguration().getDevice();
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
					MyFramecopy.this.setUndecorated(true);
					MyFramecopy.this.getRootPane().setWindowDecorationStyle(0);
					MyFramecopy.this.setResizable(false);
					gd.setFullScreenWindow(MyFramecopy.this);
				} else {
					MyFramecopy.this.setLocationByPlatform(true);
				}

				if (d2 != null && !d2.equals(d)) {
					gd.setDisplayMode(d2);
				}

				double g = gamma == 0.0D ? MyFramecopy.getGamma(gd) : gamma;
				MyFramecopy.this.inverseGamma = g == 0.0D ? 1.0D : 1.0D / g;
				MyFramecopy.this.setVisible(true);
				MyFramecopy.this.initCanvas(fullScreen, displayMode, gamma);
			}
		};
		if (EventQueue.isDispatchThread()) {
			r.run();
		} else {
			try {
				EventQueue.invokeAndWait(r);
			} catch (Exception var7) {
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
					if (MyFramecopy.this.canvas.getWidth() <= 0 || MyFramecopy.this.canvas.getHeight() <= 0) {
						return;
					}

					BufferStrategy strategy = MyFramecopy.this.canvas.getBufferStrategy();

					while(true) {
						g = strategy.getDrawGraphics();
						if (MyFramecopy.this.color != null) {
							g.setColor(MyFramecopy.this.color);
							g.fillRect(0, 0, this.getWidth(), this.getHeight());
						}

						if (MyFramecopy.this.image != null) {
							g.drawImage(MyFramecopy.this.image, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
						}

						if (MyFramecopy.this.buffer != null) {
							g.drawImage(MyFramecopy.this.buffer, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
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
		this.showImage((Image)this.converter.getBufferedImage(image, Java2DFrameConverter.getBufferedImageType(image) == 0 ? 1.0D : this.inverseGamma, flipChannels, (ColorSpace)null));
	}


	public void showImage(Image image) {
		FrameUtil.initFrame(this, 1300, 560);
		if (image != null) {
			if (this.isResizable() && this.needInitialResize) {
				int w = (int)Math.round((double)image.getWidth((ImageObserver)null) * this.initialScale);
				int h = (int)Math.round((double)image.getHeight((ImageObserver)null) * this.initialScale);
				this.setCanvasSize(w, h);
				//this.canvas.setSize(w, h);
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
					MyFramecopy.this.setExtendedState(1);
					MyFramecopy.this.canvas.setSize(width, height);
					MyFramecopy.this.pack();
					MyFramecopy.this.canvas.setSize(width + 1, height + 1);
					MyFramecopy.this.canvas.setSize(width, height);
					MyFramecopy.this.needInitialResize = false;
				}
			};
			if (EventQueue.isDispatchThread()) {
				r.run();
			} else {
				try {
					EventQueue.invokeAndWait(r);
				} catch (Exception var6) {
					;
				}
			}

		}
	}

	public Dimension getCanvasSize() {
		return this.canvas.getSize();
	}
}
