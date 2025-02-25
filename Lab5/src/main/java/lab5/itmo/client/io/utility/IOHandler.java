package lab5.itmo.client.io.utility;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IOHandler<T> extends AutoCloseable{
    T read() throws FileNotFoundException;

    void write(String data) throws IOException;
}
