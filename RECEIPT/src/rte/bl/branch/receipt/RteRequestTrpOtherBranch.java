package rte.bl.branch.receipt;

import java.util.ArrayList;
import java.util.Vector;

import layout.bean.cbranch.AskyyyymmBean;
import layout.bean.cbranch.AskyyyymmRecord;
import layout.bean.cbranch.BaddepBean;
import layout.bean.cbranch.BaddepRecord;
import layout.bean.cbranch.UcrpYYYYMMBean;
import layout.bean.cbranch.UcrpYYYYMMRecord;
import layout.bean.mstperson.PersonBean;
import manit.*;
import manit.rte.*;
import rte.bl.dataaccess.PrimaryKey;
import rte.bl.dataaccess.search.ByKey;
import rte.bl.dataaccess.search.Condition;
import rte.bl.dataaccess.search.ConditionEqual;
import rte.bl.dataaccess.search.DTable;
import rte.bl.dataaccess.search.Joint;
import rte.bl.dataaccess.search.SelectorJointOneToOneMasic;
import rte.bl.dataaccess.search.Where;
import rte.bl.dataaccess.cbranch.AskyyyymmDAO;
import rte.bl.dataaccess.cbranch.BaddepDAO;
import rte.bl.dataaccess.cbranch.UcrpYYYYMMDAO;
import rte.bl.collection.datarequest.SalesStructData;
import rte.bl.collection.servicerequest.SalesStructService;
import utility.cfile.*;
import utility.support.*;
import utility.rteutility.*;
import utility.branch.sales.BraSales;

public class RteRequestTrpOtherBranch implements TaskUsr {
	String citizenID, salesID, firstName, lastName, date, month, year, rpNo;
	String name, position, status, strID, licenseNo, licenseDate, isFound = "No", isBadDep = "No";
	String currentFullDate = DateInfo.sysDate();
	//Mrecord mPerson, badDep, askYM, ask, trpCtrl;

	Record mPerson;
	Vector vSaleDetail, vReceiptDetail;

	BraSales braSales = new BraSales();
	UserInfo user;

	public Result execute(UserInfo user, Object obj) {
		System.out.println("RteRequestTrpOtherBranch()");
		this.user = user;
		try {
			Object[] arrData = (Object[]) obj;
			String key = (String) arrData[0];
			if (key.equals("sales")) {
				String ownerBranch = (String) arrData[1];
				String depositNo = (String) arrData[2];
				return new Result(getSalesDetail(ownerBranch, depositNo), 0);
			} else if (key.equals("receipt")) {
				String ownerBranch = (String) arrData[1];
				String salesID = (String) arrData[2];
				return new Result(getReceiptDetail(ownerBranch, salesID), 0);
			}
		} catch (Exception e) {
			return new Result(e.getMessage(), 1);
		}
		return new Result("", 0);
	}

