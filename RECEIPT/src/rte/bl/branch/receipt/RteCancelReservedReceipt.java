package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
public class RteCancelReservedReceipt implements Task
{
	 /**==============================================================<br>
                <pre Vspace=100>
                execute String[] {String type, Vector  policyNo,String resonCode,String userID}
                        String  type     := type of Policy
                       	Vector  policyNo := policy number
                        String resonCode := reason to cancel receipt
                        Return :
                           

                        Status :
                                 0 = success
                                -1 = errorMessage or system error</pre>

        =================================================================*/
	public Result execute(Object o)
	{
		if(!(o instanceof Object []))
                        return new Result(M.stou("Parameter must be Array  (typeOfPolicy, Vector of PolicyNo,reasonCode )"),-1);
		Object[] parameter = (Object [])o;
		try {
			String typeOfPol =  (String)parameter[0];
			Vector vpolicy = (Vector)parameter[1];
			String reasonCode = (String)parameter[2];
			Receipt.checkDataInRctrl(vpolicy,typeOfPol,reasonCode,(String)parameter[3]);
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),-1);
		}
		return new Result("",0);
	}
}
