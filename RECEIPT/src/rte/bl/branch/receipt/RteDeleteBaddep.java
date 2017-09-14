package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteDeleteBaddep implements Task
{
	Mrecord fbaddep;
 	String branch;
	String depositNo;
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {branch,depositNo}",-1);
 		Object [] parameter = (Object []) param;
 		branch = (String)parameter[0];
 		depositNo = (String)parameter[1];
 		try
 		{
 			fbaddep = CFile.opens("baddep@cbranch");
 			boolean res = DelDataBadDep(branch,depositNo);
 			return new Result(new Boolean(res),0);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
 	}
 	private boolean DelDataBadDep(String branch, String depositNo) throws Exception
 	{   
        boolean a = false;
		  System.out.println("11111"+branch+depositNo);

		if(fbaddep.equal(branch+depositNo))
		{
		  System.out.println("22222");
            a = fbaddep.delete();
		  System.out.println("33333"+a);
		}
 		return (a);
 	}
}
                                                                               
