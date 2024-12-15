import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class DynamicSprite extends SolidSprite {
    private Direction direction = Direction.EAST;
    private double speed = 5;
    private double timeBetweenFrame = 250;
    private boolean isWalking = true;
    private final int spriteSheetNumberOfColumn = 10;

    public DynamicSprite(int x, int y, Image image, int width, int height) {
        super(x, y, image, width, height);
    }

    /**
     * Checks if the new position after moving is valid (no collision).
     */
    private boolean isMovingPossible(ArrayList<Sprite> environment) {
        Rectangle2D.Double moved = new Rectangle2D.Double();
        switch (direction) {
            case EAST:
                moved.setRect(super.getHitBox().getX() + speed, super.getHitBox().getY(),
                        super.getHitBox().getWidth(), super.getHitBox().getHeight());
                break;
            case WEST:
                moved.setRect(super.getHitBox().getX() - speed, super.getHitBox().getY(),
                        super.getHitBox().getWidth(), super.getHitBox().getHeight());
                break;
            case NORTH:
                moved.setRect(super.getHitBox().getX(), super.getHitBox().getY() - speed,
                        super.getHitBox().getWidth(), super.getHitBox().getHeight());
                break;
            case SOUTH:
                moved.setRect(super.getHitBox().getX(), super.getHitBox().getY() + speed,
                        super.getHitBox().getWidth(), super.getHitBox().getHeight());
                break;
        }

        // Check if any solid objects intersect with the new movement
        for (Sprite s : environment) {
            if (s instanceof SolidSprite && s != this) {
                if (((SolidSprite) s).intersect(moved)) {
                    return false;  // Collision detected
                }
            }
        }
        return true;  // No collision
    }

    /**
     * Sets the direction of the sprite.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Moves the sprite in the current direction.
     */
    private void move() {
        switch (direction) {
            case NORTH -> this.y -= speed;
            case SOUTH -> this.y += speed;
            case EAST -> this.x += speed;
            case WEST -> this.x -= speed;
        }
    }

    /**
     * Attempts to move the sprite, checking for possible collisions.
     */
    public void moveIfPossible(ArrayList<Sprite> environment) {
        if (isMovingPossible(environment)) {
            move();  // Move only if no collision
        }
    }

    /**
     * Draws the sprite, with animation based on the current direction and time.
     */
    @Override
    public void draw(Graphics g) {
        int index = (int) (System.currentTimeMillis() / timeBetweenFrame % spriteSheetNumberOfColumn);

        // Draw sprite with the correct frame from the sprite sheet
        g.drawImage(image, (int) x, (int) y, (int) (x + width), (int) (y + height),
                index * this.width, direction.getFrameLineNumber() * height,
                (index + 1) * this.width, (direction.getFrameLineNumber() + 1) * this.height, null);
    }
}