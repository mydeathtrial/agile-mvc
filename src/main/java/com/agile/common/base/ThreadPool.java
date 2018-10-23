package com.agile.common.base;

import java.util.LinkedList;

public class ThreadPool<T extends Runnable > {

    //最大thread数量
    private int MAX = 100;

    //默认值thread数量
    private int DEFAULT = 5;

    //缓存thread数量
    private int CACHE = 5;

    //最小数量
    private int MIN = 1;

    //每次添加线程数量
    private int STEP = 1;

    //任务列表
    private final LinkedList<T> taskList = new LinkedList<>();

    //服务线程的数量
    private int init;

    //服务线程的列表
    private final LinkedList<Waiter> runWaiters =  new LinkedList<>();

    //服务线程的列表
    private final LinkedList<Waiter> sleepWaiters =  new LinkedList<>();

    //服务监听器
    private Listener listener = new Listener();

    public ThreadPool() {
        this.init = DEFAULT;
        addWaiters(this.init);
        listener.start();
    }

    public ThreadPool(int num) {
        if (num > MAX) {
            this.init = MAX;
        }else if(num < MIN){
            this.init = MIN;
        }else{
            this.init = num;
        }
        addWaiters(this.init);
        listener.start();
    }

    public ThreadPool(int max, int min, int def, int step, int init) {
        this.MAX = max;
        this.DEFAULT = def;
        this.MIN = min;
        this.STEP = step;
        this.init = init;
        addWaiters(this.init);
        listener.start();
    }

    /**
     * 初始化服务线程
     * @param num 初始化服务线程数量
     */
    private void addWaiters(int num) {
        synchronized (sleepWaiters){
            synchronized (runWaiters){
                int sleep = sleepWaiters.size();
                int run = runWaiters.size();
                if(sleep + run + num > MAX){
                    num = MAX - sleep -run;
                }
                for (int i = 0; i < num; i++) {
                    Waiter waiter = new Waiter();
                    sleepWaiters.add(waiter);
                    waiter.start();
                }
            }
        }
    }

    /**
     * 减少服务线程
     */
    private void removeWaiters() {System.out.println("remove waiter");
        synchronized (sleepWaiters){
            while (sleepWaiters.size()>CACHE){
                sleepWaiters.remove(CACHE);
            }
        }
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        while (taskList.isEmpty()){
            for (Waiter w: runWaiters) {
                try {
                    w.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                w.shutdown();
                sleepWaiters.remove(w);
            }
            for (Waiter w: sleepWaiters) {
                w.shutdown();
            }
            listener.shutdown();
        }
    }

    /**
     * 添加任务
     * @param task 任务线程/Runnable接口
     */
    public void addTask(T task) {
        synchronized (taskList){
            if (task != null) {
                //添加任务队列并且唤醒服务线程进行工作
                taskList.addLast(task);
            }
        }
    }

    /**
     * 获取任务
     * @return task 任务线程/Runnable接口
     */
    private T getTask() {
        synchronized (taskList){
            while (!taskList.isEmpty()) {
                return taskList.removeFirst();
            }
        }
        return null;
    }
    /**
     * 监听线程类
     */
    class Listener extends Thread {

        //服务开关
        private volatile boolean running = true;

        public void run() {
            while (running) {
                synchronized (sleepWaiters){
                    synchronized (taskList){
                        synchronized (runWaiters){
                            if(sleepWaiters.isEmpty() && !taskList.isEmpty()){
                                addWaiters(STEP);
                            }
                            if(!sleepWaiters.isEmpty() && taskList.isEmpty()){
                                if(sleepWaiters.size()>CACHE)
                                removeWaiters();
                            }
                        }
                    }
                }
            }
        }

        //终止该线程
        void shutdown() {
            running = false;
        }
    }
    /**
     * 服务线程类
     */
    class Waiter extends Thread {

        //服务开关
        private volatile boolean running = true;

        public void run() {
            while (running) {

                T task = getTask();

                //执行job
                if (task != null){
                    synchronized (sleepWaiters){
                        synchronized (runWaiters){
                            if(sleepWaiters.contains(this)){
                                sleepWaiters.remove(this);
                            }
                            if(!runWaiters.contains(this)){
                                runWaiters.add(this);
                            }
                        }
                    }

                    System.out.println("任务数量："+taskList.size()+"等待线程" + sleepWaiters.size()+" | 服务线程："+runWaiters.size());
                    task.run();
                    synchronized (sleepWaiters){
                        synchronized (runWaiters){
                            if(runWaiters.contains(this)){
                                runWaiters.remove(this);
                            }
                            if(!sleepWaiters.contains(this)){
                                sleepWaiters.add(this);
                            }
                        }
                    }
                }
            }
        }

        //终止该线程
        void shutdown() {
            running = false;
        }
    }
}
