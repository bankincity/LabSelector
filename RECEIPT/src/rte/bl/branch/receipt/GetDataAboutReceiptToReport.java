package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import  utility.branch.sales.BraSales;
import  rte.bl.branch.*;
import java.util.*;
import rte.*;
import utility.rteutility.*;
import utility.support.DateInfo;
import layout.receipt.*;
import utility.cfile.Rtemp;
import utility.prename.Prename;
public class  GetDataAboutReceiptToReport implements  InterfaceRpt
{
	TempMasicFile  temptoprint;
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("args === "+args.length);
		String remote	= args[0];  // true or false 
		if(remote.compareTo("false") == 0)
			PublicRte.setRemote(false);
		else
			PublicRte.setRemote(true);
                String appserv = args[1];  // application server  <--> braxxxapp
                String filename = args[2]; // masic output stream  

		if(args[3].compareTo("CASK") == 0)
		{
			getCaskToReport(args[4],args[5],args[6]);	
		}
		else if(args[3].compareTo("TRPTMP") == 0)
		{
			// sdate ,userID, branch
			getReturnTrp(args[4],args[5],args[6]);
		}
		System.out.println("End startSearch process! ");
		rte.RteRpt.recToTemp(temptoprint, filename);
                System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		temptoprint.close();
	}	
	Mrecord tcask ;
	Mrecord tcask_1;
	Mrecord tcask_other;
	Mrecord tcask_other1;
	BraSales bsale;
	public void getCaskToReport(String branch ,String sdate,String passworduser) throws Exception
	{
		String yyyymm = sdate.substring(0,6);
			System.out.println("yyymm===========111==========="+yyyymm);
		tcask = CFile.opens("cask"+yyyymm+"@cbranch");
		tcask_other = CFile.opens("caskother"+yyyymm+"@receipt");
		if(sdate.substring(6).compareTo("17") >=  0)
		{
			System.out.println("yyymm===========222==========="+yyyymm);
			yyyymm = DateInfo.nextMonth(yyyymm).substring(0,6);
			System.out.println("yyymm===========333==========="+yyyymm);
			tcask_1 = CFile.opens("cask"+yyyymm+"@cbranch");
			tcask_other1 = CFile.opens("caskother"+yyyymm+"@receipt");
		}
		bsale = new BraSales();
		String []  field = {"userID","askDepno","depositNo","status","userName","aname","oname","ownerBranch"};
        	int [] flen         = {7,5,5,1,90,90,90,3};
		temptoprint = new  TempMasicFile("rptbranch",field,flen);
		temptoprint.keyField(true,true,new String [] {"userID","askDepno","depositNo"});
		temptoprint.buildTemp();

		getFromCask(tcask,tcask_other,branch,sdate,passworduser);
		if(tcask_1 != null)
			getFromCask(tcask_1,tcask_other1,branch,sdate,passworduser);

	}
	private void getFromCask(Mrecord cask,Mrecord tcask_other,String branch,String sdate,String passworduser) throws Exception
	{
		cask.start(1);
                for (boolean st = cask.equal(branch+sdate);st && (branch+sdate).compareTo(cask.get("branch")+cask.get("requestDate"))==0;st=cask.next())
                {
                        if (passworduser.compareTo("0000000") != 0 && passworduser.compareTo(cask.get("userID")) != 0)
                         	  continue;
			temptoprint.set(' ');
			temptoprint.set("depositNo","00000");
                        temptoprint.set("userID",cask.get("userID"));
			System.out.println("cask------------------"+cask.get("askSaleID"));
			try {
				bsale.getBySalesID(cask.get("askSaleID"));
				temptoprint.set("askDepno",bsale.getSnRec("depositNo"));
				temptoprint.set("aname",bsale.getSnRec("preName") +bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName"));	
				System.out.println("aname -- >"+temptoprint.get("aname"));	
                        	if ((cask.get("askSaleID")).compareTo(cask.get("ownerSaleID")) != 0)
				{
					System.out.println("owner cask----------------1--"+cask.get("ownerSaleID"));
					bsale.getBySalesID(cask.get("ownerSaleID"));
					System.out.println("owner cask---------------2---"+cask.get("ownerSaleID"));
					temptoprint.set("depositNo",bsale.getSnRec("depositNo"));
					temptoprint.set("oname",bsale.getSnRec("preName") +bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName"));		
				}
			}
                        catch (Exception e)
			{
				System.out.println("Error :  "+e.getMessage());
                        }
                        temptoprint.set("status",cask.get("status"));
			if( tcask_other.equal( cask.get("askSaleID") + sdate ) )
				temptoprint.set("ownerBranch",tcask_other.get("salesBranch"));
                        temptoprint.insert();
                }
                cask.start(0);
	}
	public void getReturnTrp(String sdate,String userID,String branch) throws Exception
	{
		String [] field = {"userID","depNo","rpNo","uname","oname"};
		int [] flen    	= {7,5,12,90,90};
		
		temptoprint = new  TempMasicFile("rptbranch",field,flen);
		temptoprint.keyField(true,true,new String [] {"userID","depNo","rpNo"});
		temptoprint.buildTemp();
		
		bsale = new BraSales();

		Mrecord trptmp  = CFile.opens("trptmp@cbranch");
                String sai = "";
                if (branch.compareTo("007") == 0)
                    sai = M.stou("(สาย 5)");
		for (boolean st = trptmp.equalGreat(branch) ; st ;st = trptmp.next())
		{
			if (sdate.compareTo(trptmp.get("returnDate"))==0&& branch.compareTo(trptmp.get("branch"))==0&&
		 	   (userID.compareTo("0000000") == 0 ||  userID.compareTo(trptmp.get("userID")) == 0 ))
                        {
                                temptoprint.set(' ');
				try {
                      			bsale.getBySalesID(trptmp.get("ownerSaleID"));
                                	temptoprint.set("depNo",bsale.getSnRec("depositNo"));
                                        
                               		temptoprint.set("oname",bsale.getSnRec("preName") +bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName")+" "+sai);
				}
				catch (Exception e)
				{
					temptoprint.set("depNo","00000");
					temptoprint.set("oname"," ");
					System.out.println(e.getMessage());	
				}
  				temptoprint.set("rpNo",trptmp.get("rpNo"));
				
                                temptoprint.set("userID",trptmp.get("userID"));
				
                                temptoprint.insert();
                        }
                 }
                if (branch.compareTo("007") == 0)
                {
                   
                        String tbranch = "A07";
                        sai = M.stou("(สาย 6)");
		        for (boolean st = trptmp.equalGreat(tbranch) ; st ;st = trptmp.next())
		        {
			        if (sdate.compareTo(trptmp.get("returnDate"))==0&& tbranch.compareTo(trptmp.get("branch"))==0&&
		 	        (userID.compareTo("0000000") == 0 ||  userID.compareTo(trptmp.get("userID")) == 0 ))
                                {
                                        temptoprint.set(' ');
				        try {
                      			        bsale.getBySalesID(trptmp.get("ownerSaleID"));
                                	        temptoprint.set("depNo",bsale.getSnRec("depositNo"));
                               		        temptoprint.set("oname",bsale.getSnRec("preName") +bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName")+" "+sai);
				        }
				        catch (Exception e)
				        {
					        temptoprint.set("depNo","00000");
					        temptoprint.set("oname"," ");
					        System.out.println(e.getMessage());	
				        }
  				        temptoprint.set("rpNo",trptmp.get("rpNo"));
				
                                        temptoprint.set("userID",trptmp.get("userID"));
				
                                        temptoprint.insert();
                                }
                        }
                 }
                 sai = "";
                 if (branch.compareTo("007") == 0)
                    sai = M.stou("(สาย 5)");
		 trptmp  = CFile.opens("trptmptakaful@cbranch");
		 for (boolean st = trptmp.equalGreat(branch) ; st ;st = trptmp.next())
		 {
			if (sdate.compareTo(trptmp.get("returnDate"))==0&& branch.compareTo(trptmp.get("branch"))==0&&
		 	   (userID.compareTo("0000000") == 0 ||  userID.compareTo(trptmp.get("userID")) == 0 ))
                        {
                                temptoprint.set(' ');
				try {
                      			bsale.getBySalesID(trptmp.get("ownerSaleID"));
                                	temptoprint.set("depNo",bsale.getSnRec("depositNo"));
                               		temptoprint.set("oname",bsale.getSnRec("preName") +bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName")+" "+sai);
				}
				catch (Exception e)
				{
					temptoprint.set("depNo","00000");
					temptoprint.set("oname"," ");
					System.out.println(e.getMessage());	
				}
  				temptoprint.set("rpNo",trptmp.get("rpNo"));
				
                                temptoprint.set("userID",trptmp.get("userID"));
				
                                temptoprint.insert();
                        }
                 }
                 if (branch.compareTo("007") == 0)
                 {
                        String tbranch = "A07";
                        sai = M.stou("(สาย 6)");
		        for (boolean st = trptmp.equalGreat(tbranch) ; st ;st = trptmp.next())
		        {
			        if (sdate.compareTo(trptmp.get("returnDate"))==0&& tbranch.compareTo(trptmp.get("branch"))==0&&
		 	        (userID.compareTo("0000000") == 0 ||  userID.compareTo(trptmp.get("userID")) == 0 ))
                                {
                                        temptoprint.set(' ');
				        try {
                      			        bsale.getBySalesID(trptmp.get("ownerSaleID"));
                                	        temptoprint.set("depNo",bsale.getSnRec("depositNo"));
                               		        temptoprint.set("oname",bsale.getSnRec("preName") +bsale.getSnRec("firstName")+" "+bsale.getSnRec("lastName")+ " "+sai);
				        }
				        catch (Exception e)
				        {
					        temptoprint.set("depNo","00000");
					        temptoprint.set("oname"," ");
					        System.out.println(e.getMessage());	
				        }
  				        temptoprint.set("rpNo",trptmp.get("rpNo"));
				
                                        temptoprint.set("userID",trptmp.get("userID"));
				
                                        temptoprint.insert();
                                }
                        }
                 }
	} 	
}


