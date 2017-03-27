package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.ChannelExec;
import ee.smkv.executor.remote.Command;
import ee.smkv.executor.remote.Execution;
import ee.smkv.executor.remote.Executor;
import org.springframework.beans.factory.DisposableBean;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SshExecutor implements Executor, DisposableBean {
    private final SshServer sshServer;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public SshExecutor(SshServer sshServer) {
        this.sshServer = sshServer;
    }

    @Override
    public Execution execute(Command command) {
        SshExecution execution = new SshExecution(command);
        Future<Integer> future = executorService.submit(() -> execute(execution));
        execution.setFuture(future);
        return execution;
    }

    private Integer execute(SshExecution execution) {
        try {
            return sshServer.doInExecutionChannel(execution.getCommand() , channel -> readCommandOutput(getPipedInputStream(channel), execution));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private PipedInputStream getPipedInputStream(ChannelExec channel) throws IOException {
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        channel.setOutputStream(pos);
        channel.setExtOutputStream(pos);
        channel.setErrStream(pos);
        return pis;
    }

    private void readCommandOutput(PipedInputStream pis, SshExecution callback) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(pis))) {
            String line;
            while ((line = reader.readLine()) != null) {
                callback.appendOutputLine(line);
            }
        }
    }




    @Override
    public String toString() {
        return "Executor{" + sshServer + "}";
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdownNow();
    }
}
