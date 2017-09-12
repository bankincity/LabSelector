package rte.bl.service.nstatus; 
import 	rte.*;
import  manit.*;
import  manit.rte.*;
import  utility.cfile.CFile;
import  utility.cfile.*; 
import	java.util.*;
import	utility.rteutility.*;
import	utility.support.DateInfo;
import utility.prename.Prename;
/*-----------------------------------------------------*/
public class GetOldSysPolicyStatusN implements InterfaceRpt
{
	String benefit ="0";
	String policyNo;
	public void makeReport(String [] args) throws Exception
	{
		System.out.println(" ========== GetOldSysPolicyStatusN  ================ ");
		String remote	= args[0];  // true or false 
		if(remote.compareTo("false") == 0)
			PublicRte.setRemote(false);
		else
			PublicRte.setRemote(true);	
		String appserv 	= args[1];  // application server 
		System.out.println(" appserv = " +args[1]);
       		String filename = args[2]; 	//
		System.out.println(" filename = " +args[2]); 
		System.out.println(" args = " + args.length); 
		policyNo = (String)args[3]; 
		try { 
			startSearch(); 
			System.out.println("End startSearch process! ");
			rte.RteRpt.recToTemp(temp, filename);
			System.out.println("write temp --> complete");
			rte.RteRpt.insertReportStatus(appserv, filename, 1);
			temp.close();
		}
		catch (Exception e)
		{
			rte.RteRpt.errorToTemp(e.getMessage().getBytes(),filename);
			rte.RteRpt.insertReportStatus(appserv, filename, 9);
		} 
	}
	String pname;
	String fname = "";
	String lname = "";
	String nameID = "";
	Mrecord name;
	Mrecord person;
	String personID;
	Vector vecname ;
	TempMasicFile temp;
	public void startSearch() throws Exception 
	{

		String [] field = {"typeOfPol","policyNo","preName","firstName","lastName","idNo","caseNo","insolventDate","policyStatus1","policyStatusDate1","policyStatus2","policyStatusDate2","oldPolicyStatus1","oldPolicyStatusDate1","oldPolicyStatus2","oldPolicyStatusDate2","personID","nameID"};
		int [] len = {1,8,20,30,30,13,15,8,1,8,1,8,1,8,1,8,13,13};

		temp = new TempMasicFile("rptservice", field, len);
                temp.keyField(false, false, new String [] {"typeOfPol","policyNo"});
                temp.buildTemp();
		
		Mrecord ordmast = CFile.opens("ordmast@mstpolicy");
		Mrecord whlmast = CFile.opens("whlmast@mstpolicy");
		Mrecord indmast = CFile.opens("indmast@mstpolicy");
		name = CFile.opens("name@mstperson");
		person = CFile.opens("person@mstperson");
		if(ordmast.equal(policyNo))
			nameID = ordmast.get("nameID"); 		
		else if(whlmast.equal(policyNo))
			nameID = whlmast.get("nameID"); 		
		else if(indmast.equal(policyNo))
			nameID = indmast.get("nameID"); 	
		System.out.println("nameID ---------------------->"+nameID);	
		vecname = new Vector();	
		if (name.equal(nameID))
		{
			pname = Prename.getAbb(name.get("preName"));
			fname = name.get("firstName");
			lname = name.get("lastName");
		}

		searchNameIDByName(fname,lname);			

		for (int i = 0 ; i < vecname.size();i++)
		{
			nameID = (String)vecname.elementAt(i);
			if (name.equal(nameID))
			{
				pname = Prename.getAbb(name.get("preName"));
				fname = name.get("firstName");
				lname = name.get("lastName");
				personID = name.get("personID");
				if(!person.equal(personID))
					throw new Exception (M.stou("Can not find person"));
				searchMaster(nameID,"I",indmast);
				searchMaster(nameID,"O",ordmast);
				searchMaster(nameID,"W",whlmast);
			}
		}
	}
	private void searchNameIDByName(String fname,String lname) throws Exception
	{
		name.start(3);
                for (boolean st = name.equal(lname) ; st && lname.compareTo(name.get("lastName")) == 0 ;st=name.next())
		{
			if(fname.compareTo(name.get("firstName")) == 0)
                      	{
				vecname.addElement(name.get("nameID"));
			}
		}
		name.start(0);
	}
	private void searchMaster(String nameID,String typePol,Mrecord mrec) throws Exception
	{ 
		
		System.out.println("nameID -------2--------------->"+nameID);	
		mrec.start(1);
                for (boolean st = mrec.equal(nameID) ; st && nameID.compareTo(mrec.get("nameID")) == 0 ;st=mrec.next())
                {
			temp.set(' ');
			temp.set("typeOfPol",typePol);
			temp.set("policyNo",mrec.get("policyNo"));
			temp.set("preName",pname);
			temp.set("firstName",fname);
			temp.set("lastName",lname);
			temp.set("personID",person.get("personID"));
			temp.set("nameID",nameID);
			temp.set("idNo",person.get("referenceID"));
			temp.set("policyStatus1",mrec.get("policyStatus1"));
			temp.set("policyStatusDate1",mrec.get("policyStatusDate1"));
			temp.set("policyStatus2",mrec.get("policyStatus2"));
			temp.set("policyStatusDate2",mrec.get("policyStatusDate2"));
			temp.set("oldPolicyStatus1",mrec.get("oldPolicyStatus1"));
			temp.set("oldPolicyStatusDate1",mrec.get("oldPolicyStatusDate1"));
			temp.set("oldPolicyStatus2",mrec.get("oldPolicyStatus2"));
			temp.set("oldPolicyStatusDate2",mrec.get("oldPolicyStatusDate2"));
			if(!getCaseIDFromNew(temp.get("policyNo")))
				searchCaseID(fname,lname,temp.get("idNo"));
			temp.insert();
		
		}
		mrec.start(0);
	}
	private boolean getCaseIDFromNew(String policy) throws Exception
	{
		Mrecord insolvent = CFile.opens("insolventpolicy@srvservice");
		for (boolean st = insolvent.equalGreat(policy);st && policy.compareTo(insolvent.get("policyNo")) == 0;st=insolvent.next())
		{
			if(insolvent.get("status").charAt(0) != 'C')
			{
				temp.set("caseNo",insolvent.get("caseID").trim());
				temp.set("insolventDate",insolvent.get("insolventDate"));	
				return true;

			}	
		}
		return false ;
	}
	private void searchCaseID(String fname,String lname,String idNo) throws Exception
	{
		Mrecord bankrapt = CFile.opens("bankruptcy@mstperson");
		for (boolean st = bankrapt.equal(fname);st && fname.compareTo(bankrapt.get("firstName")) == 0;st=bankrapt.next())
		{
			if (bankrapt.get("lastName").compareTo(lname) == 0)
			{
				if (bankrapt.get("decidedNo").trim().length() == 0)
					continue;
				temp.set("caseNo",bankrapt.get("decidedNo").trim());
				temp.set("insolventDate",bankrapt.get("inforceDate"));
				return ;
			}
		}
		bankrapt.start(2);
		for (boolean st = bankrapt.equal(idNo);st && idNo.compareTo(bankrapt.get("idNo")) == 0;st = bankrapt.next()) 
		{
			if(bankrapt.get("decidedNo").trim().length() > 0 )
			{
				temp.set("caseNo",bankrapt.get("decidedNo").trim());
				temp.set("insolventDate",bankrapt.get("inforceDate"));	
				break;
			}
		}
		temp.set("caseNo","");
		temp.set("insolventDate","00000000");
			
	}
	private String padBlank(String f ,int len)
	{
		if(len - f.length() <= 0)
			return f;
		return ( f+M.clears(' ',len - f.length()));		
	}
}
