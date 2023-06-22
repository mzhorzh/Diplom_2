import clients.UserClient;
import dataprovider.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import pojo.CreateOrderRequest;
import pojo.CreateUserRequest;
import pojo.LoginUserRequest;

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;

public class CreateOrderTest {
    private UserClient userClient = new UserClient();
    private String accessToken;
    List<String> ingredients = Arrays.asList(
            "61c0c5a71d1f82001bdaaa74",
            "61c0c5a71d1f82001bdaaa6c",
            "61c0c5a71d1f82001bdaaa77",
            "61c0c5a71d1f82001bdaaa7a");

    @Test
    @DisplayName("Кейс проверки создания заказа авторизованным пользователем")
    @Description("Должен вернуться статус код 200 и в теле информация о заказе")
    public void authorizeUserOrderShouldBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        userClient.CreateOrder(accessToken, createOrderRequest)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true))
                .and()
                .body("name",Matchers.notNullValue())
                .body("order",Matchers.notNullValue());
    }

    @Test
    @DisplayName("Кейс проверки создания заказа неавторизованным пользователем")
    @Description("Должен вернуться код ошибки 401, т.к в описании задачи сказано, что заказ может делать только авторизованный пользователь")
    public void noAuthorizeUserOrderShouldNotBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        userClient.CreateOrder("", createOrderRequest)
                .statusCode(SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("Кейс проверки создания заказа авторизованным пользователем без ингредиентов")
    @Description("Должен вернуться код ошибки 400 и в теле сообщение \"Ingredient ids must be provided\" ")
    public void noIngredientOrderShouldNotBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(null);
        userClient.CreateOrder(accessToken, createOrderRequest)
                .statusCode(SC_BAD_REQUEST)
                .body("success",Matchers.equalTo(false))
                .and()
                .body("message",Matchers.equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Кейс проверки создания заказа с неверным хешом ингредиента")
    @Description("Должен вернуться код ошибки 500 Internal Server Error")
    public void invalidIngredientOrderShouldNotBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        ingredients.set(0, "12345678");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        userClient.CreateOrder(accessToken, createOrderRequest)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @After
    @DisplayName("Кейс удаления пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
