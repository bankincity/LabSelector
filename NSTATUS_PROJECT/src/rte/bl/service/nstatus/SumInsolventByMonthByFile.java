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
public class SumInsolventByMonthByFile implements InterfaceRpt
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
		String year = args[3];
		try { 
			TempMasicFile tmpFile = startSearch(year); 
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

	public TempMasicFile startSearch(String year) throws Exception 
	{
		String [] field = {"month","notApprove","approve"};
		int [] len = {6,7,7};
		TempMasicFile tmpFile = new TempMasicFile("rptservice", field, len);
                tmpFile.keyField(false, false, new String [] {"month"});
                tmpFile.buildTemp();

		Mrecord insolvent = CFile.opens("insolventpolicy@srvservice");
		Mrecord insolventtran = CFile.opens("insolventtran@srvservice");
		String dataDate = year+"0101";
		String lastDataDate = year+"1231";
		String smonth = year+"01";
		String emonth = year+"12";
		insolvent.start(3);
		while (smonth.compareTo(emonth) <= 0)
		{
			tmpFile.set('0');
			tmpFile.set("month",smonth);
			tmpFile.insert();
			smonth = (DateInfo.nextMonth(smonth+"01")).substring(0,6);
		}
		String month = "";
		for (boolean st = insolvent.great(dataDate) ; st && lastDataDate.compareTo(insolvent.get("dataDate")) >= 0;st = insolvent.next())
		{
	//		System.out.println("data date === "+ insolvent.get("dataDate"));
			month = insolvent.get("dataDate").substring(0,6);
			if(tmpFile.equal(month))
			{
	//			System.out.println("data date === "+ insolvent.get("status"));
				if("SPI".indexOf(insolvent.get("status")) >= 0)
					tmpFile.set("approve",M.setlen(M.inc(tmpFile.get("approve")),7));
				else if ("A".indexOf(insolvent.get("status")) >= 0) 
					tmpFile.set("notApprove",M.setlen(M.inc(tmpFile.get("notApprove")),7));
				else if ("C".indexOf(insolvent.get("status")) >= 0) 
				{
					int e = everProve(insolventtran,insolvent.get("policyNo"),insolvent.get("caseID"));
                                        if (e == 2)
                                                tmpFile.set("approve",M.setlen(M.inc(tmpFile.get("approve")),7));
                                        else if (e == 3)
                                                tmpFile.set("notApprove",M.setlen(M.inc(tmpFile.get("notApprove")),7));

				}
				else {
					System.out.println(insolvent.get("policyNo")+"--------------"+insolvent.get("status"));
				}
					
				tmpFile.update();
			}
		}
		return tmpFile;
	}
	private int  everProve(Mrecord insolventtran ,String policyNo,String caseID)
	{
		if (insolventtran.great(policyNo+caseID) && (policyNo+caseID).compareTo(insolventtran.get("policyNo")+insolventtran.get("caseID")) == 0 && insolventtran.get("typeOfTran").charAt(0) == 'C')
                        return 1;

                for(boolean st = insolventtran.great(policyNo+caseID);st&&(policyNo+caseID).compareTo(insolventtran.get("policyNo")+insolventtran.get("caseID")) == 0;st=insolventtran.next())
                {
                        if("SI".indexOf(insolventtran.get("typeOfTran")) >= 0)
                                return 2;

                }
                return 3 ;

	}
}
