package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import utility.cfile.*;
import utility.prename.*;
import java.util.*;
import utility.support.DateInfo;
import utility.rteutility.*;
import rte.bl.branch.TempMasicFile;
import insure.*;
public class RteSumaryTakaful  implements  Task
{
 /**===============================================================<br>
    <pre Vspace=100>
		execute String[] {String  branch,String startRp,String endRp,String msg}
	    	Return :
				 0 = success
				-1 = errorMessage 							 </pre>

	===============================================================*/
	TempMasicFile temp;
	public Result execute(Object o)
	{
		 if (!(o instanceof Object []) )
			return new Result("Invalid Parameter : String array { branch,startRp,endRp,msg}",-1);
		Object []  parameter = (Object[])o;
		String startDate = (String)parameter[0];
		String endDate = (String)parameter[1];
		String [] tf = {"policyNo","payPeriod1","payPeriod2","spremium","plan","mode","tot"};
		int [] tl = {8,4,4,9,4,1,3};
		try {
			temp = new TempMasicFile("rptbranch",tf,tl);
			temp.keyField(false ,false,new String [] {"policyNo"});
			temp.buildTemp();
		
			Mrecord orc = CFile.opens("orctrl@receipt");
			Mrecord mast = CFile.opens("ordmast@mstpolicy");
			getDataFromRc(orc,mast,startDate,endDate);

			System.out.println("current file complete");
			orc.close();
			if(startDate.substring(0,4).compareTo(endDate.substring(0,4)) < 0)
			{
				String syear = startDate.substring(0,4);
				String eyear = endDate.substring(0,4);
				while (syear.compareTo(eyear) <= 0)
				{
					System.out.println("current file year -"+syear);
					if(Masic.fileStatus("orctrl"+syear+"@receipt") >= 2)
					{
						orc = CFile.opens("orctrl"+syear+"@receipt");
						getDataFromRc(orc,mast,startDate,endDate);	
						orc.close();
					}
					syear = M.inc(syear);
				}	
			}
			else {
				String syear = startDate.substring(0,4);
				if(Masic.fileStatus("orctrl"+syear+"@receipt") >= 2)
				{
					orc = CFile.opens("orctrl"+syear+"@receipt");
					getDataFromRc(orc,mast,startDate,endDate);	
				}
				orc.close();
			}	
			return new Result(temp.name(),0);			
		}
		catch(Exception e)
		{
			return new Result(e.getMessage(),1);
		}
	}
	private void getDataFromRc(Mrecord orc,Mrecord mast,String sdate,String edate)
	{
	
			for (boolean st = orc.great("90") ; st && (orc.get("rpNo").substring(0,2)).compareTo("90") == 0; st=orc.next())
			{
				System.out.println("rp --------------"+orc.get("rpNo"));
				if("BPE".indexOf(orc.get("currentStatus")) >= 0 && sdate.compareTo(orc.get("payDate")) <= 0 && edate.compareTo(orc.get("payDate")) >= 0)
				{
					if(!temp.equal(orc.get("policyNo")))
					{
						temp.set("policyNo",orc.get("policyNo"));
						temp.set("payPeriod1",orc.get("payPeriod"));
						temp.set("payPeriod2",orc.get("payPeriod"));
						temp.set("spremium",orc.get("premium"));
						temp.set("mode",orc.get("mode"));
						if(mast.equal(orc.get("policyNo")))
						{
							if(!PlanType.isIslamPlan(mast.get("planCode")))
								continue;
							temp.set("plan",mast.get("planCode"));
							
						}
						temp.set("tot","001");		
						temp.insert();
					}
					else {
						if (temp.get("payPeriod1").compareTo(orc.get("payPeriod")) > 0)
							temp.set("payPeriod1",orc.get("payPeriod"));
						else if (temp.get("payPeriod2").compareTo(orc.get("payPeriod")) < 0)
							temp.set("payPeriod2",orc.get("payPeriod"));
						temp.set("spremium",M.addnum(temp.get("spremium"),orc.get("premium")));						  	   temp.set("tot",M.setlen(M.inc(temp.get("tot")),3));
						temp.update();	
					}
				}	
			}
	}
}
