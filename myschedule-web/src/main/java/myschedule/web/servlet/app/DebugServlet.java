package myschedule.web.servlet.app;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.Data;
import myschedule.web.servlet.ActionHandler;
import myschedule.web.servlet.ActionHandlerServlet;
import myschedule.web.servlet.UrlRequestActionHandler;
import myschedule.web.servlet.ViewData;



/**
 * A debug controller for debug application and experimenting new ideas.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class DebugServlet extends ActionHandlerServlet {
	
	private static final long serialVersionUID = 1L;
	private static final String ACTION_PATH_PREFIX = "/debug-cfb5d8df-fc73-4a23-b8a4-080b29d5f022";
	
	@Override
	public void init() {
		setServletPathName(ACTION_PATH_PREFIX);
		setViewFileNamePrefix("/WEB-INF/jsp/debug");
		
		addActionHandler("/", debugHandler);
	}
	
	protected ActionHandler debugHandler = new UrlRequestActionHandler() {
		@Override
		protected void handleViewData(ViewData viewData) {
			String contextPath = viewData.getRequest().getContextPath();
			viewData.addData("debugActionPath", contextPath + ACTION_PATH_PREFIX);
			
			String viewName = viewData.getViewName();
			if (viewName.endsWith("/index")) {
				// Get a list of dir content of where the viewName path is.
				String actionDir = viewName.substring(0, viewName.length() - 5);
				String dirPrefix = getViewFileNamePrefix();
				File dir = new File(getServletContext().getRealPath(dirPrefix + actionDir));
				File[] files = dir.listFiles();
				Arrays.sort(files);
				List<String> fileNames = new ArrayList<String>();
				List<String> dirNames = new ArrayList<String>();
				for (File file:  files) {
					String name = file.getName();
					if (name.equals("index.jsp")) {
						continue;
					}
					if (file.isDirectory()) {
						dirNames.add(file.getName());
					} else {
						String nameNoExt = name.substring(0, name.lastIndexOf("."));
						fileNames.add(nameNoExt);
					}
				}
				viewData.addData("actionDir", actionDir);
				viewData.addData("fileNames", fileNames);
				viewData.addData("dirNames", dirNames);
			} else if ("/show-sysprops".equals(viewName)) {
				// Get Java sys props
				Map<String, String> sysProps = new TreeMap<String, String>();
				for (String name : System.getProperties().stringPropertyNames()) {
					sysProps.put(name, System.getProperty(name));
				}
				viewData.addData("sysProps", sysProps);
			} else if ("/show-env".equals(viewName)) {
				// Get sys env vars
				Map<String, String> sysProps = new TreeMap<String, String>();
				for (String name : System.getenv().keySet()) {
					sysProps.put(name, System.getenv(name));
				}
				viewData.addData("env", sysProps);
			}
		}			
	};
	
	@Data
	public static class FileDetail {
		private File file;
		private String nameWithoutExt;
	}
}
