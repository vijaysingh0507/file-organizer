/**
 * Copyright (c) 2012, VJ Inc. All rights reserved.
 */
package com.vj.util.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vijay Singh
 *
 */
public class DiskPath {

    /**
     *
     */
    final private Path path;
    /**
     *
     */
    final private BasicFileAttributes attrib;
    /**
     *
     */
    final private DiskFile diskFile;
    /**
     *
     */
    private List<DiskPath> duplicates;
    /**
     *
     */
    final private FileSize fileSize;

    /**
     * @param path
     * @param attrib
     */
    public DiskPath(final Path path, final BasicFileAttributes attrib) {
        this.path = path;
        this.attrib = attrib;
        diskFile = new DiskFile(path.toFile(), attrib.size());
        fileSize = new FileSize(attrib.size());
    }

    public void addDuplicate(final DiskPath path) {
        synchronized (this) {
            if (null == duplicates) {
                duplicates = new ArrayList<DiskPath>();
            }
        }
        duplicates.add(path);
    }

    public void setDuplicates(final List<DiskPath> duplicates) {
        this.duplicates = duplicates;
    }

    /**
     * @param toPath
     * @return
     * @throws IOException
     */
    public boolean compareContent(final DiskPath toPath) throws IOException {
        return getDiskFile().compareContent(getDiskFile());
    }

    /**
     * @return
     */
    public BasicFileAttributes getAttrib() {
        return attrib;
    }

    /**
     * @return
     */
    public DiskFile getDiskFile() {
        return diskFile;
    }

    /**
     * @return
     */
    public FileSize getDuplicateFileSize() {
        return new FileSize(attrib.size()
                * (null == duplicates ? 0 : duplicates.size()));
    }

    public List<DiskPath> getDuplicates() {
        return duplicates;
    }

    /**
     * @return
     */
    public String getFileName() {
        return path.getFileName().toString();
    }

    public FileSize getFileSize() {
        return fileSize;
    }

    /**
     * @return
     */
    public Path getPath() {
        return path;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return path.getFileName().toString();
    }

    public DiskPath rearrange() {
        int i = 0;
        DiskPath old = this;
        int oldIndex = -1;
        for (; i < duplicates.size(); i++) {
            final DiskPath dup = duplicates.get(i);
            if (dup.attrib.creationTime().compareTo(old.attrib.creationTime()) < 0) {
                old = dup;
                oldIndex = i;
            }
        }
        if (old != this) {
            duplicates.remove(oldIndex);
            duplicates.add(this);
            old.setDuplicates(duplicates);
            duplicates = null;
        }
        return old;
    }
}