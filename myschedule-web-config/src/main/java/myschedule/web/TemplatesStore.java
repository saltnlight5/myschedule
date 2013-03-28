package myschedule.web;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * A service responsible to store and retrieve template files and content through the MySchedule storage area.
 *
 * @author Zemian Deng
 */
public class TemplatesStore extends AbstractService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplatesStore.class);
    public static final String FIRST_TIME_MARKER = ".first-time.marker";
    private File storeDir;
    private String fileExt;
    private String[] defaultTemplateResNames;

    public TemplatesStore(File storeDir, String fileExt, String[] defaultTemplateResNames) {
        this.storeDir = storeDir;
        this.fileExt = fileExt;
        this.defaultTemplateResNames = defaultTemplateResNames;
    }

    @Override
    public void initService() {
        if (storeDir == null)
            throw new RuntimeException("Template store dir can not be empty.");

        // Get configDir and create it if not exists
        if (!storeDir.exists()) {
            LOGGER.info("Creating template store dir={}.", storeDir);
            storeDir.mkdirs();
        }
        initDefaultTemplates();
        LOGGER.debug("Service TemplatesStore[" + storeDir.getName() + "] is ready.");
    }

    private void initDefaultTemplates() {
        File markerFile = new File(storeDir, FIRST_TIME_MARKER);
        if (markerFile.exists())
            return; // Do nothing if we already done this before.

        // Create a marker file so we do not generate default templates again
        LOGGER.debug("Generating first time marker file={}", markerFile);
        writeFile(markerFile, "" + new Date());

        // Now generate default template files from our jar resource files.
        Class cls = getClass();
        for (String name : defaultTemplateResNames) {
            InputStream istream = cls.getResourceAsStream(name);
            if (istream == null) {
                LOGGER.warn("Default template file resource name={} not found.", name);
                continue;
            }
            FileWriter writer = null;
            try {
                String baseName = new File(name).getName();
                File outFile = new File(storeDir, baseName);
                writer = new FileWriter(outFile);
                LOGGER.debug("Generating default template file={}", outFile);
                IOUtils.copy(istream, writer);
            } catch (IOException e) {
                LOGGER.warn("Failed to generate default template file from resource name " + name, e);
            } finally {
                if (writer != null)
                    IOUtils.closeQuietly(writer);
                IOUtils.closeQuietly(istream); // We already verified it's non-null above.
            }
        }
    }

    @Override
    public void destroyService() {
    }

    public void add(String name, String content) {
        File file = getTemplateFile(name);
        LOGGER.info("Adding new template file={}", file);
        writeFile(file, content);
    }

    private void writeFile(File file, String text) {
        try {
            FileUtils.write(file, text);
        } catch (IOException e) {
            LOGGER.error("Failed to write template file={}", file, e);
        }
    }

    public void remove(String name) {
        File file = getTemplateFile(name);
        LOGGER.info("Deleting template file={}", file);
        if (!file.delete()) {
            throw new RuntimeException("Failed to delete template file=" + file);
        }
    }

    public String get(String name) {
        File file = getTemplateFile(name);
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read template file={}" + file);
        }
    }

    public List<String> getNames() {
        List<String> result = new ArrayList<String>();
        File[] files = storeDir.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (FIRST_TIME_MARKER.equals(fileName))
                continue;
            String name = StringUtils.isEmpty(fileExt) ? fileName : fileName.split(fileExt)[0];
            result.add(name);
        }

        // Sort them in reverse way (by extension name first)
        Collections.sort(result, new Comparator<String>() {
            @Override
            public int compare(final String left, final String right) {
                String[] leftWords = StringUtils.split(left, ".");
                String[] rightWords = StringUtils.split(right, ".");
                if (leftWords.length >= 2 && rightWords.length >= 2 ) {
                    if (!(leftWords[leftWords.length - 1].equals(rightWords[rightWords.length - 1]))) {
                        int comp = leftWords[leftWords.length - 1].compareTo(rightWords[rightWords.length - 1]);
                        if (comp != 0) {
                            return comp;
                        }
                    }
                }
                return left.compareTo(right);
            }
        });
        return result;
    }

    public void update(String name, String content) {
        File file = getTemplateFile(name);
        writeFile(file, content);
    }

    public boolean exists(String name) {
        return getTemplateFile(name).exists();
    }

    private File getTemplateFile(String name) {
        String fileName = StringUtils.isEmpty(fileExt) ? name : name + fileExt;
        return new File(storeDir, fileName);
    }
}
