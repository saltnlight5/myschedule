/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package myschedule.web.ui;

import java.util.ArrayList;
import java.util.List;

import org.quartz.SchedulerMetaData;

import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.web.MySchedule;

import com.vaadin.Application;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * A Vaadin UI application to manage Quartz.
 */
public class MyScheduleApp extends Application
{	
	private static final long serialVersionUID = 1L;
	private static MySchedule mySchedule = MySchedule.getInstance();

	@Override
    public void init()
    {
        setMainWindow(createWindow());
    }

	private Window createWindow() {
		VerticalLayout layout = new VerticalLayout();
        layout.addComponent(createToolbar());
        layout.addComponent(createShedulerSettingsTable());
        
        Window window = new Window("MySchedule - A UI for managing Quartz Schedulers");
        window.setContent(layout);
        return window;
	}

	 private Component createToolbar() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(new Button("Settings"));
		layout.addComponent(new Button("New Scheduler"));
		return layout;
	}

	private Component createShedulerSettingsTable() {
	    Table table = new Table();
	    table.setContainerDataSource(createTableDataSource());
		return table;
	}

	private Container createTableDataSource() {
		// ConfigId = settingsName
		List<SchedulerSettingsRow> rows = new ArrayList<SchedulerSettingsRow>();
		for (String settingsName : mySchedule.getSchedulerSettingsNames()) {
			SchedulerSettingsRow row = new SchedulerSettingsRow();
			row.setConfigId(settingsName);
			
			SchedulerTemplate scheduler = mySchedule.getScheduler(settingsName);
			if (scheduler != null) {
				row.setSchedulerInfo(scheduler.getMetaData());
			}
			rows.add(row);
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Container dataSource = new BeanItemContainer(SchedulerSettingsRow.class, rows);
        return dataSource;
    }
	
	public static class SchedulerSettingsRow {
		private String configId;
		private SchedulerMetaData schedulerInfo;
		
		public void setConfigId(String configId) {
			this.configId = configId;
		}
		public String getConfigId() {
			return configId;
		}
		public SchedulerMetaData getSchedulerInfo() {
			return schedulerInfo;
		}
		public void setSchedulerInfo(SchedulerMetaData schedulerInfo) {
			this.schedulerInfo = schedulerInfo;
		}
	}
}
