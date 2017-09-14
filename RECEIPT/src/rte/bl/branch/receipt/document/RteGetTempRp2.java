package rte.bl.branch.receipt.document;
import rte.*;
import 	manit.*; 
import	manit.rte.*;
import 	utility.cfile.*; 
import	utility.support.Branch;
import 	java.io.*; 
import  rte.bl.insuredocument.*;
import 	utility.rteutility.*;
import  utility.support.*; 

public class RteGetTempRp2 implements InterfaceRpt
{

	TempMasicFile tempPBE ;
	Mrecord nb, appln;
	public void makeReport(String [] args) throws Exception
    {
		System.out.println(" ============ RteGetTempRp2 =============");
        System.out.println("args === "+args.length);
        String remote  = args[0];
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

	/*-------------------------------------------------------------*/
	void getPBE(String branch, String sDate, String eDate)throws Exception
	{
		System.out.println(" ---- getPBE: " + branch + " " + sDate +" " + eDate ); 
		String[] field = { "type", "rpNo", "status", "statusDate", "branch", "policyNo"};
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

			String submitBranch = trp.get("submitNo").substring(0, 3);
			if ( M.cmps( DateInfo.sysDate(), "25581019") < 0 )
			{ 
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
				tempPBE.set("type", 		getType(trp.get("submitNo"), trp.get("rpNo")) );
				tempPBE.set("rpNo", 		trp.get("rpNo"));
				tempPBE.set("status", 		trp.get("currentStatus"));
				tempPBE.set("statusDate",	trp.get("statusDate"));
				tempPBE.set("branch",		submitBranch);
				tempPBE.set("policyNo",		trp.get("policyNo"));
				tempPBE.insert();
			}
			else 
			{ 

				if (! branch.equals( submitBranch ) )
					continue; 

				tempPBE.set("type", 		getType(trp.get("submitNo"), trp.get("rpNo")) );
				tempPBE.set("rpNo", 		trp.get("rpNo"));
				tempPBE.set("status", 		trp.get("currentStatus"));
				tempPBE.set("statusDate",	trp.get("statusDate"));
				tempPBE.set("branch",		submitBranch);
				tempPBE.set("policyNo",		trp.get("policyNo"));
				tempPBE.insert();
			}		
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
}
