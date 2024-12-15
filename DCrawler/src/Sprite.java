import java.awt.*;

public class Sprite implements Displayable {
    protected int x, y;
    protected Image image;
    protected int width, height;

    public Sprite(int x, int y, Image image, int width, int height) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = width;
        this.height = height;
    }

    // Implement the draw method from the Displayable interface
    @Override
    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    // Method for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}