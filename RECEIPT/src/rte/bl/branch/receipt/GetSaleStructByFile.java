package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import  utility.branch.sales.BraSales;
import  utility.branch.sales.SalesService;
import  rte.bl.branch.*;
import java.util.*;
import rte.*;
import utility.rteutility.*;
public class  GetSaleStructByFile implements  InterfaceRpt
{
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("args === "+args.length);
		String remote	= args[0];  // true or false 
                String appserv = args[1];  // application server  <--> braxxxapp
                String filename = args[2]; // masic output stream  
		if(remote.compareTo("false") == 0)
			PublicRte.setRemote(false);
		//  yyyymm : current ask  ,ucrpyyymm,branch, depNo
		getSaleStruct(args[3],args[4],args[5],args[6],args[7]);		
		System.out.println("End startSearch process! ");
		rte.RteRpt.recToTemp(temptoprint, filename);
                System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		temptoprint.close();
	}
	// yyyymm = current ask month
	// ucrpyyyymm = current ucrp month (filename)
	Mrecord irc;
	Mrecord orc;
	Mrecord wrc;
	Mrecord trp;
	Mrecord baddep;
	BraSales brasale ;
	public void getSaleStruct(String yyyymm,String ucrpyyyymm,String branch,String depNo,String under) throws Exception
	{
		Mrecord ask = CFile.opens("ask"+yyyymm+"@cbranch");
		Mrecord ucrp = CFile.opens(ucrpyyyymm+"@cbranch");
		baddep = CFile.opens("baddep@cbranch");
		irc = CFile.opens("irctrl@receipt");
		orc = CFile.opens("orctrl@receipt");
		wrc = CFile.opens("wrctrl@receipt");		
		trp = CFile.opens("trpctrl@receipt");		
		brasale = new BraSales();
		
		temptoprint = new TempMasicFile("rptbranch",field,len);
		temptoprint.keyField(false,false,new String [] {"status","depNo"});
                temptoprint.buildTemp();
		System.out.println("open file get all request rp ");
		Vector vsale = null;
		try
		{
			brasale.getByDepositNo(branch,depNo);
			brasale.getStruct();
			if(under.charAt(0) == 'A')
				vsale =  brasale.getDepInStruct(true);
			else
				vsale =  brasale.getDepInStruct(false);
		}
		catch (Exception e)
		{
			throw new Exception ("Error from BraSales :"+e.getMessage());
		}
		for (int i = 0 ; i < vsale.size();i++)
		{
			String [] tstr = (String [])vsale.elementAt(i);	
			temptoprint.set('0');
			temptoprint.set("status",tstr[0]);
			temptoprint.set("depNo",tstr[1]);
			temptoprint.set("saleID",tstr[2]);
			temptoprint.set("pname",tstr[3]);
			temptoprint.set("fname",tstr[4]);
			temptoprint.set("lname",tstr[5]);
			temptoprint.set("expireDate",getExpireDate(tstr[6]));
		
			if (CheckAskReceipt.inAskYYMM(ask , temptoprint.get("saleID") ,"" ,1,false,branch) != 0)
			{
				temptoprint.set("everRequest","1");		
			}
			if(CheckAskReceipt.checkDepno(ucrp,temptoprint.get("saleID"),0,false,irc,orc,wrc,trp,branch) ||
		   		CheckAskReceipt.checkDepno(ucrp,temptoprint.get("saleID"),1,false,irc,orc,wrc,trp,branch))
			{	
				temptoprint.set("inUcrp","1");		
			}
			System.out.print("before badDep............................."+depNo+"..............");
			temptoprint.set("badSale",isBadDep(tstr[1],branch));
			System.out.print("badsale"+".............."+temptoprint.get("badSale"));
			temptoprint.insert();		
		}
	}
	TempMasicFile  temptoprint;
	String [] field = {"depNo","pname","fname","lname","status","everRequest","badSale","inUcrp","saleID","expireDate"};
	int [] len = {5,30,30,30,1,1,1,1,10,8};

	public String isBadDep(String depNo,String branch)
	{
		if(baddep.equal(branch+depNo))
			return "1";
		return "0";
	}
	public String getExpireDate(String citizenID)
	{
		Vector vlc = null;
		try {
			vlc = SalesService.getLicenseByCitizenID(citizenID);
		}
		catch (Exception e)
		{
			return "00000000";
		}
		if (vlc != null && vlc.size() > 0) 
		{
			String [] slc = (String [])vlc.elementAt(0);
			return slc[7];
                      /*  snRec.set("licenseNo", slc[5]);
                        snRec.set("licenseExpireDate", slc[7]);*/
                }
		return "00000000";
	}
}


