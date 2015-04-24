/**
 * Copyright (c) 2012, VJ Inc. All rights reserved.
 */
package com.vj.util.file;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.awt.Toolkit;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Vijay Singh
 *
 */
public class DuplicateFinder extends SwingWorker<Void, Void> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DuplicateFinder.class);
    /**
     * The {@link Path} in which Duplicate files needs to be found.
     */
    final private String path;
    /**
     * A {@link Map} to store Unique {@link DiskFile}'s found in the given
     * {@link #path}.
     */
    final private Map<DiskFile, DiskPath> unique = new TreeMap<DiskFile, DiskPath>();
    /**
     * A {@link DuplicateFileVisitor}
     */
    final private DuplicateFileVisitor<DiskPath> duplicateVisitor;
    /**
     * The Size in bytes of the duplicate files found in the given {@link #path}
     * .
     */
    private long dupSize;
    /**
     * The Counter to store the number of duplicate files found.
     */
    private int dupCount = 0;
    /**
     * Counter to count visited files.
     */
    private long visited = 0;
    /**
     * Field to store total number of files in the given path.
     */
    private long total;
    /**
     * Time taken to find duplicates.
     */
    private float timeInSec;
    /**
     *
     */
    private PathFilter filter;

    /**
     *
     * @param path
     *            The path of the directory in which to look for duplicate
     *            files.
     * @param dupVisitor
     *            The {@link DuplicateFileVisitor} that should be notified if
     *            the duplicate file is found.
     */
    public DuplicateFinder(final String path,
            final DuplicateFileVisitor<DiskPath> dupVisitor) {
        this.path = path;
        duplicateVisitor = dupVisitor;
    }

    /**
     * @throws IOException
     */
    public void find() throws IOException {
        final long start = System.currentTimeMillis();
        final Path startingDir = Paths.get(path);
        // printStats(startingDir);
        final FileCounter fileCounter = new FileCounter(startingDir);
        fileCounter.setFilter(filter);
        total = fileCounter.getCount();
        final long mid = System.currentTimeMillis();
        final String message = total + " files found in "
                + (float) (mid - start) / 1000
                + " seconds. Locating Duplicates Now.";
        firePropertyChange("statusbar", "", message);
        LOGGER.info(message);
        Files.walkFileTree(startingDir, new MyVisitor());
        duplicateVisitor.completed();
        final long end = System.currentTimeMillis();
        timeInSec = (end - mid) / 1000;
    }

    @Override
    protected Void doInBackground() throws Exception {
        find();
        return null;
    }

    @Override
    protected void done() {
        final String message = "Found " + dupCount + " duplicate files in "
                + timeInSec + " seconds. " + FileSize.readableFileSize(dupSize)
                + " can be freed.";
        LOGGER.info(message);
        firePropertyChange("statusbar", "", message);
        Toolkit.getDefaultToolkit().beep();
    }

    class MyVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(final Path file,
                final BasicFileAttributes attr) {
            if (attr.isRegularFile()
                    && (filter == null || filter.accept(file, attr))) {
                visited++;
                setProgress((int) (visited * 100 / total));
                final DiskPath diskPath = new DiskPath(file, attr);
                final DiskPath uniq = unique.get(diskPath.getDiskFile());
                if (null == uniq) {
                    unique.put(diskPath.getDiskFile(), diskPath);
                } else {
                    dupCount++;
                    dupSize += attr.size();
                    LOGGER.debug("Exact Match: \"{}\" with: \"{}\" "
                            + "Size: {} File will be deleted.",
                            diskPath.toString(), uniq.toString(), attr.size());
                    final boolean visit = uniq.getDuplicates() == null;
                    uniq.addDuplicate(diskPath);
                    if (visit) {
                        duplicateVisitor.visitDuplicateFile(uniq);
                    } else {
                        duplicateVisitor.refreshModel();
                    }

                }
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file,
                final IOException exc) {
            LOGGER.error("Unable to read file {}.", file, exc);
            return CONTINUE;
        }
    }
}
