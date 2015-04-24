/**
 *
 */
package com.vj.util.file.app;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.vj.util.file.DiskPath;

/**
 * @author Vijay
 *
 */
public class DiskPathTable extends JTable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public DiskPathTable() {
    }

    /**
     * @param dm
     */
    public DiskPathTable(final TableModel dm) {
        super(dm);
    }

    /**
     * @param dm
     * @param cm
     */
    public DiskPathTable(final TableModel dm, final TableColumnModel cm) {
        super(dm, cm);
    }

    /**
     * @param numRows
     * @param numColumns
     */
    public DiskPathTable(final int numRows, final int numColumns) {
        super(numRows, numColumns);
    }

    /**
     * @param rowData
     * @param columnNames
     */
    public DiskPathTable(final Vector rowData, final Vector columnNames) {
        super(rowData, columnNames);
    }

    /**
     * @param rowData
     * @param columnNames
     */
    public DiskPathTable(final Object[][] rowData, final Object[] columnNames) {
        super(rowData, columnNames);
    }

    /**
     * @param dm
     * @param cm
     * @param sm
     */
    public DiskPathTable(final TableModel dm, final TableColumnModel cm,
            final ListSelectionModel sm) {
        super(dm, cm, sm);
    }

    @Override
    public TableCellEditor getCellEditor(final int row, final int column) {
        final Object vals = getModel().getValueAt(row, column);
        if (vals instanceof Collection) {
            final Collection<DiskPath> list = (Collection<DiskPath>) vals;
            final JComboBox<DiskPath> comboBox = new JComboBox<DiskPath>();
            for (final DiskPath item : list) {
                comboBox.addItem(item);

            }
            comboBox.setRenderer(new DiskComboBoxRenderer());
            comboBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent e) {
                    try {
                        Desktop.getDesktop().open(
                                ((DiskPath) ((JComboBox<DiskPath>) e
                                        .getSource()).getSelectedItem())
                                        .getDiskFile());
                    } catch (final IOException e1) {
                        e1.printStackTrace();
                    }

                }
            });
            return new DefaultCellEditor(comboBox);
        }
        return super.getCellEditor(row, column);
    }

    @Override
    public String getToolTipText(final MouseEvent e) {
        String tip = null;
        final java.awt.Point p = e.getPoint();
        final int rowIndex = rowAtPoint(p);
        final int colIndex = columnAtPoint(p);
        final int realColumnIndex = convertColumnIndexToModel(colIndex);
        final Object vals = getModel().getValueAt(rowIndex, colIndex);
        if (realColumnIndex == 0) {
            tip = ((DiskPath) vals).getPath().toString();
        } else if (realColumnIndex == 3) {
            if (vals instanceof List) {
                tip = ((List<DiskPath>) vals).get(0).getPath().toString();
            } else {
                tip = ((DiskPath) vals).getPath().toString();
            }
        } else {
            tip = super.getToolTipText(e);
        }
        return tip;
    }
}
