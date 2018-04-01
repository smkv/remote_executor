package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.*;
import ee.smkv.executor.remote.Command;

import java.text.ParseException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SshServer {
    private final String host;
    private final int port;
    private final String user;
    private String privateKey;
    private static final Pattern SERVER_PARSE_PATTERN = Pattern.compile("([^@]+)@([^@:]+):([0-9]+)");

    public SshServer(String user, String host, int port) {
        this.host = host;
        this.port = port;
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public String toString() {
        return String.format("%s@%s:%d", user, host, port);
    }

    public static SshServer fromString(String server) throws ParseException {
        Matcher matcher = SERVER_PARSE_PATTERN.matcher(server);
        if (matcher.matches()) {
            return new SshServer(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)));
        }
        throw new ParseException("SSH server string has wrond format", 0);
    }

    public static SshServer fromStringWithPrivateKey(String server, String privateKey) throws ParseException {
        SshServer sshServer = fromString(server);
        sshServer.setPrivateKey(privateKey);
        return sshServer;
    }


    public void doInSession(SshSessionTemplate template) throws Exception {
        Session session = null;
        try {
            session = createSshSessionAndConnect();
            template.withSession(session);
        } finally {
            if (session != null) {
                session.disconnect();
            }
        }

    }

    private Session createSshSessionAndConnect() throws JSchException {
        JSch jsch = new JSch();
        jsch.addIdentity(privateKey);
        Session session = jsch.getSession(user, host, port);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        return session;
    }

    public int doInExecutionChannel(Command command, ExecutionChannelTemplate template) throws Exception {
        final AtomicInteger exitCode = new AtomicInteger(-1);
        doInSession(session -> {
            ChannelExec channel = null;
            try {
                channel = createExecutionChannel(command, session);
                template.withExecutionChannel(channel);
            } finally {
                if (channel != null) {
                    channel.disconnect();
                    exitCode.set(channel.getExitStatus());
                }
            }

        });
        return exitCode.get();
    }

    public void doInSftpChannel(SftpChannelTemplate template) throws Exception {
        doInSession(session -> {
            ChannelSftp channel = null;
            try {
                channel = createSftpChannel(session);
                template.withSftpChannel(channel);
            } finally {
                if (channel != null) {
                    channel.disconnect();
                }
            }
        });
    }


    private ChannelExec createExecutionChannel(Command command, Session session) throws JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command.getCommand());
        channel.connect();
        return channel;
    }

    private ChannelSftp createSftpChannel(Session session) throws JSchException {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        return channel;
    }
}
