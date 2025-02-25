package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.Person;

import java.io.IOException;
import java.util.List;

public class Show extends Command{
    private final Console console;
    private CollectionManager collectionManager = new CollectionManager();

    public Show(Console console, CollectionManager collectionManager){
        super("show", "output all the elements of the collection in a string representation");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        List<Person> people = collectionManager.sort();

        if (people.isEmpty()) {
            try {
                console.println("Collection is empty.");
            } catch (IOException e) {
                throw new ExecutionError("Failed to write to console: " + e.getMessage());
            }
            return true;
        }

        try {
            StringBuilder output = new StringBuilder();
            for (Person person : people) {
                output.append(person).append("\n");
            }
            console.println(output.toString());
        } catch (IOException e) {
            throw new ExecutionError("Failed to write to console: " + e.getMessage());
        }

        return true;
    }
}
