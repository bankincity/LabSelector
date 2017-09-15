package rte.bl.branch.receipt;
import java.util.ArrayList;

import grouplife.nbgroup.data.Condition;
import layout.bean.cunderwrite.TrprangeBean;
import layout.bean.receipt.TrprangeBeanRecord;
import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import rte.bl.dataaccess.DAOInterface;
import rte.bl.dataaccess.PrimaryKey;
import rte.bl.dataaccess.search.ByKey;
import rte.bl.dataaccess.search.ConditionEqual;
import rte.bl.dataaccess.search.Where;
import rte.bl.dataaccess.selector.SelectorOneTable;
import utility.cfile.*;
import utility.support.MyVector;
import utility.support.DateInfo;
public class RteTrpRangeAndTrpTemp  implements TaskUsr
{
	//Mrecord trptmp;
	//Mrecord trprange;
	String sysdate;
	String branch;
	DAOInterface trptemp ;
	DAOInterface trprange;
	
	MyVector vec ;
	public Result execute(UserInfo user, Object param)
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
				trprange = new rte.bl.dataaccess.receipt.TrprangeDAO(user);
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
	private String getRange() throws Exception
    {
                /*trprange.//trprange.start(1);
				
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
                return M.inc(range);*/
			//Condition[] cond_table = new Condition[] {new ConditionEqual(new ByKey("range", branch))};
			String trange = DateInfo.sysDate().substring(2,4);
			String range = trange+"000";
			ByKey k1 = new ByKey("branch", branch);
			k1.setKeyNumber(1);
			//ByKey k2 = new ByKey("branch", branch);
			//k2.setKeyNumber(1);
			Where where = new Where(k1);
			SelectorOneTable s = new SelectorOneTable("trprange","receipt",where);
			XTempList out = s.select();
			ArrayList<String> field = out.getDefaultFields();
			for (boolean st = out.first();st;st=out.next())
			{
				if(trange.compareTo(out.get("range").substring(0,2)) == 0)
				{
					if(range.compareTo(out.get("range")) < 0 )
						range = out.get("range");
				}
			}	

			return M.inc(range);
        }
	private void terminateRange(String range) throws Exception
	{
		//layout.bean.receipt.TrprangeBean d = layout.bean.receipt.TrprangeBean.newInstance();
		
		//d.get
		PrimaryKey pk = new PrimaryKey();
		pk.addKey( "branch", branch);
		pk.addKey( "used", "U");
		pk.addKey( "range", range);
		layout.bean.receipt.TrprangeBean oldBean = (layout.bean.receipt.TrprangeBean)trprange.equal(pk);
		
		Record r = TrprangeBeanRecord.getRecord(oldBean);
		r.set("used", "C");
		layout.bean.receipt.TrprangeBean newBean = TrprangeBeanRecord.getTrprangeBean(r);
		trprange.update(oldBean, newBean, pk);
		
		
        /*if(trprange.equal(branch+"U"+range))
		{
			trprange.lock();
			trprange.set("used","C");
			trprange.update();
			trprange.release();
		}*/
	}
	private void  updateOrInsert(String range,String start,String end) throws Exception
	{
		System.out.println("ranmge ========"+range);
		PrimaryKey pk = new PrimaryKey();
		pk.addKey( "branch", branch);
		pk.addKey( "used", "U");
		pk.addKey( "range", range);
		layout.bean.receipt.TrprangeBean oldBean = (layout.bean.receipt.TrprangeBean)trprange.equal(pk);
		if(oldBean != null)
		{
			Record r = TrprangeBeanRecord.getRecord(oldBean);
			r.set("startTrp",start);
			r.set("endTrp",end);
			r.set("currentTrp",start);
			layout.bean.receipt.TrprangeBean newBean = TrprangeBeanRecord.getTrprangeBean(r);
			trprange.update(oldBean, newBean, pk);
		}
		else
		{
			layout.bean.receipt.TrprangeBean newBean = layout.bean.receipt.TrprangeBean.newInstance();
			newBean.setRange(getRange());
			newBean.setBranch(branch);
			newBean.setUsed("U");
			newBean.setStartTrp(start);
			newBean.setEndTrp(end);
			newBean.setCurrentTrp(start);
			trprange.insert(newBean);
		}
                /*if(trprange.equal(branch+"U"+range))
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
                }*/
		
		
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
