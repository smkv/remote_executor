package com.jcraft.jsch;

import java.util.ArrayList;
import java.util.List;

public class TestChannelSftp extends ChannelSftp {
    private List<String> files = new ArrayList<>();

    public void setFiles(List<String> files) {
        this.files = files;
    }

    @Override
    public void ls(String path, LsEntrySelector selector) throws SftpException {
        for (String file : files) {
            int ret = selector.select(new LsEntry(file, file, null));
            if (ret == LsEntrySelector.BREAK) {
                break;
            }
        }
    }
}
