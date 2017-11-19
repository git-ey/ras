package aggregate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StreamTest {

	public static void main(String[] args) {

		List<Bill> list = new StreamTest().setObject();

		Set<String> set = new HashSet();

		for (Bill bills : list) {
			set.add(bills.getTradeTime().substring(0, 8));
		}

		List list1 = new ArrayList();
		int paySuccess = 0;
		int dfSuccess = 0;
		int fee = 0;
		int count = 0;
		// 遍历set集合中的日期
		for (String a : set) {
			for (Bill bill : list) {
				if (a.equals(bill.getTradeTime().substring(0, 8))) {
					paySuccess += bill.getMoney();
					fee += bill.getFee();
					count++;
					if ("承兑或交易成功".equals(bill.getType()))
						dfSuccess += bill.getMoney();
				}
			}
			Object[] objects = new Object[10];
			objects[0] = new BigDecimal(paySuccess);
			objects[1] = new BigDecimal(dfSuccess);
			objects[2] = new BigDecimal(fee);
			objects[3] = new BigDecimal(paySuccess - fee);
			objects[4] = a;
			list1.add(objects);
			System.out.println("支付成功金额：" + objects[0] + "\t" + "承兑或交易成功: " + objects[1] + "\t" + objects[4] + "\t"
					+ "手续费：" + objects[2] + "\t" + "商户结算" + "金额：" + objects[3] + "\t" + "交易笔数：" + count + "\t日期：" + a);
			paySuccess = 0;
			dfSuccess = 0;
			fee = 0;
			count = 0;
		}
		String date = null;
		System.out.println(date == null || date.isEmpty());
	}

	public List<Bill> setObject() {
		List<Bill> list = new ArrayList<Bill>();
		Bill bill = new Bill();
		bill.setTradeTime("201605050203");
		bill.setMoney(2);
		bill.setPayType("支付成功");
		bill.setType("承兑或交易成功");
		bill.setFee(1);
		list.add(bill);

		bill = new Bill();
		bill.setTradeTime("201605060203");
		bill.setMoney(3);
		bill.setType("");
		bill.setPayType("支付成功");
		bill.setFee(1);
		list.add(bill);

		bill = new Bill();
		bill.setMoney(4);
		bill.setTradeTime("201605050203");
		bill.setPayType("支付成功");
		bill.setType("承兑或交易成功");
		bill.setFee(1);
		list.add(bill);

		bill = new Bill();
		bill.setType("");
		bill.setPayType("支付成功");
		bill.setTradeTime("201605060203");
		bill.setMoney(4);
		bill.setFee(1);
		list.add(bill);
		return list;
	}
}

class Bill {
	int money;
	String type;
	String tradeTime;
	String payType;
	int fee;

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(String tradeTime) {
		this.tradeTime = tradeTime;
	}

	public int getFee() {
		return fee;
	}

	public void setFee(int fee) {
		this.fee = fee;
	}

	@Override
	public String toString() {
		return "Bill{" + "money=" + money + ", type='" + type + '\'' + ", tradeTime='" + tradeTime + '\''
				+ ", payType='" + payType + '\'' + '}';
	}
}