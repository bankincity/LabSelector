package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteSearchPolicyr implements Task
{
	Mrecord fpol;
 	String yyyymm;  
	String branch;
 	public Result execute(Object param)
	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {branch,yyyymm}",-1);
 		Object [] parameter = (Object []) param;
 		branch = (String)parameter[0];
 		yyyymm = (String)parameter[1];
 		try 
 		{
 			fpol = CFile.opens("policy_r@cbranch");
 			Result res = searchData(yyyymm);
 			return (res);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
	}
  	private Result searchData(String yyyymm) throws Exception
	{
 		Vector v = new Vector();
 		for(boolean ok=fpol.equalGreat(branch+yyyymm); ok; ok=fpol.next())
 		{
			if(fpol.get("month").equals(yyyymm) && branch.compareTo(fpol.get("ownerBranch")) == 0)
 				v.addElement(fpol.copy().getBytes());
			else 
				break;
 		}
 		return new Result(v,0);
 	}
 }

