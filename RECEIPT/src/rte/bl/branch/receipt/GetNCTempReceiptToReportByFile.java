package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.support.DateInfo;
import  utility.cfile.CFile;
import  rte.bl.branch.*;
import  rte.*;
import utility.branch.sales.BraSales;
import utility.rteutility.*;
public class  GetNCTempReceiptToReportByFile implements  InterfaceRpt
{
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("args === "+args.length);
		String remote	= args[0];  // true or false 
                String appserv = args[1];  // application server  <--> braxxxapp
                String filename = args[2]; // masic output stream  
		if(remote.compareTo("false") == 0)
                        PublicRte.setRemote(false);


		// H....S , yyyymm : current ask , branch,cont
		System.out.println("go to search.............");
		getAllRequestedRp(args[3],args[4],args[5],args[6]);		

		System.out.println("End startSearch process! ");
		rte.RteRpt.recToTemp(temptoprint, filename);
                System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		temptoprint.close();
	}
	BraSales bsale ;
	Mrecord irc;
	Mrecord orc;
	Mrecord wrc;
	Mrecord urc;
	Mrecord trc;
	Mrecord ord;
//	Mrecord ul;
	Mrecord whl;
	Mrecord ind;
	Mrecord nask ;
	Mrecord ask ;
	String []  field = {"hcode","gcode","depNo","askDepno","noTrp","noRp","totPrem","hname","gname","aname","oname"};
	int [] len = {5,8,5,5,3,3,9,80,80,80,80};
	TempMasicFile  temptoprint;
	public void getAllRequestedRp(String hcode,String yymmdd,String branch,String cont) throws Exception
	{
		ask = CFile.opens("ask"+yymmdd+"@cbranch");
/*		yyyymm = DateInfo.nextMonth(yymmdd).substring(0,6);
		if(CFile.isFileExist("ask"+yymmdd+"@cbranch"))
		{
		    nask = CFile.opens("ask"+yyyymm+"@cbranch");
		    nask.start(2);	
		}*/
		irc = CFile.opens("irctrl@receipt");
		orc = CFile.opens("orctrl@receipt");
		urc = CFile.opens("ulrctrl@universal");
		wrc = CFile.opens("wrctrl@receipt");		
		trc = CFile.opens("trpctrl@receipt");		
//		ord = CFile.opens("ordmast@mstpolicy");
//		whl = CFile.opens("whlmast@mstpolicy");
//		ind = CFile.opens("indmast@mstpolicy");
		bsale = new BraSales();
		temptoprint = new TempMasicFile("rptbranch",field,len);
		temptoprint.keyField(true,true,new String [] {"hcode","gcode","depNo","askDepno"});
                temptoprint.buildTemp();

		searchRPFromAsk(hcode,branch,cont.charAt(0) == '1');
	}
	String sysdate ;
	public void searchRPFromAsk(String hcode,String branch,boolean cont) throws Exception
	{		
		String saleID ="";
		String askSaleID = "";
		String thcode="";
		String tgcode ="";
		String noTrp = "";
		String noRp = "";
		String totPrem="";
		sysdate = DateInfo.sysDate();
		System.out.println("-------------------------------hcode--"+branch+" ---"+hcode+"---");
		ask.start(1);
		boolean st = ask.equalGreat(branch);
		int  flagSaleError = 0;
		while (st ){
			if(branch.compareTo(ask.get("branch")) != 0)
				break;
				System.out.println("------------ask sale == "+ask.get("askSaleID")+"   "+ask.get("ownerSaleID")+"--------------------");
			saleID  = ask.get("ownerSaleID");
			askSaleID = ask.get("askSaleID");
			noTrp ="000";
			noRp="000";
			totPrem="000000000";
			try {
			
				bsale.getBySalesID(saleID);
				flagSaleError = 1;
				bsale.getStruct();
				thcode = bsale.getSnSeniorRegion("strID"); 
				System.out.println("ask sale == "+ask.get("askSaleID")+"   "+ask.get("ownerSaleID")+" "+ask.get("rpNo")+"    thcode----"+thcode+"  depno--"+bsale.getSnRec("depositNo"));

				if(!cont && hcode.compareTo(thcode) != 0)
				{
					st = ask.great(branch+saleID);
					continue;
				}
				else if (cont && hcode.compareTo(thcode) > 0)
				{
					st = ask.great(branch+saleID);
					continue;

				}		
				flagSaleError = 2;
			}
			catch (Exception e)
			{
				if(e !=null)	
					System.out.println(saleID+"....ERROR ERROR ERROR............"+e.getMessage());
				else
					System.out.println(saleID+".......NUll....... ERROR............");
	
				if(hcode.compareTo("00000") != 0 )
				{
					st = ask.great(branch+saleID);
					continue;
				}
					
			}
			boolean flagwhoreq = false ;
			while (st && saleID.compareTo(ask.get("ownerSaleID")) == 0)
			{
				if( !flagwhoreq && ask.get("ownerSaleID").compareTo(ask.get("askSaleID")) != 0) 
				{
					askSaleID = ask.get("askSaleID");
					flagwhoreq = true ;
				}
				switch (ask.get("receiptFlag").charAt(0)){
					case 'I' : if (CheckAskReceipt.checkUCStatus(irc,ask.get("rpNo"),ask.get("requestDate") ) )
						 {
							if(!inNextAskyyyymm(ask.get("rpNo"),"IX"))
							{
								noRp = M.inc(noRp);	
								totPrem = M.addnum(totPrem,irc.get("premium"));
							}
						 }
						 break;
					case 'O' :
					 	Mrecord ttrc = null;
					 	if (orc.equal(ask.get("rpNo")))
					 	{
							ttrc = orc ;
							System.out.println("found in orc");
					 	}
					 	else if (wrc.equal(ask.get("rpNo")))
					 	{
							ttrc = wrc ;				
							System.out.println("found in wrc");
					 	}
					 	if(ttrc == null)
							break;
					 	if(CheckAskReceipt.checkUCStatus(ttrc,ask.get("rpNo"),ask.get("requestDate") ))
						{
							if(!inNextAskyyyymm(ask.get("rpNo"),"OY"))
							{
								noRp = M.inc(noRp);	
								totPrem = M.addnum(totPrem,ttrc.get("premium"));
							}
						}
						break;	
					case 'W' : if (CheckAskReceipt.checkUCStatus(wrc,ask.get("rpNo"),ask.get("requestDate") ) )
						{
							if(!inNextAskyyyymm(ask.get("rpNo"),"WU"))
							{
								noRp = M.inc(noRp);	
								totPrem = M.addnum(totPrem,wrc.get("premium"));
							}
						}
						break;
					case 'A' : if (CheckAskReceipt.checkUCStatus(urc,ask.get("rpNo"),ask.get("requestDate") ) )
						{
							if(!inNextAskyyyymm(ask.get("rpNo"),"AB"))
							{
								noRp = M.inc(noRp);	
								totPrem = M.addnum(totPrem,urc.get("premium"));
							}
						}
						break;
					case 'T' : if (CheckAskReceipt.checkUCStatus(trc,ask.get("rpNo"),ask.get("requestDate") ) )
						{
							if(!inNextAskyyyymm(ask.get("rpNo"),"TZ"))
							{
								noTrp = M.inc(noTrp);	
							}
						}
						break;
				}
				st = ask.next();
			}
			if (M.ctoi(noTrp) != 0 ||  M.ctoi(noRp) !=0)
			{
				temptoprint.set(' ');
				
				temptoprint.set("depNo",bsale.getSnRec("depositNo"));
				if(flagSaleError == 2)
				{
					temptoprint.set("hcode",bsale.getSnSeniorRegion("strID"));
					temptoprint.set("gcode",bsale.getSnRegion("strID"));
					temptoprint.set("hname",bsale.getSnSeniorRegion("preName").trim() +bsale.getSnSeniorRegion("firstName").trim()+" "+bsale.getSnSeniorRegion("lastName").trim());
					temptoprint.set("gname",bsale.getSnRegion("preName").trim() +bsale.getSnRegion("firstName").trim()+" "+bsale.getSnRegion("lastName").trim());
					flagSaleError--;
				}
				else {
					temptoprint.set("hname",M.stou("หาโครงสร้างไม่พบ ")+saleID);
				}
				if(flagSaleError == 1)
					temptoprint.set("oname",bsale.getSnRec("preName").trim() +bsale.getSnRec("firstName").trim()+" "+bsale.getSnRec("lastName").trim());
				else {
					
					temptoprint.set("oname",M.stou("หาฝ่ายขายไม่พบ  ")+saleID);
				}
					
				temptoprint.set("noTrp",M.setlen(noTrp,3));
				temptoprint.set("noRp",M.setlen(noRp,3));
				temptoprint.set("totPrem",M.setlen(totPrem,9));
				try {
					bsale.getBySalesID(askSaleID);
					temptoprint.set("askDepno",bsale.getSnRec("depositNo"));
					temptoprint.set("aname",bsale.getSnRec("preName").trim() +bsale.getSnRec("firstName").trim()+" "+bsale.getSnRec("lastName").trim());
				}
				catch (Exception e)
				{
				}
				temptoprint.insert();
			}
			System.out.println("-----------before Great..............."+saleID+"*****************************************-------");
			st = ask.great(branch+saleID);
		}
	}
	boolean inNextAskyyyymm(String rpNo,String status)
	{
		if (nask != null)
		{
			for (boolean st = nask.equal(rpNo) ; st && rpNo.compareTo(nask.get("rpNo")) == 0;st =nask.next())
			{
				if(status.indexOf(nask.get("receiptFlag")) >= 0 )
					return true;	
			} 
		}
		return false ;
	}
        public static void main(String args[]) throws Exception
        {
                
                GetNCTempReceiptToReportByFile ncrec = new GetNCTempReceiptToReportByFile();
                ncrec.getAllRequestedRp(args[0],args[1],args[2],args[3]);
        } 
}

