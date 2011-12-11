package myschedule.quartz.extra;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerKey;
import org.quartz.TriggerListener;
import org.quartz.spi.SchedulerPlugin;
import org.quartz.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This plugin will record a row in a database table for each event (methods) in SchedulerPlugin and TriggerListener. 
 * The database table must setup with the following fields, and {@link #insertSql} must be populated
 * correctly with binding parameters.
 * 
 * <p>You must also set the {@link #dataSourceName} name in the quartz.properties. It can be the same data source you
 * setup for the JdbcJobStore configuration (see Quartz doc.)
 * 
 * Here is example of how to create the history table on MySQL.
 * <pre>
 * CREATE TABLE qrtz_scheduler_history (
 *   host_ip VARCHAR(15) NOT NULL,
 *   host_name VARCHAR(256) NOT NULL,
 *   scheduler_name VARCHAR(256) NOT NULL,
 *   event_type VARCHAR(128) NOT NULL,
 *   event_name VARCHAR(128) NOT NULL,
 *   event_time TIMESTAMP NOT NULL,
 *   info1 VARCHAR(256) NULL,
 *   info2 VARCHAR(256) NULL,
 *   info3 VARCHAR(256) NULL,
 *   info4 VARCHAR(256) NULL,
 *   info5 VARCHAR(256) NULL,
 *   INDEX(host_ip, host_name, event_type,event_name,event_time)
 * )
 * </pre>
 * 
 * <p>Here is an example of how you configure this plugin in <code>quartz.properties</code> file.
 * <pre>
 * # Jdbc Scheduler History Plugin
 * org.quartz.plugin.MyJobHistoryPlugin.class = myschedule.quartz.extra.JdbcSchedulerHistoryPlugin
 * org.quartz.plugin.MyJobHistoryPlugin.insertSql = INSERT INTO qrtz_scheduler_history VALUES(?,?,?,?,?,?,?,?,?,?,?)
 * org.quartz.plugin.MyJobHistoryPlugin.dataSourceName = quartzDataSource
 * 
 * # JobStore: JDBC jobStoreTX
 * org.quartz.dataSource.quartzDataSource.driver = com.mysql.jdbc.Driver
 * org.quartz.dataSource.quartzDataSource.URL = jdbc:mysql://localhost:3306/quartz2
 * org.quartz.dataSource.quartzDataSource.user = quartz2
 * org.quartz.dataSource.quartzDataSource.password = quartz2123
 * org.quartz.dataSource.quartzDataSource.maxConnections = 9
 * </pre>
 *  
 * <p>The <code>event_type</code> can be either <code>SchedulerListener</code> or <code>TriggerListener</code>, and 
 * <code>event_name</code> will contain the method
 * name that it was invoked. Note that not all methods are recorded! We only recorded some that we think they are
 * important to track on general purpose.
 * 
 * <p>For <code>SchedulerListener</code>, we are recording these methods: jobScheduled, jobUnscheduled, triggerPaused,
 * triggersPaused, triggerResumed, triggersResumed, schedulerError, schedulerInStandbyMode, schedulerStarted,
 * and schedulerShutdown.
 * 
 * <p>For <code>TriggerListener</code>, we are recording these methods: triggerFired, triggerComplete and 
 * triggerMisfired.
 * 
 * <p>If <code>event_type</code> is <code>TriggerListener</code> then info1 = trigger key, info2 = job key, 
 * info3 = [FireInstanceId], info4 = [fireTime], info5 = [CompletedExecutionInstruction].
 *  
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class JdbcSchedulerHistoryPlugin implements SchedulerPlugin {
		
	private static final Logger logger = LoggerFactory.getLogger(JdbcSchedulerHistoryPlugin.class);
	private String name;
	private Scheduler scheduler;
	private String localIp;
	private String localHost;
	private String schedulerNameAndId;
	
	private String dataSourceName;
	private String insertSql;
	
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	private void insertHistory(String sql, Object[] params) {
		Connection conn = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection(dataSourceName);
			PreparedStatement stmt = conn.prepareStatement(insertSql);
			for (int i = 1; i <= params.length; i++) {
				stmt.setObject(i, params[i - 1]);
			}
			int result = stmt.executeUpdate();
			logger.info("History record inserted: {}", result);
			stmt.close();
		} catch (SQLException e) {
			throw new QuartzRuntimeException("Failed to insert history record.", e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
					conn = null;
				} catch (SQLException e) {
					throw new QuartzRuntimeException("Failed to close DB connection.", e);
				}
			}
		}
	}

	private String retrieveLocalHost() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	private String retrieveLocalIp() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String retrieveSchedulerNameAndId() {
		try {
			return scheduler.getSchedulerName() + "_$_" + scheduler.getSchedulerInstanceId();
		} catch (SchedulerException e) {
			throw new QuartzRuntimeException(e);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public void initialize(String name, Scheduler scheduler) throws SchedulerException {
		this.name = name;
		this.scheduler = scheduler;
		this.localIp = retrieveLocalIp();
		this.localHost = retrieveLocalHost();
		this.schedulerNameAndId = retrieveSchedulerNameAndId();
		
		//register listeners
		scheduler.getListenerManager().addTriggerListener(new HistoryTriggerListener());
		scheduler.getListenerManager().addSchedulerListener(new HistorySchedulerListener());
	}
	
	@Override
	public void start() {
		logger.info(name + " has started.");
	}

	@Override
	public void shutdown() {
		logger.info(name + " has shutdown.");
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listeners
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private class HistorySchedulerListener implements SchedulerListener {

		@Override
		public void jobScheduled(Trigger trigger) {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"jobScheduled",
				new Date(),
				trigger.getKey().toString(),
				trigger.getJobKey().toString(),
				null,
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void jobUnscheduled(TriggerKey triggerKey) {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"jobUnscheduled",
				new Date(),
				triggerKey.toString(),
				null,
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void triggerPaused(TriggerKey triggerKey) {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"triggerPaused",
				new Date(),
				triggerKey.toString(),
				null,
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void triggersPaused(String triggerGroup) {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"triggersPaused",
				new Date(),
				triggerGroup,
				null,
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void triggerResumed(TriggerKey triggerKey) {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"triggerResumed",
				new Date(),
				triggerKey.toString(),
				null,
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void triggersResumed(String triggerGroup) {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"triggersResumed",
				new Date(),
				triggerGroup,
				null,
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void schedulerError(String msg, SchedulerException cause) {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"schedulerError",
				new Date(),
				msg,
				cause.getClass().getName(),
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void schedulerInStandbyMode() {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"schedulerInStandbyMode",
				new Date(),
				null,
				null,
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void schedulerStarted() {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"schedulerStarted",
				new Date(),
				null,
				null,
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void schedulerShutdown() {
			// TODO: We can not insert SQL data here yet. See QTZ-257.
			//       For now, the workaround is use schedulerShuttingdown(), which called before all pending jobs are
			//       completed.
//			Object[] params = new Object[] {
//				localIp,
//				localHost,
//				schedulerNameAndId,
//				"SchedulerListener",
//				"schedulerShutdown",
//				new Date(),
//				null,
//				null,
//				null,
//				null,
//				null
//			};
//			insertHistory(insertSql, params);
		}

		@Override
		public void schedulerShuttingdown() {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"SchedulerListener",
				"schedulerShuttingdown",
				new Date(),
				null,
				null,
				null,
				null,
				null
			};
			insertHistory(insertSql, params);
		}

		@Override
		public void schedulingDataCleared() {
		}

		@Override
		public void jobAdded(JobDetail jobDetail) {
		}

		@Override
		public void jobDeleted(JobKey jobKey) {
		}

		@Override
		public void jobPaused(JobKey jobKey) {
		}

		@Override
		public void jobsPaused(String jobGroup) {
		}

		@Override
		public void jobResumed(JobKey jobKey) {
		}

		@Override
		public void jobsResumed(String jobGroup) {
		}
		
		@Override
		public void triggerFinalized(Trigger trigger) {
		}
	}
	
	private class HistoryTriggerListener implements TriggerListener {
		@Override
		public String getName() {
			return name;
		}
	
		@Override
		public void triggerFired(Trigger trigger, JobExecutionContext context) {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"TriggerListener",
				"triggerFired",
				new Date(),
				trigger.getKey().toString(),
				trigger.getJobKey().toString(),
				context.getFireInstanceId(),
				context.getFireTime(),
				null
			};
			
			insertHistory(insertSql, params);
		}
		
		@Override
		public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
			// Do nothing.
			return false;
		}
	
		@Override
		public void triggerMisfired(Trigger trigger) {		
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"TriggerListener",
				"triggerMisfired",
				new Date(),
				trigger.getKey().toString(),
				trigger.getJobKey().toString(),
				null,
				null,
				null
			};
			
			insertHistory(insertSql, params);
		}
	
		@Override
		public void triggerComplete(Trigger trigger, JobExecutionContext context,
				CompletedExecutionInstruction triggerInstructionCode) {
			Object[] params = new Object[] {
				localIp,
				localHost,
				schedulerNameAndId,
				"TriggerListener",
				"triggerComplete",
				new Date(),
				trigger.getKey().toString(),
				trigger.getJobKey().toString(),
				context.getFireInstanceId(),
				context.getFireTime(),
				triggerInstructionCode.toString()
			};
			
			insertHistory(insertSql, params);
		}
	}
}
