package com.lh.controller.superadmin;

import com.lh.entity.bo.HeadLine;
import com.lh.entity.dto.Result;
import com.lh.service.solo.HeadLineService;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.mvc.annotation.RequestMapping;
import org.simpleframework.mvc.type.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/headline")
public class HeadLineOperationController {
    @Autowired(value = "HeadLineServiceImpl")
    private HeadLineService headLineService;

    public Result<Boolean> addHeadLine(HttpServletRequest req, HttpServletResponse resp){
        //TODO:请求参数校验以及请求参数转化
        return headLineService.addHeadLine(new HeadLine());
    }

    @RequestMapping(value = "/remove", method = RequestMethod.GET)
    public Result<Boolean> removeHeadLine(HttpServletRequest req, HttpServletResponse resp){
        //TODO:请求参数校验以及请求参数转化
        return headLineService.removeHeadLine(1);
    }
    public Result<Boolean> modifyHeadLine(HttpServletRequest req, HttpServletResponse resp){
        //TODO:请求参数校验以及请求参数转化
        return headLineService.modifyHeadLine(new HeadLine());
    }
    public Result<HeadLine> queryHeadLineById(HttpServletRequest req, HttpServletResponse resp){
        //TODO:请求参数校验以及请求参数转化
        return headLineService.queryHeadLineById(1);
    }
    public Result<List<HeadLine>> queryHeadLine(HttpServletRequest req, HttpServletResponse resp){
        //TODO:请求参数校验以及请求参数转化
        return headLineService.queryHeadLine(null, 1, 100);
    }
}
