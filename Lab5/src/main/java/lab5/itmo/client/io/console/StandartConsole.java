package lab5.itmo.client.io.console;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StandartConsole implements Console{
    private static Scanner fileScanner = null;

    private static Scanner scanner = new Scanner(System.in);

    @Override
    public void println(Object obj){
        System.out.println(obj);
    }

    @Override
    public void print(Object object){
        System.out.print(object);
    }

    @Override
    public String read() throws NoSuchElementException, IllegalStateException {
        return (fileScanner != null ? fileScanner : scanner).nextLine();

    }

    @Override
    public void selectFileScanner(Scanner scanner) {
        this.fileScanner = scanner;
    }

    @Override
    public void selectConsoleScanner() {
        this.fileScanner = null;
    }

}
