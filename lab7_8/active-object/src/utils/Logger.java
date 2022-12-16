package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Logger {
    private final Writer writer;

    public Logger(String fileName) throws IOException {
        writer = new BufferedWriter(new FileWriter(fileName, false));
    }

    public void log(String string) throws IOException {
        System.out.println(string);
        writer.write(string + "\n");
    }

    public void close() throws IOException {
        writer.flush();
        writer.close();
    }
}
