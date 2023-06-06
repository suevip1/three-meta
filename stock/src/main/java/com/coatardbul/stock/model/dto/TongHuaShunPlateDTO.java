package com.coatardbul.stock.model.dto;

import lombok.Data;

import java.util.List;

/**
    *
    */
@Data
public class TongHuaShunPlateDTO {



    private String sn;

    private Integer type;

    private List<String> codeArr;

    private String codes;

}