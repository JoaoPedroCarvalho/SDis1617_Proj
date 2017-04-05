package org.komparator.mediator.domain;

import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.ProductView;

public class Item {

    private ItemIdView itemId;
    private String desc;
    private int price;

    public Item(ProductView product, String orgName) {
	this.itemId = new ItemIdView();
	this.itemId.setProductId(product.getId());
	this.itemId.setSupplierId(orgName);
	this.desc = product.getDesc();
	this.price = product.getPrice();

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
