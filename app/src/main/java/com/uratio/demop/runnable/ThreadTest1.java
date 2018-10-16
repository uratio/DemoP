package com.uratio.demop.runnable;

public class ThreadTest1 implements Runnable {

    private static Test test;

    @Override
    public void run() {
        for (int i = 0; i < 50; i++) {
            test.f1(i);
        }
    }

    public static void main(String[] args) {
        test = new Test();
        new Thread(new ThreadTest1()).start();
        for (int i = 0; i < 50; i++) {
            test.f2(i);
        }
    }

    /**
     * 将控制和逻辑及数据分类（该类就是数据）
     */
    static class Test {
        private boolean isf1 = true;

        /**
         * 输出10次
         */
        public synchronized void f1(int j) {
            if (!isf1) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 1; i <= 10; i++) {
                System.out.println(Thread.currentThread().getName() + "第" + j + "次轮巡，输出" + i);
            }
            isf1 = false;
            notify();
        }

        /**
         * 输出100次
         */
        public synchronized void f2(int j) {
            if (isf1) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 1; i <= 100; i++) {
                System.out.println(Thread.currentThread().getName() + "第" + j + "次轮巡，输出" + i);
            }
            isf1 = true;
            notify();
        }
    }

}
