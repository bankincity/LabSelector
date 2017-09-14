package rte.bl.branch.receipt.document;
import rte.*;
import 	manit.*; 
import	manit.rte.*;
import 	utility.cfile.*; 
import 	java.io.*; 
import  rte.bl.insuredocument.*;
import 	utility.rteutility.*;

public class RunGetTempRp implements InterfaceRpt
{

	TempMasicFile tempPBE ;
	public void makeReport(String [] args) throws Exception
    {
        System.out.println("args === "+args.length);
        String remote = args[0];
        String appserv = args[1];
        System.out.println("appesrv = "+appserv);

        String filename = args[2];
        System.out.println("filename = "+filename);
        for (int i = 3; i < args.length; i++)
            System.out.println(" args["+ i + "] = " + args[i]);

        getPBE(args[3], args[4], args[5]);

        System.out.println("End startSearch process!  "+tempPBE.fileSize());
        rte.RteRpt.recToTemp(tempPBE, filename);
        System.out.println("write temp --> complete");
        rte.RteRpt.insertReportStatus(appserv, filename, 1);
        System.out.println("write status report --> complete");
        tempPBE.close();
    }

	void getPBE(String branch, String sDate, String eDate)throws Exception
	{
		String[] field = { "type", "rpNo", "status", "statusDate" };
        int[] len = { 1, 12, 1, 8 };
        tempPBE = new TempMasicFile("rptbranch",field,len);
        tempPBE.keyField(false,false,new String [] {"type", "rpNo"});
		tempPBE.buildTemp();
		Mrecord trp = CFile.opens("trpctrl@receipt");
		trp.start(1);
		boolean st = trp.equalGreat( sDate ) ;
        for ( ; st && M.cmps( trp.get("statusDate"), eDate ) <= 0; st = trp.next() )
        {
			if ( ! trp.get("submitNo").substring(0,3 ).equals( branch ) )
                continue;
			
			tempPBE.set("type", 		getType(trp.get("submitNo"), trp.get("rpNo")) );
			tempPBE.set("rpNo", 		trp.get("rpNo"));
			tempPBE.set("status", 		trp.get("currentStatus"));
			tempPBE.set("statusDate",	trp.get("statusDate"));
			tempPBE.insert();
		}
	}

	String getType(String submitNo, String rpNo)throws Exception
	{
		Mrecord dsub = CFile.opens("dsubmit@cbranch");
		String type = "";
		for (boolean b = dsub.equalGreat(submitNo+rpNo); b && dsub.get("submitNo").equals(submitNo) &&
			dsub.get("rpNo").equals(rpNo); b = dsub.next())
		{
			type = dsub.get("typeOfPolicy");
			break;
		}
		return type;
	}
}
