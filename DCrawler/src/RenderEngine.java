import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class RenderEngine extends JPanel implements Engine {
    private CopyOnWriteArrayList<Displayable> renderList;

    public RenderEngine(JFrame jFrame) {
        // Use a thread-safe list if you're adding/removing displayables from different threads
        renderList = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds a single Displayable object to the render list.
     */
    public void addToRenderList(Displayable displayable) {
        if (!renderList.contains(displayable)) {
            renderList.add(displayable);
        }
    }

    /**
     * Adds a list of Displayable objects to the render list.
     */
    public void addToRenderList(ArrayList<Displayable> displayables) {
        for (Displayable displayable : displayables) {
            if (!renderList.contains(displayable)) {
                renderList.add(displayable);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // Iterate through all the Displayable objects and draw them
        for (Displayable renderObject : renderList) {
            renderObject.draw(g);
        }
    }

    @Override
    public void update() {
        // Repaint the panel
        this.repaint();
    }
}