
package com.proj.rup.product.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.proj.rup.product.model.service.ProductService;
import com.proj.rup.product.model.vo.Brand;
import com.proj.rup.product.model.vo.Category;
import com.proj.rup.product.model.vo.Product;
import com.proj.rup.product.model.vo.ProductCategoryLevel;
import com.proj.rup.product.model.vo.Product_File;

@Controller
public class ProductController {
	@Autowired
	private ProductService productService;
	
	private Logger logger=LoggerFactory.getLogger(getClass());
	
	@RequestMapping("/product/productSearch.do")
	public ModelAndView productSearch(@RequestParam String searchKeyword) {
		ModelAndView mav=new ModelAndView();
		logger.info("검색 키워드 : "+searchKeyword);
		searchKeyword=searchKeyword.replaceAll("^\\s+","");//처음엔 트림해주고
		if(searchKeyword.equals(""))searchKeyword="공백";//트림 했는데 아무것도 입력안돼있으면 공백한칸으로 넘어오게
		//String searchKeywordd=searchKeyword.replaceAll("[!@\\\\#$%\\^&*\\(\\)_+-=|\\[\\]\\{\\}<\\,>.?/]", " ");//특문>>공백
		//String searchKeywordd=searchKeyword.replaceAll("\\W|[^가-힣]", " ");//특문>>공백
		String searchKeywordd=searchKeyword.replaceAll("[^0-9a-z가-힣]", " ");//특문>>공백
		searchKeywordd = searchKeywordd.replaceAll("^\\s+",""); // ltrim
		if(searchKeywordd.length()==0)searchKeywordd="공백";
		String[] keyword=searchKeywordd.split(" ");//공백으로 나눈다.
		Map<String,Object> map=new HashMap<String,Object>();
		int notblank=0;
		for(int i=0;i<keyword.length;i++) {
			//System.out.println(keyword[i]);
			logger.info("keyword[i] : "+keyword[i]);
			if(keyword[i]!="") notblank++;
		}
		if(notblank==0) {
			keyword[0]="noproduct";
		}
		map.put("list", keyword);
		mav.addObject("searchKeyword", searchKeyword);
		List<Category> categoryList=productService.selecteAllCategoryList();
		List<Product> list=productService.productSearch(keyword);
		//List<Product> list=productService.productSearch(searchKeyword);
		searchNaver(searchKeyword,list,mav);
        int rowprice=0;
        int avgprice=0;
        Product popmenu=null;
        if(!list.isEmpty()) {
    		rowprice = productService.rowprice(map);   		
    		avgprice = productService.avgprice(map);
    		popmenu=productService.popmenu(map);
    	}
        mav.addObject("rowprice", rowprice);
        mav.addObject("avgprice", avgprice);
        mav.addObject("categoryList", categoryList);
        mav.addObject("popmenu", popmenu);
        mav.addObject("searchList", list);

		return mav;
	}
	
	@RequestMapping("/product/productCategorySearch.do")
	public ModelAndView productCategorySearch(@RequestParam int categoryNo) {
		ModelAndView mav = new ModelAndView();
		
		Map<String,Object> map=new HashMap<String, Object>();
		List<Category> categoryList=productService.selecteAllCategoryList();
		List<Integer> categoryArr=new ArrayList<>();
		
		for(Category c:categoryList) {
			if(c.getCategory_level()==1 && c.getCategory_no()==categoryNo) {
				categoryArr.add(c.getCategory_no());
				for(Category cc:categoryList) {
					if(cc.getCategory_level()==2 && cc.getParent_category()==c.getCategory_no()) {
						categoryArr.add(cc.getCategory_no());
						for(Category ccc:categoryList) {
							if(ccc.getCategory_level()==3 && ccc.getParent_category()==cc.getCategory_no()) {
								categoryArr.add(ccc.getCategory_no());
							}
						}
					}
				}
			}else if(c.getCategory_level()==2 && c.getCategory_no()==categoryNo) {
				categoryArr.add(c.getCategory_no());
				for(Category cc:categoryList) {
					if(cc.getCategory_level()==3 && cc.getParent_category()==c.getCategory_no()) {
						categoryArr.add(cc.getCategory_no());
					}
				}
			}else if(c.getCategory_level()==3 && c.getCategory_no()==categoryNo) {
				categoryArr.add(c.getCategory_no());
			}
		}
		logger.debug("categoryArr:"+categoryArr);
		map.put("categoryArr", categoryArr);
		List<Product> list=productService.categoryLevelOneSearch(map);
		logger.debug("productCategorySearch:"+list);
		mav.addObject("bloginfo", "not");
		mav.addObject("searchList",list);
		mav.addObject("categoryList",categoryList);
		 mav.setViewName("product/productSearch");
		 mav.addObject("searchKeyword", "전체");
		return mav;
	}
	
