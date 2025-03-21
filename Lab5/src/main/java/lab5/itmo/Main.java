package lab5.itmo;

import lab5.itmo.client.CommandManager;
import lab5.itmo.client.commands.*;
import lab5.itmo.client.io.Controller;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.collection.managers.CollectionManager;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            StandartConsole console = new StandartConsole();
            Path collectionPath = Path.of(System.getProperty("collection", args[0].trim()));
            CommandManager commandManager = new CommandManager();
            CollectionManager collectionManager = new CollectionManager(collectionPath);

            try {
                collectionManager.loadCollection();
            } catch (NullPointerException e) {
                console.printError(e.getMessage());
                console.println("Continuing with empty collection.");
            }

            Controller controller = new Controller(commandManager, console);

            commandManager.register(new Help(console, commandManager));
            commandManager.register(new Show(console, collectionManager));
            commandManager.register(new Clear(console, collectionManager));
            commandManager.register(new Save(console, collectionManager));
            commandManager.register(new Exit());
            commandManager.register(new History(console, commandManager));
            commandManager.register(new UpdateId(console, collectionManager));
            commandManager.register(new Insert(console, collectionManager));
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
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}