	Vector getSalesDetail(String ownerBranch, String depositNo) throws Exception {
		System.out.println("getSalesDetail()----------------------");
		String[] arrSalesDetail;
		vSaleDetail = new Vector();

		//mPerson = CFile.opens("person@sales");
		SalesStructData sales = new SalesStructData();
		Record mPerson = sales.getSalesPersonByBranchDepositNo(user, ownerBranch, depositNo);
		salesID = mPerson.get("salesID");
		citizenID = mPerson.get("citizenID");
		
		SalesStructService sService = new SalesStructService();
		String [] sdata = sService.getSalesData(salesID);
		name = sdata[0];
		position = sdata[1];
		licenseNo = sdata[2];
		licenseDate = sdata[3];
		status = sdata[4];
		strID = sdata[5];
		PrimaryKey pk;
		boolean askok = false;
		Record askRec = new Record();
		Record baddepRec = new Record();
		if (currentFullDate.substring(6, 8).compareTo("16") > 0)
		{
			try
			{
				UcrpYYYYMMDAO ucrp = new UcrpYYYYMMDAO(user, DateInfo.previousMonth(DateInfo.sysDate().substring(0, 6)));
				UcrpYYYYMMBean ucrpBean;
				pk = new PrimaryKey();
				pk.addKey("branch", ownerBranch);
				pk.addKey("salesID", salesID);
				ucrpBean = (UcrpYYYYMMBean) ucrp.equal(pk);
				askRec = UcrpYYYYMMRecord.getRecord(ucrpBean);
				askok = true;
			}
			catch(Exception eu)
			{
				askok = false;
			}
		}
		if ( !askok )
		{
			AskyyyymmDAO ask = new AskyyyymmDAO(user);
			AskyyyymmBean askBean;
			pk = new PrimaryKey();
			pk.addKey("branch", ownerBranch);
			pk.addKey("askSalesID", salesID);
			askBean = (AskyyyymmBean) ask.equal(pk);
			askRec = AskyyyymmRecord.getRecord(askBean);
			askok = true;
		}
		isFound = askok ? "Yes" : "No";
		try
		{
			BaddepDAO baddep = new BaddepDAO(user);
			BaddepBean baddepBean;
			pk = new PrimaryKey();
			pk.addKey("branch", ownerBranch);
			pk.addKey("depositNo", depositNo);
			baddepBean = (BaddepBean) baddep.equal(pk);
			baddepRec = BaddepRecord.getRecord(baddepBean);
			isBadDep = "Yes";
		}
		catch (Exception eb)
		{
			isBadDep = "No";
		}
		
		/*mPerson.start(2);
		if (mPerson.equal(ownerBranch + depositNo)) {

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
			if (currentFullDate.substring(6, 8).compareTo("16") <= 0)
				fileName = "askyyyymm@cbranch";
			else
				fileName = "ucrp" + DateInfo.previousMonth(DateInfo.sysDate().substring(0, 6)) + "@cbranch";
			if (!CFile.isFileExist(fileName)) {
				// throw new Exception(M.stou("ไม่พบแฟ้ม ") + fileName);
				fileName = "askyyyymm@cbranch";
			}

			askYM = CFile.opens(fileName);
			if (askYM.equal(ownerBranch + salesID))
				isFound = "Yes";

			badDep = CFile.opens("baddep@cbranch");
			if (badDep.equal(ownerBranch + depositNo))
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
		}*/
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
		
		return vSaleDetail;
	}

	Vector getReceiptDetail(String ownerBranch, String salesID) throws Exception {
		System.out.println("getReceiptDetail()--------------------");
		String[] arrTmp;
		int i = 1;
		vReceiptDetail = new Vector();
		year = currentFullDate.substring(0, 4);
		month = currentFullDate.substring(4, 6);
		date = currentFullDate.substring(6, 8);

		if (date.compareTo("16") > 0) {
			if (month.equals("12")) {
				month = "01";
				year = M.addnum(year, "1");
			} else
				month = M.setlen(M.addnum(month, "1"), 2);
		}
		System.out.println("askyyyymm ===> :" + "ask" + year + month);
		/*ask = Masic.opens("ask" + year + month + "@cbranch");
		trpCtrl = Masic.opens("trpctrl@receipt");
		for (boolean b = ask.equal(ownerBranch + salesID); b
				&& (ownerBranch + salesID).equals(ask.get("branch") + ask.get("askSaleID")); b = ask.next()) {
			if (ask.get("receiptFlag").equals("T")) {
				rpNo = ask.get("rpNo");
				if (trpCtrl.equal(rpNo)) {
					if (trpCtrl.get("currentStatus").equals("N")) {
						arrTmp = new String[4];
						arrTmp[0] = trpCtrl.get("rpNo");
						System.out.println("0-----rpNo Status =  N : " + arrTmp[0]);
						arrTmp[1] = trpCtrl.get("requestDate");
						System.out.println("1-----requestDate =   " + arrTmp[1]);
						arrTmp[2] = Branch.getBranchName(trpCtrl.get("branch"));
						System.out.println("2-----branch =   " + arrTmp[2]);
						arrTmp[3] = "1";
						System.out
								.println("arrTmp = " + arrTmp[0] + " " + arrTmp[1] + " " + arrTmp[2] + " " + arrTmp[3]);
						vReceiptDetail.add(arrTmp);
					}
				}
			}
		}*/
		// selector 1-1 left joint
		SelectorJointOneToOneMasic s = new SelectorJointOneToOneMasic();
		//Condition[] cond1 = new Condition[] { new ConditionIndexOf(new ByKey(“policyStatus1”),”IJALR”) };
		Condition[] cond1 = new Condition[] { new ConditionEqual(new ByKey("receiptFlag", "T")) };
		
		ArrayList<DTable> fromTable = new ArrayList<DTable>();
		DTable t1 = new DTable();
		t1.setTableName("ask"+year+month);
		t1.setSchema("cbranch");
		t1.setOutputField(new String [] {"rpNo"});
		t1.setCondition( cond1 );
		
		DTable t2 = new DTable();
		t2.setTableName("trpctrl");
		t2.setSchema("receipt");
		t2.setOutputField(new String [] {"requestDate","branch"});
		
		fromTable.add(t1);
		fromTable.add(t2);
		
		ByKey k1 = new ByKey();
		k1.addKey("branch", ownerBranch);
		k1.addKey("askSalesID", salesID);
		k1.setKeyNumber(0);
		
		s.setFromTable(fromTable);
		s.setWhere(new Where(k1,k1));
		
		ByKey j1 = new ByKey();
		j1.addKey("rpNo", "");
		
		ArrayList <Joint> joint = new ArrayList <Joint>();
		Joint jtable1 = new Joint(j1,j1);
		joint.add(jtable1);
		s.setJoint(joint);
		
		XTempList out = s.execute();
		out.addKey(new String []{"rpNo"});
		ArrayList<String> field = out.getDefaultFields();
		for (boolean st = out.first();st;st=out.next())
		{
			arrTmp = new String[4];
			arrTmp[0] = out.get(field.get(0));
			arrTmp[1] = out.get(field.get(1));
			arrTmp[2] = Branch.getBranchName(out.get(field.get(2)));
			arrTmp[3] = "1";
			System.out.println("arrTmp = " + arrTmp[0] + " " + arrTmp[1] + " " + arrTmp[2] + " " + arrTmp[3]);
			vReceiptDetail.add(arrTmp);
		}
		
		if (vReceiptDetail.isEmpty() || vReceiptDetail.size() == 1)
			return vReceiptDetail;
		else
			return sortReceipt(vReceiptDetail);
	}

