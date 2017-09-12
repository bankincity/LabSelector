package service.nstatus;
import manit.*;
import java.util.*;
import utility.rteutility.*;
import manit.rte.*;
import utility.cfile.*;
import service.bucket.*;
public class InsolventClient
{
	static boolean remote = false ;
	public static void sendToMatchData(Vector vdata) throws Exception
	{
		PublicRte.setRemote(remote);
                Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteMatchDataInsolvent",new Object[] {vdata,"N",Passwd.getEmployeeNo()});
                System.out.println("result of RteMatchDataInsolvent -------"+res.status());
                if(res.status() != 0)
                        throw new Exception((String)res.value());
	}
	public static Vector  sendToMatchData(Vector vdata,boolean ret) throws Exception
	{
		PublicRte.setRemote(remote);
                Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteMatchDataInsolvent",new Object[] {vdata,"R",Passwd.getEmployeeNo()});
                System.out.println("result of RteMatchDataInsolvent -------"+res.status());
                if(res.status() != 0)
                        throw new Exception((String)res.value());
		System.out.println(res.value().getClass());
		return ((Vector)res.value());
	}
	public static Vector  sendToMatchDataByPolicy(Vector vdata,boolean ret) throws Exception
	{
		PublicRte.setRemote(remote);
                Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteMatchDataInsolventByPolicy",new Object[] {vdata,"R",Passwd.getEmployeeNo()});
                System.out.println("result of RteMatchDataInsolvent -------"+res.status());
                if(res.status() != 0)
                        throw new Exception((String)res.value());
		System.out.println(res.value().getClass());
		return ((Vector)res.value());
	}
	public static TempMasicFile getInsolventData(String typeInsolvent,String dataDate,String lastDataDate) throws Exception
	{
		return (getInsolventData(typeInsolvent, dataDate,lastDataDate,"0"));
	}
	public static TempMasicFile getInsolventData(String typeInsolvent,String dataDate,String lastDataDate,String ben) throws Exception
        {
		String [] field = {"idNo","policyNo","preName","firstName","lastName","birthDate","branch","idNo2","preName2","firstName2","lastName2","caseID","pstatus"};
                int [] len = {13,8,20,30,30,8,3,13,20,30,30,15,1};

                TempMasicFile temp = new TempMasicFile("serviceapp",field,len);
                temp.keyField(true,true,new String [] {"idNo2","firstName2"});
                temp.keyField(false,false,new String [] {"policyNo","caseID"});
                temp.buildTemp();

                String strRemote = "false";
                if(remote)
                        strRemote = "true";
                LoadFile.executeFileMaker(strRemote,"rte.bl.service.nstatus.GetInsolventByFile",new String [] {typeInsolvent,dataDate,lastDataDate,ben},temp);
		if(temp.fileSize() == 0)
			throw new Exception(M.stou("ไม่พบข้อมูลตามเงื่อนไขที่ระบุ"));
		System.out.println("temp----------size------------"+temp.fileSize());
                return temp;

        }
	   // Array of {policyNo,firstname,lastname,idNo,caseID,remark,userID}

