package com.example.damimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.example.common.valid.IntegerListValue;
import com.example.common.validator.group.AddGroup;
import com.example.common.validator.group.UpdateGroup;
import com.example.common.validator.group.UpdateShowStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message = "添加时不用id", groups = {AddGroup.class})
	@NotNull(message = "修改时必须指定id", groups = {UpdateGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "logo地址不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@URL(message = "logo地址必须是url格式", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@IntegerListValue(vals = {0, 1}, groups = {AddGroup.class, UpdateShowStatusGroup.class})
	@NotNull(message = "显示状态不能为空", groups = AddGroup.class)
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "检索字母地址不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是单个字母", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "sort不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@Min(value = 0, message = "sort不能是负数", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;

}
