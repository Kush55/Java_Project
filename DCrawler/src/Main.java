import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Main {

    JFrame displayZoneFrame;

    RenderEngine renderEngine;
    GameEngine gameEngine;
    PhysicEngine physicEngine;

    private JLabel timerLabel; // Label to display the timer
    private JProgressBar healthBar; // Health bar to display health
    private int timeRemaining = 40; // Countdown timer set to 40 seconds
    private int health = 100; // Initial health
    private Timer countdownTimer;
    private Timer healthDecreaseTimer;
    private Timer collisionTimer;

    public Main() throws Exception {
        // Create the main game window
        displayZoneFrame = new JFrame("Java Labs");
        displayZoneFrame.setSize(400, 600);
        displayZoneFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create the start screen panel
        JPanel startScreen = new JPanel();
        startScreen.setLayout(new BorderLayout());
        JLabel title = new JLabel("Welcome to Dungeon Crawler", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        startScreen.add(title, BorderLayout.CENTER);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setFocusable(false);
        startScreen.add(startButton, BorderLayout.SOUTH);

        // Show the start screen first
        displayZoneFrame.getContentPane().add(startScreen);
        displayZoneFrame.setVisible(true);

        // Action to switch to the game screen
        startButton.addActionListener(e -> {
            try {
                startGame();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void startGame() throws Exception {
        // Create hero sprite
        DynamicSprite hero = new DynamicSprite(200, 300,
                ImageIO.read(new File("./img/heroTileSheetLowRes.png")), 48, 50);

        // Initialize engines
        renderEngine = new RenderEngine(displayZoneFrame);
        physicEngine = new PhysicEngine();
        gameEngine = new GameEngine(hero);

        // Timers for updating the game
        Timer renderTimer = new Timer(50, (time) -> renderEngine.update());
        Timer gameTimer = new Timer(50, (time) -> gameEngine.update());
        Timer physicTimer = new Timer(50, (time) -> physicEngine.update());

        renderTimer.start();
        gameTimer.start();
        physicTimer.start();

        // Set up the game components
        Playground level = new Playground("./data/level1.txt");
        renderEngine.addToRenderList(level.getSpriteList());
        renderEngine.addToRenderList(hero);
        physicEngine.addToMovingSpriteList(hero);
        physicEngine.setEnvironment(level.getSolidSpriteList());

        // Create a timer label
        timerLabel = new JLabel("Time Remaining: " + timeRemaining + " seconds");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Create health bar
        healthBar = new JProgressBar(0, 100); // Health bar from 0 to 100
        healthBar.setValue(health); // Set initial health value
        healthBar.setStringPainted(true); // Display the value as text on the bar
        healthBar.setFont(new Font("Arial", Font.BOLD, 14));
        healthBar.setForeground(Color.GREEN); // Initially green
        healthBar.setBackground(Color.GRAY);

        // Countdown timer for 40 seconds
        countdownTimer = new Timer(1000, e -> updateTimer());
        countdownTimer.start();

        // Timer to decrease health by 25 points every 10 seconds
        healthDecreaseTimer = new Timer(10000, e -> decreaseHealth());
        healthDecreaseTimer.start();

        // Replace the start screen with the game screen
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.add(renderEngine, BorderLayout.CENTER);
        gamePanel.add(timerLabel, BorderLayout.NORTH);
        gamePanel.add(healthBar, BorderLayout.SOUTH); // Add health bar to the bottom

        displayZoneFrame.getContentPane().removeAll();
        displayZoneFrame.getContentPane().add(gamePanel);
        displayZoneFrame.revalidate();
        displayZoneFrame.repaint();

        // Add key listener for game controls
        displayZoneFrame.addKeyListener(gameEngine);

        // Check for collisions and update health
        collisionTimer = new Timer(50, e -> checkCollisions(hero, level));
        collisionTimer.start();
    }

    private void checkCollisions(DynamicSprite hero, Playground level) {
        // Iterate through all solid objects in the environment
        for (Sprite sprite : level.getSolidSpriteList()) {
            if (sprite instanceof SolidSprite) {
                SolidSprite solidSprite = (SolidSprite) sprite;  // Cast to SolidSprite

                // Check if the hitboxes intersect
                if (solidSprite.getHitBox().intersects(hero.getHitBox())) {
                    // Collision detected, reduce health by 10
                    health -= 10;
                    updateHealthBar();

                    // Optionally add any visual feedback here, e.g., red flash or sound

                    if (health <= 0) {
                        health = 0;
                        healthBar.setValue(health);
                        gameOver();
                    }
                }
            }
        }
    }

    // Method to decrease health by 25 every 10 seconds
    private void decreaseHealth() {
        if (health > 0) {
            health -= 25;  // Decrease health by 25 points every 10 seconds
            updateHealthBar();

            if (health <= 0) {
                health = 0;
                healthBar.setValue(health);
                gameOver();
            }
        }
    }

    // Method to update the health bar and change color based on health
    private void updateHealthBar() {
        healthBar.setValue(health);
        if (health > 25) {
            healthBar.setForeground(Color.GREEN); // Green for health above 25
        } else {
            healthBar.setForeground(Color.RED); // Red for health below or equal to 25
        }
    }

    private void updateTimer() {
        if (timeRemaining > 0) {
            timeRemaining--;
            timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");
        } else {
            timerLabel.setText("Time's Up!");
            gameOver();
        }
    }

    private void gameOver() {
        // Stop all game-related timers
        countdownTimer.stop();
        healthDecreaseTimer.stop();
        collisionTimer.stop();

        // Show Game Over screen with Restart button
        JPanel gameOverScreen = new JPanel();
        gameOverScreen.setLayout(new BorderLayout());

        JLabel gameOverLabel = new JLabel("Game Over", SwingConstants.CENTER);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gameOverScreen.add(gameOverLabel, BorderLayout.CENTER);

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(new Font("Arial", Font.BOLD, 18));
        restartButton.setFocusable(false);
        gameOverScreen.add(restartButton, BorderLayout.SOUTH);

        restartButton.addActionListener(e -> restartGame());

        // Replace the game screen with the Game Over screen
        displayZoneFrame.getContentPane().removeAll();
        displayZoneFrame.getContentPane().add(gameOverScreen);
        displayZoneFrame.revalidate();
        displayZoneFrame.repaint();
    }

    private void restartGame() {
        // Reset game variables and restart the game
        timeRemaining = 40;
        health = 100;
        timerLabel.setText("Time Remaining: " + timeRemaining + " seconds");
        healthBar.setValue(health);
        healthBar.setForeground(Color.GREEN);

        // Start the game again
        try {
            startGame();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
    }
}