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
public class  RteHaveOtherPolicy  implements  Task
{
	Mrecord ord;
        Mrecord whl;
	Mrecord ind;
	Mrecord ul;
	Mrecord name;
	Mrecord person;
	String sysDate="";
	Vector vreturn;
	public Result execute(Object param) 
        {
		Object [] parameter = (Object [])param;
		// Vector of String [] (prename,fiestname,lastname,idNo,caseID,caseDate,policyNo)
			
		Vector vdata = (Vector)parameter[0];
                String [] data = (String [])vdata.elementAt(0);
                vreturn = new Vector();
		try {
			ord = CFile.opens("ordmast@mstpolicy");
         		whl = CFile.opens("whlmast@mstpolicy");
            		ind = CFile.opens("indmast@mstpolicy");
			ul = CFile.opens("universallife@universal");

			person = CFile.opens("person@mstperson");
			name = CFile.opens("name@mstperson");			

                        String masterNameID = "";
                        if (ord.equal(data[6]))
                        {
                                masterNameID = ord.get("nameID");
                        }
                        else if (ind.equal(data[6]))
                        {
                                masterNameID = ind.get("nameID");
                        }
                        else if (whl.equal(data[6]))
                        {
                                masterNameID = whl.get("nameID");
                        }
                        else if (ul.equal(data[6]))
                        {
                                masterNameID = ul.get("nameID");
                        }
                        searchAllByNameID(masterNameID,data[6]);
                        person.start(1);
                        if (data[3].trim().length() != 0 && person.equal(data[3]))
                        {
                             name.start(1);
                             for (boolean st = name.equal(person.get("personID"));st && person.get("personID").compareTo(name.get("personID")) == 0;st= name.next())
                             {
                                if (masterNameID.compareTo(name.get("nameID")) != 0)
                                        searchAllByNameID(name.get("nameID"),data[6]);
                             }
                                   
                        }
                        name.start(3);
                        if (data[2].trim().length() > 0 && data[1].trim().length() > 0)
                        {
                                for (boolean st = name.equal(data[2]) ; st && data[2].compareTo(name.get("lastName")) == 0;st= name.next())
                                {
                                        if (name.get("firstName").compareTo(data[1]) == 0)
                                        {
                                                if (name.get("nameID").compareTo(masterNameID) != 0 && name.get("nameID").compareTo(person.get("nameID")) != 0)
                                                         searchAllByNameID(name.get("nameID"),data[6]);
                                        }      
                                }
                        }
                        name.start(0);
                        //Msg.msg(new Mframe(""),"vreturn.size () "+vreturn.size());
                        return new Result(vreturn,0);


		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		
	}
        
        private void searchAllByNameID(String nameID,String policyNo)
        {
                String insuredName = "";
                String idNo = "";
                String birthDate = "";
                if (name.equal(nameID))
                {
                     insuredName = utility.prename.Prename.getAbb(name.get("preName"))+name.get("firstName")+" "+name.get("lastName");   
                     person.start(0);
                     if (person.equal(name.get("personID")))
                     {
                        idNo = person.get("referenceID");
                        birthDate = person.get("birthDate");       
                     }
                     searchInMaster(nameID,ord,policyNo,insuredName,idNo,birthDate);    
                     searchInMaster(nameID,ind,policyNo,insuredName,idNo,birthDate);    
                     searchInMaster(nameID,whl,policyNo,insuredName,idNo,birthDate);    
                     searchInMaster(nameID,ul,policyNo,insuredName,idNo,birthDate);    
                }
        }
        private void searchInMaster(String nameID,Mrecord master,String initPolicyNo,String insuredName,String idNo,String birthDate) 
        {
                master.start(1);
                for (boolean st = master.equal(nameID) ; st && nameID.compareTo(master.get("nameID")) == 0 ; st = master.next())
                {
                        if (initPolicyNo.compareTo(master.get("policyNo")) == 0)
                                continue;
                        for (int i = 0 ; i < vreturn.size();i++)
                        {
                                String [] temp = (String [])vreturn.elementAt(i);
                                if (temp[0].compareTo(master.get("policyNo")) == 0)
                                        continue;
                        }
                        String [] retdata = new String []{master.get("policyNo"),insuredName,idNo,birthDate};
                        vreturn.addElement(retdata);               
                        
                }
                master.start(0);
        }
        private String nextYear(String sdate,String ny)
	{
		String yyyy = sdate.substring(0,4);
		sdate = M.addnum(yyyy,ny)+sdate.substring(4);
		return sdate;	

	}
}


