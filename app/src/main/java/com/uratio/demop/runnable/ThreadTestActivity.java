package com.uratio.demop.runnable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uratio.demop.R;

public class ThreadTestActivity extends AppCompatActivity {
    private Thread1 thread1;
    private Thread2 thread2;
    private Thread3 thread3;
    private Thread4 thread4;
    private Thread5 thread5;

    private Thread mThread1;
    private Thread mThread2;
    private Thread mThread3;
    private Thread mThread4;
    private Thread mThread5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_test);

        thread1 = new Thread1();
        thread2 = new Thread2();
        thread3 = new Thread3();
        thread4 = new Thread4();
        thread5 = new Thread5();

        mThread1 = new Thread(thread1);
        mThread2 = new Thread(thread2);
        mThread3 = new Thread(thread3);
        mThread4 = new Thread(thread4);
        mThread5 = new Thread(thread5);

    }

    //一个关于线程的经典面试题，要求用三个线程，按顺序打印1,2,3,4,5....71,72,73,74,75.
    //线程1先打印1,2,3,4,5,然后是线程2打印6,7,8,9,10,然后是线程3打印11,12,13,14,15.
    //接着再由线程1打印16,17,18,19,20....以此类推,直到线程3打印到75。

    public class Printer implements Runnable {
        int id;
        int num = 1;

        public Printer(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            synchronized (Printer.class) {
                while (num <= 75) {
                    if (num / 5 % 3 == id) {
                        System.out.print("id" + id + ":");
                        for (int i = 0; i < 5; i++) System.out.print(num++ + ",");
                        System.out.println();
                        Printer.class.notifyAll();
                    } else {
                        try {
                            Printer.class.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    class Thread1 implements Runnable {
        private int a1 = 1;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                a1 += 5;
                System.out.println("数字=" + a1);
            }
        }
    }

    class Thread2 implements Runnable {
        private int a1 = 2;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                a1 += 5;
                System.out.println("数字=" + a1);
            }
        }
    }

    class Thread3 implements Runnable {
        private int a1 = 3;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                a1 += 5;
                System.out.println("数字=" + a1);
            }
        }
    }

    class Thread4 implements Runnable {
        private int a1 = 4;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                a1 += 5;
                System.out.println("数字=" + a1);
            }
        }
    }

    class Thread5 implements Runnable {
        private int a1 = 5;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                a1 += 5;
                System.out.println("数字=" + a1);
            }
        }
    }
}
