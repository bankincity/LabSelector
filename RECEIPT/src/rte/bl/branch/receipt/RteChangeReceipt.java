package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import insure.Insure;
import java.util.*;
import utility.rteutility.PublicRte;
public class RteChangeReceipt implements Task
{
	String 	policyType,
	       	policyNo,
	       	userID,
	       	newRpNo,
	       	newPayPeriod,
		oldRpNo,
	       	oldPayPeriod,
		systime,
		mstType,
		submitNo,
		oldPayDate,
		oldSysDate;
	Record  rOldRp,
		rNewRp;
				
	public Result execute(Object param)
	{
System.out.println("RteChangeReceipt00000");		
		if(!(param instanceof Object []))
		   return new Result("Invalid Parameter:Object [] {policyType,policyNo,userID,newRpNo,newPayPeriod,oldRpNo,newPayPeriod}",-1);
		Object [] parameter = (Object []) param;
System.out.println("RteChangeReceipt..01");
		policyType = (String)parameter[0];
		policyNo = (String)parameter[1];
		userID = (String)parameter[2]; 
		newRpNo = (String)parameter[3]; 
		newPayPeriod = (String)parameter[4]; 
		oldRpNo = (String)parameter[5]; 
		oldPayPeriod = (String)parameter[6]; 
System.out.println("RteChangeReceipt..02");
		mstType = policyType.charAt(0) == 'O' ? "ord": policyType.charAt(0) == 'I' ? "ind":"whl";
		Mrecord	mstFile ;
		String fileName= "";
		String dueDate = "";
		//submitNo = "042000059049";
		systime =  Masic.time("commontable").substring(8);
		try {
			
	       		editReceipt();
System.out.println("RteChangeReceipt..03");
	       		editdSubmit();
System.out.println("RteChangeReceipt..04");
			fileName = mstType + "mast@mstpolicy";
			mstFile = CFile.opens(fileName);
			if (!mstFile.equal(policyNo))
			   initRecord(mstFile);
			if (mstFile.get("rpNo").compareTo(oldRpNo) == 0)
			{
				
System.out.println("RteChangeReceipt03");		
                        	Vector dv = new Vector();
System.out.println("RteChangeReceipt04");		
                               	dv.add(new String[] {"payPeriod", newPayPeriod});
                               	dv.add(new String[] {"rpNo", newRpNo});
				if (policyType.charAt(0) != 'I')
			        {
				   dueDate = Insure.nextDueDate(mstFile.get("mode"),
					     mstFile.get("effectiveDate"),newPayPeriod);
                               	   dv.add(new String[] {"dueDate",dueDate});
				}
System.out.println("RteChangeReceipt05");		
                        	Result rs = PublicRte.getResult("searchmaster","rte.search.master.log.ProcessMasterAndLog",
                                	    new Object[] {fileName,new String[] {policyType,policyNo,policyNo,"C"},
				            "U",dv,userID,"A"});
                        	if (rs.status() != 0)
                                	    System.out.println("Update masterFile = "+(String)rs.value());
                                System.out.println("Update masterFile = "+   rs.status());
System.out.println("RteChangeReceipt06");		
			}

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
	/*-------------------------------------------------*/
	void editdSubmit() throws Exception
	{ 	
		Record rDSub = null;
		String key = submitNo + oldRpNo;
		Mrecord dsubFile = null;
		dsubFile = CFile.opens("dsubmit@cbranch");
System.out.println("dsubFile = " + dsubFile.name());
		if (dsubFile.equalLess(key + "999") && dsubFile.get("submitNo").compareTo(submitNo) == 0 &&
		    dsubFile.get("rpNo").compareTo(oldRpNo) == 0 ) 
		{
		    rDSub = dsubFile.copy();
		    dsubFile.delete();
		    rDSub.set("payPeriod",newPayPeriod);
		    rDSub.set("rpNo",newRpNo);
		    rDSub.set("sysDate",oldSysDate);
		    rDSub.set("payDate",oldPayDate);
		    if (!dsubFile.insert(rDSub))
		       throw new Exception("Can not insert dsubmitFile" + dsubFile.lastError());
		    insertDsubmitRemark(new String[] {"oldPayPeriod " + oldPayPeriod  + "oldRpNo" + oldRpNo}, submitNo);
		    if (!M.itis(rDSub.get("submitLinker"),'0'))
		    {
			String  submitLinker = rDSub.get("submitLinker");
		    	key = submitLinker + oldRpNo;
			boolean ok = dsubFile.equalGreat(key);
			while(ok && dsubFile.get("submitNo").compareTo(submitLinker) == 0 &&
				    dsubFile.get("rpNo").compareTo(oldRpNo) == 0)
			{
				if(dsubFile.get("submitNo").compareTo(submitNo) == 0 && 
				   dsubFile.get("tempPolicyNo").compareTo(policyNo) == 0 &&
				   dsubFile.get("rpNo").compareTo("oldRpNo") == 0)
				{
  	                           rDSub = dsubFile.copy();
                                   dsubFile.delete();
                    		   rDSub.set("payPeriod",newPayPeriod);
                    		   rDSub.set("rpNo",newRpNo);
                    		   rDSub.set("sysDate",oldSysDate);
                    		   rDSub.set("payDate",oldPayDate);
                    		   if (!dsubFile.insert(rDSub))
                                      throw new Exception("Can not insert dsubmitFile" + dsubFile.lastError());
				   ok = false;
		    		   insertDsubmitRemark(new String[] {"oldPayPeriod " + oldPayPeriod  + "oldRpNo" + oldRpNo}, submitLinker);
				   continue;
				}
				ok = dsubFile.next();
			}
		    }
		   dsubFile.close();
		   return;
		}
		for (int yy =  Integer.parseInt(DateInfo.sysDate().substring(2,4)) - 1; yy >= 42 ; yy--)
		{
		dsubFile = CFile.opens("dsubmit" + yy + "@cbranch");
System.out.println("dsubFile = " + dsubFile.name());
		   if (dsubFile.equalLess(key + "999") && dsubFile.get("submitNo").compareTo(submitNo) == 0 &&
		      dsubFile.get("rpNo").compareTo(oldRpNo) == 0 ){
		       rDSub = dsubFile.copy();
		       dsubFile.delete();
		       rDSub.set("payPeriod",newPayPeriod);
		       rDSub.set("rpNo",newRpNo);
		       rDSub.set("sysDate",oldSysDate);
		       rDSub.set("payDate",oldPayDate);
		       if (!dsubFile.insert(rDSub))
		          throw new Exception("Can not insert dsubmitFile" + dsubFile.lastError());
		       insertDsubmitRemark(new String[] {"oldPayPeriod " + oldPayPeriod  + "oldRpNo" + oldRpNo}, submitNo);
                       if (!M.itis(rDSub.get("submitLinker"),'0'))
                       {
                           String  submitLinker = rDSub.get("submitLinker");
                           key = submitLinker + oldRpNo;
                           boolean ok = dsubFile.equalGreat(key);
                           while(ok && dsubFile.get("submitNo").compareTo(submitLinker) == 0 &&
                                    dsubFile.get("rpNo").compareTo(oldRpNo) == 0)
                           {
                                if(dsubFile.get("submitNo").compareTo(submitNo) == 0 &&
                                   dsubFile.get("tempPolicyNo").compareTo(policyNo) == 0 &&
                                   dsubFile.get("rpNo").compareTo("oldRpNo") == 0)
                                {
                                   rDSub = dsubFile.copy();
                                   dsubFile.delete();
                                   rDSub.set("payPeriod",newPayPeriod);
                                   rDSub.set("rpNo",newRpNo);
                                   rDSub.set("sysDate",oldSysDate);
                                   rDSub.set("payDate",oldPayDate);
                                   if (!dsubFile.insert(rDSub))
                                      throw new Exception("Can not insert dsubmitFile" + dsubFile.lastError());
                                   ok = false;
		    		   insertDsubmitRemark(new String[] {"oldPayPeriod " + oldPayPeriod  + "oldRpNo" + oldRpNo}, submitLinker);
                                   continue;
                                }
                                ok = dsubFile.next();
                           }
                       }
		       dsubFile.close();
		       return;
 		   }
		   dsubFile.close();
		}
		throw new Exception("Can not insert dsubmitFile");
	}
	/*-------------------------------------------------*/
        public void insertDsubmitRemark(String[] sr,String submit) throws Exception
        {
                Mrecord rk = CFile.opens("submitremark@cbranch");
                String sysdate = DateInfo.sysDate();
                String seqStart = "00";
                if (rk.equalLess(submitNo+sysdate+"99") &&
                    rk.get("submitNo").compareTo(submit)==0 &&
                    rk.get("sysDate").compareTo(sysdate)==0)
                        seqStart = rk.get("seqNo");
                for (int i=0;i<sr.length - 1 ;i++) {
                        String rkString  =  sr[i];
                        seqStart = M.inc(seqStart);
                        rk.set("submitNo", submit);
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

	/*-------------------------------------------------*/
	void editReceipt() throws Exception
	{
System.out.println("************* editReceipt ******************");
		String nameOldRp = searchReceiptFile(policyNo,oldPayPeriod,oldRpNo,false);
		if (M.itis(rOldRp.get("submitNo"),'0') || rOldRp.get("submitNo").trim().length() == 0)
		   throw new Exception("Can not change receipt becuse submitNo is invalid");
		
		oldSysDate = rOldRp.get("sysDate"); 
		oldPayDate = rOldRp.get("payDate");
System.out.println("************* new ******************");
		String nameNewRp = searchReceiptFile(policyNo,newPayPeriod,newRpNo,true);
printRecord(rNewRp);		

		if (rNewRp.get("originalStatus").compareTo(rOldRp.get("originalStatus")) != 0)
		   throw new Exception("Can not change receipt becuse originalStatus is not equal");
		  
		if (rNewRp.get("premium").compareTo(rOldRp.get("premium")) != 0)
		   throw new Exception("Can not change receipt becuse premium is not equal");

		submitNo = rOldRp.get("submitNo");

		rNewRp.set("effectiveDate",rOldRp.get("effectiveDate"));
		rNewRp.set("payDate",rOldRp.get("payDate"));
		rNewRp.set("sysDate",rOldRp.get("sysDate"));
		rNewRp.set("currentStatus",rOldRp.get("currentStatus"));
		rNewRp.set("time",rOldRp.get("time"));
		rNewRp.set("submitNo",rOldRp.get("submitNo"));
		rNewRp.set("requestDate",rOldRp.get("requestDate"));
		rNewRp.set("gracePeriod",rOldRp.get("gracePeriod"));
		rNewRp.set("submitBranch",rOldRp.get("submitBranch"));
		rNewRp.set("userID",rOldRp.get("userID"));
		rNewRp.set("reasonCode",rOldRp.get("reasonCode"));
		rNewRp.set("moneyOk",rOldRp.get("moneyOk"));
		insertToRpFile(nameNewRp,policyNo ,newPayPeriod ,newRpNo,rNewRp);
	
System.out.println("************* old ******************");
printRecord(rOldRp);		
		rOldRp.set("effectiveDate",rOldRp.get("effectiveDate"));
                rOldRp.set("payDate","00000000");
		rOldRp.set("sysDate",DateInfo.sysDate());
		rOldRp.set("currentStatus","W");
                rOldRp.set("time","0");
                rOldRp.set("submitNo","000000000000");
                rOldRp.set("requestDate","00000000");
                rOldRp.set("gracePeriod","");
                rOldRp.set("printedDate","00000000");
                rOldRp.set("submitBranch","000");
                rOldRp.set("userID","0000000");
                rOldRp.set("reasonCode","00");
                rOldRp.set("moneyOk","");
		insertToRpFile(nameOldRp,policyNo ,oldPayPeriod , oldRpNo,rOldRp);
	}
	/*-------------------------------------------------*/
	void insertToRpFile (String fileName, String policyNo, String payPeriod, String rpNo, Record rData)throws Exception
	{
		Mrecord	recFile = CFile.opens(fileName);
                boolean ok = recFile.equal(rpNo);
                while (ok)
                {
			if (recFile.get("policyNo").compareTo(policyNo) == 0 && 
			   recFile.get("payPeriod").compareTo(payPeriod) == 0 )
			{	
        		        recFile.delete();
printRecord(rData);		
                		recFile.insert(rData);
				return;
			}
                	ok = recFile.next();
                }
		return;
	}
	/*-------------------------------------------------*/
	String searchReceiptFile(String policyNo, String payPeriod, String rpNo, boolean isNew) throws Exception
	{ 	
		Mrecord  recFile = CFile.opens(policyType.toLowerCase() + "rctrl@receipt");
		String showPayPeriod = policyType.charAt(0) == 'I' ? payPeriod.substring(4) + "/" + payPeriod.substring(0,4):
								payPeriod.substring(0,2) + "/" + payPeriod.substring(2);
                if (!recFile.start(1))
                            throw new Exception("error change key1 " + recFile.name() + " " +
                                                recFile.lastError());
                String key = policyNo + payPeriod + rpNo;
System.out.println("key = " + key );
		boolean ok = recFile.equal(key);
System.out.println(" name = " + recFile.name());
		if (ok)
		{
			if (isNew)
			   rNewRp = recFile.copy();
			else   
			   rOldRp = recFile.copy();
			recFile.close();
			return(recFile.name());
		}
                for (int yyyy =  Integer.parseInt(DateInfo.sysDate().substring(0,4)); yyyy >= 2542 ; yyyy--)
                {
		        recFile = CFile.opens(policyType.toLowerCase() + "rctrl" + yyyy + "@receipt");
System.out.println(" name = " + recFile.name());
			 ok = recFile.equal(rpNo);
			while (ok && recFile.get("rpNo").compareTo(rpNo) == 0) 
			{
  	                        if (recFile.get("policyNo").compareTo(policyNo) == 0 &&
        	                   recFile.get("payPeriod").compareTo(payPeriod) == 0 )
                	        {
System.out.println("payPeriod = " + payPeriod);
System.out.println("rpNo = " + rpNo);
                        		if (isNew)
                           	   	   rNewRp = recFile.copy();
                        		else
                                          rOldRp = recFile.copy();
                        	        return(recFile.name());
                        	}
				ok = recFile.next();
			}
                       	recFile.close();
		}
		throw new Exception(M.stou("      ไม่พบเลขที่ใบเสร็จ  ") + rpNo + M.stou("     งวดชำระ ") + showPayPeriod);
	} 
	/*-------------------------------------------------*/
        void printRecord(Record rc)
        {
                String[] fld = rc.layout().fieldName();
                for (int i=0; i < fld.length; i++)
                    System.out.println ( fld[i] + "=" + rc.get(fld[i]) + " ");
                System.out.println();
        }

}
