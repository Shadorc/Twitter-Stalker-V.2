package me.shadorc.twitterstalker.statistics;

import java.util.HashMap;

import me.shadorc.twitterstalker.storage.Data.NumbersEnum;
import me.shadorc.twitterstalker.storage.Storage;
import me.shadorc.twitterstalker.utility.Ressources;

public class NumberStat {

	private float total;
	private double num;
	private String desc;

	public NumberStat(String desc) {
		this.desc = desc;
		this.num = 0;
	}

	public void increment(int... i) {
		num += (i.length > 0) ? i[0] : 1;
	}

	public void setNum(double num) {
		this.num = num;
	}

	public double getNum() {
		return num;
	}

	public String getDesc() {
		return desc;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	@Override
	public String toString() {
		String percen = Ressources.format(this.getNum()/total*100.0);
		return "<b>" + Ressources.format(this.getNum()) + "</b> " + this.getDesc() + (total != 0 ? " (" + percen + "%)" : "");
	}

	public static HashMap<NumbersEnum, NumberStat> init() {
		HashMap <NumbersEnum, NumberStat> uniqueStatsMap = new HashMap<>();
		for(NumbersEnum stat : NumbersEnum.values()) {
			uniqueStatsMap.put(stat, new NumberStat(Storage.tra(stat)));
		}
		return uniqueStatsMap;
	}
}
