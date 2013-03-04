package myschedule.web;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A service responsible to store and retrieve SchedulerSettings objects through the MySchedule storage area.
 *
 * @author Zemian Deng
 */
public class SchedulerSettingsStore extends AbstractService {
    public static final String SETTINGS_FILE_EXT = ".properties";
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerSettingsStore.class);
    private File storeDir;

    @Override
    public void initService() {
        if (storeDir == null)
            throw new RuntimeException("Scheduler settings store dir can not be empty.");

        // Get configDir and create it if not exists
        if (!storeDir.exists()) {
            LOGGER.info("Creating scheduler settings store dir={}.", storeDir);
            storeDir.mkdirs();
        }
    }

    @Override
    public void destroyService() {
    }

    public String generateSettingsName() {
        int maxTryCount = 1000, tryCount = 0;
        String name = UUID.randomUUID().toString();
        while (exists(name) && tryCount < maxTryCount) {
            name = UUID.randomUUID().toString();
            tryCount++;
        }

        if (tryCount == maxTryCount && exists(name))
            throw new RuntimeException("Unable to generate a unique scheduler settings name in storeDir=" + storeDir);

        return name;
    }

    public SchedulerSettingsStore(File storeDir) {
        this.storeDir = storeDir;
    }

    public SchedulerSettings add(String settingsPropsContent) {
        String settingsName = generateSettingsName();
        File file = getSettingsFile(settingsName);
        LOGGER.info("Adding new scheduler settings file={}", file);
        writeFile(file, settingsPropsContent);

        SchedulerSettings schedulerSettings = new SchedulerSettings(settingsName, file.getPath());
        return schedulerSettings;
    }

    private void writeFile(File file, String text) {
        try {
            FileUtils.write(file, text);
        } catch (IOException e) {
            LOGGER.error("Failed to save file={}", file, e);
        }
    }

    public void remove(String settingsName) {
        File file = getSettingsFile(settingsName);
        LOGGER.info("Deleting scheduler settings file={}", file);
        if (!file.delete()) {
            throw new RuntimeException("Failed to delete scheduler settings file=" + file);
        }
    }

    public SchedulerSettings get(String settingsName) {
        return get(getSettingsFile(settingsName));
    }

    private SchedulerSettings get(File settingsFile) {
        try {
            String name = settingsFile.getName();
            String settingsName = name.split(SETTINGS_FILE_EXT)[0];
            LOGGER.info("Loading scheduler settings from file={}", settingsFile);
            SchedulerSettings settings = new SchedulerSettings(settingsName, settingsFile.getPath());
            return settings;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load scheduler settings file={}" + settingsFile);
        }
    }

    public List<SchedulerSettings> getAll() {
        List<SchedulerSettings> result = new ArrayList<SchedulerSettings>();
        File[] files = storeDir.listFiles();
        for (File file : files) {
            result.add(get(file));
        }
        return result;
    }

    public SchedulerSettings update(String settingsName, String settingsPropsContent) {
        File file = getSettingsFile(settingsName);
        writeFile(file, settingsPropsContent);

        SchedulerSettings settings = new SchedulerSettings(settingsName, file.getPath());
        return settings;
    }

    public boolean exists(String settingsName) {
        return getSettingsFile(settingsName).exists();
    }

    private File getSettingsFile(String settingsName) {
        return new File(storeDir, settingsName + SETTINGS_FILE_EXT);
    }
}
