package com.zikpak.facecheck.security.filters;

import com.zikpak.facecheck.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Service
@RequiredArgsConstructor
@Order(3)
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtFilterService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

       // System.out.println("=== JWT FILTER START ===");
       // System.out.println("URL: " + request.getRequestURI());
      //  System.out.println("Method: " + request.getMethod());

        if (request.getServletPath().contains("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
      //  System.out.println("Auth header: " + authHeader);

        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        //    System.out.println("No valid auth header, passing through");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
       // System.out.println("JWT token: " + jwt.substring(0, Math.min(jwt.length(), 20)) + "...");

        try {
            userEmail = jwtFilterService.extractUsername(jwt);
          //  System.out.println("Extracted email: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
           //     System.out.println("User details loaded: " + userDetails.getUsername());
           //     System.out.println("User authorities: " + userDetails.getAuthorities());

                if (jwtFilterService.isTokenValid(jwt, userDetails)) {
              //      System.out.println("Token is valid!");
                    UsernamePasswordAuthenticationToken authToken
                            = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                //    System.out.println("Authentication set!");
                } else {
                //    System.out.println("Token is NOT valid!");
                }
            }
        } catch (Exception e) {
          //  System.out.println("Error in JWT filter: " + e.getMessage());
            e.printStackTrace();
        }

      //  System.out.println("=== JWT FILTER END ===");
        filterChain.doFilter(request, response);
    }
}