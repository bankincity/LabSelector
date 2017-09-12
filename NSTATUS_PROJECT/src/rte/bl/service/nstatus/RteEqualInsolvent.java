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
//import insure.*;
import utility.support.DateInfo;
import utility.prename.Prename;
import utility.cfile.Rtemp;
public class  RteEqualInsolvent implements  Task
{
	Mrecord ul;
	Mrecord ord;
        Mrecord whl;
	Mrecord ind;
	Mrecord name;
	Mrecord person;
	Mrecord insolvent;
	Mrecord insolventtran;
	Mrecord insolventrem;
	String sysDate="";
	Vector vit ;
	Vector vrem;
	public Result execute(Object param) 
        {
		// sel ,keySearch1,keySearch2
		Object [] parameter = (Object [])param;
			
		String selk  = (String)parameter[0];
		String keySearch1  = (String)parameter[1];
		String keySearch2  = (String)parameter[2];
		Vector vi = new Vector();
		vit = new Vector();
		vrem = new Vector();
		Vector vtot = new Vector();
		try {
			ul = CFile.opens("universallife@universal");
			ord = CFile.opens("ordmast@mstpolicy");
         		whl = CFile.opens("whlmast@mstpolicy");
            		ind = CFile.opens("indmast@mstpolicy");
			insolvent = CFile.opens("insolventpolicy@srvservice");
			insolventtran= CFile.opens("insolventtran@srvservice");
			insolventrem = CFile.opens("insolventremark@srvservice");

			Layout lay = insolventtran.copy().layout();
			String [] tfield = lay.fieldName();
			int  [] tlen = new int[tfield.length];
			for (int i=0;i < tfield.length;i++)
				tlen[i] = lay.length(tfield[i]);

			vit.addElement(tfield);			
			vit.addElement(tlen);		

			lay = insolventrem.copy().layout();
			String [] tfieldr = lay.fieldName();
			int  [] tlenr = new int[tfieldr.length];
			for (int i=0;i < tfieldr.length;i++)
				tlenr[i] = lay.length(tfieldr[i]);

			vrem.addElement(tfieldr);			
			vrem.addElement(tlenr);		
	

			insolvent.start(M.ctoi(selk));
			String kf  = "policyNo";
			if (selk.charAt(0) == '1')
				kf = "firstName";
			else if (selk.charAt(0) == '2')
				kf = "idNo";
			sysDate = DateInfo.sysDate();
			String [] field = {"policyNo","preName","firstName","lastName","idNo","branch","planNo","effectiveDate","matureDate","caseID","insolventDate","status","dataDate","idNo2","firstName2","lastName2","sum"};
			int  [] len = {8,20,30,30,13,3,4,8,8,15,8,1,8,13,30,30,9};
			Rtemp trec = new Rtemp(field,len);
			vi.addElement(field);
			vi.addElement(len);
			System.out.println(kf+"........."+selk+".........."+keySearch1);
			for(boolean st = insolvent.equalGreat(keySearch1) ; st && keySearch1.compareTo(insolvent.get(kf)) == 0;st=insolvent.next())
			{
				System.out.println(kf+"........."+selk+".........."+keySearch1+" "+keySearch2);
				if (keySearch2.trim().length() != 0)
				{
					if(keySearch2.compareTo(insolvent.get("lastName")) != 0)
						continue;
				}
				for (int i = 0 ; i < field.length-1;i++)
					trec.set(field[i],insolvent.get(field[i]));
			
				if(insolvent.get("policyType").charAt(0) == 'O')
				{
					if (ord.equal(insolvent.get("policyNo")))
						trec.set("sum",ord.get("sum"));
				}	
				else if(insolvent.get("policyType").charAt(0) == 'W')
				{
					if (whl.equal(insolvent.get("policyNo")))
						trec.set("sum",whl.get("sum"));
				}
				else if(insolvent.get("policyType").charAt(0) == 'I')
				{
					if (ind.equal(insolvent.get("policyNo")))
						trec.set("sum",ind.get("sum"));
				}
				else if(insolvent.get("policyType").charAt(0) == 'U')
				{
					if (ul.equal(insolvent.get("policyNo")))
						trec.set("sum",ul.get("sum"));
				}
				vi.addElement(trec.copy().getBytes());					
				getInsolventTran(trec.get("policyNo"),insolvent.get("caseID"));
			}
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		vtot.addElement(vi);
		vtot.addElement(vit);
		vtot.addElement(vrem);
		return new Result(vtot,0);
	}
	private void  getInsolventTran(String policyNo,String caseID) throws Exception
        {
		System.out.println("bingo...............................getTran");
                for(boolean st = insolventtran.great(policyNo+caseID);st&&(policyNo+caseID).compareTo(insolventtran.get("policyNo")+insolventtran.get("caseID")) == 0;st=insolventtran.next())
                {
			vit.addElement(insolventtran.copy().getBytes());
			if("PC".indexOf(insolventtran.get("typeOfTran").charAt(0)) >= 0)
				getRemark(policyNo,caseID,insolventtran.get("tranDate"),insolventtran.get("tranTime"));
			
                }
        }
	private void getRemark(String policyNo,String caseID,String tranDate,String tranTime)
	{
		System.out.println("bingo...............................getRemark");
		for (boolean st = insolventrem.equalGreat(policyNo+caseID);st&&(policyNo+caseID).compareTo(insolventrem.get("policyNo")+insolventrem.get("caseID")) == 0;st=insolventrem.next())
		{
			System.out.println("bingo...................111.........getRemark");
			if(tranDate.compareTo(insolventrem.get("tranDate"))  == 0 && tranTime.compareTo(insolventrem.get("tranTime"))  == 0 )
			{
				System.out.println("bingo............vrem...............getRemark");
				vrem.addElement(insolventrem.copy().getBytes());
			}
		}
	}
	 private String padBlank(String f ,int len)
        {
                if(len - f.length() <= 0)
                        return f;
                return ( f+M.clears(' ',len - f.length()));
        }

}


