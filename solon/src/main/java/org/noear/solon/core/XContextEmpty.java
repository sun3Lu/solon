package org.noear.solon.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XContextEmpty extends XContext {
    public static XContext create(){
        return new XContextEmpty();
    }

    @Override
    public Object request() {
        return null;
    }

    @Override
    public String ip() {
        return null;
    }

    @Override
    public String method() {
        return null;
    }

    @Override
    public String protocol() {
        return null;
    }

    @Override
    public URI uri() {
        return null;
    }

    @Override
    public String path() {
        return null;
    }

    @Override
    public String url() {
        return null;
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
    public String body() throws IOException {
        return null;
    }

    @Override
    public InputStream bodyAsStream() throws IOException {
        return null;
    }

    @Override
    public String[] paramValues(String key) {
        return new String[0];
    }

    @Override
    public String param(String key) {
        return paramMap().get(key);
    }

    @Override
    public String param(String key, String def) {
        return paramMap().getOrDefault(key, def);
    }

    @Override
    public int paramAsInt(String key) {
        return paramMap().getInt(key);
    }

    @Override
    public long paramAsLong(String key) {
        return paramMap().getLong(key);
    }

    @Override
    public double paramAsDouble(String key) {
        return paramMap().getDouble(key);
    }

    private XMap _paramMap = null;
    @Override
    public XMap paramMap() {
        if(_paramMap == null){
            _paramMap = new XMap();
        }
        return _paramMap;
    }

    @Override
    public void paramSet(String key, String val) {
        paramMap().put(key,val);
    }

    @Override
    public List<XFile> files(String key) throws Exception {
        return null;
    }

    @Override
    public String cookie(String key) {
        return cookieMap().get(key);
    }

    @Override
    public String cookie(String key, String def) {
        return cookieMap().getOrDefault(key,def);
    }

    XMap _cookieMap = null;
    @Override
    public XMap cookieMap() {
        if(_cookieMap == null){
            _cookieMap = new XMap();
        }
        return _cookieMap;
    }

    @Override
    public String header(String key) {
        return headerMap().get(key);
    }

    @Override
    public String header(String key, String def) {
        return headerMap().getOrDefault(key,def);
    }

    private XMap _headerMap = null;
    @Override
    public XMap headerMap() {
        if(_headerMap == null){
            _headerMap = new XMap();
        }
        return _headerMap;
    }

    private Map<String,Object> _sessionMap = null;
    public Map<String,Object> sessionMap(){
        if(_sessionMap == null){
            _sessionMap = new HashMap<>();
        }

        return _sessionMap;
    }

    @Override
    public String sessionId() {
        return null;
    }

    @Override
    public Object session(String key) {
        return sessionMap().get(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        sessionMap().put(key,val);
    }

    @Override
    public Object response() {
        return null;
    }

    @Override
    public void charset(String charset) {

    }

    @Override
    public void contentType(String contentType) {

    }

    @Override
    public void output(String str) throws IOException {

    }

    @Override
    public void output(InputStream stream) throws IOException {

    }

    @Override
    public OutputStream outputStream() throws IOException {
        return null;
    }

    @Override
    public void headerSet(String key, String val) {
        headerMap().put(key,val);
    }

    @Override
    public void cookieSet(String key, String val, int maxAge) {
        cookieMap().put(key,val);
    }

    @Override
    public void cookieSet(String key, String val, String domain, int maxAge) {
        cookieMap().put(key,val);
    }

    @Override
    public void cookieSet(String key, String val, String domain, String path, int maxAge) {
        cookieMap().put(key,val);
    }

    @Override
    public void cookieRemove(String key) {
        cookieMap().remove(key);
    }

    @Override
    public void redirect(String url) throws IOException {

    }

    @Override
    public void redirect(String url, int code) throws IOException {

    }

    private int _status = 0;
    @Override
    public int status() {
        return _status;
    }

    @Override
    public void status(int status) throws IOException {
        _status = status;
    }
}