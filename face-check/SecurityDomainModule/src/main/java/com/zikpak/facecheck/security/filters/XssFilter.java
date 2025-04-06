package com.zikpak.facecheck.security.filters;

import com.zikpak.facecheck.security.filters.wrappers.XssRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Order(1)
public class XssFilter extends OncePerRequestFilter {

    private static final Pattern[] XSS_PATTERNS = {
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
            Pattern.compile("onerror(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };



    @Override
    protected void doFilterInternal(    HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                                        throws ServletException, IOException {
        XssRequestWrapper wrapper = new XssRequestWrapper(request, XSS_PATTERNS);

        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("X-Content-Type-Options", "nosniff");

        filterChain.doFilter(request, response);


    }
}
