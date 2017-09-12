package service.nstatus;
import manit.*;
import manit.rte.*;
import java.awt.*;
import javax.swing.*;
import utility.screen.KeyInput;
import service.bucket.*;
import java.util.*;
import utility.screen.SCR;
import utility.service.*;
import java.awt.event.*;
import javax.swing.event.*;
import utility.rteutility.*;
public class DlgMaintainStatusN extends Mframe
{
/* $$$-0
B0009001100740033btSearchค้นหา
B0086001100740033btAddเพิ่ม
B0164001100740033btEditแก้ไข
B0240001100740033btDelลบ
B0023005900540031btPrev<
B0101005900540031btNext>
B0811001100740033btSaveบันทึก
B0890001100740033btCancelยกเลิก
L0017009901160035lbPolicyเลขที่กรมธรรม์
T0111010001060033tfPolicy
L0224010000800031lbNameชื่อ-สกุล
T0289010002680033tfName
L0559010100990032lbIDบัตรประชาชน
T0645010001950033tfID
L0845010100800034lbBranchสาขา
T0881009800670033tfBranch
L0647014800800033lbPlanแบบประกัน
L0018014500800034lbSumทุนประกัน
N0111014501040033nfSum0
L0221014600800034lbEffDateวันเริ่ม
D0288014501020033dfEffDate8
L0400014501010032lbMatureวันครบกำหนด
D0506014500990033dfMatureDate8
T0724014502240033tfPlan
L0008005109610139lbFrame
A0006020209610303tbTransactionT0F0096เลขที่คดีD8F0079วันที่ล้มละลายT0F0119สถานะD8F0096วันที่ปรับสถานะT0F0096ผู้ปรับสถานะT0F0096หมายเหตุT0F0096สถานะ 1D8F0094วันที่สถานะ 1T0F0094สถานะ 2D8F0093วันที่สถานะ 2
G0006007009880546
---- */
	JButton			btSearch;
	JButton			btAdd;
	JButton			btEdit;
	JButton			btDel;
	JButton			btPrev;
	JButton			btNext;
	JButton			btSave;
	JButton			btCancel;
	JLabel			lbPolicy;
	Tfield			tfPolicy;
	JLabel			lbName;
	Tfield			tfName;
	JLabel			lbID;
	Tfield			tfID;
	JLabel			lbBranch;
	Tfield			tfBranch;
	JLabel			lbPlan;
	JLabel			lbSum;
	Nfield			nfSum;
	JLabel			lbEffDate;
	Dfield			dfEffDate;
	JLabel			lbMature;
	Dfield			dfMatureDate;
	Tfield			tfPlan;
	JLabel			lbFrame;
	Mtable			tbTransaction;

