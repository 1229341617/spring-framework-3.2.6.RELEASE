/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.framework.autoproxy;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.Advisor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

/**
 * Helper for retrieving standard Spring Advisors from a BeanFactory,
 * for use with auto-proxying.
 *
 * @author Juergen Hoeller
 * @since 2.0.2
 * @see AbstractAdvisorAutoProxyCreator
 */
public class BeanFactoryAdvisorRetrievalHelper {

	private static final Log logger = LogFactory.getLog(BeanFactoryAdvisorRetrievalHelper.class);

	private final ConfigurableListableBeanFactory beanFactory;

	private String[] cachedAdvisorBeanNames;


	/**
	 * Create a new BeanFactoryAdvisorRetrievalHelper for the given BeanFactory.
	 * @param beanFactory the ListableBeanFactory to scan
	 */
	public BeanFactoryAdvisorRetrievalHelper(ConfigurableListableBeanFactory beanFactory) {
		Assert.notNull(beanFactory, "ListableBeanFactory must not be null");
		this.beanFactory = beanFactory;
	}


	/**
	 * Find all eligible Advisor beans in the current bean factory,
	 * ignoring FactoryBeans and excluding beans that are currently in creation.
	 * @return the list of {@link org.springframework.aop.Advisor} beans
	 * @see #isEligibleBean
	 */
	public List<Advisor> findAdvisorBeans() {
		// Determine list of advisor bean names, if not cached already.
		String[] advisorNames = null;
		synchronized (this) {
			//先从缓存中寻找是否含有所有增强器对应beanname
			advisorNames = this.cachedAdvisorBeanNames;
			//如果没有就重新寻找
			if (advisorNames == null) {
				// Do not initialize FactoryBeans here: We need to leave all regular beans
				// uninitialized to let the auto-proxy creator apply to them!
				//这里不需要初始化工厂bean，我们需要找到没有初始化的所有bean，然后让自动代理创建器去应用它们
				//这里其实也是通过getBeanNamesForType，根据bean的类型获取所有Advisor.class类型的增强Bean名称
				//这里也会将BeanFactoryTransactionAttributeAdvisor给一起找出来，因为它也是实现了Advisor接口的
				advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
						this.beanFactory, Advisor.class, true, false);
				this.cachedAdvisorBeanNames = advisorNames;
			}
		}
		if (advisorNames.length == 0) {
			return new LinkedList<Advisor>();
		}

		List<Advisor> advisors = new LinkedList<Advisor>();
		//遍历得到所有增强的beanName，找到对应的bean并添加到增强集合中
		for (String name : advisorNames) {
			//默认返回true，子类可以覆写该方法定义是否合格的标准
			if (isEligibleBean(name)) {
				//如果当前的beanName对应的bean还正在实例化当中，当然就不能得到它了
				if (this.beanFactory.isCurrentlyInCreation(name)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Skipping currently created advisor '" + name + "'");
					}
				}
				else {
					try {
						//已经知道了代理类的beanName，得到对应的bean就很简单了，调用方法getBean(String,Class<?> requiredType)方法即可
						advisors.add(this.beanFactory.getBean(name, Advisor.class));
					}
					catch (BeanCreationException ex) {
						Throwable rootCause = ex.getMostSpecificCause();
						if (rootCause instanceof BeanCurrentlyInCreationException) {
							BeanCreationException bce = (BeanCreationException) rootCause;
							if (this.beanFactory.isCurrentlyInCreation(bce.getBeanName())) {
								if (logger.isDebugEnabled()) {
									logger.debug("Skipping advisor '" + name +
											"' with dependency on currently created bean: " + ex.getMessage());
								}
								// Ignore: indicates a reference back to the bean we're trying to advise.
								// We want to find advisors other than the currently created bean itself.
								continue;
							}
						}
						throw ex;
					}
				}
			}
		}
		return advisors;
	}

	/**
	 * Determine whether the aspect bean with the given name is eligible.
	 * <p>The default implementation always returns {@code true}.
	 * @param beanName the name of the aspect bean
	 * @return whether the bean is eligible
	 */
	protected boolean isEligibleBean(String beanName) {
		return true;
	}

}
