package com.lzb.rock.login.shiro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.lzb.rock.base.facade.IShiro;
import com.lzb.rock.base.model.ShiroAuthz;
import com.lzb.rock.base.model.ShiroMenu;
import com.lzb.rock.base.model.ShiroRole;
import com.lzb.rock.base.model.ShiroUser;

import lombok.extern.slf4j.Slf4j;

/**
 * Shiro 登录认证公共类
 * 
 * @author lzb
 * 
 *         2019年3月31日 下午5:25:54
 */
@Slf4j
@Configuration
public class ShiroRealm extends AuthorizingRealm {
	@Autowired
	IShiro shiro;

	/**
	 * 登录认证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken)
			throws AuthenticationException {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		ShiroUser shiroUser = shiro.getShiroAuthByUserAccount(token.getUsername());
		if (shiroUser == null || shiroUser.getUserId() == null) {
			throw new LockedAccountException();
		}

		// 重新拼装树
		List<ShiroMenu> menusSort = new ArrayList<>();

		getMenuChild(shiroUser.getAuthzs(), menusSort, "0");
		shiroUser.setMenus(menusSort);
		// 用户密码
		String userPassword = shiroUser.getUserPassword();
		// 加密随机字符串
		String salt = shiroUser.getUserSalt();
		// 账号
		String userAccount = shiroUser.getUserAccount();
		ByteSource credentialsSalt = new Md5Hash(salt);
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(shiroUser, userPassword, credentialsSalt,
				userAccount);

		return info;
	}

	/**
	 * 拼接菜单树
	 */
	private void getMenuChild(Set<ShiroAuthz> authzs, List<ShiroMenu> menusSort, String menuCode) {

		for (ShiroAuthz authz : authzs) {

			if (!"0".equals(authz.getAuthzType() + "")) {
				continue;
			}
			if (menuCode.equals(authz.getAuthzParentcode())) {
				List<ShiroMenu> menusSort2 = new ArrayList<ShiroMenu>();
				getMenuChild(authzs, menusSort2, authz.getAuthzCode());
				ShiroMenu shiroMenu = new ShiroMenu();
				shiroMenu.setMenuId(authz.getAuthzId());
				shiroMenu.setMenuName(authz.getAuthzName());
				shiroMenu.setMenuCode(authz.getAuthzCode());
				shiroMenu.setMenuParentCode(authz.getAuthzParentcode());
				shiroMenu.setMenuIcon(authz.getAuthzIcon());
				shiroMenu.setMenuUrl(authz.getAuthzUrl());
				shiroMenu.setMenuChild(menusSort2);
				menusSort.add(shiroMenu);
			}
		}
		// 排序
		Collections.sort(menusSort); 
	}

	/**
	 * 权限认证
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		ShiroUser shiroUser = (ShiroUser) principals.getPrimaryPrincipal();

		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

		Set<String> permissions = new HashSet<>();

		Set<String> roles = new HashSet<>();
		for (ShiroAuthz authz : shiroUser.getAuthzs()) {
			permissions.add(authz.getAuthzUrl());
			permissions.add(authz.getAuthzCode());
		}
		for (ShiroRole shiroRole : shiroUser.getRoles()) {
			roles.add(shiroRole.getRoleName());
			roles.add(shiroRole.getRoleCode());
		}
		info.addStringPermissions(permissions);
		info.addRoles(roles);
		return info;
	}

	/**
	 * 设置认证加密方式
	 */
	@Override
	public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
		HashedCredentialsMatcher md5CredentialsMatcher = new HashedCredentialsMatcher();
		// 指定加密算法
		md5CredentialsMatcher.setHashAlgorithmName(ShiroKit.HASH_ALGORITHM_NAME);
		md5CredentialsMatcher.setHashIterations(ShiroKit.HASH_ITERATIONS);
		super.setCredentialsMatcher(md5CredentialsMatcher);
	}
}
