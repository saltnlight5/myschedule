package myschedule.web;

public class UrlRequestMapper {
	static {
		mapPathHandler("/dashboard/index", Dashboard.HOME);
	}
	
	public static void mapPathHandler(String path, PathHandler handler) {
		throw new RuntimeException("Not yet impl.");
	}
	
	public static interface PathHandler {
		public Object handlePath(String path);
	}
	
	public static class Dashboard {
		public static PathHandler HOME = new PathHandler() {
			@Override
			public Object handlePath(String path) {
				throw new RuntimeException("Not yet impl.");
			}			
		};
	}
}
