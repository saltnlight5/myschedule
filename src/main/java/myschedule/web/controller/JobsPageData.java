package myschedule.web.controller;

import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobsPageData {
	private String schedulerSummary;	
	private List<JobInfo> jobs;
		
	/**
	 * Getter.
	 * @return the schedulerSummary - String
	 */
	public String getSchedulerSummary() {
		return schedulerSummary;
	}

	/**
	 * Setter
	 * @param schedulerSummary String, the schedulerSummary to set
	 */
	public void setSchedulerSummary(String schedulerSummary) {
		this.schedulerSummary = schedulerSummary;
	}

	/**
	 * Getter.
	 * @return the jobs - List<JobInfo>
	 */
	public List<JobInfo> getJobs() {
		return jobs;
	}

	/**
	 * Setter
	 * @param jobs List<JobInfo>, the jobs to set
	 */
	public void setJobs(List<JobInfo> jobs) {
		this.jobs = jobs;
	}

	public static class JobInfo implements Comparable<JobInfo> {
		
		private JobDetail jobDetail;
		private Trigger trigger;
		private String triggerInfo;
		
		@Override
		public int compareTo(JobInfo that) {
			return jobDetail.getFullName().compareTo(that.getJobDetail().getFullName());
		}
		
		/**
		 * Getter.
		 * @return the triggerInfo - String
		 */
		public String getTriggerInfo() {
			return triggerInfo;
		}
		
		/**
		 * Setter
		 * @param triggerInfo String, the triggerInfo to set
		 */
		public void setTriggerInfo(String triggerInfo) {
			this.triggerInfo = triggerInfo;
		}
		
		/**
		 * Getter.
		 * @return the trigger - Trigger
		 */
		public Trigger getTrigger() {
			return trigger;
		}

		/**
		 * Setter
		 * @param trigger Trigger, the trigger to set
		 */
		public void setTrigger(Trigger trigger) {
			this.trigger = trigger;
		}

		/**
		 * Getter.
		 * @return the jobDetail - JobDetail
		 */
		public JobDetail getJobDetail() {
			return jobDetail;
		}

		/**
		 * Setter
		 * @param jobDetail JobDetail, the jobDetail to set
		 */
		public void setJobDetail(JobDetail jobDetail) {
			this.jobDetail = jobDetail;
		}
	}
}
