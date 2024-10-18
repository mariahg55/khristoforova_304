import java.util.LinkedList;

public class lr2 {
    private LinkedList<Integer> buffer = new LinkedList<>();
    private int capacity = 10;//максимальная емкость буфера

    //производитель
    public void produce() throws InterruptedException {

        while (true) {
            int value = (int) (1 + Math.random() * 100);
            synchronized (this) {
                //если буфер полон, производитель вызывает метод wait
                //и переходит в режим ожидания, пока потребитель не освободит место в буфере
                while (buffer.size() == capacity) {
                    wait();
                }
                System.out.println("Производитель: " + value);
                buffer.add(value);
                notify();//уведомляет потребителя о наличии новых данных
                Thread.sleep(1000);
            }
        }
    }


    //потребитель
    public void consume() throws InterruptedException {

        while (true) {
            synchronized (this) {
                //если буфер пуст, то потребитель вызывает метод wait
                //и перехит в режим ожидания, пока производитель не добавит новые данные
                while (buffer.isEmpty()) {
                    wait();
                }
                int value = buffer.removeFirst();
                System.out.println("Потребитель: " + value);
                notify();//уведомляет производителя о том, что место в буфере осовободилась
                Thread.sleep(1000);
            }
        }
    }


    public static void main(String[] args) {
        lr2 pc = new lr2();
        Thread producerThread = new Thread(() -> {
            try {
                pc.produce();
            } catch (InterruptedException e) {
                System.err.print(e);
            }
        });

        Thread consumerThread = new Thread(() -> {
            try {
                pc.consume();
            } catch (InterruptedException e) {
                System.err.print(e);
            }
        });

        producerThread.start();
        consumerThread.start();
    }
}