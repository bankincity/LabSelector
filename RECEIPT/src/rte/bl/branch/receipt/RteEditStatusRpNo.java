package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import utility.support.*;
import java.util.*;
import utility.rteutility.*;
import utility.cfile.*;

public class RteEditStatusRpNo  implements  Task
{
	Mrecord rp;
	public Result execute(Object o)
	{
		if(!(o instanceof String []))
			return new Result(M.stou("Invalid Parameter :"),-1);
		String [] parameter = (String [])o;
		String type = (String)parameter[0];
 		String rpNo = (String)parameter[1];

		String[] str = new String[11];
		Vector v = new Vector();
		try
		{
			switch (type.charAt(0))
 			{
 				case 'I' :
 				System.out.println("----------case I------>");
 					rp = CFile.opens("irctrl@receipt");
 					break;
 				case 'O' :
 				System.out.println("-----------case O------>");
 					rp = CFile.opens("orctrl@receipt"); 
					break;
 				case 'W' :
 				System.out.println("-----------case W------>");
 					rp = CFile.opens("wrctrl@receipt");
 					break;
 				default  :;
 			}
			if(rp.equal(rpNo))
			{
				str[0] = rp.get("policyNo");
				str[1] = rp.get("payPeriod");
				str[2] = rp.get("premium");
				str[3] = rp.get("currentStatus");
				str[4] = rp.get("sysDate");
				str[5] = rp.get("payDate");
				str[6] = rp.get("effectiveDate");
				str[7] = rp.get("submitBranch");
				str[8] = rp.get("submitNo");
				str[9] = rp.get("requestDate");
				str[10] = rp.get("reasonCode");
				v.addElement(str);
			}
			return (new Result(v,0));
		}
		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
		}
	}
}
