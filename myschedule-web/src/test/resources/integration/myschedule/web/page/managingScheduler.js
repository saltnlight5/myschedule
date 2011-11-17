// Delete all jobs
var jobs = scheduler.getAllJobDetails();
for (var i = 0; i < jobs.size(); i++) {
  var job = jobs.get(i);
  scheduler.deleteJob(job.key);
  output.println("Job " + job.key + " deleted.");
}