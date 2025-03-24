package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.AskManager;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.Person;
import lab5.itmo.exceptions.ExecutionError;

public class ReplaceIfGreater extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public ReplaceIfGreater(Console console, CollectionManager collectionManager) {
        super("replace_if_greater", "replace the key value if the new value is greater than the old one.");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
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
