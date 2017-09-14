package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
public class RteCancelOverride implements Task
{
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {month,strID}",-1);
 		Object [] parameter = (Object []) param;

  		String month  = (String)parameter[0];
  		String strID = (String)parameter[1];
		
    		try 
		{
                        Mrecord uc = CFile.opens("uc"+month+"@cbranch");
                        if (uc.equal(strID))
                        {
                                
                                rte.bl.branch.income.BlOverrideDenied cover = new rte.bl.branch.income.BlOverrideDenied();
                                cover.manageOverrideDenied(uc.get("salesID"),month);


                        }
                        else 
                            throw new Exception(M.stou("ไม่พบข้อมูลขาดค้าง "+strID +" ในแฟ้มขาดค้าง ")+month.substring(4)+"/"+month.substring(0,4));
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
    		}
                return new Result("",0);
	}
}
