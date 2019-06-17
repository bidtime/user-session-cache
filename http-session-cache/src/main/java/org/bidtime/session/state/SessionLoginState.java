package org.bidtime.session.state;

import org.bidtime.session.bean.ISessionUser;

public class SessionLoginState {
	
	ISessionUser sessionUser;
	
	public ISessionUser getSessionUser() {
		return sessionUser;
	}

	public void setSessionUser(ISessionUser sessionUser) {
		this.sessionUser = sessionUser;
	}

	private StateEnum loginState;

	public StateEnum getLoginState() {
		return loginState;
	}

	public void setLoginState(StateEnum loginState) {
		this.loginState = loginState;
	}
	
	public SessionLoginState(ISessionUser sessionUser, StateEnum state) {
		this.sessionUser = sessionUser;
		this.loginState = state;
	}
	
}
