package eric.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eric.task.Task;

/**
 * Saves the task list to a text file on the hard disk, one task per line, and loads it back.
 * The file path is relative, so the file is created next to where Eric is run.
 */
public class Storage {
    private final Path filePath;

    /**
     * Tasks read from the save file, together with a message for each line that could not be read.
     *
     * @param tasks Tasks read successfully, in file order.
     * @param warnings One message per skipped line, e.g. "line 2: unknown task type "X"".
     */
    public record LoadResult(List<Task> tasks, List<String> warnings) {
    }

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
     * Reads the tasks from the save file. A missing file is not an error: it simply means nothing
     * has been saved yet, so the result has no tasks. Blank lines are ignored, and lines that are
     * not in the save file format are skipped and reported in the warnings.
     *
     * @return The tasks that were read, and a warning for each skipped line.
     * @throws IOException If the file exists but cannot be read.
     */
    public LoadResult load() throws IOException {
        List<Task> tasks = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return new LoadResult(tasks, warnings);
        }

        List<String> lines = Files.readAllLines(filePath);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(Task.fromFileString(line));
            } catch (IllegalArgumentException exception) {
                warnings.add("line " + (i + 1) + ": " + exception.getMessage());
            }
        }
        return new LoadResult(tasks, warnings);
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