	@RequestMapping("/product/reSearch.do")
	public ModelAndView reSearch(@RequestParam(required=false) String searchKeyword,@RequestParam(required=false) String[] brand,@RequestParam(required=false) String categoryselect,@RequestParam(required=false,defaultValue="0") int price1,@RequestParam(required=false,defaultValue="0") int price2) {
		ModelAndView mav=new ModelAndView();
		logger.info("검색 키워드 : "+searchKeyword);
		searchKeyword=searchKeyword.replaceAll("^\\s+","");//처음엔 트림해주고
		if(searchKeyword.equals(""))searchKeyword="공백1";//트림 했는데 아무것도 입력안돼있으면 공백한칸으로 넘어오게
		//String searchKeywordd=searchKeyword.replaceAll("[!@\\\\#$%\\^&*\\(\\)_+-=|\\[\\]\\{\\}<\\,>.?/]", " ");//특문>>공백
		//String searchKeywordd=searchKeyword.replaceAll("\\W|[^가-힣]", " ");//특문>>공백
		String searchKeywordd=searchKeyword.replaceAll("[^0-9a-z가-힣]", " ");//특문>>공백
		searchKeywordd = searchKeywordd.replaceAll("^\\s+",""); // ltrim
		if(searchKeywordd.length()==0)searchKeywordd="공백2";
		String[] keyword=searchKeywordd.split(" ");//공백으로 나눈다.
		/*for(String s:brand) {
			System.out.println("브랜드="+s);
		}*/
		//System.out.println(price1+"~"+price2+"범위");
		if(brand[0].equals("all"))
			brand=new String[] {"CU","GS25","7ELEVEN","MINISTOP","EMART24"};
		Map<String,Object> map=new HashMap<String, Object>();
		int notblank=0;
		for(int i=0;i<keyword.length;i++) {
			//System.out.println(keyword[i]);
			if(keyword[i]!="") notblank++;
		}
		if(notblank==0) {
			keyword[0]="noproduct";
		}
		map.put("list",keyword );
		List<Category> categoryList=productService.selecteAllCategoryList();
		List<Integer> categoryArr=new ArrayList<>();
		if(Integer.parseInt(categoryselect)==0) {
			for(Category c:categoryList) {
				categoryArr.add(c.getCategory_no());				
			}
		}else {
			for(Category c:categoryList) {
				if(c.getCategory_level()==1 && c.getCategory_no()==Integer.parseInt(categoryselect)) {
					categoryArr.add(c.getCategory_no());
					for(Category cc:categoryList) {
						if(cc.getCategory_level()==2 && cc.getParent_category()==c.getCategory_no()) {
							categoryArr.add(cc.getCategory_no());
							for(Category ccc:categoryList) {
								if(ccc.getCategory_level()==3 && ccc.getParent_category()==cc.getCategory_no()) {
									categoryArr.add(ccc.getCategory_no());
								}
							}
						}
					}
				}else if(c.getCategory_level()==2 && c.getCategory_no()==Integer.parseInt(categoryselect)) {
					categoryArr.add(c.getCategory_no());
					for(Category cc:categoryList) {
						if(cc.getCategory_level()==3 && cc.getParent_category()==c.getCategory_no()) {
							categoryArr.add(cc.getCategory_no());
						}
					}
				}else if(c.getCategory_level()==3 && c.getCategory_no()==Integer.parseInt(categoryselect)) {
					categoryArr.add(c.getCategory_no());
				}
			}
		}
		//System.out.println("categoryArr="+categoryArr);
		
		mav.addObject("categoryList", categoryList);
		map.put("searchKeyword", searchKeyword);
		map.put("brand", brand);
		map.put("categoryArr", categoryArr);
		map.put("price1", price1);
		map.put("price2", price2);
		List<Product> plist=productService.productSearch(searchKeyword);
		List<Product> list=productService.reSearch(map);
		searchNaver(searchKeyword,list,mav);
        int rerowprice=0;
        int reavgprice=0;
        Product repopmenu=null;
        if(!list.isEmpty()) {
    		rerowprice = productService.rerowprice(map);   		
    		reavgprice = productService.reavgprice(map);
    		repopmenu=productService.repopmenu(map);
    	}
        mav.addObject("rowprice", rerowprice);
        mav.addObject("avgprice", reavgprice);
        mav.addObject("popmenu", repopmenu);
        mav.addObject("searchKeyword", searchKeyword);        
        mav.addObject("searchList", list);
        //System.out.println("검색키워드"+searchKeyword+" 브랜드"+brand+" 카테고리 배열 "+categoryArr+"가격1"+price1+" 가격2"+price2);
        mav.setViewName("product/productSearch");
		return mav;
	}

