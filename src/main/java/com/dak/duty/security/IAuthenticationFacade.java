package com.dak.duty.security;

import org.springframework.security.core.Authentication;

public interface IAuthenticationFacade {
   Authentication getAuthentication();
}