	void panel_0()
	{
		btSearch = new JButton(M.stou("ค้นหา"));
		btSearch.setBounds(9, 11, 74, 33);
		addcom(btSearch, 0);

		btAdd = new JButton(M.stou("เพิ่ม"));
		btAdd.setBounds(86, 11, 74, 33);
		addcom(btAdd, 0);

	/*	btEdit = new JButton(M.stou("แก้ไข"));
		btEdit.setBounds(240, 11, 74, 33);
		addcom(btEdit, 0);*/

		btDel = new JButton(M.stou("ลบ"));
		btDel.setBounds(164, 11, 74, 33);
		addcom(btDel, 0);

		btPrev = new JButton(M.stou("<"));
		btPrev.setBounds(23, 59, 54, 31);
		addcom(btPrev, 0);

		btNext = new JButton(M.stou(">"));
		btNext.setBounds(101, 59, 54, 31);
		addcom(btNext, 0);

		btSave = new JButton(M.stou("บันทึก"));
		btSave.setBounds(811, 11, 74, 33);
		addcom(btSave, 0);

		btCancel = new JButton(M.stou("ยกเลิก"));
		btCancel.setBounds(890, 11, 74, 33);
		addcom(btCancel, 0);

		lbPolicy = new JLabel(M.stou("เลขที่กรมธรรม์"));
		lbPolicy.setBounds(17, 99, 116, 35);
		addcom(lbPolicy, 0);

		tfPolicy = new Tfield();
		tfPolicy.setBounds(111, 100, 106, 33);
		addcom(tfPolicy, 0);

		lbName = new JLabel(M.stou("ชื่อ-สกุล"));
		lbName.setBounds(224, 100, 80, 31);
		addcom(lbName, 0);

		tfName = new Tfield();
		tfName.setBounds(289, 100, 268, 33);
		addcom(tfName, 0);

		lbID = new JLabel(M.stou("บัตรประชาชน"));
		lbID.setBounds(559, 101, 99, 32);
		addcom(lbID, 0);

		tfID = new Tfield();
		tfID.setBounds(645, 100, 195, 33);
		addcom(tfID, 0);

		lbBranch = new JLabel(M.stou("สาขา"));
		lbBranch.setBounds(845, 101, 80, 34);
		addcom(lbBranch, 0);

		tfBranch = new Tfield();
		tfBranch.setBounds(881, 98, 67, 33);
		addcom(tfBranch, 0);

		lbPlan = new JLabel(M.stou("แบบประกัน"));
		lbPlan.setBounds(647, 148, 80, 33);
		addcom(lbPlan, 0);

		lbSum = new JLabel(M.stou("ทุนประกัน"));
		lbSum.setBounds(18, 145, 80, 34);
		addcom(lbSum, 0);

		nfSum = new Nfield(0);
		nfSum.setBounds(111, 145, 104, 33);
		addcom(nfSum, 0);

		lbEffDate = new JLabel(M.stou("วันเริ่ม"));
		lbEffDate.setBounds(221, 146, 80, 34);
		addcom(lbEffDate, 0);

		dfEffDate = new Dfield(true);
		dfEffDate.setBounds(288, 145, 102, 33);
		addcom(dfEffDate, 0);

		lbMature = new JLabel(M.stou("วันครบกำหนด"));
		lbMature.setBounds(400, 145, 101, 32);
		addcom(lbMature, 0);

		dfMatureDate = new Dfield(true);
		dfMatureDate.setBounds(506, 145, 99, 33);
		addcom(dfMatureDate, 0);

		tfPlan = new Tfield();
		tfPlan.setBounds(724, 145, 224, 33);
		addcom(tfPlan, 0);

		lbFrame = new JLabel();
		lbFrame.setBorder(Mborder.hiEtch());
		lbFrame.setBounds(8, 51, 961, 139);
		addcom(lbFrame, 0);

		String[]	tbTransaction$hdr = {M.stou("เลขที่คดี"), M.stou("วันที่ล้มละลาย"), M.stou("สถานะ"), M.stou("วันที่ปรับสถานะ"), M.stou("ผู้ปรับสถานะ"), M.stou("หมายเหตุ"),M.stou("วันที่นำเข้า"), M.stou("สถานะ 1"), M.stou("วันที่สถานะ 1"), M.stou("สถานะ 2"), M.stou("วันที่สถานะ 2")};
		boolean[]	tbTransaction$edt = {false, false, false, false, false, false, false, false, false, false};
		Object[]	tbTransaction$val = {"", "00000000", "", "00000000", "", "","00000000", "", "00000000", "", "00000000"};
		tbTransaction = new Mtable(tbTransaction$hdr);
		tbTransaction.setCellEditor(0, new Tfield());
		tbTransaction.setCellEditor(1, new Dfield(true));
		tbTransaction.setCellEditor(2, new Tfield());
		tbTransaction.setCellEditor(3, new Dfield(true));
		tbTransaction.setCellEditor(4, new Tfield());
		tbTransaction.setCellEditor(5, new Tfield());
		tbTransaction.setCellEditor(6, new Dfield(true));
		tbTransaction.setCellEditor(7, new Tfield());
		tbTransaction.setCellEditor(8, new Dfield(true));
		tbTransaction.setCellEditor(9, new Tfield());
		tbTransaction.setCellEditor(10, new Dfield(true));
		tbTransaction.setColumnWidth(0, 96);
		tbTransaction.setColumnWidth(1, 100);
		tbTransaction.setColumnWidth(2, 150);
		tbTransaction.setColumnWidth(3, 100);
		tbTransaction.setColumnWidth(4, 150);
		tbTransaction.setColumnWidth(5, 180);
		tbTransaction.setColumnWidth(6, 96);
		tbTransaction.setColumnWidth(7, 96);
		tbTransaction.setColumnWidth(8, 100);
		tbTransaction.setColumnWidth(9, 94);
		tbTransaction.setColumnWidth(10, 100);
		tbTransaction.setEditable(tbTransaction$edt);
		tbTransaction.initRow(tbTransaction$val);
		tbTransaction.setBounds(6, 202, 961, 303);
		addcom(tbTransaction, 0);
		tbTransaction.setAutoResizeMode(0);

		setBounds(6, 100, 988, 546);
	}
/* $$$ */
	KeyInput key ;
	DlgMatchAnotherPolicy  another ;
	String group;
	String position;
	public DlgMaintainStatusN(JFrame parent,String grp,String pos)
	{
	//uper(parent, true);
		setTitle("DlgMaintainStatusN");
		panel_0();
		group = grp;
		position = pos;
		btPrev.setEnabled(false);
		btNext.setEnabled(false);
		key = new KeyInput(3,this,"ค้นหาข้อมูลกรมธรรม์สถานะ N");
                key.keyObject(0,new String []{M.stou("E100เลขที่กรมธรรม์")});
                key.keyObject(1,new String []{M.stou("T150ชื่อ"),M.stou("T150สกุล")});			
                key.keyObject(2,new String []{M.stou("E150บัตรประชาชน")});
                key.buildScreen();
		key.setLimit(0,0,8);
		SCR.setEnabled(this,false,"JTextField");
		btSave.setVisible(false);
		btCancel.setVisible(false);
		btAdd.setEnabled(canDo());
		btDel.setEnabled(canDo());
		try {
                   Passwd passwd = null;
                /*   if(username.trim().length() >  0)
                        passwd  = new Passwd(username,userid);
                   else*/
                   passwd = new Passwd();
                   if (!passwd.passwordOK())
                       throw new Exception(M.stou("รหัสผ่านนี้ไม่สามารถเข้าระบบนี้ได้")) ;
		   another = new  DlgMatchAnotherPolicy(this);

                }
                catch(Exception e)
                {
                        Msg.msg(this,e.getMessage());
                        return ;
                }
		setVisible(true);
	}
	TempMasicFile t1;
	TempMasicFile t2;
	TempMasicFile t3;
	public void doButton(Object o)
	{
		try {
			if(o.equals(btSearch))
			{
				key = new KeyInput(3,this,"ค้นหาข้อมูลกรมธรรม์สถานะ N");
				key.keyObject(0,new String []{M.stou("E100เลขที่กรมธรรม์")});
                		key.keyObject(1,new String []{M.stou("T150ชื่อ"),M.stou("T150สกุล")});			
                		key.keyObject(2,new String []{M.stou("E150บัตรประชาชน")});
                		key.buildScreen();
				key.setLimit(0,0,8);
				key.showme();
				if (key.currkey() == -1 )
					return ;
				String selk = M.itoc(key.currkey());
				String [] k  = key.keybuffer();
			
				Vector  vall = null;
				if (selk.charAt(0) == '1')
					vall = (Vector)InsolventClient.equalInsolvent(selk,k[0],k[1]);
				else
					vall = (Vector)InsolventClient.equalInsolvent(selk,k[0],"");
				Vector vi = (Vector)vall.elementAt(0);
				Vector vit = (Vector)vall.elementAt(1);
				Vector vrem = (Vector)vall.elementAt(2);

				String [] f = (String [])vi.elementAt(0);
				int [] l = (int [])vi.elementAt(1);
				t1 = new TempMasicFile("serviceapp",f,l);
				t1.keyField(false,false,new String [] {"policyNo","caseID"});
				t1.buildTemp();

			//	System.out.println("v size--------"+vi.size()+"  "+vit.size()+"  "+vrem.size());
				String [] f1 = (String [])vit.elementAt(0);
				int [] l1 = (int [])vit.elementAt(1);
				t2 = new TempMasicFile("serviceapp",f1,l1);
				t2.keyField(true,true,new String [] {"policyNo","caseID"});
				t2.buildTemp();

				String [] f2 = (String [])vrem.elementAt(0);
				int [] l2 = (int [])vrem.elementAt(1);
				t3 = new TempMasicFile("serviceapp",f2,l2);
				t3.keyField(true,true,new String [] {"policyNo","caseID","tranDate","tranTime"});
				t3.buildTemp();
			//	System.out.println("vrem  size--------"+vrem.size());



				for (int i = 2;i<vi.size();i++)
				{
					t1.putBytes((byte[])vi.elementAt(i));
					t1.insert();
				}
			//	System.out.println("vit size--------"+vit.size());
				for (int i = 2;i<vit.size();i++)
				{
					t2.putBytes((byte[])vit.elementAt(i));
					t2.insert();
				}
		//		System.out.println("t2--------"+t2.fileSize());
				for (int i = 2;i<vrem.size();i++)
				{
					t3.putBytes((byte[])vrem.elementAt(i));
					t3.insert();
				}
				if(!t1.first())
					throw new Exception(M.stou("ไม่มีข้อมูลกรมธรรม์ที่สถานะ N ตามเงื่อนไขที่ระบุ"));
				System.out.println("go to show");
				showData();
			}
			else if (o.equals(btPrev))
			{
				if (!t1.previous())
					throw new Exception(M.stou("ข้อมูลนี้เป็นข้อมูลแรก"));
				
				showData();
			}
			else if (o.equals(btNext))
			{
				if (!t1.next())
					throw new Exception(M.stou("ข้อมูลนี้เป็นข้อมูลสุดท้าย"));				
				showData();
			}
			else if(o.equals(btAdd))
			{
				new DlgMatchDataOnly(this);		
			}
			else if(o.equals(btDel))
			{
                                // Array of {policyNo,firstname,lastname,idNo,caseID,remark,userID
				if (tbTransaction.getRowCount() == 0)
					throw new Exception(M.stou("ลบข้อมูลไม่ได้ต้องค้นหาข้อมูลก่อน"));

                                DlgSelectToDeleteInsolvent sel = new DlgSelectToDeleteInsolvent(this);
                                if (sel.selectToDel().charAt(0) == '0')
                                        throw new Exception(M.stou("ยกเลิกการดำเนินการลบข้อมูล"));
                                String [] data = new String[7]; 
                                data[0] = (String)tfPolicy.getText();
                                data[4] = (String)tbTransaction.getValueAt(0,0);
                                String idForDelPeople = "";
                                String fnameForDelPeople = "";
                                String lnameForDelPeople = "";
                                if(t1.equalGreat(data[0]+data[4]) && (data[0]+data[4]).compareTo(t1.get("policyNo")+t1.get("caseID")) == 0)
                                {
					if(t1.get("status").charAt(0) == 'C')
						throw new Exception(M.stou("ข้อมูลนี้เป็นสถานะยกเลิกอยู่แล้ว"));
                                        data[1] = t1.get("firstName");
                                        data[2] = t1.get("lastName");
                                        data[3] = t1.get("idNo");    
                                        idForDelPeople = t1.get("idNo2");
                                        fnameForDelPeople = t1.get("firstName2");
                                        lnameForDelPeople = t1.get("lastName2");
                                        DlgSelectReason dlg = new DlgSelectReason(this);
                                        if(!dlg.ok())
                                        {
                                                Msg.msg(this,M.stou("ยกเลิกการดำเนินการ"));
                                                return ;
                                        }
                                        data[5] = dlg.reason(); //remark
                                        data[6] = Passwd.getEmployeeNo(); // userID
                                        String sales = InsolventClient.cancelInsolvent(data);
                                        t1.delete();
					Msg.msg(this,M.stou("ลบข้อมูลกรมธรรม์ที่ระบุ เรียบร้อยแล้ว"));
					if(sales.trim().length() > 0)
						Msg.msg(this,M.stou("ข้อมูลโครงสร้างฝ่ายขายไม่ถูกต้อง กรุณาตรวจสอบ  "));
					// find another policy that match name or id
					getAnother(data[1],data[2],data[3]);
                                        boolean deleteAll = true;
					if (t1.first())
					{
						another.showme(t1,sel.selectToDel(),data[5],data[6]);
                                                deleteAll = another.deleteAll();
                                                
					}
                                        if (sel.selectToDel().charAt(0) == '2' && deleteAll)
                                        {
                                            String id = idForDelPeople;
                                            String fname = fnameForDelPeople;
                                            String lname = lnameForDelPeople;
                                            String caseID = data[4];
                                            String remark = data[5];
                                            String inforceDate = "00000000";  
  
                                            PublicRte.setRemote(false);
                                            Result res = PublicRte.getResult("blservice","rte.bl.service.nstatus.RteRemoveNstatus",new String [] {id,fname,lname,caseID,inforceDate,remark});
                                            if (res.status() == 0)
                                                throw new Exception(M.stou("ลบข้อมูลในแฟ้มบุคคลล้มละลาย (insolvent people) เรียบร้อยแล้ว")); 
                                            else
                                                throw new Exception((String)res.value());
                                        }
                                        	
                                }
			}

		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
		}
	}
	private boolean canDo()
	{
		if("AJ".indexOf(group) >= 0  && position.charAt(0)!='C')
			return true;
		return false ;
	}
	private void getAnother(String firstName,String lastName,String id) throws Exception
	{
		Vector  vall = null;
		vall = (Vector)InsolventClient.anotherInsolvent("1",firstName,lastName);
		
		Vector vi = (Vector)vall.elementAt(0);
		Vector vit = (Vector)vall.elementAt(1);
		Vector vrem = (Vector)vall.elementAt(2);

		String [] f = (String [])vi.elementAt(0);
		int [] l = (int [])vi.elementAt(1);
		t1 = new TempMasicFile("serviceapp",f,l);
		t1.keyField(false,false,new String [] {"policyNo","caseID"});
		t1.buildTemp();

			System.out.println("v ---another----   size--------"+vi.size()+"  "+vit.size()+"  "+vrem.size());
		String [] f1 = (String [])vit.elementAt(0);
		int [] l1 = (int [])vit.elementAt(1);
		t2 = new TempMasicFile("serviceapp",f1,l1);
		t2.keyField(false,false,new String [] {"policyNo","caseID","tranDate","tranTime"});
		t2.buildTemp();
		for (int i = 2;i<vi.size();i++)
		{
			t1.putBytes((byte[])vi.elementAt(i));
			t1.insert();
		}
			//	System.out.println("vit size--------"+vit.size());
		for (int i = 2;i<vit.size();i++)
		{
			t2.putBytes((byte[])vit.elementAt(i));
			t2.insert();
		}
		// Search BY ID
		vall = (Vector)InsolventClient.anotherInsolvent("2",id,"");
		vi = (Vector)vall.elementAt(0);
		vit = (Vector)vall.elementAt(1);
		System.out.println("v ---another----by id size--------"+vi.size()+"  "+vit.size());

		for (int i = 2;i<vi.size();i++)
		{
			t1.putBytes((byte[])vi.elementAt(i));
			t1.insert();
		}
			//	System.out.println("vit size--------"+vit.size());
		for (int i = 2;i<vit.size();i++)
		{
			t2.putBytes((byte[])vit.elementAt(i));
			t2.insert();
		}
	}
	private void showData() throws Exception
	{
//		System.out.println("showdata----------------------------------------------");
		tfPolicy.setText(t1.get("policyNo"));
		tfName.setText(t1.get("preName")+t1.get("firstName")+" "+t1.get("lastName"));
		tfID.setText(t1.get("idNo"));
		tfBranch.setText(t1.get("branch"));
		tfPlan.setText(t1.get("planNo"));
		System.out.println("bingo.....in show.....................0.......");
		dfMatureDate.setText(t1.get("matureDate"));
		System.out.println("bingo.....in show....................1........");
		dfEffDate.setText(t1.get("effectiveDate"));
		System.out.println("bingo.....in show.....................2.......");
		nfSum.setText(t1.get("sum"));
		System.out.println("bingo.....in show......................3......");
		showTransaction(t1.get("policyNo"),t1.get("caseID"),t1.get("dataDate"));
		System.out.println("bingo.....in show.......................4.....");
		if(t1.fileSize() > 1 )
		{
			btPrev.setEnabled(true);
			btNext.setEnabled(true);
		}
		else {
			btPrev.setEnabled(false);
			btNext.setEnabled(false);
		}	
	}
	private void showTransaction(String policyNo,String caseID,String dataDate)
	{
		int row = tbTransaction.getRowCount();
		for (int i = 0 ; i < row ;i++)
			tbTransaction.deleteRow(0);
		String []  data = new String [11];
//		System.out.println("show transaction in table----------------------------------------------");
		for (boolean st = t2.equalGreat(policyNo+caseID) ; st&& (policyNo+caseID).compareTo(t2.get("policyNo")+t2.get("caseID")) == 0 ; st = t2.next())
		{
			data[0] = t2.get("caseID");
			data[1] = t1.get("insolventDate");
			if("SI".indexOf(t2.get("typeOfTran")) >= 0)
				data[2] = M.stou("ล้มละลาย (รอตรวจสอบ)");
			else if("A".indexOf(t2.get("typeOfTran")) >= 0)
				data[2] = M.stou("ล้มละลาย (โดยระบบ)");
			else if("P".indexOf(t2.get("typeOfTran")) >= 0)
				data[2] = M.stou("ล้มละลาย (ตรวจสอบแล้ว)");
			else if("C".indexOf(t2.get("typeOfTran")) >= 0)
				data[2] = M.stou("ยกเลิกล้มละลาย");
			else
				data[2] = "-";
			data[3] = t2.get("tranDate");
			if (t2.get("userID").charAt(0) =='N')
				data[4]="         -      ";
			else
				data[4] = getName(t2.get("userID"));
			data[5] = "";
			data[6] = dataDate;
			if("PC".indexOf(t2.get("typeOfTran")) >= 0)
			{
				data[5] = getRemark(policyNo,caseID,t2.get("tranDate"),t2.get("tranTime"));
				data[7] = t2.get("newStatus1"); 
				data[8] = t2.get("newStatusDate1"); 
				data[9] = t2.get("newStatus2"); 
				data[10] = t2.get("newStatusDate2"); 
			}
			else {
				data[7] = t2.get("oldStatus1"); 
				data[8] = t2.get("oldStatusDate1"); 
				data[9] = t2.get("oldStatus2"); 
				data[10] = t2.get("oldStatusDate2"); 
			}
			tbTransaction.appendRow(data);
		}
	}
	public void processWindowEvent(WindowEvent e)
        {
                if (e.getID() == WindowEvent.WINDOW_CLOSING){
                        dispose();
                }
        }

	private String getRemark(String policyNo,String caseID,String tranDate,String tranTime)
	{
		
		for (boolean st = t3.equalGreat(policyNo+caseID) ; st && (policyNo+caseID).compareTo(t3.get("policyNo")+t3.get("caseID")) == 0 ; st = t3.next())
		{
			if((tranDate+tranTime).compareTo(t3.get("tranDate")+t3.get("tranTime")) == 0)
			{
				return t3.get("remark");
			}	
		}
		return "";
	}
	private String getName(String userID)
	{
		try {
			EmployeeInfo emp = new EmployeeInfo();
       		        String [] empArray = emp.searchEmployeeInfo(userID, true);
       	        	return (empArray[5] + empArray[6]+ "  " + empArray[7]);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return userID;
	}
}
