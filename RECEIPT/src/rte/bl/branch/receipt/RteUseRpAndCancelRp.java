package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import java.util.*;
import rte.bl.branch.*;
import utility.rteutility.*;
import utility.cfile.CFile;
public class RteUseRpAndCancelRp  implements Task
{
	public Result execute(Object param)
	{
		if(! (param instanceof String  []))
 			return new Result("Invalid Parameter  : Object [] {branch,printDate,String typeOfPol}",-1);
 		String [] parameter = (String []) param;
  		String branch = (String)parameter[0];
		String printDate = (String)parameter[1];
		String typeOfForm  = "01";
		Vector vform = new Vector();
		if(parameter.length == 3)
		{
			if(parameter[2].charAt(0) == 'O')
				typeOfForm = "02";
			else if(parameter[2].charAt(0) == 'I')
				typeOfForm = "01";
			else
				typeOfForm = "17";
			
		}
		try {
			String [] tf ;
			if(typeOfForm.compareTo("02") == 0)
			{
				tf = new String [2];
				tf[0] = "02";
				tf[1] = "06";
			}
			else if(typeOfForm.compareTo("17") == 0)
			{
				tf = new String [4];
				tf[0] = "17";
				tf[1] = "18";
				tf[2] = "22";
				tf[3] = "03";
			}
			else {
				tf = new String [1];
				tf[0] = "01";
			}
			System.out.println("typeOfForm-------------------------------------->"+typeOfForm);
			Mrecord crec = CFile.opens("cancelform@insuredocument");
			Mrecord urec = CFile.opens("usedreceiptform@cbranch");
			String key = branch;
			String blank ="";
			for (int i = 0 ; i < tf.length;i++)
			{
				
				if(key.length() == 3)
					blank = " ";
				key=branch+blank+tf[i]+printDate;
				System.out.println("key ===="+key);
				for (boolean st = crec.equalGreat(key);st;st=crec.next())
				{
					if(key.compareTo(crec.get("deptCode")+blank+crec.get("docCode")+crec.get("cancelDate")) != 0)
						break;
					System.out.println(crec.get("status")+" "+crec.get("startNo"));
					vform.addElement(new String [] {crec.get("status"),crec.get("startNo"),crec.get("lastNo")});
				}
			}
			for (int j = 0 ; j < tf.length;j++)
			{
				String [] printer = {"1","2","3","4","5","6"};
				for (int i = 0 ; i < printer.length;i++)
				{
					key = printer[i]+printDate+branch+blank+tf[j];
					for (boolean st = urec.equalGreat(key);st;st=urec.next())
					{
						if(key.compareTo(urec.get("printer")+urec.get("printDate")+urec.get("deptCode")+blank+urec.get("docCode")) != 0)
							break;
						vform.addElement(new String [] {urec.get("printer"),urec.get("startPrintNo"),urec.get("lastPrintNo"),urec.get("userID")});
					}
				}
			}	
			return (new Result(vform,0));
		}
		catch (Exception e)
		{
			if (e == null || e.getMessage() == null)
				return new Result(M.stou("มีข้อมูลบางอย่างเป็น null"),1);
			return new Result(e.getMessage(),1);
		}
 	}
}
