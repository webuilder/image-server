package cool.lijian.imageserver;

/**
 * 
 * @author Li Jian
 *
 */
public enum ZoomMode {

	/**
	 * Define thumbnails with a minimum width of 'w', a minimum of 'h', a scale,
	 * and a center crop. <br>
	 * The thumbnails of the turn are usually the size of 'w' x 'h' (there is an
	 * edge that cuts off the extra part when it is over the rectangle)<br>
	 * If only the 'w' parameter is specified or only the 'h' parameter is
	 * specified, it represents a square graph that is defined as an equal
	 * length.
	 */
	MODE_1(1);

	private final int value;

	private ZoomMode(int value) {
		this.value = value;
	}

	public static ZoomMode valueOf(int value) {
		for (ZoomMode zm : ZoomMode.values()) {
			if (zm.value == value) {
				return zm;
			}
		}
		return null;
	}

	public int value() {
		return value;
	}
}
