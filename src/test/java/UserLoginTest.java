import clients.UserClient;
import dataprovider.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import pojo.CreateUserRequest;
import pojo.LoginUserRequest;

import static org.apache.http.HttpStatus.*;

public class UserLoginTest {
    private UserClient userClient = new UserClient();

    private String accessToken;

    @Test
    @DisplayName("Кейс проверки что пользователь может залогинится")
    @Description("Должен вернуться статус код 200 и в теле ответа должен вернуться accessToken и refreshToken")
    public void userShouldBeLogin() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.createUser(createUserRequest);
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);

        userClient.loginUser(loginUserRequest)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true))
                .body("accessToken", Matchers.notNullValue())
                .body("refreshToken", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Кейс проверки логина с несуществующим e-mail")
    @Description("Должен вернуться код ошибки 401 и в теле сообщение \"email or password are incorrect\"")
    public void userWithWrongEmailShouldNotBeLogin() {
        CreateUserRequest createUserRequest = new UserProvider().getRandomCreateUserRequest();
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        userClient.createUser(createUserRequest);
        createUserRequest.setEmail("wrong_email@email.com");
        userClient.loginUser(LoginUserRequest.from(createUserRequest))
                .statusCode(SC_UNAUTHORIZED)
                .body("message", Matchers.equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Кейс проверки логина с несуществующим паролем")
    @Description("Должен вернуться код ошибки 401 и в теле сообщение \"email or password are incorrect\"")
    public void userWithWrongPasswordShouldNotBeLogin() {
        CreateUserRequest createUserRequest = new UserProvider().getRandomCreateUserRequest();
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        userClient.createUser(createUserRequest);
        createUserRequest.setPassword("wrong_password");
        userClient.loginUser(LoginUserRequest.from(createUserRequest))
                .statusCode(SC_UNAUTHORIZED)
                .body("message", Matchers.equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Кейс проверки логина с несуществующим логином и паролем")
    @Description("Должен вернуться код ошибки 401 и в теле сообщение \"email or password are incorrect\"")
    public void userWithWrongEmailAndPasswordShouldNotBeLogin() {
        CreateUserRequest createUserRequest = new UserProvider().getRandomCreateUserRequest();
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        userClient.createUser(createUserRequest);
        createUserRequest.setEmail("wrong_email@email.com");
        createUserRequest.setPassword("wrong_password");
        userClient.loginUser(LoginUserRequest.from(createUserRequest))
                .statusCode(SC_UNAUTHORIZED)
                .body("message", Matchers.equalTo("email or password are incorrect"));
    }

    @After
    @DisplayName("Кейс удаления пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
