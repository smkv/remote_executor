package ee.smkv.executor.remote.web;

import com.jcraft.jsch.JSchException;
import ee.smkv.executor.remote.Config;
import ee.smkv.executor.remote.Executor;
import ee.smkv.executor.remote.Log4jCallbackImpl;
import ee.smkv.executor.remote.SshServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping("/executor")
public class SshRemoteExecutor {

    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public String execute(String command) throws IOException, JSchException, ParseException {

        SshServer sshServer = SshServer.fromString(Config.getProperty("remote.server"));
        String privateKey = Config.getProperty("keys.private");
        Executor executor = new Executor(sshServer, privateKey);
        executor.execute(command, new Log4jCallbackImpl());
        return "redirect:/applications";
    }
}
