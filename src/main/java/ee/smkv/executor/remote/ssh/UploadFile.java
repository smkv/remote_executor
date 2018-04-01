package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.ChannelSftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class UploadFile implements SftpChannelTemplate {
    private final String path;
    private final byte[] content;

    public UploadFile(String path, byte[] content) {
        this.path = path;
        this.content = content;
    }

    @Override
    public void withSftpChannel(ChannelSftp channel) throws Exception {
        channel.put( new ByteArrayInputStream(content), path);
    }

    public String getPath() {
        return path;
    }

    public byte[] getContent() {
        return content;
    }
}
