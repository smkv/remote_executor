package ee.smkv.executor.remote;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SshServer {
    private final String host;
    private final int port;
    private final String user;
    private static final Pattern SERVER_PARSE_PATTERN = Pattern.compile("([^@]+)@([^@:]+):([0-9]+)");

    public SshServer(String user, String host, int port) {
        this.host = host;
        this.port = port;
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return String.format("%s@%s:%d", user, host, port);
    }

    public static SshServer fromString(String server) throws ParseException {
        Matcher matcher = SERVER_PARSE_PATTERN.matcher(server);
        if (matcher.matches()) {
            return new SshServer(matcher.group(1), matcher.group(2), Integer.valueOf(matcher.group(3)));
        }
        throw new ParseException("SSH server string has wrond format", 0);
    }
}
