package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import service.bucket.*;
public class DlgMatchAnotherPolicy extends Mdialog
{
/* $$$-0
A0022005106790182tbMatchAnotherT0F0085เลขกรมธรรม์T0F0209ชื่อ - สกุลT0F0187เลขที่บัตรประชาชนT0F0084เลขที่คดีK0F0096เลือก
B0391024601750031btSelectลบข้อมูลสถานะ N
B0581024501220033btCancelยกเลิก
L0024000903670034lbMsgข้อมูลที่ต้องตรวจสอบว่าเป็นบุคคลเดียวกันหรือไม่
G0175009207240327
---- */
	Mtable			tbMatchAnother;
	JButton			btSelect;
	JButton			btCancel;
	JLabel			lbMsg;

	void panel_0()
	{
		String[]	tbMatchAnother$hdr = {M.stou("เลขกรมธรรม์"), M.stou("ชื่อ - สกุล"), M.stou("เลขที่บัตรประชาชน"), M.stou("เลขที่คดี"), M.stou("เลือก")};
		boolean[]	tbMatchAnother$edt = {false, false, false, false, false};
		Object[]	tbMatchAnother$val = {"", "", "", "", new Boolean(true)};
		tbMatchAnother = new Mtable(tbMatchAnother$hdr);
		tbMatchAnother.setCellEditor(0, new Tfield());
		tbMatchAnother.setCellEditor(1, new Tfield());
		tbMatchAnother.setCellEditor(2, new Tfield());
		tbMatchAnother.setCellEditor(3, new Tfield());
		tbMatchAnother.setCellEditor(4, new JCheckBox());
		tbMatchAnother.setColumnWidth(0, 85);
		tbMatchAnother.setColumnWidth(1, 209);
		tbMatchAnother.setColumnWidth(2, 187);
		tbMatchAnother.setColumnWidth(3, 84);
		tbMatchAnother.setColumnWidth(4, 96);
		tbMatchAnother.setEditable(tbMatchAnother$edt);
		tbMatchAnother.initRow(tbMatchAnother$val);
		tbMatchAnother.setBounds(22, 51, 679, 182);
		addcom(tbMatchAnother, 0);

		btSelect = new JButton(M.stou("ลบข้อมูลสถานะ N"));
		btSelect.setBounds(391, 246, 175, 31);
		addcom(btSelect, 0);

		btCancel = new JButton(M.stou("ยกเลิก"));
		btCancel.setBounds(581, 245, 122, 33);
		addcom(btCancel, 0);

		lbMsg = new JLabel(M.stou("ข้อมูลที่ต้องตรวจสอบว่าเป็นบุคคลเดียวกันหรือไม่"));
		lbMsg.setBounds(24, 9, 367, 34);
		addcom(lbMsg, 0);

		setBounds(175, 92, 724, 327);
	}
/* $$$ */
	public  DlgMatchAnotherPolicy(Mframe  parent)
	{
		super(parent, true);
		setTitle("DlgMatchAnotherPolicy");
		panel_0();
	}
        TempMasicFile t1;
        String reason="";
        String userID = "";
	public void showme(TempMasicFile temp,String isall,String reason,String userID)
	{
                
		int row = tbMatchAnother.getRowCount();
		for (int i = 0 ; i < row;i++)
		{
			tbMatchAnother.deleteRow(0);
		}
		for (boolean st = temp.first();st;st=temp.next())
		{
			Object [] data = new Object[5];
			data[0] = temp.get("policyNo");
			data[1] = temp.get("preName")+temp.get("firstName")+"  "+temp.get("lastName");
			data[2] = temp.get("idNo");
			data[3] = temp.get("caseID");
                        if (isall.charAt(0) == '2')
                        {
		            tbMatchAnother.setEditable(new boolean[]{false, false, false, false, false});
                            data[4] = new Boolean(true);
                        }
                        else {
                            data[4] = new Boolean(false);
                            tbMatchAnother.setEditable(new boolean[]{false, false, false, false, true});

                        }
			tbMatchAnother.appendRow(data); 
		}
                t1 = temp;
                this.reason = reason;
                this.userID = userID;
		selPol = "";
		selCaseID="";
		setVisible(true);
	}
	String selPol = "";
	String selCaseID = "";
        public boolean deleteAll = false;
        public boolean deleteAll()
        {
                return deleteAll;
        }
	public void doButton(Object o)
	{
		if(o.equals(btCancel))
		{
			selPol = "";
			selCaseID="";
                        deleteAll = false ;
                        Msg.msg(this,M.stou("ยกเลิการลบข้อมูลสถานะ N กรมธรรม์อื่นๆ"));
			dispose();
		}
		else if (o.equals(btSelect))
		{
                        try {
                                for (int i = 0 ; i < tbMatchAnother.getRowCount();i++)
                                {
                                        if (((Boolean)tbMatchAnother.getValueAt(i,4)).booleanValue())
                                        {            
                                                deleteAll= true;   
			                        selPol  = (String)tbMatchAnother.getValueAt(i,0);
			                        selCaseID  = (String)tbMatchAnother.getValueAt(i,3);

                                                String [] data = new String[7];
                                                data[0] = selPol;
                                                data[4] = selCaseID;
                                                if(t1.equalGreat(data[0]+data[4]) && (data[0]+data[4]).compareTo(t1.get("policyNo")+t1.get("caseID")) == 0)
                                                {
                                                        data[1] = t1.get("firstName");
                                                        data[2] = t1.get("lastName");
                                                        data[3] = t1.get("idNo");
                                                        data[5] = reason; //remark
                                                        data[6] = userID; // user
                                                        String sales = InsolventClient.cancelInsolvent(data);
                                                }

                                        }
                                }
                                if (deleteAll)
                                       throw new Exception(M.stou("ลบข้อมูลสถานะ N ที่เลือกเรียบร้อยแล้ว"));
                                else
                                       throw new Exception(M.stou("ไม่ได้ลบข้อมูล สถานะ N กรมธรม์อื่นเพิ่มเติม"));
                                        
                        }
                        catch(Exception e)
                        {
                                Msg.msg(this,e.getMessage());
                        }
                                   
			dispose();
		}
	}
}
