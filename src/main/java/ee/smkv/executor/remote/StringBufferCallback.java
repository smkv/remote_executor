package ee.smkv.executor.remote;

public class StringBufferCallback implements Callback {

    StringBuffer buffer = new StringBuffer();

    @Override
    public void connected(SshServer server) {
        appendLineToBuffer("connected to: " + server);
    }

    @Override
    public void started(String command) {
        appendLineToBuffer("Command: " + command);
    }

    @Override
    public void output(String line) {
        appendLineToBuffer(line);
    }

    @Override
    public void done(int exitStatus) {
        appendLineToBuffer("Exit status: " + exitStatus);
    }

    @Override
    public void disconnected() {
        appendLineToBuffer("disconnected");
    }

    private void appendLineToBuffer(String line) {
        buffer.append(line).append('\n');
    }

    @Override
    public String toString() {
        return buffer.toString();
    }
}
