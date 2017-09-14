package rte.bl.branch.receipt;
import manit.*;
import utility.cfile.*;
public class  ReceiptSaleStruct
{
        Mrecord saleincome;
        Mrecord salestruct;
        Mrecord person;
        public ReceiptSaleStruct() throws Exception
        {
                salestruct = CFile.opens("struct@sales");
                person = CFile.opens("person@sales");
                person.start(2);
                               
        }
        public  String getSai(String saleID)
        {
                person.start(0);
                if (person.equal(saleID))
                {
                        String strID = person.get("highStrid");
                        while (true)
                        {
                                if (salestruct.equal(strID))
                                {
                                        strID  = salestruct.get("parentStrid");
                                
                                       if (salestruct.get("parentStrid").charAt(0) == 'N')
                                       {
                                              return strID;
                                       }
                                }
                                else
                                        return "00000";
                                  
                        }
                }
                return "00000";
              
        }
        public boolean isThisSai(String branch,String depNo,String sai)
        {
                String tsai = "00000";
                if (sai.compareTo(Receipt.sai6) == 0)
                    tsai = Receipt.sai3;    
                if (person.equal(branch+depNo))
                {
                        String strID = person.get("highStrid");
                        while (true)
                        {
                                if (salestruct.equal(strID))
                                {
                                        strID  = salestruct.get("parentStrid");
                                
                                       if (salestruct.get("parentStrid").charAt(0) == 'N')
                                       {
                                                if (strID.substring(0,4).compareTo(sai.substring(0,4)) == 0 || strID.substring(0,4).compareTo(tsai.substring(0,4)) == 0)
                                                        return true;
                                                
                                                break;
                                       }
                                }
                                else 
                                       break;
                        }
                }
                return false ;
        }
        public boolean isThisFai(String branch,String depNo,String fai)
        {
                if (person.equal(branch+depNo))
                {
                        String strID = person.get("highStrid");
                        while (true)
                        {
                                if (salestruct.equal(strID))
                                {
                                        strID  = salestruct.get("parentStrid");
                                
                                       if (salestruct.get("parentStrid").charAt(0) == 'L')
                                       {
                                                if (strID.substring(0,4).compareTo(fai.substring(0,4)) == 0)
                                                        return true;
                                                
                                                break;
                                       }
                                }
                                else 
                                       break;
                        }
                }
                return false ;
        }
        public static void main(String [] args) throws Exception
        {
                ReceiptSaleStruct rss = new ReceiptSaleStruct ();
                System.out.println(args[0]+ "  "+args[1]+" in  "+args[2]+" "+rss.isThisSai(args[0],args[1],args[2]));
        }
}
