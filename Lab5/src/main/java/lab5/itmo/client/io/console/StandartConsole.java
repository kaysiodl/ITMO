package lab5.itmo.client.io.console;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class StandartConsole implements Console{
    private boolean scriptExecutionMode = false;
    private static Scanner scanner = new Scanner(System.in);
    private List<String> script = new ArrayList<>();

    public void setScriptExecutionMode(boolean scriptExecutionMode) {
        this.scriptExecutionMode = scriptExecutionMode;
    }

    public void setScript(List<String> script){
        this.script.addAll(script);
    }

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
        if (!scriptExecutionMode) {
            return scanner.nextLine();
        } else{
            if (script.size() == 1) setScriptExecutionMode(false);
            String line = script.getFirst();
            script.removeFirst();
            println(line);
            return line;
        }
    }

    @Override
    public void printError(String error){System.out.println(error);}


}
