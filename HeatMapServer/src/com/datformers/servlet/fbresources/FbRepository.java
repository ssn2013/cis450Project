package com.datformers.servlet.fbresources;

import com.datformers.database.OracleDBWrapper;
import com.datformers.utils.DatabaseUtil;
import com.restfb.types.User;

public class FbRepository {
	
	OracleDBWrapper wrapper;
	
	public FbRepository() {
		wrapper = new OracleDBWrapper(DatabaseUtil.getURL(DatabaseUtil.IP), DatabaseUtil.UERNAME, DatabaseUtil.PASSWORD);
	}
	
	public void destroyConnection () {
		wrapper.closeConnection();
	}

	public void enterInformation(FbUser me) {
		
	}

	public User getInformation(String appUserId, boolean FbUserId) {
		return null;
	}

}
