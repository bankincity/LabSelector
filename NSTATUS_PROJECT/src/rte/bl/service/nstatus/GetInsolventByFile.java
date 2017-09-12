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
public class GetInsolventByFile implements InterfaceRpt
{
	String typeOfInsolvent;
	String dataDate;
	String lastDataDate;
	String benefit ="0";
	public void makeReport(String [] args) throws Exception
	{
		System.out.println(" ========== GetInsolventByFile  ================ ");
		String remote	= args[0];  // true or false 
		if(remote.compareTo("false") == 0)
			PublicRte.setRemote(false);
		else
			PublicRte.setRemote(true);	
		String appserv 	= args[1];  // application server 
		System.out.println(" appserv = " +args[1]);
       		String filename = args[2]; 	//
		System.out.println(" filename = " +args[2]); 
		System.out.println(" args = " + args.length); 
		typeOfInsolvent = args[3];
		dataDate  = (String)args[4];
		lastDataDate  = (String)args[5];
		if(args.length == 7)
			benefit = (String)args[6]; 
		try { 
			System.out.println("benefit--------"+benefit+"  "+typeOfInsolvent );
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
		String [] field = {"idNo","policyNo","preName","firstName","lastName","birthDate","branch","idNo2","preName2","firstName2","lastName2","caseID","pstatus"};
		int [] len = {13,8,20,30,30,8,3,13,20,30,30,15,1};
		TempMasicFile tmpFile = new TempMasicFile("rptservice", field, len);
                tmpFile.keyField(true, true, new String [] {"idNo"});
                tmpFile.buildTemp();

		Mrecord insolvent = CFile.opens("insolventpolicy@srvservice");
		Mrecord histinsolvent = CFile.opens("histinsolventpolicy@srvservice");
		histinsolvent.start(2);
		Mrecord people = CFile.opens("insolventpeople@srvservice");
		insolvent.start(3);
		while (dataDate.compareTo(lastDataDate) <= 0)
		{
			for (int i = 0 ; i < typeOfInsolvent.length();i++)
			{
				String ikey = dataDate+typeOfInsolvent.substring(i,i+1);
				for (boolean st = insolvent.equalGreat(ikey);st && ikey.compareTo(insolvent.get("dataDate")+insolvent.get("status")) == 0;st=insolvent.next())
				{
					System.out.println("benefit--------"+benefit+"   "+ insolvent.get("document"));
					if(benefit.charAt(0) != insolvent.get("document").charAt(0))
							continue;
					for (int j = 0 ; j < field.length-1;j++)
					{
						tmpFile.set(field[j],insolvent.get(field[j]));	
					}
					tmpFile.set("pstatus",insolvent.get("status"));
			/*	if(people.equal(padBlank(insolvent.get("idNo"),13)+padBlank(insolvent.get("firstName"),30)+padBlank(insolvent.get("lastName"),30)+padBlank(insolvent.get("caseID"),15)+insolvent.get("insolventDate")))
				{
					tmpFile.set("idNo2",people.get("idNo"));
					tmpFile.set("firstName",people.get("firstName"));
					tmpFile.set("lastName",people.get("lastName"));
					tmpFile.set("pstatus",people.get("status"));
				}*/
					tmpFile.insert();
					for (boolean st1 = histinsolvent.equalGreat(tmpFile.get("idNo")); st1 && tmpFile.get("idNo").compareTo(histinsolvent.get("idNo")) == 0 ; st1 = histinsolvent.next())
					{
						for (int j = 0 ; j < field.length-1;j++)
						{
							tmpFile.set(field[j],insolvent.get(field[j]));	
						}
						tmpFile.set("pstatus","H");
						tmpFile.insert();

					}					
				}
				
				
			}
			dataDate = M.nextdate(dataDate,1);
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
