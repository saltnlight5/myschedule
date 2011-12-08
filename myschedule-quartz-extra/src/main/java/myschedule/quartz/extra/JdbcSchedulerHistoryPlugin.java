package myschedule.quartz.extra;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;
import org.quartz.spi.SchedulerPlugin;
import org.quartz.utils.DBConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This plugin will record a row in a database table for each event (methods) in SchedulerPlugin and TriggerListener. 
 * The database table must setup with the following fields, and {@link #insertSql} must be populated
 * correctly with 10 binding parameters.
 * 
 * <pre>
 * CREATE TABLE qrtz_scheduler_history (
 *   host_ip VARCHAR(15) NOT NULL,
 *   host_name VARCHAR(256) NOT NULL,
 *   event_type VARCHAR(128) NOT NULL,
 *   event_name VARCHAR(128) NOT NULL,
 *   event_time TIMESTAMP NOT NULL,
 *   info1 VARCHAR(256) NULL,
 *   info2 VARCHAR(256) NULL,
 *   info3 VARCHAR(256) NULL,
 *   info4 VARCHAR(256) NULL,
 *   info5 VARCHAR(256) NULL,
 *   PRIMARY KEY (host_ip, event_type,event_name,event_time)
 * )
 * </pre>
 *  * 
 * <p>If event_type is SchedulerPlugin, then info1 has the plugin name.
 * 
 * <p>If event_type is TriggerListener then info1 = trigger key, info2 = job key, info3 = [FireInstanceId],
 * info4 = [fireTime], info5 = [CompletedExecutionInstruction].
 * 
 * <p>You must also set the {@link #dataSourceName} name in the quartz.properties. It can be the same data source you
 * setup for the JdbcJobStore configuration (see Quartz doc.) If you use this, then ensure you increase the conn pool
 * size to one more.
 * 
 * <p>Here is a example of what you need to set in <code>quartz.properties</code> file.
 * <pre>
 * # Scripting plugin
 * org.quartz.plugin.MyJobHistoryPlugin.class = myschedule.quartz.extra.JdbcSchedulerHistoryPlugin
 * org.quartz.plugin.MyJobHistoryPlugin.insertSql = INSERT INTO qrtz_scheduler_history VALUES(?,?,?,?,?,?,?,?,?,?)
 * org.quartz.plugin.MyJobHistoryPlugin.dataSourceName = myQuartzDataSource
 * </pre>
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class JdbcSchedulerHistoryPlugin implements SchedulerPlugin, TriggerListener {
		
	private static final Logger logger = LoggerFactory.getLogger(JdbcSchedulerHistoryPlugin.class);
	private String name;
	private String localIp;
	private String localHost;
	
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
				stmt.setObject(i, params[i]);
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
				} catch (SQLException e) {
					throw new QuartzRuntimeException("Failed to close DB connection.", e);
				}
			}
		}
	}

	private String getLocalHost() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostName();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	private String getLocalIp() {
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			return localHost.getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(String name, Scheduler scheduler) throws SchedulerException {
		this.name = name;
		this.localIp = getLocalIp();
		this.localHost = getLocalHost();
		
		Object[] params = new Object[] {
			localIp,
			localHost,
			"SchedulerPlugin",
			"initialize",
			new Date(),
			name, /* info1 */
			null, /* info2 */
			null, /* info3 */
			null, /* info4 */
			null  /* info5 */
		};
		
		insertHistory(insertSql, params);		
		logger.info(name + " intialized on {}/{}", localIp, localHost);
	}
	
	@Override
	public void start() {
		Object[] params = new Object[] {
			localIp,
			localHost,
			"SchedulerPlugin",
			"start",
			new Date(),
			name,
			null,
			null,
			null,
			null
		};
		
		insertHistory(insertSql, params);
		logger.info(name + " has started.");
	}

	@Override
	public void shutdown() {
		Object[] params = new Object[] {
			localIp,
			localHost,
			"SchedulerPlugin",
			"shutdown",
			new Date(),
			name,
			null,
			null,
			null,
			null
		};
		
		insertHistory(insertSql, params);
		logger.info(name + " has shutdown.");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void triggerFired(Trigger trigger, JobExecutionContext context) {
		if (insertSql == null) {
			return;
		}
		
		Object[] params = new Object[] {
			localIp,
			localHost,
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
		if (insertSql == null) {
			return false;
		}
		
		Object[] params = new Object[] {
			localIp,
			localHost,
			"TriggerListener",
			"vetoJobExecution",
			new Date(),
			trigger.getKey().toString(),
			trigger.getJobKey().toString(),
			context.getFireInstanceId(),
			context.getFireTime(),
			null
		};
		
		insertHistory(insertSql, params);
		return false;
	}

	@Override
	public void triggerMisfired(Trigger trigger) {
		if (insertSql == null) {
			return;
		}
		
		Object[] params = new Object[] {
			localIp,
			localHost,
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
		if (insertSql == null) {
			return;
		}
		
		Object[] params = new Object[] {
			localIp,
			localHost,
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
