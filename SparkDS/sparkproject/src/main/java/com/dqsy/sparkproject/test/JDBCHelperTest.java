package com.dqsy.sparkproject.test;

import com.dqsy.sparkproject.domain.AdBlacklist;
import com.dqsy.sparkproject.util.DBCPUtil;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import java.util.*;

/**
 * JDBC辅助组件测试类
 *
 * @author Administrator
 */
public class JDBCHelperTest {

    public static void main(String[] args) throws Exception {
        // 获取JDBCHelper的单例
//        JDBCUtil jdbcHelper = JDBCUtil.getInstance();

        /*String str = "1";
        for (int i = 2; i < 101; i++) {
            str = str + "," + i;
        }
        System.out.println(str);*/

        // 测试普通的增删改语句
//		jdbcHelper.executeUpdate(
//				"insert into test_user(name,age) values(?,?)",
//				new Object[]{"王二", 28});

        // 测试查询语句
//		final Map<String, Object> testUser = new HashMap<String, Object>();
//		// 设计一个内部接口QueryCallback
//		// 那么在执行查询语句的时候，我们就可以封装和指定自己的查询结果的处理逻辑
//		// 封装在一个内部接口的匿名内部类对象中，传入JDBCHelper的方法
//		// 在方法内部，可以回调我们定义的逻辑，处理查询结果
//		// 并将查询结果，放入外部的变量中
//		jdbcHelper.executeQuery(
//				"select name,age from test_user where id=?",
//				new Object[]{12},
//				new JDBCUtil.QueryCallback() {
//
//					@Override
//					public void process(ResultSet rs) throws Exception {
//						if(rs.next()) {
//							String name = rs.getString(1);
//							int age = rs.getInt(2);
//
//							// 匿名内部类的使用，有一个很重要的知识点
//							// 如果要访问外部类中的一些成员，比如方法内的局部变量
//							// 那么，必须将局部变量，声明为final类型，才可以访问
//							// 否则是访问不了的
//							testUser.put("name", name);
//							testUser.put("age", age);
//						}
//					}
//				});
//		System.out.println(testUser.get("name") + ":" + testUser.get("age"));

        // 测试批量执行SQL语句
        /*String sessionid = UUID.randomUUID().toString().replace("-", "");
        String sql = "update user_visit_copy set session_id=?,page_id=?,action_time,search_keyword=?,click_category_id=?,click_product_id=?,order_category_ids=?,order_product_ids=?,pay_category_ids=?,pay_product_ids=?,city_id=?";
        List<Object[]> paramsList = new ArrayList<Object[]>();
        paramsList.add(new Object[]{"麻子", 30});
        paramsList.add(new Object[]{"王五", 35});

        jdbcHelper.executeBatch(sql, paramsList);*/

        /*Random random = new Random();
        for (int i = 0; i < 3700; i++) {
            System.out.println("{\"product_status\":" + random.nextInt(2) + "}");
        }*/
        /*Random random = new Random();

        String log = new Date().getTime() + " " + "北京市" + " " + "北京市" + " "
                + random.nextInt(100) + " " + random.nextInt(10);
        System.out.println(log);*/

//        System.out.println();


       /* String str = "'0'";
        for (int i = 1; i < 60; i++) {
            str += ",'" + i + "'";
        }
        System.out.println(str);*/

//        String s = "";
        /*for (int i = 0; i <= 23; i++) {*/
//        int k = 1;
        /*for (int i = 0; i <= 23; i++) {
            for (int j = 0; j <= 59; j++) {
                s += "[" + "datas[" + i + "].hour" + "," + "datas[" + j + "].minute" + "," + "datas[" + k++ + "].clickCount" + "],";
            }
        }*/
        /*for (int i = 0; i < 1440; i++) {
            s += "[" + "datas[" + i + "].hour" + "," + "datas[" + i + "].minute" + "," + "datas[" + i + "].clickCount" + "],\n";
        }*/

        /*for (int i = 0; i <= 23; i++) {
            for (int j = 0; j <= 59; j++) {
                s += "[" + i + "," + j + "," +  "3],";
            }
        }*/

//        System.out.println(s);

        /*System.out.println(findAll().size());
        List<AdBlacklist> list = findAll();
        for (AdBlacklist adBlacklist : list) {
            System.out.println(adBlacklist.getUserid());
        }*/


        /*IAdBlacklistDAO adBlacklistDAO = DAOFactory.getAdBlacklistDAO();
        List<AdBlacklist> adBlacklists = adBlacklistDAO.findAll();*/

        String sql1 = "select user_id from ad_blacklist";
        //获得表中旧的黑名单信息

        /*List<AdBlacklistTmp> oldBlackListtmp = qr.query(sql1, new BeanListHandler<AdBlacklistTmp>(AdBlacklistTmp.class));
        List<AdBlacklist> oldBlackList = new LinkedList<>();
        for (AdBlacklistTmp adBlacklistTmp : oldBlackListtmp) {
            oldBlackList.add(new AdBlacklist(adBlacklistTmp.getUserid()));
        }*/

        //可以
        /*List<Object[]> oldBlackList = qr.query(sql1, new ArrayListHandler());
        List<AdBlacklist> res = new ArrayList<>();
        for (Object[] obj : oldBlackList) {
            for (int i = 0; i < obj.length; i++) {
                res.add(new AdBlacklist(Long.valueOf(obj[i].toString())));
            }
        }

        for (AdBlacklist adBlacklist : res) {
            System.out.println(adBlacklist.toString());
        }*/


        AdBlacklist a1 = new AdBlacklist();
        a1.setUserid(1);
        AdBlacklist a2 = new AdBlacklist();
        a2.setUserid(2);
        AdBlacklist a3 = new AdBlacklist();
        a3.setUserid(3);
        List<AdBlacklist> alinst1 = new LinkedList<>();
        alinst1.add(a1);
        alinst1.add(a2);
        alinst1.add(a3);

        AdBlacklist a4 = new AdBlacklist(1);
        AdBlacklist a5 = new AdBlacklist(2);
        List<AdBlacklist> old = new LinkedList<>();
        old.add(a4);
        old.add(a5);

        for (AdBlacklist adBlacklist : alinst1) {
            if (!old.contains(adBlacklist)){
                System.out.println(adBlacklist.getUserid());
            }
            System.out.println(old.contains(adBlacklist));
            System.out.println(adBlacklist.getUserid());
        }



    }

    private static QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

}
