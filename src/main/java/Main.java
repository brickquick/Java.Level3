import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static final int CARS_COUNT = 10;

    public static CyclicBarrier cyclicBarrier;
    public static CountDownLatch cdl;
    public static CountDownLatch cdlEnd;
    public static Semaphore smp;
    private static final Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        cyclicBarrier = new CyclicBarrier(CARS_COUNT);
        cdl = new CountDownLatch(CARS_COUNT);
        cdlEnd = new CountDownLatch(CARS_COUNT);
        smp = new Semaphore(CARS_COUNT / 2);

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");

        Race race = new Race(new Road(60), new Tunnel(), new Road(40));

        Car[] cars = new Car[CARS_COUNT];

        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }

        for (Car value : cars) {
            new Thread(value).start();
        }

        try {
            cdl.await();
            lock.lock();
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
            lock.unlock();
            cdlEnd.await();
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Arrays.sort(cars, new Comparator<Car>() {
            public int compare(Car o1, Car o2) {
                return o1.getFinished() - o2.getFinished();
            }
        });
        for (Car car : cars) {
            System.out.println(car.getFinished() + "-м финишировал - " + car.getName());
        }

    }

}
