package cool.lijian.imageserver.factory;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import cool.lijian.imageserver.ImageServerProperties;
import cool.lijian.imageserver.ImageServerProperties.Storage;
import cool.lijian.imageserver.MasterService;
import cool.lijian.imageserver.StorageService;
import cool.lijian.imageserver.storage.FastdfsStorageService;
import cool.lijian.imageserver.storage.LocalFileSystemStorageService;

@Service
public class MasterServiceFactory extends AbstractFactoryBean<MasterService> {

	@Resource
	private ImageServerProperties props;

	@Resource
	private AutowireCapableBeanFactory beanFactory;

	@Override
	public Class<?> getObjectType() {
		return MasterService.class;
	}

	@Override
	protected MasterService createInstance() throws Exception {
		StorageService ss = null;
		final Storage storageConfig = props.getMasterStorage();
		final String type = storageConfig.getType();
		if ("file".equals(type)) {
			ss = new LocalFileSystemStorageService(storageConfig.getFile());
		} else if ("fastdfs".equals(type)) {
			ss = new FastdfsStorageService(storageConfig.getFastdfs());
		} else {
			throw new RuntimeException("Unsupport image-server.master-storage.type [" + type + "]");
		}
		beanFactory.autowireBean(ss);
		return new MasterServiceWrapper(ss);
	}

}
