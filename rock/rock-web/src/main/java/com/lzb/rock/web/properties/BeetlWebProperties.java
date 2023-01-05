package com.lzb.rock.web.properties;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.lzb.rock.base.util.UtilString;

import lombok.extern.slf4j.Slf4j;

/**
 * beetl配置(如果需要配置别的配置可参照这个形式自己添加)
 * 
 * @author lzb
 * @date 2019年7月28日 下午11:15:00
 */
@Configuration
@ConfigurationProperties(prefix = BeetlWebProperties.BEETLCONF_PREFIX)
@Slf4j
public class BeetlWebProperties {

	public static final String BEETLCONF_PREFIX = "beetl";

	/**
	 * beetl 脚本开始标签
	 */
	@Value("${delimiter-statement-start:<%}")
	private String delimiterStatementStart;
	/**
	 * beetl 脚本结束标签
	 */
	@Value("${delimiter-statement-end:%>}")
	private String delimiterStatementEnd;
	/**
	 * beetl 模板所在路径
	 */
	@Value("${resource-tagroot:common/tags}")
	private String resourceTagroot;

	/**
	 * beetl 模板后缀
	 */
	@Value("${resource-tagsuffix:tag}")
	private String resourceTagsuffix;
	/**
	 * 是否检测文件变化,开发用true合适，但线上要改为false
	 */
	@Value("${resource-auto-check:false}")
	private String resourceAutoCheck;
	/**
	 * 默认为true，集成beetl
	 */
	@Value("${beetl.enabled:true}")
	private String beetlEnabled;

	public Properties getProperties() {
		Properties properties = new Properties();
		if (UtilString.isNotEmpty(delimiterStatementStart)) {
			if (delimiterStatementStart.startsWith("\\")) {
				delimiterStatementStart = delimiterStatementStart.substring(1);
			}
			log.info("DELIMITER_STATEMENT_START:{}", delimiterStatementStart);
			properties.setProperty("DELIMITER_STATEMENT_START", delimiterStatementStart);
		}
		if (UtilString.isNotEmpty(delimiterStatementEnd)) {
			log.info("DELIMITER_STATEMENT_END:{}", delimiterStatementEnd);
			properties.setProperty("DELIMITER_STATEMENT_END", delimiterStatementEnd);
		} else {
			log.info("DELIMITER_STATEMENT_END:{}", "null");
			properties.setProperty("DELIMITER_STATEMENT_END", "null");
		}
		if (UtilString.isNotEmpty(resourceTagroot)) {
			log.info("RESOURCE.tagRoot:{}", resourceTagroot);
			properties.setProperty("RESOURCE.tagRoot", resourceTagroot);
		}
		if (UtilString.isNotEmpty(resourceTagsuffix)) {
			log.info("RESOURCE.tagSuffix:{}", resourceTagsuffix);
			properties.setProperty("RESOURCE.tagSuffix", resourceTagsuffix);
		}
		if (UtilString.isNotEmpty(resourceAutoCheck)) {
			log.info("RESOURCE.autoCheck:{}", resourceAutoCheck);
			properties.setProperty("RESOURCE.autoCheck", resourceAutoCheck);
		}
		if (UtilString.isNotEmpty(beetlEnabled)) {
			log.info("ENABLED:{}", beetlEnabled);
			properties.setProperty("ENABLED", resourceAutoCheck);
		}
		return properties;
	}

	public String getDelimiterStatementStart() {
		return delimiterStatementStart;
	}

	public void setDelimiterStatementStart(String delimiterStatementStart) {
		this.delimiterStatementStart = delimiterStatementStart;
	}

	public String getDelimiterStatementEnd() {
		return delimiterStatementEnd;
	}

	public void setDelimiterStatementEnd(String delimiterStatementEnd) {
		this.delimiterStatementEnd = delimiterStatementEnd;
	}

	public String getResourceTagroot() {
		return resourceTagroot;
	}

	public void setResourceTagroot(String resourceTagroot) {
		this.resourceTagroot = resourceTagroot;
	}

	public String getResourceTagsuffix() {
		return resourceTagsuffix;
	}

	public void setResourceTagsuffix(String resourceTagsuffix) {
		this.resourceTagsuffix = resourceTagsuffix;
	}

	public String getResourceAutoCheck() {
		return resourceAutoCheck;
	}

	public void setResourceAutoCheck(String resourceAutoCheck) {
		this.resourceAutoCheck = resourceAutoCheck;
	}
}
