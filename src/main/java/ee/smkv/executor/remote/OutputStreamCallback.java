package ee.smkv.executor.remote;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamCallback implements Callback {

    private final OutputStream outputStream;

    public OutputStreamCallback(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

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
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendLineToBuffer(String line) {
        try {
            outputStream.write(line.getBytes());
            outputStream.write('\n');
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
