package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;

public class SearchIOWByPolicy implements Task
{
	BraSales bs;
 	Mrecord irc;
 	Mrecord orc;
 	Mrecord wrc;
	Mrecord forc; 
	Mrecord firc; 
	Mrecord fwrc; 
 	String branch;
 	String yyyy;
 	String pol;
 	String data = "";
	TempMasicFile temp;
	int[] len = { 12,8,6,9,1,4,1};
	String[] field = {"rpNo","policyNo","payPeriod","premium","type","year","currentStatus"};

 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {branch,rpNo}",-1);
 		Object [] parameter = (Object []) param;
 		branch = (String)parameter[0];
 		pol = (String)parameter[1];
 		try 
 		{
 			irc = CFile.opens("irctrl@receipt");
 			orc = CFile.opens("orctrl@receipt");
 			wrc = CFile.opens("wrctrl@receipt");

 			bs= new BraSales();
 			temp = new TempMasicFile("bra"+branch+"app",field,len);
 			temp.keyField(true,false,new String [] {"type","year","rpNo"});
 			temp.buildTemp();
			data = searchData(irc,pol,"0000","I");
	//		if(data.trim().length() == 0)
	//		{
 				yyyy = DateInfo.sysDate().substring(0,4);
				while(CFile.isFileExist("irctrl"+yyyy+"@receipt"))
				{
					firc = CFile.opens("irctrl"+yyyy+"@receipt");
					data = searchData(firc,pol,yyyy,"I");
					yyyy = M.dec(yyyy);
				}

	//		}
			data = searchData(orc,pol,"0000","O");
	//		if(data.trim().length() == 0)
	//		{
 				yyyy = DateInfo.sysDate().substring(0,4);
				while(CFile.isFileExist("orctrl"+yyyy+"@receipt"))
				{
					forc = CFile.opens("orctrl"+yyyy+"@receipt");
					data = searchData(forc,pol,yyyy,"O");
					yyyy = M.dec(yyyy);
				}

	//		}
			data = searchData(wrc,pol,"0000","W");
	//		if(data.trim().length() == 0)
	//		{
 				yyyy = DateInfo.sysDate().substring(0,4);
				while(CFile.isFileExist("wrctrl"+yyyy+"@receipt"))
				{
					fwrc = CFile.opens("wrctrl"+yyyy+"@receipt");
					data = searchData(fwrc,pol,yyyy,"W");
					yyyy = M.dec(yyyy);
				}

	//		}
 			irc = CFile.opens("circtrl@receipt");
 			orc = CFile.opens("corctrl@receipt");
			data = searchData(orc,pol,"c000","O");
			data = searchData(irc,pol,"c000","I");

 			yyyy = DateInfo.sysDate().substring(0,4);
			while(CFile.isFileExist("circtrl"+yyyy+"@receipt"))
			{
				firc = CFile.opens("circtrl"+yyyy+"@receipt");
				data = searchData(firc,pol,"c0"+yyyy.substring(2,4),"I");
				yyyy = M.dec(yyyy);
			}
 			yyyy = DateInfo.sysDate().substring(0,4);
			while(CFile.isFileExist("corctrl"+yyyy+"@receipt"))
			{
				forc = CFile.opens("corctrl"+yyyy+"@receipt");
				data = searchData(forc,pol,"c0"+yyyy.substring(2,4),"O");
				yyyy = M.dec(yyyy);
			}

			return new Result(temp.name(),0);

 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
 	}
	private String searchData(Mrecord f,String pol,String year,String type) throws Exception
	{	
			f.start(1);
			for(boolean ok=f.equalGreat(pol);
						ok && f.get("policyNo").compareTo(pol) == 0 ; ok=f.next())
			{
			System.out.println("-SearchIOWByPolicy---infor--rp-----"+f.get("rpNo")+"--pol-----"+f.get("policyNo")+"--year--"+year);
				temp.set("rpNo",f.get("rpNo"));
				temp.set("policyNo",f.get("policyNo"));
				temp.set("payPeriod",f.get("payPeriod"));
				temp.set("premium",f.get("premium"));
				temp.set("type",type);
				temp.set("year",year);
				temp.set("currentStatus",f.get("currentStatus"));
        		temp.insert();
			}
			if(temp.fileSize() == 0)
				return "";
			else
				return "1";
				
	}
}


