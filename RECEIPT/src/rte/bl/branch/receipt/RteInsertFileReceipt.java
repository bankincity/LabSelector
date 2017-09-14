package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import utility.cfile.*;
import java.util.Vector;

 
public class RteInsertFileReceipt implements Task
{
	Mrecord uc;
	Mrecord ucyymm;
//	Mrecord stryymm;
	public Result execute(Object obj)
	{
		Result result = null;
		boolean ch = false;
		boolean ok = false;
		try
		{
			Object[] ob = (Object[])obj;
			Vector v = (Vector)ob[0];
			String yyyymm = (String)ob[1];
			String salesID = (String)ob[2];
			String branch  = (String)ob[3];
			ok = getPositionInSales(salesID, branch, yyyymm);
			ch = InsertFileReceipt(v, yyyymm, salesID, branch);
		}
		catch(Exception e)
		{
			return (new Result(e.getMessage(), -1));
		}		
		if ( ok || ch )
			result = new Result(new Object[] {new Boolean(ok), new Boolean(ch)}, 0);
		else 
			result = new Result(new Object[] {new Boolean(ok), new Boolean(ch)}, 1);
			
		return result;
	}
	public boolean InsertFileReceipt(Vector vdata, String yyyymm, String salesID, String branch)throws Exception
	{
		ucyymm 	= CFile.openbuild("ucdetail"+yyyymm+"@cbranch");
		
		boolean ok = false;
		for ( int i = 0; i < vdata.size(); i++ )
		{
			String[] arr = (String[])vdata.elementAt(i);
		
			System.out.println("------step ----"+M.itoc(i+1));			


			ucyymm.set("salesID", salesID);     
    		ucyymm.set("receiptFlag", arr[0].trim()); 
    		ucyymm.set("rpNo", 		  arr[1].trim());        
    		ucyymm.set("policyNo", 	  arr[2].trim());    
    		ucyymm.set("payPeriod",   arr[3].trim());   
    		ucyymm.set("premium", 	  arr[5].trim());     
    		ucyymm.set("requestDate", arr[4].trim()); 
    		ucyymm.set("currentStatus", "U");   
    		ucyymm.set("payDate", "00000000");     
   	 		ucyymm.set("submitNo", "");    
    		ucyymm.set("debtBook", "");    
    		ucyymm.set("returnDebtBook", ""); 
			ok = ucyymm.insert();
		}	
		return ok;
	}
	public boolean getPositionInSales(String salesID, String branch, String yyyymm)throws Exception
	{
		uc 		= CFile.openbuild("uc"+yyyymm+"@cbranch");
		Mrecord	stryymm = CFile.opens("str"+yyyymm.substring(2)+"@sales");
			
		stryymm.start(1);
			
		String strid = "";
		String parentid = "";
		if (stryymm.equal(salesID))
		{
			strid 	 = stryymm.get("strid");
			parentid = stryymm.get("parentStrid");
			stryymm.start(0);
			boolean ok = stryymm.equal(parentid);
			while ( ok && salesID.equals(stryymm.get("salesID")) )
			{
				strid		= stryymm.get("strid");
				parentid	= stryymm.get("parentStrid");

				ok = stryymm.equal(parentid);
			}
		}
		System.out.println("-------strid  "+strid);
		uc.set("strID", strid);
		uc.set("salesID", salesID);	
		uc.set("branch", branch);	

		System.out.println("----------branch --"+branch);
		String[] arr = (String[])getSalesStruct(salesID, yyyymm);
		System.out.println("---------Arr "+arr.length);
		System.out.println("---------unitSalesID ---- "+arr[0]);
		System.out.println("---------divisionSalesID ---- "+arr[1]);
		System.out.println("---------regionSalesID ---- "+arr[2]);
		System.out.println("---------departSalesID ---- "+arr[3]);
		uc.set("unitSalesID", arr[0]);
		uc.set("divisionSalesID", arr[1]);
		uc.set("regionSalesID", arr[2]);
		uc.set("departSalesID", arr[3]);
		uc.set("bookToCancel", "0");	
		uc.set("bookToFollow",	"0");	
		uc.set("reserved", 	"");	
		return (uc.insert());		
	}
	public String[] getSalesStruct(String salesID, String yyyymm)throws Exception
	{
		Mrecord stryymm = CFile.opens("str"+yyyymm.substring(2)+"@sales");
			
		stryymm.start(1);
		String strid = "";
		String parentid = "";
		String departSalesID   = "";    
        String regionSalesID   = "";    
        String divisionSalesID = "";   
        String unitSalesID     = "";   
		if (stryymm.equal(salesID))
		{
			strid = stryymm.get("strid");
			parentid = stryymm.get("parentStrid");
		}
		if (!strid.equals("")) 
		{
			stryymm.start(0);
			if (strid.charAt(0) == 'A')	
			{			
				while (stryymm.equal(parentid)  && M.cmps(strid.substring(0, 1), "L") <= 0)
				{
					strid = stryymm.get("strid");
					parentid = stryymm.get("parentStrid");				
					if (strid.charAt(0) == 'B')
						unitSalesID = stryymm.get("salesID");
					else if (strid.charAt(0) == 'D')
					{
						if (unitSalesID.equals(""))
							unitSalesID = stryymm.get("salesID");
						divisionSalesID = stryymm.get("salesID");	
					}	
					else if (strid.charAt(0) == 'G')
					{
						if (unitSalesID.equals(""))	
							unitSalesID 	= stryymm.get("salesID");
						if (divisionSalesID.equals(""))	
							divisionSalesID = stryymm.get("salesID");	
						regionSalesID	= stryymm.get("salesID");
					}	
					else if (strid.charAt(0) == 'L')
					{
						if (unitSalesID.equals(""))	
							unitSalesID 	= stryymm.get("salesID");
						if (divisionSalesID.equals(""))	
							divisionSalesID = stryymm.get("salesID");	
						if (regionSalesID.equals(""))	
							regionSalesID	= stryymm.get("salesID");
						departSalesID	= stryymm.get("salesID");
						break;
					}					
				}
			}
			else if (strid.charAt(0) == 'B')	
			{
				unitSalesID = stryymm.get("salesID");
				while (stryymm.equal(parentid) && M.cmps(strid.substring(0, 1), "L") <= 0)
				{
					strid = stryymm.get("strid");
					parentid = stryymm.get("parentStrid");				
			
          			if (strid.charAt(0) == 'D')
            			divisionSalesID = stryymm.get("salesID");
         			else if (strid.charAt(0) == 'G')
            		{
						if (divisionSalesID.equals(""))	
							divisionSalesID = stryymm.get("salesID");
                		regionSalesID   = stryymm.get("salesID");
					}
        			else if (strid.charAt(0) == 'L')
        			{
						if (divisionSalesID.equals(""))	
             				divisionSalesID = stryymm.get("salesID");
						if (regionSalesID.equals(""))	
             				regionSalesID   = stryymm.get("salesID");
            			departSalesID   = stryymm.get("salesID");
						break;
          			}
				}
			}
			else if (strid.charAt(0) == 'D')	
			{
				unitSalesID = stryymm.get("salesID");
				divisionSalesID = stryymm.get("salesID");
				while (stryymm.equal(parentid) && M.cmps(strid.substring(0, 1), "L") <= 0)
				{
					strid = stryymm.get("strid");
					parentid = stryymm.get("parentStrid");				
           			if (strid.charAt(0) == 'G')
                   		regionSalesID   = stryymm.get("salesID");
                	else if (strid.charAt(0) == 'L')
                	{
						if (regionSalesID.equals(""))	
                    		regionSalesID   = stryymm.get("salesID");
                    	departSalesID   = stryymm.get("salesID");
						break;
                	}
				}
			}
			else if (strid.charAt(0) == 'G')	
			{
				unitSalesID = stryymm.get("salesID");
            	divisionSalesID = stryymm.get("salesID");
				regionSalesID   = stryymm.get("salesID");
				while (stryymm.equal(parentid) && M.cmps(strid.substring(0, 1), "L") <= 0)
            	{
                	strid = stryymm.get("strid");
                	parentid = stryymm.get("parentStrid");

                	if (strid.charAt(0) == 'L')
                	{
                    	departSalesID   = stryymm.get("salesID");		
						break;
                	}
				}
			}
			else if (strid.charAt(0) == 'H')		
			{
				unitSalesID = stryymm.get("salesID");
            	divisionSalesID = stryymm.get("salesID");
            	regionSalesID   = stryymm.get("salesID");	
				while (stryymm.equal(parentid) && M.cmps(strid.substring(0, 1), "L") <= 0)
            	{
               		strid = stryymm.get("strid");
                	parentid = stryymm.get("parentStrid");

                	if (strid.charAt(0) == 'L')
                	{
                    	departSalesID   = stryymm.get("salesID");
						break;
                	}
				}
			}
			else if (strid.charAt(0) == 'L')	
			{
				unitSalesID = stryymm.get("salesID");
            	divisionSalesID = stryymm.get("salesID");
            	regionSalesID   = stryymm.get("salesID");
          		departSalesID   = stryymm.get("salesID");

			}
		}	
		return (new String[] {unitSalesID, divisionSalesID, regionSalesID, departSalesID});		
	}
}
