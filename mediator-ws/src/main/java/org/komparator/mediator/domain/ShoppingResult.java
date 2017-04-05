package org.komparator.mediator.domain;

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
