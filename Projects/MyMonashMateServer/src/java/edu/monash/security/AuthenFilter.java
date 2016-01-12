/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.monash.security;

import edu.monash.services.ApplicationConfig;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import javax.crypto.SecretKey;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zipv5_000
 */
public class AuthenFilter implements Filter {

    private SecureReceiver clerk;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            clerk = new SecureReceiver();
        } catch (Exception e) {
            throw new ServletException("Filter init: " + e.getMessage());
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        PrintWriter writer = response.getWriter();
        String result = "";
        String uri = req.getRequestURI();
        String method = req.getMethod();

        // First shakehand
        if (uri.equalsIgnoreCase(ApplicationConfig.URI_FACADE) && method.equalsIgnoreCase(ApplicationConfig.METHOD_GET)) {
            // Return server public key
            result = clerk.getServerPublKeyString();
            writer.write(result);
            writer.flush();
            return;
        }
        // acquire request payload
        String payload = "";
        try {
            payload = getResquestPayload(req);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Retrieve request payload: " + e.getClass().toString() + " : " + e.getMessage());
            return;
        }
        // acquire symmetry key to decrypt payload
        SecretKey symKey = null;
        try {
            String keyString = URLDecoder.decode(req.getHeader(ApplicationConfig.HEADER_KEY), ApplicationConfig.URL_CODE);
            symKey = clerk.getSecretKey(keyString);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Retrieve symmetry key: " + e.getClass().toString() + " : " + e.getMessage());
            return;
        }
        if (!payload.isEmpty()) {
            try {
                payload = clerk.Decrypt(payload, symKey);
            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed in decrypting content: " + e.getClass().toString() + " : " + e.getMessage());
                return;
            }
        }

        // non - User login
        if (!uri.equalsIgnoreCase(ApplicationConfig.URI_SIGNIN) && !uri.equalsIgnoreCase(ApplicationConfig.URI_SIGNUP)) {
            // Authenticate
            try {
                Integer userid = Integer.valueOf(URLDecoder.decode(req.getHeader(ApplicationConfig.HEADER_USER), ApplicationConfig.URL_CODE));
                String contentMD5 = payload.isEmpty() ? "" : clerk.calculateMD5(payload);
                String timeStamp = URLDecoder.decode(req.getHeader(ApplicationConfig.HEADER_TIMESTAMP), ApplicationConfig.URL_CODE);
                String signature = URLDecoder.decode(req.getHeader(ApplicationConfig.HEADER_SIGN), ApplicationConfig.URL_CODE);
                String raw = uri + ApplicationConfig.SIGN_DELIMETR + userid + ApplicationConfig.SIGN_DELIMETR
                        + method + ApplicationConfig.SIGN_DELIMETR + contentMD5 + ApplicationConfig.SIGN_DELIMETR + timeStamp;
                if (!clerk.authenticate(userid, raw, signature)) {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthenticated request.");
                    return;
                }
            } catch (Exception e) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "Failed in authenticating : " + e.getClass().toString() + " : " + e.getMessage());
                return;
            }
        }

        // Forward
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        chain.doFilter(new SecureRequestWrapper(req, payload), new SecureResponseWrapper(res, outStream));

        result = outStream.toString();
        
        // Encrypt results
        try {
            result = clerk.Encrypt(result, symKey);
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Encryption response content : " + e.getClass().toString() + " : " + e.getMessage());
        }
        writer.write(result);
        writer.flush();
    }

    private String getResquestPayload(HttpServletRequest request) throws Exception {
        ServletInputStream inputStream = request.getInputStream();
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return sb.toString();
    }

    @Override
    public void destroy() {

    }

}
