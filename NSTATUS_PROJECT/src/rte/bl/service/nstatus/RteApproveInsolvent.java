package rte.bl.service.nstatus;
import layout.bean.srvservice.InsolventpolicyBean;
import layout.bean.srvservice.InsolventpolicyRecord;
import layout.bean.srvservice.InsolventremarkBean;
import layout.bean.srvservice.InsolventremarkRecord;
import layout.bean.srvservice.InsolventtranRecord;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.cfile.CFile;
import utility.cfile.XTempList;

import java.util.*;
import rte.*;
import rte.bl.dataaccess.DAOInterface;
import rte.bl.dataaccess.PrimaryKey;
import rte.bl.service.datarequest.SearchInsolventTrans;
import utility.rteutility.*;
import utility.prename.Prename;
import utility.address.*;
//import insure.*;
import utility.support.DateInfo;
import utility.prename.Prename;
public class  RteApproveInsolvent implements  manit.rte.TaskUsr
{
	
	
	
	DAOInterface insolventDAO;
	DAOInterface insolventtranDAO;
	DAOInterface insolventremarkDAO;
	
	Record insolvent;
	Record insolventtran;
	Record insolventrem;
	String sysDate="";
	public Result execute(UserInfo user,Object param) 
        {
		Object [] parameter = (Object [])param;
		// Array of {policyNo,firstname,lastname,idNo,caseID,remark,userID}
			
		String [] data  = (String [])parameter[0];
		try {
			
			insolventDAO = new rte.bl.dataaccess.srvservice.InsolventpolicyDAO(user);
			insolventtranDAO = new rte.bl.dataaccess.srvservice.InsolventtranDAO(user);
			insolventremarkDAO = new rte.bl.dataaccess.srvservice.InsolventremarkDAO(user);
			
			sysDate = DateInfo.sysDate();
			System.out.println(" ------------------------------------------data [5] ==="+data[5]);
			String emsg="";
			PrimaryKey pk = new PrimaryKey();
			pk.addKey("policyNo", data[0]);
			pk.addKey("caseID",data[4]);
			
			InsolventpolicyBean ibean = (InsolventpolicyBean)insolventDAO.equal(pk);
			if (ibean != null)
			{
				insolvent = InsolventpolicyRecord.getRecord(ibean);
				if ("SI".indexOf(insolvent.get("status")) >= 0)
				{
					if(data[5].charAt(0) =='Y')
					{
						String idNo = data[5].substring(1);
						rte.bl.service.datarequest.PolicyData.updateIDNoToPerson(idNo, insolvent.get("personID"), user);
						
						System.out.println("person id ---------"+insolvent.get("personID"));
						
						if (emsg.trim().length() == 0)
							insertInsolventTransaction(data,"P",M.stou("ยืนยันการเปลี่ยน ID "));	
						else
							insertInsolventTransaction(data,"P","");
										
					}
					else 
						insertInsolventTransaction(data,"P","");
					insolvent.set("status","P");
					InsolventpolicyBean nbean = InsolventpolicyRecord.getInsolventpolicyBean(insolvent);
					insolventDAO.update(ibean, nbean,pk);
					if(emsg.trim().length() > 0)
						throw new Exception(emsg);
				}
			}
		}
		catch (Exception e)
		{
			return new Result(e.getMessage(),1);
		}
		return new Result("",0);
	}
	private layout.bean.srvservice.InsolventtranBean searchLastInsolventTran(String type ,String policyNo,String caseID) throws Exception
        {
                
                XTempList temp = (new SearchInsolventTrans()).searchData(type, policyNo, caseID);
                if (!temp.last())
                	throw new Exception(M.stou("ไม่พบข้อมูล transaction insolventtrans "));
                
                layout.bean.srvservice.InsolventtranBean bean =  layout.bean.srvservice.InsolventtranBean.newInstance();
                bean.setCaseID(caseID);
                bean.setNewStatus1(temp.get("newStatus1"));
                bean.setNewStatusDate1(temp.get("newStatusDate1"));
                bean.setNewStatus1(temp.get("newStatus2"));
                bean.setNewStatusDate1(temp.get("newStatusDate2"));
                bean.setNewStatus1(temp.get("oldStatus1"));
                bean.setNewStatusDate1(temp.get("oldStatusDate1"));
                bean.setNewStatus1(temp.get("oldStatus2"));
                bean.setNewStatusDate1(temp.get("oldStatusDate2"));
                bean.setPolicyNo(policyNo);
                
                return bean ;
        }
		private void  insertInsolventTransaction(String [] data,String status,String rem) throws Exception
        {
			   layout.bean.srvservice.InsolventtranBean mrec = searchLastInsolventTran("ASIP",data[0],data[4]);
                mrec.setTranDate(sysDate);
                mrec.setTranTime( Masic.time("commontable").substring(8));
                mrec.setUserID(data[6]);
                mrec.setTypeOfTran(status);
                insolventtranDAO.insert(mrec);
		
                if(rem.trim().length() > 0)
                {
                	insolventrem = InsolventremarkRecord.getRecord(InsolventremarkBean.newInstance());
                	insolventrem.set("policyNo",data[0]);
                	insolventrem.set("caseID",data[4]);
                	insolventrem.set("tranDate",sysDate);
               		insolventrem.set("tranTime",insolventtran.get("tranTime"));
          	        insolventrem.set("remark",rem);
          	        insolventremarkDAO.insert(InsolventremarkRecord.getInsolventremarkBean(insolventrem));
          	        
                }
        }
}


