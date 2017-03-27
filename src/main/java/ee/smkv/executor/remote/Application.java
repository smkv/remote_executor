package ee.smkv.executor.remote;

import ee.smkv.executor.remote.ssh.SshServer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.WebApplicationInitializer;

import java.text.ParseException;

@SpringBootApplication
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer implements WebApplicationInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }

    @Bean
    SshServer createSshServer() throws ParseException {
        return SshServer.fromStringWithPrivateKey(Config.getProperty("remote.server"), Config.getProperty("keys.private"));
    }
}
