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
public class  GetAllUCRPFee implements  InterfaceRpt
{
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("args === "+args.length);
		String remote	= args[0];  // true or false 
                String appserv = args[1];  // application server  <--> braxxxapp
                String filename = args[2]; // masic output stream  
		// month,branch
		try {
			UCRPYYYYMM uc = new UCRPYYYYMM ();
			TempMasicFile temptoprint = uc.searchUCFEE(args[3],args[4]);		
			System.out.println("End startSearch process! ");
			rte.RteRpt.recToTemp(temptoprint, filename);
	      	        System.out.println("write temp --> complete");
			rte.RteRpt.insertReportStatus(appserv, filename, 1);
			temptoprint.close();
		}
		catch(Exception e)
		{
		
			rte.RteRpt.errorToTemp(e.getMessage().getBytes(),filename);
			rte.RteRpt.insertReportStatus(appserv, filename, 9);		
		}
	}
}


