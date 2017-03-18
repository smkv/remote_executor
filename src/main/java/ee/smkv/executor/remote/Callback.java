package ee.smkv.executor.remote;

public interface Callback {
    void connected(String server);

    void started(String command);

    void output(String line);

    void done(int exitStatus);

    void disconnected();
}
