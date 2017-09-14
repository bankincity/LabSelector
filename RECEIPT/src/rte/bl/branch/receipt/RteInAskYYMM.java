package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;

public class RteInAskYYMM  implements Task
{
	public Result execute(Object param)
	{
		if(! (param instanceof String []))
			return new Result("Invalid Parameter  : String [] {yyyymm,depno,reqdepno,keyIndex,samedep,branch}",-1);
		String [] parameter = (String []) param;
		if(parameter.length != 6 )
			return new Result("Invalid Parameter  : String [] {yyyymm,depno,reqdepno,keyIndex,samedep,branch}",-1);

		String yyyymm = (String)parameter[0];
		String depno = (String)parameter[1];
		String reqdep = (String)parameter[2];
		int keyIndex = M.ctoi((String)parameter[3]);
		String same  = (String)parameter[4];
		String branch  = (String)parameter[5];
		
		boolean samedep = same.charAt(0) == '1';
		try {
			Mrecord  ask = CFile.opens ("ask"+yyyymm+"@cbranch");
			int typeOfRp = CheckAskReceipt.inAskYYMM(ask,depno,reqdep,keyIndex,samedep,branch);
			return new Result(M.itoc(typeOfRp),0);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
	
	}
/*	public int  static  inAskYYMM(Mrecord  task , String depno,String reqdep , int keyIdx,boolean sameDep)
        {
		task.start(keyIdx);
                boolean   otherRequestFor = depno.compareTo(reqdep) != 0 ;
		if (task.equal(depno))
		{	
			if (sameDep){
				int typeOfRp = 0 ;
				while (task.get("ownerDepNo").compareTo(depno) == 0){
					if (task.get("askDepNo").compareTo(reqdep) != 0){
						if (otherRequestFor == false)
							otherRequestFor = true ;
						if (!task.next())
							break;
						else
							continue ;
					}
					if (task.get("receiptFlag").charAt(0) == 'T' || task.get("receiptFlag").charAt(0) == 'Z'){
						if (typeOfRp != 1)
							typeOfRp +=1;
					}
					else {
						if (typeOfRp != 2)
							typeOfRp +=2;
					}
					if (typeOfRp == 3)
                                            break ;
					if (!task.next())
						break;
				} // while
				return typeOfRp ;
                           } // same depno
                           else
				return 1;
		}// equal
		else
			return 0 ;
	}*/

}
