package ee.smkv.executor.remote.web;

import ee.smkv.executor.remote.Config;
import ee.smkv.executor.remote.Executor;
import ee.smkv.executor.remote.SshServer;
import ee.smkv.executor.remote.StringBufferCallback;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/remoteExecutor")
public class SshRemoteExecutor {

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Map<String, StringBufferCallback> executions = new HashMap<>();
    private Executor remoteExecutor;


    @PostConstruct
    public void init() throws ParseException {
        SshServer sshServer = SshServer.fromString(Config.getProperty("remote.server"));
        String privateKey = Config.getProperty("keys.private");
        remoteExecutor = new Executor(sshServer, privateKey);
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
    public String execute(String command, HttpServletResponse response) throws ParseException {
        StringBufferCallback callback = new StringBufferCallback();
        executorService.execute(() -> remoteExecutor.execute(command, callback));
        String key = storeCallbackAndReturnKey(callback);
        response.addHeader("Refresh", "1; url=/remoteExecutor/log?key=" + key);
        return callback.toString();
    }

    @RequestMapping(value = "/log", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String log(String key, HttpServletResponse response){
        StringBufferCallback callback = getCallback(key);
        if (!callback.isDone()) {
            response.addHeader("Refresh", "1; url=/remoteExecutor/log?key=" + key);
        }
        return callback.toString();

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
