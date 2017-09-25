package rte.bl.collection.servicerequest;

import utility.branch.sales.BraSales;

public class SalesStructService {
	public String[] getSalesData(String salesID) throws Exception
	{
		BraSales bSales = new BraSales();
		bSales.getBySalesID(salesID);
		String firstName = bSales.getSnRec("firstName").trim();
		String lastName = bSales.getSnRec("lastName").trim();
		String name = firstName + " " + lastName;
		String position = bSales.getSnRec("positionName").trim();
		String licenseNo = bSales.getSnRec("licenseNo").trim();
		String licenseDate = bSales.getSnRec("licenseExpireDate").trim();
		String status = bSales.getSnRec("status").trim();
		String strID = bSales.getSnRec("strID").trim();
		
		
		return  new String[] {name , position, licenseNo , licenseDate , status , strID};
		
	}

}
