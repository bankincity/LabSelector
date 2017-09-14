package rte.bl.branch.receipt;
import java.util.Vector;
import manit.*;
import manit.rte.*;
import utility.cfile.*;
import utility.support.*;
import utility.rteutility.*;
import utility.branch.sales.BraSales;

public class RteRequestTrpOtherBranch implements Task
{
    String citizenID, salesID, firstName, lastName, date, month, year, rpNo;
    String name, position, status, strID, licenseNo, licenseDate, isFound = "No", isBadDep = "No";
    String currentFullDate = DateInfo.sysDate();
    Mrecord mPerson, badDep, askYM, ask, trpCtrl;

    Vector vSaleDetail, vReceiptDetail;

    BraSales braSales = new BraSales();
    public Result execute(Object obj)
    {
System.out.println("RteRequestTrpOtherBranch()");
        try
        {
            Object[] arrData = (Object[]) obj;
            String key = (String) arrData[0];
            if(key.equals("sales"))
            {
                String ownerBranch =(String) arrData[1];
                String depositNo = (String) arrData[2];
                return new Result(getSalesDetail(ownerBranch, depositNo), 0);
            }
            else if(key.equals("receipt"))
            {
                String ownerBranch =(String) arrData[1];
                String salesID =(String) arrData[2];
                return new Result(getReceiptDetail(ownerBranch, salesID), 0);
            }
        }
        catch (Exception e)
        {
            return new Result(e.getMessage(), 1);
        }
        return new Result("", 0);
    }
    Vector getSalesDetail(String ownerBranch, String depositNo) throws Exception
    {
System.out.println("getSalesDetail()----------------------");
        String[] arrSalesDetail;
        vSaleDetail = new Vector();

        mPerson = CFile.opens("person@sales");
        mPerson.start(2);
        if(mPerson.equal(ownerBranch + depositNo))
        {
            salesID = mPerson.get("salesID");
            citizenID = mPerson.get("citizenID");
          
            braSales.getBySalesID(salesID);
            firstName = braSales.getSnRec("firstName").trim();
            lastName = braSales.getSnRec("lastName").trim();
            name = firstName + " " + lastName;

            position = braSales.getSnRec("positionName").trim();
            licenseNo = braSales.getSnRec("licenseNo").trim();
            licenseDate = braSales.getSnRec("licenseExpireDate").trim();
            status = braSales.getSnRec("status").trim();
            strID = braSales.getSnRec("strID").trim();
System.out.println("------------- status : " + status);
System.out.println("------------- strID : " + strID);

            String fileName;
            if(currentFullDate.substring(6, 8).compareTo("16") <= 0)  
                fileName = "askyyyymm@cbranch";
            else
                fileName = "ucrp" + DateInfo.previousMonth(DateInfo.sysDate().substring(0, 6))+ "@cbranch";
            if( !CFile.isFileExist(fileName) )
            {
             // throw new Exception(M.stou("ไม่พบแฟ้ม ") + fileName);
                fileName = "askyyyymm@cbranch";
            }

            askYM = CFile.opens(fileName);
            if(askYM.equal(ownerBranch + salesID))
                isFound = "Yes";

            badDep = CFile.opens("baddep@cbranch");
            if(badDep.equal(ownerBranch + depositNo))
                isBadDep = "Yes";

            arrSalesDetail = new String[9];
            arrSalesDetail[0] = name;
            arrSalesDetail[1] = position;
            arrSalesDetail[2] = licenseNo;
            arrSalesDetail[3] = licenseDate;
            arrSalesDetail[4] = isFound;
            arrSalesDetail[5] = isBadDep;
            arrSalesDetail[6] = salesID;
            arrSalesDetail[7] = status;
            arrSalesDetail[8] = strID;
            vSaleDetail.add(arrSalesDetail);
        }
        return vSaleDetail;
    }
    Vector getReceiptDetail(String ownerBranch, String salesID) throws Exception
    {
System.out.println("getReceiptDetail()--------------------");
        String[] arrTmp;
        int i = 1;
        vReceiptDetail = new Vector();
        year  = currentFullDate.substring(0, 4);
        month = currentFullDate.substring(4, 6);
        date  = currentFullDate.substring(6, 8);

        if(date.compareTo("16") > 0)
        {
            if(month.equals("12"))
            {
                month = "01";
                year = M.addnum(year, "1");
            }
            else
                month = M.setlen(M.addnum(month, "1"), 2);
        }        
System.out.println("askyyyymm ===> :" + "ask" + year + month);
        ask = Masic.opens("ask" + year + month + "@cbranch");
        trpCtrl = Masic.opens("trpctrl@receipt");
        for(boolean b = ask.equal(ownerBranch + salesID); b && (ownerBranch + salesID).equals(ask.get("branch") + ask.get("askSaleID")); b = ask.next())
        {
            if(ask.get("receiptFlag").equals("T"))
            {
                rpNo = ask.get("rpNo");
                if(trpCtrl.equal(rpNo))
                {
                    if(trpCtrl.get("currentStatus").equals("N"))
                    {
                        arrTmp = new String[4];
                        arrTmp[0] = trpCtrl.get("rpNo");
System.out.println("0-----rpNo Status =  N : " + arrTmp[0]);
                        arrTmp[1] = trpCtrl.get("requestDate");
System.out.println("1-----requestDate =   " + arrTmp[1]);
                        arrTmp[2] = Branch.getBranchName(trpCtrl.get("branch"));
System.out.println("2-----branch =   " + arrTmp[2]);
                        arrTmp[3] = "1";
System.out.println("arrTmp = " + arrTmp[0] + " "+ arrTmp[1] + " " + arrTmp[2] + " " + arrTmp[3]);
                        vReceiptDetail.add(arrTmp);
                    }
                }
            }
        }
        if(vReceiptDetail.isEmpty() || vReceiptDetail.size() == 1)
            return vReceiptDetail;
        else
            return sortReceipt(vReceiptDetail);
    }
    Vector sortReceipt(Vector vReceiptDetail) throws Exception
    {
        int countAll = 1;
        int count = 1;
        Vector vReceipt = new Vector();
        String[] tmpArr = (String[]) vReceiptDetail.elementAt(0);
        String tmpRpNo = tmpArr[0];
        String tmpDate = tmpArr[1];
        String tmpBranch = tmpArr[2];

        String rpNo = tmpRpNo;
        String rpNoLast = "";
        for(int i = 1; i < vReceiptDetail.size(); i++)
        {
//System.out.println("*****************i ==> " + i);
countAll++;
            String[] tmpB = (String[]) vReceiptDetail.elementAt(i);
            rpNo = M.setlen(M.addnum(rpNo,"1"), 12);
            if(rpNo.equals(tmpB[0]) && tmpDate.equals(tmpB[1]) && tmpBranch.equals(tmpB[2]))
            {
                count++;
System.out.println("-------------------------------if : " + i);
                rpNoLast = tmpB[0];
                if(vReceiptDetail.size() - i == 1)
                {
System.out.println("last if");
                    vReceipt.add(new String[]{tmpRpNo + " - " + rpNoLast, tmpDate, tmpBranch, M.itoc(count)});
                }

            }
            else
            {
System.out.println("-------------------------------else : " + i);
//System.out.println("---------------count2 : " + count);
                if(count == 1)
                {
                    vReceipt.add(new String[]{tmpRpNo, tmpDate, tmpBranch, M.itoc(count)});
                }
                else if(count > 1)
                {
                    vReceipt.add(new String[]{tmpRpNo + " - " + rpNoLast, tmpDate, tmpBranch, M.itoc(count)});
                }
//System.out.println("----------------------------------------count : " + count);
                count = 1;
                tmpRpNo = tmpB[0];
                tmpDate = tmpB[1];
                tmpBranch = tmpB[2];
                rpNo = tmpRpNo;
                if(vReceiptDetail.size() - i == 1)
                {
System.out.println("last else");
                    vReceipt.add(new String[]{tmpRpNo, tmpDate, tmpBranch, M.itoc(count)});
                }

            }
        }
        System.out.println("-------------- size = " + vReceiptDetail.size());
        System.out.println("-------------- CountAll Qty : " + countAll);
        return vReceipt;
    }
}

