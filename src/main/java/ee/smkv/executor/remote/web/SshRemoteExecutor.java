package ee.smkv.executor.remote.web;

import com.jcraft.jsch.JSchException;
import ee.smkv.executor.remote.Executor;
import ee.smkv.executor.remote.Log4jCallbackImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
@RequestMapping("/executor")
public class SshRemoteExecutor {

    @RequestMapping(value = "/execute", method = RequestMethod.POST)
    public String execute(String command) throws IOException, JSchException {

        Executor executor = new Executor("localhost", 22, "smkv", "/home/smkv/.ssh/id_rsa");
        executor.execute(command, new Log4jCallbackImpl());
        return "redirect:/applications";
    }
}
