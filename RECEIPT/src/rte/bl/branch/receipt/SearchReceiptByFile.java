package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.DateInfo;
import  utility.cfile.CFile;
import  rte.bl.branch.*;
import  rte.*;
import utility.rteutility.*;
import java.util.Vector;
import  layout.receipt.*;
public class  SearchReceiptByFile  implements  InterfaceRpt
{
	public void makeReport(String [] args) throws Exception
        {
		System.out.println("args === "+args.length);
		String remote	= args[0];  // true or false 
		if(remote.compareTo("false") == 0)
                        PublicRte.setRemote(false);
                else
                        PublicRte.setRemote(true);
                String appserv = args[1];  // application server  <--> braxxxapp
                String filename = args[2]; // masic output stream  

		// branch status typeOfPolicy policyNo
		if(args[5].charAt(0) == 'O')
			getAllRp(args[3],args[4],"W",args[6]);		
		getAllRp(args[3],args[4],args[5],args[6]);		

		System.out.println("End startSearch process! ");
		rte.RteRpt.recToTemp(temptoprint, filename);
                System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
		temptoprint.close();
	}
	TempMasicFile  temptoprint;
	private void getAllRp(String branch,String status,String typeOfPol,String policyNo) throws Exception
	{	
		Layout lay = null;
		if(typeOfPol.charAt(0) =='I')
			lay = (new  Rirctrl()).layout();
		else if(typeOfPol.charAt(0) =='O')
			lay = (new  Rorctrl()).layout();
		if(typeOfPol.charAt(0) =='W')
			lay = (new  Rwrctrl()).layout();
		temptoprint = new TempMasicFile("rptbranch",lay);
		temptoprint.keyField(false,false,new String [] {"policyNo","payPeriod","rpNo"});
                temptoprint.buildTemp();

		Result res = PublicRte.getResult("searchreceipt","rte.search.receipt.SearchByPolicy", new String [] {typeOfPol,policyNo,status});
		System.out.println("@@@@@@ SearchReceiptByFile ***@@@@@@**** Result of rte.search.receipt.SearchByPolicy--->"+res.status());
		if(res.status() != 0)
                        throw new Exception((String)res.value());	
		Vector vec = (Vector)res.value();
		for(int i = 0 ; i < vec.size();i++)
		{
			byte [] buffer = (byte [])vec.elementAt(i);
			temptoprint.putBytes(buffer);
			temptoprint.insert();
		}
	}
}

