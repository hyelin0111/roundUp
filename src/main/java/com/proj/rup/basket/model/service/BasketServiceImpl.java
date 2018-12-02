package com.proj.rup.basket.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proj.rup.basket.model.dao.BasketDAO;
import com.proj.rup.basket.model.dao.BasketDAOImpl;
import com.proj.rup.basket.model.vo.Basket;
import com.proj.rup.basket.model.vo.BasketProduct;

@Service
public class BasketServiceImpl implements BasketService {

	@Autowired
	private BasketDAO basketDAO = new BasketDAOImpl();

	@Override
	public List<BasketProduct> selectBasketList(String memberId) {
		return basketDAO.selectBasketList(memberId);
	}

	@Override
	public int deleteBasket(int basketNo) {
		return basketDAO.deleteBasket(basketNo);
	}

	@Override
	public int updateBasket(Map<String, Integer> map) {
		return basketDAO.updateBasket(map);
	}

	@Override
	public int insertBasket(Map<String, Object> map) {
		return basketDAO.insertBasket(map);
	}
}
