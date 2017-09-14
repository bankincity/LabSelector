package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import insure.Insure;
import java.util.*;
import utility.rteutility.PublicRte;
public class RteReturnPremiumAdvance  implements Task
{
	String userID;
	String sysdate;
	String systime;
	String policyNo;
	String policyType;
	String rpMode;
	String submitNo;
	String rpNo;
	String reason;
	boolean isCancel;
	public Result execute(Object param)
	{
		if(!(param instanceof Object []))
		   return new Result("Invalid Parameter:Object [] {policyType,policyNo,rpMode,userID,submitNo,rpNo}",-1);
		Object [] parameter = (Object []) param;
		policyType = (String)parameter[0];
		policyNo = (String)parameter[1];
		rpMode =(String)parameter[2];
		userID = (String)parameter[3]; 
		submitNo = (String)parameter[4]; 
		rpNo = (String)parameter[5]; 
		isCancel = ((String)parameter[6]).charAt(0) == 'Y'; 
	        reason	= (String)parameter[7];		
		String  mst = policyType.charAt(0) == 'O' ? "ord": policyType.charAt(0) == 'I' ? "ind":"whl";
		Mrecord	mstFile ;
		String [] sArr = null;
		String fileName= "";
		String dueDate = "";
		String mstMode = "";
		sysdate = DateInfo.sysDate();
		systime =  Masic.time("commontable").substring(8);
		try {
			
			/*---------------------- master ------------------*/
			fileName = mst + "mast@mstpolicy";
			mstFile = CFile.opens(fileName);
System.out.println("testYah");
			if (!mstFile.equal(policyNo))
			   initRecord(mstFile);
			if (policyType.charAt(0) != 'I')
			   mstMode = mstFile.get("mode");
			else 
			   mstMode = "0";
/*			if (rpMode.compareTo(mstFile.get("mode")) != 0)
			   return  new Result(M.stou("ไม่สามารถแก้ไขหน้า Master ได้เนื่องจากวิธีการชำระแตกต่างกัน"),1);*/
			sArr = getLastPayPeriod(mstMode,rpMode,policyNo,policyType,
						   mstFile.get("policyStatus1"));

                        String oldMode = sArr[3];
                        String newMode = mstMode;
System.out.println("test 1 ++++++ newMode = " + newMode);
//System.out.println("test 1 ++++++ mstFile.get(Mode) = " + mstFile.get("mode"));
                        String payPeriod = sArr[0];
                        String newPayPeriod = payPeriod;
			System.out.println("start payPeriod = " + newPayPeriod);
			System.out.println("oldMode = " + oldMode + "      newMode = " + newMode);
			/*----------------- check mode -----------------*/
			
                        if (policyType.charAt(0) != 'I'){
                           String  p = payPeriod.substring(2,4);
        //                   oldMode = "4";
        //                   newMode = "2";
                           if (oldMode != newMode){
                              if (M.itis(newMode,'0'))
                                 newMode = "12";
                              switch (oldMode.charAt(0)){
                                case    '0' :     
                                                p = M.divide(payPeriod.substring(2,4),M.divide("12",newMode,0),0);
                                                break;
                                case    '1' :   p = newMode;
                                                break;
                                case    '2' :   if (newMode.compareTo("1") == 0)
                                                   p = "01";
                                                else  
                                                   p = M.multiply(payPeriod.substring(2,4),M.divide(newMode,oldMode,0),0);
                                                break;
                                case    '4' :   if (newMode.compareTo("12") != 0)
                                                   p = M.divide(M.multiply(payPeriod.substring(2,4),newMode,0),oldMode,0);
                                                else
                                                   p = M.multiply(payPeriod.substring(2,4),M.divide(newMode,oldMode,0),0);
                                                break;
                                default   :     break;
                              }
                              if (p.length() == 1)
                                  newPayPeriod =  payPeriod.substring(0,2) + "0" + p;
                              else
                                  newPayPeriod =  payPeriod.substring(0,2)  + p;
                           }
System.out.println("newPayPeriod = " + newPayPeriod);
                        }
System.out.println("newPayPeriod...exit = " + newPayPeriod);
                        Vector dv = new Vector();
		      	if (policyType.charAt(0) != 'I')
			{
			   dueDate = Insure.nextDueDate(mstFile.get("mode"),mstFile.get("effectiveDate"),sArr[0]);
                           dv.add(new String[] {"payPeriod", newPayPeriod});
                           dv.add(new String[] {"rpNo", sArr[1]});
                           dv.add(new String[] {"payDate", sArr[2]});
                           dv.add(new String[] {"dueDate",dueDate});
			}
		        else
			{
                           dv.add(new String[] {"payPeriod", newPayPeriod});
                           dv.add(new String[] {"rpNo", sArr[1]});
                           dv.add(new String[] {"payDate", sArr[2]});
			}
                        Result rs = PublicRte.getResult("searchmaster","rte.search.master.log.ProcessMasterAndLog",
                                    new Object[] {fileName,
						new String[] {policyType,policyNo,policyNo,"C"},"U",dv,userID,"A"});
                        if (rs.status() != 0)
                                        System.out.println("Update masterFile = "+(String)rs.value());

			/*--------------------- dsubmit ------------------*/
			if (isCancel)
			   dsubEdit();


		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
        String[] getLastPayPeriod(String mode,String rpMode,String policyNo,
					 String typeOfPolicy,String policyStatus) throws Exception
        {
                String payPeriod ;
                if (typeOfPolicy.charAt(0)!='I' && typeOfPolicy.charAt(0)!='X' )
                  payPeriod = "0000";
                else
                  payPeriod = "000000";
                String payDate = "00000000";
                String rpNo = "000000000000";
                String xmode = " ";
                String[] p = { payPeriod, rpNo, payDate, mode };
                String[] o = null;
                Result rs ;
                o = new String[]{ typeOfPolicy, policyNo };
                rs = PublicRte.getResult("blreceipt", "rte.bl.receipt.LastPayment", o);
                p = (String[])rs.value();
System.out.println("getLastPayPeriod..After Rte.."+p[0]+"A"+p[1]+"A"+p[2]+"A"+p[3]);
                if (!M.itis(p[0],'0')){
                        payPeriod = p[0];
                        rpNo = p[1];
                        payDate = p[2];
                        xmode = p[3];

                        if (M.itis(payPeriod,'0')) return (p);
                        if (typeOfPolicy.charAt(0)!='I' && typeOfPolicy.charAt(0)!='X' ) {
                           if ( rpMode.charAt(0)!=xmode.charAt(0) ) {
                                      String yymm = Insure.calPremiumAge(xmode, payPeriod) ;
                                      String newPayPeriod = "000000";
				      int idx = "UEMS".indexOf(policyStatus);
                                      newPayPeriod = Insure.yymmToyrpd(mode,yymm) ;
                                      if (!M.match(2, newPayPeriod,2, "00", 0 ) ) {
                                         payPeriod = newPayPeriod ;
                                      }
                           }
                        }
                }
                p[0] = payPeriod;
                p[1] = rpNo;
                p[2] = payDate;
		p[3] = xmode;
System.out.println("getLastPayPeriod....End"+p[0]+" "+p[1]+" "+p[2]);
                return (p);
        }
	/*-------------------------------------------------*/
        void initRecord(Record r)
        {
                String[] fld = r.layout().fieldName();
                for (int i=0; i<fld.length; i++) {
                    if (r.layout().type(fld[i]) == 'N') {
                       if (r.layout().scale(fld[i]) == 0)
                          r.set(fld[i], "0");
                       else
                          r.set(fld[i], "0." + M.clears('0', r.layout().scale(fld[i])));
                    }
                    else if (fld[i].endsWith("Date"))
                       r.set(fld[i], M.clears('0', r.layout().length(fld[i])));
                    else
                       r.set(fld[i], M.clears(' ', r.layout().length(fld[i])));
                }
        }

	/*----------------------------------------------------------------------------------
	  ถ้าแก้ method นี้แจ้งพี่กิ่งเพื่อแก้ /c/rte/bl/claim/claim/PostReturnPremium ด้วย 
						พี่กิ่ง  8/12/2552
	  ---------------------------------------------------------------------------------*/
        void editData(Mrecord ds) throws Exception // editdSubmit(String submitNo , String rpNo, String payDate, String userID)
        {
                String oldAdjustStatus = ds.get("adjustStatus");
                String oldAdjustDate = ds.get("adjustDate");
                ds.set("adjustStatus","C");
                ds.set("adjustDate",DateInfo.sysDate());
System.out.println("insertDsubmitRemark .. to");
                insertDsubmitRemark(new String[] {reason + M.stou(" เปลี่ยนadjustStatus") + oldAdjustStatus + "->C" + " oldAdjustDate=" + DateInfo.formatDate(1,oldAdjustDate)},submitNo);
System.out.println("insertDsubmitRemark .. exit");
                if (!ds.update())
                   throw new Exception("Can not update dsubmitFile");

        }

	/*----------------------------------------------------------------------------------
	  ถ้าแก้ method นี้แจ้งพี่กิ่งเพื่อแก้ /c/rte/bl/claim/claim/PostReturnPremium ด้วย 
						พี่กิ่ง  8/12/2552
	  ---------------------------------------------------------------------------------*/
        void insertDsubmitRemark(String[] sr,String submitNo) throws Exception
        {
System.out.println("insertDsubmitRemark .. start");
                Mrecord rk = CFile.opens("submitremark@cbranch");
                String sysdate = DateInfo.sysDate();
                String seqStart = "00";
                if (rk.equalLess(submitNo+sysdate+"99") &&
                    rk.get("submitNo").compareTo(submitNo)==0 &&
                    rk.get("sysDate").compareTo(sysdate)==0)
                        seqStart = rk.get("seqNo");
                for (int i=0;i<=sr.length - 1 ;i++) {
                        String rkString  =  sr[i];
                        seqStart = M.inc(seqStart);
                        rk.set("submitNo", submitNo);
                        rk.set("sysDate", sysdate);
                        rk.set("seqNo", seqStart);
                        rk.set("userID",userID);
                        rk.set("description",rkString);
                        rk.set("reserve"," ");
                        if (!rk.insert())
                           throw new AppException("RteSubmitAgentChange(update):submitremark "+
                                BranchMsg.InsertFail+":"+rk.get("submitNo"));
                }
                rk.close();
System.out.println("insertDsubmitRemark .. end");
        }

	/*----------------------------------------------------------------------------------
	  ถ้าแก้ method นี้แจ้งพี่กิ่งเพื่อแก้ /c/rte/bl/claim/claim/PostReturnPremium ด้วย 
						พี่กิ่ง  8/12/2552
	  ---------------------------------------------------------------------------------*/
        void dsubEdit() throws Exception  // dsubName(String submitNo, String rpNo)
        {
		boolean flagEdit = false;
                Mrecord dsubFile = null;
                dsubFile = CFile.opens("dsubmit@cbranch");
                boolean ok = dsubFile.equalGreat(submitNo + rpNo);
System.out.println("fileName#1 = " + dsubFile.name());
                while (ok && dsubFile.get("submitNo").compareTo(submitNo) == 0 && dsubFile.get("rpNo").compareTo(rpNo) == 0 )
                {
                   if (dsubFile.get("tempPolicyNo").compareTo(policyNo) == 0)
                   {
		      flagEdit = true;
                      //if (dsubFile.get("currentStatus").charAt(0) == 'B')
                      //{
System.out.println("Edit Data submitNo = " + dsubFile.get("submitNo") + "  : rpNo = " + dsubFile.get("rpNo"));
                          editData(dsubFile);
System.out.println("exit editData");
                      //}
//                    dsubFile.close();
 //                   return;
                   }
                   ok = dsubFile.next();
                }
                dsubFile.close();
		if (flagEdit)
		   return;
System.out.println(Integer.parseInt(DateInfo.sysDate().substring(2,4)));

                String yy = DateInfo.sysDate().substring(2,4);
		int count = 0;
                while (count < 3)
                {
System.out.println ("to  " + dsubFile.name());
		     if (!CFile.isFileExist("dsubmit" + yy + "@cbranch")) {
		        count++;
			continue;
		     }
                     dsubFile = CFile.opens("dsubmit" + yy + "@cbranch");
                     ok = dsubFile.equalGreat(submitNo + rpNo);
                      /*----------------------------*/
                      while (ok && dsubFile.get("submitNo").compareTo(submitNo) == 0 &&
                             dsubFile.get("rpNo").compareTo(rpNo) == 0 )
                      {
                         if (dsubFile.get("tempPolicyNo").compareTo(policyNo) == 0)
                         {
System.out.println("fileName#2 = " + dsubFile.name());
			    flagEdit = true;
                            //if (dsubFile.get("currentStatus").charAt(0) == 'B')
                            //{
                               editData(dsubFile);
                            //}
//                            dsubFile.close();
//                            return;
                         }
                         ok = dsubFile.next();
                      }
                      /*----------------------------*/
                      dsubFile.close();
		      if (flagEdit)
		          return;
                      yy = M.dec(yy);
                }
System.out.println("1 flagEdit.." + flagEdit);
                if (!flagEdit) {
                   Mrecord nbFile =  CFile.opens("nbapplication@cbranch");
                   if (!nbFile.start(3))
                       throw new Exception("not start key 3 " + nbFile.name() + " error =" + nbFile.lastError());
                   if (!nbFile.equal(policyNo))
                       throw new Exception("no data in nbapplication@cbranch");
                   dsubFile = CFile.opens("dsubmit@cbranch");
                   if (!dsubFile.start(1))
                       throw new Exception("not start key 1 " + dsubFile.name() + " error =" + dsubFile.lastError());
                   if (dsubFile.equal(nbFile.get("tempPolicyNo"))) {
                          flagEdit = true;
                          editData(dsubFile);
                   }
                   dsubFile.close();
                   if (flagEdit)
                      return;
                  yy = DateInfo.sysDate().substring(2,4);
                  count = 0;
                  while (count < 3)
                  {
                     if (!CFile.isFileExist("dsubmit" + yy + "@cbranch"))
                     {
                        count++;
                        yy = M.dec(yy);
                        continue;
                     }
                     dsubFile = CFile.opens("dsubmit" + yy + "@cbranch");
                     if (!dsubFile.start(1))
                       throw new Exception("not start key 1 " + dsubFile.name() + " error =" + dsubFile.lastError());
                     if (dsubFile.equal(nbFile.get("tempPolicyNo"))) {
                          flagEdit = true;
                          editData(dsubFile);
                     }
                     dsubFile.close();
System.out.println("2 flagEdit.." + flagEdit);
                     if (flagEdit)
                        return;
                      yy = M.dec(yy);
                   }
                }
System.out.println("3 flagEdit.." + flagEdit);

        }

























	/*----------------------------------------------------------------------------------
	  ถ้าแก้ method นี้แจ้งพี่กิ่งเพื่อแก้ /c/rte/bl/claim/claim/PostReturnPremium ด้วย 
						พี่กิ่ง  28/8/2552
	  ---------------------------------------------------------------------------------*/
/*	void editdSubmit(String submitNo , String rpNo) throws Exception
	{
System.out.println("submitNO = " + submitNo  + ":  rpNo = " + rpNo);
		String key = submitNo + rpNo;
		String fileName = dsubName(submitNo,rpNo);
		if (fileName.length() == 0){	
		   System.out.println("not found in file");
                   return;
		}   
		Mrecord dsubFile = CFile.opens(fileName);
		
		if (!dsubFile.equalLess(key + "999")||dsubFile.get("currentStatus").charAt(0) != 'B' ){
System.out.println("is not ok data becuse currentStatus" + dsubFile.get("currentStatus") +
		   "|" + "submitNo = " + dsubFile.get("submitNo") + "| rpNo = " + dsubFile.get("rpNo"));
                   return;
		}
		String oldAdjustStatus = dsubFile.get("adjustStatus");
		String oldAdjustDate = dsubFile.get("adjustDate");
		dsubFile.set("adjustStatus","C");
		//dsubFile.set("adjustDate",DateInfo.sysDate());
		insertDsubmitRemark(new String[] {M.stou("บอกล้างต่อสัญญา (สินไหม) เปลี่ยน adjustStatus ") + oldAdjustStatus + "-->  C" + " oldAdjustDate  = " + oldAdjustDate},submitNo);
		if (!dsubFile.update())
		   throw new Exception("Can not update dsubmitFile");
		
	}
	/*------------------------------------------------------------------------------------
	  ถ้าแก้ method นี้แจ้งพี่กิ่งเพื่อแก้ /c/rte/bl/claim/claim/PostReturnPremium ด้วย 
						พี่กิ่ง  28/8/2552
	  -----------------------------------------------------------------------------------*/
/*        void insertDsubmitRemark(String[] sr,String submitNo) throws Exception
        {
                Mrecord rk = CFile.opens("submitremark@cbranch");
                String sysdate = DateInfo.sysDate();
                String seqStart = "00";
                if (rk.equalLess(submitNo+sysdate+"99") &&
                    rk.get("submitNo").compareTo(submitNo)==0 &&
                    rk.get("sysDate").compareTo(sysdate)==0)
                        seqStart = rk.get("seqNo");
                for (int i=0;i<sr.length - 1 ;i++) {
                        String rkString  =  sr[i];
                        seqStart = M.inc(seqStart);
                        rk.set("submitNo", submitNo);
                        rk.set("sysDate", sysdate);
                        rk.set("seqNo", seqStart);
                        rk.set("userID",userID);
                        rk.set("description",rkString);
                        rk.set("reserve"," ");
                        if (!rk.insert())
                           throw new AppException("RteSubmitAgentChange(update):submitremark "+
                                BranchMsg.InsertFail+":"+rk.get("submitNo"));
                }
                rk.close();
        }
	/*------------------------------------------------------------------------------------
	  ถ้าแก้ method นี้แจ้งพี่กิ่งเพื่อแก้ /c/rte/bl/claim/claim/PostReturnPremium ด้วย 
						พี่กิ่ง  28/8/2552
	  ------------------------------------------------------------------------------------*/
/*	String dsubName(String submitNo , String rpNo) throws Exception
	{ 	
		String key = submitNo + rpNo;
		Mrecord dsubFile = null;
		dsubFile = CFile.opens("dsubmit@cbranch");
System.out.println("fileName#1 = " + dsubFile.name());
		if (dsubFile.equalLess(key + "999") && dsubFile.get("submitNo").compareTo(submitNo) == 0 &&
		    dsubFile.get("rpNo").compareTo(rpNo) == 0 ) 
                   return(dsubFile.name());
		dsubFile.close();
System.out.println(Integer.parseInt(DateInfo.sysDate().substring(2,4)));	
		
                String yy = DateInfo.sysDate().substring(2,4);
                while(CFile.isFileExist("dsubmit" + yy + "@cbranch"))
                {
System.out.print("to dsubmityy@cbranch");
                        dsubFile = CFile.opens("dsubmit" + yy + "@cbranch");
System.out.println("fileName#2 = " + dsubFile.name());
	                if (dsubFile.equalLess(key + "999") && dsubFile.get("submitNo").compareTo(submitNo) == 0 &&
        	            dsubFile.get("rpNo").compareTo(rpNo) == 0 )
                            return(dsubFile.name());
                	dsubFile.close();
			yy = M.dec(yy);
		}
		return("");	
	}
*/
}
