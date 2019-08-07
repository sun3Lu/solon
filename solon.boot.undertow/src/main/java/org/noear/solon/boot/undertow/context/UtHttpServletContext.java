package org.noear.solon.boot.undertow.context;

import io.undertow.server.HttpServerExchange;
import org.noear.solon.XUtil;
import org.noear.solon.boot.undertow.ext.MultipartUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import org.noear.solon.core.XMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

//和JtHttpContext区别不大的上下文类，不支持XNIO
public class UtHttpServletContext extends XContext {
    private HttpServletRequest _request;
    private HttpServletResponse _response;
    private HttpServerExchange _exchange;

    public UtHttpServletContext(HttpServletRequest request, HttpServletResponse response, HttpServerExchange exchange) {
        _request = request;
        _response = response;
        _exchange = exchange;
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String ip() {
        return _request.getRemoteAddr();
    }

    @Override
    public String method() {
        return _request.getMethod();
    }

    @Override
    public String protocol() {
        return _request.getProtocol();
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
        return _request.getRequestURI();
    }

    @Override
    public String url() {
        return _request.getRequestURL().toString();
    }

    @Override
    public long contentLength() {
        return _request.getContentLength();
    }

    @Override
    public String contentType() {
        return _request.getContentType()==null?"":_request.getContentType();
    }

    @Override
    public String body() throws IOException {
        InputStream inpStream = bodyAsStream();

        StringBuilder content = new StringBuilder();
        byte[] b = new byte[1024];
        int lens = -1;
        while ((lens = inpStream.read(b)) > 0) {
            content.append(new String(b, 0, lens));
        }

        return content.toString();
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return _request.getInputStream();
    }

    @Override
    public String[] paramValues(String key) {
        return _request.getParameterValues(key);
    }

    @Override
    public String param(String key) {
        return paramMap().get(key);
    }

    @Override
    public String param(String key, String def) {
        String temp = paramMap().get(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return temp;
        }
    }

    @Override
    public int paramAsInt(String key) {
        return Integer.parseInt(param(key, "0"));
    }

    @Override
    public long paramAsLong(String key) {
        return Long.parseLong(param(key, "0"));
    }

    @Override
    public double paramAsDouble(String key) {
        return Double.parseDouble(param(key, "0"));
    }

    @Override
    public XMap paramMap() {
        if (_paramMap == null) {
            _paramMap = new XMap();

            Enumeration<String> names = _request.getParameterNames();

            while (names.hasMoreElements()) {
                String name = names.nextElement();
                String value = _request.getParameter(name);
                _paramMap.put(name, value);
            }
        }

        return _paramMap;
    }

    private XMap _paramMap;

    @Override
    public void paramSet(String key, String val) {
        paramMap().put(key, val);
    }

    @Override
    public List<XFile> files(String key) throws Exception {
        if (isMultipartFormData()) {
            return MultipartUtil.getUploadedFiles(this, key);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public String cookie(String key) {
        return cookie(key, null);
    }

    @Override
    public String cookie(String key, String def) {
        String temp = cookieMap().get(key);
        if (temp == null) {
            return def;
        } else {
            return temp;
        }
    }

    private XMap _cookieMap;

    @Override
    public XMap cookieMap() {
        if (_cookieMap == null) {
            _cookieMap = new XMap();

            Cookie[] _cookies = _request.getCookies();

            if (_cookies != null) {
                for (Cookie c : _cookies) {
                    _cookieMap.put(c.getName(), c.getValue());
                }
            }
        }

        return _cookieMap;
    }

    @Override
    public String header(String key) {
        return _request.getHeader(key);
    }

    @Override
    public String header(String key, String def) {
        String temp = _request.getHeader(key);
        if (XUtil.isEmpty(temp)) {
            return def;
        } else {
            return temp;
        }
    }

    @Override
    public XMap headerMap() {
        if (_headerMap == null) {
            _headerMap = new XMap();
            Enumeration<String> headers = _request.getHeaderNames();

            while (headers.hasMoreElements()) {
                String key = (String) headers.nextElement();
                String value = _request.getHeader(key);
                _headerMap.put(key, value);
            }
        }

        return _headerMap;
    }

    private XMap _headerMap;

    /* @Override
     public String sessionId() {

         SessionManager sm = _exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
         SessionConfig sc = _exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
         return sm.getSession(_exchange, sc).getId();
     }

     @Override
     public Object session(String key) {
         SessionConfig sc = _exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);

         return _exchange
                 .getAttachment(SessionManager.ATTACHMENT_KEY)
                 .getSession(_exchange, sc).getAttribute(key);
     }

     @Override
     public void sessionSet(String key, Object val) {
         SessionConfig sc = _exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);

         _exchange.getAttachment(SessionManager.ATTACHMENT_KEY)
                 .getSession(_exchange, sc)
                 .setAttribute(key, val);

     }*/
    @Override
    public String sessionId() {
        return _request.getRequestedSessionId();
    }

    @Override
    public Object session(String key) {
        return _request.getSession().getAttribute(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        _request.getSession().setAttribute(key, val);
    }


    //====================================

    @Override
    public Object response() {
        return _response;
    }

    @Override
    public void charset(String charset) {
        _response.setCharacterEncoding(charset);
    }

    @Override
    public void contentType(String contentType) {
        _response.setContentType(contentType);
    }


    @Override
    public OutputStream outputStream() throws IOException {
        return _response.getOutputStream();
    }

    @Override
    public void output(String str) throws IOException {
        PrintWriter writer = _response.getWriter();
        writer.write(str);
        writer.flush();
    }

    @Override
    public void output(InputStream stream) throws IOException {
        OutputStream out = _response.getOutputStream();

        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = stream.read(buff, 0, 100)) > 0) {
            out.write(buff, 0, rc);
        }

        out.flush();
    }

    @Override
    public void headerSet(String key, String val) {
        _response.setHeader(key, val);
    }

    @Override
    public void cookieSet(String key, String val, int maxAge) {
        Cookie c = new Cookie(key, val);
        c.setPath("/");
        c.setMaxAge(maxAge);

        _response.addCookie(c);
    }

    @Override
    public void cookieSet(String key, String val, String domain, int maxAge) {
        cookieSet(key, val, domain, "/", maxAge);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        Cookie c = new Cookie(key, val);
        c.setPath(path);
        c.setMaxAge(maxAge);
        c.setDomain(domain);
        _response.addCookie(c);
    }

    @Override
    public void cookieRemove(String key) {
        Cookie c = new Cookie(key, "");
        c.setPath("/");
        c.setMaxAge(0);

        _response.addCookie(c);
    }

    @Override
    public void redirect(String url) throws IOException {
        _response.sendRedirect(url);
    }

    @Override
    public void redirect(String url, int code) throws IOException {
        status(code);
        _response.setHeader("Location", url);
    }

    @Override
    public int status() {
        return _response.getStatus();
    }

    @Override
    public void status(int status) throws IOException {
        _response.setStatus(status);
    }
}