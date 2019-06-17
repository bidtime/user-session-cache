package org.bidtime.session.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class IPListRangeUtils {
	
	private static final Logger logger = Logger
			.getLogger(IPListRangeUtils.class);
	
	private volatile static IPListRangeUtils instance;

	public static IPListRangeUtils getInstance() {
		if (instance == null) {
			synchronized (IPListRangeUtils.class) {
				if (instance == null) {
					instance = new IPListRangeUtils();
				}
			}
		}
		return instance;
	}
	
	List<IPRangeUtils> list;
	
	public static void init(String ips) {
		getInstance().initial(ips);
	}

	protected void initial(String ips) {
		if (ips != null && !ips.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("initial:" + ips);
			}
			String[] lIPs = ips.split(";");
			if (lIPs != null) {
				list = new ArrayList<IPRangeUtils>();
				for (int i=0; i<lIPs.length; i++) {
					String s = lIPs[i].trim();
					if (logger.isDebugEnabled()) {
						logger.debug(i+":" + s);
					}
					IPRangeUtils u = new IPRangeUtils(s);
					list.add(u);
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("initial: null");
			}
		}
	}
	
	protected boolean ipExists(String ip) {
		if (ip == null || ip.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("ipExists: ip is null");
			}
			return false;
		} else if (list == null || list.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("ipExists: list is null " + ip);
			}
			return false;
		} else {
			boolean bExists = false;
			for (int i=0; i<list.size(); i++) {
				IPRangeUtils u = list.get(i);
				if (u.ipExists(ip)) {
					bExists = true;
					break;
				}
			}
			return bExists;
		}
	}
	
	public static boolean isExists(String ip) {
		return getInstance().ipExists(ip);
	}

}