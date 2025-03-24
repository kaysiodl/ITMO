package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.collection.managers.AskManager;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.models.Person;
import lab5.itmo.exceptions.ExecutionError;

import java.util.Arrays;

public class UpdateId extends Command {
    private final StandartConsole console;
    private final CollectionManager collectionManager;

    public UpdateId(StandartConsole console, CollectionManager collectionManager) {
        super("update", "update the value of a collection item" +
                " whose id is equal to the specified one");
        this.collectionManager = collectionManager;
        this.console = console;
    }

    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (args.length == 0) {
            console.printError("Incorrect number of arguments: id is required.");
            return false;
        }
        int id = -1;
        try {
            id = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            console.printError("Id has not correct format.");
        }

        Person old = collectionManager.getById(id);
        if (old == null || !collectionManager.getCollection().containsValue(old)) {
            throw new NullPointerException("Nothing to update.");
        }
        try {
            if (console.isScriptExecutionMode()) {
                String[] data = Arrays.copyOfRange(args, 1, args.length);
                Person person = AskManager.personFromScript(data);
                person.validate();
                collectionManager.removeById(old.getId());
                collectionManager.add(person, old.getId());
                collectionManager.sort();
            } else {
                console.print("Create new person: ");
                Person person = AskManager.askPerson(console);
                if (person != null) {
                    person.validate();
                    collectionManager.removeById(old.getId());
                    collectionManager.add(person, old.getId());
                    collectionManager.sort();
                }
            }
        } catch (AskManager.Break e) {
            console.printError("The fields of the person are not valid!");
            return false;
        }catch (Exception e){
            console.printError("Error: "  + e.getMessage());
            return false;
        }
        return true;
    }

}
