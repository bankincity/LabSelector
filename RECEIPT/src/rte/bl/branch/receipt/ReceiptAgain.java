package rte.bl.branch.receipt;
import manit.*;
import utility.support.DateInfo;
import utility.cfile.CFile;
import java.util.Vector;
import java.io.*;
import insure.Insure;
import utility.underwrite.ClientYC;
public class ReceiptAgain
{
	String year ;
	String typeOfRp;
	String frange="";
	Mrecord rprange;
	static String sysdate = "";
	static String systime ="";
	public ReceiptAgain(String type,String yy) throws Exception
	{
		typeOfRp = type;
		System.out.println("typeOfPol................."+type);
		year = yy;
		if(DateInfo.sysDate().substring(0,4).compareTo("2553") >= 0)
		{
			frange = "newrpnumber@receipt";
		}		
		else {
			frange = "ordrpnumber@receipt";
			if(typeOfRp.charAt(0) == 'I')
				frange = "indrpnumber@receipt";
		}
		rprange = CFile.opens(frange);
		sysdate = DateInfo.sysDate();
	        systime =  Masic.time("commontable").substring(8);
	}
	public ReceiptAgain()
	{
		sysdate = DateInfo.sysDate();
	        systime =  Masic.time("commontable").substring(8);
	}
	public int sendToPrint(String month,String sbranch,String send_filename) throws Exception
        {
		Mrecord whlmast = CFile.opens("whlmast@mstpolicy");
                Mrecord ordmast = CFile.opens("ordmast@mstpolicy");
                Mrecord indmast = CFile.opens("indmast@mstpolicy");
                Mrecord nameMast = CFile.opens("name@mstperson");
                Mrecord personMast = CFile.opens("person@mstperson");
                Mrecord send = CFile.opens("sendrptoprint@cbranch");
                Mrecord served = CFile.opens(typeOfRp.toLowerCase()+"rpserved"+month+"@cbranch");
                ReceiptSaleStruct rss = new  ReceiptSaleStruct();
	        int i = 0 ;
		Mrecord rprider = null;
                String sai = "";
                String processBranch = sbranch;
                if(typeOfRp.charAt(0) == 'O')
                        rprider  = CFile.opens("rprider@cbranch");
                if (sbranch.trim().length() == 4)
                {
                        sai = sbranch.substring(3,4);
                        sbranch = sbranch.substring(0,3);
                }
                // get data by sai of sales
                System.out.println("bingo......................."+sbranch+" sai-----"+sai);
                if (sbranch.compareTo("007") == 0)
                {
                        if (sai.trim().length() == 0)
                                sai = "N215Y";
                        else if (sai.charAt(0) == 'a')
                                sai = "N216U";
                }
                        
                System.out.println("In clsss ReceiptAgain-----------------------------------"+sbranch+"------------------------");
                if(send.equal(typeOfRp+sbranch))
                {
                        int count = M.ctoi(send.get("wantToPrint")) ;
                        if(count == 0 )
                        {
                                System.out.println("Not Registry......................");
                                return -1;
                        }
                 /*----------  	 if(Masic.echo("bra"+send.get("branch")+"app","hi") != 0)
                        {
                                System.out.println("Connection Failed......................");
                                return -1;
                        }----------------*/
                        System.out.println("Sending data to "+send.get("branch")+"  "+sai+"  "+send.get("wantToPrint"));
                       Mrecord print = CFile.opens(typeOfRp.toLowerCase()+"rpserved@bra"+processBranch+"app");
                        if(print.fileSize()  != 0)
                        {
                                System.out.println("Have receipt in branch ");
                                return -1;
                        }
                        print.close();
                        print = CFile.opens(send_filename);

                        System.out.println("startSend.........................");
			String  [] field  = served.layout().fieldName();
                        for (boolean  ss = served.great(send.get("branch")); ss && i < count ;)
                        {


		/*		if("255201".compareTo(served.get("effectiveDate").substring(0,6)) > 0)
				{
					ss = served.next();
					continue;
				}
				if(served.get("planCode").compareTo("ME") != 0)
				{
					ss = served.next();
					continue;
				}*/
                                if((send.get("branch")).compareTo(served.get("branch")) != 0)
                                        break;
				if(served.get("rpFlag").charAt(0) != '1' )
				{
					ss = served.next();
					continue;
				}
                                System.out.println("bingo....................write........."+served.get("policyNo"));
                                System.out.println("bingo...........11111............again "+sbranch);
                                // check for branch 007 split by sai
                                if (sbranch.compareTo("007") == 0)
                                {
                                        System.out.println("sai....."+sai+"-------"+sbranch+ "....." +served.get("agent"));
                                        if (!rss.isThisSai(sbranch,served.get("agent"),sai))
                                        {
					        ss = served.next();
					        continue;
                                        }                                           
                                }
                                System.out.println("bingo..........22222.............again");
				if(served.get("rpNo").compareTo("000000000000") == 0)	
                        	       served.set("rpNo",getRange(served.get("branch")));
                                served.set("rpFlag","1");
				for(int j = 0 ; j < field.length;j++)
				{
					print.set(field[j],served.get(field[j]));
				}
				String canReduceTax = "000000000";
                                if(rprider != null)
                                {
                                         
                                        canReduceTax = canReduceTax(rprider,served.get("policyNo")+served.get("payPeriod"));
                                        print.set("extraPrem",M.subnum(print.get("premium"),canReduceTax));
                                }
                                System.out.println("bingo..........33333.............again");
                                if(print.get("typeOfPolicy").charAt(0) == 'I')
                                        print.set("agent",getSexAndAge(print.get("policyNo"),indmast,nameMast,personMast));                                else if(print.get("typeOfPolicy").charAt(0) == 'W')
                                        print.set("agent",getSexAndAge(print.get("policyNo"),whlmast,nameMast,personMast));                                else if(print.get("typeOfPolicy").charAt(0) == 'O')
                                        print.set("agent",getSexAndAge(print.get("policyNo"),ordmast,nameMast,personMast));

				print.set("newline","\n");
	//		System.out.println("Before insert print   :"+M.systime());
                                if(print.insert())
                                {
					
				//	System.out.println("After  insert print   :"+M.systime());
                                       if(!served.update())
					      throw new Exception("Can not update rpNo To :"+served.get("branch")+" "+served.get("policyNo")+" "+served.get("payPeriod"));
				//	System.out.println("After  update  print   :"+M.systime());
                                }
                                else
                                        throw new Exception("Can not insert to branch rpserved  : "+served.get("branch")+" "+served.get("policyNo")+" "+served.get("payPeriod"));
                                i++;
			//	System.out.println("Before next   :"+M.systime());
                                ss = served.next();
				if(i % 100 == 0)
					System.out.println("i   :"+i+" "+M.systime());
                        }
                        System.out.println("end--------------------------------"+i+"------------------------------------");                        print.close();
                }
                else {
                        System.out.println("Not Registry......................");
			return -1;
		}
                return i;
        }
	  private String getSexAndAge(String policyNo,Mrecord  master,Mrecord nameMaster,Mrecord personMaster)
        {
                if(master.equal(policyNo))
                {
                        if(nameMaster.equal(master.get("nameID")))
                        {
                                if(personMaster.equal(nameMaster.get("personID")))
                                {
                                        return (personMaster.get("sex")+master.get("insuredAge")+master.get("policyStatus1"));
                                }
                        }
                }
                return "M01I";
        }
	private String canReduceTax(Mrecord crprid ,String key)
        {
                String rprem = "000000000";
                int i = 0 ;
                for (boolean st = crprid.equalGreat(key) ; st && key.compareTo(crprid.get("rpNo")) == 0 ; st=crprid.next())                {
                        String riderText = crprid.get("riderText");
                        int j = 0 ;
                        int  len = riderText.length();
                        if (i == 0 )
                        {
                                rprem = M.addnum(rprem,riderText.substring(0,9));
                                rprem = M.addnum(rprem,riderText.substring(9,15));
                                j = 15;
                        }
                        while (j < len)
                        {
                                if(riderText.substring(j,j+3).compareTo(M.stou("ฉพ ")) == 0 ||
                                        riderText.substring(j,j+3).compareTo(M.stou("ฉพ1")) == 0 ||
                                        riderText.substring(j,j+3).compareTo(M.stou("ฉพ2")) == 0)
                                {
                                        rprem = M.addnum(rprem,riderText.substring(j+3,j+3+9));
                                        rprem = M.addnum(rprem,riderText.substring(j+12,j+12+6));

                                }
                                j+=18;
        //                      System.out.println("j ="+j+" len ----"+len);
                                if(i > 0 && j == 108 )
                                        break;
                        }
                        i++;
                }
                return rprem;
        }
	private String getRange(String branch) throws Exception
	{
		String trp = "0000001";
		String msg = "";
		rprange.lock();
		if(rprange.equal(branch+year)) 
		{
			trp = M.inc(rprange.get("currentReceipt"));
			rprange.set("currentReceipt",trp);
			if(!rprange.update())
				msg = "Can not Update "+frange+" Error :"+M.itoc(rprange.lastError());
		}
		else {
			rprange.set("department",branch);
			rprange.set("year",year);
			rprange.set("currentReceipt",trp);
			if(!rprange.insert())
				msg = "Can not Insert "+frange+" Error :"+M.itoc(rprange.lastError());
			
		}
		rprange.release();
		if(msg.trim().length() > 0)
			throw new Exception(msg); 
		return (branch+year+trp);
	}
	//rpprint  = Vecotr byte[] of rpserved
	public  Vector alreadyPrintRequestRpToRpForm(Vector rpprint,String typeOfRp,String branch,String userID,String flagPrint) throws Exception
	{
		String month = sysdate.substring(0,6);
		String ptype = typeOfRp;
		if (ptype.charAt(0) == 'W')
			ptype = "O";
		Mrecord tserved = CFile.opens(ptype.toLowerCase()+"rpserved"+month+"@cbranch");
		Mrecord rc ;
		Mrecord rc2 ;
		Mrecord rprider = null ;
		Mrecord crprider = null ;
		Vector verror = new Vector();
		if(typeOfRp.charAt(0) == 'I')
		{
			rc = CFile.opens("irctrl@receipt");
			crprider = CFile.opens("irprider@cbranch");
			rprider = CFile.opens("irprider@receipt");
			rc2 = null;
		}
		else {
			rc = CFile.opens("orctrl@receipt");
			rc2 = CFile.opens("wrctrl@receipt");
			crprider = CFile.opens("rprider@cbranch");
			rprider = CFile.opens("rprider@receipt");
		} 
		Vector newrp = new Vector();
		Vector vpolicy = new Vector();
		String trp ="";
		Record served = tserved.copy();
		String riderText ="";
		System.out.println("flagPrint................."+flagPrint+"  "+typeOfRp);
//		if("XA".indexOf(flagPrint) >= 0 && typeOfRp.charAt(0) != 'I')
		if("XAFO".indexOf(flagPrint) >= 0)
		{
			int vsize = rpprint.size();
			if (vsize > 0)
			{
				if (rpprint.elementAt(vsize - 1) instanceof String)
				{	
					riderText = (String)rpprint.elementAt(vsize - 1);
					rpprint.removeElementAt(vsize -1);
				}
				System.out.println("riderText ---------------------------------------------------->"+riderText);		
			}
		}
		System.out.println("rpprint.........................................<><><> size == "+rpprint.size());
		for (int i = 0 ; i < rpprint.size();i++)
		{
			served.putBytes((byte [])rpprint.elementAt(i));
			String oldrpno = served.get("rpNo");
			served.set("rpNo",getRange(branch));
			if(flagPrint.charAt(0) == 'R')
			{
				if(served.get("typeOfPolicy").charAt(0) == 'W')
				    served = changeRpServedRec(served.get("typeOfPolicy"),rc2,served,rprider,oldrpno);
				else if("IO".indexOf(served.get("typeOfPolicy").charAt(0)) >= 0)
				    served = changeRpServedRec(served.get("typeOfPolicy"),rc,served,rprider,oldrpno);			
			}
			else if("XAF".indexOf(flagPrint) >= 0 && typeOfRp.charAt(0) != 'I')
			{
				insertRpRiderForAX(riderText,served.get("rpNo"),rprider);
			}
		System.out.println("flagPrint........1111........."+flagPrint+"  "+typeOfRp);
			if("XAF".indexOf(flagPrint) < 0)
			{
				vpolicy.addElement(served.get("policyNo")+served.get("payPeriod"));
				checkDataInRctrl(vpolicy,served.get("typeOfPolicy"),"21",userID);
			}
		System.out.println("flagPrint.......2222.........."+flagPrint+"  "+typeOfRp);
			if(served.get("typeOfPolicy").charAt(0) == 'W')
				 moveToInsertCtrl(rc2,served,sysdate,null,null,flagPrint,branch,userID);
			else
				 moveToInsertCtrl(rc,served,sysdate,rprider,crprider,flagPrint,branch,userID);
		System.out.println("flagPrint.......3333.........."+flagPrint+"  "+typeOfRp);
		System.out.println("flagPrint.......4444.........."+new String(served.copy().getBytes()));
			newrp.addElement(served.copy().getBytes());
		System.out.println("flagPrint.......5555..........");
			vpolicy.removeAllElements();			
		System.out.println("flagPrint.......6666..........");
		}
		
		System.out.println("flagPrint.......6677..........");
		System.out.println("flagPrint.......7777.........."+newrp.size());
		return newrp;
	}
	private void insertRpRiderForAX(String riderText,String rpNo,Mrecord rprider)
	{
		int len = riderText.trim().length();
		String seq ="1";
		for (;len > 123;)
		{
			rprider.set("rpNo",rpNo);
			rprider.set("seqNo",seq);
			rprider.set("riderText",riderText.substring(0,123));
			rprider.insert();
			riderText = riderText.substring(123);	
			len = riderText.trim().length();
			seq = M.inc(seq);		
		}
		if(riderText.trim().length() > 0)
		{
			rprider.set("rpNo",rpNo);
			rprider.set("seqNo",seq);
			rprider.set("riderText",riderText);
			rprider.insert();
		}
	}
	private String getModeToPrint(String mode)
        {
                switch(mode.charAt(0))
                {
                        case '0':return "1";
                        case '1':return "12";
                        case '2':return "6";
                        case '4': return "3";
                }
                return "1";
        }
	private Record changeRpServedRec(String typeOfPol,Mrecord rc ,Record served,Mrecord rprider,String rrpno)
	{
		if(rc.equal(rrpno))
		{
			if("OW".indexOf(typeOfPol.charAt(0)) >= 0)
			{
				served.set("effectiveDate",rc.get("effectiveDate"));
				served.set("mode",getModeToPrint(rc.get("mode")));
				served.set("premium",rc.get("premium"));
				served.set("extraPrem",rc.get("extraPrem"));
				served.set("payPeriod",rc.get("payPeriod"));

				System.out.println("parameter for duedate............"+served.get("mode")+"  "+	served.get("effectiveDate")+"  "+served.get("payPeriod"));
				served.set("dueDate",Insure.dueDate(rc.get("mode"),served.get("effectiveDate"),served.get("payPeriod")));
			//tring nextPayPeriod  = Insure.nextPayPeriod(served.get("payPeriod"),served.get("mode"));
			//ystem.out.println("nextPeriod ======================="+nextPayPeriod+"-----------"+served.get("payPeriod")+"  "+served.get("mode"));
				served.set("nextDueDate",Insure.nextDueDate(rc.get("mode"),served.get("effectiveDate"),served.get("payPeriod")));
                       	//	served.set("nextDueDate",Insure.dueDate(served.get("mode"),served.get("effectiveDate"),nextPayPeriod));
			}
			else {
				served.set("effectiveDate",rc.get("effectiveDate"));
				served.set("premium",rc.get("premium"));
				served.set("payPeriod",rc.get("payPeriod"));
	
				served.set("dueDate",served.get("payPeriod")+"01");
				String nextPayPeriod  = DateInfo.nextMonth(served.get("dueDate"),1);
                       		served.set("nextDueDate",nextPayPeriod);
			}
			if(rprider != null) 
			{
				String seq  ="1";
				for(boolean st = rprider.equal(rrpno+seq) ; st && rrpno.compareTo(rprider.get("rpNo")) == 0;)
				{
					Record trec = rprider.copy();
					trec.set("rpNo",served.get("rpNo"));
					rprider.insert(trec);
					seq = M.inc(seq);
					st = rprider.equal(rrpno+seq);
				}
			}
		}
		return served;
	}
	public Vector alreadyPrintRpForm(Vector rpprint,String month,String typeOfRp,String userID) throws Exception
	{
		String ptype = typeOfRp;
		if (ptype.charAt(0) == 'W')
			ptype = "O";
		Mrecord served = CFile.opens(ptype.toLowerCase()+"rpserved"+month+"@cbranch");
		Mrecord rc ;
		Mrecord rc2 ;
		Mrecord rprider = null ;
		Mrecord crprider = null ;
		Vector verror = new Vector();
		served.start(1);
		if(typeOfRp.charAt(0) == 'I')
		{
			rc = CFile.opens("irctrl@receipt");
			rprider = CFile.opens("irprider@receipt");
			crprider = CFile.opens("irprider@cbranch");
			rc2 = null;
		}
		else {
			rc = CFile.opens("orctrl@receipt");
			rc2 = CFile.opens("wrctrl@receipt");
			rprider = CFile.opens("rprider@receipt");
			crprider = CFile.opens("rprider@cbranch");
		} 
		String trp ="";
		for (int i = 0 ; i < rpprint.size();i++)
		{
			trp = (String)rpprint.elementAt(i);
			if(!served.equal(trp.substring(12)))
				verror.addElement(trp.substring(0,12)+"N00");
			else {		
				if(served.get("typeOfPolicy").charAt(0) == 'W')
					moveToInsertCtrl(rc2,served.copy(),month+"00",null,null,"T",served.get("branch"),userID);
				else
					moveToInsertCtrl(rc,served.copy(),month+"00",rprider,crprider,"T",served.get("branch"),userID);
				
				if(served.get("rpFlag").charAt(0) == '2')
					verror.addElement(trp+"C"+served.get("reasonToCancel"));   // ชำระเบี้ยที่สาขาอื่นแล้ว
				served.set("rpFlag","8");
				served.update();
			}
		}
		return verror; 
	}
	private void  moveToInsertCtrl(Mrecord rc ,Record served,String month,Mrecord rprider,Mrecord crprider,String flagPrint,String branch,String userID) throws Exception
	{
		rc.set('0');
		rc.set("rpNo",served.get("rpNo"));
		rc.set("policyNo",served.get("policyNo"));
		rc.set("effectiveDate",served.get("effectiveDate"));
		rc.set("sysDate",month);
		rc.set("printedDate",sysdate);
		rc.set("submitBranch",branch);
		rc.set("userID",userID);
		String to_stat = flagPrint;
		if(served.get("reasonToCancel").charAt(0) == 'H')
			flagPrint = served.get("reasonToCancel").substring(1);
		if(flagPrint.charAt(0) == 'A' || flagPrint.charAt(0) == 'F')
		{
			rc.set("originalStatus","A");
			rc.set("currentStatus","A");
			if ( flagPrint.charAt(0) == 'F')
				rc.set("reasonCode", "RR");
		}
		else if(flagPrint.charAt(0) == 'X')
		{
			rc.set("originalStatus","X");
			rc.set("currentStatus","X");
		}
		else {
			rc.set("originalStatus","N");
			rc.set("currentStatus","N");
		}
		rc.set("gracePeriod"," ");
		if(served.get("typeOfPolicy").charAt(0) != 'I')
		{
			rc.set("payPeriod",served.get("payPeriod").substring(0,4));
			rc.set("premium",M.setlen(served.get("premium"),9));
			switch (M.ctoi(served.get("mode")))
			{
				case  12 : rc.set("mode","1");
					 break;
				case  6  : rc.set("mode","2");
					 break;
				case  3  : rc.set("mode","4");
					 break;
				case  1  : rc.set("mode","0");
					 break; 
					
			}
			rc.set("extraPrem",served.get("extraPrem"));
		}
		else {
			rc.set("payPeriod",served.get("payPeriod").substring(0,6));
			rc.set("premium",M.setlen(served.get("premium"),5));
		}	
		if(served.get("rpFlag").charAt(0) == '2')
		{
			rc.set("currentStatus","W");
			rc.set("reasonCode",served.get("reasonToCancel"));
		}
		else if (served.get("reasonToCancel").charAt(0) == 'H')
		{
			System.out.println("bingo....................................");
			rc.set("reasonCode","HH");
		}
		if (to_stat.charAt(0) == 'O') // rider offer 
		{
			rc.set("reasonCode","RR");
		}	
		if(!rc.insert())
			throw new Exception("Cannot insert to CTRL "+rc.get("rpNo"));
		
		System.out.println("---------rprider------------fdsafds-----------"+rprider+"---------------"+flagPrint+"--------"+to_stat);
		if(rprider != null && "RAX".indexOf(to_stat) < 0)
		{
			
			String tmpkey =null;
			
			if(served.get("typeOfPolicy").charAt(0) != 'I')
				tmpkey= rc.get("policyNo")+rc.get("payPeriod");
			else
				tmpkey= rc.get("policyNo")+rc.get("payPeriod").substring(2);
			Record trprider = null;
			boolean first = true;
			System.out.println("tmpkey =================afdfsdfdsafadsfadsfasd============"+tmpkey);

			while (true)
			{
				System.out.println("bingo............ooooooooooooooooooooooooooooo");
				 if(crprider.equalGreat(tmpkey) && tmpkey.compareTo(crprider.get("rpNo")) == 0)
				{
					trprider = crprider.copy();
					crprider.delete();
					if(flagPrint.charAt(0) =='L')
					{
						if(served.get("typeOfPolicy").charAt(0) != 'I')
							trprider.set("riderText",trprider.get("riderText").substring(0,15));						   else
						      trprider.set("riderText",trprider.get("riderText").substring(0,7));					
					}
					rprider.set("rpNo",rc.get("rpNo"));
					rprider.set("seqNo",trprider.get("seqNo"));
					rprider.set("riderText",trprider.get("riderText"));
					rprider.insert();
					first = false ;
					System.out.println("bingo............rrrrrrrrrrrrrrrrrrrrrrr");
				}
				else if(first && flagPrint.charAt(0) == 'L')
				{
					rprider.set("rpNo",rc.get("rpNo"));
					rprider.set("seqNo","1");
					if(served.get("typeOfPolicy").charAt(0) != 'I')
						rprider.set("riderText",M.setlen(rc.get("premium"),9)+"000000");
					else
						rprider.set("riderText",M.setlen(rc.get("premium"),4)+"000");
					rprider.insert();
					first = false ;
				}
				else
					break;
			}					 		
		}
		System.out.println("bingo............ooooooooooooooooooooooooooooo");
	}
	public static  void checkDataInRctrl(Vector vpolicy,String typeOfPolicy,String reasonCode,String userID) throws Exception
	{
		
		String sysdate =  DateInfo.sysDate();
             //   sysdate = "25570801";
//		sysdate = "25510412";
		Mrecord served  = null;
		String filename ="orpserved";
		if(typeOfPolicy.charAt(0) == 'I')
			filename = "irpserved";
		String month = DateInfo.nextMonth(sysdate).substring(0,6);
		for (int i = 0 ; i < 3 ; i++)
		{
			if(CFile.canOpenShare(filename+month+"@cbranch"))
			{
				served = CFile.opens(filename+month+"@cbranch");
				break;
			}
			month = DateInfo.previousMonth(month).substring(0,6);
		}
		if(served != null)
			served.start(1);
		for (int i = 0 ;i < vpolicy.size() && served != null;i++)
		{
			String tpol = (String)vpolicy.elementAt(i);
			String payPeriod = "";
			if(tpol.trim().length() > 8)
			{
				payPeriod = tpol.substring(8);
				tpol = tpol.substring(0,8);
			}
			System.out.println("tpol..for search rpserved......................................."+tpol);
			for (boolean st = served.equalGreat(tpol) ; st && tpol.compareTo(served.get("policyNo")) == 0;st=served.next())
			{
				System.out.println("payPeriod === "+payPeriod+"   "+served.get("payPeriod"));
				if(payPeriod.trim().length() != 0 && payPeriod.trim().compareTo(served.get("payPeriod")) != 0)
					continue;
					
				if(served.get("rpFlag").charAt(0) == '0')
					served.set("rpFlag","9");  // ไม่ต้องพิมพ์สำรองใบเสร็จ
				else if (served.get("rpFlag").charAt(0) == '1')
				{
					served.set("rpFlag","2");  // อยู่ระหว่างพิมพ์รอยกเลิก
					served.set("reasonToCancel",reasonCode);
				}
				if(!served.update())
					throw new Exception(M.stou("Can not update rpFlag in rpservedyyyymm"));
			}
		}
		Mrecord rc = Masic.opens(typeOfPolicy.toLowerCase()+"rctrl@receipt");
		if(rc != null)
			rc.start(1);
		for (int i = 0 ;i < vpolicy.size() && rc != null;i++)
		{
			String tpol = (String)vpolicy.elementAt(i);
			String payPeriod = "";
			if(tpol.trim().length() > 8)
			{
				payPeriod = tpol.substring(8);
				tpol = tpol.substring(0,8);
			}
			for (boolean st = rc.equalGreat(tpol) ; st && tpol.compareTo(rc.get("policyNo")) == 0 ; st=rc.next())
			{
				if(payPeriod.trim().length() != 0 && payPeriod.trim().compareTo(rc.get("payPeriod")) != 0)
					continue;
				if("NAX".indexOf(rc.get("currentStatus")) >= 0)
				{
					userID = "X0000"+reasonCode;
					insertXrpChange(typeOfPolicy.charAt(0),rc.copy(),"W",userID);
					rc.set("currentStatus","W");
					rc.set("reasonCode",reasonCode);
					rc.set("sysDate",DateInfo.sysDate());
					if(!rc.update())
						throw new Exception(M.stou("Can not update currentStatus in Ctrl ")+tpol+"   isError  "+M.itoc(rc.lastError()));
						
				}	
			}
		}
		
	}
	static Mrecord rcchg;
	public static void insertXrpChange(char type,Record  rc,String newstatus,String userID) throws Exception
	{
		if(rcchg == null)
			rcchg = CFile.opens("xrpchg@cbranch");
		if (systime.trim().length() == 0)
		{
			 sysdate = DateInfo.sysDate();
               		 systime =  Masic.time("commontable").substring(8);
		}
		CheckAskReceipt.insertXrcchg(rcchg,type,rc,newstatus,sysdate,systime,userID);
	}
	public static void moveAskyyyymm(String saleID,String oldbranch,String newbranch) throws Exception
	{
		String sysdate = DateInfo.sysDate();
		String month = sysdate.substring(0,6);
		if(sysdate.substring(6).compareTo("17") >= 0 )
		{
			month = DateInfo.nextMonth(month.substring(0,6));
			Mrecord  ask = CFile.opens("ask"+month+"@cbranch");
			if(!startMoveAsk(ask,null,saleID,oldbranch,newbranch))
			{
				month = sysdate.substring(0,6);
				ask = CFile.opens("ask"+month+"@cbranch");
				Mrecord askYM = CFile.opens("askyyyymm@cbranch");
				startMoveAsk(ask,askYM,saleID,oldbranch,newbranch);	
			}
		}
		else {
			Mrecord ask = CFile.opens("ask"+month+"@cbranch");
			Mrecord askYM = CFile.opens("askyyyymm@cbranch");
			startMoveAsk(ask,askYM,saleID,oldbranch,newbranch);	
		}
	}
	public static void moveReservedRp(String typeOfPolicy,Vector vpolicy,String newSaleID,String newbranch) throws Exception
	{
		String sysdate = DateInfo.sysDate();
                
          //      sysdate = "25570801";

		String month = DateInfo.nextMonth(sysdate.substring(0,6)).substring(0,6);
		Mrecord served = null;
		if("OW".indexOf(typeOfPolicy) > 0)
			served = Masic.opens("orpserved"+month+"@cbranch");
		else
			served = Masic.opens("irpserved"+month+"@cbranch");
		if(served == null || served.lastError() != 0)
			return ;
		Mrecord sperson = CFile.opens("person@sales");
		served.start(1);
		String tpol = "";
		for (int i = 0 ; i < vpolicy.size();i++)
		{
			tpol = (String)vpolicy.elementAt(i);
			for (boolean st = served.equalGreat(tpol);st && tpol.compareTo(served.get("policyNo")) == 0 ; st=served.next())
			{
				if(served.get("rpFlag").charAt(0) == '0')
				{
					served.set("branch",newbranch);
					if(sperson.equal(newSaleID))
					{
						served.set("agent",sperson.get("depositNo"));
					}
					if(!served.update())
						throw new Exception(M.stou("ไม่สามารถเปลี่ยนสาขาในแฟ้ม rpserved error = ")+M.itoc(served.lastError()));
				}
			}
		}		
	}
	private static boolean startMoveAsk(Mrecord ask,Mrecord askYM,String saleID,String oldbranch,String newbranch) throws Exception
	{
		Mrecord _firc = CFile.opens("irctrl@receipt");
		Mrecord _forc = CFile.opens("orctrl@receipt");
		Mrecord _fwrc = CFile.opens("wrctrl@receipt");
		Mrecord _ftrp = CFile.opens("trpctrl@receipt");
		ask.start(1);
		boolean st = ask.equal(oldbranch+saleID);
		if(!st)
		{
			return false ;	
		}
		for (; st && saleID.compareTo(ask.get("ownerSaleID")) == 0 && oldbranch.compareTo(ask.get("branch")) == 0;)
		{
			//if ( ask.get("receiptFlag").charAt(0) == 'T' )
			//{
			//	st = ask.next();
			//	continue;
			//}
			//ask.set("branch",newbranch);
			//ask.update();
			Record recask = ask.copy();
			recask.set("branch",newbranch);
			ask.delete();
			ask.insert(recask);
			if ( recask.get("receiptFlag").charAt(0) == 'O' )
			{
				if ( _forc.equal(recask.get("rpNo")) && _forc.get("currentStatus").charAt(0) == 'N' &&
				     M.cmps(_forc.get("submitBranch"), oldbranch) == 0 )
				{
					_forc.set("submitBranch", newbranch);
					_forc.update();
				}
			}
			else if ( recask.get("receiptFlag").charAt(0) == 'I' )
			{
				if ( _firc.equal(recask.get("rpNo")) && _firc.get("currentStatus").charAt(0) == 'N' &&
				     M.cmps(_firc.get("submitBranch"), oldbranch) == 0 )
				{
					_firc.set("submitBranch", newbranch);
					_firc.update();
				}
			}
			else if ( recask.get("receiptFlag").charAt(0) == 'W' )
			{
				if ( _fwrc.equal(recask.get("rpNo")) && _fwrc.get("currentStatus").charAt(0) == 'N' &&
				     M.cmps(_fwrc.get("submitBranch"), oldbranch) == 0 )
				{
					_fwrc.set("submitBranch", newbranch);
					_fwrc.update();
				}
			}
			else if ( recask.get("receiptFlag").charAt(0) == 'T' )
			{
				if ( _ftrp.equal(recask.get("rpNo")) && _ftrp.get("currentStatus").charAt(0) == 'N' &&
				     M.cmps(_ftrp.get("branch"), oldbranch) == 0 )
				{
					_ftrp.set("branch", newbranch);
					_ftrp.update();
				}
			}
			st = ask.equal(oldbranch+saleID);
		}
		if(askYM != null)
		{
			askYM.start(1);
			st = askYM.equal(oldbranch+saleID);
			for (; st && saleID.compareTo(askYM.get("ownerSaleID")) == 0 && oldbranch.compareTo(askYM.get("branch")) == 0;)
			{
				//if ( ask.get("receiptFlag").charAt(0) == 'T' )
				//{
				//	st = ask.next();
				//	continue;
				//}
				//askYM.set("branch",newbranch);
				//askYM.update();
				Record recaskYM = askYM.copy();
				recaskYM.set("branch",newbranch);
				askYM.delete();
				askYM.insert(recaskYM);
				if ( recaskYM.get("receiptFlag").charAt(0) == 'O' )
				{
					if ( _forc.equal(recaskYM.get("rpNo")) && _forc.get("currentStatus").charAt(0) == 'N' &&
					     M.cmps(_forc.get("submitBranch"), oldbranch) == 0 )
					{
						_forc.set("submitBranch", newbranch);
						_forc.update();
					}
				}
				else if ( recaskYM.get("receiptFlag").charAt(0) == 'I' )
				{
					if ( _firc.equal(recaskYM.get("rpNo")) && _firc.get("currentStatus").charAt(0) == 'N' &&
					     M.cmps(_firc.get("submitBranch"), oldbranch) == 0 )
					{
						_firc.set("submitBranch", newbranch);
						_firc.update();
					}
				}
				else if ( recaskYM.get("receiptFlag").charAt(0) == 'W' )
				{
					if ( _fwrc.equal(recaskYM.get("rpNo")) && _fwrc.get("currentStatus").charAt(0) == 'N' &&
					     M.cmps(_fwrc.get("submitBranch"), oldbranch) == 0 )
					{
						_fwrc.set("submitBranch", newbranch);
						_fwrc.update();
					}
				}
				else if ( recaskYM.get("receiptFlag").charAt(0) == 'T' )
				{
					if ( _ftrp.equal(recaskYM.get("rpNo")) && _ftrp.get("currentStatus").charAt(0) == 'N' &&
					     M.cmps(_ftrp.get("branch"), oldbranch) == 0 )
					{
						_ftrp.set("branch", newbranch);
						_ftrp.update();
					}
				}
				st = askYM.equal(oldbranch+saleID);
			}	
		}
		try
		{
			_firc.close();
			_forc.close();
			_fwrc.close();
			_ftrp.close();
		}
		catch (Exception e)
		{
		}
		return true;		
	}
	public String [] getReservedRp(Mrecord rp,String policyNo,String lastPeriod)
        {
                String [] ret = {"0000","0"};
		String lastReserve = "0000";
		int countRp = 0 ;
		for (boolean st = rp.equalGreat(policyNo); st; st = rp.next())
		{
			if(policyNo.compareTo(rp.get("policyNo")) != 0)
				break;
			if("N".indexOf(rp.get("originalStatus")) < 0 )
				continue;
			if ("NWKUD".indexOf(rp.get("currentStatus")) >= 0)
			{
				if(lastPeriod.compareTo(rp.get("payPeriod")) < 0)
				{
					if(lastReserve.compareTo(rp.get("payPeriod")) < 0)
						lastReserve = rp.get("payPeriod");
					countRp++;
				}
			}
		}
		ret[0]  = lastReserve;
		ret[1] = M.itoc(countRp);
                return ret;
        }
	
