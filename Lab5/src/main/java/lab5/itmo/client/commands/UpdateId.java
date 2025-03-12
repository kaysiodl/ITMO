package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.StandartConsole;
import lab5.itmo.collection.models.Person;
import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.collection.managers.AskManager;
import lab5.itmo.exceptions.NullFieldException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
        if (args[0].isEmpty())
            console.printError("Incorrect number of arguments");
        Integer id = -1;
        try {
            id = Integer.parseInt(args[0].trim());
        } catch (NumberFormatException e) {
            console.printError("Id has not correct format.");
        }

        Person old = collectionManager.getById(id);
        if (old == null || !collectionManager.getCollection().containsValue(old)) {
            console.printError("Id doesn't exist.");
        }
        try {
            if (console.isScriptExecutionMode()) {
                List<String> scriptData = console.getScriptData();
                scriptData.removeFirst();
                if (scriptData.size() < 11) {
                    throw new ExecutionError("Not enough data in script for 'update' command.");
                }

                console.setScript(scriptData);

                Person newPerson = AskManager.askPerson(console, old.getId());
                if (newPerson != null) {
                    newPerson.validate();
                    collectionManager.removeById(old.getId());
                    collectionManager.add(newPerson);
                    collectionManager.sort();
                }
            } else {
                console.print("Create new person: ");
                Person person = AskManager.askPerson(console, old.getId());
                if (person != null) {
                    person.validate();
                    collectionManager.removeById(old.getId());
                    collectionManager.add(person);
                    collectionManager.sort();
                }
            }
        } catch (AskManager.Break e) {
            System.err.println("the fields of the person are not valid!");
        }
        return true;
    }

}
