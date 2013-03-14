package myschedule.web;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Custom URL protocol handler to load any resource files using 'classpath' prefix. If 'classpath' is not found, then
 * it will try using default java.net.URL. And if that fails, then try local java.io.File(path) location. 
 * 
 * <p>
 * You may use this class like this:
 * {@code
 * URL url = new URL(null, "classpath:atest/config.properties", new ClasspathURLStreamHandler());
 * }
 * @see java.net.URL#URL(URL, String, URLStreamHandler)
 * @author Zemian Deng
 */
public class ClasspathURLStreamHandler extends URLStreamHandler {
	public static final String CLASSPATH_PREFIX = "classpath";
	private ClassLoader classLoader;

	public ClasspathURLStreamHandler() {
		this.classLoader = Thread.currentThread().getContextClassLoader();
	}

	public ClasspathURLStreamHandler(ClassLoader classLoader) {
		if (classLoader == null)
			this.classLoader = Thread.currentThread().getContextClassLoader();
		else
			this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		String protocol = url.getProtocol();
		if (CLASSPATH_PREFIX.equals(protocol)) {
			String path = url.getPath();
			while (path.startsWith("/"))
				path = path.substring(1);
			URL resUrl = getClassLoader().getResource(path);
			if (resUrl == null)
				throw new IllegalArgumentException("Classpath resource: " + path + " not found.");
			return resUrl.openConnection();
		} else {
			// Use default JDK url impl.
			try {
				return new URL(url.getPath()).openConnection();
			} catch (MalformedURLException e) {
				// Try again with simple File path location.
				File file = new File(url.getPath());
				return file.toURI().toURL().openConnection();
			}			
		}
	}

	public static URL createURL(String url) {
		return createURL(url, ClasspathURLStreamHandler.class.getClassLoader());
	}

	/**
	 * Create URL instance from a url string. This will use ClasspathURLStreamHandler which supports 'classpath'.
	 * <p>
	 * If url string does not contains any valid protocol, it will default to 'file' protocol.
	 * @param url
	 * @param classLoader
	 * @return URL instance.
	 */
	public static URL createURL(String url, ClassLoader classLoader) {
		URL context = null;
		URL urlObj = null;
		try {
			urlObj = new URL(context, url, new ClasspathURLStreamHandler(classLoader));
		} catch (MalformedURLException e) {
			// Retry with file:// protocol.
			try {
				urlObj = new File(url).toURI().toURL();
			} catch (MalformedURLException e2) {
				throw new IllegalArgumentException("Failed to parse " + url, e); // we report first "e".
			}
		}
		return urlObj;
	}
}