/**
 * 
 */
package com.vj.util.file;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Vijay
 * 
 */
public class DiskPathFilter implements PathFilter {
	/**
	 * 
	 */
	private String extensions;
	private String[] ext;

	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(final String extensions) {
		this.extensions = extensions.trim();
		ext = this.extensions.split(",");
	}

	/**
	 * 
	 */
	private long sizeMin = 0;
	/**
	 * 
	 */
	private long sizeMax = Long.MAX_VALUE;
	/**
	 * 
	 */
	private long lastModifiedMin = 0;
	/**
	 * 
	 */
	private long lastModifiedMax = Long.MAX_VALUE;

	public long getLastModifiedMin() {
		return lastModifiedMin;
	}

	public void setLastModifiedMin(final long lastModifiedMin) {
		this.lastModifiedMin = lastModifiedMin;
	}

	public long getLastModifiedMax() {
		return lastModifiedMax;
	}

	public void setLastModifiedMax(final long lastModifiedMax) {
		this.lastModifiedMax = lastModifiedMax;
	}

	public long getSizeMin() {
		return sizeMin;
	}

	public void setSizeMin(final long sizeMin) {
		this.sizeMin = sizeMin;
	}

	public long getSizeMax() {
		return sizeMax;
	}

	public void setSizeMax(final long sizeMax) {
		this.sizeMax = sizeMax;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vj.util.file.PathFilter#accept(java.nio.file.Path,
	 * java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public boolean accept(final Path path, final BasicFileAttributes attributes) {
		boolean ret = true;
		if (filterSize()) {
			final long size = attributes.size();
			ret = size < sizeMin && size > sizeMax;
		}
		if (ret && filterLastModified()) {
			final long last = attributes.lastModifiedTime().toMillis();
			ret = last < lastModifiedMin && last > lastModifiedMax;
		}
		if (ret && filterExtension()) {
			ret = false;
			for (final String e : ext) {
				if (path.getFileName().toString().endsWith(e)) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	private boolean filterExtension() {
		return !(null == extensions || extensions.isEmpty());
	}

	private boolean filterLastModified() {
		return sizeMin != 0 || sizeMax != Long.MAX_VALUE;
	}

	private boolean filterSize() {
		return lastModifiedMin != 0 || lastModifiedMax != Long.MAX_VALUE;
	}

}
