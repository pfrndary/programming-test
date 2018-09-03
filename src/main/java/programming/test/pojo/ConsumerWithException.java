package programming.test.pojo;

public interface ConsumerWithException<I, T extends Throwable> {

    void accept(I obj) throws T;

}
