package com.lzb.rock.base.enums;

import com.lzb.rock.base.facade.IMqDelayLevelEnum;

/**
 * 状态枚举类
 * 
 * @author lzb 2018年2月1日 下午3:50:38
 */
public enum DelayLevelEnum implements IMqDelayLevelEnum {

	DELLAY_1s(1),
	
	DELLAY_5s(2),
	
	DELLAY_10s(3),
	
	DELLAY_30s(4),
	
	DELLAY_1m(5),
	
	DELLAY_2m(6),
	
	DELLAY_3m(7),
	
	DELLAY_4m(8),
	
	DELLAY_5m(9),
	
	DELLAY_6m(10),
	
	DELLAY_7m(11),
	
	DELLAY_8m(12),
	
	DELLAY_9m(13),
	
	DELLAY_10m(14),
	
	DELLAY_20m(15),
	
	DELLAY_30m(16),
	
	DELLAY_1h(17),
	
	DELLAY_2h(18),
	
;

	private Integer level;

	DelayLevelEnum(Integer level){
		this.level = level;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}



}
