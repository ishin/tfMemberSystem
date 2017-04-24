package com.sealtalk.action.filter;
import java.io.IOException;  
import java.util.Locale;

import javax.servlet.Filter;  
import javax.servlet.FilterChain;  
import javax.servlet.FilterConfig;  
import javax.servlet.ServletException;  
import javax.servlet.ServletRequest;  
import javax.servlet.ServletResponse;  
import javax.servlet.http.HttpServletResponse;  
  

public class ContentTypeFilter implements Filter {  
  
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {  
        String contentType = req.getContentType().toLowerCase(Locale.ENGLISH);
       // if (contentType != null && contentType.contains("multipart/form-data")) {
        //	res.getWriter().write("Reject!");
        //} else {
        	chain.doFilter(req, res);  
       // }
    }  
  
    public void init(FilterConfig filterConfig) {}  
  
    public void destroy() {}  
  
}  