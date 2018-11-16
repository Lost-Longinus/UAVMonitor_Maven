package UAVMonitor_Maven;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

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

public class MyCanvasFrame extends JFrame {
	public static MyCanvasFrame global = null;
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


	public static String[] getScreenDescriptions() {
		GraphicsDevice[] screens = getScreenDevices();
		String[] descriptions = new String[screens.length];

		for(int i = 0; i < screens.length; ++i) {
			descriptions[i] = screens[i].getIDstring();
		}

		return descriptions;
	}

	public static DisplayMode getDisplayMode(int screenNumber) {
		GraphicsDevice[] screens = getScreenDevices();
		return screenNumber >= 0 && screenNumber < screens.length ? screens[screenNumber].getDisplayMode() : null;
	}

	public static double getGamma(int screenNumber) {
		GraphicsDevice[] screens = getScreenDevices();
		return screenNumber >= 0 && screenNumber < screens.length ? getGamma(screens[screenNumber]) : 0.0D;
	}

	public static double getDefaultGamma() {
		return getGamma(getDefaultScreenDevice());
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

	public static GraphicsDevice getScreenDevice(int screenNumber) throws MyCanvasFrame.Exception {
		GraphicsDevice[] screens = getScreenDevices();
		if (screenNumber >= screens.length) {
			throw new MyCanvasFrame.Exception("CanvasFrame Error: Screen number " + screenNumber + " not found. There are only " + screens.length + " screens.");
		} else {
			return screens[screenNumber];
		}
	}

	public static GraphicsDevice[] getScreenDevices() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
	}

