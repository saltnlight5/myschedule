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
package myschedule.web;

import java.util.Collections;
import java.util.List;

import myschedule.quartz.extra.SchedulerTemplate;
import myschedule.quartz.extra.job.LoggerJob;

import org.quartz.Trigger;

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
public class MyScheduleUiApp extends Application
{	
    @Override
    public void init()
    {	
        setMainWindow(createWindow());
    }

	private Window createWindow() {
		VerticalLayout layout = new VerticalLayout();
        //layout.setSizeFull();
        layout.addComponent(createToolbar());
        layout.addComponent(createTable());
        
        Window window = new Window("MySchedule - A UI for managing Quartz Schedulers");
        window.setContent(layout);
        return window;
	}

	 private Component createToolbar() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.addComponent(new Button("Create New Scheduler"));
		layout.addComponent(new Button("WebApp Settings"));
		return layout;
	}

	private Component createTable() {
		String[] columns = new String[]{ "key", "jobKey", "description", "nextFireTime", "previousFireTime", 
				"startTime", "endTime", "misfireInstruction", "priority", "calendarName"};
	    Table table = new Table();
	    table.setContainerDataSource(createTableDataSource());
	    for (Object s : table.getVisibleColumns()) System.out.println(s);
	    table.setVisibleColumns(columns);
		return table;
	}

	private Container createTableDataSource() {
		List<Trigger> triggers = Collections.EMPTY_LIST;
		Container dataSource = new BeanItemContainer(Trigger.class, triggers);
        return dataSource;
    }
}
