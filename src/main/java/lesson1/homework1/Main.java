package lesson1.homework1;

public class Main {

    public static void main(String[] args) {
        Apple apple1 = new Apple();
        Orange orange1 = new Orange();
        Box<Apple> box1 = new Box<Apple>(apple1, apple1, apple1);
        Box<Apple> box2 = new Box<Apple>(apple1, apple1, apple1);
        Box<Orange> box3 = new Box<Orange>(orange1, orange1, orange1);
        System.out.println(box3.getWeight());
        System.out.println(box1.compare(box3));
        System.out.println(box1.compare(box2));
        System.out.println(box2.getWeight());
        box1.pourOver(box2);
        System.out.println(box2.getWeight());
        System.out.println(box1.getWeight());
        box1.addFruitInBox(apple1);
        System.out.println(box1.getWeight());
    }

}
