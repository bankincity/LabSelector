package rte.bl.service.nstatus;
import manit.*;
import manit.rte.*;
import utility.cfile.*;
import utility.rteutility.*;
import rte.*;
public class RteStartBackwardBalance {
	String a1,a2,a3,a;
	String policyNotC;
	public RteStartBackwardBalance(){
	
	}
	public void calA1() throws Exception{//A1
		System.out.println("	start....calA1");
		Mrecord omast = CFile.opens("ordmast@mstpolicy");
		Mrecord imast = CFile.opens("indmast@mstpolicy");
		Mrecord wmast = CFile.opens("whlmast@mstpolicy");
		int bal = 0;
		int i = 0,o = 0,w = 0;
		for(boolean b = imast.first(); b ;b = imast.next()){
			if(imast.get("policyStatus1").equals("N")){
				i++;
			}
		}
		imast.close();
		System.out.println("		indmast status N = "+i);
		for(boolean b = omast.first(); b ;b = omast.next()){
                        if(omast.get("policyStatus1").equals("N")){
                                o++;
                        }
                }
		omast.close();
		System.out.println("		ordmast status N = "+o);
		for(boolean b = wmast.first(); b ;b = wmast.next()){
                        if(wmast.get("policyStatus1").equals("N")){
                                w++;
                        }
                }
		wmast.close();
		System.out.println("		whlmast status N = "+w);
		bal = i + o + w;
		a1 = bal + ""; 
		System.out.println("		A1 = "+a1);
		System.out.println("	end......calA1");
	}
	public void calA2() throws Exception{
		System.out.println("	start....calA2");
		int count = 0;
		Mrecord ins = CFile.opens("insolventpolicy@srvservice");
		for(boolean b = ins.first(); b ;b = ins.next()){
			if(!ins.get("status").equals("C")){
				count++;
			}
		}
		ins.close();
		a2 = count + "";
		System.out.println("		A2 = "+a2);
		System.out.println("	end......calA2");
	}
	public void calA3() throws Exception {
		System.out.println("	start....calA3");
		int count = 0;
		Mrecord ins = CFile.opens("insolventpolicy@srvservice");
		Mrecord inst = CFile.opens("insolventtran@srvservice");
		for(boolean b = ins.first(); b ;b = ins.next()){
			if(!ins.get("status").equals("C"))
				continue;
			if(inst.great(ins.get("policyNo")) && inst.get("typeOfTran").equals("C"))
				count++;
		}
		ins.close();
		inst.close(); 
		a3 = count + "";
		System.out.println("		A3 = "+a3);
		System.out.println("	end......calA3");
	}
	public String calA() throws Exception {
		System.out.println("start....calA");
		calA1();
		calA2();
		calA3();
		a = M.subnum(a1,a2);
		a = M.addnum(a,a3);
		System.out.println("end......calA");
		return a;
		//System.out.println("end......calA");
	}
	public String calB(String month) throws Exception{
		Mrecord ins = CFile.opens("insolventpolicy@srvservice");
                Mrecord inst = CFile.opens("insolventtran@srvservice");
		ins.start(3);
		int b1 = 0;
		for(boolean b = ins.great(month + "01"); b && ins.get("dataDate").substring(0,6).equals(month);b = ins.next()){
			if(!ins.get("status").equals("C")){
				b1++;
			}else if(inst.great(ins.get("policyNo")) && !inst.get("typeOfTran").equals("C")){
				b1++;
			}
		}
		ins.close();
                inst.close();
		System.out.println("B = "+b1); 
		return b1 + "";
	}
	public String calC(String month) throws Exception {
		Mrecord ins = CFile.opens("insolventpolicy@srvservice");
                Mrecord inst = CFile.opens("insolventtran@srvservice");
		ins.start(3);
		int c1 = 0;
		for(boolean b = ins.first(); b ;b = ins.next()){
			if(!ins.get("status").equals("C"))
				continue;
			System.out.println(ins.get("policyNo")+"...caseID...size "+ins.get("caseID").length());
			for(boolean c = inst.great(ins.get("policyNo")+ins.get("caseID")); c && inst.get("policyNo").equals(ins.get("policyNo")) && inst.get("caseID").equals(ins.get("caseID"));c = inst.next()){
				//System.out.println("found");
				if(inst.get("typeOfTran").equals("C") && inst.get("tranDate").substring(0,6).equals(month))
				{
					System.out.println("found");
					c1++;
				}
			}
/*
			for(boolean c = inst.great(ins.get("policyNo")+ins.get("caseID"));c && inst.get("policyNo").equals(ins.get("policyNo")); c = inst.next()){
				if(ins.get("dataDate").equals(inst.get("tranDate"))){
					c1++;
					break;
				}
			}
*/
		}
		ins.close();
                inst.close();
		System.out.println("C = "+c1);
		return c1 + "";
	}
	public static void main(String[] args){
		try{
			RteStartBackwardBalance test = new RteStartBackwardBalance();
/*			
			String a = test.calA();
			System.out.println("A = "+a);
*/
			//String b = test.calB(args[0]);
			String c = test.calC(args[0]);
						
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
}
