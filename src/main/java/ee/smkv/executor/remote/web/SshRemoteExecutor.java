package ee.smkv.executor.remote.web;

import com.jcraft.jsch.JSchException;
import ee.smkv.executor.remote.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping("/executor")
public class SshRemoteExecutor {

    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    @ResponseBody
    public String execute(String command) throws IOException, JSchException, ParseException {
        SshServer sshServer = SshServer.fromString(Config.getProperty("remote.server"));
        String privateKey = Config.getProperty("keys.private");
        Executor executor = new Executor(sshServer, privateKey);
        StringBufferCallback stringBufferCallback = new StringBufferCallback();
        executor.execute(command, stringBufferCallback);
        return stringBufferCallback.toString();
    }
}
