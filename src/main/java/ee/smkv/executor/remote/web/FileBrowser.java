package ee.smkv.executor.remote.web;

import ee.smkv.executor.remote.ssh.Sftp;
import ee.smkv.executor.remote.ssh.SshExecutor;
import ee.smkv.executor.remote.ssh.SshServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;

@Controller
@RequestMapping("/remoteFileSystem")
public class FileBrowser {

    @Autowired
    private SshServer sshServer;

    private Sftp sftp;

    @PostConstruct
    public void init() throws ParseException {
        sftp = new Sftp(sshServer);
    }

    @ModelAttribute
    public SshServer getSshServer() {
        return sshServer;
    }


    @RequestMapping()
    public String index(Model model, @RequestParam(value = "path", required = false, defaultValue = ".") String path) throws Exception {
        model.addAttribute("path", path);
        model.addAttribute("list", sftp.getDirectoryList(path));
        return "browser";
    }

    @RequestMapping("/download")
    public void download(HttpServletResponse response, String path) throws Exception {

        byte[] content = sftp.getFileContent(path);
        String fileName = new File(path).getName();
        response.setContentType("application/octet-stream");
        response.setContentLength(content.length);
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        response.getOutputStream().write(content);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String upload(@RequestParam("path") String path, @RequestParam("file") MultipartFile file) throws Exception {
        String destinationFile = path + "/" + file.getOriginalFilename();
        sftp.setFileContent(destinationFile, file.getBytes());
        return "redirect:/remoteFileSystem?path=" + URLEncoder.encode(path, "UTF-8");
    }
}
