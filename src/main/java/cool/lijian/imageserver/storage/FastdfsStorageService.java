package cool.lijian.imageserver.storage;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

import cool.lijian.imageserver.ImageServerProperties;
import cool.lijian.imageserver.StorageService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FastdfsStorageService implements StorageService {

	private ImageServerProperties.Fastdfs fastdfsConfig;

	public FastdfsStorageService(ImageServerProperties.Fastdfs fastdfsConfig) throws Exception {
		log.info("Initialize fastdfs with trackerServers : {}", fastdfsConfig.getTrackerServers());
		this.fastdfsConfig = fastdfsConfig;
		ClientGlobal.initByTrackers(fastdfsConfig.getTrackerServers());
	}

	private StorageClient1 getClient() throws Exception {
		TrackerClient trackerClient = new TrackerClient();
		TrackerServer trackerServer = trackerClient.getConnection();
		StorageServer storageServer = trackerClient.getStoreStorage(trackerServer, fastdfsConfig.getGroup());
		InetSocketAddress addr = storageServer.getInetSocketAddress();
		log.debug("Use storageServer:  address = {}, group = {}, storePath = {}", addr, fastdfsConfig.getGroup(),
				fastdfsConfig.getStorePath());
		storageServer = new StorageServer(addr.getAddress().getHostAddress(), addr.getPort(),
				fastdfsConfig.getStorePath());

		StorageClient1 storageClient = new StorageClient1(trackerServer, storageServer);
		return storageClient;
	}

	@Override
	public InputStream loadFile(String fileId) throws Exception {
		StorageClient1 client = getClient();
		byte[] data = client.download_file1(fileId);
		return new ByteArrayInputStream(data);
	}

	@Override
	public OutputStream saveFile(String fileId) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean exists(String fileId) throws Exception {
		StorageClient1 client = getClient();
		FileInfo info = client.get_file_info1(fileId);
		return info != null;
	}

	@Override
	public String saveFile(byte[] data, String originalFilename) throws Exception {
		StorageClient1 client = getClient();
		int index = originalFilename.lastIndexOf('.');
		String suffix = "";
		if (index >= 0) {
			suffix = originalFilename.substring(index + 1);
		}
		String fileId = client.upload_file1(data, suffix, null);
		return fileId;
	}
}
