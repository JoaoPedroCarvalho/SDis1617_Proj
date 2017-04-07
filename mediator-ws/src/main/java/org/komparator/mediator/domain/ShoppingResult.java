package org.komparator.mediator.domain;

import java.util.ArrayList;
import java.util.List;

import org.komparator.mediator.ws.CartItemView;
import org.komparator.mediator.ws.Result;
import org.komparator.mediator.ws.ShoppingResultView;

public class ShoppingResult {

    private String shoppingResultId;
    private Result result;
    private List<CartItemView> purchasedItems;
    private List<CartItemView> droppedItems;
    private int totalPrice;

    public ShoppingResult() {
	this.purchasedItems = new ArrayList<CartItemView>();
	this.droppedItems = new ArrayList<CartItemView>();
	this.totalPrice = 0;
    }

    public void updateResult() {
	if (purchasedItems.isEmpty()) {
	    this.result = Result.EMPTY;
	} else if (droppedItems.isEmpty()) {
	    this.result = Result.COMPLETE;
	} else {
	    this.result = Result.PARTIAL;
	}
    }

    public String getId() {
	return this.shoppingResultId;
    }

    public Result getResult() {
	return this.result;
    }

    public List<CartItemView> getPurchasedItems() {
	return this.purchasedItems;
    }

    public List<CartItemView> getDroppedItems() {
	return this.droppedItems;
    }

    public int getTotalPrice() {
	return this.totalPrice;
    }

    public void addDroppedItem(CartItemView cartItem) {
	this.droppedItems.add(cartItem);
    }

    public void addPurchasedItem(CartItemView cartItem) {
	this.totalPrice += cartItem.getQuantity() * cartItem.getItem().getPrice();
	this.purchasedItems.add(cartItem);
    }

    public void setShoppingResultId(String shoppingResultId) {
	this.shoppingResultId = shoppingResultId;
    }

    public ShoppingResultView toView() {
	ShoppingResultView shoppingResultView = new ShoppingResultView();
	shoppingResultView.setId(this.getId());
	shoppingResultView.setResult(this.getResult());
	shoppingResultView.setTotalPrice(this.getTotalPrice());
	shoppingResultView.getPurchasedItems().addAll(this.getPurchasedItems());
	shoppingResultView.getDroppedItems().addAll(this.getDroppedItems());
	return shoppingResultView;
    }

}
