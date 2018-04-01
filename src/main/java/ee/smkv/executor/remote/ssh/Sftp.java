package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.ChannelSftp;

import java.util.List;

public class Sftp {
    private final SshServer sshServer;

    public Sftp(SshServer sshServer) {
        this.sshServer = sshServer;
    }

    public List<ChannelSftp.LsEntry> getDirectoryList(String path) throws Exception {
        DirectoryList template = new DirectoryList(path);
        sshServer.doInSftpChannel(template);
        return template.getList();
    }

    public byte[] getFileContent(String path) throws Exception {
        DownloadFile template = new DownloadFile(path);
        sshServer.doInSftpChannel(template);
        return template.getContent();
    }

    public void setFileContent(String path, byte[] content) throws Exception {
        sshServer.doInSftpChannel(new UploadFile(path, content));
    }

    public void setFileContent(String path, String content, String encoding) throws Exception {
        sshServer.doInSftpChannel(new UploadFile(path, content.getBytes(encoding)));
    }

    public void setFileContent(String path, String content) throws Exception {
        sshServer.doInSftpChannel(new UploadFile(path, content.getBytes()));
    }
}