	public static String getCurrentStatus(String receiptFlag,String policyNo,String payPeriod,String currentStatus,Mrecord ind,Mrecord ord,Mrecord whl) throws Exception
        {
		if(sysdate.trim().length() == 0)
			sysdate = DateInfo.sysDate();
                if(currentStatus.charAt(0) == 'W' || currentStatus.charAt(0) == 'N')
		{
			boolean confirm = false ;
			if(receiptFlag.charAt(0) == 'I')
                                confirm = confirmCancel(ind,policyNo,payPeriod);
			else if(receiptFlag.charAt(0) == 'O')
                                confirm = confirmCancel(ord,policyNo,payPeriod);
			else if(receiptFlag.charAt(0) == 'W')
                                confirm = confirmCancel(whl,policyNo,payPeriod);
                        if(confirm)
				return "C";
			if (currentStatus.charAt(0) == 'W')
                     		   return currentStatus;
		}
                String dueDate = "00000000";
                int  day = 120;
                String nameID = "0000000000";
                Mrecord master = null ;
                switch (receiptFlag.charAt(0))
                {
                        case 'I' : if(ind.equal(policyNo))
                                   {
                                        dueDate = DateInfo.nextMonth(ind.get("payPeriod")+"01");
                                        master = ind;
                                   }

                                  break;
                        case 'O' : if( ord.equal(policyNo))
                                   {
					if(ord.get("policyStatus2").charAt(0) == 'B' || ord.get("oldPolicyStatus2").charAt(0) == 'B')
					{
						GetKBPeriod kbp = new GetKBPeriod();
                                   		String pPeriod = kbp.kbPeriod(ord.get("policyNo"));
                                 		pPeriod =  Insure.nextPayPeriod(pPeriod,ord.get("mode"));
						dueDate = Insure.dueDate(ord.get("mode"),ord.get("effectiveDate"),pPeriod);

					}
					else
                                       		 dueDate = ord.get("dueDate");
                                        if(insure.PlanType.isPAPlan(ord.get("planCode")) || ord.get("policyStatus1").charAt(0) == 'F')
                                                day = 31;
                                        master = ord ;
                                   }
                                  break;
                        case 'W' : if(whl.equal(policyNo))
                                  {
                                        dueDate = whl.get("dueDate");
                                        master = whl;
                                  }
                                  break;
                }
		if (dueDate.compareTo("00000000") == 0)
                        return currentStatus;
				
		int diff = 0 ;
		if(dueDate.compareTo(sysdate) < 0)
		{

	                diff = DateInfo.calDay(dueDate,sysdate,new byte[3],new byte[3]);
        	        System.out.println("diff ----------"+diff+"-----------------"+day);
               		if(diff  > day)
              		{
                      		if(day == 31)
                              	  return "1";
                     	 	return "4";
			}
                }
//              if("25274344".compareTo(master.get("policyNo")) == 0)
//                      System.out.println("25274344...........................diff ---"+diff);
                if(diff > 31)
                {
			if(receiptFlag.charAt(0)  == 'I' && diff <= 180 )
				return currentStatus;
                        String [] nm = getName(master.get("nameID"));
			if(nm[1].trim().length() > 0)
			{
                     	System.out.println("1 bingo-----------"+nm[1]+" "+nm[2]+" "+nm[3]);
                      /*   if(ClientYC.haveYC(nm[0],nm[1],nm[2],nm[3]))
                       	   {
                                if("LAO".indexOf(master.get("policyStatus1")) >=0) // lapse or สิน้สุด APL
                                        return "Y";
                       	   }*/
			}
                     //	System.out.println("2 bingo-----------"+nm[1]);
                        if("Y".indexOf(master.get("lapseFlag")) >= 0)
                                if("LOA".indexOf(master.get("policyStatus1")) >= 0)
                                        return "L";
                     //	System.out.println("3 bingo-----------"+nm[1]);
                        if(master.get("hivFlag").charAt(0) == 'Y')
                        {
                                if("LO".indexOf(master.get("policyStatus1")) >= 0)
                                        return "H";
                                else if ("A".indexOf(master.get("policyStatus1")) >=0)
                                        currentStatus = "5";
                        }
                     //	System.out.println("4 bingo-----------"+nm[1]);
                        if("MB".indexOf(master.get("medical")) >= 0)
                        {
                                if(master.get("class").charAt(0) == 'S')
                                        if("LOA".indexOf(master.get("policyStatus1")) >= 0)
                                                return "S";
                                if(master.get("class").charAt(0) != 'S')
                                {
                                        if("O".indexOf(master.get("policyStatus1")) >= 0)
                                                return "M";
                                        else if("L".indexOf(master.get("policyStatus1")) >= 0 && diff > 61)
						return "M";
                                        else if("A".indexOf(master.get("policyStatus1")) >= 0)
                                                currentStatus = "5";
                                        //      return "5";
                                }
                        }
                     //	System.out.println("4 bingo-----------"+nm[1]);
                        if("NA".indexOf(master.get("medical")) >= 0)
                                if("LOA".indexOf(master.get("policyStatus1")) >= 0)
                                        currentStatus = "5";
                                //      return "5";
                     //	System.out.println("5 bingo-----------"+nm[1]);
                }
                else
                        return currentStatus;

                return currentStatus;
        }
	private static boolean  confirmCancel(Mrecord master,String policyNo,String payPeriod )
        {
                if(master.equal(policyNo))
                {
                        if("XDSEU#*".indexOf(master.get("policyStatus1")) >= 0)
                                return true;
                        if(payPeriod.compareTo(master.get("payPeriod")) <= 0)
                                return true;
                }
                return false ;
        }

