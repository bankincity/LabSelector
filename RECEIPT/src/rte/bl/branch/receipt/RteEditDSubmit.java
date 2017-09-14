package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import insure.Insure;
import java.util.*;
import utility.rteutility.PublicRte;
public class RteEditDSubmit  implements Task
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
		sysdate = DateInfo.sysDate();
		systime =  Masic.time("commontable").substring(8);
		try {
			
			/*--------------------- dsubmit ------------------*/
		        dsubEdit();	


		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
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
						พี่กิ่ง  28/8/2552
	  ---------------------------------------------------------------------------------*/
	void editData(Mrecord ds) throws Exception // editdSubmit(String submitNo , String rpNo, String payDate, String userID)
	{
System.out.println("editData  .. to");
		String oldAdjustStatus = ds.get("adjustStatus");
		String oldAdjustDate = ds.get("adjustDate");
		ds.set("adjustStatus","C");
		ds.set("adjustDate",DateInfo.sysDate());
System.out.println("insertDsubmitRemark .. to");
		insertDsubmitRemark(new String[] { reason + M.stou("เปลี่ยนadjustStatus") + oldAdjustStatus + "->C" + " oldAdjustDate=" + DateInfo.formatDate(1,oldAdjustDate)},submitNo);
System.out.println("insertDsubmitRemark .. exit");
		if (!ds.update())
		   throw new Exception("Can not update dsubmitFile");
		
	}
	/*------------------------------------------------------------------------------------
	  ถ้าแก้ method นี้แจ้งพี่กิ่งเพื่อแก้ /c/rte/bl/claim/claim/PostReturnPremium ด้วย 
						พี่กิ่ง  28/8/2552
	  -----------------------------------------------------------------------------------*/
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
                        if (!rk.insert()) {
System.out.println("can not insert .. ");
                           throw new AppException("RteSubmitAgentChange(update):submitremark "+
                                BranchMsg.InsertFail+":"+rk.get("submitNo"));	
			}
System.out.println("submitNo .. "  + submitNo);
System.out.println("description .. "  + rkString);
                }
                rk.close();
System.out.println("insertDsubmitRemark .. end");
        }
	/*------------------------------------------------------------------------------------
	  ถ้าแก้ method นี้แจ้งพี่กิ่งเพื่อแก้ /c/rte/bl/claim/claim/PostReturnPremium ด้วย 
						พี่กิ่ง  28/8/2552
	  ------------------------------------------------------------------------------------*/
	void dsubEdit() throws Exception  // dsubName(String submitNo, String rpNo)
	{ 
		boolean flagEdit = false;	
		Mrecord dsubFile = null;
		dsubFile = CFile.opens("dsubmit@cbranch");
		boolean ok = dsubFile.equalGreat(submitNo + rpNo);
System.out.println(" 25550402  fileName#1 = " + dsubFile.name());
		while (dsubFile.get("submitNo").compareTo(submitNo) == 0 && dsubFile.get("rpNo").compareTo(rpNo) == 0 )
		{
System.out.println("dsubEdit..1");
		   if (dsubFile.get("tempPolicyNo").compareTo(policyNo) == 0) 
		   {
		      flagEdit = true;   
System.out.println("dsubEdit..2");
		      editData(dsubFile);
System.out.println("dsubEdit..3");
		   }   
		   ok = dsubFile.next();
System.out.println("dsubEdit..4");
		}
		dsubFile.close();
		if (flagEdit)
		    return;
System.out.println(Integer.parseInt(DateInfo.sysDate().substring(2,4)));	
		
                String yy = DateInfo.sysDate().substring(2,4);
		int count = 0;
                while (count < 3)
                {
System.out.print("to dsubmityy@cbranch");
		     if (!CFile.isFileExist("dsubmit" + yy + "@cbranch"))
		     {
			count++;
		        yy = M.dec(yy);
			continue;
		     }
System.out.println("dsubEdit..5");
                     dsubFile = CFile.opens("dsubmit" + yy + "@cbranch");
System.out.println("dsubEdit..5-2   file " + dsubFile.name());
		     ok = dsubFile.equalGreat(submitNo + rpNo);
		      /*----------------------------*/
                      while (ok && dsubFile.get("submitNo").compareTo(submitNo) == 0 && 
		             dsubFile.get("rpNo").compareTo(rpNo) == 0 )
                      {
System.out.println("dsubEdit..6");
                         if (dsubFile.get("tempPolicyNo").compareTo(policyNo) == 0)
                         {
System.out.println(" 25550402 fileName#2 = " + dsubFile.name());
System.out.println("dsubEdit..7");
			    flagEdit = true;   
System.out.println("dsubEdit..8");
                            editData(dsubFile);
System.out.println("dsubEdit..9");
                         }
                         ok = dsubFile.next();
                      }
		      /*----------------------------*/
                      dsubFile.close();
		     if (flagEdit)
		        return;
		      yy = M.dec(yy);
		}
System.out.println("   ");
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
}
