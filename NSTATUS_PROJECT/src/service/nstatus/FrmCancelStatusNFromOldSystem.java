package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;
import service.bucket.*;
public class FrmCancelStatusNFromOldSystem extends Mframe
{
/* $$$-0
L0015001501360034lbPolicyเลขที่กรมธรรม์
T0120001701400033tfPolicy
B0262001700900033btSearchค้นหา
A0008006912870479tbAllPolicyT0F0072กรมธรรม์T0F0166ชื่อ - สกุลT0F0148บัตรประชาชนT0F0103เลขที่คดีD8F0076วันที่บังคับคดีT0F0072สถานะ1D8F0081วันที่สถานะ 1T0F0063สถานะ 2D8F0082วันที่สถานะ 2T0T0081สถานะเดิม 1D8T0129วันที่สถานะเดิม 1T0T0106สถานะเดิม 2D8T0105วันที่สถานะเดิม 2
B0865001701310034btCancelยกเลิกสถานะ N
F0004007610080592
---- */
	JLabel			lbPolicy;
	Tfield			tfPolicy;
	JButton			btSearch;
	Mtable			tbAllPolicy;
	JButton			btCancel;

	void panel_0()
	{
		lbPolicy = new JLabel(M.stou("เลขที่กรมธรรม์"));
		lbPolicy.setBounds(15, 15, 136, 34);
		addcom(lbPolicy, 0);

		tfPolicy = new Tfield();
		tfPolicy.setBounds(120, 17, 140, 33);
		addcom(tfPolicy, 0);

		btSearch = new JButton(M.stou("ค้นหา"));
		btSearch.setBounds(262, 17, 90, 33);
		addcom(btSearch, 0);

		String[]	tbAllPolicy$hdr = {M.stou("กรมธรรม์"), M.stou("ชื่อ - สกุล"), M.stou("บัตรประชาชน"), M.stou("เลขที่คดี"), M.stou("วันที่บังคับคดี"), M.stou("สถานะ1"), M.stou("วันที่สถานะ 1"), M.stou("สถานะ 2"), M.stou("วันที่สถานะ 2"), M.stou("สถานะเดิม 1"), M.stou("วันที่สถานะเดิม 1"), M.stou("สถานะเดิม 2"), M.stou("วันที่สถานะเดิม 2")};
		boolean[]	tbAllPolicy$edt = {false, false, false, false, false, false, false, false, false, false , false , false , false };
		Object[]	tbAllPolicy$val = {"", "", "", "", "00000000", "", "00000000", "", "00000000", "", "00000000", "", "00000000"};
		tbAllPolicy = new Mtable(tbAllPolicy$hdr);
		tbAllPolicy.setCellEditor(0, new Tfield());
		tbAllPolicy.setCellEditor(1, new Tfield());
		tbAllPolicy.setCellEditor(2, new Tfield());
		tbAllPolicy.setCellEditor(3, new Tfield());
		tbAllPolicy.setCellEditor(4, new Dfield(true));
		tbAllPolicy.setCellEditor(5, new Tfield());
		tbAllPolicy.setCellEditor(6, new Dfield(true));
		tbAllPolicy.setCellEditor(7, new Tfield());
		tbAllPolicy.setCellEditor(8, new Dfield(true));
		tbAllPolicy.setCellEditor(9, new Tfield());
		tbAllPolicy.setCellEditor(10, new Dfield(true));
		tbAllPolicy.setCellEditor(11, new Tfield());
		tbAllPolicy.setCellEditor(12, new Dfield(true));
		tbAllPolicy.setColumnWidth(0, 72);
		tbAllPolicy.setColumnWidth(1, 166);
		tbAllPolicy.setColumnWidth(2, 148);
		tbAllPolicy.setColumnWidth(3, 103);
		tbAllPolicy.setColumnWidth(4, 76);
		tbAllPolicy.setColumnWidth(5, 81);
		tbAllPolicy.setColumnWidth(6, 81);
		tbAllPolicy.setColumnWidth(7, 81);
		tbAllPolicy.setColumnWidth(8, 81);
		tbAllPolicy.setColumnWidth(9, 100);
		tbAllPolicy.setColumnWidth(10, 100);
		tbAllPolicy.setColumnWidth(11, 100);
		tbAllPolicy.setColumnWidth(12, 100);
		tbAllPolicy.setEditable(tbAllPolicy$edt);
		tbAllPolicy.initRow(tbAllPolicy$val);
		tbAllPolicy.setBounds(8, 69, 985, 479);
		tbAllPolicy.setAutoResizeMode(0);
		addcom(tbAllPolicy, 0);

		btCancel = new JButton(M.stou("ยกเลิกสถานะ N"));
		btCancel.setBounds(865, 17, 131, 34);
		addcom(btCancel, 0);

		setBounds(4, 76, 1008, 592);
	}
/* $$$ */

