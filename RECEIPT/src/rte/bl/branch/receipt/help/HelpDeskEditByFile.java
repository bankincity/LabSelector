package rte.bl.branch.receipt.help;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import  rte.bl.branch.*;
import java.util.*;
import rte.*;
import utility.rteutility.*;
public class  HelpDeskEditByFile implements  InterfaceRpt
{
	TempMasicFile temptoprint;
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("args === "+args.length);
		String remote	= args[0];  // true or false 
                String appserv = args[1];  // application server  <--> braxxxapp
                String filename = args[2]; // masic output stream  
		if(remote.compareTo("false") == 0)
			PublicRte.setRemote(false);
		temptoprint = startSearch(args[3],args[4],args[5]);			
		System.out.println("End startSearch process! ");

		rte.RteRpt.recToTemp(temptoprint, filename);
                System.out.println("write temp --> complete");

		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		temptoprint.close();
	}
	public  TempMasicFile  startSearch(String branch,String sdate,String edate) throws Exception
 	{
		String[] field2 = {"branch","sysDate","msg"};
 		int [] flen2 = {3,8,200};

		Mrecord help = CFile.opens("helpdeskedit@insuredocument");
 		TempMasicFile tempTot = new TempMasicFile("rptbranch",field2,flen2);
 		tempTot.keyField(true,true,new String [] {"sysDate"});
 		tempTot.buildTemp();
// 		help.start(1);
 		System.out.println("sdate.................."+sdate);
 		for (boolean st = help.equalGreat(branch+sdate);st;st=help.next())
 		{
			if(branch.compareTo(help.get("branch")) != 0)
 				break;
			if(sdate.compareTo(help.get("sysDate")) > 0)
 				break;
			if(edate.compareTo(help.get("sysDate")) < 0)
 				break;
			tempTot.set("branch",help.get("branch"));
			tempTot.set("sysDate",help.get("sysDate"));
			tempTot.set("msg",help.get("msg"));
 			tempTot.insert();
		}
		return tempTot;
	}
}


