package com.lzb.rock.base.factory;

import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lzb.rock.base.model.PageReq;
import com.lzb.rock.base.util.UtilHttpKit;
import com.lzb.rock.base.util.UtilString;

/**
 * 
 * @author lzb
 *
 * @param <T> 2019年4月1日 下午8:00:51
 */
public class PageFactory<T> {

	public Page<T> defaultPage() {
		HttpServletRequest request = UtilHttpKit.getRequest();
		int limit = 30;// 每页多少条数据
		int pageNum = 1;// 当前页

		String limitStr = request.getParameter("limit");
		String pageStr = request.getParameter("page");
		if (UtilString.isNotBlank(limitStr) && UtilString.isNumeric(limitStr)) {
			limit = Integer.valueOf(limitStr);
		}
		if (UtilString.isNotBlank(pageStr) && UtilString.isNumeric(pageStr)) {
			pageNum = Integer.valueOf(pageStr);
		}

		Page<T> page = new Page<>(pageNum, limit);
		return page;
	}

	/**
	 * 
	 * @param limit  每页多少条数据
	 * @param offset 每页的偏移量(本页当前有多少条)
	 * @param sort   排序字段名称
	 * @param order  asc或desc(升序或降序)
	 * @return
	 */
	public Page<T> defaultPage(Integer limit, Integer pageNum) {
		if (limit == null) {
			limit = 30;
		}

		if (pageNum == null) {
			pageNum = 1;
		}
		Page<T> page = new Page<>(pageNum, limit);
		return page;
	}

	public Page<T> defaultPage(PageReq req) {
		if (req.getPage() == null) {
			req.setPage((req.getOffset() / req.getLimit()) + 1);
		}

		Page<T> page = new Page<>(req.getPage(), req.getLimit());
		return page;
	}
}
