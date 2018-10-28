package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.TestChannelSftp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DownloadFileTest {

    DownloadFile downloadFile;
    TestChannelSftp channel;

    @Before
    public void setUp() throws Exception {
        downloadFile = new DownloadFile("path");
        channel = new TestChannelSftp();
    }

    @Test
    public void getContent() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3};
        channel.setBytes(bytes);
        downloadFile.withSftpChannel(channel);
        Assert.assertArrayEquals(bytes, downloadFile.getContent());
    }
}