package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class JSONArrayWriter<T extends ConvertableToJSON> {
    private final Writer writer;
    private boolean isFirst = true;

    public JSONArrayWriter(String fileName) throws IOException {
        writer = new BufferedWriter(new FileWriter(fileName, false));
        writer.write("[");
    }

    public void write(T object) throws IOException {
        write(object, false);
    }

    public void write(T object, boolean verbose) throws IOException {
        if (!isFirst) writer.write(",");
        writer.write(object.toJSON());
        isFirst = false;
    }

    public void close() throws IOException {
        writer.write("]");
        writer.flush();
        writer.close();
    }
}
