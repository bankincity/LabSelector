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
public class  RteMatchDataInsolvent implements  Task
{
	Mrecord ord;
        Mrecord whl;
	Mrecord ind;
	Mrecord name;
	Mrecord person;
	Mrecord ul;
	Mrecord ulinsolvent;
	Mrecord insolvent;
	Mrecord insolventpeople;
	Mrecord insolventtran;
	Mrecord bankraptcy;
	String sysDate="";
	String wantReturn ="N";
	Vector vreturn;
	String userID;
	Mrecord histinsolvent;
	public Result execute(Object param) 
        {
		Object [] parameter = (Object [])param;
		// Vector of (prename,fiestname,lastname,idNo,caseID,caseDate)
			
		Vector vdata = (Vector)parameter[0];
		userID = "N000000";
		System.out.println("para-----------------------"+parameter.length);
		if(parameter.length >= 2)
		{
			wantReturn = (String)parameter[1];
			vreturn = new Vector();
		}		
		if (parameter.length == 3)
		
			userID = (String)parameter[2];
		
		try {
			ord = CFile.opens("ordmast@mstpolicy");
         		whl = CFile.opens("whlmast@mstpolicy");
            		ind = CFile.opens("indmast@mstpolicy");

			ul = CFile.opens("universallife@universal");
			ulinsolvent = CFile.opens("ulmasterinsolvent@universal");

			person = CFile.opens("person@mstperson");
			person.start(1);
			name = CFile.opens("name@mstperson");			
			insolvent = CFile.opens("insolventpolicy@srvservice");
			histinsolvent = CFile.opens("histinsolventpolicy@srvservice");
			insolventpeople = CFile.opens("insolventpeople@srvservice");
			insolventtran = CFile.opens("insolventtran@srvservice");
			bankraptcy = CFile.opens("bankruptcy@mstperson");
			Mrecord comp = CFile.opens("compensateid@mstperson");
			comp.start(1);
			if(wantReturn.charAt(0) == 'R')
			{
				Layout  lay = insolvent.copy().layout();
                       		String [] tfieldr = lay.fieldName();
                  	        int  [] tlenr = new int[tfieldr.length];
                 	        for (int i=0;i < tfieldr.length;i++)
                         	       tlenr[i] = lay.length(tfieldr[i]);
				vreturn.addElement(tfieldr);
				vreturn.addElement(tlenr);
			}
			sysDate = DateInfo.sysDate();
			System.out.println("vdata------->"+vdata.size());
			for (int i = 0 ; i < vdata.size();i++)
			{
				String [] data = (String [])vdata.elementAt(i);
				System.out.println("data[3]--"+data[3]+"--"+data[1]+"--"+data[2]);
				person.start(1);
				boolean foundbyid = false ;
				if(data[3].trim().length() == 13)
				{
					
				    if(person.equal(data[3]))
				    {	
					foundbyid = true;	
					boolean foundname = false ;
				//	boolean fi = false ,fo = false ,fw = false ;
					System.out.println("in search by ID  "+data[3]+"  "+data[1]+"  "+data[2]+"  "+foundname);
					name.start(1);
					String pid = person.get("personID");
					for (boolean st = name.equal(pid); st && pid.compareTo(name.get("personID")) == 0 ; st=name.next())
					{
						System.out.println("nameid----------------------++++++++++++++++="+pid+"----------------------"+name.get("nameID"));
						
						if(data[1].compareTo(name.get("firstName")) == 0 && data[2].compareTo(name.get("lastName")) == 0 )
						{
							 searchMasterToInsolvent(name.get("nameID"),data,ind,"I","A");
							 searchMasterToInsolvent(name.get("nameID"),data,ord,"O","A");
							 searchMasterToInsolvent(name.get("nameID"),data,whl,"W","A");
							 searchMasterToInsolvent(name.get("nameID"),data,ul,"U","A");
				//			 foundname = true;
						}
						else {
							searchMasterToInsolvent(name.get("nameID"),data,ind,"I","I");
							searchMasterToInsolvent(name.get("nameID"),data,ord,"O","I");
							searchMasterToInsolvent(name.get("nameID"),data,whl,"W","I");
							searchMasterToInsolvent(name.get("nameID"),data,ul,"U","I");
						//foundname = false ;
						}
					 }
					 if (!foundname)
					 {		
							name.start(3);
							System.out.println("in search by name "+data[1]+"  "+data[2]);
							for (boolean ss = name.equal(data[2]) ; ss && data[2].compareTo(name.get("lastName")) == 0 ;ss=name.next())
							{
								System.out.println("in loop search by name "+data[1]+"  "+data[2]);
								if(data[1].compareTo(name.get("firstName")) == 0)
								{
	
									System.out.println("Yes  2  !!!!!! found "+data[1]+"  "+data[2]+".....nameid ............."+name.get("nameID"));
							
									searchMasterToInsolvent(name.get("nameID"),data,ind,"I","S");
									searchMasterToInsolvent(name.get("nameID"),data,ord,"O","S");
									searchMasterToInsolvent(name.get("nameID"),data,whl,"W","S");
									searchMasterToInsolvent(name.get("nameID"),data,ul,"U","S");
									foundname = true;
								//	break;
								}
							}			
					}
				   }
				   if(comp.equal(data[3]))
				   {
					data[3] = comp.get("compensateID");
				    	if(person.equal(data[3]))
				    	{	
						foundbyid = true;	
						boolean foundname = false ;
				//	boolean fi = false ,fo = false ,fw = false ;
						System.out.println("in search by ID  "+data[3]+"  "+data[1]+"  "+data[2]+"  "+foundname);
						name.start(1);
						String pid = person.get("personID");
						for (boolean st = name.equal(pid); st && pid.compareTo(name.get("personID")) == 0 ; st=name.next())
						{
							System.out.println("nameid----------------------++++++++++++++++="+pid+"----------------------"+name.get("nameID"));
							searchMasterToInsolvent(name.get("nameID"),data,ind,"I","I");
							searchMasterToInsolvent(name.get("nameID"),data,ord,"O","I");
							searchMasterToInsolvent(name.get("nameID"),data,whl,"W","I");
							searchMasterToInsolvent(name.get("nameID"),data,ul,"U","I");
						//	foundname = false ;
						}
						name.start(3);
						System.out.println("in search by name "+data[1]+"  "+data[2]);
						for (boolean ss = name.equal(data[2]) ; ss && data[2].compareTo(name.get("lastName")) == 0 ;ss=name.next())
						{
							System.out.println("in loop search by name "+data[1]+"  "+data[2]);
							if(data[1].compareTo(name.get("firstName")) == 0)
							{
								System.out.println("Yes  2  !!!!!! found "+data[1]+"  "+data[2]);
								searchMasterToInsolvent(name.get("nameID"),data,ind,"I","S");
								searchMasterToInsolvent(name.get("nameID"),data,ord,"O","S");
								searchMasterToInsolvent(name.get("nameID"),data,whl,"W","S");
								searchMasterToInsolvent(name.get("nameID"),data,ul,"U","S");
								foundname = true;
							}
						}
				   		System.out.println("in search by ID  "+data[3]+"  "+data[1]+"  "+data[2]+"  "+foundname);			    	      if(!foundname)
			           		{
							insertInsolventPeople(data,"A");
				   		}
				  		 else {
							insertInsolventPeople(data,"F");
				   		}
					}			
				   }
				}
				if (!foundbyid)
				{
					boolean foundname = false ;
					name.start(3);
					System.out.println("in search by name "+data[1]+"  "+data[2]);
					for (boolean st = name.equal(data[2]) ; st && data[2].compareTo(name.get("lastName")) == 0 ;st=name.next())
					{
						System.out.println("in loop search by name "+data[1]+"  "+data[2]);
						if(data[1].compareTo(name.get("firstName")) == 0)
						{
	
							System.out.println("Yes !!!!!!!! found "+data[1]+"  "+data[2]);
							
							searchMasterToInsolvent(name.get("nameID"),data,ind,"I","S");
							searchMasterToInsolvent(name.get("nameID"),data,ord,"O","S");
							searchMasterToInsolvent(name.get("nameID"),data,whl,"W","S");
							searchMasterToInsolvent(name.get("nameID"),data,ul,"U","S");
							foundname = true;
						}
					}
					if(!foundname)
					{
						insertInsolventPeople(data,"A");
					}
					else {
						insertInsolventPeople(data,"F");
					}
				}
                                else {
				        insertInsolventPeople(data,"F");
                                }
				System.out.println("found by id === "+foundbyid);						
			}
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		
		if(wantReturn.charAt(0) == 'R')
			return new Result(vreturn,0);
		return new Result("",0);
	}
	private boolean  searchMasterToInsolvent(String nameID,String [] data,Mrecord mrec,String policyType,String match) throws Exception 
	{
		if (policyType.charAt(0)== 'U')
			return (searchMasterToInsolventForUL(nameID,data,mrec,policyType,match));
		mrec.start(1);
		for (boolean st = mrec.equal(nameID) ; st && nameID.compareTo(mrec.get("nameID")) == 0 ;st=mrec.next())
		{
			System.out.println(nameID +"   search in master "+policyType+" "+mrec.get("policyNo")+"---"+mrec.get("policyStatus1")+"---"+mrec.get("policyStatusDate1"));
			if ("N".indexOf(mrec.get("policyStatus1")) >= 0 && mrec.get("policyStatusDate1").compareTo(DateInfo.sysDate()) != 0)
			{
				insertInsolventPeople(data,"N");
				continue;
			}	
			else  if("ABCFIJNRTU".indexOf(mrec.get("policyStatus1")) >= 0 ||
				("E".indexOf(mrec.get("policyStatus1")) >= 0 && mrec.get("policyStatusDate2").compareTo(sysDate) >= 0) ||
				("M".indexOf(mrec.get("policyStatus1")) >= 0 && "U".indexOf(mrec.get("policyStatus2")) >= 0) ||
					 
				("E".indexOf(mrec.get("policyStatus1")) >= 0 && mrec.get("policyStatusDate2").compareTo(beforeMatDate(nameID,mrec)) == 0) ||
				("W".indexOf(mrec.get("policyStatus1")) >= 0 && mrec.get("policyStatusDate1").compareTo(sysDate) >= 0) ||
				("LOY".indexOf(mrec.get("policyStatus1")) >= 0 && nextYear(mrec.get("policyStatusDate1"),"5").compareTo(sysDate) >= 0) )
			{
				System.out.println("-------------------------"+mrec.get("policyNo"));
				if (insolvent.equal(mrec.get("policyNo")+data[4]) && insolvent.get("status").charAt(0) == 'C')
				{
					deleteInsolventTran(insolvent.get("policyNo"),insolvent.get("caseID"));
					insolvent.delete();
				}
				if (!insolvent.equal(mrec.get("policyNo")+data[4]))
				{
					insolvent.set('0');
					insolvent.set("policyNo",mrec.get("policyNo"));
					insolvent.set("policyType",policyType);
					insolvent.set("preName",Prename.getAbb(name.get("preName")));
					insolvent.set("firstName",name.get("firstName"));
					insolvent.set("lastName",name.get("lastName"));
					insolvent.set("branch",mrec.get("branch"));
					insolvent.set("planNo",mrec.get("planCode"));
					insolvent.set("effectiveDate",mrec.get("effectiveDate"));
					insolvent.set("status",match);					
					insolvent.set("caseID",data[4]);
					insolvent.set("insolventDate",data[5]);
					insolvent.set("idNo2",data[3]);
					insolvent.set("preName2",data[0]);
					insolvent.set("firstName2",data[1]);
					insolvent.set("lastName2",data[2]);
					insolvent.set("nameID",name.get("nameID"));
					insolvent.set("dataDate",sysDate);
					if("AI".indexOf(match) >= 0)
					{
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						String matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
						insolvent.set("matureDate",matDate);
						insolvent.set("idNo",person.get("referenceID"));
						insolvent.set("personID",person.get("personID"));
						insolvent.set("birthDate",person.get("birthDate"));
					}
					else if ("S".indexOf(match) >= 0)
					{
						person.start(0);
						String matDate = "00000000";
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						if(person.equal(name.get("personID")))
						{
							matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
							
							insolvent.set("personID",person.get("personID"));
							insolvent.set("birthDate",person.get("birthDate"));
							insolvent.set("idNo",person.get("referenceID"));
							insolvent.set("matureDate",matDate);
						}
					}
					insolvent.insert();
					if(wantReturn.charAt(0) == 'R')
					{
						vreturn.addElement(insolvent.copy().getBytes());
					}
					insertInsolventTrans(mrec,data,match);
					updateFirstStatus(policyType,mrec);
				}
				else {
					histinsolvent.set('0');
					histinsolvent.set("policyNo",mrec.get("policyNo"));
					histinsolvent.set("policyType",policyType);
					histinsolvent.set("preName",Prename.getAbb(name.get("preName")));
					histinsolvent.set("firstName",name.get("firstName"));
					histinsolvent.set("lastName",name.get("lastName"));
					histinsolvent.set("branch",mrec.get("branch"));
					histinsolvent.set("planNo",mrec.get("planCode"));
					histinsolvent.set("effectiveDate",mrec.get("effectiveDate"));
					histinsolvent.set("status",match);					
					histinsolvent.set("caseID",data[4]);
					histinsolvent.set("insolventDate",data[5]);
					histinsolvent.set("idNo2",data[3]);
					histinsolvent.set("preName2",data[0]);
					histinsolvent.set("firstName2",data[1]);
					histinsolvent.set("lastName2",data[2]);
					histinsolvent.set("nameID",name.get("nameID"));
					histinsolvent.set("dataDate",sysDate);
					if("AI".indexOf(match) >= 0)
					{
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						String matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
						histinsolvent.set("matureDate",matDate);
						histinsolvent.set("idNo",person.get("referenceID"));
						histinsolvent.set("personID",person.get("personID"));
						histinsolvent.set("birthDate",person.get("birthDate"));
					}
					else if ("S".indexOf(match) >= 0)
					{
						person.start(0);
						String matDate = "00000000";
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						if(person.equal(name.get("personID")))
						{
							matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
							
							histinsolvent.set("personID",person.get("personID"));
							histinsolvent.set("birthDate",person.get("birthDate"));
							histinsolvent.set("idNo",person.get("referenceID"));
							histinsolvent.set("matureDate",matDate);
						}
					}
					histinsolvent.insert();
				}	
			}
			else 
			{
				if (insolvent.equal(mrec.get("policyNo")+data[4]) && insolvent.get("status").charAt(0) == 'C')
				{
					deleteInsolventTran(insolvent.get("policyNo"),insolvent.get("caseID"));
					insolvent.delete();
				}
				if (!insolvent.equal(mrec.get("policyNo")+data[4]))
				{
					insolvent.set('0');
					insolvent.set("policyNo",mrec.get("policyNo"));
					insolvent.set("policyType",policyType);
					insolvent.set("preName",Prename.getAbb(name.get("preName")));
					insolvent.set("firstName",name.get("firstName"));
					insolvent.set("lastName",name.get("lastName"));
					insolvent.set("branch",mrec.get("branch"));
					insolvent.set("planNo",mrec.get("planCode"));
					insolvent.set("effectiveDate",mrec.get("effectiveDate"));
					insolvent.set("status",match);					
					insolvent.set("caseID",data[4]);
					insolvent.set("insolventDate",data[5]);
					insolvent.set("nameID",name.get("nameID"));
					insolvent.set("dataDate",sysDate);
					insolvent.set("idNo2",data[3]);
					insolvent.set("preName2",data[0]);
					insolvent.set("firstName2",data[1]);
					insolvent.set("lastName2",data[2]);
					if("AI".indexOf(match) >= 0)
					{
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						String matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
						insolvent.set("matureDate",matDate);
						insolvent.set("personID",person.get("personID"));
						insolvent.set("birthDate",person.get("birthDate"));
						insolvent.set("idNo",person.get("referenceID"));
					}
					else if ("S".indexOf(match) >= 0)
					{
						person.start(0);
						String matDate = "00000000";
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						if(person.equal(name.get("personID")))
						{
							matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
							
							insolvent.set("personID",person.get("personID"));
							insolvent.set("idNo",person.get("referenceID"));
							insolvent.set("birthDate",person.get("birthDate"));
						}					
						insolvent.set("matureDate",matDate);
					}
					if (updateBenefitStatus(mrec.get("policyNo"),policyType))
					{
						insertInsolventTrans(mrec,data,"D",match);
						insolvent.set("document","D");
						insolvent.insert();
						insertRemark(mrec.get("policyNo"),true);
						if(wantReturn.charAt(0) == 'R')
						{
							vreturn.addElement(insolvent.copy().getBytes());
						}
					}
				/*	else {
						insertInsolventTrans(mrec,data,"P",match);
						insolvent.set("document","P");
						insolvent.insert();
					}*/
				}				
				
			}
			updateRpStatus(mrec.get("policyNo"),policyType);
					
		} 
		return false ;
	}
	private boolean  searchMasterToInsolventForUL(String nameID,String [] data,Mrecord mrec,String policyType,String match) throws Exception 
	{
		System.out.println("find in universallife.....................................");
		mrec.start(1);
		for (boolean st = mrec.equal(nameID) ; st && nameID.compareTo(mrec.get("nameID")) == 0 ;st=mrec.next())
		{
			System.out.println(nameID +"   search in master "+policyType+" "+mrec.get("policyNo")+"---"+mrec.get("policyStatus1")+"---"+mrec.get("policyStatusDate1"));
			if ("N".indexOf(mrec.get("policyStatus1")) >= 0 && mrec.get("policyStatusDate1").compareTo(DateInfo.sysDate()) != 0)
			{
				insertInsolventPeople(data,"N");
				continue;
			}	
			else  if("IHOYTB".indexOf(mrec.get("policyStatus1")) >= 0 || ("L".indexOf(mrec.get("policyStatus1")) >= 0 && mrec.get("policyStatus2").charAt(0) != 'P'))
			{
				System.out.println("-------------------------"+mrec.get("policyNo"));
				if (insolvent.equal(mrec.get("policyNo")+data[4]) && insolvent.get("status").charAt(0) == 'C')
				{
					deleteInsolventTran(insolvent.get("policyNo"),insolvent.get("caseID"));
					insolvent.delete();
				}
				if (!insolvent.equal(mrec.get("policyNo")+data[4]))
				{
					insolvent.set('0');
					insolvent.set("policyNo",mrec.get("policyNo"));
					insolvent.set("policyType",policyType);
					insolvent.set("preName",Prename.getAbb(name.get("preName")));
					insolvent.set("firstName",name.get("firstName"));
					insolvent.set("lastName",name.get("lastName"));
					insolvent.set("branch",mrec.get("branch"));
					insolvent.set("planNo",mrec.get("planCode"));
					insolvent.set("effectiveDate",mrec.get("effectiveDate"));
					insolvent.set("status",match);					
					insolvent.set("caseID",data[4]);
					insolvent.set("insolventDate",data[5]);
					insolvent.set("idNo2",data[3]);
					insolvent.set("preName2",data[0]);
					insolvent.set("firstName2",data[1]);
					insolvent.set("lastName2",data[2]);
					insolvent.set("nameID",name.get("nameID"));
					insolvent.set("dataDate",sysDate);
					if("AI".indexOf(match) >= 0)
					{
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						String matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
						insolvent.set("matureDate",matDate);
						insolvent.set("idNo",person.get("referenceID"));
						insolvent.set("personID",person.get("personID"));
						insolvent.set("birthDate",person.get("birthDate"));
					}
					else if ("S".indexOf(match) >= 0)
					{
						person.start(0);
						String matDate = "00000000";
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						if(person.equal(name.get("personID")))
						{
							matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
							
							insolvent.set("personID",person.get("personID"));
							insolvent.set("birthDate",person.get("birthDate"));
							insolvent.set("idNo",person.get("referenceID"));
							insolvent.set("matureDate",matDate);
						}
					}
					insolvent.insert();
					if(wantReturn.charAt(0) == 'R')
					{
						vreturn.addElement(insolvent.copy().getBytes());
					}
					insertInsolventTrans(mrec,data,match);
					updateFirstStatus(policyType,mrec);
				}
				else {
					histinsolvent.set('0');
					histinsolvent.set("policyNo",mrec.get("policyNo"));
					histinsolvent.set("policyType",policyType);
					histinsolvent.set("preName",Prename.getAbb(name.get("preName")));
					histinsolvent.set("firstName",name.get("firstName"));
					histinsolvent.set("lastName",name.get("lastName"));
					histinsolvent.set("branch",mrec.get("branch"));
					histinsolvent.set("planNo",mrec.get("planCode"));
					histinsolvent.set("effectiveDate",mrec.get("effectiveDate"));
					histinsolvent.set("status",match);					
					histinsolvent.set("caseID",data[4]);
					histinsolvent.set("insolventDate",data[5]);
					histinsolvent.set("idNo2",data[3]);
					histinsolvent.set("preName2",data[0]);
					histinsolvent.set("firstName2",data[1]);
					histinsolvent.set("lastName2",data[2]);
					histinsolvent.set("nameID",name.get("nameID"));
					histinsolvent.set("dataDate",sysDate);
					if("AI".indexOf(match) >= 0)
					{
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						String matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
						histinsolvent.set("matureDate",matDate);
						histinsolvent.set("idNo",person.get("referenceID"));
						histinsolvent.set("personID",person.get("personID"));
						histinsolvent.set("birthDate",person.get("birthDate"));
					}
					else if ("S".indexOf(match) >= 0)
					{
						person.start(0);
						String matDate = "00000000";
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						if(person.equal(name.get("personID")))
						{
							matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
							
							histinsolvent.set("personID",person.get("personID"));
							histinsolvent.set("birthDate",person.get("birthDate"));
							histinsolvent.set("idNo",person.get("referenceID"));
							histinsolvent.set("matureDate",matDate);
						}
					}
					histinsolvent.insert();
				}	
			}
			else 
			{
				if (insolvent.equal(mrec.get("policyNo")+data[4]) && insolvent.get("status").charAt(0) == 'C')
				{
					deleteInsolventTran(insolvent.get("policyNo"),insolvent.get("caseID"));
					insolvent.delete();
				}
				if (!insolvent.equal(mrec.get("policyNo")+data[4]))
				{
					insolvent.set('0');
					insolvent.set("policyNo",mrec.get("policyNo"));
					insolvent.set("policyType",policyType);
					insolvent.set("preName",Prename.getAbb(name.get("preName")));
					insolvent.set("firstName",name.get("firstName"));
					insolvent.set("lastName",name.get("lastName"));
					insolvent.set("branch",mrec.get("branch"));
					insolvent.set("planNo",mrec.get("planCode"));
					insolvent.set("effectiveDate",mrec.get("effectiveDate"));
					insolvent.set("status",match);					
					insolvent.set("caseID",data[4]);
					insolvent.set("insolventDate",data[5]);
					insolvent.set("nameID",name.get("nameID"));
					insolvent.set("dataDate",sysDate);
					insolvent.set("idNo2",data[3]);
					insolvent.set("preName2",data[0]);
					insolvent.set("firstName2",data[1]);
					insolvent.set("lastName2",data[2]);
					if("AI".indexOf(match) >= 0)
					{
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						String matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
						insolvent.set("matureDate",matDate);
						insolvent.set("personID",person.get("personID"));
						insolvent.set("birthDate",person.get("birthDate"));
						insolvent.set("idNo",person.get("referenceID"));
					}
					else if ("S".indexOf(match) >= 0)
					{
						person.start(0);
						String matDate = "00000000";
						TLPlan p  = PlanSpec.getPlan(mrec.get("planCode"));
						if(person.equal(name.get("personID")))
						{
							matDate =   Insure.matureDate(mrec.get("effectiveDate"),p.endowmentYear(mrec.get("insuredAge")),p.code(),person.get("birthDate"));
							
							insolvent.set("personID",person.get("personID"));
							insolvent.set("idNo",person.get("referenceID"));
							insolvent.set("birthDate",person.get("birthDate"));
						}					
						insolvent.set("matureDate",matDate);
					}
					if (updateBenefitStatus(mrec.get("policyNo"),policyType))
					{
						insertInsolventTrans(mrec,data,"D",match);
						insolvent.set("document","D");
						insolvent.insert();
						insertRemark(mrec.get("policyNo"),true);
						if(wantReturn.charAt(0) == 'R')
						{
							vreturn.addElement(insolvent.copy().getBytes());
						}
					}
				/*	else {
						insertInsolventTrans(mrec,data,"P",match);
						insolvent.set("document","P");
						insolvent.insert();
					}*/
				}				
				
			}
			updateRpStatus(mrec.get("policyNo"),policyType);
					
		} 
		return false ;
	}
	private String beforeMatDate(String nameID,Mrecord mastrec)
	{
		try {
			Mrecord nrec1 = CFile.opens("name@mstperson");
			Mrecord prec1 = CFile.opens("person@mstperson");
			if (nrec1.equal(nameID))
			{
				if(prec1.equal(nrec1.get("personID")))
				{
					TLPlan p  = PlanSpec.getPlan(mastrec.get("planCode"));
					String matDate =   Insure.matureDate(mastrec.get("effectiveDate"),p.endowmentYear(mastrec.get("insuredAge")),p.code(),prec1.get("birthDate"));
					matDate = M.nextdate(matDate,-1);
					return matDate;
				}
				
			}
		}
		catch(Exception e)
		{
		}
		return "00000000";
	}
	private String nextYear(String sdate,String ny)
	{
		String yyyy = sdate.substring(0,4);
		sdate = M.addnum(yyyy,ny)+sdate.substring(4);
		return sdate;	

	}
	private void updateRpStatus(String policyNo,String policyType) throws Exception
	{
		Vector pol = new Vector();
                pol.add(policyNo);
		if (policyType.charAt(0) =='U')
		{
			Result rs = PublicRte.getResult("blbranch","rte.bl.universal.receipt.RteCancelULReservedReceipt",new Object[] {policyType, pol,"33","X000033"});
			if (rs.status()!=0)
				throw new Exception ("Error RteCancelReservedReceipt = "+(String)rs.value());
		}
		else {
			Result rs = PublicRte.getResult("blbranch","rte.bl.branch.receipt.RteCancelReservedReceipt",new Object[] {policyType, pol,"33","X000033"});
			if (rs.status()!=0)
				throw new Exception ("Error RteCancelReservedReceipt = "+(String)rs.value());
		}

	}
	private void updateFirstStatus(String policyType,Mrecord mrec) throws Exception
	{
		String filename ="";
		switch (policyType.charAt(0))
		{
			case 'I' : filename = "indmast@mstpolicy"; break;
			case 'O' : filename = "ordmast@mstpolicy"; break;
			case 'W' : filename = "whlmast@mstpolicy"; break;
			case 'U' : filename = "universallife@universal"; break;
		}
		if (policyType.charAt(0) == 'U')
		{
			ulinsolvent.set("policyNo",mrec.get("policyNo"));
			ulinsolvent.set("insolventDate",sysDate);
			ulinsolvent.set("policyStatus1",mrec.get("policyStatus1"));	
			ulinsolvent.set("policyStatus2",mrec.get("policyStatus2"));	
			ulinsolvent.set("policyStatusDate1",mrec.get("policyStatusDate1"));	
			ulinsolvent.set("policyStatusDate2",mrec.get("policyStatusDate2"));	
			if (!ulinsolvent.insert())
				throw new Exception(M.stou("Can not insert ulmasterinsolvent@universal")+M.itoc(ulinsolvent.lastError()));
		}		
		Vector rv = new Vector();
                rv.add(new String[] {"policyStatus1","N"});
                rv.add(new String[] {"policyStatusDate1",sysDate });
                rv.add(new String[] {"policyStatus2"," "});
                rv.add(new String[] {"policyStatusDate2","00000000" });
                rv.add(new String[] {"oldPolicyStatus1", mrec.get("policyStatus1")});
                rv.add(new String[] {"oldPolicyStatusDate1",mrec.get("policyStatusDate1")});
                rv.add(new String[] {"oldPolicyStatus2", mrec.get("policyStatus2")});
                rv.add(new String[] {"oldPolicyStatusDate2",mrec.get("policyStatusDate2")});
		Object [] v = new Object [] {filename,new String[] {policyType,mrec.get("policyNo"),mrec.get("policyNo"),"C"},"U",rv,"Appl_N","A"};
		Object [] ob = new Object[] {"searchmaster", "rte.search.master.log.ProcessMasterAndLog", v};
		Result rs = PublicRte.getResult("blmaster", "rte.ForwardRequest", ob);
		if (rs.status() == 0)
		{
			Vector m = new Vector();
			m.add( M.stou("บันทึกสถานะ N วันที่") + DateInfo.formatDate(1,sysDate)); 
			rs = PublicRte.getResult("blmaster","rte.bl.master.RemarkFromTransaction", new Object[] {mrec.get("policyNo"),"04",m});
		}
	}
	private boolean  updateBenefitStatus(String policyNo,String policyType) throws Exception
	{
		if (policyType.charAt(0) =='U')
			return false ;
		boolean found = false;
		String filename ="";
		switch (policyType.charAt(0))
		{
			case 'I' : filename = "dvmastindi@mstpolicy"; break;
			case 'O' : filename = "dvmastordo@mstpolicy"; break;
			case 'W' : filename = "dvmastwhlw@mstpolicy"; break;
		}
		Mrecord dv = CFile.opens(filename);
		for (boolean st = dv.equalGreat(policyNo) ; st && policyNo.compareTo(dv.get("policyNo")) == 0 ; st=dv.next())
		{
			if (dv.get("payFlag").charAt(0) == '2')
			{
				dv.set("payFlag","N");
				dv.update();
				found = true;
			}
		}
		dv.close();
		switch (policyType.charAt(0))
		{
			case 'I' : filename = ""; break;
			case 'O' : filename = "paycmastord@mstpolicy"; break;
			case 'W' : filename = ""; break;
		}
		if(filename.trim().length() > 0)
		{
			dv = CFile.opens(filename);
			for (boolean st = dv.equalGreat(policyNo) ; st && policyNo.compareTo(dv.get("policyNo")) == 0 ; st=dv.next())
			{
				if (dv.get("payFlag").charAt(0) == '2')
				{
					dv.set("payFlag","N");
					dv.update();
					found = true;
				}
			}
		}	
		return found ;
	}
	private void insertRemark(String policyNo,boolean in) throws Exception
	{
		Vector m = new Vector();
		if(in)
			m.add( M.stou("บันทึกสถานะ N วันที่") + DateInfo.formatDate(1,sysDate)); 
		else
			m.add( M.stou("ยกเลิกบันทึกสถานะ N วันที่") + DateInfo.formatDate(1,sysDate)); 
			
		Result rs = PublicRte.getResult("blmaster","rte.bl.master.RemarkFromTransaction", new Object[] {policyNo,"04",m});
		if (rs.status() != 0)
			throw new Exception((String)rs.value());
	}
	private void insertInsolventPeople(String [] data,String status) throws Exception
	{
                //Msg.msg(new Mframe(""),"to insert people");
		insolventpeople.set("idNo",data[3]);
		insolventpeople.set("caseID",data[4]);
		insolventpeople.set("insolventDate",data[5]);
		insolventpeople.set("preName",data[0]);
		insolventpeople.set("firstName",data[1]);
		insolventpeople.set("lastName",data[2]);
		insolventpeople.set("dataDate",sysDate);
		insolventpeople.set("status",status);
		if(!insolventpeople.insert() && insolventpeople.lastError() != 27)
//if(!insolventpeople.insert())
			throw new Exception(M.stou("ไม่สามารถ insert  insolventpeople error = ")+M.itoc(insolventpeople.lastError()));		
		bankraptcy.set(' ');
		bankraptcy.set("idNo",data[3]);
		bankraptcy.set("preName",data[0]);
		bankraptcy.set("firstName",data[1]);
		bankraptcy.set("lastName",data[2]);
		bankraptcy.set("decidedNo",data[4]);
		bankraptcy.set("inforceDate",data[5]);
		bankraptcy.insert();		
	} 
	private void insertInsolventTrans(Mrecord mrec,String [] data,String typeTran) throws Exception
	{
		insertInsolventTrans(mrec,data,"N",typeTran); 
	}
	private void insertInsolventTrans(Mrecord mrec,String [] data,String status2,String typeTran) throws Exception
	{
		insolventtran.set("policyNo",mrec.get("policyNo"));
		insolventtran.set("caseID",data[4]);
		insolventtran.set("tranDate",sysDate);
		insolventtran.set("typeOfTran",typeTran);
		insolventtran.set("tranTime", Masic.time("commontable").substring(8));
		insolventtran.set("userID",userID);
		if(status2.charAt(0) == 'D')
		{
			insolventtran.set("newStatus1",mrec.get("policyStatus1"));
			insolventtran.set("newStatusDate1",mrec.get("policyStatusDate1"));
			insolventtran.set("newStatus2","N");
			insolventtran.set("newStatusDate2",sysDate);
		}
		else if(status2.charAt(0) == 'P')
		{
			insolventtran.set("newStatus1",mrec.get("policyStatus1"));
			insolventtran.set("newStatusDate1",mrec.get("policyStatusDate1"));
			insolventtran.set("newStatus2",mrec.get("policyStatus2"));
			insolventtran.set("newStatusDate2",mrec.get("policyStatusDate2"));
		}
		else { 
			insolventtran.set("newStatus1","N");
			insolventtran.set("newStatusDate1",sysDate);
			insolventtran.set("newStatus2",mrec.get("policyStatus2"));
			insolventtran.set("newStatusDate2",mrec.get("policyStatusDate2"));
		}
		insolventtran.set("oldStatus1",mrec.get("policyStatus1"));
		insolventtran.set("oldStatusDate1",mrec.get("policyStatusDate1"));
		insolventtran.set("oldStatus2",mrec.get("policyStatus2"));
		insolventtran.set("oldStatusDate2",mrec.get("policyStatusDate2"));
		insolventtran.insert();	
	}
	private void deleteInsolventTran(String policyNo,String caseID) throws Exception
	{
                Mrecord insolventtran1 =CFile.opens("insolventtran@srvservice");
                for(boolean st = insolventtran1.great(policyNo+caseID);st&&(policyNo+caseID).compareTo(insolventtran1.get("policyNo")+insolventtran1.get("caseID")) == 0;st=insolventtran1.next())
                {
			insolventtran1.delete();
                }
                insolventtran1.close();

	}
}


