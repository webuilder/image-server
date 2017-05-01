package cool.lijian.imageserver;

/**
 * The strategy of file's naming.
 * 
 * @author Li Jian
 *
 */
public interface NamingStrategy {

	/**
	 * Create a fileId for input file.
	 * 
	 * @param fileData
	 *            the bytes of uploaded file.
	 * @param originalFileName
	 *            the original name of uploaded file.
	 * @return a unique file id.
	 */
	String createFileId(byte[] fileData, String originalFileName);
}
