package programming.test.pojo;

@FunctionalInterface
public interface ConsumerWithException<I, T extends Throwable> {

    void accept(I obj) throws T;

}
