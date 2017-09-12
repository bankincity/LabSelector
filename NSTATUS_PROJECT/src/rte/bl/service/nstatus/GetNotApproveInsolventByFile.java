package rte.bl.service.nstatus; 
import 	rte.*;
import  manit.*;
import  manit.rte.*;
import  utility.cfile.CFile;
import  utility.cfile.*; 
import	java.util.*;
import	utility.rteutility.*;
import	utility.support.DateInfo;

/*-----------------------------------------------------*/
public class GetNotApproveInsolventByFile implements InterfaceRpt
{
	public void makeReport(String [] args) throws Exception
	{
		System.out.println(" ========== GetNotApproveInsolventByFile  ================ ");
		String remote	= args[0];  // true or false 
		if(remote.compareTo("false") == 0)
			PublicRte.setRemote(false);
		else
			PublicRte.setRemote(true);	
		String appserv 	= args[1];  // application server 
		System.out.println(" appserv = " +args[1]);
       		String filename = args[2]; 	//
		System.out.println(" filename = " +args[2]); 
		try { 
			TempMasicFile tmpFile = startSearch(); 
			System.out.println("End startSearch process! ");
			rte.RteRpt.recToTemp(tmpFile, filename);
			System.out.println("write temp --> complete");
			rte.RteRpt.insertReportStatus(appserv, filename, 1);
		        tmpFile.close();
		}
		catch (Exception e)
		{
			rte.RteRpt.errorToTemp(e.getMessage().getBytes(),filename);
			rte.RteRpt.insertReportStatus(appserv, filename, 9);
		} 
	}

	public TempMasicFile startSearch() throws Exception 
	{
		String [] field = {"dataDate","idNo","policyNo","preName","firstName","lastName","birthDate","branch","idNo2","preName2","firstName2","lastName2","caseID","pstatus"};
		int [] len = {8,13,8,20,30,30,8,3,13,20,30,30,15,1};
		TempMasicFile tmpFile = new TempMasicFile("rptservice", field, len);
                tmpFile.keyField(true, true, new String [] {"dataDate"});
                tmpFile.buildTemp();

		Mrecord insolvent = CFile.opens("insolventpolicy@srvservice");
//		Mrecord people = CFile.opens("insolventpeople@srvservice");
		for (boolean st = insolvent.first();st;st= insolvent.next())
		{
			if("IS".indexOf(insolvent.get("status")) >= 0)
			{
				for (int j = 0 ; j < field.length-1;j++)
				{
					tmpFile.set(field[j],insolvent.get(field[j]));	
				}
				tmpFile.set("pstatus",insolvent.get("status"));
				tmpFile.insert();
			}
		}		
		return tmpFile; 
	}	
	private String padBlank(String f ,int len)
	{
		if(len - f.length() <= 0)
			return f;
		return ( f+M.clears(' ',len - f.length()));		
	}
}
