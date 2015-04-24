/**
 * Copyright (c) 2012, VJ Inc. All rights reserved.
 */
package com.vj.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vijay Singh
 *
 */
public class DiskFile extends File {
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DiskFile.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private final int BUFFER = 1048576;
    /**
     *
     */
    private static final int FIRST = 8;
    /**
     * The first FIRST bytes of file.
     */
    private byte[] firstBytes;
    /**
     *
     */
    private final long length;

    /**
     * @param pathname
     */
    public DiskFile(final String pathname, final Long length) {
        super(pathname);
        this.length = length;
    }

    /**
     * @param file
     */
    public DiskFile(final File file, final Long length) {
        super(file.getAbsolutePath());
        this.length = length;
    }

    /**
     * TODO: Test and improve performance of this method.
     *
     * @param other
     * @return
     * @throws IOException
     */
    public boolean compareContent(final DiskFile other) throws IOException {
        if (other.length == length && compareFirstBytes(other) == 0) {
            BufferedInputStream stream1 = null, stream2 = null;
            try {
                stream1 = other.getFirstBytes();
                stream2 = getFirstBytes();
                if (Arrays.equals(firstBytes, other.firstBytes)) {
                    if (stream1 == null) {
                        stream1 = new BufferedInputStream(new FileInputStream(
                                other));
                        stream1.skip(FIRST);
                    }
                    if (stream2 == null) {
                        stream2 = new BufferedInputStream(new FileInputStream(
                                this));
                        stream2.skip(FIRST);
                    }
                    final byte[] buffer1 = new byte[BUFFER];
                    final byte[] buffer2 = new byte[BUFFER];
                    int read1 = stream1.read(buffer1);
                    int read2 = stream2.read(buffer2);
                    while (read1 == read2 && read1 != -1) {
                        if (!Arrays.equals(buffer1, buffer2)) {
                            return false;
                        }
                        read1 = stream1.read(buffer1);
                        read2 = stream2.read(buffer2);
                    }
                    return true;
                }
            } finally {
                if (stream1 != null) {
                    stream1.close();
                }
                if (stream2 != null) {
                    stream2.close();
                }
            }
        }
        return false;
    }

    private int compareFirstBytes(final DiskFile file) {
        return null == firstBytes || null == file.firstBytes ? 0 : compare(
                firstBytes, file.firstBytes);
    }

    private BufferedInputStream getFirstBytes() throws IOException {
        BufferedInputStream stream = null;
        if (null == firstBytes) {
            stream = new BufferedInputStream(new FileInputStream(this));
            firstBytes = new byte[FIRST];
            stream.read(firstBytes, 0, FIRST);
        }
        return stream;
    }

    static String readFirstLineFromFile(final String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            return br.readLine();
        }
    }

    @Override
    public int compareTo(final File file) {
        if (file instanceof DiskFile) {
            final DiskFile df = (DiskFile) file;
            final int ret = Long.compare(length, df.length);
            if (ret == 0) {
                try {
                    return compare(df);
                } catch (final IOException e) {
                    LOGGER.error("Failed to read file {}.", file, e);
                    return super.compareTo(file);
                }
            } else {
                return ret;
            }
        } else {
            return super.compareTo(file);
        }
    }

    private int compare(final DiskFile other) throws IOException {
        final int c = compareFirstBytes(other);
        if (c == 0) {
            BufferedInputStream stream1 = null, stream2 = null;
            try {
                stream1 = getFirstBytes();
                stream2 = other.getFirstBytes();
                final int d = compare(firstBytes, other.firstBytes);
                if (d == 0) {
                    if (stream2 == null) {
                        stream2 = new BufferedInputStream(new FileInputStream(
                                other));
                        stream2.skip(FIRST);
                    }
                    if (stream1 == null) {
                        stream1 = new BufferedInputStream(new FileInputStream(
                                this));
                        stream1.skip(FIRST);
                    }
                    final byte[] buffer1 = new byte[BUFFER];
                    final byte[] buffer2 = new byte[BUFFER];
                    int read1 = stream1.read(buffer1);
                    int read2 = stream2.read(buffer2);
                    while (read1 == read2 && read1 != -1) {
                        final int e = compare(buffer1, buffer2);
                        if (e != 0) {
                            return e;
                        }
                        read1 = stream2.read(buffer1);
                        read2 = stream1.read(buffer2);
                    }
                    return 0;
                } else {
                    return d;
                }
            } finally {
                if (stream2 != null) {
                    stream2.close();
                }
                if (stream1 != null) {
                    stream1.close();
                }
            }
        } else {
            return c;
        }
    }

    private int compare(final byte[] one, final byte[] two) {
        final int x = one.length;
        final int y = two.length;
        if (x < y) {
            return -1;
        } else if (x > y) {
            return 1;
        } else {
            for (int j = 0; j < x; j++) {
                if (one[j] < two[j]) {
                    return -1;
                } else if (one[j] > two[j]) {
                    return 1;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof DiskFile) {
            try {
                return compareContent((DiskFile) obj);
            } catch (final IOException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (int) (length ^ length >>> 32);
    }

    @Override
    public long length() {
        return length;
    }

}
