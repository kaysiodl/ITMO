package lab5.itmo.client.commands;

import lab5.itmo.exceptions.ExecutionError;
import lab5.itmo.exceptions.NullFieldException;

import java.util.Objects;

/**
 * An abstract base class for all commands.
 */
public abstract class Command {
    private final String name;
    private final String description;

    /**
     * Constructs a {@code Command} with the specified name and description.
     *
     * @param name        The name of the command.
     * @param description The description of the command.
     */
    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Returns the name of the command.
     *
     * @return The name of the command.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the command.
     *
     * @return The description of the command.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Executes the command with the provided arguments.
     *
     * @param args The arguments passed to the command.
     * @return {@code true} if the command executed successfully.
     * @throws ExecutionError     If an error occurs during execution.
     * @throws NullFieldException If a required field is null.
     */
    public abstract boolean apply(String[] args) throws ExecutionError, NullFieldException;

    /**
     * Checks if this command is equal to another object.
     *
     * @param o The object to compare with.
     * @return {@code true} if the objects are equal, otherwise {@code false}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Command command = (Command) o;
        return Objects.equals(name, command.name) && Objects.equals(description, command.description);
    }

    /**
     * Returns the hash code of the command.
     *
     * @return The hash code of the command.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }

    /**
     * Returns a string representation of the command.
     *
     * @return A string representation of the command.
     */
    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}