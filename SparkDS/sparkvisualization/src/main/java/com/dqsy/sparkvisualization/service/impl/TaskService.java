package com.dqsy.sparkvisualization.service.impl;

import com.dqsy.sparkvisualization.entity.Task;
import com.dqsy.sparkvisualization.mapper.TaskMapper;
import com.dqsy.sparkvisualization.service.ITaskService;
import com.dqsy.sparkvisualization.util.InputStreamReaderRunnable;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import java.util.HashMap;
import java.util.List;

/**
 * @author liusinan
 * @version 1.0.0
 * @ClassName TaskService.java
 * @Description TODO
 * @createTime 2020年03月27日 15:35:00
 */
@Service
@Slf4j
public class TaskService implements ITaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public List<Task> getAllTaskList() {
        List<Task> taskList = taskMapper.getAllTaskList();
        return taskList;
    }

    @Override
    public void runJobOnce(Integer taskid, Task task) {
        try {
            /*String[] arr = {String.valueOf(taskid)};
            UserVisitSessionAnalyzeSpark userVisitSessionAnalyzeSpark = new UserVisitSessionAnalyzeSpark();
            userVisitSessionAnalyzeSpark.main(arr);*/

//            String[] arg0=new String[]{"/usr/job/"+taskid, "--master","spark://master:7077", "--name","web polling", "--executor-memory","1G"};
//            SparkSubmit.main(arg0);
            log.info(task.toString() + "执行Session任务");
            if (taskMapper.getTask(taskid).getTaskType().equals("TEST")) {
                HashMap env = new HashMap();
                String[] arr = {String.valueOf(taskid), "true"};
                //这两个属性必须设置
                env.put("HADOOP_CONF_DIR", "/home/user/hadoop/etc/hadoop");
                env.put("JAVA_HOME", "/home/user/jdk");
                CountDownLatch countDownLatch = new CountDownLatch(1);
                SparkAppHandle handle = new SparkLauncher(env)
                        .setSparkHome("/home/user/spark-2.3.3")
                        .setAppResource("/home/user/sparkproject-0.0.1-SNAPSHOT-jar-with-dependencies.jar")
                        .setMainClass("com.dqsy.sparkproject.spark.session.UserVisitSessionAnalyzeSpark")
                        .setMaster("spark://master:7077")
//                    .setDeployMode("client")
                        .setConf("spark.driver.memory", "1g")
                        .setConf("spark.executor.memory", "5g")
                        .setConf("spark.executor.cores", "1")
                        .addAppArgs(arr)
                        .setVerbose(true)
                        .startApplication(new SparkAppHandle.Listener() {
                            //这里监听任务状态，当任务结束时（不管是什么原因结束）,isFinal（）方法会返回true,否则返回false
                            @Override
                            public void stateChanged(SparkAppHandle sparkAppHandle) {
                                if (sparkAppHandle.getState().isFinal()) {
                                    countDownLatch.countDown();
                                }
                                log.info("状态state: " + sparkAppHandle.getState().toString());
                            }

                            @Override
                            public void infoChanged(SparkAppHandle sparkAppHandle) {
                                log.info("Info:" + sparkAppHandle.getState().toString());
                            }
                        });
                        /*        .startApplication();
                        while (!handle.getState().equals(SparkAppHandle.State.FINISHED)){
                            System.out.println("Wait Loop: App_ID: " + handle.getAppId() + " state: " +  handle.getState());
                            Thread.sleep(10000);
                        }
                        if (handle.getState().toString() == "FINISHED" || handle.getState().toString().equals("FINISHED")) {
                            return true;
                        } else if (handle.getState().toString() == "FAILED" || handle.getState().toString().equals("FAILED")) {
                            return false;
                        } else {
                            return false;
                        }*/


                log.info("The task is executing, please wait ....");
                //线程等待任务结束
                countDownLatch.await();
                log.info("The task is finished!");

            /*        .setVerbose(true);
            Process process = handle.launch();
            InputStreamReaderRunnable inputStreamReaderRunnable = new InputStreamReaderRunnable(process.getInputStream(), "input");
            Thread inputThread = new Thread(inputStreamReaderRunnable, "LogStreamReader input");
            inputThread.start();

            InputStreamReaderRunnable errorStreamReaderRunnable = new InputStreamReaderRunnable(process.getErrorStream(), "error");
            Thread errorThread = new Thread(errorStreamReaderRunnable, "LogStreamReader error");
            errorThread.start();

            System.out.println("Waiting for finish...");
            int exitCode = process.waitFor();
            System.out.println("Finished! Exit code:" + exitCode);
            if (process.isAlive()) {
                return true;
            } else {
                return false;
            }*/
            } else if (taskMapper.getTask(taskid).getTaskType().equals("PRODUCTION")) {
                HashMap env = new HashMap();
                String[] arr = {String.valueOf(taskid), "false"};
                //这两个属性必须设置
                env.put("HADOOP_CONF_DIR", "/home/user/hadoop/etc/hadoop");
                env.put("JAVA_HOME", "/home/user/jdk");
                env.put("HIVE_HOME", "/home/user/hive");
                CountDownLatch countDownLatch = new CountDownLatch(1);
                SparkAppHandle handle = new SparkLauncher(env)
                        .setSparkHome("/home/user/spark-2.3.3")
                        .setAppResource("/home/user/sparkproject-0.0.1-SNAPSHOT-jar-with-dependencies.jar")
                        .setMainClass("com.dqsy.sparkproject.spark.session.UserVisitSessionAnalyzeSpark")
                        .setMaster("yarn")
                        .setDeployMode("client")
                        .setConf("spark.driver.memory", "1g")
                        .setConf("spark.executor.memory", "5g")
                        .setConf("spark.executor.cores", "2")
                        .addFile("/home/user/hive/conf/hive-site.xml")
                        .addAppArgs(arr)
                        .setVerbose(true)
                        .startApplication(new SparkAppHandle.Listener() {
                            //这里监听任务状态，当任务结束时（不管是什么原因结束）,isFinal（）方法会返回true,否则返回false
                            @Override
                            public void stateChanged(SparkAppHandle sparkAppHandle) {
                                if (sparkAppHandle.getState().isFinal()) {
                                    countDownLatch.countDown();
                                }
                                log.info("状态state: " + sparkAppHandle.getState().toString());
                            }
                            @Override
                            public void infoChanged(SparkAppHandle sparkAppHandle) {
                                log.info("Info:" + sparkAppHandle.getState().toString());
                            }
                        });
                log.info("The task is executing, please wait ....");
                //线程等待任务结束
                countDownLatch.await();
                log.info("The task is finished!");
            }
        } catch (Exception e) {
            task.setTaskStatus("KILLED");
            taskMapper.updateStatus(task);
            e.printStackTrace();
        }
    }

    @Override
    public void runPageJobOnce(Integer taskid, Task task) {
        try {
            String param = task.getTaskParam();
            log.info(task.toString() + "执行Page任务");
            if (task.getTaskType().equals("TEST")) {
                HashMap env = new HashMap();
                String[] arr = {String.valueOf(taskid), "true"};
                //这两个属性必须设置
                env.put("HADOOP_CONF_DIR", "/home/user/hadoop/etc/hadoop");
                env.put("JAVA_HOME", "/home/user/jdk");
                CountDownLatch countDownLatch = new CountDownLatch(1);
                SparkAppHandle handle = new SparkLauncher(env)
                        .setSparkHome("/home/user/spark-2.3.3")
                        .setAppResource("/home/user/sparkproject-0.0.1-SNAPSHOT-jar-with-dependencies.jar")
                        .setMainClass("com.dqsy.sparkproject.spark.page.PageOneStepConvertRateSpark")
                        .setMaster("spark://master:7077")
                        .setConf("spark.driver.memory", "1g")
                        .setConf("spark.executor.memory", "5g")
                        .setConf("spark.executor.cores", "1")
                        .addAppArgs(arr)
                        .setVerbose(true)
                        .startApplication(new SparkAppHandle.Listener() {
                            //这里监听任务状态，当任务结束时（不管是什么原因结束）,isFinal（）方法会返回true,否则返回false
                            @Override
                            public void stateChanged(SparkAppHandle sparkAppHandle) {
                                if (sparkAppHandle.getState().isFinal()) {
                                    countDownLatch.countDown();
                                }
                                log.info("状态state: " + sparkAppHandle.getState().toString());
                            }

                            @Override
                            public void infoChanged(SparkAppHandle sparkAppHandle) {
                                log.info("Info:" + sparkAppHandle.getState().toString());
                            }
                        });
                log.info("The task is executing, please wait ....");
                //线程等待任务结束
                countDownLatch.await();
                log.info("The task is finished!");
            } else if (task.getTaskType().equals("PRODUCTION")) {
                HashMap env = new HashMap();
                String[] arr = {String.valueOf(taskid), "false"};
                //这两个属性必须设置
                env.put("HADOOP_CONF_DIR", "/home/user/hadoop/etc/hadoop");
                env.put("JAVA_HOME", "/home/user/jdk");
                env.put("HIVE_HOME", "/home/user/hive");
                CountDownLatch countDownLatch = new CountDownLatch(1);
                SparkAppHandle handle = new SparkLauncher(env)
                        .setSparkHome("/home/user/spark-2.3.3")
                        .setAppResource("/home/user/sparkproject-0.0.1-SNAPSHOT-jar-with-dependencies.jar")
                        .setMainClass("com.dqsy.sparkproject.spark.page.PageOneStepConvertRateSpark")
                        .setMaster("yarn")
                        .setDeployMode("client")
                        .setConf("spark.driver.memory", "1g")
                        .setConf("spark.executor.memory", "5g")
                        .setConf("spark.executor.cores", "2")
                        .addFile("/home/user/hive/conf/hive-site.xml")
                        .addAppArgs(arr)
                        .setVerbose(true)
                        .startApplication(new SparkAppHandle.Listener() {
                            //这里监听任务状态，当任务结束时（不管是什么原因结束）,isFinal（）方法会返回true,否则返回false
                            @Override
                            public void stateChanged(SparkAppHandle sparkAppHandle) {
                                if (sparkAppHandle.getState().isFinal()) {
                                    countDownLatch.countDown();
                                }
                                log.info("状态state: " + sparkAppHandle.getState().toString());
                            }

                            @Override
                            public void infoChanged(SparkAppHandle sparkAppHandle) {
                                log.info("Info:" + sparkAppHandle.getState().toString());
                            }
                        });
                log.info("The task is executing, please wait ....");
                //线程等待任务结束
                countDownLatch.await();
                log.info("The task is finished!");
            }
        } catch (Exception e) {
            task.setTaskStatus("KILLED");
            taskMapper.updateStatus(task);
            e.printStackTrace();
        }
    }

    @Override
    public void runAreaJobOnce(Integer taskid, Task task) {
        try {
            String param = task.getTaskParam();
            log.info(task.toString() + "执行Page任务");
            if (task.getTaskType().equals("TEST")) {
                HashMap env = new HashMap();
                String[] arr = {String.valueOf(taskid), "true"};
                //这两个属性必须设置
                env.put("HADOOP_CONF_DIR", "/home/user/hadoop/etc/hadoop");
                env.put("JAVA_HOME", "/home/user/jdk");
                env.put("HIVE_HOME", "/home/user/hive");
                CountDownLatch countDownLatch = new CountDownLatch(1);
                SparkAppHandle handle = new SparkLauncher(env)
                        .setSparkHome("/home/user/spark-2.3.3")
                        .setAppResource("/home/user/sparkproject-0.0.1-SNAPSHOT-jar-with-dependencies.jar")
                        .setMainClass("com.dqsy.sparkproject.spark.product.AreaTop3ProductSpark")
                        .setMaster("spark://master:7077")
                        .setConf("spark.driver.memory", "1g")
                        .setConf("spark.executor.memory", "5g")
                        .setConf("spark.executor.cores", "1")
                        .addFile("/home/user/hive/conf/hive-site.xml")
                        .addAppArgs(arr)
                        .setVerbose(true)
                        .startApplication(new SparkAppHandle.Listener() {
                            //这里监听任务状态，当任务结束时（不管是什么原因结束）,isFinal（）方法会返回true,否则返回false
                            @Override
                            public void stateChanged(SparkAppHandle sparkAppHandle) {
                                if (sparkAppHandle.getState().isFinal()) {
                                    countDownLatch.countDown();
                                }
                                log.info("状态state: " + sparkAppHandle.getState().toString());
                            }

                            @Override
                            public void infoChanged(SparkAppHandle sparkAppHandle) {
                                log.info("Info:" + sparkAppHandle.getState().toString());
                            }
                        });
                log.info("The task is executing, please wait ....");
                //线程等待任务结束
                countDownLatch.await();
                log.info("The task is finished!");
            } else if (task.getTaskType().equals("PRODUCTION")) {
                HashMap env = new HashMap();
                String[] arr = {String.valueOf(taskid), "false"};
                //这两个属性必须设置
                env.put("HADOOP_CONF_DIR", "/home/user/hadoop/etc/hadoop");
                env.put("JAVA_HOME", "/home/user/jdk");
                env.put("HIVE_HOME", "/home/user/hive");
                CountDownLatch countDownLatch = new CountDownLatch(1);
                SparkAppHandle handle = new SparkLauncher(env)
                        .setSparkHome("/home/user/spark-2.3.3")
                        .setAppResource("/home/user/sparkproject-0.0.1-SNAPSHOT-jar-with-dependencies.jar")
                        .setMainClass("com.dqsy.sparkproject.spark.product.AreaTop3ProductSpark")
                        .setMaster("yarn")
                        .setDeployMode("client")
                        .setConf("spark.driver.memory", "1g")
                        .setConf("spark.executor.memory", "5g")
                        .setConf("spark.executor.cores", "2")
                        .addFile("/home/user/hive/conf/hive-site.xml")
                        .addAppArgs(arr)
                        .setVerbose(true)
                        .startApplication(new SparkAppHandle.Listener() {
                            //这里监听任务状态，当任务结束时（不管是什么原因结束）,isFinal（）方法会返回true,否则返回false
                            @Override
                            public void stateChanged(SparkAppHandle sparkAppHandle) {
                                if (sparkAppHandle.getState().isFinal()) {
                                    countDownLatch.countDown();
                                }
                                log.info("状态state: " + sparkAppHandle.getState().toString());
                            }

                            @Override
                            public void infoChanged(SparkAppHandle sparkAppHandle) {
                                log.info("Info:" + sparkAppHandle.getState().toString());
                            }
                        });
                log.info("The task is executing, please wait ....");
                //线程等待任务结束
                countDownLatch.await();
                log.info("The task is finished!");
            }
        } catch (Exception e) {
            task.setTaskStatus("KILLED");
            taskMapper.updateStatus(task);
            e.printStackTrace();
        }
    }

    @Override
    public Task getTask(Integer taskid) {
        return taskMapper.getTask(taskid);
    }

    @Override
    public int deleteTask(Integer taskid) {
        return taskMapper.deleteTask(taskid);
    }

    @Override
    public void saveOrupdate(Task task) throws Exception {
        Preconditions.checkNotNull(task, "task is null");
        Integer taskid = task.getTaskid();
        String taskName = task.getTaskName();
        if (StringUtils.isEmpty(task.getTaskid())) {
            if (taskMapper.getTaskByName(taskName) != null) {
                log.error(task.toString() + " 已存在,重新创建！");
            } else if (StringUtils.isEmpty(task.getTaskName()) || StringUtils.isEmpty(task.getTaskType()) || StringUtils.isEmpty(task.getTaskParam())) {
                log.error("任务必要参数为空，请重新创建！");
            } else {
                addTask(task);
            }
        } else {
            if (taskMapper.getTaskByNameAndId(taskid, taskName) != null) {
                log.error(task.toString() + " 已存在，请重新修改");
            } else if (StringUtils.isEmpty(task.getTaskName()) || StringUtils.isEmpty(task.getTaskType()) || StringUtils.isEmpty(task.getTaskParam())) {
                log.error(task.toString() + " 必要参数为空，请重新修改！");
            } else {
                updateTask(task);
            }
        }
    }

    @Override
    public void addTask(Task task) throws Exception {
        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        // 获取当前时间
        Date date = new Date();
        log.info("[" + sdf.format(date) + "] " + "创建了task_name为" + "'" + task.getTaskName() + "'" + "的任务");
        task.setCreateTime(sdf.format(date));
        taskMapper.add(task);
    }

    @Override
    public void updateTask(Task task) throws Exception {
        taskMapper.update(task);
    }

}
