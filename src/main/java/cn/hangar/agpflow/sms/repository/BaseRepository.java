package cn.hangar.agpflow.sms.repository;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hangar.util.StringUtils;
import cn.hangar.util.data.PropertyEntity;
import cn.hangar.util.db.IDataBase;
import cn.hangar.util.db.bean.SimpleBeanHandler;
import cn.hangar.util.db.dialect.OracleDialect;
import cn.hangar.util.db.dialect.SQLServerDialect;
import cn.hangar.util.db.repository.Repository;

/**
 * @author Eoge E-mail:18802012501@139.com
 * @version 创建时间：2017年9月21日 下午5:09:41
 *          类说明
 */
public class BaseRepository<T>  implements Repository<T> {

    public BaseRepository(IDataBase db) {
        this.db = db;
    }
    
    public void setDb(IDataBase db)
    {
    	this.db = db;
    }
    
    protected IDataBase db;

    public IDataBase getDb() {
        return db;
    }

    public void executeNonQueryBatchSql(String batchSql) throws Exception {
        db.executeBatch(batchSql, null);
    }
    
    private String tranceSql(String sql)
    {
    	return sql;
    }

    public int executeNonQuerySql(String sql) throws Exception {
    	sql = tranceSql(sql);
        return db.executeUpdate(sql, null);
    }

    public Object executeScalar(String sql) throws Exception {
    	sql = tranceSql(sql);
        return db.executeScalar(sql, null);
    }
    
    public Object executeScalar(String sql, Map<String, Object> condition) throws Exception {
    	sql = tranceSql(sql);
        return db.executeScalar(sql, condition);
    }
    
    public int executeUpdate(String sql, Map<String, Object> condition) throws Exception {
    	sql = tranceSql(sql);
        return db.executeUpdate(sql, condition);
    }
    
    public int update(String tableName, Map<String, Object> fields, Map<String, Object> condition,
            String conditionParamPrefix) throws Exception {
    	return update(tableName,fields,condition,conditionParamPrefix,0);
    }

    public int update(String tableName, Map<String, Object> fields, Map<String, Object> condition,
                      String conditionParamPrefix,int timeOut) throws Exception {

        String fieldSql = "";
        boolean isFirst = true;
        for (Map.Entry<String, Object> item : fields.entrySet()) {
            if (!isFirst) {
                fieldSql += ", ";
            }
            fieldSql += item.getKey() + " = " + db.buildParamHolder(item.getKey());
            isFirst = false;
        }

        String whereSql = "";
        isFirst = true;
        for (Map.Entry<String, Object> item : condition.entrySet()) {
            if (!isFirst) {
                whereSql += " and ";
            }
            whereSql += item.getKey() + " = "
                    + db.buildParamHolder((conditionParamPrefix == null ? "" : conditionParamPrefix) + item.getKey());
            isFirst = false;
        }
        Map<String, Object> pars = new HashMap<String, Object>();
        pars.putAll(fields);
		for (Map.Entry<String, Object> en : condition.entrySet()) {
			pars.put((conditionParamPrefix == null ? "" : conditionParamPrefix)+en.getKey(), en.getValue());
		}
        String sql = StringUtils.Format("update {0} set {1} where {2}", tableName, fieldSql, whereSql);
        return db.executeUpdate(sql, pars,timeOut);
    }

    public String buildUpdateFieldSql(Map<String, Object> fields) {
        return buildFieldsSql(fields, ", ", null);
    }

    public String buildWhereFieldSql(Map<String, Object> fields) {
        return buildFieldsSql(fields, " and ", null);
    }

    public String buildUpdateFieldSql(Map<String, Object> fields, String alias) {
        return buildFieldsSql(fields, ", ", alias);
    }

    public String buildWhereFieldSql(Map<String, Object> fields, String alias) {
        return buildFieldsSql(fields, " and ", alias);
    }

    public String buildFieldsSql(Map<String, Object> fields, String seperator, String alias) {
        String fieldSql = "";
        boolean isAlias = !StringUtils.isEmpty(alias);
        boolean isFirst = true;
        for (Map.Entry<String, Object> item : fields.entrySet()) {
            if (!isFirst) {
                fieldSql += seperator;
            }
            fieldSql += (isAlias ? alias + "." : "") + item.getKey() + " = " + db.buildParamHolder(item.getKey());
            isFirst = false;
        }

        return fieldSql;
    }

    public <T> String buildInSql(T[] values) {
        return buildInSql(false, values);
    }

    public <T> String buildInSql(boolean isString, T[] values) {
        if (isString) {
            String sql = "";
            boolean isFirst = true;
            for (T item : values) {
                if (item == null)
                    continue;

                if (!isFirst) sql += ",";

                sql += "'" + safeSql(item.toString()) + "'";

                isFirst = false;
            }
            return sql;
        }

        return StringUtils.join(", ", values);
    }

