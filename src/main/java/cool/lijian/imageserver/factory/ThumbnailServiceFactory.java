package cool.lijian.imageserver.factory;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import cool.lijian.imageserver.ImageServerProperties;
import cool.lijian.imageserver.ImageServerProperties.Storage;
import cool.lijian.imageserver.StorageService;
import cool.lijian.imageserver.ThumbnailService;
import cool.lijian.imageserver.storage.LocalFileSystemStorageService;

@Service
public class ThumbnailServiceFactory extends AbstractFactoryBean<ThumbnailService> {

	@Resource
	private ImageServerProperties props;

	@Resource
	private AutowireCapableBeanFactory beanFactory;

	@Override
	public Class<?> getObjectType() {
		return ThumbnailService.class;
	}

	@Override
	protected ThumbnailService createInstance() throws Exception {
		StorageService ss = null;
		final Storage storageConfig = props.getThumbnailStorage();
		final String type = storageConfig.getType();
		if ("file".equals(type)) {
			ss = new LocalFileSystemStorageService(storageConfig.getFile());
		} else {
			throw new RuntimeException("Unsupport image-server.thumbnail-storage.type [" + type + "]");
		}
		beanFactory.autowireBean(ss);
		return new ThumbnailServiceWrapper(ss);
	}

}
