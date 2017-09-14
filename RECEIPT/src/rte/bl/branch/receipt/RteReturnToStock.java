package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;

public class RteReturnToStock  implements Task
{
	Mrecord stockdoc ;
        Mrecord recform;
	public Result execute(Object param)
	{
		if(! (param instanceof String []))
			return new Result("Invalid Parameter  : String [] {deptCode,docCode,sendDate,receiveDate,startNo,lastNo,currentNo,currentDate,newStokStatus ,oldStockStatus,userID,printer }",-1);
		String [] parameter = (String []) param;
		try {
			stockdoc = CFile.opens("stockdepart@insuredocument");
		        recform = CFile.opens("receiptform@cbranch");
			returnStockDoc(parameter);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
	String branch ;
	private void returnStockDoc(String [] str) throws Exception
	{
		if(str[0].length() == 3)
			str[0]+=" ";
		branch = str[0];
		System.out.println("key-----------------"+str[9]+str[0]+str[1]+str[4]+str[2]);
		if(stockdoc.equalLess(str[9]+str[0]+str[1]+str[4]+str[2]) 
                && (str[9]+str[0].trim()).compareTo(stockdoc.get("statusStock")+stockdoc.get("deptCode")) == 0
                && (str[1]+str[4]).compareTo(stockdoc.get("docCode")+stockdoc.get("startNo")) == 0)
		{	
			stockdoc.set("currentNo",str[6]);
			stockdoc.set("currentDate",str[7]);
			stockdoc.set("receiveDate",str[3]);
			stockdoc.set("statusStock",str[8]);
			stockdoc.set("userID",str[10]);
			if(stockdoc.get("statusStock").charAt(0) == 'R')
			{
				deleteStockInPrinter(stockdoc.copy(),str[11]);
			}
			if(!stockdoc.update())
			{
					throw new Exception("Can not update to stockdepart@insuredocument error ="+stockdoc.lastError());
			}
		}
	}
	public void deleteStockInPrinter(Record rec,String printer) throws Exception
	{
               
                branch = rec.get("deptCode");
                if(branch.length() == 3)
                        branch+=" ";
                System.out.println("key == "+printer+branch+rec.get("docCode")+rec.get("startNo"));         
               for (boolean st = recform.equal(printer+branch+rec.get("docCode")+rec.get("startNo"));
                   st && printer.equals(recform.get("printer"))  
                   && rec.get("deptCode").compareTo(recform.get("deptCode")) == 0          
                   && rec.get("docCode").compareTo(recform.get("docCode")) == 0 
                   && rec.get("startNo").compareTo(recform.get("startNo")) == 0;
                   st = recform.next())
                {    
                        if (recform.get("startNo").compareTo(recform.get("startPrintNo")) == 0 && recform.get("lastNo").compareTo(recform.get("lastPrintNo")) == 0)
                        {
                		if(!recform.delete())
	                 	{
		                	throw new Exception("Can not delete receiptform@cbranch error=" +recform.lastError() );
        	        	}
                        }
                }

	}
}
