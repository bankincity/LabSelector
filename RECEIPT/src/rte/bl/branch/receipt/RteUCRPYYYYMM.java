package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import rte.bl.branch.TempMasicFile;
import rte.*;
import java.util.*;

public class RteUCRPYYYYMM  implements Task
{
	UCRPYYYYMM ucrp;
	TempMasicFile temp;
	String [] fileName ;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {action,yyyymm}",-1);
		Object [] parameter = (Object []) param;
		String remote = (String)parameter[0];
		String yyyymm = (String)parameter[2];
		String action  = (String)parameter[1];
		try {
			ucrp = new UCRPYYYYMM();
			System.out.println("action============="+action+"  "+yyyymm);
			if(action.charAt(0) == 'S')
			{
				Vector vec = (Vector)parameter[3];
				fileName = new String [1];
				fileName [0] = RteRpt.getTempName("rptbranch");
				temp = ucrp.searchStructUCRP(yyyymm,vec);
				System.out.println("temp........."+temp.fileSize());
                        	rte.RteRpt.recToTemp(temp, fileName[0]);
				temp.close();				
			}
			else if(action.charAt(0) == 'I')
			{
				String branch = (String)parameter[3];
				String backward = (String)parameter[4];
				fileName = new String [1];
				fileName [0] = RteRpt.getTempName("rptbranch");
//				temp = ucrp.search
				temp = ucrp.searchSummaryUCRPForInsure(yyyymm,backward,branch);
				System.out.println("temp........."+temp.fileSize());
                        	rte.RteRpt.recToTemp(temp, fileName[0]);
				temp.close();
			}
			else if (action.charAt(0) == 'T')
			{
				String strID = (String)parameter[3];
				fileName = new String [2];
				fileName [0] = RteRpt.getTempName("rptbranch");
				TempMasicFile [] tempuc = ucrp.searchDetailUCRPForInsure(yyyymm,strID);
				if(tempuc[0].fileSize() == 0)
				{
					tempuc[0].close();
					throw new Exception(M.stou("ไม่มีข้อมูลของ strID =")+strID); 
				}
                        	rte.RteRpt.recToTemp(tempuc[0], fileName[0]);
				tempuc[0].close();
				fileName [1] = RteRpt.getTempName("rptbranch");
                        	rte.RteRpt.recToTemp(tempuc[1], fileName[1]);
				tempuc[1].close();		
			}
			else if(action.charAt(0) == 'A')
			{
				String branch = (String)parameter[3];
				fileName = new String [1];
				fileName [0] = RteRpt.getTempName("rptbranch");
				temp = ucrp.searchSummaryUCRP(yyyymm,branch);			
                        	System.out.println(" fileName=" + fileName[0]);
                        	rte.RteRpt.recToTemp(temp, fileName[0]);
				temp.close();
			}
			else if (action.charAt(0) == 'B')
			{
				String branch = (String)parameter[3];
				fileName = new String [2];
				fileName [0] = RteRpt.getTempName("rptbranch");
				TempMasicFile [] tempuc = ucrp.searchUCRPForBranch(yyyymm,branch);
				if(tempuc[0].fileSize() == 0)
				{
					tempuc[0].close();
					throw new Exception(M.stou("ไม่มีข้อมูลขาดค้าง"));
				}
                        	rte.RteRpt.recToTemp(tempuc[0], fileName[0]);
				tempuc[0].close();
				fileName [1] = RteRpt.getTempName("rptbranch");
                        	rte.RteRpt.recToTemp(tempuc[1], fileName[1]);
				tempuc[1].close();		
			}
			else if (action.charAt(0) == 'D')
			{
				String strID = (String)parameter[3];
				fileName = new String [2];
				fileName [0] = RteRpt.getTempName("rptbranch");
				TempMasicFile [] tempuc = ucrp.searchDetailUCRP(yyyymm,strID);
				if(tempuc[0].fileSize() == 0)
				{
					tempuc[0].close();
					throw new Exception(M.stou("ไม่มีข้อมูลของ strID =")+strID); 
				}
                        	rte.RteRpt.recToTemp(tempuc[0], fileName[0]);
				tempuc[0].close();
				fileName [1] = RteRpt.getTempName("rptbranch");
                        	rte.RteRpt.recToTemp(tempuc[1], fileName[1]);
				tempuc[1].close();		
			}
			else if (action.charAt(0) == 'd')
			{
				String strID = (String)parameter[3];
				fileName = new String [2];
				fileName [0] = RteRpt.getTempName("rptbranch");
				TempMasicFile [] tempuc = ucrp.searchDetailUCRP(yyyymm,strID,true);
				if(tempuc[0].fileSize() == 0)
				{
					tempuc[0].close();
					throw new Exception(M.stou("ไม่มีข้อมูลของ strID =")+strID); 
				}
                        	rte.RteRpt.recToTemp(tempuc[0], fileName[0]);
				tempuc[0].close();
				fileName [1] = RteRpt.getTempName("rptbranch");
                        	rte.RteRpt.recToTemp(tempuc[1], fileName[1]);
				tempuc[1].close();	
                        }	
			else if (action.charAt(0) == '4')
			{
				String branch = (String)parameter[3];
				String strID = (String)parameter[4];
				fileName = new String [1];

				fileName [0] = RteRpt.getTempName("rptbranch");
                        	System.out.println(" fileName=" + fileName[0]);
				temp = ucrp.getForTV04(yyyymm,branch,strID);			
                        	System.out.println(" fileName=" + fileName[0]+"   "+temp.fileSize());
                        	rte.RteRpt.recToTemp(temp, fileName[0]);

				temp.close();
			}
			else if (action.charAt(0) == 'U')
			{
				String branch = (String)parameter[3];
				String lcode = (String)parameter[4];
				String gcode = (String)parameter[5];
				fileName = new String [1];
                        	System.out.println(" action----------------"+action);

				fileName [0] = RteRpt.getTempName("rptbranch");
                        	System.out.println(" fileName=" + fileName[0]);
				temp  = ucrp.getForUnclear(yyyymm,branch,lcode,gcode);		
                        	rte.RteRpt.recToTemp(temp, fileName[0]);
				temp.close();
			}
/*			else if (action.charAt(0) == 'U')
			{
				String branch = (String)parameter[3];
				String lcode = (String)parameter[4];
				String gcode = (String)parameter[5];
				fileName = new String [2];
                        	System.out.println(" action----------------"+action);

				fileName [0] = RteRpt.getTempName("rptbranch");
                        	System.out.println(" fileName=" + fileName[0]);
				TempMasicFile []  tt  = ucrp.getForUnclear(yyyymm,branch,lcode,gcode);		
				fileName [1] = RteRpt.getTempName("rptbranch");
                        	rte.RteRpt.recToTemp(tt[0], fileName[0]);
                        	rte.RteRpt.recToTemp(tt[1], fileName[1]);

				tt[0].close();
				tt[1].close();
			}*/
			else if (action.charAt(0) == 'F')
			{
				System.out.println(" ------ Action.charAt(0) : " + action.charAt(0) + "" ); 
				fileName 	 = new String [1];
				fileName [0] = RteRpt.getTempName("rptbranch");
	        	System.out.println(" fileName=" + fileName[0]);
				String month = (String)parameter[2]; 				
				System.out.println(" --- >> Month : " + month); 
				temp 		 = ucrp.searchDetailUCRP(month); 
				System.out.println(" Ans. temp.fileSize() = " + temp.fileSize()); 
               	rte.RteRpt.recToTemp(temp, fileName[0]);				
			}
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result(fileName,0);
	}	
}
