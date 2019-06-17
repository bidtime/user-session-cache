package org.bidtime.session.state;

import org.bidtime.session.bean.ISessionUser;

public class SessionState {
	
	ISessionUser sessionUser;
	
	public ISessionUser getSessionUser() {
		return sessionUser;
	}

	public void setSessionUser(ISessionUser sessionUser) {
		this.sessionUser = sessionUser;
	}

	private int loginState;

	public int getLoginState() {
		return loginState;
	}

	public void setLoginState(int loginState) {
		this.loginState = loginState;
	}
	
	public SessionState(ISessionUser sessionUser, int state) {
		this.sessionUser = sessionUser;
		this.loginState = state;
	}
	
}
