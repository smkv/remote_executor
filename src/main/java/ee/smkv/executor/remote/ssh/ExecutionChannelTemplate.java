package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.ChannelExec;

public interface ExecutionChannelTemplate {
    void withExecutionChannel(ChannelExec channel) throws Exception;
}
