package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import utility.support.MyVector;
import java.util.Vector;
public class RteCancelOrLoseRpForm  implements Task
{
	Mrecord receiptform ;
	String branch;
	String userID;
	Vector vecForm;
	Vector docVec;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {branch,Vector Of {cancelDate,docCode,startNo,lastNo,status},userID,action}",-1);
		Object [] parameter = (Object [])param;
		branch  = (String)parameter[0];
		if(branch.length() == 3)
			branch+=" ";
		docVec = (Vector)parameter[1];
		userID = (String)parameter[2];
		String action = (String)parameter[3];   // 'I' - insert data  'A'-approve data 'D' delete ' F' find data
		try {
			receiptform = CFile.opens("cancelform@insuredocument");
			vecForm = new Vector();
			if(action.charAt(0)  == 'I')
			{
				System.out.println("in RTE.............START INSERT");
				insertCancelData();
				System.out.println("in RTE.............AFTER INSERT");
				if(verror != null && verror.size() > 0)
					return new Result(verror,2);
			}
			else if(action.charAt(0)  == 'A')
			{
				approvedCancelData();	
			}
			else if(action.charAt(0)  == 'D')
			{
				deleteCancelData();	
			}
			else if(action.charAt(0)  == 'F')
			{
				searchCancelData();	
			}
		}
		catch (Exception e)
		{
			if(e == null || e.getMessage() == null)
				return new Result(M.stou("มีข้อมูลบางอย่างเป็น null จาก bl.branch.RteCancelOrLoseRpForm"),1);
			return new Result(e.getMessage(),1);
		}		
		return new Result(vecForm,0);
	}
	private void searchCancelData()
	{
		String [] buff = (String [] )docVec.elementAt(0);
		String skey = branch+buff[1]+buff[0];
		String blank="";
		if(branch.trim().length() == 3)
			blank = " ";
		for (boolean st = receiptform.equalGreat(skey);st;st=receiptform.next())
		{
			if(skey.compareTo(branch+blank+receiptform.get("deptCode")+receiptform.get("docCode")) != 0)
				break;
			vecForm.addElement(receiptform.copy().getBytes());
		}		
	}
	Vector  verror  ;
	private void insertCancelData() throws Exception
	{
		verror = new Vector();
		for (int i = 0 ; i < docVec.size();i++)
		{
			String [] buff = (String [] )docVec.elementAt(i);
			if(checkEverCancel(buff[1],buff[2],buff[3]))
			{
				verror.addElement(new String [] {receiptform.get("startNo"),receiptform.get("lastNo"),receiptform.get("cancelDate"),receiptform.get("userID"),receiptform.get("status")});
				continue;
			}
                        if (branch.compareTo("000") != 0)
                        {
                                if (buff[1].compareTo("03") != 0)
                                        buff[1] = "22";
                        }	
			receiptform.set("deptCode",branch);
			receiptform.set("docCode",buff[1]);
			receiptform.set("cancelDate",buff[0]);
			receiptform.set("startNo",buff[2]);
			receiptform.set("lastNo",buff[3]);
			receiptform.set("status",buff[4]);
			receiptform.set("userID",userID);	
			receiptform.set("approvedDate","00000000");
			receiptform.set("approvedUser","0000000");
			receiptform.insert();
			verror.addElement("Success");
		}
	}
	private boolean  checkEverCancel(String docCode, String startNo,String lastNo) 
	{
		receiptform.start(2);
		if(receiptform.equalGreat(docCode+startNo))
		{
			if(startNo.compareTo(receiptform.get("startNo")) <= 0 
				 && lastNo.compareTo(receiptform.get("startNo")) >= 0 
				 && docCode.compareTo(receiptform.get("docCode")) == 0)
				return true ;
			if(receiptform.previous() && 
				startNo.compareTo(receiptform.get("startNo")) >= 0 && 
				startNo.compareTo(receiptform.get("lastNo")) <= 0  &&
				docCode.compareTo(receiptform.get("docCode")) == 0)
				return true ;				
		}
		else {
			if(receiptform.last() &&
				startNo.compareTo(receiptform.get("startNo")) >= 0 && 
				startNo.compareTo(receiptform.get("lastNo")) <= 0 && 
				docCode.compareTo(receiptform.get("docCode")) == 0)
				return true ;				
		}
		receiptform.start(0);
		return  false;
	}
	private void approvedCancelData() throws Exception
	{
		for (int i = 0 ; i < docVec.size();i++)
		{
			String [] buff = (String [] )docVec.elementAt(i);
                        if (branch.compareTo("000") != 0)
                        {
                                if (buff[1].compareTo("03") != 0)
                                         buff[1] = "22";
                        }	
			if(receiptform.equal(branch+buff[1]+buff[0]+buff[2]))
			{
				receiptform.set("approvedDate",DateInfo.sysDate());
				receiptform.set("approvedUser",userID);
				receiptform.update();
			}
			else 
				throw new Exception(M.stou("ไม่พบข้อมูลที่ระบุ(")+branch+buff[1]+buff[0]+buff[2]+")");
		}
	}
	private void deleteCancelData() throws Exception
	{
		
		for (int i = 0 ; i < docVec.size() ; i++)
		{
			String [] buff = (String [] )docVec.elementAt(i);
			if(receiptform.equal(branch+buff[1]+buff[0]+buff[2]))
			{
				if(userID.compareTo(receiptform.get("approvedUser")) != 0)
					throw new Exception(M.stou("รายการนี้ตรวจสอบโดย ")+receiptform.get("approvedUser")+ M.stou("  ลบข้อมูลไม่ได้"));
				receiptform.delete();
			}
			else 
				throw new Exception(M.stou("ไม่พบข้อมูลที่ระบุ(")+branch+buff[1]+buff[0]+buff[2]+")");
		}
	}
}
