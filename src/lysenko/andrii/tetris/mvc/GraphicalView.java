package lysenko.andrii.tetris.mvc;


import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import static java.awt.Color.*;
import java.awt.Graphics;
import java.awt.Dimension;


public class GraphicalView extends View {
	private JPanel panel;
	private JFrame frame;

	GraphicalView(int maxX, int maxY) {
		super(maxX, maxY);
		init();
	}


	public void drawGame() {
		refreshScore();
		panel.repaint();
	}

	public JFrame getJFrame() {
		return frame;
	}


	private void init() {
		frame = new JFrame("Tetris v1.0");
		frame.setFocusable(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel = this.new MyJPanel();
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}

	private int getNextBrickIdentifier(int x, int y) {
		return Controller.getInstance().getNextBrickIdentifier(x + 2, y - 1);
	}

	class MyJPanel extends JPanel {
		final int xBase,
				  yBase,
				  /* length of one side of one grid's square */
				  side = 20,
				  depth = side / 10;

		public MyJPanel() {
			/* this code make grid be centred */
			int xWindowSize = (int) this.getPreferredSize().getWidth();
			int yWindowSize = (int) this.getPreferredSize().getHeight();
			int xContentSize = (MAX_X + 6 + 2) * (this.side - this.depth);
			int yContentSize = MAX_Y * (this.side - this.depth);
			this.xBase = (xWindowSize - xContentSize) / 2;
			this.yBase = (yWindowSize - yContentSize) /2;
		}

		private void drawGameOver(final Graphics g) {
			g.setColor(RED);
			g.drawString("Game is over", 0, 30);
		}

		private void drawStrings(final Graphics g) {
			g.setColor(BLACK);
			g.drawString(String.format("Your score is %d", score), 0, 10);
			g.drawString("Current game", xBase, yBase - 5);
			int xTemp = xBase + (MAX_X + 2) * (side - depth);
			g.drawString("Next brick", xTemp, yBase - 5);
		}

        /**
        * Matter fact, locking is performed in this method because View layer
        * uses Model layer
        * to identify which cells to paint, so that we need to keep
        * memory consistency is Model layer
		*/
        @Override
		public void paintComponent(final Graphics g) {
            log.info(" AWT-EventQueue thread entered paintComponent method");
            super.paintComponent(g);
			if (GraphicalView.this.gameOver)
                this.drawGameOver(g);
            this.drawStrings(g);
			this.drawGrid(g);
            this.drawSmallGrid(g);
            this.fillSmallGrid(g);
			this.fillGrid(g);
            synchronized (Controller.getInstance().obj) {
                Controller.getInstance().obj.notifyAll();
                Controller.inform("notified!");
            }
			log.info(" AWT-EventQueue thread exited paintComponent method");
		}

		private void drawGrid(final Graphics g) {
			int xTemp = xBase;
			int yTemp = yBase;
			g.setColor(LIGHT_GRAY);
			for (int y = 0; y <= MAX_Y; y++) {
				for (int x = 0; x <= MAX_X; x++) {
					g.fillRect(xTemp, yTemp, side, depth); // highest side of a square
					g.fillRect(xTemp, yTemp, depth, side); // left side of a square
					g.fillRect(xTemp + side - depth, yTemp, depth, side); // rigth side of a square
					g.fillRect(xTemp, yTemp + side - depth, side, depth); // lowest side of a square
					xTemp += side - depth;
				}
				yTemp += side - depth;
				xTemp = xBase;
			}
		}

		private void drawSmallGrid(final Graphics g) {
			int xTemp = xBase + (MAX_X + 2) * (side - depth);
			int yTemp = yBase;
			for (int y = 0 ; y < 6; y++) {
				for (int x = 0; x < 6; x++) {
					g.fillRect(xTemp, yTemp, side, depth);
					g.fillRect(xTemp, yTemp, depth, side);
					g.fillRect(xTemp + side - depth, yTemp, depth, side);
					g.fillRect(xTemp, yTemp + side - depth, side, depth);
					xTemp += side - depth;
				}
				yTemp += side - depth;
				xTemp = xBase + (MAX_X + 2) * (side - depth);
			}

		}

		private void fillGrid(final Graphics g) {
			for (int y = MIN_Y; y <= MAX_Y; y++) {
				for (int x = MIN_X; x <= MAX_X; x++) {
					int id = GraphicalView.this.getIdentifier(x, y);
					Color color = this.getColor(id);
					this.drawCell(x, y, color, g);
				}
			}
		}

		private void fillSmallGrid(final Graphics g) {
			for (int y = 0; y < 6; y++) {
				for (int x = 0; x < 6; x++) {
					int id = GraphicalView.this.getNextBrickIdentifier(x, y);
					Color color = this.getColor(id);
					this.drawSmallCell(x, y, color, g);
				}
			}
		}

		private void drawCell(int widthPos, int heightPos, Color color, final Graphics g) {
			int xTemp = xBase + widthPos * (side - depth) + depth;
			int yTemp = yBase + heightPos * (side - depth) + depth;
			g.setColor(color);
			g.fillRect(xTemp, yTemp, side - depth * 2, side - depth * 2);
		}

		private void drawSmallCell(int x, int y, Color color, final Graphics g) {
			drawCell(x + MAX_X + 2, y, color, g);
		}

		private Color getColor(int id) {
			Color color = null;
			switch (id) {
				case 1 : color = RED; break;
				case 2 : color = YELLOW; break;
				case 3 : color = BLUE; break;
				case 4 : color = DARK_GRAY; break;
				case 5 : color = MAGENTA; break;
				case 6 : color = GREEN; break;
				case 7 : color = ORANGE; break;
				default : color = WHITE; break;
			}
			return color;
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400, 450);
		}

	}
}