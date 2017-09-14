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

public class  SearchRpStatusCGZY implements InterfaceRpt
{	
	TempMasicFile temp; 	
	String branch = ""; 
	Mrecord orRp  = null; 
	Mrecord irRp  = null; 
	Mrecord wlRp  = null; 
	Mrecord ulrp  = null; 
	public SearchRpStatusCGZY() throws Exception
	{
		System.out.println(" ============ searchRpStatusCGZY ============ "); 
/*		orRp  = CFile.opens("orctrl@receipt"); 
		irRp  = CFile.opens("irctrl@receipt"); 
		wlRp  = CFile.opens("wrctrl@receipt"); 	*/
	}

	public void makeReport(String [] args) throws Exception
	{
		System.out.println("args === "+args.length);
		for (int i = 0; i < args.length; i++)
			System.out.println(" args [ " + i + " ] = " + args[i]); 
		String remote = args[0];			
		String appserv = args[1];				
		System.out.println("appesrv = "+appserv); 
		String filename = args[2];		 
		System.out.println("filename = "+filename); 						
		branch  = args[3]; 
		System.out.println("  === branch = " +branch); 
		String mmyyyy	 = args[4]; 		
		System.out.println("  === mmyyyy = " + mmyyyy); 
		startSearch(branch, mmyyyy); 			
		System.out.println("End startSearch process!  "+temp.fileSize());
		rte.RteRpt.recToTemp(temp, filename);
		System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		System.out.println("write status report --> complete");
		System.out.println(" ----------------- End startSearch ---------------------- "+temp.fileSize()); 
		temp.close();		
	}

	//---------------------------------------------//
	//	'F' : แบบฟอร์มใบเสร็จใบรับเงินฯ 
	//---------------------------------------------//
	public void startSearch(String branch, String yyyymm)throws Exception
	{
		String [] field = {"branch", "typeOfPolicy", "typeOfstatus", "numOfType", "form"};
		int    [] flen  = {3, 1, 1, 5, 1};
		temp = new TempMasicFile("rptbranch", field, flen);
		temp.keyField(true, false, new String[] {"typeOfstatus"}); 
		temp.buildTemp();
		System.out.println("can buildTemp !!!"); 									
		String indexType = "IOWUL"; 
		String indexStatus = "CGZY"; 
		String key4 = ""; 
		Mrecord rp = null; 
		boolean okfind = false; 
		for (int j = 0; j < indexType.length(); j++)
		{
			char index = indexType.charAt(j); 
			System.out.println(" ---------- index : "+ index); 			
			int inds = 0; 
			switch (index)
			{
				case 'I' :  rp  = CFile.opens("irctrl@receipt");
							break; 

				case 'O' :  rp  = CFile.opens("orctrl@receipt");
							break; 

				case 'W' :  rp  = CFile.opens("wrctrl@receipt");
							break; 

				case 'U' :  if (Masic.fileStatus("ulrctrl@universal") >= 2)
								rp 	= CFile.opens("ulrctrl@universal"); 
							break; 

				case 'L' :  if (Masic.fileStatus("uliprctrl@unitlink") >= 2)
								rp 	= CFile.opens("uliprctrl@unitlink"); 
							break;
			}
			rp.start(4); // CGZY - (currentStatus submitBranch)
			while (inds <= 3)
			{
				String status = indexStatus.charAt(inds)+""; 
				String typeOfPolicy = index+"";  
				System.out.println( typeOfPolicy + " " +inds + " status : "+ status);
				String count = "0"; 
				for (int d = 1; d < 32; d++)
				{
					String day = M.setlen(d+"", 2); 
					key4 = status+yyyymm+day+branch; // CGZY (4 - currentStatus  sysDate submitBranch rpNo policyNo) 				
					okfind = rp.equalGreat(key4); 			
					System.out.println(" ---- >>> key4    = " + key4); 
					System.out.println(" ---- >>  okfind = " + okfind); 
					for (; okfind && branch.compareTo(rp.get("submitBranch").trim()) == 0; okfind = rp.next())
					{	
						
						if (rp.get("sysDate").compareTo(yyyymm+day) !=  0)
							continue; 

			//			if (rp.get("rpNo").charAt(0) == 'C' || rp.get("rpNo").charAt(0) == 'A' || rp.get("rpNo").charAt(0) == 'B' )
						if (rp.get("rpNo").charAt(0) == 'C' || rp.get("rpNo").charAt(0) == 'B' ||  rp.get("rpNo").charAt(0) == 'D' )
							continue; //last update 03/08/2553 order by P'Tuan

						System.out.println(" -- >>> 2.3 sysDate: " + rp.get("sysDate") + " " + rp.get("rpNo") ); 
						System.out.println(" -- >>> 2.2 status : "+ status + " currentstatus : " +rp.get("currentStatus").trim()); 	
						if (status.compareTo(rp.get("currentStatus").trim()) ==  0 )
							count = M.addnum(count, "1"); 
					}
					System.out.println ("SUM : " + day  + " status = " + status + " count = " + count); 
				}							
				System.out.println ("***SUMALL :  status = " + status + " count = " + count); 
				
				temp.set("branch", 		 rp.get("submitBranch")); 
				temp.set("typeOfPolicy", typeOfPolicy); 
				temp.set("typeOfstatus", status); 
				temp.set("numOfType", 	 count);		
				temp.set("form", 	     ""); 
				temp.insert(); 
				inds = inds+1;
				System.out.println(" finished typeOf policy : "+ typeOfPolicy + " inds : "+inds); 
			}
		}	
		Vector v = getRpCancelForm(yyyymm); 
		String fCI = (String)v.elementAt(0); 
		temp.set("branch", branch); 
		temp.set("typeOfPolicy", "I"); 
		temp.set("typeOfstatus", "C"); 
		temp.set("numOfType",    fCI); 
		temp.set("form", 	     "F"); 
		temp.insert(); 
		String fCOW = (String)v.elementAt(1); 
		temp.set("branch", branch); 
		temp.set("typeOfPolicy", "O"); 
		temp.set("typeOfstatus", "C"); 
		temp.set("numOfType",    fCOW); 
		temp.insert(); 
		String fRI = (String)v.elementAt(2); 
		temp.set("branch", branch); 
		temp.set("typeOfPolicy", "I"); 
		temp.set("typeOfstatus", "R"); 
		temp.set("numOfType",    fRI); 
		temp.set("form", 	     "F"); 
		temp.insert(); 
		String fROW = (String)v.elementAt(3); 
		temp.set("branch", branch); 
		temp.set("typeOfPolicy", "O"); 
		temp.set("typeOfstatus", "R"); 
		temp.set("numOfType",    fROW); 
		temp.insert(); 
		String fC17 = (String)v.elementAt(4); 
		temp.set("branch", 		 branch); 
		temp.set("typeOfPolicy", "N"); 
		temp.set("typeOfstatus", "C"); 
		temp.set("numOfType",    fC17); 
		temp.insert(); 
		String fR17 = (String)v.elementAt(5); 
		temp.set("branch", 		 branch); 
		temp.set("typeOfPolicy", "N"); 
		temp.set("typeOfstatus", "R"); 
		temp.set("numOfType",    fR17); 
		temp.insert(); 
		System.out.println(" ##### temp.fileSize() = " + temp.fileSize()); 
		for (boolean st = temp.first(); st; st = temp.next())
			System.out.println(" typeOfPolicy : " + temp.get("typeOfPolicy") + " status : "+temp.get("typeOfstatus") + " num : "+ temp.get("numOfType") + " form : " + temp.get("form")); 
	}	

