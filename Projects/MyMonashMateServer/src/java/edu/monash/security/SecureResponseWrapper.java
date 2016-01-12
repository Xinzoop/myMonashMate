/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.monash.security;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 *
 * @author zipv5_000
 */
public class SecureResponseWrapper extends HttpServletResponseWrapper{

    private ByteArrayOutputStream outStream;
    
    public SecureResponseWrapper(HttpServletResponse response, ByteArrayOutputStream outStream) {
        super(response);
        this.outStream = outStream;
    }
    
    @Override
    public ServletOutputStream getOutputStream(){
        return new ServletOutputStream() {

            @Override
            public boolean isReady() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void write(int i) throws IOException {
                outStream.write(i);
            }
        };
    }
}
