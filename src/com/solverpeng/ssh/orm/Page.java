package com.solverpeng.ssh.orm;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * 与具体 ORM 实现无关的分页参数及查询结果封装 
 * 所有需要均从 1 开始
 * 
 * T: Page 中记录的类型
 *
 */
public class Page<T> {
	/**
	 * 公共变量
	 */
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	
	/**
	 * 分页参数
	 */
	protected int pageNo = 1;
	protected int pageSize = 5;
	protected String orderBy = null; //"departmentId, job_id, email"
	protected String order = null; //"asc, desc, desc"
	protected boolean autoCount = true; //是否需要设置总的记录数. 
	
	/**
	 * 返回结果
	 */
	protected List<T> result = Collections.emptyList();
	protected long totalCount = -1;
	
	/**
	 * 构造器
	 */
	public Page(){}
	
	public Page(int pageSize){
		setPageSize(pageSize);
	}

	/**
	 * 设置每页的记录数量, 低于 1 时自动调整为 1
	 * @param pageSize
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		
		if(pageSize < 1)
			this.pageSize = 1;
	}
	
	public Page(int pageSize, boolean autoCount){
		setPageSize(pageSize);
		setAutoCount(autoCount);
	}

	/**
	 * 查询对象时是否自动另外执行 count 查询获取总记录数, 默认值为 true
	 * @param autoCount
	 */
	public void setAutoCount(boolean autoCount) {
		this.autoCount = autoCount;
	}
	
	/**
	 * 获得当前页的页号, 序号从 1 开始, 默认为 1
	 * @return
	 */
	public int getPageNo(){
		return pageNo;
	}
	
	/**
	 * 设置当前页的页号, 序号从 1 开始, 低于 1 时自动调整为 1, 
	 * 此时不可能校验 pageNo 的合法性, 因为此时还没有查询数据库获取总的记录数 
	 * @param pageNo
	 */
	public void setPageNo(int pageNo){
		this.pageNo = pageNo;
		
		if(pageNo < 1)
			this.pageNo = 1;
	}
	
	/**
	 * 获得每页的记录数量, 默认值为 1
	 */
	public int getPageSize(){
		return pageSize;
	}
	
	/**
	 * 根据 pageNo 和 pageSize 计算当前页第一条记录在总结果集中的位置, 序号从 1 开始
	 * @return
	 */
	public int getFirst(){
		return (pageNo - 1) * pageSize + 1;
	}
	
	/**
	 * 获取排序字段, 无默认值. 多个排序字段用 ',' 分隔
	 * 例如: loginname, email
	 * @return
	 */
	public String getOrderBy(){
		return orderBy;
	}
	
	/**
	 * 设置排序字段.  多个排序字段用 ',' 分隔
	 * @param orderBy
	 */
	public void setOrderBy(String orderBy){
		this.orderBy = orderBy;
	}
	
	/**
	 * 获取排序方向
	 * @return
	 */
	public String getOrder(){
		return order;
	}
	
	/**
	 * 设置排序方向
	 * @param order: 可选值为 desc 或 asc, 多个排序字段时用 ',' 分隔
	 * 例如: desc, asc, desc
	 */
	public void setOrder(String order){
		String [] orders = StringUtils.split(StringUtils.lowerCase(order), ',');
		
		for(String orderStr: orders){
			if(!StringUtils.equals(DESC, orderStr) && !StringUtils.equals(ASC, orderStr)){
				throw new IllegalArgumentException("排序方向" + orderStr + "不是合法值");
			}
		}
		
		this.order = StringUtils.lowerCase(order);
	}
	
	/**
	 * 是否已设置排序字段, 无默认值
	 * @return
	 */
	public boolean isOrderBySetted(){
		return StringUtils.isNotBlank(orderBy) && StringUtils.isNotBlank(order);
	}
	
	/**
	 * 查询对象时是否自动另外执行 count 查询获取总记录数, 默认为 true
	 * @return
	 */
	public boolean isAutoCount(){
		return autoCount;
	}

	/**
	 * 获得页面内的记录列表
	 * @return
	 */
	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	/**
	 * 获得总记录数, 默认值为 -1
	 * @return
	 */
	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
		
		//重新检查 pageNo, 因为在 setPageNo 时, 还不能估算其是否完全合法
		long totalPages = getTotalPages();
		if(pageNo > totalPages)
			pageNo = (int)totalPages;
	}
	
	/**
	 * 根据 pageSize, totalCount 计算总页数, 默认值为 -1
	 * @return
	 */
	public long getTotalPages(){
		if(totalCount < 0)
			return -1;
		
		long count = totalCount / pageSize;
		if(totalCount % pageSize > 0){
			count++;
		}
		
		return count;
	}
	
	/**
	 * 是否还有下一页
	 * @return
	 */
	public boolean isHasNext(){
		return (pageNo + 1 <= getTotalPages());
	}
	
	/**
	 * 取得下页的页号, 序号从 1 开始.
	 * 当前页尾尾号时仍返回尾页序号
	 * @return
	 */
	public int getNextPage(){
		if(isHasNext())
			return pageNo + 1;
		else
			return pageNo;
	}
	
	/**
	 * 是否还有上一页
	 * @return
	 */
	public boolean isHasPre(){
		return (pageNo - 1 >= 1);
	}
	
	/**
	 * 取得上页的页码, 序号从 1 开始
	 * 当前页为首页时返回首页序号
	 * @return
	 */
	public int getPrePage(){
		if(isHasPre())
			return pageNo - 1;
		else
			return pageNo;
	}
	
	
}