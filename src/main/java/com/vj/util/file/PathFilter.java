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
public interface PathFilter {
	/**
	 * @param path
	 * @param attributes
	 * @return
	 */
	boolean accept(Path path, BasicFileAttributes attributes);

}
