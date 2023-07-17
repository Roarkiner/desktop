package helper;

import java.awt.Component;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import model.ActivityModel;

public class ActivityListCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof ActivityModel) {
            ActivityModel activity = (ActivityModel) value;
            setText(activity.getName());
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(5, 10, 5, 10));
            setFont(getFont().deriveFont(Font.BOLD, 16f));
        }

        return this;
    }
}