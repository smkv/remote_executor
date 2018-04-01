package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.ChannelSftp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadFile implements SftpChannelTemplate {
    private final String path;
    private byte[] content;

    public DownloadFile(String path) {
        this.path = path;
    }

    @Override
    public void withSftpChannel(ChannelSftp channel) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = channel.get(path)){
            int read;
            while ((read = inputStream.read()) >= 0) {
                outputStream.write(read);
            }
        }
        content = outputStream.toByteArray();
    }

    public String getPath() {
        return path;
    }

    public byte[] getContent() {
        return content;
    }
}
