package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;
public class RteLoadBaddep implements Task
{
	Mrecord fbaddep;
	BraSales bs;
	String branch;	
	TempMasicFile temp;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {branch}",-1);
 		Object [] parameter = (Object []) param;
  		branch = (String)parameter[0];
    	try 
		{
			fbaddep = CFile.opens("baddep@cbranch");
			bs = new BraSales();
			temp = new TempMasicFile("rptbranch",fbaddep.layout());
			temp.keyField(false,false,new String [] {"branch","depositNo"});
			temp.buildTemp();
			loadDataBadDep(branch);
			return new Result(temp.name(),0);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
    	}
	}
	private void  loadDataBadDep(String branch) throws Exception
	{
 		for(boolean ok=fbaddep.equalGreat(branch); ok; ok=fbaddep.next())
 		{
			if(branch.compareTo(fbaddep.get("branch")) != 0)
				break;
			if(fbaddep.get("name").trim().length() == 0)
			{
				 try {
				 	bs.getByDepositNo(branch, fbaddep.get("depositNo").trim());
                                 	fbaddep.set("name", bs.getSnRec("preName").trim()+bs.getSnRec("firstName").trim()+" "+bs.getSnRec("lastName").trim());
				 }
				 catch (Exception e)
				 {
					fbaddep.set("name",M.stou("ไม่พบฝ่ายขายนี้"));
				 }
				 fbaddep.update();
			}
 			temp.putBytes(fbaddep.copy().getBytes());		
			temp.insert();
 		}
 	}
}
