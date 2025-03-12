package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.Person;
import lab5.itmo.collection.models.Color;
import lab5.itmo.exceptions.ExecutionError;

import java.io.IOException;
import java.util.Collection;

/**
 * A command that prints elements whose hair color is greater than the specified value.
 */
public class FilterGreaterThanHairColor extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    /**
     * Constructs a {@code FilterGreaterThanHairColor} command.
     *
     * @param console           The console used for input and output.
     * @param collectionManager The collection manager whose collection will be processed.
     */
    public FilterGreaterThanHairColor(Console console, CollectionManager collectionManager) {
        super("filter_greater_than_hair_color",
                "print elements whose value of the hair color field is greater than the specified value");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command by filtering and printing elements with hair color greater than the specified value.
     *
     * @param args The arguments passed to the command (should contain one argument: hair color).
     * @return {@code true} if the command executed successfully.
     * @throws ExecutionError If the argument is missing or invalid.
     */
    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (args.length < 1) {
            throw new ExecutionError("This command requires one argument: hair color.");
        }

        String hairColorStr = args[0].trim().toUpperCase();

        Color specifiedHairColor;
        try {
            specifiedHairColor = Color.valueOf(hairColorStr);
        } catch (IllegalArgumentException e) {
            throw new ExecutionError("Invalid hair color. Available values: BLUE, ORANGE, WHITE, BROWN.");
        }

        Collection<Person> people = collectionManager.getCollection().values();

        try {
            console.println("Elements with hair color greater than " + specifiedHairColor + ":");
            for (Person person : people) {
                Color personHairColor = person.getHairColor();
                if (personHairColor != null && personHairColor.compareTo(specifiedHairColor) > 0) {
                    console.println(person.toString());
                }
            }
        } catch (IOException e) {
            throw new ExecutionError("Failed to write to console: " + e.getMessage());
        }

        return true;
    }
}