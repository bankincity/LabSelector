package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;

public class RteUpdateStockDoc  implements Task
{
	Mrecord stockdoc ;
	public Result execute(Object param)
	{
		if(! (param instanceof String []))
			return new Result("Invalid Parameter  : String [] {deptCode,docCode,sendDate,receiveDate,startNo,lastNo,currentNo,currentDate,newStokStatus ,oldStockStatus,userID,printer }",-1);
		String [] parameter = (String []) param;
		try {
			stockdoc = CFile.opens("stockdepart@insuredocument");
			insertUpdateStockDoc(parameter);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
	String reqStock;
	String reqStart  = "A";
	String branch ;
	private void insertUpdateStockDoc(String [] str) throws Exception
	{
		if(str[0].length() == 3)
			str[0]+=" ";
		branch = str[0];
		System.out.println("key-----------------"+str[9]+str[0]+str[1]+str[4]+str[2]);
		if(stockdoc.equal(str[9]+str[0]+str[1]+str[4]+str[2]))
		{	
			reqStock = M.subnum(stockdoc.get("currentNo"),str[6]);	
			String typeDoc = stockdoc.get("docCode");
		//	if("07".compareTo(stockdoc.get("docCode")) == 0 || "08".compareTo(stockdoc.get("docCode")) == 0 )
			if (typeDoc.compareTo("07") == 0 || typeDoc.compareTo("08") == 0 ||typeDoc.compareTo("04") == 0 || typeDoc.compareTo("05") == 0)
				reqStart  = stockdoc.get("currentNo");
			stockdoc.set("currentNo",str[6]);
			stockdoc.set("currentDate",str[7]);
			stockdoc.set("receiveDate",str[3]);
			stockdoc.set("statusStock",str[8]);
			stockdoc.set("userID",str[10]);
			if(stockdoc.get("statusStock").charAt(0) == 'C')
			{
				insertStockInPrinter(stockdoc.copy(),str[11]);
			}
			if(str[4].compareTo("000000000000") == 0)
			{
				if(M.cmps(stockdoc.get("currentNo"),"00") >0)
					stockdoc.set("statusStock","R");
					
			}
			else if (typeDoc.compareTo("07") == 0 || typeDoc.compareTo("08") == 0 ||typeDoc.compareTo("04") == 0 || typeDoc.compareTo("05") == 0)
			{
		//	else if("07".compareTo(stockdoc.get("docCode")) == 0 || "08".compareTo(stockdoc.get("docCode")) == 0 ){
				if(stockdoc.get("currentNo").compareTo(stockdoc.get("lastNo")) < 0)
					stockdoc.set("statusStock","R");
			}		
			if(!stockdoc.update())
			{
					throw new Exception("Can not update to stockdepart@insuredocument error ="+stockdoc.lastError());
			}
		}
		else {
			stockdoc.set("deptCode",str[0]);
			stockdoc.set("docCode",str[1]);
			stockdoc.set("sendDate",str[2]);
			stockdoc.set("receiveDate",str[3]);
			stockdoc.set("startNo",str[4]);
			stockdoc.set("lastNo",str[5]);
			stockdoc.set("currentNo",str[6]);
			stockdoc.set("currentDate",str[7]);
			stockdoc.set("statusStock",str[8]);
			stockdoc.set("userID",str[10]);
			if(!stockdoc.insert())
			{
				throw new Exception("Can not insert to stockdepart@insuredocument error ="+stockdoc.lastError());
			}
		}
	}
	public void insertStockInPrinter(Record rec,String printer) throws Exception
	{
		Mrecord recform = CFile.opens("receiptform@cbranch");
		if(rec.get("startNo").compareTo("000000000000") != 0 && (M.ctoi(rec.get("docCode")) <= 3 || M.ctoi(rec.get("docCode")) == 17 || M.ctoi(rec.get("docCode")) == 22))
		{
			System.out.println("before  of for......................");
			for (boolean st = recform.equalGreat(printer+rec.get("deptCode"));st && (printer+rec.get("deptCode")).compareTo(recform.get("printer")+recform.get("deptCode")) == 0;st=recform.next())
			{
				if(rec.get("deptCode").compareTo(recform.get("deptCode")) == 0 && printer.compareTo(recform.get("printer")) == 0 && rec.get("docCode").compareTo(recform.get("docCode")) == 0 )
					if(recform.get("startPrintNo").compareTo(recform.get("lastNo")) <=  0)
						
						throw new Exception(M.stou("ยังมีเอกสารอยู่ที่ เครื่อง Printer ")+printer+M.stou(" เบิกเอกสารใหม่ไม่ได้"));
			}
			System.out.println("end of for......................");
		}
		recform.set("printer",printer);
		recform.set("printDate",DateInfo.sysDate());
		recform.set("deptCode",rec.get("deptCode"));
		recform.set("docCode",rec.get("docCode"));
		recform.set("startNo",rec.get("startNo"));
		recform.set("lastNo",rec.get("lastNo"));
		String typeDoc = rec.get("docCode");
		if(reqStart.charAt(0) != 'A')
			recform.set("startPrintNo",reqStart);
		else 
			recform.set("startPrintNo",rec.get("startNo"));
		if(rec.get("startNo").compareTo("000000000000") == 0)
			recform.set("lastPrintNo",M.setlen(reqStock,12));
//		else if("07".compareTo(rec.get("docCode")) == 0 || "08".compareTo(rec.get("docCode")) == 0 )
		else if (typeDoc.compareTo("07") == 0 || typeDoc.compareTo("08") == 0 ||typeDoc.compareTo("04") == 0 || typeDoc.compareTo("05") == 0)
		{
			recform.set("lastPrintNo",M.setlen(M.dec(rec.get("currentNo")),12));
		}
		else
			recform.set("lastPrintNo",rec.get("lastNo"));
		if(!recform.insert())
		{
			throw new Exception("Can not Insert receiptform@cbranch error=" +recform.lastError() );
		}
	}
}
