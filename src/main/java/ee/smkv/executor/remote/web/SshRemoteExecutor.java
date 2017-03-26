package ee.smkv.executor.remote.web;

import ee.smkv.executor.remote.Config;
import ee.smkv.executor.remote.Executor;
import ee.smkv.executor.remote.SshServer;
import ee.smkv.executor.remote.StringBufferCallback;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/remoteExecutor")
public class SshRemoteExecutor {

    public static final String EOT = "\u2404";

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Map<String, StringBufferCallback> executions = new HashMap<>();
    private Executor remoteExecutor;
    private SshServer sshServer;


    @PostConstruct
    public void init() throws ParseException {
        sshServer = SshServer.fromString(Config.getProperty("remote.server"));
        String privateKey = Config.getProperty("keys.private");
        remoteExecutor = new Executor(sshServer, privateKey);
    }

    @ModelAttribute
    public SshServer getSshServer(){
        return sshServer;
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdownNow();
    }

    @RequestMapping()
    public String index() {
        return "executor";
    }

    @RequestMapping(value = "/execute", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String execute(String command) throws ParseException {
        StringBufferCallback callback = new StringBufferCallback();
        executorService.execute(() -> remoteExecutor.execute(command, callback));
        return storeCallbackAndReturnKey(callback);

    }

    @RequestMapping(value = "/log", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String log(String key, int offset) {
        StringBufferCallback callback = getCallback(key);
        String executionOutput = callback.toString();
        if (offset > executionOutput.length()) {
            offset = executionOutput.length();
        }
        String incrementalOutput = executionOutput.substring(offset);
        if (callback.isDone()) {
            incrementalOutput += EOT;
        }
        return incrementalOutput;
    }

    private StringBufferCallback getCallback(String key) {
        return executions.get(key);
    }

    private String storeCallbackAndReturnKey(StringBufferCallback callback) {
        String key = UUID.randomUUID().toString();
        executions.put(key, callback);
        return key;
    }

}
