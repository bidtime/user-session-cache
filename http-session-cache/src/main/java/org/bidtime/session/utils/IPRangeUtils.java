package org.bidtime.session.utils;

import org.apache.log4j.Logger;

public class IPRangeUtils {
	
	private static final Logger logger = Logger
			.getLogger(IPRangeUtils.class);
	
	String sBeginIP;
	String sEndIP;

	long beginIP=0;
	long endIP=0;

	public IPRangeUtils(String ipSection) {
		if (ipSection != null && !ipSection.isEmpty()) {
			int idx = ipSection.indexOf('-');
			String IP1 = ipSection.substring(0, idx);
			String IP2 = ipSection.substring(idx + 1);
			this.sBeginIP = IP1;
			this.sEndIP = IP2;
			if (logger.isDebugEnabled()) {
				logger.debug("IPRangeUtils:" + 
					IP1 + ":" + IP2 + "(" + ipSection + ")");
			}
			this.beginIP = IPUtils.getIp2long(IP1);
			this.endIP = IPUtils.getIp2long(IP2);
			if (logger.isDebugEnabled()) {
				logger.debug("create: ips " + this.sBeginIP + this.sEndIP);
			}
		} else {
			sBeginIP = null;
			sEndIP = null;
			if (logger.isDebugEnabled()) {
				logger.debug("create: ips is null");
			}
		}
	}

	public boolean ipExists(String ip) {
		if (sBeginIP==null && sEndIP==null) {
			if (logger.isDebugEnabled()) {
				logger.debug("ipExists:ips is null");
			}
			return false;
		} else {
			long lIP = IPUtils.getIp2long(ip);
			boolean bExists = beginIP <= lIP
					&& lIP <= endIP;
			if (logger.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("ipExists:");
				sb.append(bExists);
				sb.append(" ");
				sb.append(lIP);
				sb.append("(");
				sb.append(sBeginIP);
				sb.append("-");
				sb.append(sEndIP);
				sb.append(")");
				sb.append(" long:");
				sb.append(lIP);
				sb.append("(");
				sb.append(beginIP);
				sb.append("-");
				sb.append(endIP);
				sb.append(")");
			}
			return bExists;
		}
	}
	
}