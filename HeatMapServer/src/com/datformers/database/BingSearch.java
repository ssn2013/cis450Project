package com.datformers.database;

import java.util.ArrayList;

import org.core4j.Enumerable;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.consumer.ODataConsumers;
import org.odata4j.consumer.behaviors.OClientBehaviors;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperty;
import org.odata4j.core.OQueryRequest;

public class BingSearch {
	public ArrayList<WebSearchResult> search(String query) {
		try {
			String accountKey = "oMiF6HC+ecJWSW1Nhp75kXmm9kpFMOPQ2CBnnI0va/0";
			ODataConsumer c = ODataConsumers
					.newBuilder(
							"https://api.datamarket.azure.com/Bing/SearchWeb/")
					.setClientBehaviors(
							OClientBehaviors
									.basicAuth("accountKey", accountKey))
					.build();
			query = "'" + query + "'";

			OQueryRequest<OEntity> oRequest = c.getEntities("Web").custom(
					"Query", query);
			Enumerable<OEntity> entities = oRequest.execute();

			System.out.println(entities.count());
			ArrayList<WebSearchResult> web = new ArrayList<WebSearchResult>();
			for (OEntity e:entities) {
				WebSearchResult tmp = new WebSearchResult();
				OProperty<?> title = e.getProperty("ID");
				Object a = title.getValue();
				tmp.setId(title.getValue().toString());
				title = e.getProperty("Title");
				tmp.setTitle(title.getValue().toString());
				title = e.getProperty("Description");
				tmp.setDescription(title.getValue().toString());
				title = e.getProperty("DisplayUrl");
				tmp.setDisplayURL(title.getValue().toString());
				title = e.getProperty("Url");
				tmp.setUrl(title.getValue().toString());
				web.add(tmp);
			}
			return web;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		BingSearch b = new BingSearch();
		ArrayList<WebSearchResult> t=b.search("this is sparta!!");
		WebSearchResult tmp=t.get(1);
		System.out.println(tmp.getId());
		System.out.println(tmp.getTitle());
		System.out.println(tmp.getDescription());
		System.out.println(tmp.getDisplayURL());
		System.out.println(tmp.getUrl());
	}
}