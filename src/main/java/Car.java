import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private static CyclicBarrier cyclicBarrier;
    private static CountDownLatch countDownLatchStart;
    private static CountDownLatch countDownLatchFinish;
    private static Lock lock;

    static {
        CARS_COUNT = 0;
    }

    private Race race;
    private int speed;
    private String name;
    private static boolean isWin = false;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed, CyclicBarrier barrier, CountDownLatch start, CountDownLatch finish, Lock lock) {
        Car.lock = lock;
        countDownLatchStart = start;
        countDownLatchFinish = finish;
        this.race = race;
        this.speed = speed;
        cyclicBarrier = barrier;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int) (Math.random() * 800));
            cyclicBarrier.await();
            System.out.println(this.name + " готов");
            countDownLatchStart.countDown();
            countDownLatchStart.await();
            Thread.sleep(100);
            for (int i = 0; i < race.getStages().size(); i++) {
                race.getStages().get(i).go(this);
            }
            countDownLatchFinish.countDown();
            try {
                lock.lock();
                if (!isWin) {
                    System.out.println(name + " - WIN");
                    isWin = true;
                }
            } finally {
                lock.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}