package eric.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import eric.task.Task;

/**
 * Saves the task list to a text file on the hard disk, one task per line.
 * The file path is relative, so the file is created next to where Eric is run.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage that saves to {@code filePath}.
     *
     * @param filePath Location of the save file, preferably built with {@link Path#of} so that it
     *         works on any operating system.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /** Returns the location of the save file. */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Writes the first {@code taskCount} entries of {@code tasks} to the save file, replacing its
     * previous contents. Creates the file, and the folder that contains it, if they do not exist yet.
     *
     * @param tasks Array holding the tasks, in list order.
     * @param taskCount Number of entries of {@code tasks} that are in use.
     * @throws IOException If the file cannot be written.
     */
    public void save(Task[] tasks, int taskCount) throws IOException {
        Path folder = filePath.getParent();
        if (folder != null) {
            Files.createDirectories(folder);
        }
        List<String> lines = Arrays.stream(tasks, 0, taskCount).map(Task::toFileString).toList();
        Files.write(filePath, lines);
    }
}
