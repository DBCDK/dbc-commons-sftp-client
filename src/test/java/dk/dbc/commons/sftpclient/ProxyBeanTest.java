package dk.dbc.commons.sftpclient;

import com.jcraft.jsch.ProxySOCKS5;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProxyBeanTest {
    private static class MockSftpClient extends SFtpClient {
        public MockSftpClient(SFTPConfig config, ProxySOCKS5 proxyHandlerBean, List<String> nonProxiedHosts) {
            super(config, proxyHandlerBean, nonProxiedHosts);
        }

        @Override
        public void connect() {}
    }

    @Test
    public void test_that_inhouse_url_disables_proxying() {
        ProxySOCKS5 proxyHandlerBean = new ProxySOCKS5("test_proxy_host");
        MockSftpClient sftpClient = new MockSftpClient(
                new SFTPConfig().withHost("my_inside_test_host.inhouse.dk"),
                proxyHandlerBean, Arrays.asList("first.inhouse.domain.dk", "inhouse.dk", "some.other.inhouse.domain.dk"));

        assertThat("NOT Proxy'ing", sftpClient.proxy, nullValue());
    }

    @Test
    public void test_that_an_outofthehouse_url_is_proxied() {
        ProxySOCKS5 proxyHandlerBean = new ProxySOCKS5("test_proxy_host");
        MockSftpClient sftpClient = new MockSftpClient(
                new SFTPConfig().withHost("outofthehouse_test_host.outofthehouse.dk"),
                proxyHandlerBean, Arrays.asList("first.inhouse.domain.dk", "inhouse.dk", "some.other.inhouse.domain.dk"));

        assertThat("Proxy'ing", sftpClient.proxy, notNullValue());
    }

    @Test
    public void test_that_everything_is_proxied_on_an_empty_list() {
        ProxySOCKS5 proxyHandlerBean = new ProxySOCKS5("test_proxy_host");
        MockSftpClient sftpClient = new MockSftpClient(
                new SFTPConfig().withHost("my_inside_test_host.inhouse.dk"),
                proxyHandlerBean, Collections.EMPTY_LIST);

        assertThat("Proxy'ing", sftpClient.proxy, notNullValue());
    }
}
