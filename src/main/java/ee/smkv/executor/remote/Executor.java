package ee.smkv.executor.remote;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.*;
import java.util.Properties;

public class Executor {

    private final String host;
    private final int port;
    private final String user;
    private final String privateKey;

    public Executor(String host, int port, String user, String privateKey) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.privateKey = privateKey;
    }


    public void execute(String command, Callback callback) throws JSchException, IOException {
        Session session = createSshSession();
        callback.connected(getServerIdentifier());

        ChannelExec channel = createExecutionChannel(command, session);
        callback.started(command);


        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        channel.setOutputStream(pos);
        channel.setExtOutputStream(pos);

        readCommandOutput(pis, callback);

        callback.done(channel.getExitStatus());
        channel.disconnect();
        session.disconnect();
        callback.disconnected();
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
        Session session = jsch.getSession(user, host, port);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword(privateKey);
        session.connect();
        return session;
    }


    private String getServerIdentifier() {
        return String.format("%s@%s:%d", user, host, port);
    }

    @Override
    public String toString() {
        return "Executor{" + getServerIdentifier() + "}";
    }
}
