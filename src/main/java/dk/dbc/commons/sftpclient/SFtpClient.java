package dk.dbc.commons.sftpclient;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxySOCKS5;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SFtpClient implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            SFtpClient.class);
    private Session session = null;
    private ChannelSftp channelSftp = null;
    ProxySOCKS5 proxyHandlerBean = null;
    SFTPConfig config = null;
    private static final JSch jsch = new JSch();
    private static final Properties jschConfig = new Properties();

    static {
        jschConfig.setProperty("StrictHostKeyChecking", "no");
    }

    public SFtpClient(SFTPConfig config, ProxySOCKS5 proxyHandlerBean)  {

        LOGGER.info("Trying to connect to '{}' at port '{}' with user '{}' at path '{}'",
                config.getHost(),
                config.getPort(),
                config.getUsername(),
                config.getDir());
        this.proxyHandlerBean = proxyHandlerBean;
        this.config = config;
        connect();
    }

    public SFtpClient(SFTPConfig config, ProxySOCKS5 proxyHandlerBean, List<String> nonProxiedHosts)  {
        LOGGER.info("Trying to connect to '{}' at port '{}' with user '{}' at path '{}'",
                config.getHost(),
                config.getPort(),
                config.getUsername(),
                config.getDir());
        boolean isProxied = nonProxiedHosts.stream().noneMatch(domain -> config.getHost().endsWith(domain));
        if (isProxied) {
            this.proxyHandlerBean = proxyHandlerBean;
        } else {
            this.proxyHandlerBean = null;
        }

        this.config = config;
        connect();
    }



    void connect() {
        try {
            session = jsch.getSession(config.getUsername(), config.getHost(), config.getPort());
            if (proxyHandlerBean != null) {
                session.setProxy(proxyHandlerBean);
            }
            session.setPassword(config.getPassword());
            session.setConfig(jschConfig);
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.cd(config.getDir());
            LOGGER.info("Connection to '{}' was succesful.", config.getHost());
        } catch (JSchException | SftpException e) {
            throw new SFtpClientException(e);
        }
    }

    @Override
    public void close() throws SFtpClientException {

        if (channelSftp != null) {
            channelSftp.exit();
            channelSftp.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
        LOGGER.info("SFtpClient for '{}' port '{}' was succesfully closed", config.getHost(), config.getPort());
    }

    private void verifyConnection() {
        if (session == null || !session.isConnected() ||
                channelSftp == null || channelSftp.isClosed()) {
            close();
            connect();
        }
    }

    public Vector<ChannelSftp.LsEntry> ls(String pattern) {
        verifyConnection();
        try {
            Vector files = channelSftp.ls(pattern);
            return files;
        } catch (SftpException e) {
            throw new SFtpClientException(e);
        }
    }

    public InputStream getContent(String filename) {
        LOGGER.info("Getting content of file:{}", filename);
        verifyConnection();
        try {
            return channelSftp.get(filename);
        } catch (SftpException e) {
            throw new SFtpClientException(e);
        }
    }

    public void putContent(String filename, InputStream content) {
        verifyConnection();
        try {
            channelSftp.put(content, filename);
        } catch (SftpException e) {
            throw new SFtpClientException(e);
        }
    }

    public String pwd() {
        verifyConnection();
        try {
            return channelSftp.pwd();
        } catch (SftpException e) {
            throw  new SFtpClientException(e);
        }
    }

    public void mkdir(String dirname) {
        verifyConnection();
        try {
            channelSftp.mkdir(dirname);
        } catch (SftpException e) {
            throw new SFtpClientException(e);
        }
    }

    public void cd(String dirname) {
        verifyConnection();
        try {
            channelSftp.cd(dirname);
        } catch (SftpException e) {
            throw new SFtpClientException(e);
        }
    }

    public void rm(String filename) {
        verifyConnection();
        try {
            channelSftp.rm(filename);
        } catch (SftpException e) {
            throw new SFtpClientException(e);
        }
    }

    public void rmdir(String dirname) {
        verifyConnection();
        try {
            channelSftp.rmdir(dirname);
        } catch (SftpException e) {
            throw new SFtpClientException(e);
        }
    }
}
