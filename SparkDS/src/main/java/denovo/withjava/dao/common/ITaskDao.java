package denovo.withjava.dao.common;

import scala.Int;
import top.newforesee.bean.Task;

/**
 * creat by newforesee 2018/11/27
 */
public interface ITaskDao {
    Task findTaskById(int taskId);
}
