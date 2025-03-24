package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.*;
import lab5.itmo.exceptions.ExecutionError;

import java.time.LocalTime;
import java.util.Random;
import static java.lang.Math.round;

/**
 * A command that adds a randomly generated element to the collection.
 */
public class AddRandom extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    /**
     * Constructs an {@code AddRandom} command.
     *
     * @param console           The console used for input and output.
     * @param collectionManager The collection manager to which the random element will be added.
     */
    public AddRandom(Console console, CollectionManager collectionManager) {
        super("add_random", "adds a random element to collection.");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
     * Generates a random integer.
     *
     * @return A random integer between 3 and 100000.
     */
    private int getRandom() {
        Random random = new Random(LocalTime.now().getNano());
        return (int) round(random.nextDouble(3, 100000));
    }

    /**
     * Executes the command by adding a randomly generated {@link Person} to the collection.
     *
     * @param args The arguments passed to the command (not used in this command).
     * @return {@code true} if the command executed successfully.
     * @throws ExecutionError If an error occurs during execution.
     */
    @Override
    public boolean apply(String[] args) throws ExecutionError {
        try {
            Person person = new Person(
                    "person_" + getRandom(),
                    new Coordinates(getRandom(), getRandom()),
                    (float) getRandom(),
                    Color.BLUE,
                    Color.WHITE,
                    Country.FRANCE,
                    new Location((float) getRandom(), (long) getRandom(), (long) getRandom(), "location_" + getRandom()));
            collectionManager.add(person);
            console.print("Random person has been added.\n");
        } catch (ExecutionError e) {
            throw new ExecutionError(e.getMessage());
        }
        return true;
    }
}