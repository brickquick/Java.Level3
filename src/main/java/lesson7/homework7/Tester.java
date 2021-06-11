package lesson7.homework7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

public class Tester {

    public static void main(String[] args) {
        start(ClassTest.class);
    }

    public static void start(Class test) {
        Method[] methods = test.getDeclaredMethods();
        Method[] before = new Method[1];
        int b = 0;
        Method[] after = new Method[1];
        int a = 0;
        int t = 0;
        for (Method o : methods) {
            try {
                if(o.getAnnotation(BeforeSuite.class) != null) {
                    before[b] = o;
                    b++;
                }
                if(o.getAnnotation(AfterSuite.class) != null) {
                    after[a] = o;
                    a++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new RuntimeException("Методы с аннотациями @BeforeSuite и @AfterSuite должны присутствовать в единственном экземпляре");
            }
            if(o.getAnnotation(Test.class) != null) {
                if (o.getAnnotation(Test.class).priority() < 0 || o.getAnnotation(Test.class).priority() > 10) {
                    throw new RuntimeException("Приоритет должен быть от 0 до 10");
                }
                t++;
            }
        }

        try {
            if (before[0] != null) {
                before[0].invoke(test.newInstance());
            }

            Method[] tests = new Method[t];
            for (int i = 0, j = 0; i < methods.length; i++) {
                if (methods[i].getAnnotation(Test.class) != null) {
                    tests[j] = methods[i];
                    j++;
                }
            }
            Arrays.sort(tests, new Comparator<Method>() {
                @Override
                public int compare(Method o1, Method o2) {
                    return o2.getAnnotation(Test.class).priority() - o1.getAnnotation(Test.class).priority();
                }
            });
            for (Method m : tests) {
                m.invoke(test.newInstance());
            }

            if (after[0] != null) {
                after[0].invoke(test.newInstance());
            }
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
