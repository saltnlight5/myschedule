package unit.myschedule.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;

import myschedule.service.AbstractService;
import myschedule.service.ServiceContainer;

import org.junit.Test;

public class ServiceContainerTest {
	
	@Test
	public void testAbstractService() throws Exception {
		MyService s = new MyService();
		assertThat(s.isInited(), is(false));
		assertThat(s.isStarted(), is(false));
		
		s.init();
		assertThat(s.isInited(), is(true));
		assertThat(s.isStarted(), is(false));
		
		s.start();
		assertThat(s.isInited(), is(true));
		assertThat(s.isStarted(), is(true));

		s.stop();
		assertThat(s.isInited(), is(true));
		assertThat(s.isStarted(), is(false));
		
		s.destroy();
		assertThat(s.isInited(), is(false));
		assertThat(s.isStarted(), is(false));

		// Again should still works.
		s.init();
		assertThat(s.isInited(), is(true));
		assertThat(s.isStarted(), is(false));
	}
	
	@Test
	public void testServiceContainer() throws Exception {
		testServiceContainer(5);
		testServiceContainer(1);
		testServiceContainer(0);
	}
	
	private void testServiceContainer(int numOfService) throws Exception {
		ServiceContainer container = new ServiceContainer();
		List<MyService> list = new ArrayList<MyService>();
		for (int i = 0; i < numOfService; i++) {
			MyService s = new MyService();
			list.add(s);
			container.addService(s);
		}
		
		// Before we init
		assertThat(container.isInited(), is(false));
		assertThat(container.isStarted(), is(false));
		for (MyService s : list) {
			assertThat(s.isInited(), is(false));
			assertThat(s.isStarted(), is(false));
		}
		
		container.init();
		assertThat(container.isInited(), is(true));
		assertThat(container.isStarted(), is(false));
		for (MyService s : list) {
			assertThat(s.isInited(), is(true));
			assertThat(s.isStarted(), is(false));
		}
		
		container.start();
		assertThat(container.isInited(), is(true));
		assertThat(container.isStarted(), is(true));
		for (MyService s : list) {
			assertThat(s.isInited(), is(true));
			assertThat(s.isStarted(), is(true));
		}
		
		container.stop();
		assertThat(container.isInited(), is(true));
		assertThat(container.isStarted(), is(false));
		for (MyService s : list) {
			assertThat(s.isInited(), is(true));
			assertThat(s.isStarted(), is(false));
		}
		
		container.destroy();
		assertThat(container.isInited(), is(false));
		assertThat(container.isStarted(), is(false));
		for (MyService s : list) {
			assertThat(s.isInited(), is(false));
			assertThat(s.isStarted(), is(false));
		}
		
		// re-test one more time.
		container.init();
		assertThat(container.isInited(), is(true));
		assertThat(container.isStarted(), is(false));
		for (MyService s : list) {
			assertThat(s.isInited(), is(true));
			assertThat(s.isStarted(), is(false));
		}
	}

	public static class MyService extends AbstractService {
		
	}
}
