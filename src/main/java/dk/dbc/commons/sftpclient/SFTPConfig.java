package dk.dbc.commons.sftpclient;

public class SFTPConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String dir;
    private String filesPattern;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFilesPattern() {
        return filesPattern;
    }

    public void setFilesPattern(String filesPattern) {
        this.filesPattern = filesPattern;
    }

    public SFTPConfig withHost(String host) {
        this.host = host;
        return this;
    }

    public SFTPConfig withPort(int port) {
        this.port = port;
        return this;
    }

    public SFTPConfig withUsername(String username) {
        this.username = username;
        return this;
    }

    public SFTPConfig withPassword(String password) {
        this.password = password;
        return this;
    }

    public SFTPConfig withDir(String dir) {
        this.dir = dir;
        return this;
    }

    public SFTPConfig withFilesPattern(String filesPattern) {
        this.filesPattern = filesPattern;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SFTPConfig that = (SFTPConfig) o;

        if (port != that.port) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (dir != null ? !dir.equals(that.dir) : that.dir != null) return false;
        return filesPattern != null ? filesPattern.equals(that.filesPattern) : that.filesPattern == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (dir != null ? dir.hashCode() : 0);
        result = 31 * result + (filesPattern != null ? filesPattern.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SFTPConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", dir='" + dir + '\'' +
                ", filesPattern='" + filesPattern + '\'' +
                '}';
    }
}
