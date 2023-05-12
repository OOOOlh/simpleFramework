package com.lh.entity.dto;

import com.lh.entity.bo.HeadLine;
import com.lh.entity.bo.ShopCategory;
import lombok.Data;
import org.simpleframework.core.annotation.Component;

import java.util.List;

@Data
@Component
public class MainPageInfoDTO {
    private List<HeadLine> headLineList;
    private List<ShopCategory> shopCategoryList;
}
