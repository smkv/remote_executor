package ee.smkv.executor.remote.web;

import com.jcraft.jsch.JSchException;
import ee.smkv.executor.remote.Config;
import ee.smkv.executor.remote.Executor;
import ee.smkv.executor.remote.SshServer;
import ee.smkv.executor.remote.StringBufferCallback;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/executor")
public class SshRemoteExecutor {

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Map<String, StringBufferCallback> executions = new HashMap<>();


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
    public String execute(String command, HttpServletResponse response) throws IOException, JSchException, ParseException {
        SshServer sshServer = SshServer.fromString(Config.getProperty("remote.server"));
        String privateKey = Config.getProperty("keys.private");
        Executor executor = new Executor(sshServer, privateKey);
        StringBufferCallback callback = new StringBufferCallback();
        executorService.execute(() -> {
            try {
                executor.execute(command, callback);
            } catch (JSchException | IOException e) {
                e.printStackTrace();
            }
        });
        String key = UUID.randomUUID().toString();
        executions.put(key, callback);
        response.addHeader("Refresh", "1; url=/executor/log?key=" + key);
        return callback.toString();
    }

    @RequestMapping(value = "/log", method = RequestMethod.GET , produces = "text/plain")
    @ResponseBody
    public String log(String key, HttpServletResponse response) throws IOException, JSchException, ParseException {

        StringBufferCallback callback = executions.get(key);
        if (!callback.isDone()) {
            response.addHeader("Refresh", "1; url=/executor/log?key=" + key);
        }
        return callback.toString();

    }

}
