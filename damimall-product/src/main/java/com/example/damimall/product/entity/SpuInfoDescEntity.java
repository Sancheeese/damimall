package com.example.damimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * spu信息介绍
 * 
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:50
 */
@Data
@TableName("pms_spu_info_desc")
public class SpuInfoDescEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 商品id
	 */
	private Long spuId;
	/**
	 * 商品介绍
	 */
	private String decript;

}
