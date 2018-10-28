package com.jcraft.jsch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TestChannelSftp extends ChannelSftp {
    private List<String> files = new ArrayList<>();
    private byte[] bytes = new byte[0];

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
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

    @Override
    public InputStream get(String src) throws SftpException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void put(InputStream src, String dst) throws SftpException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            int b;
            while ((b = src.read()) != -1) {
                buffer.write(b);
            }
        } catch (IOException e) {
            throw new SftpException(0, e.getMessage(), e);
        }
        bytes = buffer.toByteArray();
    }
}
