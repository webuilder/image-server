package cool.lijian.imageserver.impl;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cool.lijian.imageserver.ImageServerProperties;
import cool.lijian.imageserver.MasterService;
import cool.lijian.imageserver.ThumbnailService;
import cool.lijian.imageserver.ZoomMode;
import cool.lijian.imageserver.ZoomService;

/**
 * ZoomMode.MODE_1's implemention.
 * 
 * @author Li Jian
 *
 */
public class Mode1ZoomService implements ZoomService {

	private static final Logger logger = LoggerFactory.getLogger(Mode1ZoomService.class);

	@Resource
	private ImageServerProperties props;

	@Resource
	private MasterService masterService;

	@Resource
	private ThumbnailService thumbnailService;

	@Override
	public String zoom(String fileId, ZoomMode m, int w, int h) throws Exception {
		if (w == 0)
			w = h;
		if (h == 0)
			h = w;
		String newFileId = fileId + "." + m.value() + "_" + w + "_" + h;
		// File destFile = new File(props.getZoomPath(), newFileId);
		if (thumbnailService.exists(newFileId)) {
			logger.debug("<{}> exist", newFileId);
			return newFileId;
		}

		// File srcFile = new File(storagePath, fileId);
		InputStream srcData = masterService.loadFile(fileId);
		BufferedImage srcImg = ImageIO.read(srcData);
		int srcW = srcImg.getWidth();
		int srcH = srcImg.getHeight();

		int sx1 = 0, sy1 = 0, sx2 = srcW - 1, sy2 = srcH - 1;
		double srcScale = (double) srcW / srcH;
		double destScale = (double) w / h;
		if (srcScale > destScale) {
			int cutW = (int) (srcH * destScale);
			sx1 = srcW / 2 - cutW / 2;
			sx2 = srcW / 2 + cutW / 2;
		} else {
			int cutH = (int) (srcW / destScale);
			sy1 = srcH / 2 - cutH / 2;
			sy2 = srcH / 2 + cutH / 2;
		}

		logger.debug("zoom sx1={}, sy1={}, sx2={}, sy2={}", sx1, sy1, sx2, sy2);
		BufferedImage destImg = new BufferedImage(w, h, srcImg.getType());
		Graphics g = destImg.getGraphics();
		g.drawImage(srcImg, 0, 0, w - 1, h - 1, sx1, sy1, sx2, sy2, null);
		g.dispose();

		int lastIndex = fileId.lastIndexOf('.');
		String type = fileId.substring(lastIndex + 1);

		// File destParentDir = destFile.getParentFile();
		// if (!destParentDir.exists()) {
		// destParentDir.mkdirs();
		// }
		try (OutputStream out = thumbnailService.saveFile(newFileId)) {
			ImageIO.write(destImg, type, out);
			logger.debug("created <{}>", newFileId);
		}

		return newFileId;
	}

}
