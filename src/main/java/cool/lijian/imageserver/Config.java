package cool.lijian.imageserver;

import javax.annotation.Resource;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(ImageServerProperties.class)
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
