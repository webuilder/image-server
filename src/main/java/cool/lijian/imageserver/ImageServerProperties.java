package cool.lijian.imageserver;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "image-server")
public class ImageServerProperties {
	private String index;

	/**
	 * 是否允许指定文件名<br>
	 * 如果为true，并且request的参数中包含filename字段，那么就用filename作为上传后的文件名;
	 * 否则就是用NamingStrategy生成文件名
	 */
	private boolean enableFilename;

	private Storage masterStorage;

	private Storage thumbnailStorage;

	@Data
	public static class Storage {
		private String type;

		private File file;

		private Fastdfs fastdfs;
	}

	@Data
	public static class File {
		private String path;
	}

	@Data
	public static class Fastdfs {
		private String trackerServers;

		private String group;

		private int storePath;
	}
}