	Vector sortReceipt(Vector vReceiptDetail) throws Exception {
		int countAll = 1;
		int count = 1;
		Vector vReceipt = new Vector();
		String[] tmpArr = (String[]) vReceiptDetail.elementAt(0);
		String tmpRpNo = tmpArr[0];
		String tmpDate = tmpArr[1];
		String tmpBranch = tmpArr[2];

		String rpNo = tmpRpNo;
		String rpNoLast = "";
		for (int i = 1; i < vReceiptDetail.size(); i++) {
			// System.out.println("*****************i ==> " + i);
			countAll++;
			String[] tmpB = (String[]) vReceiptDetail.elementAt(i);
			rpNo = M.setlen(M.addnum(rpNo, "1"), 12);
			if (rpNo.equals(tmpB[0]) && tmpDate.equals(tmpB[1]) && tmpBranch.equals(tmpB[2])) {
				count++;
				System.out.println("-------------------------------if : " + i);
				rpNoLast = tmpB[0];
				if (vReceiptDetail.size() - i == 1) {
					System.out.println("last if");
					vReceipt.add(new String[] { tmpRpNo + " - " + rpNoLast, tmpDate, tmpBranch, M.itoc(count) });
				}

			} else {
				System.out.println("-------------------------------else : " + i);
				// System.out.println("---------------count2 : " + count);
				if (count == 1) {
					vReceipt.add(new String[] { tmpRpNo, tmpDate, tmpBranch, M.itoc(count) });
				} else if (count > 1) {
					vReceipt.add(new String[] { tmpRpNo + " - " + rpNoLast, tmpDate, tmpBranch, M.itoc(count) });
				}
				// System.out.println("----------------------------------------count : " +
				// count);
				count = 1;
				tmpRpNo = tmpB[0];
				tmpDate = tmpB[1];
				tmpBranch = tmpB[2];
				rpNo = tmpRpNo;
				if (vReceiptDetail.size() - i == 1) {
					System.out.println("last else");
					vReceipt.add(new String[] { tmpRpNo, tmpDate, tmpBranch, M.itoc(count) });
				}

			}
		}
		System.out.println("-------------- size = " + vReceiptDetail.size());
		System.out.println("-------------- CountAll Qty : " + countAll);
		return vReceipt;
	}
}
