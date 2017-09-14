package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteAddBaddep implements Task
{
	Mrecord fbaddep;
 	String branch;
 	String depositNo;
	String badCause;
	String name ;
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {branch,depositNo,badCause}",-1);
 		Object [] parameter = (Object []) param;
 		branch = (String)parameter[0];
		depositNo = (String)parameter[1];
		name = (String)parameter[2];
		badCause = (String)parameter[3];
 		try
 		{
 			fbaddep = CFile.opens("baddep@cbranch");
			if(fbaddep.equal(branch+depositNo))
			{
				return new Result(fbaddep.getBytes(),1);
			}
 			boolean res = AddDataBadDep(branch,depositNo,name,badCause);
 			return new Result(new Boolean(res),0);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
 	}
	private boolean AddDataBadDep(String branch, String depositNo,String name, String badCause) throws Exception
 	{
 		boolean a = false;
		System.out.println("BRANCH"+branch);
		System.out.println("dep"+depositNo);
			fbaddep.set("branch",branch);      
          	fbaddep.set("depositNo",depositNo);     
          	fbaddep.set("name",name);     
			fbaddep.set("badCause", badCause);           
		 
 			a = fbaddep.insert();
		System.out.println("A"+a);
 		return (a);
 	}
}

