package rte.bl.collection.datarequest;

import layout.bean.mstperson.PersonBean;
import layout.bean.mstperson.PersonRecord;
import manit.Record;
import manit.rte.UserInfo;
import rte.bl.dataaccess.PrimaryKey;
import rte.bl.dataaccess.mstperson.PersonDAO;

public class SalesStructData {
	public Record getSalesPersonByBranchDepositNo(UserInfo user, String branch, String depositNo) throws Exception
	{
		PersonDAO mPerson = new PersonDAO(user);
		PersonBean bean;
		PrimaryKey pk = new PrimaryKey();
		pk.setKeyNumber(2);
		pk.addKey("branchCode", branch);
		pk.addKey("depositNo", depositNo);
		bean = (PersonBean) mPerson.equal(pk);
		
		return PersonRecord.getRecord(bean);
	}

}
