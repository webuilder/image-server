package cool.lijian.imageserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cool.lijian.imageserver.impl.DateNamingStrategy;
import cool.lijian.imageserver.impl.DefaultZoomService;

/**
 * 
 * @author Li Jian
 *
 */
@Configuration
public class Config {

	@Bean
	public NamingStrategy namingStrategy() {
		return new DateNamingStrategy();
	}

	@Bean
	public ZoomService zoomService() {
		return new DefaultZoomService();
	}
}
