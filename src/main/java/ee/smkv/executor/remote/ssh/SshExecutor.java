package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import ee.smkv.executor.remote.*;
import org.springframework.beans.factory.DisposableBean;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SshExecutor implements Executor , DisposableBean {
    private final SshServer sshServer;
    private final String privateKey;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public SshExecutor(SshServer sshServer, String privateKey) {
        this.sshServer = sshServer;
        this.privateKey = privateKey;
    }

    @Override
    public Execution execute(Command command) {
        SshExecution execution = new SshExecution(command);
        Future<Integer> future = executorService.submit(() -> execute(execution));
        execution.setFuture(future);
        return execution;
    }

    private Integer execute(SshExecution execution) {
        Integer exitStatus = -1;
        Session session = null;
        try {
            session = createSshSession();

            ChannelExec channel = null;
            try {
                channel = createExecutionChannel(execution.getCommand(), session);
                readCommandOutput(getPipedInputStream(channel), execution);

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                if (channel != null) {
                    channel.disconnect();
                    exitStatus = channel.getExitStatus();
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }
        return exitStatus;
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

    private ChannelExec createExecutionChannel(Command command, Session session) throws JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command.getCommand());

        channel.connect();
        return channel;
    }


    private Session createSshSession() throws JSchException {
        JSch jsch = new JSch();
        jsch.addIdentity(privateKey);
        Session session = jsch.getSession(sshServer.getUser(), sshServer.getHost(), sshServer.getPort());
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(privateKey);
        session.connect();
        return session;
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
