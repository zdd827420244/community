package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
//阻塞队列满足生产者消费者模式，所以得有生产者线程和消费者线程，所以要额外定义两个类，可以在外面定义，也可以在内部定义（内部类），但是一定不要写public，因为一个文件里面只能有一个类可以是public的
public class BlockingQueueTests {

    public static void main(String[] args) {
        BlockingQueue queue = new ArrayBlockingQueue(10); //实例化阻塞队列
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start(); //1个生产者生产数据，三个消费者并发消费数据
    }

}

class Producer implements Runnable { //是一个线程，所以要实现Runnable接口，所以要实现里面的run方法  本线程是要交给阻塞队列来管理，所以要把阻塞队列传进来

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    } //实例化Producer时就要把阻塞队列传进来

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + "生产:" + queue.size()); //获取当前线程名
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class Consumer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000)); //0-1000随机个数
                queue.take();
                System.out.println(Thread.currentThread().getName() + "消费:" + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}