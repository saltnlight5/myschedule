package myschedule.quartz.extra.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * A easy file wrapper that used for writing test result.
 *
 * @author Zemian Deng <saltnlight5@gmail.com>
 */
public class ResultFile {

    protected File file;

    /**
     * Create a new file in a temporary directory.
     */
    public ResultFile(String filename) {
        this.file = new File(filename);
    }

    public ResultFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void resetFile() {
        // Reset file content
        try {
            FileUtils.writeStringToFile(file, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        if (!file.delete()) {
            throw new RuntimeException("Failed to delete file: " + file);
        }
    }

    public List<String> readLines() {
        try {
            return FileUtils.readLines(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String read() {
        try {
            return FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void appendLine(String text) {
        append(text + "\n");
    }

    public void append(String text) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(file, true);
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Override
    public String toString() {
        return "ResultFile[" + file == null ? "null" : file.toString() + "]";
    }
}
