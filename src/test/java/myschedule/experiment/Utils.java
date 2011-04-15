package myschedule.experiment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utils
 *
 * @author Zemian Deng
 */
public class Utils {
	
	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * Generate a stacktrace with method call stack and log it.
	 */
	public static void stackTraceCheckpoint() {
		try { 
			throw new RuntimeException(); 
		} 
		catch (RuntimeException e) { 
			logger.info("StackTrace Checkpoint.", e); 
		}
	}
	
	public static class Getter {
		public Object object;
		public Method method;
		public String propName;
	}
	
	/**
	 * Get all the getter methods from an object, including its parent classes, except getClass(). 
	 * A getter method is defined as zero parameter and starts with getX, hasX, or isX.
	 * 
	 * @param object - object to look getters with.
	 * @param upToClass - stop traversing up to this class to look for getters.
	 */
	public static List<Getter> getGetters(Object object, Class<?> upToClass) {
		if (upToClass == null)
			upToClass = Object.class;
		List<Getter> getters = new ArrayList<Getter>();
		Class<?> objCls = object.getClass();
		Class<?> parentCls = objCls.getSuperclass();
		Class<?> rootCls = Object.class;
		while (parentCls != null && !parentCls.equals(rootCls)) {
			Method[] methods = parentCls.getMethods();
			for (Method method : methods) {
				if (method.getParameterTypes().length > 0)
					continue; // skip any non-zero param methods.

				String methodName = method.getName();
				if (methodName.equals("getClass"))
					continue; // skip getClass method.
				
				String propName = null;
				if (methodName.length() >= 4 && methodName.startsWith("get")) {
					propName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);		
				} else if (methodName.length() >= 4 && methodName.startsWith("has")) {
					propName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
				} if (methodName.length() >= 3 && methodName.startsWith("is")) {
					propName = methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
				}
				
				if (propName != null) {
					Getter getter = new Getter();
					getter.object = object;
					getter.propName = propName;
					getter.method = method;
				}
			}
			if (parentCls.equals(upToClass)) {
				parentCls = null;
			} else {
				parentCls = parentCls.getSuperclass();
			}
		}
		return getters;
	}
	
	public static Object getGetterValue(Getter getter) {
		try {
			return getter.method.invoke(getter.object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getGetterStrValue(Getter getter) {
		return getGetterValue(getter).toString();
	}
	
	public static String toObjectString(Object object) {
		return object.getClass().getName() + "@" + System.identityHashCode(object);
	}
	
	public static String join(Collection<?> items, String sep) {
		if (items.isEmpty())
			return "";		
		Iterator<?> itr = items.iterator();
		StringBuilder sb = new StringBuilder();
		if (itr.hasNext())
			sb.append(itr.next().toString());
		while (itr.hasNext()) {
			sb.append(sep).append(itr.next().toString());
		}
		return sb.toString();
	}
	
	public static String toString(Object object) { return toString(object, ", "); }
	public static String toString(Object object, String sep) {
		StringBuilder sb = new  StringBuilder(toObjectString(object));
		if (object instanceof CharSequence) {
			return object.toString();
		} else if (object instanceof Number) {
			return object.toString();
		} else if (object.getClass().isArray()) {
			Object[] array = (Object[])object;
			sb.append("{");
			if (array.length > 0)
				sb.append(sep).append(join(Arrays.asList(array), sep)).append(sep);
			sb.append("}").append(", size=").append(array.length);
		}  else if (object instanceof Collection<?>) {
			List<Object> list = new ArrayList<Object>((Collection<?>)object);
			sb.append("{");
			if (list.size() > 0)
				sb.append(sep).append(join(list, sep)).append(sep);
			sb.append("}").append(", size=").append(list.size());
		} else if (object instanceof Map<?,?>) {
			Map<?,?> map = (Map<?,?>)object;
			sb.append("{");
			if (map.size() > 0)
				sb.append(sep).append(join(map.entrySet(), sep)).append(sep);
			sb.append("}").append(", size=").append(map.size());
		} else {
			List<Getter> getters = getGetters(object, null);
			if (getters.size() > 0) {
				sb.append("{");
				List<String> propVals = new ArrayList<String>();
				for (Getter getter : getters) {
					String val = getGetterStrValue(getter);
					propVals.add(getter.propName + "=" + val);
				}
				sb.append(join(propVals, sep));
				sb.append("}");
			}
		}
		return sb.toString();
	}
	
	/** Print object's toString recursively. */
	public static void dump(Object object) {
		logger.info(toString(object, "\n"));
	}
	
}
