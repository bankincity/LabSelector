package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import utility.support.MyVector;
public class RteUpdateCurrentRpFormByOperate  implements Task
{
	Mrecord receiptform;	
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {typeOfPol,printerCode,branch,userID,flagNew}",-1);
		String  [] parameter = (String []) param;
		String printer  = parameter[0];
		String printDate = parameter[1];
		String branch = parameter[2];
		if(branch.length() == 3)
			branch+=" ";
		String docCode =  parameter[3];
		String startNo =  parameter[4];
		String startPrintNo =  parameter[5];
		String lastPrintNo =  parameter[6];
		try {
			receiptform = Masic.opens("receiptform@cbranch");
			receiptform.start(1);
			if(receiptform.equal(printer+printDate+branch+docCode+startNo))
			{
				receiptform.set("startPrintNo",startPrintNo);
				receiptform.set("lastPrintNo",lastPrintNo);
				receiptform.update();
			}
		}
		catch(Exception e)
		{
			return new Result(e.getMessage(),2);
		}
		return new Result("",0);
		
	}
}
