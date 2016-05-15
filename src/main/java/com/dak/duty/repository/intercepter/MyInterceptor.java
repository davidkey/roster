package com.dak.duty.repository.intercepter;

import org.hibernate.EmptyInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyInterceptor extends EmptyInterceptor {
	private static final long serialVersionUID = -505669418039984304L;
	private static final Logger logger = LoggerFactory.getLogger(MyInterceptor.class);

	@Override
	public String onPrepareStatement(final String sql) {
		logger.debug("onPrepareStatement: {}", sql);
		return sql;
	}
}
