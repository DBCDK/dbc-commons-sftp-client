package dk.dbc.commons.sftpclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;

import java.time.Duration;

public class ContainerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerTest.class);
    protected static final GenericContainer sftpServer;
    protected static final GenericContainer socks5Proxy;
    protected static final String SFTP_HOST;
    protected static final int SFTP_PORT;
    protected static final String PROXY_HOST;
    protected static final int PROXY_PORT;
    protected static final String SFTP_USER = "sftp";
    protected static final String SFTP_PASSWORD = "sftp";
    protected static final String SFTP_DIR = "upload";
    protected static final String PROXY_USER = "socksuser";
    protected static final String PROXY_PASSWORD = "sockspassword";
    private static final String DOCKER_HOST= "docker-metascrum.artifacts.dbccloud.dk";

    static {
        Network network = Network.newNetwork();
        sftpServer = new GenericContainer(DOCKER_HOST + "/simplesftpserver:latest")
                .withNetwork(network)
                .withNetworkAliases("sftp")
                .withExposedPorts(22)
                .withCommand(String.format("%s:%s:::%s", SFTP_USER, SFTP_PASSWORD, SFTP_DIR))
                .waitingFor(Wait.forLogMessage("^Server listening on.*", 2))
                .withStartupTimeout(Duration.ofMinutes(1));

        socks5Proxy = new GenericContainer(DOCKER_HOST + "/socks5proxy:latest")
                .withNetwork(network)
                .withNetworkAliases("proxy")
                .withExposedPorts(1080)
                .withEnv("USERNAME", PROXY_USER)
                .withEnv("PASSWORD", PROXY_PASSWORD)
                // Wait for 5 threads logging somehing like: "v1.4.2 running."
                .waitingFor(Wait.forLogMessage("^.*v\\d+(\\.\\d+)+ running.*$", 5))
                .withStartupTimeout(Duration.ofMinutes(1));

        sftpServer.start();
        socks5Proxy.start();

        SFTP_HOST = "sftp";
        SFTP_PORT = 22;

        PROXY_HOST = socks5Proxy.getContainerIpAddress();
        PROXY_PORT = socks5Proxy.getMappedPort(1080);

        LOGGER.info(
                "Started suite.\n" +
                        "   SFTP:{} at port {} {}/{}\n" +
                        "   PROXY: {} at port {} {}/{}",
                SFTP_HOST, SFTP_PORT, SFTP_USER, SFTP_PASSWORD,
                PROXY_HOST, PROXY_PORT, PROXY_USER, PROXY_PASSWORD);
    }
}
