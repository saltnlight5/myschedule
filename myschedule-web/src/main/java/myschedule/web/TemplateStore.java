package myschedule.web;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A service responsible to store and retrieve template files and content through the MySchedule storage area.
 *
 * @author Zemian Deng
 */
public class TemplateStore extends AbstractService {
    public static final String FILE_EXT = ".txt";
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateStore.class);
    private File storeDir;

    @Override
    public void initService() {
        if (storeDir == null)
            throw new RuntimeException("Template store dir can not be empty.");

        // Get configDir and create it if not exists
        if (!storeDir.exists()) {
            LOGGER.info("Creating template store dir={}.", storeDir);
            storeDir.mkdirs();
        }
    }

    @Override
    public void destroyService() {
    }

    public TemplateStore(File storeDir) {
        this.storeDir = storeDir;
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

    private String get(String name) {
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
            String name = fileName.split(FILE_EXT)[0];
            result.add(name);
        }
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
        return new File(storeDir, name + FILE_EXT);
    }
}
