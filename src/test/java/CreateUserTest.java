import clients.UserClient;
import dataprovider.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.After;
import pojo.CreateUserRequest;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserTest {
    private UserClient userClient = new UserClient();
    private String accessToken;
    @Test
    @DisplayName("Кейс проверки создания пользователя")
    @Description("Должен создаться пользователь. Код ответа должен быть 200")
    public void userShouldBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();

        userClient.createUser(createUserRequest)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true));;
    }

    @Test
    @DisplayName("Кейс проверки создания пользователя который уже существует")
    @Description("При повторном создании пользователя должен возвращаться код ошибки 403")
    public void duplicateUserShouldNotBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        userClient.createUser(createUserRequest);
        userClient.createUser(createUserRequest)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Кейс проверки создания пользователя без e-mail")
    @Description("При создании пользователя без e-mail должен возвращаться код ошибки 403 и в теле ответа показываться сообщение \"Email, password and name are required fields\"")
    public void userWithoutEmailShouldNotBeCreated() {
        CreateUserRequest createCourierRequest = new CreateUserRequest();
        createCourierRequest.setEmail("");
        createCourierRequest.setPassword("12345678");
        createCourierRequest.setName("Ashot");
        userClient.createUser(createCourierRequest)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Кейс проверки создания пользователя без имени")
    @Description("При создании пользователя без имени должен возвращаться код ошибки 403 и в теле ответа показываться сообщение \"Email, password and name are required fields\"")
    public void userWithoutNameShouldNotBeCreated() {
        CreateUserRequest createCourierRequest = new CreateUserRequest();
        createCourierRequest.setEmail("test@test.ru");
        createCourierRequest.setPassword("12345678");
        createCourierRequest.setName("");
        userClient.createUser(createCourierRequest)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Кейс проверки создания пользователя без пароля")
    @Description("При создании пользователя без пароля должен возвращаться код ошибки 403 и в теле ответа показываться сообщение \"Email, password and name are required fields\"")
    public void userWithoutPasswordShouldNotBeCreated() {
        CreateUserRequest createCourierRequest = new CreateUserRequest();
        createCourierRequest.setEmail("test@test.ru");
        createCourierRequest.setPassword("");
        createCourierRequest.setName("Ashot");
        userClient.createUser(createCourierRequest)
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    @DisplayName("Кейс удаления пользователя")
    public void tearDown(){
        if (accessToken != null){
            userClient.deleteUser(accessToken);
        }
    }
}
