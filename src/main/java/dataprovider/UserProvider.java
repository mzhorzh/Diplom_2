package dataprovider;

import org.apache.commons.lang3.RandomStringUtils;
import pojo.CreateUserRequest;
import pojo.UpdateDataUserRequest;

public class UserProvider {
    public static CreateUserRequest getRandomCreateUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName(RandomStringUtils.randomAlphabetic(8));
        createUserRequest.setEmail(RandomStringUtils.randomAlphabetic(8) + "@mail.ru");
        createUserRequest.setPassword(RandomStringUtils.randomAlphabetic(8));
        return createUserRequest;
    }

    public static UpdateDataUserRequest getRandomUpdateDataUserRequest() {
        UpdateDataUserRequest updateDataUserRequest = new UpdateDataUserRequest();
        updateDataUserRequest.setEmail(RandomStringUtils.randomAlphabetic(8) + "@test.ru");
        updateDataUserRequest.setName(RandomStringUtils.randomAlphabetic(8));
        return updateDataUserRequest;
    }
}
