package com.dak.duty.security;

import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * This class exists to tell Spring to use spel on jpa repositories. See:
 * http://docs.spring.io/autorepo/docs/spring-security/4.0.x/reference/html/data-configuration.html
 * @author David
 *
 */
public class SecurityEvaluationContextExtension extends EvaluationContextExtensionSupport {

	@Override
	public String getExtensionId() {
		return "security";
	}

	@Override
	public SecurityExpressionRoot getRootObject() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return new SecurityExpressionRoot(authentication) {
		};
	}

}
