package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.ChannelSftp;

public interface SftpChannelTemplate {
    void withSftpChannel(ChannelSftp channel) throws Exception;
}
