package dk.dbc.commons.sftpclient;

public class SFtpClientException extends RuntimeException {
    public SFtpClientException(Exception e) {
        super(e);
    }
}