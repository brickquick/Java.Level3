package java8.lvl3.hw1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Box<T extends Fruit> {
    private List<T> listOfFruits;
    T fruit;

    public Box(T... arr) {
        fruit = arr[0];
        listOfFruits = new ArrayList<>(arrToArrayList(arr));
    }

//1. Написать метод, который меняет два элемента массива местами.(массив может быть любого ссылочного типа);
    private T[] swapElementsInArray(T[] arr, int first, int second) {
        T element = arr[first];
        arr[first] = arr[second];
        arr[second] = element;
        return arr;
    }

//2. Написать метод, который преобразует массив в ArrayList;
    private List<T> arrToArrayList(T[] arr) {
        return Arrays.asList(arr);
    }

//3.d. Сделать метод getWeight() который высчитывает вес коробки, зная количество фруктов и вес одного фрукта
// (вес яблока - 1.0f, апельсина - 1.5f, не важно в каких это единицах);
    public float getWeight() {
        return listOfFruits.size() * fruit.getMass();
    }

//3.e. Внутри класса коробка сделать метод compare, который позволяет сравнить текущую коробку с той,
// которую подадут в compare в качестве параметра, true - если их веса равны, false в противном случае
// (коробки с яблоками мы можем сравнивать с коробками с апельсинами);
    public boolean compare(Box<?> box) {
        return box.getWeight() == this.getWeight();
    }

//3.f. Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую коробку
// (помним про сортировку фруктов, нельзя яблоки высыпать в коробку с апельсинами),
// соответственно в текущей коробке фруктов не остается, а в другую перекидываются объекты, которые были в этой коробке;
    public void pourOver(Box<T> box) {
        box.listOfFruits.addAll(listOfFruits);
        listOfFruits.removeAll(listOfFruits);
    }

//3.g. Не забываем про метод добавления фрукта в коробку.
    public void addFruitInBox(T fruit) {
        listOfFruits.add(fruit);
    }
}
