package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import  rte.bl.branch.*;
import  rte.*;
import utility.cfile.Rtemp;
import utility.branch.sales.*;
import utility.rteutility.*;
import java.util.*;
import utility.support.DateInfo;
public class RteCheckSaleStatus implements Task
{
	Mrecord irc;
        Mrecord orc;
        Mrecord wrc;
        Mrecord trp;
        Mrecord baddep;
	String [] str = new String [9];
	String ucrpyyyymm ;
	String branchCode;
	String depNo ;
	String yyyymm;
	String reqSaleID;
	BraSales sale;
	public Result execute(Object param)
        {
		if(!(param instanceof String []))
		{
			return new Result("Invalid parameter String []",-1);
		}
		String [] parameter = (String[])param;		
		ucrpyyyymm = (String)parameter[0];
		branchCode = (String)parameter[1];
		depNo = (String)parameter[2];
		if(parameter.length == 5)
		{
			yyyymm = parameter[3];
			reqSaleID = parameter[4];
		}
		Result res = null;
		try {
			sale = new BraSales();
			getSaleRecord();
			if(parameter.length == 3)	 // check for ask sale id
				res = checkDataOfAskSaleID();
			else {
				if(ucrpyyyymm.compareTo("addition") == 0)
					res = checkDataOfOwnerSaleID(true);
				else
					res = checkDataOfOwnerSaleID();
			}
		}
		catch (Exception e)
		{
			return (new Result(e.getMessage(),-1));
		}
		return res;
	}
	// FOR ADDITION ASK RP
	public Result checkDataOfOwnerSaleID(boolean flagAddition)
	{
		String saleID = str[5];
		String [] salerec = new String [4];
		salerec[0] = str[4];
		salerec[1] = str[5];
		salerec[2] = str[6]+str[7]+" "+str[8];
		salerec[3] = "0";	
		try {
			String rpAskSaleID="";
			String trpAskSaleID="";
			Mrecord ask = CFile.opens("ask"+yyyymm+"@cbranch");
			ask.start(1);
			for (boolean st = ask.equal(branchCode+saleID); st && saleID.compareTo(ask.get("ownerSaleID")) == 0  && branchCode.compareTo(ask.get("branch")) == 0; st =ask.next())
			{
				if("IOWXYU".indexOf(ask.get("receiptFlag")) >= 0) 
					rpAskSaleID = ask.get("askSaleID");
				else
					trpAskSaleID = ask.get("askSaleID");
				if(rpAskSaleID.length() == 10 && trpAskSaleID.length() == 10)
					break;
			}
			if(rpAskSaleID.length() == 0 && trpAskSaleID.length() == 0)
				return (new Result(M.stou("ไม่มีข้อมูลการเบิก ต้องเลือก เบิกทั้งหมด"),2));
			System.out.println("rpAskSaleID= "+rpAskSaleID);
			if(rpAskSaleID.length() == 10)
			{
				if(rpAskSaleID.compareTo(saleID) == 0 )
				{
					if(rpAskSaleID.compareTo(reqSaleID) == 0) 
						salerec[3] = "3";  //can request ถาวร + ชั่วคราว
					else
						return new Result(M.stou("ฝากค้ำที่เบิกเพิ่มต้องเป็นฝากค้ำเลขที่ ")+getDepNo(saleID),1);
				}
				else if(rpAskSaleID.compareTo(reqSaleID) == 0)
						salerec[3] = "1";  //can request ถาวร 
				else if(saleID.compareTo(reqSaleID) == 0)
						salerec[3] = "2";  //can request  ชั่วคราว
				else	
					return new Result(M.stou("ฝากค้ำที่เบิกเพิ่มต้องเป็นฝากค้ำเลขที่ ")+getDepNo(rpAskSaleID),1);
				
			}
			else if(trpAskSaleID.compareTo(reqSaleID) == 0)
				salerec[3] = "3";  //can request ถาวร + ชั่วคราว
			else
				return new Result(M.stou("ฝากค้ำที่เบิกเพิ่มต้องเป็นฝากค้ำเลขที่ ")+getDepNo(trpAskSaleID),1);
			
		}
		catch (Exception e)
		{
			return (new Result(e.getMessage(),-1));
		}
		System.out.println("can request.............."+salerec[3]);
		return (new Result(salerec,0));
	}	
	private String  getDepNo(String saleID) throws Exception
	{
                sale.getBySalesID(saleID);
		return (sale.getSnRec("depositNo"));
	}
	public Result checkDataOfOwnerSaleID()
	{
		String saleID = str[5];
	/*	String [] salerec = new String [4];
		salerec[0] = str[4];
		salerec[1] = str[5];
		salerec[2] = str[6]+str[7]+" "+str[8];
		salerec[3] = "0";	*/
		try {
			String [] field = {"depNo","pname","fname","lname","status","everRequest","badSale","inUcrp","saleID","expireDate"};
			int [] len = {5,30,30,30,1,1,1,1,10,8};
			Rtemp temp = new Rtemp(field,len);
			Record rec = temp.copy();
			rec.set('0');
			rec.set("depNo",depNo);
			rec.set("pname",str[6]);
			rec.set("fname",str[7]);
			rec.set("lname",str[8]);
			System.out.println("bingo...........................................");
			String [] lic = getLicence(str[1]);
			rec.set("expireDate",lic[1]);
			rec.set("saleID",saleID);
			rec.set("status","N");

			System.out.println("bingo..........................................."+yyyymm+"  "+ucrpyyyymm+"   "+saleID+"  "+reqSaleID);
			Mrecord ask = CFile.opens("ask"+yyyymm+"@cbranch");
			Mrecord ucrp = CFile.opens(ucrpyyyymm+"@cbranch");
               		baddep = CFile.opens("baddep@cbranch");
               		irc = CFile.opens("irctrl@receipt");
               		orc = CFile.opens("orctrl@receipt");
               		wrc = CFile.opens("wrctrl@receipt");
               		trp = CFile.opens("trpctrl@receipt");
			boolean samedep = saleID.compareTo(reqSaleID) == 0 ;
			System.out.println("bingo...........................................");
			if(CheckAskReceipt.inAskYYMM(ask ,saleID,reqSaleID , 1,false,branchCode) != 0)
			{
				// ever request -->  can  request only temp receipt
				if(samedep && CheckAskReceipt.inAskYYMM(ask ,saleID,reqSaleID , 1,true,branchCode) == 0)
					return new Result(rec.getBytes(),1);
				else  // erver request --> can request temp receipt and receipt
					return new Result(rec.getBytes(),2);

			}
			else {
				if(CheckAskReceipt.checkDepno(ucrp,saleID,0,false,irc,orc,wrc,trp,branchCode) ||
					CheckAskReceipt.checkDepno(ucrp,saleID,1,false,irc,orc,wrc,trp,branchCode))
                		{
                        		rec.set("inUcrp","1");
                		}

				System.out.println("before...........baddep................depNo------------>"+depNo);
				if(isBadDep(depNo,branchCode))
				{
					System.out.println("baddep................depNo------------>"+depNo);
					rec.set("badSale","1");
				}
				return new Result(rec.getBytes(),0);
			}

		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),9);
		}
	}	
	public void  getSaleRecord() throws Exception
	{
                sale.getByDepositNo(branchCode,depNo);
		if(sale.getSnRec("status").charAt(0) == 'R')
			throw new Exception(M.stou("ฝ่ายขายลาออกแล้ว"));
				
		str[1] = sale.getSnRec("citizenID");
		str[2] = sale.getSnRec("strID");
		str[4] = depNo;
                str[5] = sale.getSnRec("salesID");
                str[6] = sale.getSnRec("preName");
                str[7] = sale.getSnRec("firstName");
                str[8] = sale.getSnRec("lastName");
	}
	public Result checkDataOfAskSaleID()
	{
		if(str[2].charAt(0) == 'A' &&  str[2].charAt(str[2].length() -1) == 'T')
			return new Result(M.stou("ฝ่ายขายยังเป็นตัวแทนฝึกหัด"),1);
		String saleID = str[5];
		String [] salerec = new String [4];
		salerec[0] = str[4];
		salerec[1] = str[5];
		salerec[2] = str[6]+str[7]+" "+str[8];
		salerec[3] = "";	
			
		try {	
			Mrecord ucrp = CFile.opens(ucrpyyyymm+"@cbranch");
               		baddep = CFile.opens("baddep@cbranch");
               		irc = CFile.opens("irctrl@receipt");
               		orc = CFile.opens("orctrl@receipt");
               		wrc = CFile.opens("wrctrl@receipt");
               		trp = CFile.opens("trpctrl@receipt");
			String [] lic = getLicence(str[1]);
			if(lic[0].compareTo("0000000000") == 0 ||  lic[1].compareTo(DateInfo.sysDate()) < 0)
				return new Result(M.stou("ฝ่ายขายคนนี้เป็นผู้เบิกใบเสร็จไม่ได้(ไม่มีใบอนุญาต หรือ ใบอนุญาตหมดอายุ)"),1);
			if(isBadDep(depNo,branchCode))
				return new Result(M.stou("ฝ่ายขายคนนี้ไม่มีสิทธิเบิกใบเสร็จ"),1);
			if(CheckAskReceipt.checkDepno(ucrp,saleID,0,false,irc,orc,wrc,trp,branchCode))
			{
				salerec[3] = "0";
				return (new Result(salerec,2));
			
			}	
			else if (CheckAskReceipt.checkDepno(ucrp,saleID,1,false,irc,orc,wrc,trp,branchCode))
               		{
				salerec[3] = "1";
				return (new Result(salerec,2));
			}
			return new Result(salerec,0);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),9);
		}
        }
	public boolean  isBadDep(String depNo,String branch) throws Exception
	{ 
                return baddep.equal(branch+depNo);
        }
	public String [] getLicence(String citizenID)
        {
                Vector vlc = null;
                try {
			System.out.println("bingo..........licence");
                        vlc = SalesService.getLicenseByCitizenID(citizenID);
			System.out.println("bingo..........licence");
                }
                catch (Exception e)
                {
                        return (new String []{"0000000000","00000000"});
                }
                if (vlc != null && vlc.size() > 0)
                {
                        String [] slc = (String [])vlc.elementAt(0);
			slc[5] = slc[5].trim();
			int len = slc[5].length();
			if(len  == 0 || M.itis(slc[5],'0')) 
                        	return (new String []{"0000000000","00000000"});
                        return (new String [] {slc[5],slc[7]});
                      /*  snRec.set("licenseNo", slc[5]);
                        snRec.set("licenseExpireDate", slc[7]);*/
                }
                return (new String []{"0000000000","00000000"});
        }
}
