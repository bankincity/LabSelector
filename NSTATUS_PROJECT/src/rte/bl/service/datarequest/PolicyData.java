package rte.bl.service.datarequest;

import rte.bl.dataaccess.PrimaryKey;
import layout.bean.mstperson.PersonBean;
import layout.bean.mstperson.PersonRecord;
import manit.rte.UserInfo;

public class PolicyData {
	public static void updateIDNoToPerson(String idNo,String personID,UserInfo user) throws Exception
	{
		PrimaryKey pk = new PrimaryKey();
		pk.addKey("personID", personID);
		rte.bl.dataaccess.mstperson.PersonDAO person = new rte.bl.dataaccess.mstperson.PersonDAO(user);
		PersonBean old = (PersonBean)person.equal(pk);
		PersonBean newbean = PersonRecord.getPersonBean(PersonRecord.getRecord(old));
		newbean.setReferenceID(idNo);
		newbean.setReferenceType("01");
		person.update(old, newbean, pk);
		
	}

}
