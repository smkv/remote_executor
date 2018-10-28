package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.TestChannelSftp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class DirectoryListTest {

    TestChannelSftp channel;
    DirectoryList directoryList;

    @Before
    public void setUp() throws Exception {
        directoryList = new DirectoryList("path");
        channel = new TestChannelSftp();
    }

    @Test
    public void getListEmptyDirectory() throws Exception {
        execute(directoryList);
        Assert.assertEquals(Collections.emptyList(), directoryList.getList());
    }

    @Test
    public void getListOneFile() throws Exception {
        channel.setFiles(Arrays.asList("file.txt"));
        execute(directoryList);
        Assert.assertEquals(1, directoryList.getList().size());
        Assert.assertEquals("file.txt", directoryList.getList().get(0).getFilename());
    }

    @Test
    public void getListTwoFile() throws Exception {
        channel.setFiles(Arrays.asList("file.txt", "file2.txt"));
        execute(directoryList);
        Assert.assertEquals(2, directoryList.getList().size());
        Assert.assertEquals("file.txt", directoryList.getList().get(0).getFilename());
        Assert.assertEquals("file2.txt", directoryList.getList().get(1).getFilename());
    }

    @Test
    public void getListOneNormalFileAndOneHidden() throws Exception {
        channel.setFiles(Arrays.asList(".hidden", "file.txt"));
        execute(directoryList);
        Assert.assertEquals(1, directoryList.getList().size());
        Assert.assertEquals("file.txt", directoryList.getList().get(0).getFilename());
    }


    private void execute(SftpChannelTemplate template) throws Exception {
        template.withSftpChannel(channel);
    }
}