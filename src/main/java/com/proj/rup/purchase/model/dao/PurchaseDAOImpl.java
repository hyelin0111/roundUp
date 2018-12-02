package com.proj.rup.purchase.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.proj.rup.basket.model.vo.BasketProduct;
import com.proj.rup.member.model.vo.Address;
import com.proj.rup.member.model.vo.MemberAddress;
import com.proj.rup.product.model.vo.Product;
import com.proj.rup.purchase.model.vo.Purchase;
import com.proj.rup.purchase.model.vo.PurchaseCancel;
import com.proj.rup.purchase.model.vo.PurchaseComplete;

@Repository
public class PurchaseDAOImpl implements PurchaseDAO {

	@Autowired
	private SqlSessionTemplate sqlSession;

	@Override
	public List<PurchaseComplete> selectPCList(String memberId) {
		return sqlSession.selectList("purchase.selectPCList",memberId);
	}

	@Override
	public BasketProduct selectPurchaseOne(int basketNo) {
		return sqlSession.selectOne("purchase.selectPurchaseOne", basketNo);
	}

	@Override
	public MemberAddress selectMemberInfo(String memberId) {
		return sqlSession.selectOne("purchase.selectMemberInfo", memberId);
	}

	@Override
	public List<Address> selectAddrList(String memberId) {
		return sqlSession.selectList("purchase.selectAddrList", memberId);
	}

	@Override
	public Product buyNow(int productNo) {
		return sqlSession.selectOne("purchase.buyNow", productNo);
	}

	@Override
	public int insertPurchase(Purchase purchase) {
		return sqlSession.insert("purchase.insertPurchase", purchase);
	}

	@Override
	public int insertPurchaseComplete(PurchaseComplete purchaseComplete) {
		return sqlSession.insert("purchase.insertPurchaseComplete", purchaseComplete);
	}

	@Override
	public int updateProductPurchase(Map<String, Integer> map) {
		return sqlSession.update("purchase.updateProductPurchase", map);
	}

	@Override
	public int deletePurchase(String imp_uid) {
		return sqlSession.delete("purchase.deletePurchase", imp_uid);
	}

	@Override
	public int deletePurchaseComplete(String imp_uid) {
		return sqlSession.delete("purchase.deletePurchaseComplete", imp_uid);
	}

	@Override
	public List<PurchaseComplete> selectAPCancelList() {
		return sqlSession.selectList("purchase.selectAPCancelList");
	}

	@Override
	public List<Map<String, String>> selectAPCList(int cPage, int numPerPage) {
		RowBounds rowBounds = new RowBounds((cPage-1)*numPerPage, numPerPage);
		return sqlSession.selectList("purchase.selectAPCList",null,rowBounds);
	}

	@Override
	public int selectTotalPurchase() {
		return sqlSession.selectOne("purchase.selectTotalPurchase");
	}

	@Override
	public List<PurchaseComplete> selectPurchaseCompleteList(Map<String, Object> map, int cPage, int numPerPage) {
		RowBounds rowBounds = new RowBounds((cPage-1)*numPerPage, numPerPage);
		return sqlSession.selectList("purchase.selectPurchaseCompleteList",map,rowBounds);
	}

	@Override
	public int selectPurchaseCompleteListCount(Map<String, Object> map) {
		return sqlSession.selectOne("purchase.selectPurchaseCompleteListCount", map);
	}

	@Override
	public List<PurchaseCancel> selectAPCancelList(int cPage, int numPerPage) {
		RowBounds rowBounds = new RowBounds((cPage-1)*numPerPage, numPerPage);
		return sqlSession.selectList("purchase.selectAPCancelList",null,rowBounds);

	}

	@Override
	public int selectTotalCancel() {
		return sqlSession.selectOne("purchase.selectTotalCancel");
	}

}
