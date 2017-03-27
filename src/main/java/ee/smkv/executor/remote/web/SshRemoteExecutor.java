package ee.smkv.executor.remote.web;

import ee.smkv.executor.remote.*;
import ee.smkv.executor.remote.ssh.SshExecutor;
import ee.smkv.executor.remote.ssh.SshServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/remoteExecutor")
public class SshRemoteExecutor {

    public static final String EOT = "\u2404";

    private Map<String, Execution> executions = new HashMap<>();
    private Executor remoteExecutor;

    @Autowired
    private SshServer sshServer;


    @PostConstruct
    public void init() throws ParseException {
        remoteExecutor = new SshExecutor(sshServer);
    }

    @ModelAttribute
    public SshServer getSshServer() {
        return sshServer;
    }


    @RequestMapping()
    public String index() {
        return "executor";
    }

    @RequestMapping(value = "/execute", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String execute(String command) throws ParseException {
        return storeCallbackAndReturnKey(remoteExecutor.execute(new Command(command)));

    }

    @RequestMapping(value = "/log", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String log(String key, int offset) {
        Execution execution = getExecution(key);
        String executionOutput = execution.getOutput();
        if (offset > executionOutput.length()) {
            offset = executionOutput.length();
        }
        String incrementalOutput = executionOutput.substring(offset);
        if (execution.isFinished()) {
            incrementalOutput += String.format("ExitCode: %s%n%s", execution.getExitCode(), EOT);
        }
        return incrementalOutput;
    }

    private Execution getExecution(String key) {
        return executions.get(key);
    }

    private String storeCallbackAndReturnKey(Execution execution) {
        String key = UUID.randomUUID().toString();
        executions.put(key, execution);
        return key;
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public boolean cancel(String key) {
        Execution execution = getExecution(key);
        execution.cancel();
        return true;
    }

}