	public FrmCancelStatusNFromOldSystem()
	{
		super("FrmCancelStatusNFromOldSystem");
		panel_0();
		try {
                   Passwd passwd = null;
                   passwd = new Passwd();
                   if (!passwd.passwordOK())
                       throw new Exception(M.stou("รหัสผ่านนี้ไม่สามารถเข้าระบบนี้ได้")) ;

                }
                catch(Exception e)
                {
                        Msg.msg(this,e.getMessage());
                        return ;
                }

		setVisible(true);
	}
	TempMasicFile temp;
	String [] field = {"typeOfPol","policyNo","preName","firstName","lastName","idNo","caseNo","insolventDate","policyStatus1","policyStatusDate1","policyStatus2","policyStatusDate2","oldPolicyStatus1","oldPolicyStatusDate1","oldPolicyStatus2","oldPolicyStatusDate2","personID","nameID"};
	int [] len = {1,8,20,30,30,13,15,8,1,8,1,8,1,8,1,8,13,13};

	public void doButton(Object o)
	{
		try {
			if (o.equals(btSearch))
			{			
				temp = InsolventClient.getPolicyStatusNFromOldSys(tfPolicy.getText());
				int row = tbAllPolicy.getRowCount();
				for (int i = 0 ; i < row; i++)
					tbAllPolicy.deleteRow(0);
				showAllPolicy();
			}
			else if (o.equals(btCancel))
			{
				int selr = tbAllPolicy.getSelectedRow();
				if (selr < 0 )
					throw new Exception(M.stou("เลือกข้อมูลในตารางก่อน"));	
				if (temp.equal((String)tbAllPolicy.getValueAt(selr,0)))
				{
					if(temp.get("policyStatus1").charAt(0) != 'N')
						throw new Exception(M.stou("กรมธรรม์ที่เลือกไม่ใช่กรมธรรม์ที่ล้มละลาย"));
					DlgNewStatusChange dlg = new DlgNewStatusChange(this,new String []{temp.get("oldPolicyStatus1"),temp.get("oldPolicyStatusDate1"),temp.get("oldPolicyStatus2"),temp.get("oldPolicyStatusDate2")});
					if (!dlg.ok())
						throw new Exception(M.stou("ยกเลิกการแก้ไขข้อมูล"));
					String [] chgdat = dlg.getChgStatus();
			//		String [] chgdat = new String []{temp.get("oldPolicyStatus1"),temp.get("oldPolicyStatusDate1"),temp.get("oldPolicyStatus2"),temp.get("oldPolicyStatusDate2")};
					System.out.println("chg............."+chgdat.length);
					if (chgdat[0].trim().length() == 0)
						throw new Exception(M.stou("ข้อมูลสถานะกรมธรรม์1 ที่ต้องการแก้ไม่ถูกต้อง"));
					String sales = InsolventClient.cancelInsolventFromOldSys(temp.copy().getBytes(),field,len,chgdat);					   temp.delete();	  	
					tbAllPolicy.deleteRow(selr);
					Msg.msg(this,M.stou("ยกเลิกกรมธรรม์สถานะ N เรียบร้อยแล้ว "));
					if (sales.trim().length() > 0)
						    Msg.msg(this,M.stou("ข้อมูลโครงสร้างฝ่ายขายไม่ถูกต้อง กรุณาตรวจสอบ  "));

				}				
			}
		}
		catch(Exception e)
		{
			Msg.msg(this,e.getMessage());
		}
	}
	private void showAllPolicy()
	{
		String [] data = new String [13];
		for (boolean st = temp.first();st;st=temp.next())
		{
			data[0] = temp.get("policyNo");
			data[1] = temp.get("preName")+temp.get("firstName")+"  "+temp.get("lastName");
			data[2] = temp.get("idNo");
			data[3] = temp.get("caseNo");
			data[4] = temp.get("insolventDate");
			data[5] = temp.get("policyStatus1");
			data[6] = temp.get("policyStatusDate1");
			data[7] = temp.get("policyStatus2");
			data[8] = temp.get("policyStatusDate2");
			data[9] = temp.get("oldPolicyStatus1");
			data[10] = temp.get("oldPolicyStatusDate1");
			data[11] = temp.get("oldPolicyStatus2");
			data[12] = temp.get("oldPolicyStatusDate2");
			tbAllPolicy.appendRow(data);
		}
	}
	public static void main(String[] args)
	{
		new FrmCancelStatusNFromOldSystem();
	}
	public void processWindowEvent(WindowEvent e)
        {
                if (e.getID() == WindowEvent.WINDOW_CLOSING)
		{
                        dispose();
                }
        }
}
