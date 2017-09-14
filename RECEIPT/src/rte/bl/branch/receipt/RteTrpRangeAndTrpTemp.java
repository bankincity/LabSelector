package rte.bl.branch.receipt;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.MyVector;
import utility.support.DateInfo;
public class RteTrpRangeAndTrpTemp  implements Task
{
	Mrecord trptmp;
	Mrecord trprange;
	String sysdate;
	String branch;
	MyVector vec ;
	public Result execute(Object param)
	{
		if(! (param instanceof Object []))
			return new Result("Invalid Parameter  : Object [] {branch,vector}",-1);
		Object [] parameter = (Object []) param;
		if(parameter.length == 3)
		{		
			branch = (String)parameter[0];
			vec = (MyVector)parameter[1];
			String takaful = (String)parameter[2];
			return (trpToCenter(takaful.charAt(0) == 'K'));
		}
		else if(parameter.length  == 6 || parameter.length == 5)
		{
			branch = (String)parameter[0];
			String use = (String)parameter[1];
			String range = (String)parameter[2];
			String startRp = (String)parameter[3];
			String endRp = (String)parameter[4];
			String fileName = "trprange@receipt";
			if(parameter.length == 6)
				fileName = (String)parameter[5];
			try {
				trprange  = CFile.opens(fileName);
				if(use.charAt(0) == 'D')
					terminateRange(range);
				else
					updateOrInsert(range,startRp,endRp);
			}
			catch (Exception e)
			{
				return new Result(e.getMessage(),1);
			}
		}
		return new Result("",0);
	}
	private String getRange()
        {
                trprange.start(1);
                String trange = DateInfo.sysDate().substring(2,4);
                String range = trange+"000";
                for (boolean st = trprange.equalGreat(branch+range);
                        st && trange.compareTo(trprange.get("range").substring(0,2)) == 0 && 
			branch.compareTo(trprange.get("branch")) == 0;
                        st= trprange.next())
                {
                        if(range.compareTo(trprange.get("range")) < 0 )
                           range = trprange.get("range");
                }
                trprange.start(0);
                return M.inc(range);
        }
	private void terminateRange(String range) throws Exception
	{
                if(trprange.equal(branch+"U"+range))
		{
			trprange.lock();
			trprange.set("used","C");
			trprange.update();
			trprange.release();
		}
	}
	private void  updateOrInsert(String range,String start,String end) throws Exception
	{
		System.out.println("ranmge ========"+range);
                if(trprange.equal(branch+"U"+range))
                {
			trprange.lock();
                        trprange.set("startTrp",start);
                        trprange.set("endTrp",end);
                        trprange.set("currentTrp",start);
                        trprange.update();
                	trprange.release();
                }
                else {
                        trprange.set("range",getRange());
			trprange.set("branch",branch);
                        trprange.set("used","U");
                        trprange.set("startTrp",start);
                        trprange.set("endTrp",end);
                        trprange.set("currentTrp",start);
			trprange.lock();
                        trprange.insert();
                	trprange.release();
                }
		
	}

	private Result trpToCenter(boolean flagTakaful)
	{
		try {
			if(!flagTakaful)
				trptmp = CFile.opens("trptmp@cbranch");
			else
				trptmp = CFile.opens("trptmptakaful@cbranch");
			String trp= "";
			String msg="";
			for (int i =  0 ; i < vec.size();i++)
			{
				trp = (String)vec.elementAt(i);
				if(trptmp.equal(branch+trp))
				{
					trptmp.set("ownerSaleID","0000000000");
					trptmp.update();
				}
                                else if (branch.compareTo("007") == 0)
                                {
                                        if (trptmp.equal("A07"+trp))
                                        {
					        trptmp.set("ownerSaleID","0000000000");
					        trptmp.update();
                                        }
                                        else
					        msg= msg+trp+",";
                                        
                                }
				else 
					msg= msg+trp+",";
			}
			if(msg.trim().length() == 0)
				return (new Result("",0));
			return (new Result(msg,2));
		}
		catch (Exception e)
		{
			return (new Result(e.getMessage(),1));
		}
	}	
}
