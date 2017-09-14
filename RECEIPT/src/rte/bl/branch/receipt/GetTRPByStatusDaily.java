package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import java.util.*;
import rte.*;
import utility.rteutility.*;
import utility.cfile.Rtemp;
import utility.prename.Prename;
import utility.support.DateInfo;
import rte.bl.branch.*;
public class  GetTRPByStatusDaily  implements  InterfaceRpt
{
	Mrecord trec;
	public void makeReport(String [] args) throws Exception
    {
		System.out.println("args === "+args.length);
		for (int i = 0 ; i < args.length;i++)
		{
			System.out.println("args "+i+" ----  "+args[i]);
		}
		String remote	= args[0];  // true or false 
		if(remote.compareTo("false") == 0)
			PublicRte.setRemote(false);
		else
			PublicRte.setRemote(true);
        String appserv = args[1];  // application server  <--> braxxxapp
        String filename = args[2]; // masic output stream  

		trec = CFile.opens("trpctrl@receipt");
		
		temptoprint = new TempMasicFile("rptinsure",field,len);
		temptoprint.keyField(false,false,new String [] {"status","rpNo"});
                temptoprint.buildTemp();

		//   branch sdate status
		String branch = args[3];
		String period = args[4];
		String status = args[5];
		
		for (int i = 0 ; i < status.length();i++)
		{
			String tstatus = status.substring(i,i+1);
			System.out.println("tstatus..........."+tstatus);
			getData(tstatus, period, branch);
		}

		System.out.println("End startSearch process! ");
		rte.RteRpt.recToTemp(temptoprint, filename);
                System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		temptoprint.close();
	}
	TempMasicFile  temptoprint;
	String [] field = {"status","rpNo","policyNo","name"};
    int []  len = {1,12,8,80};

	/*--------------------------------------------------------------*/
	private void getData( String status, String sdate, String branch)
	{
		trec.start(4);
System.out.println("key search : "+status+sdate+branch);
		for (boolean st = trec.equalGreat(status+sdate+branch); st ;st = trec.next())
		{
			if((status+sdate+branch).compareTo(trec.get("currentStatus")+trec.get("statusDate")+trec.get("branch").substring(0,3)) != 0)
				break;
			temptoprint.set(' ');
			System.out.println( " Aoil --------trec.currentStatus : trec.rpNo :" + trec.get("rpNo") ); 
			if("CZ".indexOf(trec.get("currentStatus"))>=0)
				temptoprint.set("status","C");
			else	
				temptoprint.set("status",trec.get("currentStatus"));
			temptoprint.set("rpNo",trec.get("rpNo"));
			temptoprint.set("policyNo",trec.get("policyNo"));
			if("HQX".indexOf(temptoprint.get("status")) >=0 )
				temptoprint.set("name",getName(trec.get("policyNo")));
		
			temptoprint.insert();	
		}
		System.out.println("temp to print---"+temptoprint.fileSize());
	}
	private String getName(String policyNo)
	{
		try {
			Mrecord master = null;
			master = CFile.opens("ordmast@mstpolicy");
			if(!master.equal(policyNo))
			{
				master = CFile.opens("whlmast@mstpolicy");
				if(!master.equal(policyNo))
				{
					master = CFile.opens("indmast@mstpolicy");
					if(!master.equal(policyNo))
					{
						master = null;
					}
				}
			}
        	        if(master == null)
			return "";
			Mrecord nameMaster = CFile.opens("name@mstperson");
       		if(nameMaster.equal(master.get("nameID")))
           		 return  (Prename.getAbb(nameMaster.get("preName").trim())+nameMaster.get("firstName").trim()+" "+nameMaster.get("lastName").trim());
		}
		catch(Exception e)
		{			
		}
       	return "";
	}
}

