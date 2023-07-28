package cz.cvut.fel.ida.isbra2022;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Utility class for manipulating input and output.
 *
 * @author Petr Ryšavý
 */
public class IOUtils {

    public static final OpenOption[] FILE_WRITE_OPTIONS = new OpenOption[]{
        StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
    };

    /** Do not let anybody to instantiate the class. */
    private IOUtils() {
    }

    /**
     * Writes set of strings to a file. Assumes that the file should be
     * truncated if existing and created if it does not exist.
     * @param file Target file.
     * @param contents The lines that will be stored in the file.
     * @throws IOException When IO fails.
     */
    public static void write(Path file, List<String> contents) throws IOException {
        Files.write(
                file,
                contents,
                FILE_WRITE_OPTIONS
        );
    }

    /**
     * Writes set of strings to a file.Assumes that the file should be truncated
     * if existing and created if it does not exist.
     * @param <T> Type of the objects.
     * @param file Target file.
     * @param stream Stream of objects that should be printed to the file. One
     * at a line.
     * @throws IOException When IO fails.
     */
    public static <T> void write(Path file, Stream<T> stream) throws IOException {
        Files.write(
                file,
                (Iterable<String>) stream.map(Object::toString)::iterator,
                FILE_WRITE_OPTIONS
        );
    }

    /**
     * Writes set of strings to a file. Assumes that the file should be
     * truncated if existing and created if it does not exist.
     * @param file Target file.
     * @param stream The list of numbers to store to a file.
     * @throws IOException When IO fails.
     */
    public static void write(Path file, IntStream stream) throws IOException {
        write(file, stream.mapToObj(i -> Integer.toString(i)));
    }

    /**
     * Writes an array to a file, one element per line. Assumes that the file
     * should be truncated if existing and created if it does not exist.
     * @param <T> Type of the objects.
     * @param file Target file.
     * @param values Array of values to write.
     * @throws IOException When IO fails.
     */
    public static <T> void write(Path file, T[] values) throws IOException {
        write(file, Arrays.stream(values));
    }
}
