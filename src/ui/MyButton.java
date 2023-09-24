package ui;

import java.awt.*;

public class MyButton {
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final String text;
    private Rectangle bounds;
    private boolean mouseOver, mousePressed;

    public MyButton(String text, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        initBounds();
    }


    public void draw(Graphics g) {
        //Body
        drawBody(g);

        //Border
        drawBorder(g);


        //Text
        drawText(g);
    }

    private void drawBorder(Graphics g) {
        g.setColor(new Color(88, 69, 47));
        g.drawRect(x, y, width, height);
        g.drawRect(x + 1, y + 1, width - 2, height - 2);
        if (mousePressed) {
            g.drawRect(x + 2, y + 2, width - 4, height - 4);
        }
    }

    private void drawBody(Graphics g) {
        if (mouseOver)
            g.setColor(new Color(192, 154, 111));
        else
            g.setColor(new Color(168, 212, 190));
        g.fillRect(x, y, width, height);
    }

    private void drawText(Graphics g) {
        int w = g.getFontMetrics().stringWidth(text);
        int h = g.getFontMetrics().getHeight();

        g.drawString(text, x - w / 2 + width / 2, y + h / 2 + height / 2);
    }

    public void resetBooleans() {
        this.mouseOver = false;
        this.mousePressed = false;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    private void initBounds() {
        this.bounds = new Rectangle(x, y, width, height);
    }
}
