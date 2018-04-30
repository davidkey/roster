package com.dak.duty.tags;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.jasper.tagplugins.jstl.Util;

public class FormatDateTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	protected Temporal value;
	protected String pattern;
	private String var;
	private int scope;

	public FormatDateTag() {
		super();
		init();
	}

	private void init() {

		this.pattern = this.var = null;
		this.value = null;
		this.scope = PageContext.PAGE_SCOPE;
	}

	public void setVar(final String var) {
		this.var = var;
	}

	public void setScope(final String scope) {
		this.scope = Util.getScope(scope);
	}

	public void setValue(final Temporal value) {
		this.value = value;
	}

	public void setPattern(final String pattern) {
		this.pattern = pattern;
	}

	@Override
	public int doEndTag() throws JspException {

		String formatted = null;

		if (this.value == null) {
			if (this.var != null) {
				this.pageContext.removeAttribute(this.var, this.scope);
			}
			return EVAL_PAGE;
		}

		// Create formatter
		if (this.pattern != null) {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(this.pattern);
			formatted = formatter.format(this.value);
		} else {
			// no formatting locale available, use Date.toString()
			formatted = this.value.toString();
		}

		if (this.var != null) {
			this.pageContext.setAttribute(this.var, formatted, this.scope);
		} else {
			try {
				this.pageContext.getOut().print(formatted);
			} catch (final IOException ioe) {
				throw new JspTagException(ioe.toString(), ioe);
			}
		}

		return EVAL_PAGE;
	}

	@Override
	public void release() {
		init();
	}

}
