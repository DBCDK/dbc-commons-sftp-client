package dk.dbc.commons.sftpclient;

import com.jcraft.jsch.ProxySOCKS5;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SFtpClientIT extends ContainerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(SFtpClientIT.class);

    @Test
    public void check_connection_through_proxy() {
        try (SFtpClient client = getClient()) {
            LOGGER.info("Succesfully connected to '{}' through proxy '{}'", SFTP_HOST, PROXY_HOST);
        }
    }

    @Test
    public void test_no_dir_specified() {
        try (SFtpClient client = getClient()) {
            client.ls("*");
        }
    }

    @Test
    public void test_simple_put_and_get() {
        String someFilesBytes = "test get and put";
        String myTestFile = "myTestFile.txt";
        try (SFtpClient client = getClient()) {
            client.cd(SFTP_DIR);
            client.putContent(myTestFile, getStream(someFilesBytes));
        }

        try (SFtpClient client = getClient()) {
            client.cd(SFTP_DIR);
            String actual = readInputStream(client.getContent(myTestFile));
            assertThat("File bytes are OK", actual, is(someFilesBytes));
        }
    }

    private SFtpClient getClient() {
        ProxySOCKS5 proxySOCKS5Config = new ProxySOCKS5(PROXY_HOST, PROXY_PORT);
        proxySOCKS5Config.setUserPasswd(PROXY_USER, PROXY_PASSWORD);
        return new SFtpClient(
                new SFTPConfig()
                        .withHost(SFTP_HOST)
                        .withPort(SFTP_PORT)
                        .withUsername(SFTP_USER)
                        .withPassword(SFTP_PASSWORD),
                proxySOCKS5Config,
                Arrays.asList("first.inhouse.domain.dk", "inhouse.dk", "some.other.inhouse.domain.dk"));
    }

    private String readInputStream(InputStream is) {
        return new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }

    private InputStream getStream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

}