package com.dqsy.sparkproject.test;

import com.dqsy.sparkproject.dao.ITaskDAO;
import com.dqsy.sparkproject.dao.factory.DAOFactory;
import com.dqsy.sparkproject.domain.Task;

/**
 * 任务管理DAO测试类
 *
 * @author liusinan
 */
public class TaskDAOTest {

    public static void main(String[] args) {
        ITaskDAO taskDAO = DAOFactory.getTaskDAO();
        Task task = taskDAO.findById(4);
        System.out.println(task);
    }

}
