package org.noear.solon.boot.undertow.context;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import org.noear.solon.XUtil;
import org.noear.solon.boot.undertow.ext.MultipartUtil;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XFile;
import org.noear.solon.core.XMap;
import org.noear.solon.core.XSessionState;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.*;

/**
 * 对XNIO友好的上下文
 * 扩展功能，增加通用性（不该仅限于httpServletXXX）
 * author： Yukai
 **/
public class UtHttpExchangeContext extends XContext {
    private ServletRequest _request;
    private ServletResponse _response;
    private HttpServerExchange _exchange;

    public UtHttpExchangeContext(ServletRequest request, ServletResponse response, HttpServerExchange exchange) {
        _request = request;
        _response = response;
        _exchange = exchange;

        sessionStateInit(new XSessionState() {
            @Override
            public String sessionId() {
                SessionManager sm = _exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
                SessionConfig sc = _exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
                return sm.getSession(_exchange, sc).getId();
            }

            @Override
            public Object sessionGet(String key) {
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
            }
        });
    }

    @Override
    public Object request() {
        return _request;
    }

    @Override
    public String ip() {
        if (_request instanceof HttpServletRequest) {
            return ((HttpServletRequest) _request).getRemoteAddr();
        }
        return "";
    }

    @Override
    public String method() {
        if (_request instanceof HttpServletRequest) {
            return ((HttpServletRequest) _request).getMethod();
        }
        return _exchange.getRequestMethod().toString();
    }

    @Override
    public String protocol() {
        if (_request instanceof HttpServletRequest) {
            return _request.getProtocol();
        }
        return _exchange.getProtocol().toString();
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
        if (_request instanceof HttpServletRequest) {
            return ((HttpServletRequest) _request).getRequestURI();
        }
        return _exchange.getRequestURI();
    }

    @Override
    public String url() {
        if (_request instanceof HttpServletRequest) {
            return ((HttpServletRequest) _request).getRequestURL().toString();
        }
        return _exchange.getRequestURL();
    }

    @Override
    public long contentLength() {
        if (_request instanceof HttpServletRequest) {
            return ((HttpServletRequest) _request).getContentLength();
        }
        return _exchange.getRequestContentLength();
    }

    @Override
    public String contentType() {
        if (_request instanceof HttpServletRequest) {
            return ((HttpServletRequest) _request).getContentType();
        }
        HeaderMap headers = _exchange.getRequestHeaders();
        String content_type = headers.getFirst("Content-Type");
        if (content_type == null) {
            return "";
        } else {
            return content_type;
        }
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
        if (_request instanceof HttpServletRequest) {
            return ((HttpServletRequest) _request).getParameterValues(key);
        }
        // 尝试从exchange中获取
        Map<String, Deque<String>> query = _exchange.getQueryParameters();
        Deque<String> deque = query.get(key);
        return deque.toArray(new String[deque.size()]);
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

            if (_request instanceof HttpServletRequest) {
                Enumeration<String> names = ((HttpServletRequest) _request).getParameterNames();

                while (names.hasMoreElements()) {
                    String name = names.nextElement();
                    String value = ((HttpServletRequest) _request).getParameter(name);
                    _paramMap.put(name, value);
                }
            } else {
                Map<String, Deque<String>> params = _exchange.getQueryParameters();
                params.forEach((s, deque) -> _paramMap.put(s, deque.getFirst()));
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

            Cookie[] _cookies = null;
            if (_request instanceof HttpServletRequest) {
                _cookies = ((HttpServletRequest) _request).getCookies();

                if (_cookies != null) {
                    for (Cookie c : _cookies) {
                        _cookieMap.put(c.getName(), c.getValue());
                    }
                }
            } else {
                Map<String, io.undertow.server.handlers.Cookie> cookieMap = _exchange.getRequestCookies();
                cookieMap.forEach((s, cookie) -> {
                    _cookieMap.put(cookie.getName(), cookie.getValue());
                });
            }

        }

        return _cookieMap;
    }

    @Override
    public String header(String key) {
        if (_request instanceof HttpServletRequest) {
            return ((HttpServletRequest) _request).getHeader(key);
        }
        HeaderMap headers = _exchange.getRequestHeaders();
        return headers.getFirst(key);
    }

    @Override
    public String header(String key, String def) {
        String temp = header(key);
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
            HeaderMap headers = _exchange.getRequestHeaders();
            headers.forEach(hv -> {
                headerMap().put(hv.getHeaderName().toString(), hv.getFirst());
            });
        }

        return _headerMap;
    }

    private XMap _headerMap;



    //====================================

    @Override
    public Object response() {
        return _response;
    }

    @Override
    public void charset(String charset) {
//        if (_response instanceof HttpServletResponse) {
            _response.setCharacterEncoding(charset);
//        }
    }

    @Override
    public void contentType(String contentType) {
//        if (_response instanceof HttpServletResponse) {
            _response.setContentType(contentType);
//        }
    }


    @Override
    public OutputStream outputStream() throws IOException {
        return _exchange.getOutputStream();
    }

    @Override
    public void output(String str)  {
        PrintWriter writer = new PrintWriter(_exchange.getOutputStream());
        writer.write(str);
        writer.flush();
    }

    @Override
    public void output(InputStream stream) {
        try {
            OutputStream out = _exchange.getOutputStream();

            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = stream.read(buff, 0, 100)) > 0) {
                out.write(buff, 0, rc);
            }

            out.flush();
        }catch (Throwable ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void headerSet(String key, String val) {
        if (_response instanceof HttpServletResponse) {
            ((HttpServletResponse) _response).setHeader(key, val);
        }
        HeaderMap respHeaders = _exchange.getResponseHeaders();
        respHeaders.add(new HttpString(key), val);
    }

    @Override
    public void cookieSet(String key, String val, int maxAge) {
        Cookie c = new Cookie(key, val);
        c.setPath("/");
        c.setMaxAge(maxAge);

        if (_response instanceof HttpServletResponse) {
            ((HttpServletResponse) _response).addCookie(c);
        } else {
            _exchange.setResponseCookie(new CookieImpl(key, val).setMaxAge(maxAge));
        }

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

        cookieSet(key, val, maxAge);
    }

    @Override
    public void cookieRemove(String key) {
        Cookie c = new Cookie(key, "");
        c.setPath("/");
        c.setMaxAge(0);

        _exchange.setResponseCookie(new CookieImpl("key").setMaxAge(0));
    }

    @Override
    public void redirect(String url) {
        if (_response instanceof HttpServletResponse) {
            try {
                ((HttpServletResponse) _response).sendRedirect(url);
            }catch (Throwable ex){
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void redirect(String url, int code) {
        status(code);
        if (_response instanceof HttpServletResponse) {
            ((HttpServletResponse) _response).setHeader("Location", url);
            ;
        } else {
            //todo _exchange mode

        }
    }

    @Override
    public int status() {
        return _exchange.getStatusCode();
    }

    @Override
    public void status(int status) {
        _exchange.setStatusCode(status);
    }
}
