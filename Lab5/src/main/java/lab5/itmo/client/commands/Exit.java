package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;

import static java.lang.System.exit;

/**
 * A command that exits the program without saving.
 */
public class Exit extends Command {
    /**
     * Constructs an {@code Exit} command.
     */
    public Exit() {
        super("exit", "exit the program (without saving)");
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
        exit(0);
        return true;
    }
}