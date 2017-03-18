package ee.smkv.executor.remote;

import org.junit.Test;

import static org.junit.Assert.*;

public class SshServerTest {
    @Test
    public void fromString() throws Exception {
        SshServer sshServer = SshServer.fromString("user@server:22");
        assertEquals("user", sshServer.getUser());
        assertEquals("server", sshServer.getHost());
        assertEquals(22, sshServer.getPort());
    }

}