package form.customGraphics.comboBox;

import form.customGraphics.table.ScrollBarCustom;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;

public class CustomComboBoxUI extends BasicComboBoxUI {
    JComboBox comboBox;
    public CustomComboBoxUI(JComboBox comboBox) {
        this.comboBox = comboBox;
    }

    @Override
    public void paintCurrentValueBackground(Graphics graphics, Rectangle rectangle, boolean bln) {

    }

    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
        super.paintCurrentValue(g, bounds, false);
    }

    @Override
    protected JButton createArrowButton() {
        return new ArrowButton();
    }

    @Override
    protected ComboPopup createPopup() {
        BasicComboPopup pop = new BasicComboPopup(comboBox) {
            @Override
            protected JScrollPane createScroller() {
                list.setFixedCellHeight(30);
                JScrollPane scroll = new JScrollPane(list);
                scroll.setBackground(Color.WHITE);
                scroll.setVerticalScrollBar(new ScrollBarCustom());
                return scroll;
            }
        };
        pop.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        return pop;
    }

    @Override
    public void paint(Graphics graphics, JComponent jc) {
        super.paint(graphics, jc);
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        int width = comboBox.getWidth();
        int height = comboBox.getHeight();
        g2.setColor(DesignUtils.MAIN_COLOR);
        g2.fillRect(2, height - 1, width - 4, 1);
        createLineStyle(g2);
        g2.dispose();
    }

    private void createLineStyle(Graphics2D g2) {
        double width = comboBox.getWidth() - 4;
        int height = comboBox.getHeight();
        g2.setColor(DesignUtils.MAIN_COLOR);
        double size;
        size = width * 5;
        double x = (width - size) / 2;
        g2.fillRect((int) (x + 2), height - 2, (int) size, 2);
    }

    private static class ArrowButton extends JButton {
        public ArrowButton() {
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(5, 5, 5, 5));
            setBackground(new Color(150, 150, 150));
        }

        @Override
        public void paint(Graphics graphics) {
            super.paint(graphics);
            Graphics2D g2 = (Graphics2D) graphics;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = getWidth();
            int height = getHeight();
            int size = 10;
            int x = (width - size) / 2;
            int y = (height - size) / 2 - 3;
            int[] px = {x, x + size, x + size / 2};
            int[] py = {y, y, y + size};
            g2.setColor(DesignUtils.MAIN_COLOR);
            g2.fillPolygon(px, py, px.length);
            g2.dispose();
        }
    }
}
