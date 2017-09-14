package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import java.util.*;
import rte.bl.branch.*;
import utility.rteutility.*;
public class RteCountPrintReceipt implements Task
{
	public Result execute(Object param)
	{
		if(! (param instanceof String  []))
 			return new Result("Invalid Parameter  : Object [] {branch,printDate,String typeOfPol}",-1);
 		String [] parameter = (String []) param;
  		String branch = (String)parameter[0];
		String printDate = (String)parameter[1];
		String [] typeOfPol ;
		if(parameter.length == 3)
		{
			if(parameter[2].charAt(0) == 'O')
				typeOfPol = new String [] {"O","W"};
			else if(parameter[2].charAt(0) == 'I')
				typeOfPol = new String [] {"I"};
			else if(parameter[2].charAt(0) == 'B')
				typeOfPol = new String [] {"I","O","W","U","L"};
			else
				typeOfPol = new String [] {" "};
		}
		else {
			typeOfPol = new String []{"I","O","W","U"};
		}
		String [] countrp = {"0","0","0","0","0"};
		Result res ;
                PublicRte.setRemote(false);
		for (int i = 0 ; i < typeOfPol.length ; i++)
		{
			res= PublicRte.getResult("searchreceipt","rte.search.receipt.SearchReceiptByPrintDate", new String [] {typeOfPol[i],branch,printDate,""});
			System.out.println("result of SearchReceiptByPrintDate -------->"+res.status());
			if(res.status() == 0)
				countrp[i]  = (String)res.value();
			else
				return res;
		}
		return new Result(countrp,0);
 	}
}
