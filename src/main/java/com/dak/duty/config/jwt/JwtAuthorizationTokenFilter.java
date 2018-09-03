package com.dak.duty.config.jwt;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dak.duty.repository.PersonRepository;
import com.dak.duty.security.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {

	private static final String TOKEN_HEADER = "Authorization";
	
	private final String signingKey;
	private final PersonRepository personRepos;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.trace("doFilterInternal for '{}'", request.getRequestURL());

		final String requestHeader = request.getHeader(TOKEN_HEADER);

        Claims claims = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {
            final String authToken = requestHeader.substring(7);
            try {
            	Jws<Claims> claimsWrapper = Jwts.parser()
    				.setSigningKey(signingKey.getBytes(Charset.forName("UTF-8")))
    				.parseClaimsJws(authToken);
            	
    			claims = claimsWrapper.getBody();
            	
            } catch (SignatureException se) {
                logger.error("Invalid Token!", se);
            } catch (ExpiredJwtException e) {
                logger.warn("the token is expired and not valid anymore", e);
            }
        } else {
            logger.trace("couldn't find bearer string, will ignore the header");
        }

        if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.trace("security context was null, so authorizating user");

            final UserDetails userDetails = new CustomUserDetails(JwtUser.fromClaims(claims, personRepos));
            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(userDetails);
            log.trace("authorizated user '{}', setting security context", userDetails);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        
        }

        filterChain.doFilter(request, response);
	}

}
