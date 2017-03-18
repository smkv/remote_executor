package ee.smkv.executor.remote;

import org.apache.log4j.Logger;

public class Log4jCallbackImpl implements Callback {

    private final static Logger log = Logger.getLogger(Log4jCallbackImpl.class);
    private SshServer server;
    private int lineNumber;

    @Override
    public void connected(SshServer server) {
        log.info(String.format("Connected to %s", server));
        this.server = server;
    }

    @Override
    public void started(String command) {
        log.info(String.format("%s [   ] << %s", server, command));
    }

    @Override
    public void output(String line) {
        log.info(String.format("%s [%-3d] >> %s", server, ++lineNumber, line));
    }

    @Override
    public void done(int exitStatus) {
        log.info(String.format("%s [%-3d] >> Exit(%d)", server, ++lineNumber, exitStatus));
    }

    @Override
    public void disconnected() {
        log.info("Disconnected from " + server);
    }
}
