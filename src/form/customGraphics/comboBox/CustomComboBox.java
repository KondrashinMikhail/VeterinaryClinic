package form.customGraphics.comboBox;

import lombok.Getter;
import lombok.Setter;
import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Getter
@Setter
public class CustomComboBox extends JComboBox<String> {
    public CustomComboBox(String[] items) {

        setBackground(Color.WHITE);
        setForeground(DesignUtils.MAIN_COLOR);
        setFont(DesignUtils.REGULAR_FONT);
        setMaximumRowCount(5);
        setEditable(false);
        setMinimumSize(new Dimension(100, 50));

        for(String item : items) {
            addItem(item);
        }

        setBorder(new EmptyBorder(15, 3, 5, 3));
        setUI(new CustomComboBoxUI(this));
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> jList, Object o, int i, boolean bln, boolean bln1) {
                Component component = super.getListCellRendererComponent(jList, o, i, bln, bln1);
                setBorder(new EmptyBorder(5, 5, 5, 5));
                if (bln) {
                    component.setBackground(Color.WHITE);
                    component.setForeground(DesignUtils.SELECTION_COLOR);
                }
                return component;
            }
        });
    }
}
