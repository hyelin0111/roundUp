package com.proj.rup.purchase.model.dao;

import java.util.List;
import java.util.Map;

import com.proj.rup.basket.model.vo.BasketProduct;
import com.proj.rup.member.model.vo.Address;
import com.proj.rup.member.model.vo.MemberAddress;
import com.proj.rup.product.model.vo.Product;
import com.proj.rup.purchase.model.vo.Purchase;
import com.proj.rup.purchase.model.vo.PurchaseCancel;
import com.proj.rup.purchase.model.vo.PurchaseComplete;

public interface PurchaseDAO {

	List<PurchaseComplete> selectPCList(String memberId);

	BasketProduct selectPurchaseOne(int basketNo);

	MemberAddress selectMemberInfo(String memberId);

	List<Address> selectAddrList(String memberId);

	Product buyNow(int productNo);

	int insertPurchase(Purchase purchase);

	int insertPurchaseComplete(PurchaseComplete purchaseComplete);

	int updateProductPurchase(Map<String, Integer> map);

	int deletePurchase(String imp_uid);

	int deletePurchaseComplete(String imp_uid);

	List<PurchaseComplete> selectAPCancelList();

	List<Map<String, String>> selectAPCList(int cPage, int numPerPage);

	int selectTotalPurchase();

	List<PurchaseCancel> selectAPCancelList(int cPage, int numPerPage);

	int selectTotalCancel();

	List<PurchaseComplete> selectPurchaseCompleteList(Map<String, Object> map, int cPage, int numPerPage);

	int selectPurchaseCompleteListCount(Map<String, Object> map);
}
