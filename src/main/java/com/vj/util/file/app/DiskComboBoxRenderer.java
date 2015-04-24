/**
 *
 */
package com.vj.util.file.app;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

import com.vj.util.file.DiskPath;

/**
 * @author Vijay
 *
 */
public class DiskComboBoxRenderer extends DefaultListCellRenderer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
     * .JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(
            final JList<? extends Object> list, final Object value,
            final int index, final boolean isSelected,
            final boolean cellHasFocus) {
        final JComponent comp = (JComponent) super
                .getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);

        if (-1 < index && null != value) {
            list.setToolTipText(((DiskPath) value).getPath().toString());
        }
        return comp;
    }

}
