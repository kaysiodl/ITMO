package lab5.itmo.client.io.console;

import java.io.IOException;
import java.util.Scanner;

public interface Console {
    void println(Object obj) throws IOException;

    void print(Object object);

    String read();

    void selectFileScanner(Scanner obj);

    void selectConsoleScanner();
}
