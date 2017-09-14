package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import utility.support.MyVector;
public class RteGetCurrentRpFormByOperate  implements Task
{
	Mrecord usedreceiptform ;
	Mrecord receiptform;	
	String useStartRp ;
	String useEndRp ;
	String printDate;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {typeOfPol,printerCode,branch,userID,flagNew}",-1);
		String  [] parameter = (String []) param;
		String docCode = parameter[0];
		if(docCode.charAt(0) =='O')
			docCode = "02";
		else if(docCode.charAt(0) =='I')
			docCode = "01";
		else if(docCode.charAt(0) =='B')
			docCode = "17";
		else if(docCode.charAt(0) =='A')
			docCode = "06";
		else if(docCode.charAt(0) =='a')
			docCode = "18";
		else if(docCode.charAt(0) =='b')
			docCode = "22";
		else if(docCode.charAt(0) =='T')
			docCode = "03";
		String printer = parameter[1];
		String branch = parameter[2];
		String userID = parameter[3];
		String flagNew = parameter[4];
		if(flagNew.compareTo("used") == 0 || flagNew.compareTo("del") == 0 )
		{
			useStartRp = parameter[5];
			useEndRp = parameter[6];
			printDate = parameter[7];
		}
		receiptform = Masic.opens("receiptform@cbranch");
		usedreceiptform = Masic.opens("usedreceiptform@cbranch");
		Record rec = null;
		Record lrec = null;
		try {
			if(flagNew.compareTo("old") == 0 || flagNew.compareTo("used") == 0 )
			{
				System.out.println(flagNew +".................."+docCode+" "+printer+" "+branch+" "+userID);
				for (boolean st = receiptform.great(printer+branch);st && (printer+branch).compareTo(receiptform.get("printer")+receiptform.get("deptCode")) == 0;st=receiptform.next())
				{
					if(branch.compareTo(receiptform.get("deptCode")) != 0)
						continue;
					
					if(flagNew.compareTo("used") == 0  && userID.compareTo("0001230") == 0)
					{
						if(docCode.compareTo(receiptform.get("docCode")) == 0)
						{
							if(receiptform.get("startNo").compareTo(useStartRp) <= 0 && 
							   receiptform.get("lastNo").compareTo(useStartRp) >= 0 )
							{								
								rec = receiptform.copy();
								break;
							}
						}				
					}
					else if(flagNew.compareTo("old") == 0)
					{
						 if(receiptform.get("startPrintNo").compareTo(receiptform.get("lastNo")) > 0)
                                                	continue;					
						if(docCode.compareTo("00") == 0|| docCode.compareTo(receiptform.get("docCode")) == 0)
						{
							rec = receiptform.copy();
							break;
						}
					}
				}
				if(rec == null && flagNew.compareTo("used") == 0)
				{
					throw new Exception(M.stou("เอกสารทีบันทึกไม่อยู่ในสต๊อกเอกสารเครื่องพิมพ์ที่ ")+printer);
				}
				if(rec != null && flagNew.compareTo("used") == 0)
				{
					System.out.println("use..........."+useEndRp+ "   "+receiptform.get("lastPrintNo"));
		//			if(useEndRp.compareTo(receiptform.get("lastPrintNo")) > 0)
		//				throw new Exception(M.stou("เอกสารทีบันทึกไม่อยู่ในสต๊อกเอกสารเครื่องพิมพ์ที่ ")+printer);	
					usedreceiptform.set("printer",receiptform.get("printer"));			
					usedreceiptform.set("printDate",printDate);
					usedreceiptform.set("deptCode",receiptform.get("deptCode"));
					usedreceiptform.set("docCode",receiptform.get("docCode"));		
					usedreceiptform.set("startNo",receiptform.get("startNo"));
					usedreceiptform.set("startPrintNo",useStartRp);
					usedreceiptform.set("lastPrintNo",useEndRp);
					usedreceiptform.set("userID",userID);
					Record insrec = usedreceiptform.copy();
					insrec.set("flagStock","O");
					if(!usedreceiptform.insert(insrec))
						throw new Exception (M.stou("insert usereceiptform@cbranch  iserror  ")+M.itoc(usedreceiptform.lastError()));	
					System.out.println("insert ok");
				}
			}
			else if (flagNew.compareTo("new") == 0)
			{


			}
			else if (flagNew.compareTo("del") == 0)
			{
				System.out.println("bingo.................................start delete " +useStartRp+" "+useEndRp);
				deleteUsedStock(printer,branch,docCode,useStartRp,useEndRp);
				rec = null;
				System.out.println("bingo.................................end delete");
			}
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);	
		}
		if(rec != null)
		{
			return new Result(rec.getBytes(),0);
		}
		if(lrec != null)
			return new Result(lrec.getBytes(),0);
		
		return new Result(null,0);
		
	}
	private void deleteUsedStock(String printer,String branch,String docCode,String startPrintNo,String lastPrintNo) throws Exception
	{
		usedreceiptform.start(3);
		String blank ="";
		if(branch.length() == 3)
			blank = " ";
		System.out.println("delbyoperate..............."+branch+blank+docCode+printer+startPrintNo); 
		if(usedreceiptform.equal(branch+blank+docCode+printer+startPrintNo))
		{
			usedreceiptform.delete();
		}
		if(docCode.compareTo("02") == 0)
			docCode = "06";
		else if(docCode.compareTo("17") == 0)
			docCode = "18";	
		System.out.println("delbyoperate..............."+branch+blank+docCode+printer+startPrintNo); 
		if(usedreceiptform.equal(branch+blank+docCode+printer+startPrintNo))
		{
			usedreceiptform.delete();
		}
		usedreceiptform.start(0);
	}
}
