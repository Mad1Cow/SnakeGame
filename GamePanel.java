import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {

    //Size of app
    static final int screenWidth = 600;
    static final int screenHeight = 600;

    //Split the screen into units
    static final int unitSize = 25;
    static final int gameUnits = (screenWidth * screenHeight) / unitSize;

    //Delay for timer
    static final int delay = 70;

    //Arrays to track positions of snake body parts
    final int[] x = new int[gameUnits];
    final int[] y = new int[gameUnits];

    //Number of body parts of the snake
    int bodyParts = 5;

    //Score
    int applesEaten;

    //Co-ordinates of randomly generated apple
    int appleX;
    int appleY;

    //Determine direction
    //U = up; R = right; D = down; L = left
    char direction = 'R';

    //Check if app is running
    boolean running = false;

    Timer timer;
    Random random;

    //Constructor
    GamePanel() {
        //Create instance of Random
        random = new Random();

        //Set screen
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        startGame();
    }

    public void startGame() {
        //App is now running
        running = true;
        spawnApple();

        //Create instance of Timer with specified delay
        timer = new Timer(delay, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //Create a grid to visualise units
        for(int i = 0; i < screenHeight/unitSize; i++) {
            g.drawLine(i * unitSize, 0, i * unitSize, screenHeight);
            g.drawLine(0, i * unitSize, screenWidth, i * unitSize);
        }

        //Check for game over
        if(running) {
            //Draw the apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, unitSize, unitSize);

            //Draw the snake
            for(int i = 0; i < bodyParts; i++) {
                //Draw the head
                if(i == 0) {
                    g.setColor(Color.YELLOW);
                    g.fillRect(x[i], y[i], unitSize, unitSize);
                } else {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], unitSize, unitSize);
                }
            }

            //Setup score text
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            FontMetrics metricsScore = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (screenWidth - metricsScore.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void spawnApple() {
        //Generate apple co-ordinates
        appleX = random.nextInt((int)(screenWidth/unitSize)) * unitSize;
        appleY = random.nextInt((int)(screenHeight/unitSize)) * unitSize;
    }

    public void move() {
        //Shift the body parts of the snake
        for(int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        //Switch case for head direction
        switch(direction) {
            case 'U':
                y[0] = y[0] - unitSize;
                break;
            case 'R':
                x[0] = x[0] + unitSize;
                break;
            case 'D':
                y[0] = y[0] + unitSize;
                break;
            case 'L':
                x[0] = x[0] - unitSize;
                break;
        }
    }

    public void checkApple() {
        //Check if head has collided with apple
        if((x[0] == appleX) && (y[0] == appleY)) {
            //Increase score by 1
            applesEaten++;
            //Increase body parts by 1
            bodyParts++;

            //Spawn a new apple
            spawnApple();
        }

    }

    public void checkCollisions() {
        //Check if head collides with body
        for(int i = bodyParts; i > 0; i--) {
            if((x[0] == x[i]) && (y[0] == y[i])) {
                //Stop the game
                running = false;
            }
        }

        //Check if head collides with left border
        if(x[0] < 0) {
            running = false;
        }

        //Check if head collides with right border
        if(x[0] > screenWidth) {
            running = false;
        }

        //Check if head collides with top border
        if(y[0] < 0) {
            running = false;
        }

        //Check if head collides with bottom border
        if(y[0] > screenHeight) {
            running = false;
        }

        //If game stops running stop the timer
        if(!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //Setup score text
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metricsScore = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (screenWidth - metricsScore.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        //Setup game over text
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 75));
        FontMetrics metricsGameOver = getFontMetrics(g.getFont());
        g.drawString("Game Over", (screenWidth - metricsGameOver.stringWidth("Game Over")) / 2, screenHeight / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            //Switch case for player input
            switch(e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    //Make sure player can't turn 180 and collide with body
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
            }
        }
    }
}
