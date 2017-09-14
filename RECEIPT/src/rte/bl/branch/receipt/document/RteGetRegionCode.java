package rte.bl.branch.receipt.document; 
import manit.*;
import rte.*;
import manit.rte.*; 
import manit.rte.Task; 
import java.io.*;
import utility.cfile.*;
import utility.support.*; 
import java.util.Vector;
import layout.branch.*; 
import manit.rte.*;
import utility.rteutility.*;

public class RteGetRegionCode implements Task
{
	public Result execute(Object param)
	{
/*		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : String []", -1);
*/
		String branchCode = param.toString().trim();
		try
		{		
			System.out.println("  ============= RteGetRegionCode =============== " ); 
			return new Result( getRegionCode(branchCode), 0); 
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),2);
		}
	}

	public String getRegionCode(String branchCode) throws Exception
	{
		String regionCode = ""; 
		Mrecord fpb = CFile.opens("portbook@cunderwrite"); 
		if (fpb.equal(branchCode))
			regionCode = fpb.get("regionCode");
		return regionCode; 		
	}	
}
