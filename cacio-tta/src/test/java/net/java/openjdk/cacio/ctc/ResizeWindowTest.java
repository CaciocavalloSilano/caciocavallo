package net.java.openjdk.cacio.ctc;

import static org.fest.assertions.Assertions.assertThat;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.java.openjdk.cacio.ctc.junit.CacioFESTRunner;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.core.BasicRobot;
import org.fest.swing.core.Robot;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.edt.GuiTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(value = CacioFESTRunner.class)
public class ResizeWindowTest {

	private DraggablePopupMenu popupMenu;
	protected int action;
	private Robot robot;

	@Before
	public void setUp() {
		this.popupMenu = GuiActionRunner
				.execute(new GuiQuery<DraggablePopupMenu>() {
					@Override
					protected DraggablePopupMenu executeInEDT()
							throws Throwable {
						DraggablePopupMenu menu = new DraggablePopupMenu(true);
						menu.setSize(100, 100);
						Box panel = new Box(BoxLayout.Y_AXIS);
						panel.add(createMenuItem("one"));
						panel.add(createMenuItem("two"));
						panel.add(createMenuItem("three"));
						panel.setBorder(BorderFactory.createEmptyBorder(0, 0,
								10, 0));
						menu.add(panel);
						return menu;
					}
				});
		robot = BasicRobot.robotWithNewAwtHierarchy();
		robot.settings().delayBetweenEvents(50);
		robot.settings().eventPostingDelay(50);
	}

	@After
	public void tearDown() throws Exception {
		robot.cleanUp();
	}

	public JMenuItem createMenuItem(final String name) {
		return GuiActionRunner.execute(new GuiQuery<JMenuItem>() {
			@Override
			protected JMenuItem executeInEDT() throws Throwable {
				JMenuItem item = new JMenuItem();
				item.setName(name);
				item.setText(name + "<- text");
				return item;
			}
		});
	}

	@Ignore
	@Test
	@GUITest
	public void resize() {
		GuiActionRunner.execute(new GuiTask() {
			@Override
			protected void executeInEDT() throws Throwable {
				popupMenu.show(null, 50, 50);
			}
		});

		Dimension oldSize = this.popupMenu.getSize();

		robot.pressMouse(
				this.popupMenu,
				new Point(this.popupMenu.getWidth() - 5, this.popupMenu
						.getHeight() - 5));
		robot.moveMouse(
				this.popupMenu,
				new Point(this.popupMenu.getWidth() + 20, this.popupMenu
						.getHeight() + 20));
		robot.releaseMouseButtons();

		Dimension newSize = this.popupMenu.getSize();

		assertThat(oldSize.height).isLessThan(newSize.height);
		assertThat(oldSize.width).isLessThan(newSize.width);
	}

	// ---------------------- DraggablePopupMenu-class --------------------------------
	
	public class DraggablePopupMenu extends JPopupMenu {
		/** generated serial version UID */
		private static final long serialVersionUID = 9039094937240906150L;

		private static final int DOT_SIZE = 2;

		private static final int DOT_START = 2;

		private static final int DOT_STEP = 4;

		private final boolean resizable;

		/**
		 * Default constructor
		 */
		public DraggablePopupMenu() {
			this(false);
		}

		/**
		 * @param resizable
		 *            whether this pop-up should include a draggable resizer in
		 *            the bottom right corner
		 */
		public DraggablePopupMenu(final boolean resizable) {
			super();
			this.resizable = resizable;
			if (resizable)
				new PopupMenuResizer(this);
		}

		@Override
		public void paintChildren(Graphics g) {
			super.paintChildren(g);
			if (this.resizable)
				drawResizer(g);
		}

		private void drawResizer(Graphics g) {
			int x = getWidth() - 2;
			int y = getHeight() - 2;
			Graphics g2 = g.create();
			try {
				for (int dy = DOT_START, j = 2; j > 0; j--, dy += DOT_STEP) {
					for (int dx = DOT_START, i = 0; i < j; i++, dx += DOT_STEP) {
						drawDot(g2, x - dx, y - dy);
					}
				}
			} finally {
				g2.dispose();
			}
		}

		private void drawDot(Graphics g, int x, int y) {
			// TODO L&F BG + FG
			g.setColor(Color.WHITE);
			g.fillRect(x, y, DOT_SIZE, DOT_SIZE);
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(x - 1, y - 1, DOT_SIZE, DOT_SIZE);
		}

	}

	final class PopupMenuResizer extends MouseAdapter {

		private final JPopupMenu menu;

		private static final int REZSIZE_SPOT_SIZE = 10;

		private Point mouseStart = new Point(Integer.MIN_VALUE,
				Integer.MIN_VALUE);

		private Dimension startSize;

		private boolean isResizing = false;

		public PopupMenuResizer(JPopupMenu menu) {
			this.menu = menu;
			this.menu.setLightWeightPopupEnabled(true);
			menu.addMouseListener(this);
			menu.addMouseMotionListener(this);
		}

		private boolean isInResizeSpot(Point point) {
			if (point == null)
				return false;
			Rectangle resizeSpot = new Rectangle(this.menu.getWidth()
					- REZSIZE_SPOT_SIZE, this.menu.getHeight()
					- REZSIZE_SPOT_SIZE, REZSIZE_SPOT_SIZE, REZSIZE_SPOT_SIZE);
			return resizeSpot.contains(point);

		}

		@Override
		public void mouseMoved(MouseEvent e) {
			this.menu.setCursor(Cursor.getPredefinedCursor(isInResizeSpot(e
					.getPoint()) ? Cursor.SE_RESIZE_CURSOR
					: Cursor.DEFAULT_CURSOR));
		}

		private Point toScreen(MouseEvent e) {
			Point p = e.getPoint();
			SwingUtilities.convertPointToScreen(p, e.getComponent());
			return p;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			this.mouseStart = toScreen(e);
			this.startSize = this.menu.getSize();
			this.isResizing = isInResizeSpot(e.getPoint());
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			this.mouseStart = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
			this.isResizing = false;
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!this.isResizing)
				return;
			Point p = toScreen(e);
			int dx = p.x - this.mouseStart.x;
			int dy = p.y - this.mouseStart.y;
			Dimension minDim = this.menu.getMinimumSize();
			Dimension newDim = new Dimension(this.startSize.width + dx,
					this.startSize.height + dy);
			if (newDim.width >= minDim.width && newDim.height >= minDim.height) {
				this.menu.setPopupSize(newDim);
			}
		}
	}
}
