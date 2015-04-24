/**
 * Copyright (c) 2012, VJ Inc. All rights reserved.
 */
package com.vj.util.file.app;

import java.awt.Desktop;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vj.util.file.DiskPath;
import com.vj.util.file.DuplicateFileVisitor;
import com.vj.util.file.FileSize;

/**
 *
 * @author Vijay Singh
 */
public class DiskDataModel extends AbstractTableModel implements
        DuplicateFileVisitor<DiskPath>, ListSelectionListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DiskDataModel.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private final List<DiskPath> data = new ArrayList<DiskPath>();
    /**
     *
     */
    private final String[] columnNames = { "File", "Size", "# of Duplicates",
            "Duplicate Files", "Total Duplicate size" };

    /**
     *
     */
    public void clear() {
        data.clear();
        fireTableDataChanged();
    }

    /**
     *
     */
    public void deleteAllDuplicates() {
        for (final DiskPath path : data) {
            deleteDuplicates(path);
        }
        data.clear();
    }

    /**
     * @param path
     */
    private void deleteDuplicates(final DiskPath path) {
        final List<DiskPath> dup = path.getDuplicates();
        for (int row = 0; row < dup.size(); row++) {
            final DiskPath dupPath = dup.get(row);
            try {
                if (Files.deleteIfExists(dupPath.getPath())) {
                    System.out.println("Deleted : "
                            + dupPath.getPath().toString());
                    fireTableRowsDeleted(row, row);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param row
     */
    public void deleteDuplicates(final int row) {
        deleteDuplicates(data.get(row));
        data.remove(row);
        fireTableRowsDeleted(row, row);
        // fireTableDataChanged();
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        Class<?> ret;
        switch (columnIndex) {
        case 1:
            ret = FileSize.class;
            break;
        case 2:
            ret = Integer.class;
            break;
        case 3:
            ret = Collection.class;
            break;
        case 4:
            ret = FileSize.class;
            break;
        default:
            ret = Object.class;

        }
        return ret;

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(final int col) {
        return columnNames[col].toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        return data.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final DiskPath path = data.get(rowIndex);
        Object ret = path;
        switch (columnIndex) {
        case 1:
            ret = path.getFileSize();
            break;
        case 2:
            ret = path.getDuplicates().size();
            break;
        case 3:
            ret = path.getDuplicates().size() > 1 ? path.getDuplicates() : path
                    .getDuplicates().get(0);
            break;
        case 4:
            ret = path.getDuplicateFileSize();
            break;
        }
        return ret;
    }

    @Override
    public boolean isCellEditable(final int row, final int col) {
        return col == 3;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vj.util.file.DuplicateFileVisitor#refreshModel()
     */
    @Override
    public void refreshModel() {
        fireTableDataChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vj.util.file.DuplicateFileVisitor#visitDuplicateFile(java.lang.Object
     * )
     */
    @Override
    public FileVisitResult visitDuplicateFile(final DiskPath path) {
        data.add(path);
        final int row = data.size() - 1;
        fireTableRowsInserted(row, row);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public void valueChanged(final ListSelectionEvent event) {
    }

    void open(final int row, final int column) {
        try {
            if (column == 3) {
                Desktop.getDesktop().open(
                        data.get(row).getDuplicates().get(0).getDiskFile());
            } else {
                Desktop.getDesktop().open(data.get(row).getDiskFile());
            }
        } catch (final IOException ex) {
            LOGGER.debug("Could not open file {}.", toString(), ex);
        }
    }

    public String getToolTipText(final MouseEvent e) {
        return null;

    }

    @Override
    public void completed() {
        for (int i = 0; i < data.size(); i++) {
            final DiskPath d = data.get(i);
            final DiskPath t = d.rearrange();
            if (t != d) {
                data.set(i, t);
            }
        }
        refreshModel();
    }
}