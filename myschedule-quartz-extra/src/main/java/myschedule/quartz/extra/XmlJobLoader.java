package myschedule.quartz.extra;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.simpl.CascadingClassLoadHelper;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.xml.XMLSchedulingDataProcessor;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

/**
 * Extending XMLSchedulingDataProcessor to expose two getter methods, {@link #getLoadedJobs()} and
 * {@link #getLoadedTriggers()}.
 * <p/>
 * <p/>
 * The XMLSchedulingDataProcessor is more than just a loader, it also keep data after loaded such
 * as list of loaded jobs. This class will expose those fields so user may access them after them.
 * <p/>
 * <p/>
 * The XMLSchedulingDataProcessor constructor requires a CascadingClassLoadHelper that must be
 * initialized! If you forget to initialize the ClassLoadHelper parameter before passing to constructor,
 * you will always ended up a xml xsd schema default to the public web version and not the
 * one comes with the quartz jar! This can be a really nasty hidden problem that makes your application
 * unusable offline! We can't automatically call it in constructor either, so we provide a static helper
 * XmlJobLoader.newInstance() to remind and workaround this problem.
 *
 * @author Zemian Deng
 */
public class XmlJobLoader extends XMLSchedulingDataProcessor {

    public static final String XML_SYSTEM_ID = XMLSchedulingDataProcessor.QUARTZ_SYSTEM_ID_JAR_PREFIX;

    /**
     * A simple factory method that automatically initialize a new CascadingClassLoadHelper and
     * pass to XmlJobLoader.
     */
    public static XmlJobLoader newInstance() {
        CascadingClassLoadHelper clhelper = new CascadingClassLoadHelper();
        clhelper.initialize(); // we must initialize this first!
        try {
            return new XmlJobLoader(clhelper);
        } catch (ParserConfigurationException e) {
            throw new QuartzRuntimeException("Failed to construct XmlJobLoader.", e);
        }
    }

    /**
     * The ClassLoadHelper parameter must be initialized before pass in here!
     */
    public XmlJobLoader(ClassLoadHelper clhelper) throws ParserConfigurationException {
        super(clhelper);
    }

    /**
     * Expose getter with public access.
     */
    @Override
    public List<JobDetail> getLoadedJobs() {
        return super.getLoadedJobs();
    }

    /**
     * Expose getter with public access.
     */
    @Override
    public List<Trigger> getLoadedTriggers() {
        return super.getLoadedTriggers();
    }

}
