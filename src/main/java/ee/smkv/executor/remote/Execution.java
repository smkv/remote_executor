package ee.smkv.executor.remote;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public interface Execution {

    boolean isFinished();

    Command getCommand();

    String getOutput();

    int getExitCode();

    void waitUtilFinish() throws ExecutionException, InterruptedException;

    void waitUtilFinish(long timeout , TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException;

}
