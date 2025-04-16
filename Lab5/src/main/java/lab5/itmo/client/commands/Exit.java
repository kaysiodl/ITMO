package lab5.itmo.client.commands;

import lab5.itmo.client.io.console.Console;
import lab5.itmo.collection.managers.BackUpManager;
import lab5.itmo.collection.managers.CollectionManager;
import lab5.itmo.exceptions.ExecutionError;

import java.nio.file.Path;

import static java.lang.System.exit;

/**
 * A command that exits the program without saving.
 */
public class Exit extends Command {
    private final Console console;
    private final BackUpManager backUpManager;
    private final CollectionManager collectionManager;

    /**
     * Constructs an {@code Exit} command.
     */
    public Exit(Console console, BackUpManager backUpManager, CollectionManager collectionManager) {
        super("exit", "exit the program (without saving)");
        this.console = console;
        this.backUpManager = backUpManager;
        this.collectionManager = collectionManager;
    }

    /**
     * Executes the command by terminating the program.
     *
     * @param args The arguments passed to the command (not used).
     * @return {@code true} if the command executed successfully.
     * @throws ExecutionError If an error occurs during execution.
     */
    @Override
    public boolean apply(String[] args) throws ExecutionError {
        if (backUpManager.checkBackUpFile()) {
            console.print("Are you sure you want to exit? You have an unsaved collection.\nType yes if you want to save collection and exit ");
            String line = console.read().trim().toLowerCase();
            if (line.equals("yes")) {
                collectionManager.loadCollection(Path.of(backUpManager.fileName()));
            }
        }
        exit(0);
        return true;
    }
}