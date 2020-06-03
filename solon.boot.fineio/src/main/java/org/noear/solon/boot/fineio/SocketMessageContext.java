package org.noear.solon.boot.fineio;

import org.noear.fineio.NetSession;
import org.noear.solon.core.SocketMessage;
import org.noear.solon.core.XContextEmpty;
import org.noear.solon.core.XMethod;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;

public class SocketMessageContext extends XContextEmpty {
    private InetSocketAddress _inetSocketAddress;
    private NetSession<SocketMessage> _session;
    private SocketMessage _message;

    public SocketMessageContext(NetSession<SocketMessage> session) {
        _session = session;
        _message = session.message();

        try {
            _inetSocketAddress = session.getRemoteAddress();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Object request() {
        return _session;
    }

    @Override
    public String ip() {
        if (_inetSocketAddress == null)
            return null;
        else
            return _inetSocketAddress.getAddress().getHostAddress();
    }

    @Override
    public boolean isMultipart() {
        return false;
    }

    @Override
    public String method() {
        return XMethod.SOCKET.name;
    }

    @Override
    public String protocol() {
        return "SOCKET";
    }

    @Override
    public URI uri() {
        if (_uri == null) {
            _uri = URI.create(url());
        }

        return _uri;
    }

    private URI _uri;

    @Override
    public String path() {
        return uri().getPath();
    }

    @Override
    public String url() {
        return _message.resourceDescriptor;
    }

    @Override
    public long contentLength() {
        return 0;
    }

    @Override
    public String contentType() {
        return null;
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return new ByteArrayInputStream(_message.content);
    }

    //==============

    @Override
    public Object response() {
        return _session;
    }

    @Override
    public void contentType(String contentType) {
        headerSet("Content-Type", contentType);
    }


    ByteArrayOutputStream _outputStream = new ByteArrayOutputStream();

    @Override
    public OutputStream outputStream() {
        return _outputStream;
    }

    @Override
    public void output(String str) {
        try {
            outputStream().write(str.getBytes(_charset));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void output(InputStream stream) {
        try {
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = stream.read(buff, 0, 100)) > 0) {
                outputStream().write(buff, 0, rc);
            }

        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    protected void commit() throws IOException {
        if (_session.isOpen()) {
            SocketMessage msg =  SocketMessage.wrap(_message.key, _message.resourceDescriptor, _outputStream.toByteArray());
            _session.writeAndFlush(msg.encode());
        }
    }

    @Override
    public void close() throws IOException {
        _session.close();
    }
}