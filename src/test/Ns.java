package test;

import java.util.ArrayList;
import java.util.List;

public class Ns {
	public static void main(String[] args) {
		List<NsObject> list = new ArrayList<>();
		NsObject ns = new NsObject();
		ns.age = (long) 46;
		ns.name = "ţ˶˶";
		ns.gender = "��";
		String[] oth = new String[2];
		oth[0] = "�ܸ�";
		oth[1] = "��˧";
		ns.others = oth.toString();
		list.add(ns);
		for(NsObject nss:list){
			System.out.println(nss.others);
		}
	}
}
