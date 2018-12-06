package cool.lijian.imageserver.impl;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;

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
		if (thumbnailService.exists(newFileId)) {
			logger.debug("<{}> exist", newFileId);
			return newFileId;
		}

		InputStream srcData = masterService.loadFile(fileId);
		BufferedInputStream bufIn = new BufferedInputStream(srcData);
		bufIn.mark(Integer.MAX_VALUE);

		Metadata metadata = ImageMetadataReader.readMetadata(bufIn);
		ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
		JpegDirectory jpegDirectory = (JpegDirectory) metadata.getFirstDirectoryOfType(JpegDirectory.class);

		int orientation = 1;
		try {
			orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		int width = jpegDirectory.getImageWidth();
		int height = jpegDirectory.getImageHeight();

		AffineTransform affineTransform = new AffineTransform();

		switch (orientation) {
		case 1:
			break;
		case 2: // Flip X
			affineTransform.scale(-1.0, 1.0);
			affineTransform.translate(-width, 0);
			break;
		case 3: // PI rotation
			affineTransform.translate(width, height);
			affineTransform.rotate(Math.PI);
			break;
		case 4: // Flip Y
			affineTransform.scale(1.0, -1.0);
			affineTransform.translate(0, -height);
			break;
		case 5: // - PI/2 and Flip X
			affineTransform.rotate(-Math.PI / 2);
			affineTransform.scale(-1.0, 1.0);
			break;
		case 6: // -PI/2 and -width
			affineTransform.translate(height, 0);
			affineTransform.rotate(Math.PI / 2);
			break;
		case 7: // PI/2 and Flip
			affineTransform.scale(-1.0, 1.0);
			affineTransform.translate(-height, 0);
			affineTransform.translate(0, width);
			affineTransform.rotate(3 * Math.PI / 2);
			break;
		case 8: // PI / 2
			affineTransform.translate(0, width);
			affineTransform.rotate(3 * Math.PI / 2);
			break;
		default:
			break;
		}

		AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);
		bufIn.reset();
		
		BufferedImage originalImage = ImageIO.read(bufIn);
		
		BufferedImage srcImg = new BufferedImage(originalImage.getHeight(), originalImage.getWidth(), originalImage.getType());
		srcImg = affineTransformOp.filter(originalImage, srcImg);
        
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

		try (OutputStream out = thumbnailService.saveFile(newFileId)) {
			ImageIO.write(destImg, type, out);
			logger.debug("created <{}>", newFileId);
		}

		return newFileId;
	}

}
