package lab5.itmo;

import lab5.itmo.client.CommandManager;
import lab5.itmo.client.commands.*;
import lab5.itmo.client.io.Controller;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.collection.managers.AskManager;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.*;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            Coordinates coordinates = new Coordinates(34, 432);
            Location location = new Location(32.5f, 234545L, 341L, "fdvsc");
            Person person = new Person("habibi", coordinates, 165, Color.GREEN, Color.WHITE, Country.FRANCE, location);
            Person person1 = new Person("polyakov", coordinates, 180, Color.BLUE, Color.BROWN, Country.SPAIN, location);
            //DumpManager dumpManager = new DumpManager(Path.of("test2.json"));
            CommandManager commandManager = new CommandManager();
            AskManager askManager = new AskManager();
            CollectionManager collectionManager = new CollectionManager();
            collectionManager.add(person);
            collectionManager.add(person1);
            StandartConsole console = new StandartConsole();
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

            controller.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}