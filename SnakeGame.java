import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class SnakeGame extends JPanel implements ActionListener {

    private static final int TILE_SIZE = 25;
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int COLUMNS = BOARD_WIDTH / TILE_SIZE;
    private static final int ROWS = BOARD_HEIGHT / TILE_SIZE;
    private static final int MAX_LENGTH = COLUMNS * ROWS;
    private static final int STARTING_LENGTH = 5;
    private static final int TIMER_DELAY = 150;

    private final int[] snakeX = new int[MAX_LENGTH];
    private final int[] snakeY = new int[MAX_LENGTH];
    private final Random random = new Random();
    private final Timer timer = new Timer(TIMER_DELAY, this);

    private int snakeLength;
    private int foodX;
    private int foodY;
    private int score;
    private Direction direction;
    private boolean running;
    private boolean paused;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public SnakeGame() {
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyHandler());
        resetGame();
    }

    private void resetGame() {
        snakeLength = STARTING_LENGTH;
        score = 0;
        direction = Direction.RIGHT;
        running = true;
        paused = false;

        for (int i = 0; i < snakeLength; i++) {
            snakeX[i] = (snakeLength - 1 - i) * TILE_SIZE;
            snakeY[i] = 0;
        }

        spawnFood();
        timer.start();
        requestFocusInWindow();
        repaint();
    }

    private void spawnFood() {
        if (MAX_LENGTH - snakeLength == 0) {
            running = false;
            timer.stop();
            return;
        }

        do {
            foodX = random.nextInt(COLUMNS) * TILE_SIZE;
            foodY = random.nextInt(ROWS) * TILE_SIZE;
        } while (isSnakeTile(foodX, foodY));
    }

    private boolean isSnakeTile(int tileX, int tileY) {
        for (int i = 0; i < snakeLength; i++) {
            if (snakeX[i] == tileX && snakeY[i] == tileY) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawFood(g);
        drawSnake(g);
        drawScore(g);

        if (!running) {
            drawEndScreen(g, "Game Over", "Press R to restart");
        } else if (paused) {
            drawEndScreen(g, "Paused", "Press P to resume");
        }

        g.dispose();
    }

    private void drawFood(Graphics2D g) {
        g.setColor(new Color(80, 160, 255));
        g.fillOval(foodX + 2, foodY + 2, TILE_SIZE - 4, TILE_SIZE - 4);
    }

    private void drawSnake(Graphics2D g) {
        for (int i = 0; i < snakeLength; i++) {
            g.setColor(i == 0 ? new Color(70, 220, 110) : new Color(245, 200, 60));
            g.fillRoundRect(snakeX[i], snakeY[i], TILE_SIZE, TILE_SIZE, 6, 6);
        }
    }

    private void drawScore(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g.drawString("Score: " + score, 12, 28);
    }

    private void drawEndScreen(Graphics2D g, String title, String instruction) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 46));
        drawCenteredString(g, title, BOARD_HEIGHT / 2 - 10);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        drawCenteredString(g, instruction, BOARD_HEIGHT / 2 + 32);
    }

    private void drawCenteredString(Graphics2D g, String text, int y) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(text, (BOARD_WIDTH - metrics.stringWidth(text)) / 2, y);
    }

    private void move() {
        for (int i = snakeLength - 1; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }

        switch (direction) {
            case UP -> snakeY[0] -= TILE_SIZE;
            case DOWN -> snakeY[0] += TILE_SIZE;
            case LEFT -> snakeX[0] -= TILE_SIZE;
            case RIGHT -> snakeX[0] += TILE_SIZE;
        }
    }

    private void checkFood() {
        if (snakeX[0] == foodX && snakeY[0] == foodY) {
            if (snakeLength < MAX_LENGTH) {
                snakeLength++;
                score++;
            }
            spawnFood();
        }
    }

    private void checkCollision() {
        if (snakeX[0] < 0 || snakeX[0] >= BOARD_WIDTH
                || snakeY[0] < 0 || snakeY[0] >= BOARD_HEIGHT) {
            endGame();
            return;
        }

        for (int i = 1; i < snakeLength; i++) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                endGame();
                return;
            }
        }
    }

    private void endGame() {
        running = false;
        timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (running && !paused) {
            move();
            checkFood();
            checkCollision();
        }
        repaint();
    }

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent event) {
            switch (event.getKeyCode()) {
                case KeyEvent.VK_LEFT -> changeDirection(Direction.LEFT);
                case KeyEvent.VK_RIGHT -> changeDirection(Direction.RIGHT);
                case KeyEvent.VK_UP -> changeDirection(Direction.UP);
                case KeyEvent.VK_DOWN -> changeDirection(Direction.DOWN);
                case KeyEvent.VK_P -> paused = running && !paused;
                case KeyEvent.VK_R -> resetGame();
            }
            repaint();
        }
    }

    private void changeDirection(Direction newDirection) {
        boolean reversing = direction == Direction.UP && newDirection == Direction.DOWN
                || direction == Direction.DOWN && newDirection == Direction.UP
                || direction == Direction.LEFT && newDirection == Direction.RIGHT
                || direction == Direction.RIGHT && newDirection == Direction.LEFT;
        if (!reversing) {
            direction = newDirection;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(new SnakeGame());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}