	static Mrecord name;
	static Mrecord person;
	public static String []  getName(String nameID) throws Exception
        {
		System.out.println("getName ============================"+nameID);
                String [] nm = {"0000000000000","","","00000000"}; // id , firstName , lastName,birthDate
		if(name == null)
		{
			name = CFile.opens("name@mstperson");
               		person  = CFile.opens("person@mstperson");
		}
                if(name.equal(nameID))
                {
                        nm [1] = name.get("firstName");
                        nm [2] = name.get("lastName");
                        if(person.equal(name.get("personID")))
                        {
                                nm[0] = person.get("referenceID");
                                nm[3] = person.get("birthDate");
                        }
                }
		System.out.println("getName ============================"+nm[0]);
                return nm;
        }
	public static boolean checkAskForSubmit(String branch,String saleID,Vector vrp) throws Exception
	{
		Mrecord ask = null;
		String sysdate = DateInfo.sysDate();
		boolean found = false ;
		if(sysdate.substring(6,8).compareTo("17") >= 0)
		{
			String nmonth  = DateInfo.nextMonth(sysdate);
			
			ask = CFile.opens("ask"+nmonth.substring(0,6)+"@cbranch");
			ask.start(1);
			for (boolean st = ask.equal(branch+saleID) ; st && (branch+saleID).compareTo(ask.get("branch")+ask.get("ownerSaleID")) == 0;st=ask.next())
			{
				if("IOW".indexOf(ask.get("receiptFlag")) >= 0)
					found = true;
			}
		}
		if(found)
		{
			found = false ;
			ask = CFile.opens("ask"+sysdate.substring(0,6)+"@cbranch");
			ask.start(2);
			for (int i = 0 ; i < vrp.size();i++)
			{
				if(ask.equal(branch+(String)vrp.elementAt(i)))
				{
					found = true ;
					break;
				}
			}
		}
		return found ;
	}
	public static void main(String [] args) throws Exception
	{
		ErrorBox.setExit(false);
		InputStreamReader rin = new  InputStreamReader(System.in);
       		BufferedReader bin = new BufferedReader(rin);

		System.out.println("---------------------------------------------------");
		System.out.println("		Menu  for RpServed			");
		System.out.println("---------------------------------------------------");
		System.out.print(" 1. IND      2. ORD+WHL		:");
		String typeOfPol = bin.readLine();
		if (typeOfPol.charAt(0) == '1')
			typeOfPol = "I";
		else if (typeOfPol.charAt(0) == '2')
			typeOfPol = "O";	
		System.out.print(" Month : ");
		String month = bin.readLine();
		
		ReceiptAgain rpserved = new ReceiptAgain(typeOfPol,month.substring(2,4));
		if(args.length == 2)
			rpserved.sendToPrint(month,args[0],args[1]);
		else
			rpserved.sendToPrint(month,"000","999");
	}
}
