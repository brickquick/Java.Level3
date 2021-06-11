package lesson7.homework7;

public class ClassTest {

    public static void main(String[] args) {
        Tester.start(ClassTest.class);
    }

    @BeforeSuite
    public void beforeMethods() {
        System.out.println("Begin.");
    }

    @AfterSuite
    public void afterMethods() {
        System.out.println("End.");
    }

    @Test(priority = 6)
    public void methodTest1() {
        System.out.println("Test1.");
    }

    @Test(priority = 0)
    public void methodTest2() {
        System.out.println("Test2.");
    }

    @Test
    public void methodTest3() {
        System.out.println("Test3.");
    }

}
