package myschedule.quartz.extra;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerBuilder;
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
 * Here is an example of how to create the history table on MySQL.
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
 * And here is an example of how to create the history table on Oracle.
 * <pre>
 * CREATE TABLE "QRTZ_SCHEDULER_HISTORY"
 * (
 * "HOST_IP" VARCHAR2(120 BYTE) NOT NULL,
 * "HOST_NAME" VARCHAR2(200 BYTE) NOT NULL,
 * "SCHEDULER_NAME" VARCHAR2(250 BYTE) NOT NULL,
 * "EVENT_TYPE" VARCHAR2(250 BYTE) NOT NULL,
 * "EVENT_NAME" VARCHAR2(250 BYTE) NOT NULL,
 * "EVENT_TIME" TIMESTAMP NOT NULL,
 * "INFO1" VARCHAR2(250 BYTE),
 * "INFO2" VARCHAR2(250 BYTE),
 * "INFO3" VARCHAR2(250 BYTE),
 * "INFO4" VARCHAR2(250 BYTE),
 * "INFO5" VARCHAR2(250 BYTE)
 * );
 * CREATE INDEX QRTZ_SCHEDULER_HISTORY_INDEX on QRTZ_SCHEDULER_HISTORY(HOST_IP, HOST_NAME, EVENT_TYPE,EVENT_NAME,EVENT_TIME);
 * </pre>
 * 
 * <p>Here is an example of how you configure this plugin in <code>quartz.properties</code> file.
 * <pre>
 * # Jdbc Scheduler History Plugin
 * org.quartz.plugin.MyJobHistoryPlugin.class = myschedule.quartz.extra.JdbcSchedulerHistoryPlugin
 * org.quartz.plugin.MyJobHistoryPlugin.insertSql = INSERT INTO qrtz_scheduler_history VALUES(?,?,?,?,?,?,?,?,?,?,?)
 * org.quartz.plugin.MyJobHistoryPlugin.querySql = SELECT * FROM qrtz_scheduler_history ORDER BY event_time DESC
 * org.quartz.plugin.MyJobHistoryPlugin.deleteSql = DELETE qrtz_scheduler_history WHERE event_time < ?
 * org.quartz.plugin.MyJobHistoryPlugin.deleteIntervalInSecs = 604800
 * org.quartz.plugin.MyJobHistoryPlugin.dataSourceName = quartzDataSource
 * org.quartz.plugin.MyJobHistoryPlugin.schedulerContextKey = JdbcSchedulerHistoryPlugin.Instance
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
	
	public static final String DEFAULT_SCHEDULER_CONTEXT_KEY = "JdbcSchedulerHistoryPlugin.Instance";
		
	private static final Logger logger = LoggerFactory.getLogger(JdbcSchedulerHistoryPlugin.class);
	private String name;
	private Scheduler scheduler;
	private String localIp;
	private String localHost;
	private String schedulerNameAndId;
	
	private String dataSourceName;
	private String insertSql;
	private String querySql;
	private String deleteSql;
	private String schedulerContextKey;
	private long deleteIntervalInSecs;
	private int[] columnSqlTypes;
	
	public void setSchedulerContextKey(String schedulerContextKey) {
		this.schedulerContextKey = schedulerContextKey;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public void setQuerySql(String querySql) {
		this.querySql = querySql;
	}
	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}
	public void setDeleteSql(String deleteSql) {
		this.deleteSql = deleteSql;
	}
	public void setDeleteIntervalInSecs(long deleteIntervalInSecs) {
		this.deleteIntervalInSecs = deleteIntervalInSecs;
	}
	public long getDeleteIntervalInSecs() {
		return deleteIntervalInSecs;
	}

	public List<List<Object>> getJobHistoryData() {
		final List<List<Object>> result = new ArrayList<List<Object>>();
		withConn(new ConnAction() {
			@Override
			public void onConn(Connection conn) throws SQLException {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(querySql);
				int colCount = rs.getMetaData().getColumnCount();
				while(rs.next()) {
					List<Object> row = new ArrayList<Object>();
					for (int i = 1; i <= colCount; i++) {
						row.add(rs.getObject(i));
					}
					result.add(row);
				}
			}
		});
		return result;
	}
	
	public int deleteJobHistory(Date olderThanDate) {
		final List<Integer> result = new ArrayList<Integer>();
		withConn(new ConnAction() {
			@Override
			public void onConn(Connection conn) throws SQLException {
				Statement stmt = conn.createStatement();
				int count = stmt.executeUpdate(deleteSql);
				result.add(count);
			}
		});
		return result.get(0);
	}

	private void insertHistory(final String sql, final Object[] params) {
		logger.debug("Insert SQL: {}", sql);
		withConn(new ConnAction() {
			@Override
			public void onConn(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(insertSql);
				if (columnSqlTypes != null) {
					for (int i = 1; i <= params.length; i++) {
						Object param = params[i - 1];
						if (param instanceof Date) {
							long time = ((Date)param).getTime();
							param = new java.sql.Timestamp(time);
						}
						int type = columnSqlTypes[i - 1];
						logger.debug("Binding param[{}]: {}, type={}", new Object[]{i, param, type});
						stmt.setObject(i, param, type);
					}
				} else {
					for (int i = 1; i <= params.length; i++) {
						Object param = params[i - 1];
						logger.debug("Binding param[{}]: {}", i, param);
						stmt.setObject(i, param);
					}
				}
				int result = stmt.executeUpdate();
				logger.info("History record inserted: {}", result);
				stmt.close();
			}
		});
	}
	
	private void withConn(ConnAction action) {
		Connection conn = null;
		try {
			conn = DBConnectionManager.getInstance().getConnection(dataSourceName);
			action.onConn(conn);
		} catch (SQLException e) {
			throw new QuartzRuntimeException("Failed to execute DB connection action.", e);
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
	
	private static interface ConnAction {
		void onConn(Connection conn) throws SQLException;
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
		
		// Register listeners
		scheduler.getListenerManager().addTriggerListener(new HistoryTriggerListener());
		scheduler.getListenerManager().addSchedulerListener(new HistorySchedulerListener());
		
		// Store this plugin instance into scheduler context map
		if (schedulerContextKey == null)
			schedulerContextKey = DEFAULT_SCHEDULER_CONTEXT_KEY;
		scheduler.getContext().put(schedulerContextKey, this);
		logger.info("Added plugin instance {} to scheduler context key: {}", this, schedulerContextKey);
		
		// Extract and find all the SQL types for table columns.
		withConn(new ConnAction() {
			@Override
			public void onConn(Connection conn) throws SQLException {
				PreparedStatement stmt = conn.prepareStatement(querySql);
				ResultSetMetaData metaData = stmt.getMetaData();
				if (metaData != null) {
					int size = metaData.getColumnCount();
					columnSqlTypes = new int[size];
					for (int i = 1; i <= size; i++) {
						int type = metaData.getColumnType(i);
						String typeName = metaData.getColumnTypeName(i);
						String name = metaData.getColumnName(i);
						logger.debug("History table SQL column {}, type={}, typeName={}", 
								new Object[]{name, type, typeName});
						columnSqlTypes[i - 1] = type;
					}
				}
				stmt.close();
			}			
		});
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
			
			// Auto add a job to delete job history if configured to do so
			if (deleteIntervalInSecs > 0) {
				try {
					String jobName = "JobHistoryRemovalJob";
					if (scheduler.checkExists(TriggerKey.triggerKey(jobName))) {
						scheduler.unscheduleJob(TriggerKey.triggerKey(jobName));
						logger.info("The JobHistoryRemovalJob already exist. Removed it from scheduler first.");
					}
					JobDetail job = JobBuilder.newJob(JobHistoryRemovalJob.class).
							withIdentity(jobName).
							usingJobData(JobHistoryRemovalJob.PLUGIN_KEY_NAME, schedulerContextKey).
							build();
					Trigger trigger = TriggerBuilder.newTrigger().withIdentity(jobName).
							withSchedule(SimpleScheduleBuilder.repeatSecondlyForever((int)deleteIntervalInSecs)).
							startAt(new Date(System.currentTimeMillis() - (deleteIntervalInSecs * 1000))).
							build();
					scheduler.scheduleJob(job, trigger);
					logger.info("Added JobHistoryRemovalJob that runs every {} secs.", deleteIntervalInSecs);
				} catch (SchedulerException e) {
					logger.error("Failed to insert JobHistoryRemovalJob.", e);
				}
			}
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
	
	public static class JobHistoryRemovalJob implements Job {
		public static final String PLUGIN_KEY_NAME = "JdbcSchedulerHistoryPluginKey";
		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			try {
				Scheduler scheduler = context.getScheduler();
				String pluginKey = context.getMergedJobDataMap().getString(PLUGIN_KEY_NAME);
				JdbcSchedulerHistoryPlugin plugin = (JdbcSchedulerHistoryPlugin)scheduler.getContext().get(pluginKey);
				long deleteIntervalInSecs = plugin.getDeleteIntervalInSecs();
				Date olderThanDate = new Date(System.currentTimeMillis() - (deleteIntervalInSecs * 1000));
				int result = plugin.deleteJobHistory(olderThanDate);
				logger.info("{} job history records were deleted with date older than {}.", result, olderThanDate);
			} catch (SchedulerException e) {
				throw new JobExecutionException("Failed to run JobHistoryRemovalJob.", e);
			}
		}		
	}
}
