public class Homework1_2 {

    public static void main(String[] args) {
        System.out.println(second4(new int[]{1, 1, 1, 4, 4, 1, 4, 4}));
        System.out.println(second4(new int[]{1, 1, 1, 1, 1, 1}));
        System.out.println(second4(new int[]{4, 4, 4, 4}));
        System.out.println(second4(new int[]{1, 4, 4, 1, 1, 4, 3}));
    }

    //Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
    //Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
    //идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку, иначе в методе
    //необходимо выбросить RuntimeException.
    //Написать набор тестов для этого метода (по 3-4 варианта входных данных).
    //Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].
    public static int[] first4(int[] arr) {
        for (int i = arr.length, j = 0; i > 0; i--, j++) {
            if (arr[i - 1] == 4) {
                int[] arrEnd = new int[j];
                for (int l = 0; l < arrEnd.length; l++, i++) {
                    arrEnd[l] = arr[i];
                }
                return arrEnd;
            }
        }
        throw new RuntimeException();
    }

    //Написать метод, который проверяет состав массива из чисел 1 и 4. Если в нем нет хоть одной четверки или единицы,
    //то метод вернет false; Написать набор тестов для этого метода (по 3-4 варианта входных данных).
    //[ 1 1 1 4 4 1 4 4 ] -> true
    //[ 1 1 1 1 1 1 ] -> false
    //[ 4 4 4 4 ] -> false
    //[ 1 4 4 1 1 4 3 ] -> false
    public static boolean second4(int[] arr) {
        int one = 0, four = 0;
        for (int j : arr) {
            if (j == 1) {
                one++;
            }
            if (j == 4) {
                four++;
            }
        }
        return four + one >= arr.length && one != 0 && four != 0;
    }
}
