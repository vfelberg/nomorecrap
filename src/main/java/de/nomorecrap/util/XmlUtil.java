package de.nomorecrap.util;

import java.util.List;


public class XmlUtil {

	public static void collectionToXml(MyStringBuilder s,
			String collectionName, String itemName, List<String> collection) {
		s.start("<" + collectionName + ">");
		for (String item : collection) {
			XmlUtil.itemToXml(s, itemName, item);
		}
		s.end("</" + collectionName + ">");
	}

	public static void itemToXml(MyStringBuilder s, String itemName, String item) {
		s.start("<" + itemName + ">").append(item).end("</" + itemName + ">");
	}

}
