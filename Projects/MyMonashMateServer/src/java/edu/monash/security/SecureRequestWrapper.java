/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.monash.security;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author zipv5_000
 */
public class SecureRequestWrapper extends HttpServletRequestWrapper{

    private String payload;
    
    public SecureRequestWrapper(HttpServletRequest request, String payload) {
        super(request);
        this.payload = payload;
    }
    @Override
    public ServletInputStream getInputStream(){
        final ByteArrayInputStream byteStream = new ByteArrayInputStream(payload.getBytes());
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean isReady() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public int read() throws IOException {
                return byteStream.read();
            }
        };
    }
}
