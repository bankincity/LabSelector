package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import insure.Insure;
import java.util.*;
import utility.rteutility.PublicRte;
public class RteInsertNewReceipt implements Task
{
	String 	policyType;
				
	public Result execute(Object param)
	{
		if(!(param instanceof Object []))
		   return new Result("Invalid Parameter:Object [] {policyType,byte [] buffer}",-1);
		Object [] parameter = (Object []) param;
		
		policyType = (String)parameter[0];
		String payDate  = (String)parameter[1];
		byte [] buffer = (byte [])parameter[2];
		try {
			String filename = policyType.toLowerCase()+"rctrl"+payDate.substring(0,4)+"@receipt";
			if(!CFile.isFileExist(filename))
				filename = policyType.toLowerCase()+"rctrl@receipt";

			Mrecord rec = CFile.opens(filename);
			rec.putBytes(buffer);
			if(!rec.insert())
				throw new Exception("Insert "+filename+" error == "+M.itoc(rec.lastError()));

		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
}
