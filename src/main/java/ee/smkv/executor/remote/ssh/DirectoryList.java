package ee.smkv.executor.remote.ssh;

import com.jcraft.jsch.ChannelSftp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryList implements SftpChannelTemplate {
    private final String path;
    private List<ChannelSftp.LsEntry> list = Collections.emptyList();

    public DirectoryList(String path) {
        this.path = path;
    }

    @Override
    public void withSftpChannel(ChannelSftp channel) throws Exception {
        ArrayList<ChannelSftp.LsEntry> entries = new ArrayList<>();
        channel.ls(path, entry -> {
            if (!isHidden(entry)) {
                entries.add(entry);
            }
            return ChannelSftp.LsEntrySelector.CONTINUE;
        });
        list = Collections.unmodifiableList(entries);
    }

    private boolean isHidden(ChannelSftp.LsEntry entry) {
        return entry.getFilename().startsWith(".");
    }

    public String getPath() {
        return path;
    }

    public List<ChannelSftp.LsEntry> getList() {
        return list;
    }
}
