import clients.UserClient;
import dataprovider.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import pojo.CreateUserRequest;
import pojo.UpdateDataUserRequest;

import static org.apache.http.HttpStatus.*;

public class UpdateUserDataTest {
    private UserClient userClient = new UserClient();
    private String accessToken;

    @Test
    @DisplayName("Кейс проверки обновления данных авторизованного пользователя")
    @Description("Должен вернуться статус код 200 в теле сообщения обновленные данные пользователя")
    public void updateAuthorizeUserData() {
        UpdateDataUserRequest updateDataUserRequest = UserProvider.getRandomUpdateDataUserRequest();
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.UpdateDataUser(accessToken, updateDataUserRequest)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true))
                .body("user", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Кейс проверки обновления данных авторизованного пользователя")
    @Description("Должен вернуться статус код ошибки 401 в теле сообщение \"You should be authorised\"")
    public void updateNoAuthorizeUserData() {
        UpdateDataUserRequest updateDataUserRequest = UserProvider.getRandomUpdateDataUserRequest();
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.UpdateDataUser("", updateDataUserRequest)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Кейс проверки обновления e-mail авторизованного пользователя")
    @Description("Должен вернуться статус код 200 в теле сообщения обновленный e-mail пользователя")
    public void updateUserDataEmail() {
        UpdateDataUserRequest updateDataUserRequest = new UpdateDataUserRequest();
        updateDataUserRequest.setEmail("test@tet.com");
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.UpdateDataUser(accessToken, updateDataUserRequest)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true));
    }

    @Test
    @DisplayName("Кейс проверки обновления имени авторизованного пользователя")
    @Description("Должен вернуться статус код 200 в теле сообщения обновленное имя пользователя")
    public void updateUserDataName() {
        UpdateDataUserRequest updateDataUserRequest = new UpdateDataUserRequest();
        updateDataUserRequest.setName("Ашот Петрович");
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        userClient.UpdateDataUser(accessToken, updateDataUserRequest)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true));
    }

    @After
    @DisplayName("Кейс удаления пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
