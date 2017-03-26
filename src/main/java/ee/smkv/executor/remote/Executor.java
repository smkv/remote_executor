package ee.smkv.executor.remote;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.Properties;

public class Executor {

    private final SshServer sshServer;
    private final String privateKey;

    public Executor(SshServer sshServer, String privateKey) {
        this.sshServer = sshServer;
        this.privateKey = privateKey;
    }


    public void execute(String command, Callback callback) {
        Session session = null;
        try {
            session = createSshSession();
            callback.connected(sshServer);

            ChannelExec channel = null;
            try {
                channel = createExecutionChannel(command, session);
                callback.started(command);

                readCommandOutput(getPipedInputStream(channel), callback);

            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            } finally {
                if (channel != null) {
                    channel.disconnect();
                    callback.done(channel.getExitStatus());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (session != null) {
                session.disconnect();
                callback.disconnected();
            }
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

    private void readCommandOutput(PipedInputStream pis, Callback callback) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(pis))) {
            String line;
            while ((line = reader.readLine()) != null) {
                callback.output(line);
            }
        }
    }

    private ChannelExec createExecutionChannel(String command, Session session) throws JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

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
}
