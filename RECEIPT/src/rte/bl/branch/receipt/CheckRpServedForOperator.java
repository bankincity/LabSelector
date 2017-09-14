package rte.bl.branch.receipt; 
import manit.*;
import manit.rte.*;
import utility.support.*;
import utility.cfile.*;
import utility.rteutility.*;
import rte.bl.branch.TempMasicFile;
import rte.*;
import java.util.Vector;
import utility.prename.*;

public class  CheckRpServedForOperator implements InterfaceRpt
{	
	TempMasicFile temp; 	
	String sbranch = "000"; 
	String ebranch = "000"; 
	String month = "000000";
	Mrecord orRp  = null; 
	Mrecord irRp  = null; 
	Mrecord wlRp  = null; 
	public CheckRpServedForOperator() throws Exception
	{
		System.out.println(" ============ CheckRpServedForOperator ============ "); 
	}
	public void makeReport(String [] args) throws Exception
	{
		System.out.println("args === "+args.length);
		for (int i = 0; i < args.length; i++)
			System.out.println(" args [ " + i + " ] = " + args[i]); 
		String remote = args[0];			
		String appserv = args[1];				
		String filename = args[2];		 
					
		sbranch  = args[4]; 
		ebranch  = args[5]; 
		month = args[3];

		startSearch(month,sbranch,ebranch); 			
		System.out.println("End startSearch process!  "+temp.fileSize());
		rte.RteRpt.recToTemp(temp, filename);
		System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		System.out.println("write status report --> complete");
		temp.close();		
	}
	public void startSearch(String month, String sbranch, String ebranch) throws Exception
	{
		String [] field = {"branch","policyType","total","printed","cancelPrint","printing","noprint"};
                int [] len = {3,1,6,6,6,6,6};

		temp = new TempMasicFile("rptbranch", field, len);
		temp.keyField(false, false, new String[] {"branch","policyType"}); 
		temp.buildTemp();
		Mrecord irpserved = CFile.opens("irpserved"+month+"@cbranch");
		Mrecord orpserved = CFile.opens("orpserved"+month+"@cbranch");
		countServed(irpserved,"I",sbranch,ebranch);
		countServed(orpserved,"O",sbranch,ebranch);

		System.out.println("can buildTemp !!!"); 									
	}
	public void countServed(Mrecord served,String policyType,String branch,String ebranch)
        {
                for (boolean st = served.equalGreat(branch) ;st;)
                {
			branch = served.get("branch");
			if (branch.compareTo(ebranch) > 0)
				break;
			int  [] data = {0,0,0,0,0};
			for (;st && branch.compareTo(served.get("branch")) == 0;)
			{
				data[0]++;
                     		if("28".indexOf(served.get("rpFlag").charAt(0))>= 0)
                                	data[1]++;
                        	else if(served.get("rpFlag").charAt(0) == '9')
                               		 data[2]++;
                        	else if("1".indexOf(served.get("rpFlag").charAt(0)) >= 0)
                                	data[3]++;
                      		else
                                	data[4]++;
				st=served.next();
			}
			System.out.println("policyType---------"+policyType+"----"+branch);

			temp.set("branch",branch);
			temp.set("policyType",policyType);
			temp.set("total",M.itoc(data[0]));	
			temp.set("printed",M.itoc(data[1]));	
			temp.set("cancelPrint",M.itoc(data[2]));	
			temp.set("printing",M.itoc(data[3]));	
			temp.set("noprint",M.itoc(data[4]));	
			if (M.cmps(temp.get("printing"),"0") <= 0 && M.cmps(temp.get("noprint"),"0") <= 0)
                                continue;
			temp.insert();
                }
        }
}
