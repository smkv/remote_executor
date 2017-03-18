package ee.smkv.executor.remote.web;

import com.jcraft.jsch.JSchException;
import ee.smkv.executor.remote.Config;
import ee.smkv.executor.remote.Executor;
import ee.smkv.executor.remote.OutputStreamCallback;
import ee.smkv.executor.remote.SshServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

@Controller
@RequestMapping("/executor")
public class SshRemoteExecutor {
    @RequestMapping()
    public String index(){
        return "executor";
    }

    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public void execute(String command, HttpServletResponse response) throws IOException, JSchException, ParseException {
        SshServer sshServer = SshServer.fromString(Config.getProperty("remote.server"));
        String privateKey = Config.getProperty("keys.private");
        Executor executor = new Executor(sshServer, privateKey);
        OutputStreamCallback callback = new OutputStreamCallback(response.getOutputStream());
        executor.execute(command, callback);

    }
}
