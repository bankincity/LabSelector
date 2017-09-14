package rte.bl.branch.receipt;
import  manit.*;
import  manit.rte.*;
import  utility.support.MyVector;
import  utility.support.DateInfo;
import  utility.cfile.CFile;
import  utility.cfile.Rtemp;
import  rte.bl.branch.*;
import  rte.*;
import utility.rteutility.*;
public class  GetStockDocumentByFile  implements  InterfaceRpt
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

		// typeOfDocument  ,flagStock [sendStockDate]
		if(args.length == 6)
			getAllStock(args[3],args[4],args[5],"00000000");		
		else if (args.length == 7)
			getAllStock(args[3],args[4],args[5],args[6]);		

		System.out.println("in GetStockDocument temp--------->"+temp.fileSize());

		rte.RteRpt.recToTemp(temp, filename);
		temp.close();
                System.out.println("write temp --> complete");
		rte.RteRpt.insertReportStatus(appserv, filename, 1);
	}
	TempMasicFile  temp;
	Mrecord doc;
	Mrecord doctype ;
	private void getAllStock(String departCode,String typeOfDocument,String flagStock,String sendStockDate) throws Exception
	{
		String [] field =  {"docCode","receiveDate","startNo","lastNo","sendDate","docType","currentNo"};
                int []  len = {2,8,12,12,8,50,12};
		temp = new TempMasicFile ("rptbranch",field,len);
                temp.keyField(false,false,new String [] {"docCode","startNo","sendDate"});
                temp.buildTemp();
		doc = CFile.opens("stockdepart@insuredocument");
		doctype = CFile.opens("document@insuredocument");
		if(departCode.length() == 3)
			departCode+=" ";
		if(flagStock.compareTo("in") == 0)
			flagStock = "R";	
		else if(flagStock.compareTo("out") == 0)
			flagStock = "S";	
		else if(flagStock.compareTo("used") == 0)
			flagStock ="C";
		else if(flagStock.compareTo("all") == 0)
			flagStock ="CSR";
		for (int i = 0 ; i < flagStock.length();i++)
		{
			getStock(flagStock.substring(i,i+1),departCode,typeOfDocument);
		}
	}
	private void getStock(String flagStock,String departCode,String typeOfDocument)
	{
		for (boolean st = doc.equalGreat(flagStock+departCode+typeOfDocument);st;st=doc.next())
		{
			if( flagStock.charAt(0) != doc.get("statusStock").charAt(0))
				break;
			if(departCode.trim().compareTo(doc.get("deptCode").trim()) != 0)
				break;
			System.out.println("in GetStock  ........typeOfDocument......"+typeOfDocument);
			if(typeOfDocument.compareTo("00") != 0 && typeOfDocument.compareTo(doc.get("docCode")) != 0)
				break;
			temp.set(' ');
			temp.set("docCode",doc.get("docCode"));
			temp.set("receiveDate",doc.get("receiveDate"));
			temp.set("startNo",doc.get("startNo"));
			if(temp.get("startNo").compareTo("000000000000") == 0)
				temp.set("lastNo",doc.get("currentNo"));
			else	
				temp.set("lastNo",doc.get("lastNo"));
			temp.set("currentNo",doc.get("currentNo"));
			temp.set("sendDate",doc.get("sendDate"));
			temp.set("docType",getDocType(doc.get("docCode")));
			temp.insert();
		}
	}
	private String getDocType(String docCode)
	{
		if(doctype.equal(docCode))
			return doctype.get("docName");
		return "";
	}
}

