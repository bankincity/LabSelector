package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteFind_W_ReservedReceipt  implements Task
{
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {policyType,policyNo,reasonCode,userID}",-1);
		System.out.println("-----------------in RteFind_W_ReservedReceipt-------------");
		Object [] parameter = (Object []) param;
		System.out.println("bingo................");
		System.out.println("p [0] --"+parameter[0].getClass());
		String policyType = (String)parameter[0];
		
		String policyNo = (String)parameter[1];
		String reasonCode = (String)parameter[2];
		String userID	= (String)parameter[3];
		System.out.println("bingo.........................2");
		try{
			String filename ="";
              		switch (policyType.charAt(0))
             		{
                       		 case 'I' : filename = "irctrl@receipt"; break;
                      		 case 'O' : filename = "orctrl@receipt"; break;
                       		 case 'W' : filename = "wrctrl@receipt"; break;
                       		 case 'U' : filename = "ulrctrl@universal"; break;
                	}
			String sysDate = DateInfo.sysDate();
			String systime = Masic.time("commontable").substring(8);
			Mrecord rc = CFile.opens(filename);
			Mrecord  rcchg = CFile.opens("xrpchg@cbranch");

			rc.start(1);
			for (boolean st = rc.great(policyNo) ;st && policyNo.compareTo(rc.get("policyNo")) == 0;st=rc.next())
			{
				if(rc.get("currentStatus").charAt(0) == 'W' && rc.get("reasonCode").compareTo(reasonCode) == 0)
				{
					CheckAskReceipt.insertXrcchg(rcchg,policyType.charAt(0),rc,rc.get("originalStatus"),sysDate,systime,userID);
					rc.set("currentStatus",rc.get("originalStatus"));
					rc.set("sysDate",sysDate);
					rc.set("reasonCode","00");
					rc.update();

				}
				
			}
			checkReservedRp(policyType,policyNo);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),2);
		}
		return new Result("",0);
	}
	private void checkReservedRp(String policyType,String policyNo) throws Exception
	{
		String filename ="";
                switch (policyType.charAt(0))
                {
                        case 'I' : filename = "indmast@mstpolicy"; break;
                        case 'O' : filename = "ordmast@mstpolicy"; break;
                        case 'W' : filename = "whlmast@mstpolicy"; break;
                        case 'U' : filename = "universallife@universal"; break;
                }
		Mrecord mrec = CFile.opens(filename);
		if(mrec.equal(policyNo))
		{
	//		String nextDue = Insure.nextDueDate(mrec.get("mode"),mrec.get("effectiveDate"),lastedPayPeriod));

		}
	}
}
