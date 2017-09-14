package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import utility.support.MyVector;
public class RteCancelAsk  implements Task
{
	Mrecord trptmp;
	Mrecord trptmptakaful;
	Mrecord orc;
	Mrecord irc;
	Mrecord trp;
	Mrecord wrc;
	Mrecord urc;
	Mrecord ask;
	Mrecord cask;
	Mrecord ucrp;
	Mrecord rpaskfor;
	Mrecord hisrpaskfor;

	String userID;
	String sysdate;
	String branch;
	MyVector vec ;
	String yyyymm;
	String ucrpyyyymm  = "";
	String saleID ;
	String askSaleID;
	String requestDate;
	boolean  flagSuper  = false;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {yyyymm,ucrpyyyymm,userID,branch,saleID,askSaleID,requestDate}",-1);
		Object [] parameter = (Object []) param;
		boolean search = true;
		// for delete 
		if(parameter.length == 7)
		{
			yyyymm = (String)parameter[0];
			ucrpyyyymm = (String)parameter[1];
			branch = (String)parameter[2];
			userID =(String)parameter[3];
			if(userID.charAt(0) == 'S')
			{
				flagSuper = true;
				userID = userID.substring(1);
			}
			saleID = (String)parameter[4];
			askSaleID = (String)parameter[5];
			requestDate = (String)parameter[6];
			search = false ;
		}
		else {
			yyyymm = (String)parameter[0];
			branch = (String)parameter[1];
			saleID = (String)parameter[2];
			askSaleID = (String)parameter[3];
			requestDate = (String)parameter[4];
		}
		sysdate = DateInfo.sysDate();

