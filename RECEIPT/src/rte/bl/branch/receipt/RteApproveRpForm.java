package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import java.util.*;
import rte.bl.branch.*;
import utility.rteutility.*;
import utility.cfile.CFile;
import utility.support.DateInfo;
public class RteApproveRpForm  implements Task
{
  	String branch ;
  	String userID ;
	String printDate ;
	String typeOfForm ;
	Mrecord crec;
	Mrecord urec ;
	public Result execute(Object param)
	{
		if(! (param instanceof Object  []))
 			return new Result("Invalid Parameter  : Object [] {branch,userID,printDate,String typeOfPol,Vector }",-1);
 		Object [] parameter = (Object []) param;
  		branch = (String)parameter[0];
  		userID = (String)parameter[1];
		printDate = (String)parameter[2];
		if(((String)parameter[3]).charAt(0) == 'O')
			typeOfForm = "02";
		else if(((String)parameter[3]).charAt(0) == 'I')
			typeOfForm = "01";
		else if (((String)parameter[3]).charAt(0) == 'B')

			typeOfForm = "17";
		else if (((String)parameter[3]).charAt(0) == 'b')
			 typeOfForm = "22";
		else if (((String)parameter[3]).charAt(0) == 'T')
			 typeOfForm = "03";
		Vector vform = (Vector)parameter[4];
		String blank ="";
		if(branch.length() == 3)
			blank = " ";
		try {
			crec = CFile.opens("cancelform@insuredocument");
			urec = CFile.opens("usedreceiptform@cbranch");			
			for (int i = 0 ; i < vform.size();i++)
			{
				String [] str = (String [])vform.elementAt(i);
				if("CR".indexOf(str[0]) >= 0)
					updateDataCancelForm(str,blank);
			}
		}
		catch (Exception e)
		{
			if (e == null || e.getMessage() == null)
				return new Result(M.stou("มีข้อมูลบางอย่างเป็น null"),1);
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
 	}
	private void updateDataCancelForm(String [] rp,String blank)
	{
		if(rp[3].charAt(0) == 'D')
		{
			if(crec.equal(branch+blank+typeOfForm+printDate+rp[1]))
			{
				System.out.println("DELETE -- "+branch+blank+typeOfForm+printDate+rp[1]);
				crec.delete();
			}
			return ;
		}
		System.out.println(rp[0]+"---"+rp[1]+"----"+rp[2]+"-----"+rp[3]);
		crec.set("deptCode",branch);
		crec.set("docCode",typeOfForm);
		crec.set("cancelDate",printDate);
		crec.set("status",rp[0]);
		crec.set("startNo",rp[1]);
		crec.set("lastNo",rp[2]);
		crec.set("approvedUser",userID);
		crec.set("approvedDate",DateInfo.sysDate());
		crec.set("userID",userID);
		Record trec = crec.copy();	
		if(rp[3].charAt(0) == 'N')
		{
			if (!crec.insert())
				System.out.println("insert cancel form  error "+crec.lastError());
		}
	/*	else if(rp[3].charAt(0) == 'O') 
		{
			if(crec.equal(branch+blank+typeOfForm+printDate+rp[1]))
			{
				trec.set("userID",crec.get("userID"));
				trec.set("cancelDate",crec.get("cancelDate"));
				crec.putBytes(trec.getBytes());
				System.out.println("trec="+new String trec.getBytes
				if (!crec.update())
					System.out.println("update cancel form  error "+crec.lastError());
					
			}
		}*/	
	}
}
