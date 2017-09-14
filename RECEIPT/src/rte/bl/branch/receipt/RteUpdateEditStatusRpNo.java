package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteUpdateEditStatusRpNo implements Task
{
 	Mrecord rp;
 	Mrecord edit;
	String type;
	String payPeriod;
	String rpNo;
	String status;
	String date;
	String effectiveDate;
	String payDate;
	String submitNo;
	String policyNo;
	String user;
	String branch;
	String requestDate;
        String reasonCode;
 	public Result execute(Object param)
 	{
 		if(! (param instanceof Object[]))
 			return new Result("Invalid Parameter : Object []",-1);
		Object[] pa = (Object[])param;          
		type = (String)pa[0];
		policyNo = (String)pa[1];
		payPeriod = (String)pa[2];
		rpNo = (String)pa[3];
		status = (String)pa[4];
		date = (String)pa[5];
		effectiveDate = (String)pa[6];
		payDate = (String)pa[7];
		submitNo = (String)pa[8];
		user = (String)pa[9];
		branch = (String)pa[10];
		requestDate = (String)pa[11];
	        reasonCode = (String)pa[12];
	/*	askUser	= (String)pa[10];
		askBranch = (String)pa[11];*/
		boolean a = false;
 		try
 		{
			switch (type.charAt(0))
 			{
 				case 'I' : 
 					System.out.println("----------case I------>");
 					rp = CFile.opens("irctrl@receipt");
 					break;
 				case 'O' :
 					System.out.println("-----------case O------>");
 					rp = CFile.opens("orctrl@receipt");
 					break;
 				case 'W' :
 					System.out.println("-----------case W------>");
 					rp = CFile.opens("wrctrl@receipt");
 					break;
 				default  :;
 			}
			rp.start(1);
			String key = policyNo+payPeriod+rpNo;
 			System.out.println("----------------->L1---"+key);
			if(rp.equal(key))
			{
 				System.out.println("----------------->L2");
		//		String seq = "";
		//		edit = CFile.opens("editreceipt@receipt");
				edit = CFile.opens("helpdeskedit@insuredocument");
		/*		if(edit.less(policyNo+"999"))
				{
					if(policyNo.compareTo(edit.get("policyNo")) == 0)
					{
						seq = M.inc(edit.get("seq"));
						setMessage(seq,rp.get("currentStatus"),rp.get("effectiveDate"),rp.get("sysDate"),rp.get("payDate"),rp.get("submitNo"));
					}
					else {
						seq = "001";
						setMessage(seq,rp.get("currentStatus"),rp.get("effectiveDate"),rp.get("sysDate"),rp.get("payDate"),rp.get("submitNo"));
					}
				}*/
				if(edit.less(branch+"999"))
				{
 				System.out.println("----------------->L3");
					setMessage(rp.get("currentStatus"),rp.get("effectiveDate"),rp.get("sysDate"),rp.get("payDate"),rp.get("submitNo"),rp.get("requestDate"));
				}
				
				rp.set("sysDate",date);
				rp.set("currentStatus",status);
				rp.set("effectiveDate",effectiveDate);
				rp.set("payDate",payDate);
				rp.set("submitNo",submitNo);
				rp.set("requestDate",requestDate);
                                rp.set("reasonCode",reasonCode);
				a = rp.update();
					
 			}
			return new Result(new Boolean(a),0);	
 		}
 		catch (Exception e)
 		{
 			return new Result(e.getMessage(),2);
 		}
 	}

/*	public void setMessage(String seq,String fstatus,String feffectiveDate,String fdate,String fpayDate,String fsubmitNo)throws Exception
	{
		String message = "";
		if(status.compareTo(rp.get("currentStatus")) != 0)
		{
			message = M.stou("แก้ไขสถานะจาก  ")+fstatus+M.stou("  เป็น  ")+status+M.stou("  โดย  ")+user;
			setEdit(policyNo,seq,message);
			seq = M.inc(edit.get("seq"));

		}
		if(effectiveDate.compareTo(rp.get("effectiveDate")) != 0)
		{
			message = M.stou("แก้ไขวันเริ่มสัญญาจาก  ")+feffectiveDate+M.stou("  เป็น  ")+effectiveDate+M.stou("  โดย  ")+user;
			setEdit(policyNo,seq,message);
			seq = M.inc(edit.get("seq"));
			
		}
		if(date.compareTo(rp.get("sysDate")) != 0)
		{
			message = M.stou("แก้ไขวันที่สถานะจาก  ")+fdate+M.stou("  เป็น  ")+date+M.stou("  โดย  ")+user;
			setEdit(policyNo,seq,message);
			seq = M.inc(edit.get("seq"));

		}
		if(payDate.compareTo(rp.get("payDate")) != 0)
		{
			message = M.stou("แก้ไขวันที่รับเบี้ยจาก  ")+fpayDate+M.stou("  เป็น  ")+payDate+M.stou("  โดย  ")+user;
			setEdit(policyNo,seq,message);
			seq = M.inc(edit.get("seq"));

		}
		if(submitNo.compareTo(rp.get("submitNo")) != 0)
		{
			message = M.stou("แก้ไขเลขที่นำส่งจาก  ")+fsubmitNo+M.stou("  เป็น  ")+submitNo+M.stou("  โดย  ")+user;
			setEdit(policyNo,seq,message);
			seq = M.inc(edit.get("seq"));

		}
	}
	*/
	public void setMessage(String fstatus,String feffectiveDate,String fdate,String fpayDate,String fsubmitNo,String rdate)throws Exception
	{
		String message = "";
		if(status.compareTo(rp.get("currentStatus")) != 0)
		{
			message = rpNo+M.stou("แก้ไขสถานะจาก  ")+fstatus+M.stou("  เป็น  ")+status+M.stou("  โดย  ")+user;
			setEdit(branch,message);

		}
		if(effectiveDate.compareTo(rp.get("effectiveDate")) != 0)
		{
			message = rpNo+M.stou("แก้ไขวันเริ่มสัญญาจาก  ")+feffectiveDate+M.stou("  เป็น  ")+effectiveDate+M.stou("  โดย  ")+user;
			setEdit(branch,message);
			
		}
		if(date.compareTo(rp.get("sysDate")) != 0)
		{
			message = rpNo+M.stou("แก้ไขวันที่สถานะจาก  ")+fdate+M.stou("  เป็น  ")+date+M.stou("  โดย  ")+user;
			setEdit(branch,message);

		}
		if(payDate.compareTo(rp.get("payDate")) != 0)
		{
			message = rpNo+M.stou("แก้ไขวันที่รับเบี้ยจาก  ")+fpayDate+M.stou("  เป็น  ")+payDate+M.stou("  โดย  ")+user;
			setEdit(branch,message);

		}
		if(submitNo.compareTo(rp.get("submitNo")) != 0)
		{
			message = rpNo+M.stou("แก้ไขเลขที่นำส่งจาก  ")+fsubmitNo+M.stou("  เป็น  ")+submitNo+M.stou("  โดย  ")+user;
			setEdit(branch,message);

		}
		if(requestDate.compareTo(rp.get("requestDate")) != 0)
		{
			message = rpNo+M.stou("แก้ไขวันที่เบิกจาก  ")+rdate+M.stou("  เป็น  ")+requestDate+M.stou("  โดย  ")+user;
			setEdit(branch,message);
			
		}

	}
/*	public void setEdit(String policy,String seq,String message)throws Exception
 	{
 		System.out.println("policyNo-------------------"+policyNo);
 		System.out.println("seq-------------------"+seq);
 		System.out.println("message-------------------"+message);
 		edit.set("policyNo",policy);
 		edit.set("seq",seq);
 		edit.set("message",message);
 		edit.insert();
 
 	}*/
	public void setEdit(String branch,String message)throws Exception
	{
 		edit.set("branch",branch);
 		edit.set("sysDate",DateInfo.sysDate());
 		edit.set("msg",message);
 		edit.insert();
	}
}



