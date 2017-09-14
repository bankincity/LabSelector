package rte.bl.branch.receipt.document;
import rte.*;
import 	manit.*; 
import	manit.rte.*;
import 	utility.cfile.*; 
import	utility.support.Branch;
import 	java.io.*; 
import  rte.bl.insuredocument.*;
import 	utility.rteutility.*;

public class RteGetTempRp implements InterfaceRpt
{

	TempMasicFile tempPBE ;
	Mrecord nb, appln;
	
	public RteGetTempRp(){}
	
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
System.out.println("=============b4 build tmp");
		String[] field = { "type", "rpNo", "status", "statusDate", "branch", "policyNo" };
        int[] len = { 1, 12, 1, 8, 3, 8 };
        tempPBE = new TempMasicFile("rptbranch",field,len);
        tempPBE.keyField(true,false,new String [] {"rpNo"});
		tempPBE.buildTemp();
System.out.println("=============after build tmp");
		Mrecord trp = CFile.opens("trpctrl@receipt");
		trp.start(1);
		nb = CFile.opens("nbapplication@cbranch");
		boolean st = trp.equalGreat( sDate ) ;
        for ( ; st && M.cmps( trp.get("statusDate"), eDate ) <= 0; st = trp.next() )
        {
			if ( "PBE".indexOf( trp.get("currentStatus") ) < 0 )
				continue; 

			String submitBranch = trp.get("submitNo").substring(0,3);
			if ( ! submitBranch.equals( branch ) &&  ! Branch.isNonAgent(submitBranch))
				continue; 
 System.out.println("------------------------ : "+trp.get("rpNo")+ Branch.isNonAgent(submitBranch));		
			if (Branch.isNonAgent(submitBranch) && ! trp.get("branch").equals( branch ) )	
				continue; 

				System.out.println("------------------------ : "+trp.get("rpNo"));
			if (M.cmps(trp.get("statusDate"), "25570611") >= 0 && M.cmps(trp.get("statusDate"), "25570630") <= 0)
			{
				if (checkHead(trp.get("policyNo"), trp.get("rpNo")))
					continue;
			}

			String statusDate = trp.get("statusDate");
			if ( ! checkDate(trp.get("submitNo"), trp.get("rpNo"), statusDate))
			{
				System.out.println("checkDate FALSE");
				System.out.println("rpNo : "+trp.get("rpNo")+", date : "+statusDate);
				continue;
			}
			else
				System.out.println("checkDate TRUE");
/*
			if ( ! checkDate(trp.get("submitNo"), trp.get("rpNo"), sDate, eDate))
				continue;*/
		
System.out.println(" result rpNo : "+trp.get("rpNo"));
			tempPBE.set("type", 		getType(trp.get("submitNo"), trp.get("rpNo")) );
			tempPBE.set("rpNo", 		trp.get("rpNo"));
			tempPBE.set("status", 		trp.get("currentStatus"));
			tempPBE.set("statusDate",	trp.get("statusDate"));
			tempPBE.set("branch",		submitBranch);
			tempPBE.set("policyNo",		trp.get("policyNo"));
			tempPBE.insert();
		}
	}

	boolean checkHead(String policyNo, String rpNo)throws Exception
	{
System.out.println("checkHead---------------------1");
		String key = policyNo;
        if (M.numeric(policyNo))
            nb.start( 3 );
        else
            nb.start( 0 );
        boolean check = false;
        if ( ! nb.equal(policyNo))
        {
            nb.start(1);
            key = rpNo;
        }
System.out.println("key : "+key +" policyNo : "+policyNo+"  rpNo : "+ rpNo);
        if ( nb.equal( key ) )
        {
System.out.println("key : "+key +" nb :"+nb.get("yymm")+" approveLocation : "+nb.get("approveLocation"));
            appln = CFile.opens("appln"+nb.get("yymm")+"@cunderwrite");
            if (nb.get("approveLocation").equals("H"))
                check = true;
            else if (appln.equal(nb.get("applicationNo")))
            {
                if (appln.get("attachment").trim().length() > 0)
                    check = true;
            }
            appln.close();
        }
System.out.println("checkHead---------------------2");
        return check ;
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

	boolean checkDate(String submitNo, String rpNo, String statusDate)throws Exception
	{
		Mrecord dsub = CFile.opens("dsubmit@cbranch");
		boolean cDate = true;
		for (boolean b = dsub.equalGreat(submitNo+rpNo); b && dsub.get("submitNo").equals(submitNo) &&
            dsub.get("rpNo").equals(rpNo); b = dsub.next())
        {
			String payDate = dsub.get("payDate");
			if( ! payDate.equals(statusDate) )
			{
				String sysDate = dsub.get("sysDate");
				if ( ! sysDate.equals(statusDate) || M.cmps(payDate, statusDate) > 0)
					cDate = false;
			}
		}
		return cDate;
	}

	boolean checkDate(String submitNo, String rpNo, String sDate, String eDate)throws Exception
	{
		Mrecord dsub = CFile.opens("dsubmit@cbranch");
		boolean cDate = true;
		for (boolean b = dsub.equalGreat(submitNo+rpNo); b && dsub.get("submitNo").equals(submitNo) &&
            dsub.get("rpNo").equals(rpNo); b = dsub.next())
        {
			String payDate = dsub.get("payDate");
			if(M.cmps(payDate, sDate) < 0 || M.cmps(payDate, eDate) > 0)
				cDate = false;
		}
		return cDate;
	}

	public static void main(String args[])throws Exception
	{
		RteGetTempRp rte = new  RteGetTempRp();
		rte.getPBE(args[0], args[1], args[2]);
		for (boolean b = rte.tempPBE.first(); b ; b = rte.tempPBE.next())
		{
			System.out.println("rpNo : "+rte.tempPBE.get("rpNo"));
		}
	}
	
}
