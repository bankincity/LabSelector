package rte.bl.service.nstatus;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import java.util.*;
import rte.*;
import utility.rteutility.*;
import utility.prename.Prename;
import utility.address.*;
import insure.*;
import utility.support.DateInfo;
import utility.prename.Prename;
import utility.cfile.Rtemp;
public class  RteRemoveNstatus  implements  Task
{
	Mrecord bankruptcy;
	public Result execute(Object param) 
        {
		// sel ,keySearch1,keySearch2
		Object [] parameter = (Object [])param;
			
		String id  = (String)parameter[0];
		String fname  = (String)parameter[1];
		String lname = (String)parameter[2];
            //    Msg.msg(new Mframe(""),"parameter...."+parameter.length);
		Vector ret = new Vector();
		try {
			bankruptcy= CFile.opens("bankruptcy@mstperson");
			if (parameter.length > 3 )
			{
				String decidedNo = (String)parameter[3];
				String inforceDate ="00000000";
				String remark = "";
				if (parameter.length == 6)
				{
					inforceDate = (String)parameter[4];
					remark = (String)parameter[5];
				}
				else
					 remark  = (String)parameter[4];

				try {
					System.out.println("to cancel...........");
					cancelBankruptcy(id,fname,lname,decidedNo,remark);
				
				}
				catch (Exception e)
				{
					System.out.println("Error...........");
					return new Result(e.getMessage(),1);
				}
				return new Result("",0);
			}	
			if (id.trim().length() > 0)
			{
				bankruptcy.start(2);
				for (boolean st = bankruptcy.equal(id) ; st && id.compareTo(bankruptcy.get("idNo")) == 0 ;st=bankruptcy.next())
				{
					if(bankruptcy.get("courtName").charAt(0) == 'C')
						continue;	
					String [] data  = new 	String [6];
					data [0] = bankruptcy.get("idNo");
					data [1] = bankruptcy.get("preName");
					data [2] = bankruptcy.get("firstName");
					data [3] = bankruptcy.get("lastName");
					data [4] = bankruptcy.get("decidedNo");
					data [5] = bankruptcy.get("inforceDate");
					ret.addElement(data);
				}			
			} 
			else if (lname.trim().length() > 0)
			{
				bankruptcy.start(1);	
				for (boolean st = bankruptcy.equalGreat(lname) ; st && matchData(lname,bankruptcy.get("lastName")) ;st=bankruptcy.next())
				{
					if(fname.trim().length() == 0 || matchData(fname,bankruptcy.get("firstName")))
					{
						if(bankruptcy.get("courtName").charAt(0) == 'C')
							continue;	
						String [] data  = new 	String [6];
						data [0] = bankruptcy.get("idNo");
						data [1] = bankruptcy.get("preName");
						data [2] = bankruptcy.get("firstName");
						data [3] = bankruptcy.get("lastName");
						data [4] = bankruptcy.get("decidedNo");
						data [5] = bankruptcy.get("inforceDate");
						ret.addElement(data);
					}
				}				
			}
			else if (fname.trim().length() > 0)
			{
				bankruptcy.start(0);	
				for (boolean st = bankruptcy.equalGreat(fname) ; st && matchData(fname,bankruptcy.get("firstName"));st=bankruptcy.next())
				{
					if(bankruptcy.get("courtName").charAt(0) == 'C')
						continue;	
					String [] data  = new 	String [6];
					data [0] = bankruptcy.get("idNo");
					data [1] = bankruptcy.get("preName");
					data [2] = bankruptcy.get("firstName");
					data [3] = bankruptcy.get("lastName");
					data [4] = bankruptcy.get("decidedNo");
					data [5] = bankruptcy.get("inforceDate");
					ret.addElement(data);
				}				
			}
			return new Result(ret,0);
			
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
	}
	private void  cancelBankruptcy(String id,String fname,String lname,String decideNo,String remark) throws Exception
	{
             //   Msg.msg(new Mframe(""),id+"---"+fname+"---"+lname);

		System.out.println(id+"  "+fname+"  "+lname);
		bankruptcy.start(1);	
		for (boolean st = bankruptcy.equalGreat(lname) ; st && matchData(lname,bankruptcy.get("lastName")) ;st=bankruptcy.next())
		{
			if( matchData(fname,bankruptcy.get("firstName")) && (id.trim()).compareTo(bankruptcy.get("idNo").trim()) == 0 )
			{
				if ((decideNo.trim()).compareTo(bankruptcy.get("decidedNo").trim()) == 0)
				{
					bankruptcy.set("courtName","C"+remark);
					bankruptcy.update();
				}
			}
		}
                Mrecord insp = CFile.opens("insolventpeople@srvservice");
                Mrecord remarkrec = CFile.opens("insolventremark@srvservice");
                insp.start(3);
		for (boolean st = insp.equalGreat(lname) ; st && matchData(lname,insp.get("lastName")) ;st=insp.next())
		{
			if( matchData(fname,insp.get("firstName")))
			{
                                id = M.setlen(id,13);      
                                String tid = M.setlen(insp.get("idNo"),13);
                                if (id.compareTo(tid) != 0)
                                        continue;
				insp.set("status","C");
				insp.update();
                                String sysDate = M.sysdate();
                                String sysTime = M.systime();
                                remarkrec.set("policyNo","00000000");
                                remarkrec.set("caseID",insp.get("caseID"));
                                remarkrec.set("tranDate",sysDate);
                                remarkrec.set("tranTime",sysTime);
                                if (insp.get("idNo").trim().length() == 0 || M.itis(insp.get("idNo"),'0'))
                                {
                                        remarkrec.set("remark",insp.get("firstName")+" "+insp.get("lastName")+":"+remark);
                                }
                                else
                                        remarkrec.set("remark",insp.get("idNo")+":"+remark);
                                if(!remarkrec.insert())
                                {
                                    remarkrec.set("tranTime",M.inc(sysTime));
                                    remarkrec.insert();
                                }
			}
		}
                
			
	}
	private boolean matchData(String fname ,String ffname)
	{
		int len = fname.trim().length();
		return M.match(len,fname,0,ffname,0);
	}
}


