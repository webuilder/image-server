package cool.lijian.imageserver.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.annotation.Resource;

import cool.lijian.imageserver.ImageServerProperties;
import cool.lijian.imageserver.NamingStrategy;
import cool.lijian.imageserver.StorageService;

/**
 * Use local filesystem as a storage.
 * 
 * @author lijian
 *
 */
public class LocalFileSystemStorageService implements StorageService {

	private String path;

	@Resource
	private NamingStrategy namingStrategy;

	public LocalFileSystemStorageService(ImageServerProperties.File fileConfig) {
		path = fileConfig.getPath();
	}

	@Override
	public InputStream loadFile(String fileId) throws Exception {
		File srcFile = new File(path, fileId);
		FileInputStream in = new FileInputStream(srcFile);
		return in;
	}

	@Override
	public OutputStream saveFile(String fileId) throws Exception {
		File srcFile = new File(path, fileId);
		File dir = srcFile.getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		FileOutputStream out = new FileOutputStream(srcFile);
		return out;
	}

	@Override
	public boolean exists(String fileId) {
		File srcFile = new File(path, fileId);
		return srcFile.exists();
	}

	@Override
	public String saveFile(byte[] data, String originalFileName) throws Exception {
		String fileId = namingStrategy.createFileId(data, originalFileName);
		try (OutputStream out = new BufferedOutputStream(saveFile(fileId))) {
			out.write(data);
		}
		return fileId;
	}

}
