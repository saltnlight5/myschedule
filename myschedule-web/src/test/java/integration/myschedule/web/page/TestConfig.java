package integration.myschedule.web.page;

import lombok.Getter;

public class TestConfig {
	
	// This class singleton instance access
	// ====================================
	protected static TestConfig instance;
	
	protected TestConfig() {
	}
	
	synchronized public static TestConfig getInstance() {
		if (instance == null) {
			instance = new TestConfig();
		}
		return instance;
	}

	// Web Server Config
	// =================
	
	@Getter
	private String webappUrl = "http://localhost:8080/myschedule";
}
