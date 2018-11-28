package cool.lijian.imageserver.factory;

import java.io.InputStream;
import java.io.OutputStream;

import cool.lijian.imageserver.MasterService;
import cool.lijian.imageserver.StorageService;

public class MasterServiceWrapper implements MasterService {

	private StorageService storageService;

	public MasterServiceWrapper(StorageService storageService) {
		this.storageService = storageService;
	}

	@Override
	public InputStream loadFile(String fileId) throws Exception {
		return storageService.loadFile(fileId);
	}

	@Override
	public OutputStream saveFile(String fileId) throws Exception {
		return storageService.saveFile(fileId);
	}

	@Override
	public boolean exists(String fileId) throws Exception {
		return storageService.exists(fileId);
	}

	@Override
	public String saveFile(byte[] data, String originalFileName) throws Exception {
		return storageService.saveFile(data, originalFileName);
	}

}
