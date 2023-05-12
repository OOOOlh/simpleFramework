package com.lh.service.combine.impl;

import com.lh.entity.bo.HeadLine;
import com.lh.entity.bo.ShopCategory;
import com.lh.entity.dto.MainPageInfoDTO;
import com.lh.entity.dto.Result;
import com.lh.service.combine.HeadLineShopCategoryCombineService;
import com.lh.service.solo.HeadLineService;
import com.lh.service.solo.ShopCategoryService;
import org.simpleframework.core.annotation.Service;

import java.util.List;

@Service
public class HeadLineShopCategoryCombineServiceImpl implements HeadLineShopCategoryCombineService {
    private HeadLineService headLineService;
    private ShopCategoryService shopCategoryService;
    @Override
    public Result<MainPageInfoDTO> getMainPageInfo() {
        //1、获取头条列表
        HeadLine headLineCondition = new HeadLine();
        headLineCondition.setEnableStatus(1);
        Result<List<HeadLine>> headLineList = headLineService.queryHeadLine(headLineCondition, 1, 4);

        //2、获取店铺类别列表
        ShopCategory shopCategoryCondition = new ShopCategory();
        Result<List<ShopCategory>> shopCategoryList = shopCategoryService.queryShopCategory(shopCategoryCondition, 1, 100);

        //3、合并两者并返回
        Result<MainPageInfoDTO> result = mergeMainPageInfoResult(headLineList, shopCategoryList);


        return null;
    }

    private Result<MainPageInfoDTO> mergeMainPageInfoResult(Result<List<HeadLine>> headLineList, Result<List<ShopCategory>> shopCategoryList) {
        return null;
    }
}
