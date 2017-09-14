package rte.bl.branch.receipt.help;
import manit.*;
import java.io.*;
import utility.cfile.*;
import insure.*;
import java.util.*;
import rte.bl.receipt.*;
public class HelpCheckRpRider
{
        public static void main(String [] args) throws Exception
        {
                Mrecord orps = CFile.opens("orpserved255505@cbranch");
		Mrecord crprider = CFile.opens("rprider@cbranch");
		for (boolean st = orps.first();st;st=orps.next())
		{
			if (crprider.equalGreat(orps.get("policyNo")+orps.get("payPeriod")) && crprider.get("rpNo").compareTo(orps.get("policyNo")+orps.get("payPeriod")) == 0)
			{
				Vector vec = new Vector();
				for (boolean ss = crprider.equalGreat(orps.get("policyNo")+orps.get("payPeriod")) ;ss && crprider.get("rpNo").compareTo(orps.get("policyNo")+orps.get("payPeriod")) == 0 ;ss =crprider.next())
				{
					vec.addElement(crprider.get("riderText"));
				}
				String [] pp = rpriderToPremium(vec);
				if (M.cmps(pp[0],orps.get("premium")) != 0)
					System.out.println("Not Equal premium  "+orps.get("policyNo")+orps.get("payPeriod")+"  "+orps.get("rpNo")+" "+pp[0]+orps.get("premium")); 
					
			}
			else
				System.out.println("Not Found crprider  "+orps.get("policyNo")+orps.get("payPeriod")+"  "+orps.get("rpNo"));

		}
	}
	 public static String [] rpriderToPremium(Vector vec)
        {
                String premium = "0";
                String extraPremium = "0";
                for (int i = 0 ; i < vec.size();i++)
                {
                        String tstr = (String)vec.elementAt(i);
                        int  len = tstr.length();
                        System.out.println("len ---------------"+len);
                        int j = 0;
                        if(i == 0 )
                        {
                                premium = M.addnum(premium,tstr.substring(0,9));
                                extraPremium = M.addnum(extraPremium,tstr.substring(9,15));
                                j = 15;
                        }
                        System.out.println("-----j ="+j+" len ----"+len);
                        while (j < len )
                        {
                                premium = M.addnum(premium,tstr.substring(j+3,j+3+9));
                                extraPremium = M.addnum(extraPremium,tstr.substring(j+12,j+12+6));
                                System.out.println(premium+"--------------"+extraPremium);
                                j+=18;

                        System.out.println("j ="+j+" len ----"+len);
                                if(i > 0 && j == 108 )
                                        break;
                        }
                }
                premium = M.addnum(premium,extraPremium);
                System.out.println("premium: ---------------"+premium);
                return (new String [] {premium,extraPremium});
        }

}