	public static GraphicsDevice getDefaultScreenDevice() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}

	public MyCanvasFrame(String title) {
		this(title, 0.0D);
	}
	//需要用到的构造器
	public MyCanvasFrame(String title, double gamma) {
		super(title);
		this.latency = 200L;
		this.keyEvent = null;
		this.keyEventDispatch = new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == 401) {
					MyCanvasFrame var2 = MyCanvasFrame.this;
					synchronized(MyCanvasFrame.this) {
						MyCanvasFrame.this.keyEvent = e;
						MyCanvasFrame.this.notify();
					}
				}

				return false;
			}
		};
		this.setBounds(0,0,1260,540);
		this.setResizable(false);
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

	public MyCanvasFrame(String title, GraphicsConfiguration gc) {
		this(title, gc, 0.0D);
	}

	public MyCanvasFrame(String title, GraphicsConfiguration gc, double gamma) {
		super(title, gc);
		this.latency = 200L;
		this.keyEvent = null;
		this.keyEventDispatch = new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == 401) {
					MyCanvasFrame var2 = MyCanvasFrame.this;
					synchronized(MyCanvasFrame.this) {
						MyCanvasFrame.this.keyEvent = e;
						MyCanvasFrame.this.notify();
					}
				}

				return false;
			}
		};
		this.canvas = null;
		this.needInitialResize = false;
		this.initialScale = 1.0D;
		this.inverseGamma = 1.0D;
		this.color = null;
		this.image = null;
		this.buffer = null;
		this.converter = new Java2DFrameConverter();
		this.init(false, (DisplayMode)null, gamma);
	}

	public MyCanvasFrame(String title, int screenNumber, DisplayMode displayMode) throws MyCanvasFrame.Exception {
		this(title, screenNumber, displayMode, 0.0D);
	}

	public MyCanvasFrame(String title, int screenNumber, DisplayMode displayMode, double gamma) throws MyCanvasFrame.Exception {
		super(title, getScreenDevice(screenNumber).getDefaultConfiguration());
		this.latency = 200L;
		this.keyEvent = null;
		this.keyEventDispatch = new KeyEventDispatcher() {
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getID() == 401) {
					MyCanvasFrame var2 = MyCanvasFrame.this;
					synchronized(MyCanvasFrame.this) {
						MyCanvasFrame.this.keyEvent = e;
						MyCanvasFrame.this.notify();
					}
				}

				return false;
			}
		};
		this.canvas = null;
		this.needInitialResize = false;
		this.initialScale = 1.0D;
		this.inverseGamma = 1.0D;
		this.color = null;
		this.image = null;
		this.buffer = null;
		this.converter = new Java2DFrameConverter();
		this.init(true, displayMode, gamma);
	}

	private void init(final boolean fullScreen, final DisplayMode displayMode, final double gamma) {
		Runnable r = new Runnable() {
			public void run() {
				KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(MyCanvasFrame.this.keyEventDispatch);
				GraphicsDevice gd = MyCanvasFrame.this.getGraphicsConfiguration().getDevice();
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
					MyCanvasFrame.this.setUndecorated(true);
					MyCanvasFrame.this.getRootPane().setWindowDecorationStyle(0);
					MyCanvasFrame.this.setResizable(false);
					gd.setFullScreenWindow(MyCanvasFrame.this);
				} else {
					MyCanvasFrame.this.setLocationByPlatform(true);
				}

				if (d2 != null && !d2.equals(d)) {
					gd.setDisplayMode(d2);
				}

				double g = gamma == 0.0D ? MyCanvasFrame.getGamma(gd) : gamma;
				MyCanvasFrame.this.inverseGamma = g == 0.0D ? 1.0D : 1.0D / g;
				MyCanvasFrame.this.setVisible(true);
				MyCanvasFrame.this.initCanvas(fullScreen, displayMode, gamma);
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

	protected void initCanvas(boolean fullScreen, DisplayMode displayMode, double gamma) {
		this.canvas = new Canvas() {
			public void update(Graphics g) {
				this.paint(g);
			}

			public void paint(Graphics g) {
				try {
					if (MyCanvasFrame.this.canvas.getWidth() <= 0 || MyCanvasFrame.this.canvas.getHeight() <= 0) {
						return;
					}

					BufferStrategy strategy = MyCanvasFrame.this.canvas.getBufferStrategy();

					while(true) {
						g = strategy.getDrawGraphics();
						if (MyCanvasFrame.this.color != null) {
							g.setColor(MyCanvasFrame.this.color);
							g.fillRect(0, 0, this.getWidth(), this.getHeight());
						}

						if (MyCanvasFrame.this.image != null) {
							g.drawImage(MyCanvasFrame.this.image, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
						}

						if (MyCanvasFrame.this.buffer != null) {
							g.drawImage(MyCanvasFrame.this.buffer, 0, 0, this.getWidth(), this.getHeight(), (ImageObserver)null);
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

	public long getLatency() {
		return this.latency;
	}

	public void setLatency(long latency) {
		this.latency = latency;
	}

	public void waitLatency() throws InterruptedException {
		Thread.sleep(this.getLatency());
	}

	public KeyEvent waitKey() throws InterruptedException {
		return this.waitKey(0);
	}

	public synchronized KeyEvent waitKey(int delay) throws InterruptedException {
		if (delay >= 0) {
			this.keyEvent = null;
			this.wait((long)delay);
		}

		KeyEvent e = this.keyEvent;
		this.keyEvent = null;
		return e;
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	public Dimension getCanvasSize() {
		return this.canvas.getSize();
	}

	public void setCanvasSize(final int width, final int height) {
		Dimension d = this.getCanvasSize();
		if (d.width != width || d.height != height) {
			Runnable r = new Runnable() {
				public void run() {
					MyCanvasFrame.this.setExtendedState(0);
					MyCanvasFrame.this.canvas.setSize(width, height);
					MyCanvasFrame.this.pack();
					MyCanvasFrame.this.canvas.setSize(width + 1, height + 1);
					MyCanvasFrame.this.canvas.setSize(width, height);
					MyCanvasFrame.this.needInitialResize = false;
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

	public double getCanvasScale() {
		return this.initialScale;
	}

	public void setCanvasScale(double initialScale) {
		this.initialScale = initialScale;
		this.needInitialResize = true;
	}

	public Graphics2D createGraphics() {
		if (this.buffer == null || this.buffer.getWidth() != this.canvas.getWidth() || this.buffer.getHeight() != this.canvas.getHeight()) {
			BufferedImage newbuffer = this.canvas.getGraphicsConfiguration().createCompatibleImage(this.canvas.getWidth(), this.canvas.getHeight(), 3);
			if (this.buffer != null) {
				Graphics g = newbuffer.getGraphics();
				g.drawImage(this.buffer, 0, 0, (ImageObserver)null);
				g.dispose();
			}

			this.buffer = newbuffer;
		}

		return this.buffer.createGraphics();
	}

	public void releaseGraphics(Graphics2D g) {
		g.dispose();
		this.canvas.paint((Graphics)null);
	}

	public void showColor(Color color) {
		this.color = color;
		this.image = null;
		this.canvas.paint((Graphics)null);
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

	public static void tile(final MyCanvasFrame[] frames) {
		class MovedListener extends ComponentAdapter {
			boolean moved = false;

			MovedListener() {
			}

			public void componentMoved(ComponentEvent e) {
				this.moved = true;
				Component c = e.getComponent();
				synchronized(c) {
					c.notify();
				}
			}
		}

		final MovedListener movedListener = new MovedListener();
		int canvasCols = (int)Math.round(Math.sqrt((double)frames.length));
		if (canvasCols * canvasCols < frames.length) {
			++canvasCols;
		}

		int canvasX = 0;
		int canvasY = 0;
		int canvasMaxY = 0;

		for(int i = 0; i < frames.length; ++i) {
			final int n = i;
			final int x = canvasX;
			final int y = canvasY;

			try {
				movedListener.moved = false;
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						frames[n].addComponentListener(movedListener);
						frames[n].setLocation(x, y);
					}
				});

				for(int count = 0; !movedListener.moved && count < 5; ++count) {
					synchronized(frames[n]) {
						frames[n].wait(100L);
					}
				}

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						frames[n].removeComponentListener(movedListener);
					}
				});
			} catch (java.lang.Exception var14) {
				;
			}

			canvasX = frames[i].getX() + frames[i].getWidth();
			canvasMaxY = Math.max(canvasMaxY, frames[i].getY() + frames[i].getHeight());
			if ((i + 1) % canvasCols == 0) {
				canvasX = 0;
				canvasY = canvasMaxY;
			}
		}

	}

	public static class Exception extends java.lang.Exception {
		public Exception(String message) {
			super(message);
		}

		public Exception(String message, Throwable cause) {
			super(message, cause);
		}
	}


	//以上为CanvasFrame原始代码
	//-----------------------------------分割线-----------------------------------------------
	//以下为新添加或修改代码


}
