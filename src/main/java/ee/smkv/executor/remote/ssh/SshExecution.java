package ee.smkv.executor.remote.ssh;

import ee.smkv.executor.remote.Command;
import ee.smkv.executor.remote.Execution;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SshExecution implements Execution {

    private final Command command;
    private final StringBuilder outputStringBuilder = new StringBuilder();
    private int exitCode = -1;
    private Future future;


    SshExecution(Command command) {
        this.command = command;
    }

    @Override
    public boolean isFinished() {
        return future == null || future.isDone() || future.isCancelled();
    }


    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public String getOutput() {
        return outputStringBuilder.toString();
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }

    @Override
    public void waitUtilFinish() throws ExecutionException, InterruptedException {
        future.get();
    }

    @Override
    public void waitUtilFinish(long timeout , TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        future.get(timeout, unit);
    }

    void done(int exitCode) {
        this.exitCode = exitCode;
    }

    void appendOutputLine(String line) {
        outputStringBuilder.append(line).append('\n');
    }

    void setFuture(Future future) {
        this.future = future;
    }


}
