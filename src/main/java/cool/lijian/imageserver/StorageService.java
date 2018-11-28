package cool.lijian.imageserver;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Service for storage a image file.
 * 
 * @author lijian
 *
 */
public interface StorageService {

	/**
	 * Load a image file from storage.
	 * 
	 * @param fileId
	 * @return
	 */
	InputStream loadFile(String fileId) throws Exception;

	/**
	 * Get a OutputStream for save a image file to storage.
	 * 
	 * @param fileId
	 * @return
	 */
	OutputStream saveFile(String fileId) throws Exception;

	/**
	 * Save file and generate a fileId
	 * 
	 * @param data
	 * @param originalFileName
	 * @return
	 * @throws Exception
	 */
	String saveFile(byte[] data, String originalFileName) throws Exception;

	/**
	 * Tests whether the file is exists
	 * 
	 * @param fileId
	 * @return <code>true</code> if and only if the file exists;
	 *         <code>false</code> otherwise
	 * @throws Exception
	 */
	boolean exists(String fileId) throws Exception;
}
