package myschedule.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;

import myschedule.service.ObjectUtils.Getter;

import org.junit.Test;
import org.quartz.Scheduler;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.StdSchedulerFactory;

/** SchedulerMetaDataTest
 *
 * @author Zemian Deng
 */
public class SchedulerMetaDataTest {

	@Test
	public void testSchedulerMetaData() throws Exception {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		SchedulerMetaData metaData = scheduler.getMetaData();
		List<Getter> getters = ObjectUtils.getGetters(metaData);
		System.out.println(getters);
	}
}
