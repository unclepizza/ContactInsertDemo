package example.gk.com.contactinsertdemo;

/**
 * Created by gaok on 2016/12/16.
 */

public class ContactMan {
    private String name;
    //支持多个电话号码，用","隔开
    private String numbers;

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
