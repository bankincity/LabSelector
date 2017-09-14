package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteInsertReceiptT implements Task
{
	Mrecord fxrp;
	Mrecord tmp;
	String userID;
	String currentStatus;
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {tmp,userID,currentStatus}",-1);
 		Object [] parameter = (Object []) param;
 		tmp = (Mrecord)parameter[0];
 		userID = (String)parameter[1];
 		currentStatus = (String)parameter[2];
 		try
 		{
 			fxrp = CFile.opens("xrpchg@cbranch");
 			boolean res = insertData(tmp);
 			return new Result(new Boolean(res),0);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
 	}
	private boolean insertData(Mrecord tmp) throws Exception
 	{
 		boolean a = false;
		for(boolean st = tmp.first();st;st=tmp.next())
		{
		System.out.println("tmp.get(rpNo)"+tmp.get("rpNo"));
		System.out.println("tmp.get(policy)"+tmp.get("policyNo"));
		System.out.println("SYSTIME"+M.systime());
		//	fxrp.set("sysDate",tmp.get("statusDate"));      
			fxrp.set("sysDate",M.sysdate());      
          	fxrp.set("rpNo",tmp.get("rpNo"));     
          	fxrp.set("policyNo",tmp.get("policyNo"));     
			fxrp.set("premium",tmp.get("premium"));            
			fxrp.set("newStatus",tmp.get("currentStatus"));            
			fxrp.set("oldStatus",currentStatus);            
			fxrp.set("payPeriod","000000");            
			fxrp.set("sysTime",M.systime());            
			fxrp.set("typeOfReceipt","T");            
			fxrp.set("userID",userID);            
		}	
 			a = fxrp.insert();
		System.out.println("A"+a);
 		return (a);
 	}
}

