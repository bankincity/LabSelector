package service.nstatus.help;
import manit.*;
import java.io.*;
import utility.cfile.*;
public class ReadFromOld
{
	public static void main(String [] args) throws Exception
	{
		RandomAccessFile  frand = new RandomAccessFile("insolvent9999.csv","rw");
		Mrecord m = CFile.opens("bankruptcy@mstperson");
		for (boolean st  = m.first();st;st=m.next())
		{
			String strbuff = "";
			int idx = m.get("preName").indexOf(",");
			if(idx > 0)
				strbuff = m.get("preName").substring(0,idx)+"/"+m.get("preName").substring(idx+1);
			else
				strbuff = m.get("preName");
			strbuff = strbuff+","+m.get("firstName")+","+m.get("lastName")+","+m.get("idNo")+","+m.get("decidedNo")+","+m.get("inforceDate");			
			strbuff+="\r\n";
                        frand.write(M.utos(strbuff));

		}

	}
}
