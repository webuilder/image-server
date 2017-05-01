package cool.lijian.imageserver.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import cool.lijian.imageserver.ZoomMode;
import cool.lijian.imageserver.ZoomService;

/**
 * ZoomService's default implemention.
 * 
 * @author Li Jian
 *
 */
public class DefaultZoomService implements ZoomService, InitializingBean {

	private Map<ZoomMode, ZoomService> map;

	@Autowired
	private AutowireCapableBeanFactory acbf;

	@Override
	public String zoom(String fileId, ZoomMode m, int w, int h) throws Exception {
		ZoomService service = map.get(m);
		String zoomedFileId = service.zoom(fileId, m, w, h);
		return zoomedFileId;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.map = new HashMap<ZoomMode, ZoomService>();
		map.put(ZoomMode.MODE_1, new Mode1ZoomService());

		// initializing all service beans
		for (ZoomService service : map.values()) {
			acbf.autowireBean(service);
			acbf.initializeBean(service, service.getClass().getSimpleName());
		}
	}

}
