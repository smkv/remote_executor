package ee.smkv.executor.remote;

public class Command {
    public final String command;

    public Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "Command{" +
                "command='" + command + '\'' +
                '}';
    }
}
