package unit.myschedule.quartz.extra.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * A easy file wrapper that used for writing test result.
 * 
 * @author Zemian Deng <saltnlight5@gmail.com>
 *
 */
public class ResultFile {
	
	protected File file;
	
	/** Create a new file in a temporary directory. */
	public ResultFile(String filename) {
		this.file = new File(System.getProperty("java.io.tmpdir") + "/" + filename);
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
	
	public void writeLine(String text) {
		write(text + "\n");
	}
	
	public void write(String text) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file, true);
			IOUtils.write(text, writer);
			writer.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(writer);
		}
	}
}
