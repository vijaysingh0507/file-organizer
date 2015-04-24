/**
 * Copyright (c) 2012, VJ Inc. All rights reserved.
 */
package com.vj.util.file;

import java.text.DecimalFormat;

/**
 * @author Vijay
 * 
 */
public class FileSize implements Comparable<FileSize> {
    /**
     * @param size
     * @return
     */
    public static String readableFileSize(final long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        final int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size
                / Math.pow(1024, digitGroups))
                + " " + units[digitGroups];
    }

    /**
	 * 
	 */
    private final long size;
    /**
	 * 
	 */
    private final String readableSize;

    /**
     * @param size
     */
    public FileSize(final long size) {
        this.size = size;
        this.readableSize = readableFileSize(size);
    }

    public int compareTo(final FileSize o) {
        return Long.compare(this.size, o.size);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FileSize)) {
            return false;
        }
        final FileSize other = (FileSize) obj;
        if (this.size != other.size) {
            return false;
        }
        return true;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return this.size;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.size ^ (this.size >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return this.readableSize;
    }

}
