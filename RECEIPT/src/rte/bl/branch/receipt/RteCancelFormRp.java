package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;
public class RteCancelFormRp implements Task
{
	Mrecord fcancel;
//	BraSales bs;
	String branch;	
	String yyyymmdd;	
	String userID;	
	TempMasicFile temp;
	int [] len = {7,2,12,1};
	String [] field = {"userID","docCode","rpNo","status"};	
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {branch,yyyymmdd,userID}",-1);
 		Object [] parameter = (Object []) param;
  		branch = (String)parameter[0];
  		yyyymmdd = (String)parameter[1];
  		userID = (String)parameter[2];
    	try 
		{
			fcancel = CFile.opens("cancelform@insuredocument");
//			bs = new BraSales();
			temp = new TempMasicFile("rptbranch",field,len);
			temp.keyField(false,false,new String [] {"userID","docCode","rpNo","status"});
			temp.buildTemp();
			searchData(branch,yyyymmdd);
			return new Result(temp.name(),0);
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
    	}
	}
	private void  data(String key) throws Exception
	{
 		for(boolean ok=fcancel.equalGreat(key); ok; ok=fcancel.next())
 		{
			System.out.println("-------------------------------L1");
			if(key.compareTo(fcancel.get("deptCode")+" "+fcancel.get("docCode")+fcancel.get("cancelDate")) != 0)
				break;
			if(userID.compareTo("0000000")!= 0 && userID.compareTo(fcancel.get("userID")) != 0)
				continue;
			System.out.println("-------------------------------L2");
			temp.set("userID",fcancel.get("userID"));
			temp.set("docCode",fcancel.get("docCode"));
			temp.set("status",fcancel.get("status"));
			String start = fcancel.get("startNo").trim();
			String end = fcancel.get("lastNo").trim();
			while(start.compareTo(end) <= 0)
			{
				temp.set("rpNo",start);
				start = M.inc(start);
				temp.insert();
			}
 		}
	}
	private void  searchData(String branch, String yyyymmdd) throws Exception
	{
		String key = branch+" "+"01"+yyyymmdd;
		data(key);
		key = branch+" "+"02"+yyyymmdd;
		data(key);
		key = branch+" "+"03"+yyyymmdd;
		data(key);
		key = branch+" "+"17"+yyyymmdd;
		data(key);
		key = branch+" "+"18"+yyyymmdd;
		data(key);
		key = branch+" "+"22"+yyyymmdd;
		data(key);
 	}
}
