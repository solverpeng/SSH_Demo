package com.solverpeng.ssh.dao.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.solverpeng.ssh.orm.Page;
import com.solverpeng.ssh.orm.PropertyFilter;
import com.solverpeng.ssh.orm.PropertyFilter.MatchType;
import com.solverpeng.ssh.utils.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.CriteriaImpl.OrderEntry;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.Assert;

/**
 * HibernateDao 泛型基类
 * <p>
 * 功能包括分页查询, 按属性过滤条件列表 在 Service 层直接使用, 也可以扩展泛型 DAO 子类使用
 * <p>
 * T: Dao 操作的对象类型 PK: 主键类型
 */
public class HibernateDao<T, PK extends Serializable> extends
        SimpleHibernateDao<T, Serializable> {
    /**
     * 用于 Dao 层子类使用的构造器 通过子类的泛型定义取得对象类型 Class
     */
    public HibernateDao() {
    }

    /**
     * 用于省略 Dao 层, Service 层直接使用通用的 HibernateDao 构造函数 在构造器中定义对象类型 Class
     */
    public HibernateDao(SessionFactory sessionFactory, Class<T> entityClass) {
        super(sessionFactory, entityClass);
    }

    /**
     * 判断对象的属性值在数据库中是否唯一. 在修改对象的情境下, 如果属性新修改的值(value)等于原来的值(orgValue), 则不作比较
     */
    public boolean isPropertyUnique(String propertyName, Object newValue,
                                    Object oldValue) {
        if (newValue == null || newValue.equals(oldValue))
            return true;

        Object object = findUniqueBy(propertyName, newValue);

        return object == null;
    }

    /**
     * 按属性条件创建 Criterion. 辅助函数
     */
    private Criterion buildPropertyFilterCriterion(String propertyName,
                                                   Object propertyValue, Class<?> propertyType, MatchType matchType) {

        Assert.hasText(propertyName, "propertyName 不能为空");

        Criterion criterion = null;

        try {

            if (MatchType.ISNULL.equals(matchType)) {
                criterion = Restrictions.isNull(propertyName);
                return criterion;
            }

            Object realValue = propertyValue;

            // 按 entity property 中的类型将字符串 转换为实际的类型
            try {
                realValue = ReflectionUtils.convertValue(propertyValue,
                        propertyType);
            } catch (Exception e) {
            }

            // 根据 MatchType 构造 Criterion 对象
            if (MatchType.EQ.equals(matchType)) {
                criterion = Restrictions.eq(propertyName, realValue);
            }
            if (MatchType.LIKE.equals(matchType)) {
                criterion = Restrictions.like(propertyName, (String) realValue,
                        MatchMode.ANYWHERE);
            }
            if (MatchType.LE.equals(matchType)) {
                criterion = Restrictions.le(propertyName, realValue);
            }
            if (MatchType.LT.equals(matchType)) {
                criterion = Restrictions.lt(propertyName, realValue);
            }
            if (MatchType.GE.equals(matchType)) {
                criterion = Restrictions.ge(propertyName, realValue);
            }
            if (MatchType.GT.equals(matchType)) {
                criterion = Restrictions.gt(propertyName, realValue);
            }
        } catch (Exception e) {
            // e.printStackTrace();
            throw ReflectionUtils.convertToUncheckedException(e);
        }

        return criterion;
    }

    /**
     * 按属性条件列表创建 Criterion 数组, 辅助函数
     */
    private Criterion[] buildPropertyFilterCriterions(
            List<PropertyFilter> filters) {
        List<Criterion> criterionList = new ArrayList<Criterion>();

        for (PropertyFilter filter : filters) {
            // 只有一个属性需要比较
            if (!filter.isMultiProperty()) {
                Criterion criterion = buildPropertyFilterCriterion(
                        filter.getPropertyName(), filter.getPropertyValue(),
                        filter.getPropertyType(), filter.getMatchType());
                criterionList.add(criterion);
            }

            // 包含多个属性需要比较的情况, 使用 OR 处理
            else {
                Disjunction disjunction = Restrictions.disjunction();

                for (String param : filter.getPropertyNames()) {
                    Criterion criterion = buildPropertyFilterCriterion(param,
                            filter.getPropertyValue(),
                            filter.getPropertyType(), filter.getMatchType());
                    disjunction.add(criterion);
                }

                criterionList.add(disjunction);
            }
        }

        return criterionList.toArray(new Criterion[criterionList.size()]);
    }

    public T findUnique(List<PropertyFilter> filters) {
        Criterion[] criterions = buildPropertyFilterCriterions(filters);
        return findUnique(criterions);
    }

    /**
     * 按属性过滤条件列表查找对象列表
     */
    public List<T> find(List<PropertyFilter> filters) {
        Criterion[] criterions = buildPropertyFilterCriterions(filters);
        return find(criterions);
    }

    /**
     * 按属性查找对象列表, 支持多种匹配方式
     * @param matchType    : 匹配方式
     */
    public List<T> findBy(String propertyName, Object value, MatchType matchType) {
        Criterion criterion = buildPropertyFilterCriterion(propertyName, value,
                value.getClass(), matchType);
        return find(criterion);
    }

    /**
     * 执行 count 查询, 获得本次 Criteria 查询所能获得的对象总数
     */
    public int countCriteriaResult(final Criteria criteria) {
        CriteriaImpl impl = (CriteriaImpl) criteria;

        // 先把 Projection, ResultTransformer, OrderBy 取出来, 清空三者后再执行 Count 操作
        Projection projection = impl.getProjection();
        ResultTransformer resultTransformer = impl.getResultTransformer();

        List<OrderEntry> orderEntries = null;
        try {
            orderEntries = (List<OrderEntry>) ReflectionUtils.getFieldValue(
                    impl, "orderEntries");
            ReflectionUtils
                    .setFieldValue(impl, "orderEntries", new ArrayList());
        } catch (Exception e) {
            logger.error("不可能抛出的异常: {}", e.getMessage());
        }

        // 执行 count 查询
        long totalCount = (Long) criteria.setProjection(Projections.rowCount())
                .uniqueResult();

        // 将之前的 Projection, ResultTransformer, orderBy 条件重新设回去
        criteria.setProjection(projection);
        // criteria.setResultTransformer(resultTransformer);

        if (projection == null)
            criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        if (resultTransformer != null)
            criteria.setResultTransformer(resultTransformer);

        try {
            ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
        } catch (Exception e) {
            logger.error("不可能抛出的异常: {}", e.getMessage());
        }

        return (int) totalCount;

    }

    /**
     * 自动处理简单的 hql 语句
     */
    public long countHqlResult(String hql, Map<String, Object> values) {
        long count = 0;

        String fromHql = hql;
        // Select 子句与 order by 子句会影响 count 查询, 进行简单的排除
        // hql = SELECT c.loginname, c.email FROM Employee e WHERE e.loginname
        // LIKE '%o%' ORDER BY e.gender

        fromHql = "FROM " + StringUtils.substringAfter(fromHql, "from");
        // FROM Employee e WHERE e.loginname LIKE '%o%' ORDER BY e.gender

        fromHql = StringUtils.substringBefore(fromHql, "order by");
        // FROM  Employee e WHERE e.loginname LIKE '%o%'

        String countHql = "SELECT count(*) " + fromHql;
        // SELECT count(*) FROM  Employee e WHERE e.loginname LIKE '%o%'

        try {
            count = findUnique(countHql, values);
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:"
                    + countHql, e);
        }

        return count;
    }

    /**
     * 自动处理简单的 hql 语句
     */
    public long countHqlResult(String hql, Object... values) {
        long count = 0;

        String fromHql = hql;
        // Select 子句与 order by 子句会影响 count 查询, 进行简单的排除
        fromHql = "FROM " + StringUtils.substringAfter(fromHql, "from");
        fromHql = StringUtils.substringBefore(fromHql, "order by");

        String countHql = "SELECT count(*) " + fromHql;

        try {
            count = findUnique(countHql, values);
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:"
                    + countHql, e);
        }

        return count;
    }

    /**
     * 设置分页参数到 Criteria 对象, 辅助函数
     */
    private Criteria setPageParameter(Criteria criteria, Page<T> page) {
        criteria.setFirstResult(page.getFirst() - 1);
        criteria.setMaxResults(page.getPageSize());

        // 以下代码用于设置排序
        if (page.isOrderBySetted()) {
            String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
            String[] orderArray = StringUtils.split(page.getOrder(), ',');

            Assert.isTrue(orderArray.length == orderArray.length,
                    "分页多重排序参数中, 排序字段与排序方向的个数不相等");

            for (int i = 0; i < orderArray.length; i++) {
                if (Page.ASC.equals(orderArray[i])) {
                    criteria.addOrder(Order.asc(orderByArray[i]));
                } else {
                    criteria.addOrder(Order.desc(orderByArray[i]));
                }
            }
        }

        return criteria;
    }

    /**
     * 设置分页参数到 Query 对象, 辅助函数
     */
    private Query setPageParameter(Query query, Page<T> page) {
        query.setFirstResult(page.getFirst() - 1);
        query.setMaxResults(page.getPageSize());

        return query;
    }

    /**
     * 按 Criteria 分页查询
     * @param page       : 分页参数
     * @param criterions : 数量可变的 Criterion
     * @return: 分页查询结果. 附带结果列表及所有查询时的参数
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Page<T> findPage(Page<T> page, Criterion... criterions) {
        Assert.notNull(page, "page 不能为空");

        Criteria criteria = createCriteria(criterions);

        if (page.isAutoCount()) {
            int totalCount = countCriteriaResult(criteria);
            // 第一次使用 Page 对象
            page.setTotalCount(totalCount);
        }

        setPageParameter(criteria, page);
        List result = criteria.list();
        page.setResult(result);

        return page;
    }

    /**
     * 按属性过滤条件列表分页查找对象
     */
    public Page<T> findPage(final Page<T> page,
                            final List<PropertyFilter> filters) {
        Criterion[] criterions = buildPropertyFilterCriterions(filters);

        return findPage(page, criterions);
    }

    public Page<T> findPage(final Page<T> page,
                            final String propertyName, final Object propertyValue) {
        Criterion criterion = Restrictions.eq(propertyName, propertyValue);
        return findPage(page, criterion);
    }

    /**
     * 按 HQL 分页查询
     *
     * @param page   : 分页参数
     * @param hql    : hql 语句
     * @param values : 命名参数, 按名称绑定
     * @return: 分页查询结果. 附带结果列表及所有查询时的参数
     */
    public Page<T> findPage(Page<T> page, String hql, Map<String, Object> values) {
        Assert.notNull(page, "page 不能为空");

        Query query = createQuery(hql, values);

        if (page.isAutoCount()) {
            long totalCount = countHqlResult(hql, values);
            page.setTotalCount(totalCount);
        }

        setPageParameter(query, page);
        List result = query.list();
        page.setResult(result);

        return page;
    }

    /**
     * 按 HQL 分页查询
     *
     * @param page   : 分页参数
     * @param hql    : hql 语句
     * @param values : 命名参数, 按名称绑定
     * @return: 分页查询结果. 附带结果列表及所有查询时的参数
     */
    public Page<T> findPage(Page<T> page, String hql, Object... values) {
        Assert.notNull(page, "page 不能为空");

        Query query = createQuery(hql, values);

        if (page.isAutoCount()) {
            long totalCount = countHqlResult(hql, values);
            page.setTotalCount(totalCount);
        }

        setPageParameter(query, page);
        List result = query.list();
        page.setResult(result);

        return page;
    }

    /**
     * 获取全部对象
     */
    public Page<T> getAll(Page<T> page) {
        return findPage(page);
    }

}