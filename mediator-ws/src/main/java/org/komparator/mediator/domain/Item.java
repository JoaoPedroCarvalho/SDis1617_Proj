package org.komparator.mediator.domain;

import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;

public class Item {

    private ItemIdView itemId;
    private String desc;
    private int price;

    public Item(String desc, int price, String pid, String orgName) {
	this.itemId = new ItemIdView();
	this.itemId.setProductId(pid);
	this.itemId.setSupplierId(orgName);
	this.desc = desc;
	this.price = price;

    }

    public ItemIdView getItemId() {
	return this.itemId;
    }

    public String getDesc() {
	return this.desc;
    }

    public int getPrice() {
	return this.price;
    }

    public ItemView toView() {
	ItemView itemView = new ItemView();
	itemView.setItemId(this.getItemId());
	itemView.setDesc(this.getDesc());
	itemView.setPrice(this.getPrice());
	return itemView;
    }

}
