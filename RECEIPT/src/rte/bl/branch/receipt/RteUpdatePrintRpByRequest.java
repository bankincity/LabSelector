package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteUpdatePrintRpByRequest implements Task
{
	 /**==============================================================<br>
                <pre Vspace=100>
                execute String[] {String type, Vector  policyNo,String branch}
                        String  type     := type of Policy
                       	Vector  policyNo := Vector (byte array of rpserved) 
                        String branch := สาขาที่ พิมพ์ใบเสร็จ
                        Return :
                           

                        Status :
                                 0 = success
                                -1 = errorMessage or system error</pre>

        =================================================================*/
	public Result execute(Object o)
	{
		if(!(o instanceof Object []))
                        return new Result(M.stou("Parameter must be Array  (typeOfPolicy, Vector of PolicyNo,reasonCode,userID )"),-1);
		Object[] parameter = (Object [])o;
		System.out.println("Parameter..................in RteUpdateReceiptByRequest................"+parameter.length);
		try {
			String typeOfPol =  (String)parameter[0];
			System.out.println("type in update........................>"+typeOfPol);
			Vector vpolicy = (Vector)parameter[1];
			String branch = (String)parameter[2];
			String userID = (String)parameter[3];
			String flagPrint = "T";
			if(parameter.length == 5)
				flagPrint = (String)parameter[4];
			Receipt rec = new Receipt(typeOfPol,DateInfo.sysDate().substring(2,4));
			return new Result(rec.alreadyPrintRequestRpToRpForm(vpolicy,typeOfPol,branch,userID,flagPrint),0);
		}
		catch (Exception e)
		{
			System.out.println("bingo...................................error.............RteUpdateReceiptByRequest");
			e.printStackTrace();
			return new Result(e.getMessage(),-1);
		}
	}
}
