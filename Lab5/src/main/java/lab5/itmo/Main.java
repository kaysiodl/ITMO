package lab5.itmo;

import lab5.itmo.client.CommandManager;
import lab5.itmo.client.commands.*;
import lab5.itmo.client.io.Controller;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.collection.managers.AskManager;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.managers.DumpManager;
import lab5.itmo.collection.models.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        try {
            StandartConsole console = new StandartConsole();

            CommandManager commandManager = new CommandManager();
            AskManager askManager = new AskManager();
            CollectionManager collectionManager = new CollectionManager();

            try {
                collectionManager.loadCollection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }

            Controller controller = new Controller(commandManager, console);

            commandManager.register(new Help(console, commandManager));
            commandManager.register(new Show(console, collectionManager));
            commandManager.register(new Clear(console, collectionManager));
            commandManager.register(new Save(console, collectionManager));
            commandManager.register(new Exit());
            commandManager.register(new History(console, commandManager));
            commandManager.register(new UpdateId(console, collectionManager));
            commandManager.register(new Insert(console, collectionManager, askManager));
            commandManager.register(new RemoveKey(console, collectionManager));
            commandManager.register(new Info(console, collectionManager));
            commandManager.register(new RemoveGreater(console, collectionManager));
            commandManager.register(new ReplaceIfGreater(console, collectionManager));
            commandManager.register(new ExecuteScript(console, controller));
            commandManager.register(new AddRandom(console, collectionManager));
            commandManager.register(new CountLessThanNationality(console, collectionManager));
            commandManager.register(new FilterGreaterThanHairColor(console, collectionManager));

            controller.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}