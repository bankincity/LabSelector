package rte.bl.branch.receipt;

import manit.*;
import manit.rte.*;
import manit.rte.Task.*;
import utility.cfile.*;
import utility.support.*;
import java.util.*;
import rte.bl.branch.*;
import utility.branch.sales.*;
import utility.branch.sales.BraSales;

public class RteCancelRp implements Task {
	Mrecord fxrp;
	Mrecord firc;
	Mrecord forc;
	Mrecord fwrc;
	Mrecord ful;
	Mrecord fulip;
	Mrecord ftrp;
	BraSales bs;
	String branch;
	String yyyymmdd;
	String userID;
	TempMasicFile temp;
	int[] len = { 7, 2, 12, 1, 1 };
	String[] field = { "userID", "docCode", "rpNo", "status", "oldStatus" };

	public Result execute(Object param) {
		if (!(param instanceof Object[]))
			return new Result(
					"Invalid Parameter  : Object [] {yyyymmdd,userID}", -1);
		Object[] parameter = (Object[]) param;
		yyyymmdd = (String) parameter[0];
		userID = (String) parameter[1];
		branch = (String) parameter[2];
		try {
			fxrp = CFile.opens("xrpchg@cbranch");
			firc = CFile.opens("irctrl@receipt");
			forc = CFile.opens("orctrl@receipt");
			fwrc = CFile.opens("wrctrl@receipt");
			ftrp = CFile.opens("trpctrl@receipt");
			if (Masic.fileStatus("ulrctrl@universal") >= 2)
				ful = CFile.opens("ulrctrl@universal");
			if (Masic.fileStatus("uliprctrl@unitlink") >= 2)
				fulip = CFile.opens("uliprctrl@unitlink");
			bs = new BraSales();
			temp = new TempMasicFile("rptbranch", field, len);
			temp.keyField(false, false, new String[] { "userID", "docCode",
					"rpNo", "status", "oldStatus" });
			temp.buildTemp();
			if (userID.compareTo("0000000") == 0) {
				System.out.println("branch  from tick === " + branch);
				Vector vsale = bs.getEmployee(branch);
				for (int i = 0; i < vsale.size(); i++) {
					String[] str = (String[]) vsale.elementAt(i);
					if (yyyymmdd.compareTo(str[14]) > 0
							&& !M.itis(str[14], '0'))
						continue;

					userID = str[0];
					System.out.println("USEID" + userID);
					// Thread.sleep(500);
					searchData(yyyymmdd, userID);
				}
			} else {
				searchData(yyyymmdd, userID);
			}
			return new Result(temp.name(), 0);
		} catch (Exception e) {
			return new Result(e.getMessage(), 2);
		}
	}

	/*----------------------------------------------------------------------*/
	private void searchData(String yyyymmdd, String userID) throws Exception {
		String key = yyyymmdd + userID;
		System.out.println(" key : " + key);
		for (boolean ok = fxrp.equalGreat(key); ok; ok = fxrp.next()) {

			if (key.compareTo(fxrp.get("sysDate") + fxrp.get("userID")) != 0)
				break;

			if ("CRZYG".indexOf(fxrp.get("newStatus")) >= 0) {
				System.out.println( fxrp.get("newStatus") + ":" + fxrp.get("rpNo")); 
				switch (fxrp.get("typeOfReceipt").charAt(0)) {				
				case 'I':
					if (firc.equal(fxrp.get("rpNo"))) {
						if (fxrp.get("newStatus").charAt(0) == firc.get(
								"currentStatus").charAt(0)) {
							temp.set("userID", fxrp.get("userID"));
							// temp.set("userID","0000000");
							temp.set("docCode", "01");
							temp.set("rpNo", fxrp.get("rpNo"));
							temp.set("status", fxrp.get("newStatus"));
							temp.set("oldStatus", fxrp.get("oldStatus"));
							temp.insert();
						}
					}
					break;
				case 'O':
					if (forc.equal(fxrp.get("rpNo"))) {
						if (fxrp.get("newStatus").charAt(0) == forc.get(
								"currentStatus").charAt(0)) {
							/*
							 * if(fxrp.get("rpNo").compareTo("007004837360") ==
							 * 0) {
							 * System.out.println("---OO--RTE-L3-"+fxrp.get(
							 * "rpNo")); break; }
							 */
							temp.set("userID", fxrp.get("userID"));
							// temp.set("userID","0000000");
							temp.set("docCode", "02");
							temp.set("rpNo", fxrp.get("rpNo"));
							temp.set("status", fxrp.get("newStatus"));
							temp.set("oldStatus", fxrp.get("oldStatus"));
							temp.insert();
						}
					} else if (fwrc.equal(fxrp.get("rpNo"))) {
						if (fxrp.get("newStatus").charAt(0) == fwrc.get(
								"currentStatus").charAt(0)) {
							temp.set("userID", fxrp.get("userID"));
							// temp.set("userID","0000000");
							temp.set("docCode", "02");
							temp.set("rpNo", fxrp.get("rpNo"));
							temp.set("status", fxrp.get("newStatus"));
							temp.set("oldStatus", fxrp.get("oldStatus"));
							temp.insert();
						}
					}
					break;
				case 'W':
					if (fwrc.equal(fxrp.get("rpNo"))) {
						if (fxrp.get("newStatus").charAt(0) == fwrc.get(
								"currentStatus").charAt(0)) {
							temp.set("userID", fxrp.get("userID"));
							// temp.set("userID","0000000");
							temp.set("docCode", "02");
							temp.set("rpNo", fxrp.get("rpNo"));
							temp.set("status", fxrp.get("newStatus"));
							temp.set("oldStatus", fxrp.get("oldStatus"));
							temp.insert();
						}
					}
					break;
				case 'U':
					if (ful.equal(fxrp.get("rpNo"))) {
						if (fxrp.get("newStatus").charAt(0) == ful.get(
								"currentStatus").charAt(0)) {
							temp.set("userID", fxrp.get("userID"));
							// temp.set("userID","0000000");
							temp.set("docCode", "05");
							temp.set("rpNo", fxrp.get("rpNo"));
							temp.set("status", fxrp.get("newStatus"));
							temp.set("oldStatus", fxrp.get("oldStatus"));
							temp.insert();
						}
					}
					break;
				case 'T':
					if (ftrp.equal(fxrp.get("rpNo"))) {
						if (fxrp.get("newStatus").charAt(0) == ftrp.get(
								"currentStatus").charAt(0)) {
							temp.set("userID", fxrp.get("userID"));
							// temp.set("userID","0000000");
							temp.set("docCode", "04");
							temp.set("rpNo", fxrp.get("rpNo"));
							temp.set("status", fxrp.get("newStatus"));
							temp.set("oldStatus", fxrp.get("oldStatus"));
							temp.insert();
						}
					}
					break;
				case 'L':
					if (fulip.equal(fxrp.get("rpNo"))) {
						if (fxrp.get("newStatus").charAt(0) == fulip.get("currentStatus").charAt(0)) {
							temp.set("userID", fxrp.get("userID"));
							temp.set("docCode", "06");// Unitlink
							temp.set("rpNo", fxrp.get("rpNo"));
							temp.set("status", fxrp.get("newStatus"));
							temp.set("oldStatus", fxrp.get("oldStatus"));
							temp.insert();
						}
					}
					break;
				}
			}
		}
	}
}
