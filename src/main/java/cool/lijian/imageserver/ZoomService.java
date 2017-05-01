package cool.lijian.imageserver;

/**
 * Service for zoom a image.
 * 
 * @author Li Jian
 *
 */
public interface ZoomService {

	/**
	 * Zoom a image, write to a new file ,and return a new file id.
	 * 
	 * @param fileId
	 *            TODO
	 * @param m
	 *            Mode of zoom operation
	 * @param w
	 *            The width after zooming
	 * @param h
	 *            The height after zooming
	 * 
	 * @return Zoomed image's fileId
	 * @throws Exception
	 *             if zoom operation fail, then throw Exception
	 */
	String zoom(String fileId, ZoomMode m, int w, int h) throws Exception;
}
