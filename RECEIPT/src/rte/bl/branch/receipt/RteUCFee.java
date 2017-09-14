package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import rte.bl.branch.TempMasicFile;
import rte.*;
import java.util.*;
public class RteUCFee  implements Task
{
	UCRPYYYYMM ucrp;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {action,yyyymm}",-1);
		Object [] parameter = (Object []) param;
		String remote = (String)parameter[0];
		String action  = (String)parameter[1];
		String yyyymm = (String)parameter[2];
		String branch = (String)parameter[3];
		String strID = (String)parameter[4];
		Vector vec = new Vector();
		try {
			ucrp = new UCRPYYYYMM();
			System.out.println("action============="+action+"  "+yyyymm);
			if(action.charAt(0) == 'I')
			{
				String fee = M.undot((String)parameter[5]);
				if (parameter.length != 8)
					fee = fee.substring(0,fee.length() -2);
				String rpNo = (String)parameter[6];
				String clearDate = DateInfo.sysDate();
				if (parameter.length == 8)
					clearDate = (String)parameter[7];
				ucrp.insertUCFee(branch,yyyymm,strID,fee,rpNo,clearDate);
			}
			else if(action.charAt(0) == 'D')
			{
				String rpNo = (String)parameter[5];
				ucrp.deleteUCFee(branch,yyyymm,strID,rpNo);
			}
			else {
				vec = ucrp.getUCFeeClearing(branch,strID,yyyymm);
			}
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result(vec ,0);
	}	
}
