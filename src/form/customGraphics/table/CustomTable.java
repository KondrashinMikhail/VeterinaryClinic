package form.customGraphics.table;

import utils.DesignUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CustomTable extends JTable {
    private final CustomTableHeader header;
    private final CustomTableCell cell;

    public CustomTable() {
        header = new CustomTableHeader();
        cell = new CustomTableCell();
        getTableHeader().setDefaultRenderer(header);
        getTableHeader().setPreferredSize(new Dimension(0, 35));
        setDefaultRenderer(Object.class, cell);
        setRowHeight(30);
    }

    public void setColumnAlignment(int column, int align) {
        header.setAlignment(column, align);
    }

    public void setCellAlignment(int column, int align) {
        cell.setAlignment(column, align);
    }

    public void setColumnWidth(int column, int width) {
        getColumnModel().getColumn(column).setPreferredWidth(width);
        getColumnModel().getColumn(column).setMinWidth(width);
        getColumnModel().getColumn(column).setMaxWidth(width);
        getColumnModel().getColumn(column).setMinWidth(10);
        getColumnModel().getColumn(column).setMaxWidth(10000);
    }

    public void fixTable(JScrollPane scroll) {
        scroll.setVerticalScrollBar(new ScrollBarCustom());
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        scroll.setCorner(JScrollPane.UPPER_RIGHT_CORNER, panel);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
    }

    private static class CustomTableHeader extends DefaultTableCellRenderer {

        private final Map<Integer, Integer> alignment = new HashMap<>();

        public void setAlignment(int column, int align) {
            alignment.put(column, align);
        }

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
            Component com = super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
            com.setBackground(Color.WHITE);
            com.setForeground(DesignUtils.MIXED_MAIN_COLOR);
            com.setFont(com.getFont().deriveFont(Font.BOLD, 18));
            setHorizontalAlignment(alignment.getOrDefault(i1, JLabel.CENTER));
            setBorder(new LineBorder(DesignUtils.MIXED_MAIN_COLOR));
            return com;
        }
    }

    private class CustomTableCell extends DefaultTableCellRenderer {

        private final Map<Integer, Integer> alignment = new HashMap<>();

        public void setAlignment(int column, int align) {
            alignment.put(column, align);
        }

        @Override
        public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int row, int column) {
            Component com = super.getTableCellRendererComponent(jtable, o, bln, bln1, row, column);
            if (isCellSelected(row, column)) {
                com.setBackground(DesignUtils.SELECTION_COLOR);
                com.setForeground(Color.WHITE);
            } else {
                if (row % 2 == 0) {
                    com.setBackground(DesignUtils.MIXED_MAIN_COLOR);
                } else {
                    com.setBackground(DesignUtils.MIXED_MAIN_COLOR);
                }
                com.setForeground(Color.WHITE);
            }
            setBorder(new EmptyBorder(0, 5, 0, 5));
            setHorizontalAlignment(alignment.getOrDefault(column, JLabel.CENTER));
            return com;
        }
    }
}
