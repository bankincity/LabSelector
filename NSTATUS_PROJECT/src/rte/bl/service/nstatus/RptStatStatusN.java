package rte.bl.service.nstatus;
import manit.*;
import manit.rte.*;
import utility.cfile.*;
import utility.rteutility.*;
import rte.*;
import utility.support.DateInfo;
import rte.bl.service.nstatus.RteStartBackwardBalance;
public class RptStatStatusN implements rte.InterfaceRpt{
	String smonth,emonth;
	TempMasic tempFile;
        String  tempName = "",
                appserv = "",
                streamName = "";
	Mrecord stins;
	public void makeReport(String[] str) throws Exception{
		for (int m = 0; m < str.length; m++)
                        System.out.println("str ["+m+"] = " + str[m]);
		try{
			PublicRte.setRemote(str[0].compareTo("true") == 0);
                        appserv = str[1];
                        streamName = str[2];
                        if (str.length != 5)
                        {
                                RteRpt.insertReportStatus(appserv, streamName, 8);
                                return;
                        }
                        System.out.println("ReportStatusN -> " + appserv + " " + streamName + "args.length = " + str.length);
                        for (int i =0; i < str.length; i++)
                                System.out.print("i = " + i + " -> " + str[i] + " ");
			smonth = str[3];
			emonth = str[4];
			System.out.println( "start prepareData" );
			tempFile = prepareData();
			tempName = tempFile.name();
                        System.out.println( "prepare data finish" );
                        System.out.println("ReportSurPay tempFile.fileSize = " + tempFile.fileSize());
                        if (tempFile.fileSize() > 0)
                        {
                                RteRpt.recToTemp(tempFile.file(), streamName);
                                System.out.println("write record ----complete");
                                RteRpt.insertReportStatus(appserv, streamName, 1);
                                System.out.println("write status report ----complete");
                        }
                        else
                                RteRpt.insertReportStatus(appserv, streamName, 2 );			
		}catch(Exception e){
			System.out.println("test=============>" + e.getMessage());
			RteRpt.insertReportStatus(appserv, streamName, 9);
		}finally{
			if (tempFile != null)
                        {
                                Masic.purge(tempName);
                                Masic.remove(tempName);
                        }
		}
	}
	public void setData() throws Exception {
		String tmonth = "",b = "0",c = "0";
		RteStartBackwardBalance newData = new RteStartBackwardBalance();
		System.out.println("statinsolvent@srvservice size : "+stins.fileSize());
		if(stins.fileSize() < 1){
			tmonth = "255306";
			String a = newData.calA();
			b = newData.calB(tmonth);
			c = newData.calC(tmonth);
			stins.set("month",tmonth);
			stins.set("backwardBalance",a);
			stins.set("nstatus",b);
			stins.set("cancel",c);
			String start = M.addnum(a,b,0);
			start = M.subnum(start,c,0);
			stins.set("forwardBalance",start);
			stins.insert();
		}
		boolean have = false;
		have = stins.equal(emonth);
		System.out.println("Have....."+have);
		if(have){
			if(stins.last()){
				System.out.println(emonth+".....in file : "+stins.get("month"));
				if(!stins.get("month").equals(emonth))	
					return;
			}
			if(stins.delete())
				System.out.println("Delete Succuss");
			else
				System.out.println("Delete Fail");
		}
		System.out.println("get.....last : "+stins.last());
		String nextMonth = DateInfo.nextMonth(stins.get("month") + "01",1).substring(0,6);
		System.out.println("start.....nextMonth : "+nextMonth);
		while(nextMonth.compareTo(emonth) <= 0){
			b = newData.calB(nextMonth);
			c = newData.calC(nextMonth);
			stins.set("month",nextMonth);
                        stins.set("backwardBalance",stins.get("forwardBalance"));
                        stins.set("nstatus",b);
                        stins.set("cancel",c);
			String forwardBalance = M.addnum(stins.get("forwardBalance"),b,0);
                        forwardBalance = M.subnum(forwardBalance,c,0);
			stins.set("forwardBalance",forwardBalance);
			System.out.println("insert......"+nextMonth+" : "+stins.insert());
			System.out.println("get.....last : "+stins.last());
			nextMonth = DateInfo.nextMonth(nextMonth + "01",1).substring(0,6);
		}
	}
	public TempMasic prepareData() throws Exception{
		stins = CFile.opens("statinsolvent@srvservice");
		System.out.println("open file statinsolvent@srvservice success");
		setData();
		TempMasic temp = createTemp();
		for(boolean b = stins.equal(smonth);b && stins.get("month").compareTo(emonth) <= 0;b = stins.next()){
			temp.set("month",stins.get("month"));
			temp.set("backwardBalance",stins.get("backwardBalance"));
			temp.set("nstatus",stins.get("nstatus"));
			temp.set("cancel",stins.get("cancel"));
			temp.set("forwardBalance",stins.get("forwardBalance"));
			temp.insert();
		}
		stins.close();
		return temp;
	}
	public TempMasic createTemp(){
                String[] fieldName = null;
                int   [] fieldLength = null;
                String[] keyField = null;
                char  [] fieldType = null;
                int   [] fieldScale = null;
                fieldName   = new String[]{ "month","backwardBalance","nstatus","cancel","forwardBalance"};
                fieldLength = new int[]{6,7,7,7,7};
                keyField  = new String[] {"month"};
                fieldType = new char[] {'T','N','N','N','N'};
                fieldScale= new int[]{0,0,0,0};
                TempMasic temp = new TempMasic(fieldName, fieldType, fieldLength, fieldScale);
                temp.setNoOfKey(1);
                temp.setKey(0,keyField);
                temp.buildTemp("rptservice");
                return temp;
        }
}
