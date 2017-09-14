package rte.bl.branch.receipt.help;
//package branch.oper;
import manit.*;
import manit.rte.*;
import utility.rteutility.*;
import utility.support.*;
public class BuildAskyyyymm
{
	private boolean remote = false ;
	public BuildAskyyyymm(String yyyymm)
	{
		try {
			buildAskyyyymm(yyyymm);
		}
		catch (Exception e)
		{
			System.out.println("Error :"+e.getMessage());
		}
	}
	private void buildAskyyyymm(String yyyymm)throws Exception
	{
		LocalTask task = new LocalTask();
		System.out.println("Start Build ask....."+yyyymm+".........");
		Result res = task.exec("rte.bl.branch.receipt.RteBuildAskyyyymm",new Object [] {yyyymm});
		if(res.status() == 0)
		{
			System.out.println("Build ask.....success");
		}
		else
			throw new Exception((String)res.value());
	}
	public static void main (String [] args)
	{
		if(args.length == 1)
		{
			new BuildAskyyyymm(args[0]);
		}
		else {
			String  month = DateInfo.nextMonth(DateInfo.sysDate()).substring(0,6);
			new BuildAskyyyymm(month);
		}
		System.exit(0);
	}
}

