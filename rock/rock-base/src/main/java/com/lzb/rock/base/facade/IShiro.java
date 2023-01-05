/**
 * Copyright (c) 2015-2017, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzb.rock.base.facade;

import java.util.List;
import java.util.Set;

import com.lzb.rock.base.model.LogUser;
import com.lzb.rock.base.model.ShiroAuthz;
import com.lzb.rock.base.model.ShiroMenu;
import com.lzb.rock.base.model.ShiroRole;
import com.lzb.rock.base.model.ShiroUser;

/**
 * 获取用户登录信息，以及校验
 * 
 * @author lzb
 *
 */
public interface IShiro {
	static final String NAMES_DELIMETER = ",";

	/**
	 * 获取shiro的认证信息 根据账号查询用户信息
	 */
	ShiroUser getShiroAuthByUserAccount(String userAccount);

	/**
	 * 根据角色查询用户菜单按钮字段权限
	 * 
	 * @param userId
	 * @return
	 */
	public Set<ShiroAuthz> getShiroAuthzByShiroRoles(List<ShiroRole> roles);

	/**
	 * 根据用户ID查询用户角色集
	 * 
	 * @param userId
	 * @return
	 */
	public List<ShiroRole> getShiroRolesByUserId(Long userId);

	/**
	 * 获取封装的 ShiroUser
	 *
	 * @return ShiroUser
	 */
	public LogUser getUser();
}
