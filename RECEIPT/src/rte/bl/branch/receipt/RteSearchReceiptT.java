package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;
public class RteSearchReceiptT implements Task
{
	BraSales bs ;
	Mrecord ftrp;
	Mrecord faskC;
	Mrecord faskF;
	String yyyy;
	String mm;
	String branch;
	boolean chk;
	String rpNo;
	String rp;
	String flag;
	String policy;
	String pol;
	String sys;
	String statusDate;
	String askSaleID = "";
 	TempMasicFile temp;
  	int[] len     = { 12,8,9,8,8,1,1,1,5,7,7,12,3,10,5,80};
	String[] field  = {"rpNo","policyNo","premium","requestDate","statusDate","currentStatus","originalStatus","branchFlag","seqBook","clearUserID","askUserID","submitNo","reserve","askSaleID","depositNo","name"}; 

 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {rpNo,branch,flag,policy.statusDate}",-1);
 		Object [] parameter = (Object []) param;
 		rpNo = (String)parameter[0];
 		branch = (String)parameter[1];
 		flag = (String)parameter[2];
 		policy = (String)parameter[3];
 		statusDate = (String)parameter[4];
		try
 		{
 			ftrp = CFile.opens("trpctrl@receipt");
			bs = new BraSales();
 			temp = new TempMasicFile("bra"+branch+"app",field,len);
			temp.keyField(false,false,new String [] {"rpNo"});
 			temp.buildTemp();
			System.out.println("flag" + flag); 
			if(M.cmps(flag,"1") == 0)//next
			{
				ftrp.last();
				rp = ftrp.get("rpNo"); 
				pol = ftrp.get("policyNo"); 
				sys = ftrp.get("statusDate"); 
				if(rpNo.compareTo(rp) == 0 && policy.compareTo(pol) == 0 && statusDate.compareTo(sys) == 0)
				{
					throw new Exception(M.stou("ท้ายแฟ้ม"));
				}
				else
				{
					if(rpNo.trim().length() == 0)
					{
						ftrp.first();
						System.out.println("rpNo*if*first"+rpNo);	
						System.out.println("policy"+ftrp.get("policyNo"));	
						rpNo = ftrp.get("rpNo");
 						loadData();
					}
					else
					{
						for(boolean ss = ftrp.equalGreat(rpNo); ss ;ss = ftrp.next())
						{
							if(policy.compareTo(ftrp.get("policyNo")) == 0 
							&& statusDate.compareTo(ftrp.get("statusDate"))==0)
								break;						
						}
						if(!ftrp.next())
							ftrp.last();
						System.out.println(" === rpNo " + ftrp.get("rpNo")); 
						System.out.println(" === policyNo " + ftrp.get("policyNo")); 
						System.out.println(" After Break === " + ftrp.get("policyNo")); 
						loadData(); 
					}
				}
			}
			else if(M.cmps(flag,"2")==0)//previous
			{
				ftrp.first();
				rp  = ftrp.get("rpNo");
				pol = ftrp.get("policyNo");
				sys = ftrp.get("statusDate"); 
				if((rpNo.compareTo(rp) == 0 && policy.compareTo(pol) == 0) && statusDate.compareTo(sys) == 0 || rpNo.trim().length() == 0)
				{
						System.out.println("***** in IF");
					throw new Exception(M.stou("ต้นแฟ้ม"));
				}
				else
				{
					for(boolean ss = ftrp.equalGreat(rpNo); ss ;ss = ftrp.next())
					{
						if(policy.compareTo(ftrp.get("policyNo")) == 0 
						&& statusDate.compareTo(ftrp.get("statusDate"))==0)
							break;						
					}
					if(!ftrp.previous())
						ftrp.first();
					loadData(); 


				}
			}
			else if(M.cmps(flag,"3")==0)//search
			{
 				if(ftrp.equal(rpNo))
 					loadData();
			}	
          	return new Result(temp.name(),0);
		}
 		catch (Exception e)
 		{ 
 			return new Result(e.getMessage(),2);
 		}
 	}
    private String getAskID(Mrecord fask,String rpNo,String branch) throws Exception
	{
		if(fask == null)
			return "";
		fask.start(2);
		if(fask.equal(branch+rpNo))
		{ 
			askSaleID = fask.get("askSaleID").trim();
			System.out.println(" ------------------------1-> askSaleID== "+askSaleID); 
			bs.getBySalesID(askSaleID);
			System.out.println(" ------------------------2-> askSaleID== "+askSaleID); 
		}
		return (askSaleID);
	}
 	private void  loadData() throws Exception
 	{
			yyyy = ftrp.get("requestDate").substring(0,4);
			if(M.ctoi(ftrp.get("requestDate").substring(6,8)) >= 17 )
			{
				mm = DateInfo.nextMonth(ftrp.get("requestDate"),1);
				if(CFile.isFileExist("ask"+mm.substring(0,6)+"@cbranch"))
				{
 					faskF = CFile.opens("ask"+mm.substring(0,6)+"@cbranch");
				}
			}
			mm = ftrp.get("requestDate").substring(4,6);
			if(CFile.isFileExist("ask"+yyyy+mm+"@cbranch"))
			{
 				faskC = CFile.opens("ask"+yyyy+mm+"@cbranch");
			}
			if(ftrp.get("requestDate").compareTo("00000000") != 0)
			{
				askSaleID = getAskID(faskF,rpNo,branch);
				if(askSaleID.trim().length() == 0)
				{
					chk = true;
					askSaleID = getAskID(faskC,rpNo,branch);
				}
				if(askSaleID.trim().length() == 0)
				{
					chk = false;
					temp.set("depositNo", "");
					temp.set("name", "");
				}
				else
				{	
					bs.getBySalesID(askSaleID);
					temp.set("depositNo", bs.getSnRec("depositNo").trim());
					temp.set("name", bs.getSnRec("preName").trim()+bs.getSnRec("firstName").trim()+" "+bs.getSnRec("lastName").trim());
				}
			}
			else
			{
				temp.set("depositNo", "");
				temp.set("name", "");
			}
			temp.set("askUserID", ftrp.get("askUserID").trim());
			temp.set("clearUserID", ftrp.get("clearUserID").trim());
			temp.set("rpNo", ftrp.get("rpNo").trim());
			temp.set("policyNo", ftrp.get("policyNo").trim());
			temp.set("statusDate", ftrp.get("statusDate").trim());
			temp.set("currentStatus", ftrp.get("currentStatus").trim());
			temp.set("seqBook", ftrp.get("seqBook").trim());
			temp.set("premium", ftrp.get("premium").trim());
			temp.set("requestDate", ftrp.get("requestDate").trim());
			
 			temp.insert();

 	}
}

