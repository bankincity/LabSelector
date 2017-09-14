package rte.bl.branch.receipt;
   
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;

public class RteSearchRp implements Task
{
	BraSales bs;
 	Mrecord ftrp;
 	Mrecord firc;
 	Mrecord forc;
 	Mrecord fwrc;
	Mrecord fask; 
	Mrecord faskF; 
	Mrecord faskC; 
	Mrecord rpaskfor;
	Mrecord hisrpaskfor;
	Mrecord ff; 
	Mrecord f; 
	Mrecord fk; 
 	boolean chk;
 	String flag;
 	String branch;
 	String rpNo;
 	String time;
 	String mm;
 	String yyyy;
 	String askSaleID = "";
 	String ownerSaleID = "";
	TempMasicFile temp;
	int count = 0;
	int value;
 	int[] len = { 12,8,9,8,8,1,5,80,1,6,5,80,4};
 	String[] field = {"rpNo","policyNo","premium","requestDate","statusDate","currentStatus","depositNo","name","time","payPeriod","depOwner","nameOwner","payPeriod1"};
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object []))
 			return new Result("Invalid Parameter  : Object [] {flag,branch,rpNo}",-1);
 		Object [] parameter = (Object []) param;
 		flag = (String)parameter[0];
 		branch = (String)parameter[1];
 		rpNo = (String)parameter[2];
 		try 
 		{
 			ftrp = CFile.opens("trpctrl@receipt");
 			firc = CFile.opens("irctrl@receipt");
 			forc = CFile.opens("orctrl@receipt");
 			fwrc = CFile.opens("wrctrl@receipt");
 			rpaskfor = CFile.opens("rpaskfor@receipt");
 			hisrpaskfor = CFile.opens("hisrpaskfor@receipt");
			
 			bs= new BraSales();
 			temp = new TempMasicFile("bra"+branch+"app",field,len);
 			temp.keyField(true,true,new String [] {"rpNo"});
 			temp.buildTemp();
			searchData(flag,rpNo,branch);
			return new Result(temp.name(),0);

 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
 	}
	private boolean checkAskFor(String type,String rpNo,String reqDate,String askSaleID)
	{
		if("IX".indexOf(type) >= 0)
			type = "I";
		else if("OY".indexOf(type) >= 0)
			type = "O";
		else if("WU".indexOf(type) >= 0)
			type = "W";
			
		for (boolean st = rpaskfor.equalGreat(type+rpNo) ; st && (type+rpNo).compareTo(rpaskfor.get("typeOfPol")+rpaskfor.get("rpNo")) == 0;st=rpaskfor.next())
		{
			if(reqDate.compareTo(rpaskfor.get("requestDate")) == 0 &&askSaleID.compareTo(rpaskfor.get("askSaleID")) == 0 && (rpaskfor.get("ownerSaleID")).compareTo(rpaskfor.get("askSaleID")) != 0)
				return true;
		}
		for (boolean st = hisrpaskfor.equalGreat(type+rpNo) ; st && (type+rpNo).compareTo(hisrpaskfor.get("typeOfPol")+hisrpaskfor.get("rpNo")) == 0;st=hisrpaskfor.next())
		{
			if(reqDate.compareTo(hisrpaskfor.get("requestDate")) == 0 &&askSaleID.compareTo(hisrpaskfor.get("askSaleID")) == 0 && (hisrpaskfor.get("ownerSaleID")).compareTo(hisrpaskfor.get("askSaleID")) != 0)
				return true;
		}
		return false ;
	}
 	private String getAskID(Mrecord fask,String rpNo,String branch) throws Exception
	{
		if(fask == null)
			return "";
		fask.start(2);
		if(fask.equal(branch+rpNo))
		{
			temp.set("requestDate", fask.get("requestDate").trim());
			askSaleID = fask.get("askSaleID").trim();
			ownerSaleID = fask.get("ownerSaleID").trim();
			System.out.println(" ------------------------1-> askSaleID== "+askSaleID);
			bs.getBySalesID(askSaleID);
			System.out.println(" ------------------------2-> askSaleID== "+askSaleID);
		}
		return (askSaleID);
	}
	private void setTemp(Mrecord ff) throws Exception
	{	
		temp.set("rpNo", ff.get("rpNo").trim());
		temp.set("policyNo", ff.get("policyNo").trim());
		temp.set("currentStatus", ff.get("currentStatus").trim());
		temp.set("premium", ff.get("premium").trim());
		temp.set("time", ff.get("time").trim());
	}
	private void getSaleID() throws Exception
	{	
		bs.getBySalesID(askSaleID);
		temp.set("depositNo", bs.getSnRec("depositNo").trim());
		temp.set("name", bs.getSnRec("preName").trim()+bs.getSnRec("firstName").trim()+" "+bs.getSnRec("lastName").trim());
		bs.getBySalesID(ownerSaleID);
		temp.set("depOwner", bs.getSnRec("depositNo").trim());
		temp.set("nameOwner", bs.getSnRec("preName").trim()+bs.getSnRec("firstName").trim()+" "+bs.getSnRec("lastName").trim());
	}	
    private void searchData(String flag,String rpNo,String branch) throws Exception
 	{
		if(flag.compareTo("1") == 0)//ชั่วคราว
		{
			if(!ftrp.equal(rpNo))
			{
				ftrp = Masic.opens("histrpctrl@receipt");
				if (!ftrp.equal(rpNo))
					throw new Exception(M.stou("ไม่พบข้อมูลใบเสร็จที่ระบุ ")+rpNo);
			}
			yyyy = DateInfo.sysDate().substring(0,4);
			if(M.ctoi(DateInfo.sysDate().substring(6,8)) >= 17 )
			{
				mm = DateInfo.nextMonth(DateInfo.sysDate(),1);
				if(CFile.isFileExist("ask"+mm.substring(0,6)+"@cbranch"))
				{
					faskF = CFile.opens("ask"+mm.substring(0,6)+"@cbranch");
				}
			}
			mm = DateInfo.sysDate().substring(4,6);
			if(CFile.isFileExist("ask"+yyyy+mm+"@cbranch"))
			{
				faskC = CFile.opens("ask"+yyyy+mm+"@cbranch");
			}
			askSaleID = getAskID(faskF,rpNo,branch);
			if(askSaleID.trim().length() == 0)
			{
				askSaleID = getAskID(faskC,rpNo,branch);
			}
			if(askSaleID.trim().length() == 0)
			{
				temp.set("depositNo", "");
				temp.set("name", "");
				temp.set("depOwner", "");
				temp.set("nameOwner", "");
				temp.set("requestDate","00000000");
			}
			else
			{
				getSaleID(); 
			}
			//	setTemp(ftrp);
			temp.set("rpNo", ftrp.get("rpNo").trim());
			temp.set("policyNo", ftrp.get("policyNo").trim());
			temp.set("currentStatus", ftrp.get("currentStatus").trim());
			temp.set("premium", ftrp.get("premium").trim());
			temp.set("statusDate", ftrp.get("statusDate").trim());
  			temp.set("time","0");
  			temp.set("payPeriod","000000");
  			temp.set("payPeriod1","0000");
			temp.insert();
		
 		}
		else if(flag.compareTo("2") == 0)//รายเดือน
		{
			searchData(firc,"2");
		}
		else if(flag.compareTo("3") == 0)//รายงวด
		{
			searchData(forc,"3");
		}
		else if(flag.compareTo("4") == 0)//รายสูงอายุ
		{
			searchData(fwrc,"4");
		}
	}
	private void searchData(Mrecord f,String flag) throws Exception
	{	
		count = 0;
		for(boolean ok=f.equalGreat(rpNo);
					ok	&&	f.get("rpNo").compareTo(rpNo) == 0 ; ok=f.next())
		{
			time = f.get("time");
			setTemp(f);
			temp.set("requestDate","00000000");
			String typepol = "IX";
			if(flag == "2")
			{
  				temp.set("payPeriod",f.get("payPeriod"));
				typepol = "IX";
			}
			else{
				
				typepol = "OYWU";
  				temp.set("payPeriod1",f.get("payPeriod"));
			}
  		//	temp.set("time","0");
			temp.set("depositNo", "");
			temp.set("name", "");
			temp.set("depOwner", "");
			temp.set("nameOwner", "");
			if(time.compareTo("0") == 0)
			{
				temp.insert();
				break;					
			}
			System.out.println("bingo......................................................");
 			if(M.ctoi(DateInfo.sysDate().substring(6,8)) >= 20 )
 			{
				mm = DateInfo.nextMonth(DateInfo.sysDate(),1);
				System.out.println(" ------------------------L4->"+mm);
				yyyy = mm.substring(0,4);	
				mm = mm.substring(4,6);
			}
			else
			{
				mm =  DateInfo.sysDate().substring(4,6);
				yyyy = DateInfo.sysDate().substring(0,4);
			}
			System.out.println("yyyy mm   "+yyyy+mm);
			count = M.ctoi(f.get("time"));
			while(count > 0)
			{
	 			if(!CFile.isFileExist("ask"+yyyy+mm+"@cbranch"))
				{
		
					if(temp.fileSize() == 0)//วนแฟ้มaskจนหมด
						temp.insert();
 					return;
				}
				System.out.println(" ------------------------MM->"+mm+"YYYY"+yyyy);
 				fask = CFile.opens("ask"+yyyy+mm+"@cbranch");
				mm = DateInfo.previousMonth(yyyy+mm+"01");
				yyyy = mm.substring(0,4);
				mm  = mm.substring(4,6);

				if(fask != null)
				{
					fask.start(2);
					for (boolean con  = fask.equal(branch+rpNo) ; con &&  (branch+rpNo).compareTo(fask.get("branch")+fask.get("rpNo")) == 0;con = fask.next())
					{
						
						System.out.println("typepol..................."+typepol);
						if(typepol.indexOf(fask.get("receiptFlag")) < 0)
							continue;
						chk = true;
						count--;
						askSaleID = fask.get("askSaleID").trim();
						ownerSaleID = fask.get("ownerSaleID").trim();
						System.out.println(" askSaleID = " +  askSaleID); 
						System.out.println(" ownerSaleID = " + ownerSaleID); 
						getSaleID();
						temp.set("requestDate", fask.get("requestDate").trim());
						temp.set("statusDate", f.get("sysDate").trim());
						if(flag == "2")
  							temp.set("payPeriod",f.get("payPeriod"));
						else
  							temp.set("payPeriod1",f.get("payPeriod"));
						if(fask.get("askSaleID").compareTo(fask.get("ownerSaleID")) != 0 )
						{
							if(checkAskFor(fask.get("receiptFlag"),fask.get("rpNo"),fask.get("requestDate"),fask.get("askSaleID")))
								temp.set("name",temp.get("name")+"***");
						}
						temp.insert();
						System.out.println(" temp.fileSize() = " + temp.fileSize()); 
					}
				}
			}
		}
	}
}


