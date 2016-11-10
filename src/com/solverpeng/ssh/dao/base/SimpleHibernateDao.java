package com.solverpeng.ssh.dao.base;

import com.solverpeng.ssh.utils.ReflectionUtils;
import org.hibernate.*;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.source.annotations.entity.EntityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;


/**
 * 在 Service 层直接使用, 也可以扩展泛型 DAO 子类使用 T: Dao 操作的对象类型 PK: 主键类型
 */
class SimpleHibernateDao<T, PK extends Serializable> {

    Logger logger = LoggerFactory.getLogger(getClass());



    private Class<T> entityClass;

    /**
     * 用于 Dao 层子类使用的构造函数 通过子类的泛型定义取得对象类型 Class
     * <p>
     * 例如: public class UserDao extends SimpleHibernateDao<User, String>
     */
    SimpleHibernateDao() {
        this.entityClass = ReflectionUtils.getSuperGenericType(getClass());
    }

    /**
     * 用于省略 Dao 层, 在 Service 层直接使用通用 SimpleHibernateDao 的构造函数 在构造函数中定义对象类型 Class
     */
    SimpleHibernateDao(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    private SessionFactory sessionFactory;

    private SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 获取当前 Session
     */
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 保存新增或修改的对象
     */
    public void save(T entity) {
        Assert.notNull(entity, "entity 不能为空");
        getSession().saveOrUpdate(entity);
        logger.debug("save entity: {}", entity);
    }

    /**
     * 按 id 获取对象
     */
    private T get(PK id) {
        Assert.notNull(id, "id不能为空");
        return (T) getSession().get(entityClass, id);
    }

    /**
     * 根据 Criterion 条件创建 Criteria
     */
    Criteria createCriteria(Criterion... criterions) {
        // 创建一个 Criteria 对象
        Criteria criteria = getSession().createCriteria(entityClass);

        // 添加查询条件
        for (Criterion c : criterions) {
            criteria.add(c);
        }

        // 返回 Criteria
        return criteria;
    }

    /**
     * 按属性查找唯一对象, 匹配方式为相等
     */
    T findUniqueBy(String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName不能为空");
        Criterion criterion = Restrictions.eq(propertyName, value);
        return (T) createCriteria(criterion).uniqueResult();
    }

    /**
     * 按 Criteria 查询唯一对象
     * @param criterions : 数量可变的 Criterion
     */
    T findUnique(Criterion... criterions) {
        return (T) createCriteria(criterions).uniqueResult();
    }

    /**
     * 根据查询 HQL 与参数列表创建 Query 对象
     * @param values      : 数来那个可变的参数, 按顺序绑定
     */
    public Query createQuery(String queryString, Object... values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = getSession().createQuery(queryString);

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }

        return query;
    }

    /**
     * 按 HQL 查询唯一对象
     */
    public <X> X findUnique(String hql, Object... values) {
        return (X) createQuery(hql, values).uniqueResult();
    }

    /**
     * 根据查询 HQL 与参数列表创建 Query 对象
     *
     * @param queryString : FROM User u WHERE u.nickName = :nickname AND u.email =
     *                    :email
     * @param values      {nickname=aa, email=aa@163.com}
     */
    public Query createQuery(String queryString, Map<String, Object> values) {
        // String hql = "FROM Employee e where e.loginname = :loginname";
        Assert.hasText(queryString, "queryString不能为空");
        Query query = getSession().createQuery(queryString);

        if (values != null) {
            query.setProperties(values);
        }

        return query;
    }

    /**
     * 按 HQL 查询唯一对象
     */
    <X> X findUnique(String hql, Map<String, Object> values) {
        return (X) createQuery(hql, values).uniqueResult();
    }

    /**
     * 按 HQL 查询对象列表
     */
    public <X> List<X> find(String hql, Map<String, Object> values) {
        return createQuery(hql, values).list();
    }

    /**
     * 按 HQL 查询对象列表
     */
    public <X> List<X> find(String hql, Object... values) {
        return createQuery(hql, values).list();
    }

    /**
     * 按 Criteria 查询对象列表
     *
     * @param criterions : 数量可变的 Criterion
     */
    List<T> find(Criterion... criterions) {
        return createCriteria(criterions).list();
    }

    /**
     * 获取全部对象
     */
    public List<T> getAll() {
        return find();
    }

    /**
     * 按属性查找对象列表, 匹配方式为相等
     */
    public List<T> findBy(String propertyName, Object value) {
        Assert.hasText(propertyName, "propertyName不能为空");

        Criterion criterion = Restrictions.eq(propertyName, value);
        return find(criterion);
    }

    public T findById(Integer id) {
        Assert.notNull(id, "id 不能为空!");
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.add(Restrictions.eq("id", id));
        return (T) criteria.uniqueResult();
    }

    /**
     * 获取对象的主键名.
     */
    private String getIdName() {
        ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
        return meta.getIdentifierPropertyName();
    }

    /**
     * 按 id 列表获取对象列表
     */
    public List<T> findByIds(List<?> ids) {
        return find(Restrictions.in(getIdName(), ids));
    }

    /**
     * 删除对象
     * @param entity ： 持久化对象或"瞬态"对象
     */
    private void delete(T entity) {
        if (entity != null) {
            getSession().delete(entity);
            logger.debug("delete entity: {}", entity);
        }
    }

    public void delete(PK id) {
        Assert.notNull(id, "id 不能为空");
        delete(get(id));
        logger.debug("delete entity {},id is {}", entityClass.getSimpleName(),
                id);
    }

    /**
     * 执行 hql 进行批量修改/删除操作
     */
    public int batchExecute(String hql, Map<String, Object> values) {
        return createQuery(hql, values).executeUpdate();
    }

    /**
     * 执行 hql 进行批量修改/删除操作
     */
    public int batchExecute(String hql, Object... values) {
        return createQuery(hql, values).executeUpdate();
    }

    /**
     * 初始化对象. 使用 load() 方法得到的仅是对象的代理, 在传到视图层前需要进行初始化
     * <p>
     * 只初始化 entity 的直接属性, 但不会初始化延迟加载的关联集合和属性 如需初始化关联属性, 可执行:
     * Hibernate.initialize(user.getRoles());
     */
    public void initEntity(Object entity) {
        Hibernate.initialize(entity);
    }

    public void initEntity(Collection<?> entityList) {
        for (Object entity : entityList) {
            Hibernate.initialize(entity);
        }
    }

    /**
     * 通过 Set 将不唯一的对象列表唯一化 主要用于 HQL/Criteria 预加载关联集合形成重复记录, 又不方便使用 distinct
     * 查询语句时: 例如: 迫切左外连接
     */
    public <X> List<X> distinct(List list) {
        Set<X> set = new LinkedHashSet<X>(list);
        return new ArrayList<X>(set);
    }

    /**
     * 为 Criteria 添加 distinct transformer
     */
    public Criteria distinct(Criteria criteria) {
        criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return criteria;
    }

    /**
     * 为 Query 添加 distinct transformer
     */
    public Query distinct(Query query) {
        query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
        return query;
    }


}