    public String safeSql(String sql) {
        if (StringUtils.isEmpty(sql))
            return "";

        return sql.replace("'", "''");
    }

    public boolean checkDbTableExist(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return false;
        }
        String tableExistSql = StringUtils.Format("select 1 from {0}", tableName);

        try {
            db.executeUpdate(tableExistSql, null);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public String buildParamHolder(String name) {
        return this.getDb().buildParamHolder(name);
    }

    @Override
    public void insert(T entity) throws Exception{
        this.getDb().insert(entity);
    }
    
    public int insert(List entity) throws Exception{
    	int i=0;
    	for(Object obj:entity)
    	{
    		i+=this.getDb().insert(obj);
    	}
    	return i;
    }

    @Override
    public void insert(T entity, String tableName) throws Exception{
        this.getDb().insert(entity, tableName);
    }

    public int update(List entity) throws Exception{
    	int i=0;
    	for(Object obj:entity)
    	{
    		i+=this.getDb().update(obj);
    	}
    	return i;
    }
    
    public int update(List<T> entity,List<String> fields) throws Exception{
    	int i=0;
    	for(T obj:entity)
    	{
    		i+= updateFields(obj,fields);
    	}
    	return i;
    }
    
    @Override
    public int update(T entity) throws Exception{
        return this.getDb().update(entity);
    }
    
    public int updateFields(T entity,List<String> fields) throws Exception{
    	if(fields==null||fields.isEmpty())
    		return  this.getDb().update(entity);
    	Map<String, Object> fds =  SimpleBeanHandler.instance().entityToMap(entity, null, fields);
    	Map<String, Object> condition = new HashMap<String, Object>();
    	String keyfield =  SimpleBeanHandler.instance().getKeyName(entity.getClass());
    	Object id = SimpleBeanHandler.instance().getId(entity);
    	condition.put(keyfield, id);
        return update((Class<T>)entity.getClass(),fds,condition);
    }


    @Override
    public int update(T entity, String tableName) throws Exception{
        return this.getDb().update(entity, tableName);
    }

    public int update(Class<T> c, Map<String, Object> fields, Map<String, Object> condition) throws Exception {
        return update(c, fields, condition, null);
    }

    public int update(Class<T> c, Map<String, Object> fields, Map<String, Object> condition,
                      String conditionParamPrefix) throws Exception {
        String tableName = PropertyEntity.getTableName(c);
        return update(tableName, fields, condition, conditionParamPrefix);
    }
    
	public int update(Class<T> c, Map<String, Object> fields, Map<String, Object> condition,
			String conditionParamPrefix,int timeOut) throws Exception {
		String tableName = PropertyEntity.getTableName(c);
		return update(tableName, fields, condition, conditionParamPrefix,timeOut);
	}

    public int delete(List entity) throws Exception{
    	int i=0;
    	for(Object obj:entity)
    	{
    		i+=this.getDb().delete(obj);
    	}
    	return i;
    }
    
    @Override
    public int delete(T entity) throws Exception {
        return this.getDb().delete(entity);
    }

    @Override
    public <T1> int delete(Class<T1> c, Object id) throws Exception {
        return this.getDb().deleteById(c, id);
    }

    @Override
    public <T1> int delete(Class<T1> c, Map<String, Object> condition) throws Exception {
        return delete(PropertyEntity.getTableName(c), condition);
    }

    @Override
    public int delete(String tableName, Map<String, Object> condition) throws Exception {
        String whereSql = "";
        boolean isFirst = true;
        for (Map.Entry<String, Object> item : condition.entrySet()) {
            if (!isFirst) {
                whereSql += " and ";
            }
            whereSql += item.getKey() + " = " + db.buildParamHolder(item.getKey());
            isFirst = false;
        }

        String sql = StringUtils.Format("delete from {0} where {1}", tableName, whereSql);
        return db.executeUpdate(sql, condition);
    }

    @Override
    public <T1> T1 getById(Class<T1> c, Object id){
        try {
			return this.getDb().findFirst(c, id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    @Override
    public <T1> T1 getById(Class<T1> c,String table, Object id){
        try {
			return this.getDb().findFirstByTable(c,table, id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }

    @Override
    public <T1> List<T1> getAll(Class<T1> c) throws Exception {
        return this.getDb().findAll(c);
    }

    @Override
    public long countAll(Class<T> c) throws Exception {
        return this.getDb().count(c);
    }

    public <T1> List<T1> executeSelectList(Class<T1> c, Map<String, Object> arg) throws Exception {
        return executeSelectList(c, arg, null, null,false);
    }
    
    public <T1> List<T1> executeSelectList(Class<T1> c, Map<String, Object> arg,boolean withlock) throws Exception {
        return executeSelectList(c, arg, null, null,withlock);
    }

    public <T1> List<T1> executeSelectList(Class<T1> c, Map<String, Object> arg, String orderBy) throws Exception {
        return executeSelectList(c, arg, null, orderBy,false);
    }

    public <T1> List<T1> executeSelectList(Class<T1> c, Map<String, Object> arg, List<String> fieldList, String orderBy,boolean withlock) throws Exception {
        String tableName = PropertyEntity.getTableName(c);
        return executeSelectList(c, tableName, arg, fieldList, orderBy,withlock);
    }

    public <T1> T1 executeSelect(Class<T1> c, Map<String, Object> arg) throws Exception {
        String tableName = PropertyEntity.getTableName(c);
        return executeSelect(c, tableName, arg, null, null);
    }
    
    public <T1> T1 executeSelect(Class<T1> c, Map<String, Object> arg,List<String> fieldList) throws Exception {
        String tableName = PropertyEntity.getTableName(c);
        return executeSelect(c, tableName, arg, fieldList, null);
    }

    public <T1> T1 executeSelect(Class<T1> c, String tableName, Map<String, Object> arg) throws Exception {
        return executeSelect(c, tableName, arg, null, null);
    }

    public <T1> T1 executeSelect(Class<T1> c, String tableName, Map<String, Object> arg, List<String> fieldList, String orderBy) throws Exception {
        List<T1> list = executeSelectList(c, tableName, arg, fieldList, orderBy,false);
        return list == null || list.size() < 1 ? null : list.get(0);
    }

    public <T1> List<T1> executeSelectList(Class<T1> c, String tableName, Map<String, Object> arg) throws Exception {
        return executeSelectList(c, tableName, arg, null, null,false);
    }

    public <T1> List<T1> executeSelectList(Class<T1> c, String tableName, Map<String, Object> arg, List<String> fieldList, String orderBy,boolean withlock) throws Exception {
        String fieldSql = "*";
        if (fieldList != null && fieldList.size() > 0) {
            fieldSql = StringUtils.join(",", fieldList);
        }
        String whereSql = arg==null||arg.isEmpty()?"":"where ";
        boolean isFirst = true;
		if (arg != null)
			for (Map.Entry<String, Object> item : arg.entrySet()) {
				if (!isFirst) {
					whereSql += " and ";
				}
				whereSql += item.getKey() + " = " + db.buildParamHolder(item.getKey());
				isFirst = false;
			}
		if(db.getDialect()!=null&&db.getDialect().getClass().equals(SQLServerDialect.class)&&withlock)
			tableName = tableName+" With (NoLock) ";
        String sql = StringUtils.Format("select {0} from {1} {2} {3}", fieldSql, tableName, whereSql,
                StringUtils.isEmpty(orderBy) ? "" : "order by " + orderBy);
        
        return executeSelectSqlList(sql, arg, c);
    }
    
    public List<Map> executeSelectMap(String tableName, Map<String, Object> arg, List<String> fieldList, String orderBy) {
        String fieldSql = "*";
        if (fieldList != null && fieldList.size() > 0) {
            fieldSql = StringUtils.join(",", fieldList);
        }
        String whereSql = arg==null||arg.isEmpty()?"":"where ";
        boolean isFirst = true;
		if (arg != null)
			for (Map.Entry<String, Object> item : arg.entrySet()) {
				if (!isFirst) {
					whereSql += " and ";
				}
				whereSql += item.getKey() + " = " + db.buildParamHolder(item.getKey());
				isFirst = false;
			}

        String sql = StringUtils.Format("select {0} from {1} {2} {3}", fieldSql, tableName, whereSql,
                StringUtils.isEmpty(orderBy) ? "" : "order by " + orderBy);
        return executeSelectMap(sql, arg);
    }

    public <T1> T1 executeSelectSql(String sql, Map<String, Object> condition, Class<T1> c) throws Exception {
        return db.selectOne(sql, condition, c);
    }

    public <T1> List<T1> executeSelectSqlList(String sql, Map<String, Object> condition, Class<T1> c) throws Exception {
        return db.select(sql, condition, c);
    }
    

    public List<Map> executeSelectMap(String sql, Map<String, Object> params)
    {
    	return db.selectMap(sql, params);
    }
    
    public List executeSelectObjs(String sql, Map<String, Object> params) throws Exception
    {
    	return db.selectObjs(sql, params);
    }
    
    
    public <T1> boolean exist(T1 t) throws Exception 
    {
    	 return db.exist(t);
    }
    
    public <T1> boolean exist(Class<T1> c, Map<String, Object> param) throws Exception {
    	 String tableName = PropertyEntity.getTableName(c);
    	 List<String> fields = new ArrayList<String>();
    	 fields.add("1");
    	 List<Map> objs = executeSelectMap(tableName, param,fields,null);
        return objs != null&&objs.size()>0;
    }

    public boolean exist(String sql, Map<String, Object> param) throws Exception {
        Object obj = db.selectObjOne(sql, param);
        return obj != null;
    }

}
