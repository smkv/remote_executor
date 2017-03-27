package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.Session;

public interface SshSessionTemplate {
    void withSession(Session session) throws Exception;
}
