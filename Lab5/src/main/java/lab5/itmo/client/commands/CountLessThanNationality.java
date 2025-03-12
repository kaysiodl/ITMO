package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.Person;
import lab5.itmo.collection.models.Country;
import lab5.itmo.exceptions.ExecutionError;

import java.io.IOException;
import java.util.Collection;

/**
 * A command that counts the number of elements in the collection
 * whose nationality field value is less than the specified value.
 */
public class CountLessThanNationality extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    /**
     * Constructs a {@code CountLessThanNationality} command.
     *
     * @param console           The console used for input and output.
     * @param collectionManager The collection manager whose collection will be processed.
     */
    public CountLessThanNationality(Console console, CollectionManager collectionManager) {
        super("count_less_than_nationality",
                "print the number of elements whose value of the nationality field is less than the specified value");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command by counting the number of elements
     * whose nationality is less than the specified value.
     *
     * @param args The arguments passed to the command (should contain one argument: nationality).
     * @return {@code true} if the command executed successfully.
     * @throws ExecutionError If the argument is missing or invalid.
     */
    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (args.length < 1) {
            throw new ExecutionError("This command requires one argument: nationality.");
        }

        String nationalityStr = args[0].trim().toUpperCase();

        Country specifiedNationality;
        try {
            specifiedNationality = Country.valueOf(nationalityStr);
        } catch (IllegalArgumentException e) {
            throw new ExecutionError("Invalid nationality. Available values: FRANCE, SPAIN, THAILAND.");
        }

        Collection<Person> people = collectionManager.getCollection().values();

        int count = 0;
        for (Person person : people) {
            Country personNationality = person.getNationality();
            if (personNationality != null && personNationality.compareTo(specifiedNationality) < 0) {
                count++;
            }
        }

        String result = String.format("Number of elements with nationality less than %s: %d", specifiedNationality, count);

        try {
            console.println(result);
        } catch (IOException e) {
            throw new ExecutionError("Failed to write to console: " + e.getMessage());
        }

        return true;
    }
}