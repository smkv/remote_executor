package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.TestChannelSftp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UploadFileTest {

    TestChannelSftp channel;

    @Before
    public void setUp() throws Exception {
        channel = new TestChannelSftp();
    }

    @Test
    public void withSftpChannel() throws Exception {
        byte[] bytes = new byte[]{1, 2, 3};
        UploadFile uploadFile = new UploadFile("path", bytes);
        uploadFile.withSftpChannel(channel);
        Assert.assertArrayEquals(bytes, channel.getBytes());
    }
}