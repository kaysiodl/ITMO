package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.*;
import lab5.itmo.exceptions.ExecutionError;

import java.time.LocalTime;
import java.util.Random;
import static java.lang.Math.round;

public class AddRandom extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public AddRandom(Console console, CollectionManager collectionManager) {
        super("add_random", "adds a random element to collection.");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    private int getRandom(){
        Random random = new Random(LocalTime.now().getNano());
        return (int) round(random.nextDouble(3, 100000));
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        try {
            Person person = new Person(
                    "person_" + String.valueOf(getRandom()),
                    new Coordinates((int) getRandom(), (int) getRandom()),
                    (float) getRandom(),
                    Color.BLUE,
                    Color.WHITE,
                    Country.FRANCE,
                    new Location((float) getRandom(), (long) getRandom(), (long) getRandom(), "location_"+ String.valueOf(getRandom())));

            collectionManager.add(person);
            console.print("Random person has been added.\n");
        } catch (ExecutionError e) {
            throw new ExecutionError(e.getMessage());
        }
        return true;
    }
}
