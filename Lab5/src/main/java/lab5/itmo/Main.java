package lab5.itmo;

import lab5.itmo.client.CommandManager;
import lab5.itmo.client.commands.*;
import lab5.itmo.client.io.Controller;
import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.*;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            Coordinates coordinates = new Coordinates(34, 432);
            Location location = new Location(32.5f, 234545L, 341L, "fdvsc");
            Person person = new Person("Robert", coordinates, 165, Color.GREEN, Color.WHITE, Country.FRANCE, location);
            Person person1 = new Person("Artem", coordinates, 180, Color.BLUE, Color.BROWN, Country.SPAIN, location);
            //DumpManager dumpManager = new DumpManager(Path.of("test2.json"));
            CommandManager commandManager = new CommandManager();
            CollectionManager collectionManager = new CollectionManager();
            collectionManager.add(person);
            collectionManager.add(person1);
            collectionManager.removeById(2);
            collectionManager.saveCollection(Path.of("test2.json"));
            StandartConsole console = new StandartConsole();
            try {
                collectionManager.loadCollection(Path.of("test.json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }

            commandManager.register(new Help(console, commandManager));
            commandManager.register(new Show(console, collectionManager));
            commandManager.register(new Clear(console, collectionManager));
            commandManager.register(new Save(console, collectionManager));
            commandManager.register(new Exit());
            commandManager.register(new History(console, commandManager));

            Controller controller = new Controller(commandManager, console);
            controller.run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}