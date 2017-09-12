package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
class DlgSelectReason extends Mdialog
{
/* $$$-0
R0023001501880029rdNotMatchไม่ใช่บุคคลเดียวกันreason
R0021004902640033rdDelปลดล้มละลายโดยกรมบังคับคดีreason
R0020008601080034rdOtherอื่น ๆ ระบุreason
T0129008703190032tfReason
B0268014000860032btOKตกลง
B0361014000870032btCancelยกเลิก
G0180011504710213
---- */
	ButtonGroup		reason;
	JRadioButton	rdNotMatch;
	JRadioButton	rdDel;
	JRadioButton	rdOther;
	Tfield			tfReason;
	JButton			btOK;
	JButton			btCancel;

	void panel_0()
	{
		reason = new ButtonGroup();
		rdNotMatch = new JRadioButton(M.stou("ไม่ใช่บุคคลเดียวกัน"));
		reason.add(rdNotMatch);
		rdNotMatch.setBounds(23, 15, 188, 29);
		addcom(rdNotMatch, 0);

		rdDel = new JRadioButton(M.stou("ปลดล้มละลายโดยกรมบังคับคดี"));
		reason.add(rdDel);
		rdDel.setBounds(21, 49, 264, 33);
		addcom(rdDel, 0);

		rdOther = new JRadioButton(M.stou("อื่น ๆ ระบุ"));
		reason.add(rdOther);
		rdOther.setBounds(20, 86, 108, 34);
		addcom(rdOther, 0);

		tfReason = new Tfield();
		tfReason.setBounds(129, 87, 319, 32);
		addcom(tfReason, 0);

		btOK = new JButton(M.stou("ตกลง"));
		btOK.setBounds(268, 140, 86, 32);
		addcom(btOK, 0);

		btCancel = new JButton(M.stou("ยกเลิก"));
		btCancel.setBounds(361, 140, 87, 32);
		addcom(btCancel, 0);

		setBounds(180, 115, 471, 213);
	}
/* $$$ */
	DlgSelectReason(Mdialog parent)
	{
		super(parent, true);
		setTitle("DlgSelectReason");
		panel_0();
		rdNotMatch.setSelected(true);
		setVisible(true);
	}

	DlgSelectReason(Mframe  parent)
	{
		super(parent, true);
		setTitle("DlgSelectReason");
		panel_0();
		rdNotMatch.setSelected(true);
		setVisible(true);
	}
	boolean ok = false ;
	public boolean ok()
	{
		return ok;
	}
	String reasonText = "";
	public void doButton(Object o)
	{
		if (o.equals(btOK))
		{
			if(rdNotMatch.isSelected())
				reasonText = M.stou("ไม่ใช่บุคคลเดียวกัน");
			else if (rdDel.isSelected())
				reasonText = M.stou("ปลดล้มละลายโดยกรมบังคับคดี");
			else if (rdOther.isSelected())
			{
				if(tfReason.getText().trim().length() == 0)
				{
					Msg.msg(this,M.stou("ระบุเหตุผลในการยกเลิก สถานะ N"));
					return ;
				}
				reasonText = tfReason.getText();	
			}
			ok = true;
			dispose();
		}
		else if (o.equals(btCancel))
		{
			ok = false ;
			dispose();
		}		
	}
	public String reason()
	{
		return reasonText;
	}
	
}
