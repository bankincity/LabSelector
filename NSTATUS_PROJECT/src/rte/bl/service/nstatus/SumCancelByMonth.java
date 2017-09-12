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
public class SumCancelByMonth implements InterfaceRpt
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
		String [] field = {"month","notSamePerson","cancelFromCourt","other"};
		int [] len = {6,7,7,7};
		TempMasicFile tmpFile = new TempMasicFile("rptservice", field, len);
                tmpFile.keyField(false, false, new String [] {"month"});
                tmpFile.buildTemp();

		Mrecord insolvent = CFile.opens("insolventpolicy@srvservice");
		Mrecord insolventtran = CFile.opens("insolventtran@srvservice");
		Mrecord insolventrem = CFile.opens("insolventremark@srvservice");
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
//		for (boolean st = insolvent.great(dataDate) ; st && lastDataDate.compareTo(insolvent.get("dataDate")) >= 0;st = insolvent.next())
		for (boolean st = insolvent.first() ; st ;st = insolvent.next())
		{
			
			if ("C".indexOf(insolvent.get("status")) < 0) 
				continue;
	//		System.out.println("data date === "+ insolvent.get("dataDate"));
	//month = insolvent.get("dataDate").substring(0,6);
			String transac = tranDateCancel(insolventtran,insolvent.get("policyNo"),insolvent.get("caseID"),insolvent.get("dataDate"));
			month = transac.substring(0,6);
			if(tmpFile.equal(month))
			{
				System.out.println("policyNo-------------"+insolvent.get("policyNo"));
	//	String transac = tranDateCancel(insolventtran,insolvent.get("policyNo"),insolvent.get("caseID"),insolvent.get("dataDate"));
				int ret =  checkReasonToCancel(insolventrem,transac,insolvent.get("policyNo"),insolvent.get("caseID"));
				if(ret == 1)
					tmpFile.set("notSamePerson",M.setlen(M.inc(tmpFile.get("notSamePerson")),7));
				else if (ret == 2)
					tmpFile.set("cancelFromCourt",M.setlen(M.inc(tmpFile.get("cancelFromCourt")),7));
				else 
					tmpFile.set("other",M.setlen(M.inc(tmpFile.get("other")),7));
				tmpFile.update();
			}
		}
		return tmpFile;
	}
	private String   padBlank(String caseID,int l)
	{
		l = l - caseID.trim().length();
		caseID = caseID+M.clears(' ',l);
		return caseID;
	}
	// 1 - cancel by not same person     2 - cancel from court    3 - cancel by other reason
	private int checkReasonToCancel(Mrecord insolventrem,String transac,String policyNo,String caseID)
	{
		caseID = padBlank(caseID,15);
		System.out.println("....................."+policyNo+caseID+transac);
		
		if  (insolventrem.equal(policyNo+caseID+transac))
		{
			if(insolventrem.get("remark").indexOf(M.stou("ไม่ใช่บุคคลเดียวกัน")) >= 0)
				return 1;
			else if (insolventrem.get("remark").indexOf(M.stou("ปลดล้มละลายโดยกรมบังคับคดี")) >= 0)
				return 2;			
		}
		return 3;	 
	}
	private String  tranDateCancel(Mrecord insolventtran ,String policyNo,String caseID,String dataDate)
        {
                for(boolean st = insolventtran.great(policyNo+caseID);st&&(policyNo+caseID).compareTo(insolventtran.get("policyNo")+insolventtran.get("caseID")) == 0;st=insolventtran.next())
                {
			if (insolventtran.get("tranDate").compareTo(dataDate) < 0)
				continue;
                        if("C".indexOf(insolventtran.get("typeOfTran")) >= 0)
                              return insolventtran.get("tranDate")+insolventtran.get("tranTime");

                }
                return "00000000000000";
        }

}
