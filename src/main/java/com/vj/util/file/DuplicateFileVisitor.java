/**
 * 
 */
package com.vj.util.file;

import java.nio.file.FileVisitResult;

/**
 * This interface can be implemented to visit Duplicate files in a given path.
 * 
 * @author Vijay
 * 
 * @param <T>
 */
public interface DuplicateFileVisitor<T> {
	/**
	 * Visit a duplicate path.
	 * 
	 * @param path
	 *            the T of a duplicate file. Where T can be a handle to the
	 *            given file.
	 * @return
	 */
	FileVisitResult visitDuplicateFile(T path);

	/**
	 * To refresh the model.
	 */
	void refreshModel();

	/**
	 * 
	 */
	void completed();
}
