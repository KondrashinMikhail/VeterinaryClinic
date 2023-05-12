package form.customGraphics.notification;

import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoundedPane extends JPanel {
    public RoundedPane() {
        setOpaque(false);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
        setForeground(DesignUtils.MAIN_COLOR);
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        int RADIUS = 20;
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RADIUS, RADIUS);
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, RADIUS, RADIUS);
        super.paintComponent(g);
    }
}
