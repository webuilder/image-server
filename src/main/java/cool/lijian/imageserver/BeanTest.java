package cool.lijian.imageserver;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class BeanTest {

	@Resource
	private MasterService masterService;

	@PostConstruct
	public void init() throws Exception {
	}
}
