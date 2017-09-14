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

public class  SearchRpStatusCGZYDetail implements InterfaceRpt
{	
	TempMasicFile temp; 	
	String branch = ""; 
	Mrecord orRp  = null; 
	Mrecord irRp  = null; 
	Mrecord wlRp  = null; 
	public SearchRpStatusCGZYDetail() throws Exception
	{
		System.out.println(" ============ searchRpStatusCGZYDetailBUG ============ "); 
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
		String branch  = args[3]; 
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

	public void startSearch(String branch, String yyyymm)throws Exception
	{
		String [] field = {"branch", "typeOfPolicy", "typeOfstatus", "numOfRec", "rpNo", "sysDate", "form"};
		int    [] flen  = {3, 1, 1, 5, 12, 8, 1};
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
			
				case 'U' :  rp  = CFile.opens("ulrctrl@universal"); 
							break; 

				case 'L' :  rp  = CFile.opens("uliprctrl@unitlink"); 
							break; 
			}
			rp.start(4); // CGZY - (currentStatus submitBranch)
			while (inds <= 3)
			{
				String status = indexStatus.charAt(inds)+""; 
				System.out.println(" ******************* loop inds : "+inds + " status : "+ status);
				String typeOfPolicy = index+"";  
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
System.out.println(" -- >>> 2.3 sysDate: " + rp.get("sysDate")); 
System.out.println(" -- >>> 2.2 status : "+ status + " currentstatus : " +rp.get("currentStatus").trim() + " rpNo " + rp.get("rpNo")); 	
						if (rp.get("rpNo").charAt(0) == 'C' || rp.get("rpNo").charAt(0) == 'D' || rp.get("rpNo").charAt(0) == 'B')
							continue; 

						if (status.compareTo(rp.get("currentStatus").trim()) ==  0 )
						{
							count = M.addnum(count, "1"); 
							temp.set("branch", 		 rp.get("submitBranch")); 
							temp.set("typeOfPolicy", typeOfPolicy); 
							temp.set("typeOfstatus", status); 
							temp.set("numOfRec", 	 count); 
							temp.set("rpNo", 	 	 rp.get("rpNo"));									
							temp.set("sysDate", 	 rp.get("sysDate")); 
							temp.set("form",         ""); 
							temp.insert(); 
						}
					}
					System.out.println ("SUM : " + day  + " status = " + status + " count = " + count); 
				}							
				System.out.println ("***SUMALL :  status = " + status + " count = " + count); 
				inds = inds+1;
				System.out.println(" finished typeOf policy : "+ typeOfPolicy + " inds : "+inds); 
			}
		}		
		loadCancelForm(branch, yyyymm); 
		System.out.println(" ##### temp.fileSize() = " + temp.fileSize()); 
		for (boolean st = temp.first(); st; st = temp.next())
			System.out.println(" typeOfPolicy : " + temp.get("typeOfPolicy") + " status : "+temp.get("typeOfstatus") + " num : "+ temp.get("numOfRec") + " sysDate : "+ temp.get("sysDate") ); 
	}	


	public void loadCancelForm(String branch, String yyyymm)throws Exception
	{		
		System.out.println(" innser gtRPCancelForm :  "+ yyyymm); 
		Vector v = new Vector(); 
		Mrecord rpc = CFile.opens("cancelform@insuredocument"); 
		rpc.start(0);  //(deptCode docCode cancelDate startNo)
		System.out.println(" branch :  " + branch); 
		boolean okfind = rpc.equalGreat(branch); 
		System.out.println(" okfind : "+okfind); 
		for (; okfind && branch.compareTo(rpc.get("deptCode").trim()) == 0; okfind = rpc.next())			
		{
			System.out.println(" --->>. innerloop : "+ rpc.get("cancelDate").substring(0, 6)); 
			if (yyyymm.compareTo(rpc.get("cancelDate").substring(0, 6)) != 0)
				continue; 
			System.out.println(" --->>. status : " + rpc.get("status") + " docCode : "+rpc.get("docCode")); 
			if ("R".compareTo(rpc.get("status").trim()) == 0 )
			{				
				if (rpc.get("docCode").trim().compareTo("01") == 0)				
					insertTemp(branch, "I", "R", rpc.get("startNo"), rpc.get("lastNo"), rpc.get("cancelDate")); 
				else if (rpc.get("docCode").trim().compareTo("02") == 0 || rpc.get("docCode").trim().compareTo("03") == 0)
					insertTemp(branch, "O", "R", rpc.get("startNo"), rpc.get("lastNo"), rpc.get("cancelDate")); 			
				else if (rpc.get("docCode").trim().compareTo("17") == 0 || rpc.get("docCode").trim().compareTo("22") == 0 )
					insertTemp(branch, "N", "R", rpc.get("startNo"), rpc.get("lastNo"), rpc.get("cancelDate")); 			

			}	
			else if ("C".compareTo(rpc.get("status").trim()) == 0 )
			{
				if (rpc.get("docCode").trim().compareTo("01") == 0)
					insertTemp(branch, "I", "C", rpc.get("startNo"), rpc.get("lastNo"), rpc.get("cancelDate")); 			
				else if (rpc.get("docCode").trim().compareTo("02") == 0|| rpc.get("docCode").trim().compareTo("03") == 0)
					insertTemp(branch, "O", "C", rpc.get("startNo"), rpc.get("lastNo"), rpc.get("cancelDate"));
				else if (rpc.get("docCode").trim().compareTo("17") == 0 || rpc.get("docCode").trim().compareTo("22") == 0 )
					insertTemp(branch, "N", "C", rpc.get("startNo"), rpc.get("lastNo"), rpc.get("cancelDate")); 			
			}
		}	
	}

	public void insertTemp(String branch, String typeOfPolicy, String typeOfstatus, String startNo, String lastNo, 
						   String cancelDate)throws Exception
	{		
		while (startNo.compareTo(lastNo) <= 0)
		{
			temp.set("branch", 		 branch); 
			temp.set("typeOfPolicy", typeOfPolicy); 
//			temp.set("typeOfstatus", "N"); 
			temp.set("typeOfstatus", typeOfstatus); 
			temp.set("numOfRec", "1"); 
			temp.set("rpNo", 	 startNo); 
			temp.set("sysDate",  cancelDate); 
			temp.set("form",    "F"); 
			temp.insert(); 	
			System.out.println(" ````````````````insert : "+startNo); 
			startNo = M.inc(startNo); 
		}
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