		try {
			ask = CFile.opens("ask"+yyyymm+"@cbranch");
			cask = CFile.opens("cask"+yyyymm+"@cbranch");
			if(ucrpyyyymm.trim().length() > 0)
				ucrp = CFile.opens(ucrpyyyymm+"@cbranch");
			urc = CFile.opens("ulrctrl@universal");
			orc = CFile.opens("orctrl@receipt");
			irc = CFile.opens("irctrl@receipt");
			wrc = CFile.opens("wrctrl@receipt");
			trp = CFile.opens("trpctrl@receipt");
			rpaskfor = CFile.opens("newrpaskfor@receipt");
			hisrpaskfor = CFile.opens("hisnewrpaskfor@receipt");
			trptmp = CFile.opens("trptmp@cbranch");
			trptmptakaful = CFile.opens("trptmptakaful@cbranch");
			if (search)
			{
				 vec = new MyVector();
				 searchAskReceipt();				
			}
			else {
				 vec = new MyVector();
				 searchAskReceipt();			
				 deleteAskReceipt();	
			}
			return new Result(vec,0);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
	}	
	int ret;
	public void searchAskReceipt() throws Exception
	{
		ask.start(1);
		boolean st = ask.equal(branch+saleID);
		String errMessage = "";
		System.out.println("-------------------------------------in search to Cancel request -------------------");
 		while (st  && saleID.equals(ask.get("ownerSaleID")) )
		{
			if (saleID.equals(ask.get("ownerSaleID")) &&askSaleID.equals(ask.get("askSaleID"))&&requestDate.equals(ask.get("requestDate"))) {
			//	System.out.println(ask.get("rpNo")+" "+ask.get("receiptFlag"));
				ret = checkReceipt(ask.get("receiptFlag").charAt(0),ask.get("rpNo"),ask.get("requestDate"),false);
				switch (ret){
					case 0 : 
						vec.add (ask.get("receiptFlag")+ask.get("rpNo"));
						break ;
					case 1 :  errMessage = M.stou("ไม่พบใบเสร็จ  "+ask.get("rpNo") +"เบิกวันที่  " + DateInfo.formatDate(1,ask.get("requestDate")));
						break;
					case 2 : errMessage = M.stou("ใบเสร็จ "+   ask.get("rpNo")  + " ได้มีการเปลี่ยนแปลงสถานะแล้ว ยกเลิกการเบิกไม่ได้ ");
						break ;
					case 3 : errMessage = M.stou("ใบเสร็จ "+   ask.get("rpNo")  + " ได้มีการเคลียร์แล้ว ยกเลิกการเบิกไม่ได้ ");
						break ;
				}
				//if (ret == 2 || ret == 1 || ret == 3)
				if ( ret == 1 )
					throw new Exception (errMessage);
			}
			st =  ask.next();
		}
	}
  	private int  checkReceipt(char type,String rpNo,String reqDate,boolean update)
        {
		Mrecord  rec =  null;
		if (type == 'I')
			rec = (Mrecord)irc;
		else if (type == 'O')
			rec = (Mrecord)orc;
		else if (type == 'W')
			rec = (Mrecord)wrc;
		else if (type == 'A')
			rec = (Mrecord)urc;
		else if (type == 'T')
			rec = (Mrecord)trp;
		else 
			return 3;
		for (boolean st = rec.equal(rpNo) ; st && rpNo.equals(rec.get("rpNo")) ; st = rec.next())
		{
			if (reqDate.equals(rec.get("requestDate")))
			{
				if ( rec.get("currentStatus").equals(rec.get("originalStatus")) || rec.get("currentStatus").charAt(0) == 'W'){
					if (update){
						rec.set("requestDate","00000000");
						if ("IOWA".indexOf(type) >= 0)
							rec.set("time",M.dec(rec.get("time")));
						rec.update();
					}
					return 0 ;
				}
				else  // if currentStatus ="PBECR"
					return 9 ;
			/*	else
					return 2;*/
			}
		}
		return 1 ;
        }
	void deleteAskReceipt() throws Exception
	{
		String tstr ="";
		if(!updateCask())
			throw new Exception(M.stou("ยกเลิกข้อมูลในแฟ้ม ผู้เบิกไม่สำเร็จ"));
		
		System.out.println("delete ask........................................"+vec.size());
		for (int i = 0 ; i < vec.size();i++)
		{
			tstr = (String)vec.elementAt(i);
			System.out.println("tstr .................."+tstr);
			checkReceipt(tstr.charAt(0),tstr.substring(1),requestDate,true);
			removeAsk(tstr.substring(1));			
		}
	}
	void removeAsk(String rpNo) throws Exception
        {
		ask.start(2);
		ucrp.start(2);
		for (boolean st = ask.equal(branch+rpNo) ; st && (branch+rpNo).equals(ask.get("branch")+ask.get("rpNo")) ; st = ask.next()){
			System.out.println("rpno .................."+rpNo);
			System.out.println("buffer ......"+new String(ask.getBytes()));
			if (requestDate.equals(ask.get("requestDate"))&&("AIOWT").indexOf(ask.get("receiptFlag").charAt(0)) >= 0 && ask.get("ownerSaleID").equals(saleID) && ask.get("askSaleID").equals(askSaleID))
			{	
				if (ask.get("receiptFlag").charAt(0)  == 'T')
				{
                                        ReceiptSaleStruct rss = new ReceiptSaleStruct();
                                        String tbranch = branch;
                                        if(branch.compareTo("007") == 0)
                                        {
                                                if (Receipt.sai6.compareTo(rss.getSai(ask.get("ownerSaleID")) )== 0 || Receipt.sai3.compareTo(rss.getSai(ask.get("ownerSaleID")) )== 0 )
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
					}
					System.out.println("trptmp===="+ask.get("rpNo"));
				}
				else {
					if(rpaskfor.equal(ask.get("receiptFlag")+ask.get("rpNo")+ask.get("requestDate")))
					{
						hisrpaskfor.putBytes(rpaskfor.getBytes());
						hisrpaskfor.insert();
						rpaskfor.delete();
					}
				}	
				ask.delete();
				break;
			}
                 }
		 if(ucrp == null)
			return ;
		 for (boolean st = ucrp.equal(branch+rpNo) ; st && rpNo.equals(ucrp.get("rpNo")) ; st = ucrp.next()){
			if (requestDate.equals(ucrp.get("requestDate"))&&("AIOWT").indexOf(ucrp.get("receiptFlag").charAt(0)) >=  0 && ucrp.get("ownerSaleID").equals(saleID) && ucrp.get("askSaleID").equals(askSaleID)){
				ucrp.delete();
				break;
			}
                 }
        }
	boolean  updateCask() throws Exception
        {
		System.out.println("will update cask---------"+branch+" "+saleID);
		for (boolean st = cask.equal(branch+saleID) ; st &&  saleID.compareTo(cask.get("ownerSaleID")) == 0;st = cask.next()){
		System.out.println("update cask---------"+branch+" "+saleID+ "  "+cask.get("status"));
			if (askSaleID.equals(cask.get("askSaleID")) && requestDate.equals(cask.get("requestDate")) &&
				cask.get("status").charAt(0) == 'N'){
		/*		if (!flagSuper &&  userID.compareTo(cask.get("userID")) != 0)
                                  {
                                          throw new Exception(M.stou("ต้องเป็นพนักงานที่ทำเบิกเท่านั้น ที่ยกเลิกได้"));
                                  }*/
                                  if (cask.get("reserve").charAt(0) != 'T')
                                  {      
                                        cask.set("status","C");
                                        cask.update();
                                  }
                          //        break;
			}
		}
		return true;
	}
}
