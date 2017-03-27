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
    private Future<Integer> future;


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
        try {
            return future.get(0, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void waitUtilFinish() throws ExecutionException, InterruptedException {
        future.get();
    }

    @Override
    public void waitUtilFinish(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        future.get(timeout, unit);
    }


    void appendOutputLine(String line) {
        outputStringBuilder.append(line).append('\n');
    }

    void setFuture(Future<Integer> future) {
        this.future = future;
    }


    @Override
    public void cancel() {
        if (!isFinished()) {
            future.cancel(true);
        }
    }
}
