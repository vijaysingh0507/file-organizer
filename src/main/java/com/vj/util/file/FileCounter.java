/**
 *
 */
package com.vj.util.file;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Vijay
 *
 */
public class FileCounter extends SimpleFileVisitor<Path> {

    /**
     *
     */
    private final Path startingDir;
    /**
     *
     */
    private long count = 0;
    /**
     *
     */
    private PathFilter filter;

    public FileCounter(final Path startingDir) {
        this.startingDir = startingDir;
    }

    public long getCount() throws IOException {
        Files.walkFileTree(startingDir, this);
        return count;
    }

    @Override
    public FileVisitResult visitFile(final Path file,
            final BasicFileAttributes attr) {
        if (attr.isRegularFile()
                && (filter == null || filter.accept(file, attr))) {
            count++;
        }
        return CONTINUE;
    }

    public PathFilter getFilter() {
        return filter;
    }

    public void setFilter(final PathFilter filter) {
        this.filter = filter;
    }

    @Override
    public FileVisitResult visitFileFailed(final Path file,
            final IOException exc) {
        return CONTINUE;
    }
}
