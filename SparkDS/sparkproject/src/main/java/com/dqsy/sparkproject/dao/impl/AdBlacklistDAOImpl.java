package com.dqsy.sparkproject.dao.impl;

import com.dqsy.sparkproject.dao.IAdBlacklistDAO;
import com.dqsy.sparkproject.domain.AdBlacklist;
import com.dqsy.sparkproject.util.DBCPUtil;
import com.dqsy.sparkproject.util.DateUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ArrayListHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 广告黑名单DAO实现类
 *
 * @author liusinan
 */
public class AdBlacklistDAOImpl implements IAdBlacklistDAO {

    private QueryRunner qr = new QueryRunner(DBCPUtil.getDataSource());

    /**
     * 批量插入广告黑名单用户
     *
     * @param adBlacklists
     */
    @Override
    public void insertBatch(List<AdBlacklist> adBlacklists) {
        try {
            /*假如有两个用户A和B在不同的地方插入相同的记录。
            插入步骤分成这样
            1：查询记录数
            2：判断记录个数
            3：执行编辑或者插入
            如果A用户执行了1，2，但没有进行插入。这时A用户的程序停顿了一下下，由B用户立刻执行这无了1，2，3步骤。
            这时A再执行3，就相当于把这条记录插入了两次。*/

            //步骤：
            //①准备容器，用于存储新增的黑名单信息
            List<AdBlacklist> insertContainer = new LinkedList<>();

            //②填充容器
            List<AdBlacklist> oldBlackList = findAll();

            //将旧的黑名单列表与待分析的容器中的数据进行比对，若不存在，证明是新增的黑名单，将其添加到容器中即可
            for (AdBlacklist bean : adBlacklists) {
                if (!oldBlackList.contains(bean)) {
                    insertContainer.add(bean);
                }
            }

            //将新的黑名单批量保存到表中
            String sql = "insert into advertisement_blacklist values(?,?)";
            Object[][] params = new Object[insertContainer.size()][];
            for (int i = 0; i < params.length; i++) {
                AdBlacklist bean = insertContainer.get(i);
                params[i] = new Object[]{bean.getUserid(), DateUtils.getTodayDate()};
            }
            qr.batch(sql, params);

        } catch (SQLException e) {

        }

    }

    /**
     * 查询所有广告黑名单用户
     *
     * @return
     */
    @Override
    public List<AdBlacklist> findAll() {
        try {
            String sql1 = "select user_id from advertisement_blacklist";

            List<Object[]> tmp = qr.query(sql1, new ArrayListHandler());
            List<AdBlacklist> oldBlackList = new ArrayList<>();
            for (Object[] obj : tmp) {
                for (int i = 0; i < obj.length; i++) {
                    oldBlackList.add(new AdBlacklist(Long.valueOf(obj[i].toString())));
                }
            }

            return oldBlackList;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("有黑名单信息发生异常");
        }

    }

}
