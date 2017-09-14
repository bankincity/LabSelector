package rte.bl.branch.receipt;
import manit.*;
import insure.*;
import utility.support.TextUtility;
class GetKBPeriod
{
	Mrecord mst=null;
	Mrecord guar=null;
	Mrecord rid = null;
	GetKBPeriod()
	{
		mst = Masic.opens("ordmast@mstpolicy");
		rid = Masic.opens("rider@mstpolicy");
		guar = Masic.opens("guardian@mstpolicy");
	}
	GetKBPeriod(String s)
	{	
                mst = Masic.opens("ordmast@mstpolicy");
                rid = Masic.opens("rider@mstpolicy");
                guar = Masic.opens("guardian@mstpolicy");

		for (boolean ok= mst.first(); ok; ok=mst.next())
		{
			if (mst.get("policyStatus2").charAt(0) == 'B')
			{
				System.out.println("pol = "+mst.get("policyNo")+ " "+ kbPeriod(mst.get("policyNo")));
			}
		}
	}
	String kbPeriod(String policyNo)
	{
		if (mst.equal(policyNo))
		{
			if (!guar.equal(policyNo))
				return(mst.get("payPeriod"));
			String rdType = TextUtility.fillString ( M.stou("คบ "), 3, false);
			System.out.println("rdType = "+rdType);
			if (rid.equalGreat(policyNo+M.stou("คบ")) && rdType.substring(0,2).compareTo(M.stou("คบ"))==0 && rid.get("riderStatus").charAt(0)=='B')
			{
				TLPlan tl = PlanSpec.getPlan( mst.get("planCode"));
				RiderPlan rdp = RiderSpec.riderType( rid.get("riderType"));
				if (rdp != null && tl != null)
				{
				     System.out.println("endowmentYear:"+mst.get("insuredAge")+" "+guar.get("parentAge"));
				     String kbYear = rdp.endowmentYear(mst.get("insuredAge"),guar.get("parentAge"), tl.payYear(mst.get("insuredAge")),mst.get("effectiveDate"),  mst.get("mode"));
				     System.out.println("kbYear:"+kbYear);
                                     String kbPD = M.setlen(kbYear,2)+ periodKb(mst.get("mode"));
				     if (M.cmps( mst.get("payPeriod"), kbPD) < 0)
						return(kbPD);
				}
			}
			return(mst.get("payPeriod"));
		}
		return(null);
	}
	String periodKb(String mode)
	{
		String[] st = {"12", "01", "02", "", "04"};
		return(st[M.ctoi(mode)]);
	}
	public static void main(String args[])
	{
		new GetKBPeriod("a");
	}
}