	public static String  cancelInsolvent(String [] data) throws Exception
	{
		PublicRte.setRemote(remote);
                Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteCancelInsolvent",new Object[] {data});
                System.out.println("result of RteCancelInsolvent -------"+res.status());
                if(res.status() != 0)
                        throw new Exception((String)res.value());
		return ((String)res.value());
	}
	public static void approveInsolvent(String [] data) throws Exception
	{
		PublicRte.setRemote(remote);
                Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteApproveInsolvent",new Object[] {data});
                System.out.println("result of RteApproveInsolvent -------"+res.status());
                if(res.status() != 0)
                        throw new Exception((String)res.value());
	}
	public static Vector  equalInsolvent(String sel,String keySearch1,String keySearch2) throws Exception
        {
		PublicRte.setRemote(remote);
                Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteEqualInsolvent",new Object[] {sel,keySearch1,keySearch2});
                System.out.println("result of RteEqualInsolvent -------"+res.status());
                if(res.status() != 0)
                        throw new Exception((String)res.value());
		return (Vector)res.value();
        }
	public static Vector  anotherInsolvent(String sel,String keySearch1,String keySearch2) throws Exception
        {
		PublicRte.setRemote(remote);
                Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteCheckAnother",new Object[] {sel,keySearch1,keySearch2});
                System.out.println("result of RteCheckAnother -------"+res.status());
                if(res.status() != 0)
                        throw new Exception((String)res.value());
		return (Vector)res.value();
        }
	public static TempMasicFile getStatisticInsolvent(String year) throws Exception
        {
		String [] field = {"month","notApprove","approve"};
                int [] len = {6,7,7};
                TempMasicFile temp  = new TempMasicFile("serviceapp", field, len);
                temp.keyField(false, false, new String [] {"month"});
                temp.buildTemp();

                String strRemote = "false";
                if(remote)
                        strRemote = "true";
                LoadFile.executeFileMaker(strRemote,"rte.bl.service.nstatus.SumInsolventByMonthByFile",new String [] {year},temp);
		if(temp.fileSize() == 0)
			throw new Exception(M.stou("ไม่พบข้อมูลตามเงื่อนไขที่ระบุ"));
		boolean found = false ;
		for (boolean st = temp.first();st;st=temp.next())
		{
			if(M.ctoi(temp.get("notApprove")) > 0 || M.ctoi(temp.get("approve")) > 0)
			{
				found = true;
				break;
			}
			
		}
		if(!found)
			throw new Exception(M.stou("ไม่มีข้อมูล"));
		System.out.println("temp----------size------------"+temp.fileSize());
                return temp;
	}
	public static TempMasicFile getCancelInsolvent(String year) throws Exception
        {
		String [] field = {"month","notSamePerson","cancelFromCourt","other"};
                int [] len = {6,7,7,7};

                TempMasicFile temp  = new TempMasicFile("serviceapp", field, len);
                temp.keyField(false, false, new String [] {"month"});
                temp.buildTemp();

                String strRemote = "false";
                if(remote)
                        strRemote = "true";
                LoadFile.executeFileMaker(strRemote,"rte.bl.service.nstatus.SumCancelByMonth",new String [] {year},temp);
		if(temp.fileSize() == 0)
			throw new Exception(M.stou("ไม่พบข้อมูลตามเงื่อนไขที่ระบุ"));
		boolean found = false ;
		for (boolean st = temp.first();st;st=temp.next())
		{
			if(M.ctoi(temp.get("notSamePerson")) > 0 || M.ctoi(temp.get("cancelFromCourt")) > 0 || M.ctoi(temp.get("other")) > 0 )
			{
				found = true;
				break;
			}
			
		}
		if(!found)
			throw new Exception(M.stou("ไม่มีข้อมูล"));
		System.out.println("temp----------size------------"+temp.fileSize());
                return temp;
	}
	public static TempMasicFile AllNotApprove() throws Exception
        {

		String [] field = {"dataDate","idNo","policyNo","preName","firstName","lastName","birthDate","branch","idNo2","preName2","firstName2","lastName2","caseID","pstatus"};
                int [] len = {8,13,8,20,30,30,8,3,13,20,30,30,15,1};

                TempMasicFile temp  = new TempMasicFile("serviceapp", field, len);
                temp.keyField(true, true, new String [] {"dataDate","idNo"});
                temp.buildTemp();

                String strRemote = "false";
                if(remote)
                        strRemote = "true";
                LoadFile.executeFileMaker(strRemote,"rte.bl.service.nstatus.GetNotApproveInsolventByFile",new String [] {" "},temp);
		if(temp.fileSize() == 0)
			throw new Exception(M.stou("ไม่พบข้อมูลตามเงื่อนไขที่ระบุ"));
		System.out.println("temp----------size------------"+temp.fileSize());
                return temp;
	}
	public static TempMasicFile getPolicyStatusNFromOldSys(String policy) throws Exception
	{
		String [] field = {"typeOfPol","policyNo","preName","firstName","lastName","idNo","caseNo","insolventDate","policyStatus1","policyStatusDate1","policyStatus2","policyStatusDate2","oldPolicyStatus1","oldPolicyStatusDate1","oldPolicyStatus2","oldPolicyStatusDate2","personID","nameID"};
		int [] len = {1,8,20,30,30,13,15,8,1,8,1,8,1,8,1,8,13,13};
                TempMasicFile temp  = new TempMasicFile("serviceapp", field, len);
                temp.keyField(true, true, new String [] {"policyNo"});
                temp.buildTemp();

                String strRemote = "false";
                if(remote)
                        strRemote = "true";
                LoadFile.executeFileMaker(strRemote,"rte.bl.service.nstatus.GetOldSysPolicyStatusN",new String [] {policy},temp);
		if(temp.fileSize() == 0)
			throw new Exception(M.stou("ไม่พบข้อมูลตามเงื่อนไขที่ระบุ"));
		System.out.println("temp----------size------------"+temp.fileSize());
                return temp;

	}
	public static String  cancelInsolventFromOldSys(byte [] buffer,String [] field,int [] len,String [] chgdat) throws Exception
	{
		PublicRte.setRemote(remote);
                Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteCancelInsolventFromOld",new Object[] {buffer,field,len,chgdat});
                System.out.println("result of RteCancelInsolventFromOld -------"+res.status());
                if(res.status() != 0)
                        throw new Exception((String)res.value());
		return ((String)res.value());
			
	}
//      input :  Vector of String [] (prename,fiestname,lastname,idNo,caseID,caseDate,policyNo)

        public static Vector searchOtherPolicyToMarkN(Vector vdata) throws Exception
        {
		PublicRte.setRemote(remote);
                Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteHaveOtherPolicy",new Object[]{vdata});
                System.out.println("result of RteHaveOtherPolicy -------"+res.status());
                if(res.status() != 0)
                        throw new Exception((String)res.value());
		return ((Vector)res.value());
                
        }
	
}
