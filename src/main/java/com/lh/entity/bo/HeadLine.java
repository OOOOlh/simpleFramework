package com.lh.entity.bo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.simpleframework.core.annotation.Component;

import java.util.Date;

@Data
@Component
public class HeadLine {
    private Long lineId;
    private String lineName;
    private String lineLink;
    private String lineImg;
    private Integer priority;
    private Integer enableStatus;
    private Date createTime;
    private Date lastEditTime;
}
