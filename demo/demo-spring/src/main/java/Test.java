public class Test {

    public static void main(String[] args) {
        Thread.startVirtualThread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println("hello");
            }
        });
        System.out.println("world");
    }

}
