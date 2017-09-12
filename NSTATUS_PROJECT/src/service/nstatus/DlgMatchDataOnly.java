package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import service.bucket.*;
import java.io.*;
import utility.support.*;
import javax.swing.border.*;
import javax.swing.BorderFactory.*;
class DlgMatchDataOnly extends Mdialog
{
/* $$$-0
L0065002100800034lbPreNameคำนำหน้า
T0142002200920033tfPreName
L0262002300800034lbFirstNameชื่อ
T0302002201400033tfFirstName
L0446002200800034lbLastNameนามสกุล
T0509002201400033tfLastName
L0058006401300033lbIDNoเลขที่บัตรประชาชน
Q0174006602720029ffIDNoNS-NNNNS-NNNNNS-NNS-N
L0061010700900034lbCaseIDเลขที่คดี
T0143010601400033tfCaseID
L0321010601300034lbInsolventDateวันที่ล้มละลาย
D0418010601260033dfInsolventDate8
B0550010600940034btSearchตรวจสอบ
A0013014907030156tbMatchDataT0F0111เลขที่กรมธรรม์T0F0099คำนำหน้าT0F0153ชื่อT0F0158สกุลT0F0164บัตรประชาชน
B0473032901140034btConfirmยืนยัน Mark N
B0597032801190035btCancelยกเลิก Mark N
G0180011507370410
---- */
	JLabel			lbPolicyNo;
	Tfield			tfPolicyNo;
	JLabel			lbPreName;
	Tfield			tfPreName;
	JLabel			lbFirstName;
	Tfield			tfFirstName;
	JLabel			lbLastName;
	Tfield			tfLastName;
	JLabel			lbIDNo;
	Mformat			ffIDNo;
	JLabel			lbCaseID;
	Tfield			tfCaseID;
	JLabel			lbInsolventDate;
	Dfield			dfInsolventDate;
	JButton			btSearch;
	Mtable			tbMatchData;
	JButton			btConfirm;
	JButton			btCancel;
        JLabel                  lbFrame;
	void panel_0()
	{
		
		lbPolicyNo = new JLabel(M.stou("เลขที่กรมธรรม์"));
		lbPolicyNo.setBounds(55, 10, 80, 34);
		addcom(lbPolicyNo, 0);

		tfPolicyNo = new Tfield();
		tfPolicyNo.setBounds(142, 10, 90, 33);
		addcom(tfPolicyNo, 0);

		lbPreName = new JLabel(M.stou("คำนำหน้า"));
		lbPreName.setBounds(65, 71, 80, 34);
		addcom(lbPreName, 0);

		tfPreName = new Tfield();
		tfPreName.setBounds(142, 72, 92, 33);
		addcom(tfPreName, 0);

		lbFirstName = new JLabel(M.stou("ชื่อ"));
		lbFirstName.setBounds(262, 71, 80, 34);
		addcom(lbFirstName, 0);

		tfFirstName = new Tfield();
		tfFirstName.setBounds(302, 72, 140, 33);
		addcom(tfFirstName, 0);

		lbLastName = new JLabel(M.stou("นามสกุล"));
		lbLastName.setBounds(446, 71, 80, 34);
		addcom(lbLastName, 0);

		tfLastName = new Tfield();
		tfLastName.setBounds(509, 72, 140, 33);
		addcom(tfLastName, 0);

		lbIDNo = new JLabel(M.stou("เลขที่บัตรประชาชน"));
		lbIDNo.setBounds(58, 114, 130, 33);
		addcom(lbIDNo, 0);

		ffIDNo = new Mformat("NS-NNNNS-NNNNNS-NNS-N");
		ffIDNo.setBounds(174, 116, 272, 29);
		addcom(ffIDNo, 0);

		lbCaseID = new JLabel(M.stou("เลขที่คดี"));
		lbCaseID.setBounds(61, 157, 90, 34);
		addcom(lbCaseID, 0);

		tfCaseID = new Tfield();
		tfCaseID.setBounds(143, 156, 140, 33);
		addcom(tfCaseID, 0);

		lbInsolventDate = new JLabel(M.stou("วันที่ล้มละลาย"));
		lbInsolventDate.setBounds(321, 156, 130, 34);
		addcom(lbInsolventDate, 0);

		dfInsolventDate = new Dfield(true);
		dfInsolventDate.setBounds(418, 156, 126, 33);
		addcom(dfInsolventDate, 0);

		btSearch = new JButton(M.stou("Mark N"));
		btSearch.setBounds(250, 10, 94, 34);
		addcom(btSearch, 0);

		String[]	tbMatchData$hdr = {M.stou("เลขที่กรมธรรม์"), M.stou("คำนำหน้า"), M.stou("ชื่อ"), M.stou("สกุล"), M.stou("บัตรประชาชน")};
		boolean[]	tbMatchData$edt = {false, false, false, false, false};
		Object[]	tbMatchData$val = {"", "", "", "", ""};
		tbMatchData = new Mtable(tbMatchData$hdr);
		tbMatchData.setCellEditor(0, new Tfield());
		tbMatchData.setCellEditor(1, new Tfield());
		tbMatchData.setCellEditor(2, new Tfield());
		tbMatchData.setCellEditor(3, new Tfield());
		tbMatchData.setCellEditor(4, new Tfield());
		tbMatchData.setColumnWidth(0, 111);
		tbMatchData.setColumnWidth(1, 99);
		tbMatchData.setColumnWidth(2, 153);
		tbMatchData.setColumnWidth(3, 158);
		tbMatchData.setColumnWidth(4, 164);
		tbMatchData.setEditable(tbMatchData$edt);
		tbMatchData.initRow(tbMatchData$val);
		tbMatchData.setBounds(13, 219, 703, 156);
		addcom(tbMatchData, 0);

                lbFrame = new JLabel();
                lbFrame.setBorder(Mborder.hiEtch());
                lbFrame.setBounds(10, 42, 700, 164);
                addcom(lbFrame, 0);


                btConfirm = new JButton(M.stou("ยืนยัน Mark N"));
		btConfirm.setBounds(473, 379, 114, 34);
//		addcom(btConfirm, 0);

		btCancel = new JButton(M.stou("ยกเลิก Mark N"));
		btCancel.setBounds(597, 378, 119, 35);
//		addcom(btCancel, 0);

		setBounds(180, 115, 737, 460);
	}
/* $$$ */