	//===========================================================
	// universallife docCode - same ordinary and whole 
	//===========================================================
	public Vector getRpCancelForm(String yyyymm)throws Exception 
	{
		System.out.println(" innser gtRPCancelForm :  "+ yyyymm); 
		Vector v = new Vector(); 
		Mrecord rpc = CFile.opens("cancelform@insuredocument"); 
		rpc.start(0);  //(deptCode docCode cancelDate startNo)
		String fRI = "0"; 
		String fROW = "0"; 		
		String fR17	= "0"; 
		String fCI  = "0"; 
		String fCOW = "0"; 
		String fC17	= "0";

		System.out.println(" branch :  " + branch); 
		boolean okfind = rpc.equalGreat(branch); 
		System.out.println(" okfind : "+okfind); 
		for (; okfind && branch.compareTo(rpc.get("deptCode").trim()) == 0; okfind = rpc.next())			
		{
			System.out.println(" --->>. innerloop : "+ rpc.get("cancelDate").substring(0, 6)); 
			if (yyyymm.compareTo(rpc.get("cancelDate").substring(0, 6)) != 0)
				continue; 
			System.out.println(" --->>. status : " + rpc.get("status") + " docCode : "+rpc.get("status")); 
			if ("R".compareTo(rpc.get("status").trim()) == 0 )
			{
				if (rpc.get("docCode").trim().compareTo("01") == 0)
					fRI = M.addnum(fRI, calNum(rpc.get("startNo"), rpc.get("lastNo"))); 				
				else if (rpc.get("docCode").trim().compareTo("02") == 0 || rpc.get("docCode").trim().compareTo("03") == 0)
					fROW = M.addnum(fROW, calNum(rpc.get("startNo"), rpc.get("lastNo"))); 								
				else if (	rpc.get("docCode").trim().compareTo("17") == 0 || 
							rpc.get("docCode").trim().compareTo("22") == 0 )
					fR17 = M.addnum(fR17, calNum(rpc.get("startNo"), rpc.get("lastNo"))); 								
			}	
			else if ("C".compareTo(rpc.get("status").trim()) == 0 )
			{
				if (rpc.get("docCode").trim().compareTo("01") == 0)
					fCI = M.addnum(fCI, calNum(rpc.get("startNo"), rpc.get("lastNo"))); 											
				else if (rpc.get("docCode").trim().compareTo("02") == 0 || rpc.get("docCode").trim().compareTo("03") == 0 )
					fCOW = M.addnum(fCOW, calNum(rpc.get("startNo"), rpc.get("lastNo")));
				else if (rpc.get("docCode").trim().compareTo("17") == 0 || rpc.get("docCode").trim().compareTo("22") == 0  )
					fC17 = M.addnum(fC17, calNum(rpc.get("startNo"), rpc.get("lastNo"))); 					
			}
		}	
		v.add(fCI); 
		v.add(fCOW); 
		v.add(fRI); 
		v.add(fROW); 
		v.add(fC17); 
		v.add(fR17); 
		return v; 
	}

	public String calNum(String startNo, String lastNo) throws Exception
	{
		String num = "0"; 
		if (M.cmps(startNo, lastNo) == 0)
			num = "1"; 
		else if (M.cmps(startNo, lastNo) < 0)		
			num = M.addnum(M.subnum(lastNo, startNo), "1"); 
		System.out.println(" ----------- >> start : "+ startNo + " lastNo : " + lastNo + " num : " + num); 
		return num; 
	}
}
