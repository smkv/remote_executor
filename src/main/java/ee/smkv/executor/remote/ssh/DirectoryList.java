package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.ChannelSftp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryList implements SftpChannelTemplate, ChannelSftp.LsEntrySelector {

    private final String path;
    private List<ChannelSftp.LsEntry> list = new ArrayList<>();

    public DirectoryList(String path) {
        this.path = path;
    }

    @Override
    public void withSftpChannel(ChannelSftp channel) throws Exception {
        channel.ls(path, this);
    }

    public String getPath() {
        return path;
    }

    public List<ChannelSftp.LsEntry> getList() {
        return list;
    }

    @Override
    public int select(ChannelSftp.LsEntry entry) {
        if (!isHidden(entry)) {
            list.add(entry);
        }
        return ChannelSftp.LsEntrySelector.CONTINUE;
    }

    protected boolean isHidden(ChannelSftp.LsEntry entry) {
        return entry.getFilename().startsWith(".");
    }

}
