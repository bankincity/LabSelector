package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import utility.support.MyVector;
public class RteGetCurrentRpForm_New  implements Task
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
		try {
			if(flagNew.compareTo("old") == 0 || flagNew.compareTo("used") == 0 )
			{
				System.out.println(flagNew +".................."+docCode+" "+printer+" "+branch+" "+userID);
				for (boolean st = receiptform.great(printer+branch);st && (printer+branch).compareTo(receiptform.get("printer")+receiptform.get("deptCode")) == 0;st=receiptform.next())
				{
					if(branch.compareTo(receiptform.get("deptCode")) != 0)
						continue;
					if(receiptform.get("startPrintNo").compareTo(receiptform.get("lastNo")) > 0)
						continue;
					if(docCode.compareTo("00") == 0|| docCode.compareTo(receiptform.get("docCode")) == 0)
					{
						if("used".compareTo(flagNew) == 0 && (receiptform.get("startNo").compareTo(useStartRp) > 0 || receiptform.get("lastNo").compareTo(useEndRp) < 0))
							continue;
					
						rec = receiptform.copy();
						System.out.println("buffer..............."+(new String (rec.getBytes())));
						break;
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
					insrec.set("flagStock",checkUsedStock(rec,useStartRp,useEndRp));
					if(!usedreceiptform.insert(insrec))
						throw new Exception (M.stou("insert usereceiptform@cbranch  iserror  ")+M.itoc(usedreceiptform.lastError()));	
					System.out.println("insert ok");
					rec = null;
					for (boolean st = receiptform.great(printer+branch);st && (printer+branch).compareTo(receiptform.get("printer")+receiptform.get("deptCode")) == 0;st=receiptform.next())
					{
						if(branch.compareTo(receiptform.get("deptCode")) != 0)
							continue;
						if(receiptform.get("startPrintNo").compareTo(receiptform.get("lastNo")) > 0)
							continue;
						if(docCode.compareTo("00") == 0|| docCode.compareTo(receiptform.get("docCode")) == 0)
						{
							rec = receiptform.copy();
							break;
						}
					}
					if(rec != null)
						System.out.println("after insert used rec "+rec.get("startPrintNo"));
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
		return new Result(null,0);
		
	}
	private void deleteUsedStock(String printer,String branch,String docCode,String startPrintNo,String lastPrintNo) throws Exception
	{
		Record rec = null;
		System.out.println("printer......."+printer+"  "+docCode+"  "+branch);
		for (boolean st = receiptform.great(printer+branch);st && (printer+branch).compareTo(receiptform.get("printer")+receiptform.get("deptCode")) == 0;st=receiptform.next())
		{
			if(branch.compareTo(receiptform.get("deptCode")) != 0)
				continue;
			if(docCode.compareTo("00") == 0|| docCode.compareTo(receiptform.get("docCode")) == 0)
			{
			
				if(receiptform.get("startNo").compareTo(startPrintNo) <=  0 && receiptform.get("lastNo").compareTo(lastPrintNo) >= 0)
				{
					rec = receiptform.copy();
					break;
				}
			}
		}
		if(rec == null)
		{
			throw new Exception(M.stou("ไม่พบช่วงเลขที่เอกสารที่ต้องการลบในแฟ้ม สต๊อกเอกสาร"));
		}
		usedreceiptform.start(3);
		String blank ="";
		if(branch.length() == 3)
			blank = " ";
		System.out.println("ppppppppppppppppppppppppppppppppppppppppppppppp"+(new String (rec.getBytes())));
		if(usedreceiptform.equal(branch+blank+docCode+printer+startPrintNo))
		{
			System.out.println("ppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppppp");
			usedreceiptform.delete();
			if(rec.get("startPrintNo").compareTo(rec.get("lastNo")) > 0) // end of range
			{
				System.out.println("ddddddddddd--------->"+rec.get("startPrintNo")+"   "+rec.get("lastNo")+"    "+lastPrintNo+"    "+startPrintNo);
				rec.set("startPrintNo",startPrintNo);
				rec.set("lastPrintNo",lastPrintNo);
				terminateUsedRpForm(rec.get("printer"),rec.get("docCode"),rec.get("startNo"),"O","I");
				receiptform.update(rec);
			}
			else {
				TempFile temp = searchUsedForm(docCode,rec.get("startNo"));
				System.out.println("temp...............form search used................."+temp.fileSize());
				boolean st =temp.first();
				String ulast = "";
				for (;st;st=temp.equal(ulast))
				{
					ulast = M.setlen(M.inc(temp.get("end")),12);
				}
				System.out.println("temp...............ulast................."+ulast);
				if(ulast.trim().length() == 0)
					ulast = startPrintNo;
				rec.set("startPrintNo",ulast);
				receiptform.update(rec);
			}
		}
		usedreceiptform.start(0);
	}
	private String checkUsedStock(Record trec,String startNo,String lastNo) throws Exception
	{
		String stockStart = trec.get("startPrintNo");
		String stockEnd	= trec.get("lastPrintNo");
		if(lastNo.compareTo(stockEnd) > 0)
			throw new Exception(M.stou("ช่วงที่บันทึกไม่ถูกต้อง "));	
		
		System.out.println("stock ------"+stockStart+"  "+stockEnd);
		System.out.println("used ------"+startNo+"  "+lastNo);
		String ss = checkUsedAllStock(trec.get("docCode"),trec.get("startNo"),startNo,lastNo,receiptform.copy());
		System.out.println("ss==========================="+ss);
		if(ss.trim().length() == 0)
		{
			receiptform.set("startPrintNo",M.setlen(M.inc(receiptform.get("lastNo")),12));
			receiptform.update();		
			terminateUsedRpForm(trec.get("printer"),trec.get("docCode"),trec.get("startNo"),"I","O");
			return "O";
		}	
		if(startNo.compareTo(stockStart) == 0)
		{
			if(ss.compareTo(stockEnd) > 0)
			{
				receiptform.set("startPrintNo",M.setlen(M.inc(receiptform.get("lastNo")),12));
				receiptform.update();		
				terminateUsedRpForm(trec.get("printer"),trec.get("docCode"),trec.get("startNo"),"I","O");
				//receiptform.delete();		
				return "O" ;
			}
			else  if(ss.compareTo(stockEnd) <= 0)
			{
				receiptform.set("startPrintNo",ss);
				receiptform.update();	
				return "O";
			}
			else 
				throw new Exception(M.stou("ข้อมูลเลขเอกสารที่บันทึกไม่ถูกต้อง "));
		}
		return "O";	
	}
	private TempFile searchUsedForm(String docCode,String startNo)
	{
		TempFile temp = new TempFile(new String []{"start","end"},new int[] {12,12});
		temp.setNoOfKey(1);
		temp.setKey(0,new String [] {"start"});
		usedreceiptform.start(1);
		String key = "I"+docCode+startNo;
		for (boolean st = usedreceiptform.equalGreat(key);st;st=usedreceiptform.next())
		{
			if(key.compareTo(usedreceiptform.get("flagStock")+usedreceiptform.get("docCode")+usedreceiptform.get("startNo")) != 0) 
				break;
			temp.newRecord();
			temp.set("start",usedreceiptform.get("startPrintNo"));
			temp.set("end",usedreceiptform.get("lastPrintNo"));
			temp.add();
		}
		key = "O"+docCode+startNo;
		for (boolean st = usedreceiptform.equalGreat(key);st;st=usedreceiptform.next())
		{
			if(key.compareTo(usedreceiptform.get("flagStock")+usedreceiptform.get("docCode")+usedreceiptform.get("startNo")) != 0) 
				break;
			temp.newRecord();
			temp.set("start",usedreceiptform.get("startPrintNo"));
			temp.set("end",usedreceiptform.get("lastPrintNo"));
			temp.add();
		}
		return temp;
	}
	private String  checkUsedAllStock(String docCode,String startNo,String ustart,String ulast,Record trec) throws Exception
	{
		TempFile temp = searchUsedForm(docCode,startNo);
		if(temp.equal(ustart))
			throw new Exception(M.stou("ข้อมูลเลขเอกสารที่บันทึกไม่ถูกต้อง มีการบันทึก (")+temp.get("start")+" - "+temp.get("end")+")");
		if(temp.great(ustart))
		{
			if(ulast.compareTo(temp.get("start")) >= 0)
				throw new Exception(M.stou("ข้อมูลเลขเอกสารที่บันทึกไม่ถูกต้อง มีการบันทึก (")+temp.get("start")+" - "+temp.get("end")+")");
			if(temp.previous())
			{
				if(ustart.compareTo(temp.get("end")) <= 0)
					throw new Exception(M.stou("ข้อมูลเลขเอกสารที่บันทึกไม่ถูกต้อง มีการบันทึก (")+temp.get("start")+" - "+temp.get("end")+")");				
			}
				
		}
		else if(temp.last())
		{
			if(ustart.compareTo(temp.get("end")) <= 0)
				throw new Exception(M.stou("ข้อมูลเลขเอกสารที่บันทึกไม่ถูกต้อง มีการบันทึก (")+temp.get("start")+" - "+temp.get("end")+")");				
		}
		if(ustart.trim().length() ==  12)
		{
			temp.newRecord();
			temp.set("start",ustart);
			temp.set("end",ulast);
			temp.add();
		}
		boolean st = temp.first();
		ustart = temp.get("start");
		ulast = M.setlen(M.inc(temp.get("end")),12);
		System.out.println("stock ---pp---"+ulast+"  "+ustart);
		st = temp.next();
		System.out.println("bingo....................4...."+st);
		for (;st;st=temp.next())
		{
			System.out.println("in loop  ---pp---"+ulast+"  "+temp.get("start"));
			if(ulast.compareTo(temp.get("start")) == 0)
				ulast = M.setlen(M.inc(temp.get("end")),12);
			else if(ulast.compareTo(temp.get("start")) < 0)
				break ;			
			else
				throw new Exception(M.stou("ข้อมูลเลขเอกสารที่บันทึกไม่ถูกต้อง "));
		}
		if(ustart.compareTo(trec.get("startNo")) == 0 && ulast.compareTo(trec.get("lastNo")) > 0)
			return "";

		System.out.println("bingo.....find last..............."+ulast+"  "+temp.fileSize());
	/*t = temp.equal(ulast);
		for (;st;st=temp.equal(ulast))
		{
			ulast = M.setlen(M.inc(temp.get("end")),12);
		}*/
		temp.last();			
		ulast = M.setlen(M.inc(temp.get("end")),12);
		System.out.println("bingo.....find last..............."+ulast);
		return ulast ;
	}
	private void  terminateUsedRpForm(String printer,String docCode,String startStock,String flagSearch,String flagEnd)
	{	
		usedreceiptform.start(1);
		String key = flagSearch+docCode+startStock;
		for (boolean st = usedreceiptform.equalGreat(key);st;st=usedreceiptform.equalGreat(key))
		{
			if(key.compareTo(usedreceiptform.get("flagStock")+usedreceiptform.get("docCode")+usedreceiptform.get("startNo")) != 0) 
				break;
			usedreceiptform.set("flagStock",flagEnd);
			usedreceiptform.update();			
		}
	}
/*	private boolean  updateUsedStock(String docCode,String startNo,String ustart,String ulast,Record trec) throws Exception
	{
	
		TempFile temp = new TempFile(new String []{"start","end"},new int[] {12,12});
		temp.setNoOfKey(1);
		temp.setKey(0,new String [] {"start"});
		System.out.println("stock ---pp--1-"+ulast+"  "+ustart);
		if(ustart.trim().length() ==  12)
		{
			temp.set("start",ustart);
			temp.set("end",ulast);
			temp.add();
		}
		usedreceiptform.start(1);
		String key = "I"+docCode+startNo;
		String startkey = ustart;
		String endkey = ulast;
		System.out.println("bingo....................1");
		for (boolean st = usedreceiptform.great(key);st;st=usedreceiptform.next())
		{
			if(key.compareTo(usedreceiptform.get("flagStock")+usedreceiptform.get("docCode")+usedreceiptform.get("startNo")) != 0) 
				break;
			temp.set("start",usedreceiptform.get("startPrintNo"));
			temp.set("end",usedreceiptform.get("lastPrintNo"));
			temp.add();
		}
		System.out.println("bingo....................2");
		boolean st = temp.first();
		ustart = temp.get("start");
		System.out.println("bingo....................3");
		ulast = M.setlen(M.inc(temp.get("end")),12);
		System.out.println("stock -00--pp---"+ulast+"  "+ustart);
		st = temp.next();
		System.out.println("bingo....................4");
		for (;st;st=temp.next())
		{
			if(ulast.compareTo(temp.get("start")) == 0)
			{
			//	ustart = temp.get("start");
				ulast = M.setlen(M.inc(temp.get("end")),12);
			}
			else if(ulast.compareTo(temp.get("start")) < 0)
				return false ;			
			else
				throw new Exception(M.stou("ข้อมูลเลขเอกสารที่บันทึกไม่ถูกต้อง "));
					
		}
		System.out.println("stock -11--pp---"+ulast+"  "+ustart);
		ulast = M.setlen(M.dec(ulast),12);
		System.out.println("stock -22--pp---"+ulast+"  "+ustart);
		System.out.println("trec ...start ....end =="+trec.get("startPrintNo")+"  "+trec.get("lastPrintNo"));
		if(ustart.compareTo(trec.get("startPrintNo")) == 0 )
		{
		  	if(ulast.compareTo(trec.get("lastPrintNo")) == 0)
			{
				receiptform.set("startPrintNo",M.setlen(M.inc(receiptform.get("lastNo")),12));
				receiptform.update();		
				for (st = usedreceiptform.great(key);st;)
				{
					if(key.compareTo(usedreceiptform.get("flagStock")+usedreceiptform.get("docCode")+usedreceiptform.get("startNo")) != 0) 
						break;
					usedreceiptform.set("flagStock","O");
					usedreceiptform.update();
					st= usedreceiptform.great(key);
				}	
				return true ;
			}
			else if(ulast.compareTo(trec.get("lastPrintNo")) < 0)
			{
				receiptform.set("startPrintNo",M.setlen(M.inc(ulast),12));
				receiptform.update();		
				for (st = usedreceiptform.great(key);st;)
				{
					if(key.compareTo(usedreceiptform.get("flagStock")+usedreceiptform.get("docCode")+usedreceiptform.get("startNo")) != 0) 
						break;
					if(ulast.compareTo(usedreceiptform.get("lastPrintNo")) >= 0)
					{
						usedreceiptform.set("flagStock","O");
						usedreceiptform.update();
					}
					st= usedreceiptform.great(key);
				}	
				return true ;
			}
		}
		return false ;	
	}*/
}
