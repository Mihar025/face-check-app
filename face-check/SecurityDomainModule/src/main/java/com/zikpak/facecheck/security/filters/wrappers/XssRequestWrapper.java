package com.zikpak.facecheck.security.filters.wrappers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.Pattern;


public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final Pattern[] patterns;

    public XssRequestWrapper(HttpServletRequest request, Pattern[] patterns) {
        super(request);
        this.patterns = patterns;
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodeValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodeValues[i] = stripXss(values[i]);
        }
        return encodeValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return stripXss(value);
    }


    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
       return stripXss(value);
    }

    private String stripXss(String value) {
        if(value == null) {
            return null;
        }

        String result = value;
        for(Pattern pattern : patterns) {
            result = pattern.matcher(result).replaceAll("");
        }
        result = StringEscapeUtils.escapeHtml4(result);
        return result;
    }


}