	DlgMatchDataOnly(Mframe parent)
	{
		super(parent, true);
		setTitle("DlgMatchDataOnly");
		panel_0();
		try {
                   Passwd passwd = null;
                /*   if(username.trim().length() >  0)
                        passwd  = new Passwd(username,userid);
                   else*/
                   passwd = new Passwd();
                   if (!passwd.passwordOK())
                       throw new Exception(M.stou("รหัสผ่านนี้ไม่สามารถเข้าระบบนี้ได้")) ;

                }
                catch(Exception e)
                {
                        Msg.msg(this,e.getMessage());
                        return ;
                }
                utility.screen.SCR.setTitleBorder(lbFrame,M.stou("ข้อมูลบุคคลล้มละลายจากกรมบังคับคดี"),TitledBorder.LEFT,TitledBorder.DEFAULT_POSITION,M.getFont(),Color.blue);
		setVisible(true);
	}
	TempMasicFile t1;
	public void doButton(Object o)
	{
		try {
			if(o.equals(btSearch))
			{
				RandomAccessFile  frand = new RandomAccessFile("/c/service/nstatus/insolvent"+DateInfo.sysDate().substring(0,4)+".csv","rw");
                                frand.seek(frand.length());
				String strbuff = "";
				
		
				int row = tbMatchData.getRowCount();
				for (int i = 0; i < row;i++)
				{
					tbMatchData.deleteRow(0);
				}
				Vector vi = new Vector();
				if(tfPolicyNo.getText().trim().length() == 8)
				{
					String [] data  = new String[7];
					data[0] = tfPreName.getText().trim();
					data[1] = tfFirstName.getText().trim();
					data[2] = tfLastName.getText().trim();
					data[3] = ffIDNo.getText().trim();
					data[4] = tfCaseID.getText();
					data[5] = dfInsolventDate.getText();
					data[6] = tfPolicyNo.getText();
                                        if (data[2].trim().length() == 0  &&  data[3].trim().length() == 0 && data[2].trim().length() == 0 )
                                        {
                                                if (Msg.msg(this,M.stou("ข้อมูล จากกรมบังคับคดี ไม่เพียงพอที่จะ บันทึกลงแฟ้มบุคคลล้มละลาย \nระบบจะบันทึกเฉพาะแฟ้มกรมธรรม์ที่ล้มละลาย (สถานะ กธ. เป็น N) ต้องการดำเนินการต่อหรือไม่"),Msg.QUESTION,Msg.YES+Msg.NO) != Msg.YES)
                                                        throw new Exception(M.stou("ยกเลิกการดำเนินการ "));
                                        }
				  	Vector vdata = new Vector();
					vdata.addElement(data);		
					System.out.println("by......................policy");	
					vi  = InsolventClient.sendToMatchDataByPolicy(vdata,true);
					btConfirm.setEnabled(false);
					btCancel.setEnabled(false);
				}
			/*else
				{	
					String [] data  = new String [6];
					data[0] = tfPreName.getText();
					data[1] = tfFirstName.getText();
					data[2] = tfLastName.getText();
					data[3] = ffIDNo.getText();
					data[4] = tfCaseID.getText();
					data[5] = dfInsolventDate.getText();
					strbuff=data[0]+","+data[1]+","+data[2]+","+data[3]+","+data[4]+","+data[5];
					strbuff+="\r\n";
                        	        frand.write(M.utos(strbuff));
					frand.close();
				  	Vector vdata = new Vector();
					vdata.addElement(data);
					vi  = InsolventClient.sendToMatchData(vdata,true);
					btConfirm.setEnabled(false);
					btCancel.setEnabled(false);
				}*/
				System.out.println("bingo...................");
				if (vi.size() <= 2)
					throw new Exception(M.stou("ไม่พบข้อมูลกรมธรรม์ที่ต้อง Mark N"));
				System.out.println("bingo...................1");
				String [] f = (String [])vi.elementAt(0);
				System.out.println("bingo...................2");
                                int [] l = (int [])vi.elementAt(1);
				System.out.println("bingo...................3");
                                t1 = new TempMasicFile("serviceapp",f,l);
                                t1.keyField(false,false,new String [] {"policyNo","caseID"});
                                t1.buildTemp();
				System.out.println("bingo...................4");
				for (int i = 2;i<vi.size();i++)
                                {
                                        t1.putBytes((byte[])vi.elementAt(i));
                                        t1.insert();
                                }
				System.out.println("bingo..................5");
				String [] data1 = new String [5];
				for (boolean st = t1.first();st;st=t1.next())
				{
					data1[1] = t1.get("preName");			
					data1[2] = t1.get("firstName");			
					data1[3] = t1.get("lastName");			
					data1[4] = t1.get("idNo");			
					data1[0] = t1.get("policyNo");
					tbMatchData.appendRow(data1);			
				}
		                if(tfPolicyNo.getText().trim().length() == 8)
		                {
				        Msg.msg(this,M.stou("ปรับสถานะกรมธรรม์ เป็น N เรียบร้อยแล้ว"));
                                        searchForOtherPolicy();
                                
			        }
                                else {
				        Msg.msg(this,M.stou("ปรับสถานะกรมธรรม์ เป็น N เรียบร้อยแล้ว"));
                                }		
			}
			else if (o.equals(btCancel))
			{
				int row = tbMatchData.getSelectedRow();
                                if(row < 0 )
                                        throw new  Exception(M.stou("เลือกข้อมูลในตารางก่อน"));
                                // Array of {policyNo,firstname,lastname,idNo,caseID,remark,userID}

                                String [] data = new String[7]; 
                                data[0] = (String)tbMatchData.getValueAt(row,0);
                                if(t1.equalGreat(data[0]+tfCaseID.getText()) && (data[0]+tfCaseID.getText()).compareTo(t1.get("policyNo")+t1.get("caseID")) == 0)
                                {
                                        data[1] = t1.get("firstName");
                                        data[2] = t1.get("lastName");
                                        data[3] = t1.get("idNo");
					data[4] = t1.get("caseID");
                                        DlgSelectReason dlg = new DlgSelectReason(this);
                                        if(!dlg.ok())
                                        {
                                                Msg.msg(this,M.stou("ยกเลิกการดำเนินการ"));
                                                return ;
                                        }
                                        data[5] = dlg.reason(); //remark
                                        data[6] = Passwd.getEmployeeNo(); // userID
					Msg.msg(this,M.stou("ยกเลืกปรับสถานะกรมธรรม์เป็น N "));
                                        InsolventClient.cancelInsolvent(data);
                                        t1.delete();
                                        tbMatchData.deleteRow(row);
                                }
			}
			else if (o.equals(btConfirm))
			{
				int row = tbMatchData.getSelectedRow();
                                if(row < 0 )
                                        throw new  Exception(M.stou("เลือกข้อมูลในตารางก่อน"));
                                // Array of {policyNo,firstname,lastname,idNo,caseID,chgID,userID}

                                String [] data = new String[7];

                                data[0] = (String)tbMatchData.getValueAt(row,0);
                                data[4] = tfCaseID.getText();
                                if(t1.equalGreat(data[0]+data[4]) && (data[0]+data[4]).compareTo(t1.get("policyNo")+t1.get("caseID").trim()) == 0)
                                {
                                        data[1] = t1.get("firstName");
                                        data[2] = t1.get("lastName");
                                        data[3] = t1.get("idNo");
                                        data[5] = "N"; //remark
                                        data[6] = Passwd.getEmployeeNo(); // userID
                                        if(t1.get("idNo").compareTo(t1.get("idNo2")) != 0)
                                        {
                                                int ans = Msg.msg(this,M.stou("คุณต้องการเปลี่ยน เลขบัตรประชาชนเป็น " +t1.get("idNo2")+" หรือไม่? "),Msg.QUESTION,Msg.YES+Msg.NO+Msg.CANCEL) ;
                                                if (ans == Msg.YES)
                                                {
                                                        data[5]="Y"+t1.get("idNo2");
                                                }
                                                else if (ans == Msg.CANCEL)
                                                {
                                                        throw new Exception(M.stou("ยกเลิกการยืนยันข้อมูล"));
                                                }
                                        }
                                        InsolventClient.approveInsolvent(data);
					Msg.msg(this,M.stou("ยืนยันปรับสถานะกรมธรรม์เป็น N เรียบร้อยแล้ว"));
					
                                        t1.delete();
                                        tbMatchData.deleteRow(row);
                                }
			}
		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
		}
		
	}
        private void searchForOtherPolicy() throws Exception
        {
                String [] data  = new String[7];
                data[0] = tfPreName.getText();
                data[1] = tfFirstName.getText();
                data[2] = tfLastName.getText();
                data[3] = ffIDNo.getText();
                data[4] = tfCaseID.getText();
                data[5] = dfInsolventDate.getText();
                data[6] = tfPolicyNo.getText();
                Vector vsearch = new Vector();
                vsearch.add(data);
                Vector vresult = InsolventClient.searchOtherPolicyToMarkN(vsearch);
                DlgOtherPolicyForMarkN dlgMarkN = new  DlgOtherPolicyForMarkN(this,vresult,data);


        }
}