	//상품등록
	@RequestMapping("/product/productEnroll.do")
	public ModelAndView pruductEnroll() {
		ModelAndView mav = new ModelAndView();
		List<Brand> brandList = productService.selectBrandList();
		List<Category> categoryList = productService.selectCategoryList();
		
		logger.debug("brandList@controller"+brandList);
		logger.debug("categoryList@controller"+categoryList);
		
		mav.addObject("brandList",brandList);
		mav.addObject("categoryList",categoryList);
		mav.setViewName("manager/productEnroll");
		return mav;
	}
	
	@RequestMapping("/product/autoComplete.do")
	@ResponseBody
	public List<Product> autoCompleate(@RequestParam String autoKeyword) {
		logger.debug("오토키워드 ajax시작"+autoKeyword);
		List<Product> list=productService.productSearch(autoKeyword);
		return list;

	}
	
	@RequestMapping("/product/productEnrollEnd.do")
	public ModelAndView productEnrollEnd(@RequestParam(value="productName") String productName
										,@RequestParam(value="memberId") String memberId
										,@RequestParam(value="brandNo") int brandNo
										,@RequestParam(value="categoryNo") int[] categoryNo
										,@RequestParam(value="price") int price
										,@RequestParam(value="upFile") MultipartFile upFile,HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		logger.debug("상품등록요청");
		logger.debug("upFile : "+upFile.getOriginalFilename());
		try {	
			//첨부파일 이름 설정/upload폴더에 저장로직
			String saveDirectory = request.getSession().getServletContext().getRealPath("/resources/upload/productFile");
			
			String originalFileName = upFile.getOriginalFilename();
			String ext = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
			int rndNum = (int)(Math.random()*1000);
			String renamedFileName = sdf.format(new Date(System.currentTimeMillis()))+"_"+rndNum+"."+ext;
			
			try {
				upFile.transferTo(new File(saveDirectory+"/"+renamedFileName));
			} catch (Exception e) {
				e.printStackTrace();
			}
			int category_No = 0;
			for(int i=0;i<categoryNo.length;i++) {
				category_No = categoryNo[i];
			}
			
			Product p = new Product(productName, brandNo, price, memberId,category_No);
			
			Product_File pf = new Product_File();
			pf.setOriginalFilename(originalFileName);
			pf.setRenamedFilename(renamedFileName);
			
			logger.debug("productFile@productController : "+pf);
			logger.debug("product@productController : "+p);
			
			//비지니스 로직
			int result = productService.insertProduct(p,pf);
			
			//view단 분기
			String loc = "/product/allProductList.do?flag=1";
			String msg = "";
			
			if(result>0) {
				msg="상품등록 성공!";
			}else {
				msg="상품등록 실패!";
			}
			
			mav.addObject("msg",msg);
			mav.addObject("loc",loc);
			mav.setViewName("common/msg");
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return mav;
	}
	

	//메일테스트
	@RequestMapping("/product/mailTest.do")
	public ModelAndView mailTest() {
		ModelAndView mav = new ModelAndView();
		return mav;
	}

	@RequestMapping("/product/selectNewProduct.do") 
	@ResponseBody
	public Map<String, Object> selectNewProduct(HttpServletResponse response) {
		List<Product> productNewList = productService.selectNewProduct();
	    //System.out.println("productNewList" + productNewList);
	    
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productNewList", productNewList);
		
		return map;
	}
	
	@RequestMapping("/product/selectHotProduct.do") 
	@ResponseBody
	public Map<String, Object> selectHotProduct(HttpServletResponse response) {
		List<Product> productHotList = productService.selectHotProduct();
	    //System.out.println("productList" + productHotList);
	    
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productHotList", productHotList);
		
		return map;
	}
	
	@RequestMapping("/product/selectChildCategory.do")
	@ResponseBody
	public List<Category> selectChildCategory(@RequestParam(value="categoryNo") int categoryNo) {
		List<Category> categoryList = productService.selectChildCategory(categoryNo);
		logger.debug("categoryNo@controller:"+categoryList);
		return categoryList;
	}
	
	
	
	@RequestMapping("/product/selectCategory.do")
	@ResponseBody
	public List<ProductCategoryLevel> selectCategory(@RequestParam(value="searchKeyword") String searchKeyword) {
		List<ProductCategoryLevel> list = productService.selectCategory(searchKeyword);
		
/*		System.out.println("######################################################################");
		System.out.println(list);
*/		
		for(int i=0; i<list.size(); i++) {
			if(list.get(i).getCategory_level() == 1) {
				//List<Category> categoryList = productService.selectCategoryList();
				
			} else if(list.get(i).getCategory_level() == 2) {
				System.out.println("######################################################################");
				System.out.println(list.get(i).getCategory_name());
				Category parentCategory = productService.selectParentCategory(list.get(i).getParent_category());
				System.out.println("######################################################################");
				System.out.println(parentCategory);
				System.out.println(parentCategory.getCategory_name());
				System.out.println("######################################################################");
				
			} else {
				
			}
		}
		
		return list;
	}
	
	public void searchNaver(String searchKeyword,List<Product> list,ModelAndView mav) {
	      //-------------------------------------------------------------------------------------키워드로 네이버 블로그 검색------------------------------
	            String clientId = "vbEkw23fbdDmfyg_CYg9";//애플리케이션 클라이언트 아이디값";
	              String clientSecret = "iTpsbroJuP";//애플리케이션 클라이언트 시크릿값";
	              try {
	                 String text="";
	                 if(list.isEmpty()) {
	                    text = URLEncoder.encode(searchKeyword, "UTF-8");              
	                 }else {
	                    text = URLEncoder.encode(list.get(0).getProductName(), "UTF-8");              
	                 }
	                  String apiURL = "https://openapi.naver.com/v1/search/blog?query="+ text+"&display=5&start=1"; // json 결과
	                  //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // xml 결과
	                  URL url = new URL(apiURL);
	                  HttpURLConnection con = (HttpURLConnection)url.openConnection();
	                  con.setRequestMethod("GET");
	                  con.setRequestProperty("X-Naver-Client-Id", clientId);
	                  con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
	                  int responseCode = con.getResponseCode();
	                  BufferedReader br;
	                  if(responseCode==200) { // 정상 호출
	                      br = new BufferedReader(new InputStreamReader(con.getInputStream()));
	                  } else {  // 에러 발생
	                      br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
	                  }
	                  String inputLine;
	                  StringBuffer response = new StringBuffer();
	                  while ((inputLine = br.readLine()) != null) {
	                      response.append(inputLine);
	                  }
	                  br.close();
	                  //System.out.println(response.toString());
	                  mav.addObject("bloginfo", response);
	              } catch (Exception e) {
	                  System.out.println(e);
	              }
	            //-------------------------------------------------------------------------------------키워드로 네이버 블로그 검색 끝------------------------------
	   }
	@RequestMapping("/product/newProductPop.do")
	public ModelAndView newProductPop(){
		ModelAndView mav = new ModelAndView();
		Map<String,Object> pf = productService.selectProductFileOne();
		mav.addObject("pf",pf);
		logger.info("pf====="+pf);
		mav.setViewName("product/newProductPop");
		return mav;
	}
	
	@RequestMapping("/product/productView.do")
	public ModelAndView productView(@RequestParam("productNo") int productNo) {
		ModelAndView mav = new ModelAndView();
		Product p = productService.productView(productNo);
		List<Brand> brandList = productService.selectBrandList();
		List<Category> categoryList = productService.selectCategoryList();
		
		mav.addObject("brandList",brandList);
		mav.addObject("categoryList",categoryList);
		mav.addObject("product",p);
		mav.setViewName("manager/productView");
		return mav;
	}
	
	@RequestMapping("/product/productUpdate.do")
	public ModelAndView updateProduct(@RequestParam(value="productName") String productName
									 ,@RequestParam(value="memberId") String memberId
									 ,@RequestParam(value="brandNo") int brandNo
									 ,@RequestParam(value="categoryNo") int[] categoryNo
									 ,@RequestParam(value="price") int price
									 ,@RequestParam(value="productNo") int productNo
									 ,@RequestParam(value="upFile") MultipartFile upFile,HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		try {	
			//첨부파일 이름 설정/upload폴더에 저장로직
			String saveDirectory = request.getSession().getServletContext().getRealPath("/resources/upload/productFile");
			
			String originalFileName = upFile.getOriginalFilename();
			String ext = originalFileName.substring(originalFileName.lastIndexOf(".")+1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
			int rndNum = (int)(Math.random()*1000);
			String renamedFileName = sdf.format(new Date(System.currentTimeMillis()))+"_"+rndNum+"."+ext;
			
			try {
				upFile.transferTo(new File(saveDirectory+"/"+renamedFileName));
			} catch (Exception e) {
				e.printStackTrace();
			}
			int category_No = 0;
			for(int i=0;i<categoryNo.length;i++) {
				category_No = categoryNo[i];
			}
			
			Product p = new Product(productNo,productName, brandNo, price, memberId,category_No);
			
			Product_File pf = new Product_File();
			pf.setOriginalFilename(originalFileName);
			pf.setRenamedFilename(renamedFileName);
			pf.setProductNo(p.getProductNo());
			
			logger.debug("productFile@productController : "+pf);
			logger.debug("product@productController : "+p);
			
			//비지니스 로직
			int result = productService.updateProduct(p,pf);
			
			//view단 분기
			String loc = "/product/allProductList.do";
			String msg = "";
			
			if(result>0) {
				msg="상품수정 성공!";
			}else {
				msg="상품수정 실패!";
			}
			
			mav.addObject("msg",msg);
			mav.addObject("loc",loc);
			mav.setViewName("common/msg");
		}catch(Exception e) {
			e.printStackTrace();
		}
		return mav;
	}
	
	@RequestMapping("/product/deleteProduct")
	public ModelAndView deleteProduct(@RequestParam("productNo") int productNo) {
		ModelAndView mav = new ModelAndView();
		int result = productService.deleteProduct(productNo);
		//view단 분기
		String loc = "/product/allProductList.do";
		String msg = "";
		
		if(result>0) {
			msg="상품삭제 성공!";
		}else {
			msg="상품삭제 실패!";
		}
		
		mav.addObject("msg",msg);
		mav.addObject("loc",loc);
		mav.setViewName("common/msg");
		
		mav.setViewName("common/msg");
		return mav;
	}
	
}

