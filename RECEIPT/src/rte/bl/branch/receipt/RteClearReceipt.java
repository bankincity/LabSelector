package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import utility.support.MyVector;
public class RteClearReceipt implements Task
{
	Mrecord trptmp;
	Mrecord trptmptakaful;
	Mrecord orc;
	Mrecord irc;
	Mrecord trp;
	Mrecord wrc;
	String userID;
	String sysdate;
	String systime;
	String branch;
	Mrecord xrcchg;
	Mrecord rpaskfor;
        Mrecord hisrpaskfor;
        Mrecord policynotreq;

	MyVector vcancel ;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {yyyymm,ucrpyyyymm,userID,branch,saleID,MyVector}",-1);
		Object [] parameter = (Object []) param;
		
		String yyyymm = (String)parameter[0];
		String ucrpyyyymm = (String)parameter[1];
		branch = (String)parameter[2];
		userID =(String)parameter[3];
		String saleID = (String)parameter[4];
		MyVector vec = (MyVector)parameter[5];
		String byKey = (String)parameter[6];
		if(parameter.length == 8)
			vcancel  = (MyVector)parameter[7]; 
		System.out.println("vec pass to RteClearReceipt   size ------>"+vec.size()+"  "+yyyymm+"  "+ucrpyyyymm);
		sysdate = DateInfo.sysDate();
		systime =  Masic.time("commontable").substring(8);
		try {
			Mrecord  ask = null;
			if(yyyymm.trim().length() == 6)
				ask = CFile.opens("ask"+yyyymm+"@cbranch");
			Mrecord  ucrp = CFile.opens(ucrpyyyymm+"@cbranch");
			orc = CFile.opens("orctrl@receipt");
			irc = CFile.opens("irctrl@receipt");
			wrc = CFile.opens("wrctrl@receipt");
			trp = CFile.opens("trpctrl@receipt");
			trptmp = CFile.opens("trptmp@cbranch");
			trptmptakaful  = CFile.opens("trptmptakaful@cbranch");
			xrcchg = CFile.opens("xrpchg@cbranch");
			rpaskfor = CFile.opens("newrpaskfor@receipt");
                        hisrpaskfor = CFile.opens("hisnewrpaskfor@receipt");
			policynotreq = CFile.opens("policynotreqrp@receipt");
			if(ask != null)
			{
				if(CheckAskReceipt.inAskYYMM(ask , saleID ,"" ,1,false,branch) != 0 )
					clearReceipt(ask,vec,M.ctoi(byKey),saleID);
			}	
			clearReceipt(ucrp,vec,M.ctoi(byKey),saleID);
			return new Result("",0);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
	}
	private void insertDataToPolicyNotReq(String ttrp)
	{
		String res = getCancelReason(ttrp.substring(0,14),ttrp.substring(15,16));
		String policyNo = res.substring(0,8);
		String saleID = res.substring(8,18);
		String reason = res.substring(18);
		policynotreq.set("branch",branch);
		policynotreq.set("policyNo",policyNo);
		policynotreq.set("used","U");
		policynotreq.set("salesID",saleID);
		policynotreq.set("userID",userID);
		policynotreq.set("remark",reason+"^^^");
		policynotreq.set("recordDate",DateInfo.sysDate());
		policynotreq.insert();
		
	}
	private String  getCancelReason(String strrp,String cancelstatus) 
	{
		if(vcancel == null)
                        return "00";
                String rp = "";
                int len = strrp.length();
                for (int i =0; i < vcancel.size();i++)
                {
                        rp = (String)vcancel.elementAt(i);
                        if(strrp.compareTo(rp.substring(0,len)) == 0 && cancelstatus.charAt(0) == rp.charAt(len))
                        {
                                return rp.substring(len+1);
                        }
                }
                return "00";
	}
	private void clearReceipt(Mrecord ask,MyVector tvec,int index,String saleID) throws Exception
	{
		boolean st,findFlag ;
		String trpno="";
		String reasonCode ="00";
		char status = ' ';
		if (ask.start(2) == false)
			throw new Exception("ไม่สามารถ เปลี่ยน Key แฟ้มข้อมูล ASKYYMM เป็น Key   2  ได้ ");
		for (int i = 0 ; i < tvec.size() ; i++)
		{
			trpno = (String)tvec.elementAt(i);
			if(trpno.substring(15,16).charAt(0) != 'G' && trpno.substring(15,16).charAt(0) != ' ')
			{
				reasonCode = getCancelReason(trpno.substring(0,14),trpno.substring(15,16));
			}
			else {
				reasonCode = "00";
			}
			st = ask.equal(branch+trpno.substring(2,14));
			findFlag = false ;
			System.out.println("trpNo =="+trpno+"rp----->"+branch+trpno.substring(2,14)+" st==== "+st);
			while (st == true && (ask.get("branch")+ask.get("rpNo")).compareTo(branch+trpno.substring(2,14)) == 0)
			{
				System.out.println("trpNo == "+trpno+" i-------------"+i);
				status = ask.get("receiptFlag").charAt(0);
				if (status != trpno.charAt(0) || (index == 0  &&  saleID.compareTo(ask.get("askSaleID")) != 0) || (index == 1   &&  saleID.compareTo(ask.get("ownerSaleID")) != 0))
				{
					st = ask.next();
					continue;
				}
				switch (status){
					case 'I' : if (CheckAskReceipt.checkUCStatus(irc,ask.get("rpNo"),ask.get("requestDate")))
						 {
							System.out.println("found in irc");
							if (trpno.charAt(15) != ' ') 
							{
								if(reasonCode.compareTo("00") != 0)
									irc.set("reasonCode",reasonCode);
								insertXrcchg(xrcchg,'I',irc.copy(),trpno.substring(15,16));
             							irc.set("currentStatus",trpno.substring(15,16));
								irc.set("sysDate",sysdate);
								irc.set("userID",userID);
							//	if(reasonCode.compareTo("00") != 0)
							//		irc.set("reasonCode",reasonCode);
 							}
							else {
								irc.set("requestDate","00000000");
								if("UD".indexOf(irc.get("currentStatus")) >= 0)
									irc.set("currentStatus",irc.get("originalStatus"));
							}
							irc.update();
							deleteRpAskFor(ask.get("receiptFlag"),ask.get("rpNo"),ask.get("requestDate"));
							ask.set("receiptFlag","X");
							ask.update();
							if(trpno.charAt(15) == 'G')
							{
								insertDataToPolicyNotReq(trpno);
							}
      							findFlag = true ;
						 }
						 break;
					case 'O' : if (CheckAskReceipt.checkUCStatus(orc,ask.get("rpNo"),ask.get("requestDate")))
						 {
							System.out.println("found in orc");
							if (trpno.charAt(15) != ' ') 
							{
								if(reasonCode.compareTo("00") != 0)
									orc.set("reasonCode",reasonCode);
								insertXrcchg(xrcchg,'O',orc.copy(),trpno.substring(15,16));
								orc.set("currentStatus",trpno.substring(15,16));
                      						orc.set("sysDate",sysdate);
								orc.set("userID",userID);
					//			if(reasonCode.compareTo("00") != 0)
					//				orc.set("reasonCode",reasonCode);
							}
							else {
								orc.set("requestDate","00000000");
								if("UD".indexOf(orc.get("currentStatus")) >= 0)
									orc.set("currentStatus",orc.get("originalStatus"));
							}
            						orc.update();
							deleteRpAskFor(ask.get("receiptFlag"),ask.get("rpNo"),ask.get("requestDate"));
        						ask.set("receiptFlag","Y");
							ask.update();
							if(trpno.charAt(15) == 'G')
							{
								insertDataToPolicyNotReq(trpno);
							}
							findFlag = true ;
						}
						break ;
					case 'W' : if (CheckAskReceipt.checkUCStatus(wrc,ask.get("rpNo"),ask.get("requestDate")))
						 {
							System.out.println("found in wrc");
							if (trpno.charAt(15) != ' ') 
							{
								if(reasonCode.compareTo("00") != 0)
									wrc.set("reasonCode",reasonCode);
								insertXrcchg(xrcchg,'W',wrc.copy(),trpno.substring(15,16));
								wrc.set("currentStatus",trpno.substring(15,16));
                      						wrc.set("sysDate",sysdate);
								wrc.set("userID",userID);
					//			if(reasonCode.compareTo("00") != 0)
					//				wrc.set("reasonCode",reasonCode);
							}
							else {
								wrc.set("requestDate","00000000");
								if("UD".indexOf(wrc.get("currentStatus")) >= 0)
									wrc.set("currentStatus",wrc.get("originalStatus"));
							}
            						wrc.update();
							deleteRpAskFor(ask.get("receiptFlag"),ask.get("rpNo"),ask.get("requestDate"));
        						ask.set("receiptFlag","U");
							ask.update();
							if(trpno.charAt(15) == 'G')
							{
								insertDataToPolicyNotReq(trpno);
							}
							findFlag = true ;
						}
						break ;
					case 'T' : if (CheckAskReceipt.checkUCStatus(trp,ask.get("rpNo"),ask.get("requestDate")))
						 {
							System.out.println("found in trp");
							if (trpno.charAt(15) != ' ') {
								insertXrcchg(xrcchg,'T',trp.copy(),trpno.substring(15,16));
								trp.set("currentStatus",trpno.substring(15,16));
								trp.set("statusDate",sysdate);
							}
							else {	
								trp.set("requestDate","00000000");
								if("UD".indexOf(trp.get("currentStatus")) >= 0)
									trp.set("currentStatus",trp.get("originalStatus"));
					
                                                                ReceiptSaleStruct rss = new ReceiptSaleStruct();
	
                                                                 String tbranch = branch;
                                                                if (branch.compareTo("007") == 0)
                                                                {
                                                                        if (Receipt.sai6.compareTo(rss.getSai(ask.get("ownerSaleID")) )== 0 || Receipt.sai3.compareTo(rss.getSai(ask.get("ownerSaleID"))) == 0)
                                                                                tbranch = "A07";
                                                                }
	
								if(ask.get("rpNo").charAt(0) == '9')
								{
									trptmptakaful.set("branch",tbranch);
									trptmptakaful.set("rpNo",ask.get("rpNo"));
									trptmptakaful.set("returnDate",sysdate);
									trptmptakaful.set("userID",userID);
									trptmptakaful.set("ownerSaleID",ask.get("ownerSaleID"));
									trptmptakaful.insert();			
							
								}
								else {
									trptmp.set("branch",tbranch);
									trptmp.set("rpNo",ask.get("rpNo"));
									trptmp.set("returnDate",sysdate);
									trptmp.set("userID",userID);
									trptmp.set("ownerSaleID",ask.get("ownerSaleID"));
									trptmp.insert();			
									System.out.println("trptmp===="+ask.get("rpNo"));
								}
							}
							trp.set("clearUserID",userID);
							trp.update();
							ask.set("receiptFlag","Z");
							ask.update();
					
							findFlag = true ;
						}
 						break ;
				}
				if (findFlag)
					 break ;
				st = ask.next();
			}
		}
	}	
	public void deleteRpAskFor(String typePol,String rpNo,String requestDate)
	{
		if(rpaskfor.equal(typePol+rpNo+requestDate))
		{
			hisrpaskfor.putBytes(rpaskfor.getBytes());
			hisrpaskfor.insert();
			rpaskfor.delete();
		}
	}
	private void insertXrcchg(Mrecord rcchg,char type,Record rc,String newstatus)
	{
		System.out.println("xrp --------------->"+rc.get("rpNo"));
		CheckAskReceipt.insertXrcchg(rcchg,type,rc,newstatus,sysdate,systime,userID);
                if (type == 'T')
                        return ;
                if (rc.get("reasonCode").compareTo("43") == 0)
                {
                        insertDataToReserved(rc,type);
                }
	}
        private void insertDataToReserved(Record rc,char type)
        {
                try{
                     Mrecord tores = CFile.opens("helptoreserved@receipt");
                     tores.set("policyType",String.valueOf(type));
                     tores.set("policyNo",rc.get("policyNo"));
                     tores.set("payPeriod",rc.get("payPeriod"));
                     tores.set("rpNo",rc.get("rpNo"));
                     tores.set("clearDate",sysdate);
                     tores.set("userID",userID);
                     tores.insert();
                }
                catch(Exception e)
                {
                        System.out.println("Error...........FOR.............Help Reserved");
                        e.printStackTrace();
                }
        }
}
