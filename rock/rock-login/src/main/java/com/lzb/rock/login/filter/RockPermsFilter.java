/**
 * @author lzb
 *
 * 
 *2019年5月26日 上午11:10:21
 */
package com.lzb.rock.login.filter;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author lzb
 * 
 *         2019年5月26日 上午11:10:21
 */
@Slf4j
public class RockPermsFilter extends PermissionsAuthorizationFilter {
	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
		log.debug("BusAnonFilter     ===============>");

		return super.isAccessAllowed(request, response, mappedValue);
	}
}
