/**
 * A command that replaces an element in the collection if the new element's coordinate sum is greater than the old one.
 * The command requires one argument - the ID of the element to potentially replace.
 */
package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.AskManager;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.Person;
import lab5.itmo.exceptions.ExecutionError;

/**
 * Command that replaces an element in the collection with a new one if the new element's coordinate sum
 * is greater than the old element's coordinate sum. The element is identified by its ID.
 */
public class ReplaceIfGreater extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    /**
     * Constructs a ReplaceIfGreater command.
     *
     * @param console the console interface for user interaction
     * @param collectionManager the collection manager that maintains the collection
     */
    public ReplaceIfGreater(Console console, CollectionManager collectionManager) {
        super("replace_if_greater", "replace the key value if the new value is greater than the old one.");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    /**
     * Executes the command by replacing the element with the specified ID if the new element's
     * coordinate sum is greater than the old one.
     *
     * @param args the arguments passed to the command (should contain exactly one argument: the element ID)
     * @return {@code true} if the command executed successfully
     * @throws ExecutionError if the argument is missing or invalid
     * @throws NullPointerException if no element with the specified ID exists in the collection
     */
    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (args.length != 1) {
            throw new ExecutionError("This command accepts one argument.");
        }
        try {
            if (args[0].isEmpty())
                console.printError("Incorrect number of arguments");
            int id = -1;
            try {
                id = Integer.parseInt(args[0].trim());
            } catch (NumberFormatException e) {
                console.printError("Id has not correct format.");
            }
            Person old = collectionManager.getById(id);
            if (old == null){
                throw new NullPointerException();
            }
            try {
                console.print("Create new person: \n");
                Person person = AskManager.askPerson(console);
                if (person != null) {
                    person.validate();
                    if (person.getSumCoordinates() > old.getSumCoordinates()) {
                        collectionManager.removeById(old.getId());
                        collectionManager.add(person, old.getId());
                        collectionManager.sort();
                    }
                }
            } catch (ExecutionError e) {
                console.printError("the fields of the person are not valid!");
            }
        } catch (AskManager.Break e) {
            console.printError("the fields of the person are not valid!");
        } catch(NullPointerException e){
            throw new NullPointerException("Nothing to update.");
        }
        return true;
    }
}