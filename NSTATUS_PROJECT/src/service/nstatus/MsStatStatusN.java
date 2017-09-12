package service.nstatus;
import manit.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.font.*;
import utility.support.DateInfo;
import utility.support.Assemble;
import utility.cfile.TempMasic;
import utility.cfile.CFile;
import utility.rteutility.PublicRte;
import service.service.ServiceCentralize;
import manit.rte.*;
import service.surender.*;
public class MsStatStatusN extends Mframe
{
/* $$$-0
L0077007600400034lbStartตั้งแต่
L0219007900200034lbToถึง
M0129007900700032myStart8
M0254007900700032myEnd8
B0153017301260043btPrintพิมพ์
L0038004003510109lb1
F0180011504440270
---- */
	JLabel			lbStart;
	JLabel			lbTo;
	MYfield			myStart;
	MYfield			myEnd;
	JButton			btPrint;
	JLabel			lb1;

	void panel_0()
	{
		lbStart = new JLabel(M.stou("ตั้งแต่"));
		lbStart.setBounds(77, 76, 40, 34);
		addcom(lbStart, 0);

		lbTo = new JLabel(M.stou("ถึง"));
		lbTo.setBounds(219, 79, 20, 34);
		addcom(lbTo, 0);

		myStart = new MYfield(true);
		myStart.setBounds(129, 79, 70, 32);
		addcom(myStart, 0);

		myEnd = new MYfield(true);
		myEnd.setBounds(254, 79, 70, 32);
		addcom(myEnd, 0);

		btPrint = new JButton(M.stou("พิมพ์"));
		btPrint.setBounds(153, 173, 126, 43);
		addcom(btPrint, 0);

		lb1 = new JLabel();
		//lb1.setBorder(Mborder.hiEtch());
		lb1.setBorder(BorderFactory.createRaisedBevelBorder());
		lb1.setBounds(38, 40, 351, 109);
		addcom(lb1, 0);

		setBounds(180, 115, 444, 270);
	}
/* $$$ */
	String sysMonth = DateInfo.sysDate().substring(0,6);	
	String servName = "serviceapp";
	public MsStatStatusN()
	{
		super("MsStatStatusN");
		panel_0();
		setVisible(true);
	}
	public void doButton(Object o){
		try{
			String smonth = myStart.getText();
			String emonth = myEnd.getText();
			if(!M.dateok(smonth+"01") || !M.dateok(emonth+"01"))
				throw new Exception(M.stou("ป้อนเดือนปีไม่ถูกต้อง"));
			if(smonth.compareTo(emonth) > 0)
				throw new Exception(M.stou("ป้อนเดือนปีไม่ถูกต้อง"));
			if(smonth.compareTo("255306") < 0)
				throw new Exception(M.stou("เดือนปีที่เริ่มอย่างน้อยต้อง 06/2553"));
			if(emonth.compareTo(sysMonth) > 0)
				throw new Exception(M.stou("เดือนปีที่สิ้นสุดต้องไม่มากกว่าปัจจุปัน"));
			print(smonth,emonth);
		}catch(Exception e){
			Msg.msg(this,e.getMessage(),Msg.INFO,Msg.DEFAULT);
		}
	}
	public void print(String smonth,String emonth) throws Exception {
		String path = "rte.bl.service.nstatus.RptStatStatusN";
		String param[]={SurenderCentralize.getRemote(),servName ,path,"rptservice",smonth,emonth};
		System.out.println( "before call pass FileMaker" );
		Result result = PublicRte.getClientResult( ServiceCentralize.BL_SERVER, "rte.FileMaker", param );
		System.out.println( "result status ------------> : " + result.status() );
		if ( result.status() != 0 )
                      throw new Exception( M.stou( "เกิดข้อผิดพลาดในการสร้าง Report Error: " ) + (String)result.value() );
                String fileName = (String)result.value();
		System.out.println( "ReportStatStatusN filename = " + fileName );
		SurenderUtility.checkStatusProcess( fileName );
                System.out.println( "Check status process ok..." );
		String[] fieldName   = new String[]{ "month", "backwardBalance", "nstatus", "cancel", "forwardBalance"};
		int[] fieldLength = new int[]{6, 7, 7 , 7, 7 };
                String[] keyField  = new String[] { "month"};
                char[] fieldType = new char[] {'T','N','N','N','N'};
                int[] fieldScale= new int[]{0,0,0,0};
		TempMasic temp = new TempMasic(fieldName, fieldType, fieldLength, fieldScale);
                temp.setNoOfKey(1);
                temp.setKey(0,keyField);
                System.out.println( "servName = " + servName );
                temp.buildTemp(servName);
                MasicInputStream stream = new MasicInputStream( fileName );
                Assemble ass = new Assemble();
                ass.getFile( stream, temp.file() );
                Masic.purge( fileName );
                Masic.remove( fileName );
		StatStatusView view = new StatStatusView(temp.file(),smonth,emonth);
/*
		MprintDot printer = new MprintDot("epson");
                Mscreen rd = new Mscreen("");
                rd.setPageSize(  8.0d, 11.0d );
                rd.setPrinter(printer );
                rd.render(view, 0, view.getPageCount()-1);
*/
		Mprint printer = new Mprint("laser");
                Mscreen rd = new Mscreen("");
                rd.setPrinter(printer );
                rd.render(view, 0, view.getPageCount()-1);
	}
	public void processWindowEvent(WindowEvent e)
        {
                if (e.getID() == WindowEvent.WINDOW_CLOSING)
                {
                        dispose();
                }
        }
	public class StatStatusView extends Mview {
		Font[]  font = new Font[4];
                float   iy = 0f;
                float[] bar =   { 1.0f, 3.5f, 7.5f, 11.5f, 15.5f, 19.5f};
		String  sysDate = DateInfo.formatDate(1,DateInfo.sysDate());
		Mrecord temp;
                String  smonth,emonth;
		public StatStatusView(Mrecord temp,String smonth,String emonth){
			font[0]  = M.getFont();
                        font[1] = font[0].deriveFont( 16.0f );
                        font[2] = font[1].deriveFont(Font.BOLD);
                        font[3] = font[0].deriveFont( 14.0f );
			this.temp = temp;
			this.smonth =smonth;
			this.emonth =emonth;
			try{
				reset();
                                head();
                                table();
                                detail();
                                savePage();
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
		public void head()throws Exception{
			setFont(font[3]);
			setLocation(20.0f,0.5f);
			drawText(M.stou("วันที่ ")+sysDate,Mview.RIGHT);
			setFont(font[2]);
			iy += 1.0f;
                        setLocation(10.5f,iy);
                        drawText(M.stou("สถิติกรมธรรม์บุคคลล้มละลาย"),Mview.MIDDLE);
			iy += 0.7f;
			String sToE = smonth.substring(4,6)+"/"+smonth.substring(0,4)+" - "+emonth.substring(4,6)+"/"+emonth.substring(0,4);
			setLocation(10.5f,iy);
			drawText(M.stou("ตั้งแต่เดือน ")+sToE,Mview.MIDDLE);
		}
		public void table() throws Exception{
			setFont(font[3]);
			iy += 1.0f;
			setLocation(bar[0],iy);
			lineTo(bar[5],iy);
			iy += 0.25f;
			setLocation((bar[0] + bar[1])/2,iy);
                        drawText(M.stou("เดือน/ปี"),Mview.MIDDLE);
                        setLocation((bar[1] + bar[2])/2,iy);
                        drawText(M.stou("จำนวนยกมา"),Mview.MIDDLE);
                        setLocation((bar[2] + bar[3])/2,iy);
                        drawText(M.stou("ปรับสถานะเป็น N"),Mview.MIDDLE);
                        setLocation((bar[3] + bar[4])/2,iy);
                        drawText(M.stou("ยกเลิกสถานะ N"),Mview.MIDDLE);
                        setLocation((bar[4] + bar[5])/2,iy);
                        drawText(M.stou("รวม"),Mview.MIDDLE);
			iy -= 0.25f;
			for(int i = 0;i < 6;i++){
				setLocation(bar[i],iy);
                                lineTo(bar[i],iy + 1.0f);
			}
			iy += 1.0f;
			setLocation(bar[0],iy);
                        lineTo(bar[5],iy);
		}
		public void detail() throws Exception{
			setFont(font[3]);
			String sumB = "0",sumC = "0";
			for(boolean b = temp.first(); b ;b = temp.next()){
				if(iy == 25.0f){
                                        for(int i = 0;i < bar.length;i++){
                                                setLocation(bar[i],iy);
                                                lineTo(bar[i],iy + 0.2f);
                                        }
                                        iy += 0.20f;
                                        setLocation(bar[0],iy);
                                        lineTo(bar[9],iy);
                                        savePage();
                                        reset();
                                        iy = 0.0f;
                                }
                                if(iy == 0.0f){
                                        //page = M.addnum(page,"1");
                                        head();
                                        table();
                                        iy += 0.25f;
                                }
				setLocation((bar[0] + bar[1])/2,iy);
                                drawText(temp.get("month").substring(4,6)+"/"+temp.get("month").substring(0,4),Mview.MIDDLE);
                                setLocation(bar[2] - 0.2f,iy);
                                drawText(M.edits(temp.get("backwardBalance")),Mview.RIGHT);
                                setLocation(bar[3] - 0.2f,iy);
                                drawText(M.edits(temp.get("nstatus")),Mview.RIGHT);
                                setLocation(bar[4] - 0.2f,iy);
                                drawText(M.edits(temp.get("cancel")),Mview.RIGHT);
                                setLocation(bar[5] - 0.2f,iy);
                                drawText(M.edits(temp.get("forwardBalance")),Mview.RIGHT);
				sumB = M.addnum(sumB,temp.get("nstatus"));
				sumC = M.addnum(sumC,temp.get("cancel"));
				iy -= 0.25f;
                                for(int i = 0;i < bar.length;i++){
                                        setLocation(bar[i],iy);
                                        lineTo(bar[i],iy + 0.80f);
                                }
                                iy += 0.80f;
			}
			for(int i = 0;i < bar.length;i++){
                                setLocation(bar[i],iy);
                                lineTo(bar[i],iy + 0.2f);
                        }
                        iy += 0.20f;
                        setLocation(bar[0],iy);
                        lineTo(bar[5],iy);
			sum(sumB,sumC);
		}
		public void sum(String sumB,String sumC)throws Exception{
			setFont(font[3]);
			temp.first();
			String A = temp.get("backwardBalance");
			iy += 0.25f;
			setLocation((bar[0] + bar[1])/2,iy);
              		drawText(M.stou("รวม"),Mview.MIDDLE);
                   	setLocation(bar[2] - 0.2f,iy);
                    	drawText(M.edits(A),Mview.RIGHT);
                    	setLocation(bar[3] - 0.2f,iy);
                   	drawText(M.edits(sumB),Mview.RIGHT);
                   	setLocation(bar[4] - 0.2f,iy);
                	drawText(M.edits(sumC),Mview.RIGHT);
                   	setLocation(bar[5] - 0.2f,iy);
                  	String total = M.addnum(A, sumB,0);
                  	total = M.subnum(total, sumC,0);
                   	drawText(M.edits(total),Mview.RIGHT);
                    	iy -= 0.25f;
                  	for(int i = 0;i < bar.length;i++){
                       		setLocation(bar[i],iy);
                    		lineTo(bar[i],iy + 1.0f);
                  	}
			iy += 1.0f;
			setLocation(bar[0],iy);
                        lineTo(bar[5],iy);
		}
	}//end View
	public static void main(String[] args)
	{
		new MsStatStatusN();
	}
}
