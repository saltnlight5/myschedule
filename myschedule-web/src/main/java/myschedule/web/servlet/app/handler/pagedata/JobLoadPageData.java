package myschedule.web.servlet.app.handler.pagedata;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** JobLoadPageData
 *
 * @author Zemian Deng
 */
public class JobLoadPageData {
	@Getter @Setter
	private List<String> jobGroupsToNeverDelete;
	@Getter @Setter
	private List<String> triggerGroupsToNeverDelete;
	@Getter @Setter
	private List<String> loadedJobs; // full name.
	@Getter @Setter
	private List<String> loadedTriggers; // full name.
	@Getter @Setter
	private boolean ignoreDuplicates;
	@Getter @Setter
	private boolean overWriteExistingData;
}
