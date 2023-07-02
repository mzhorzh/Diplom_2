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

import java.util.Arrays;
import java.util.List;

import static org.apache.http.HttpStatus.*;

public class GetUserOrderTest {
    private UserClient userClient = new UserClient();
    private String accessToken;
    List<String> ingredients = Arrays.asList(
            "61c0c5a71d1f82001bdaaa74",
            "61c0c5a71d1f82001bdaaa6c",
            "61c0c5a71d1f82001bdaaa77",
            "61c0c5a71d1f82001bdaaa7a");

    @Test
    @DisplayName("Кейс проверки получения списка заказов авторизованного пользователя")
    @Description("Должен возвращаться статус код 200 и в теле ответа должен отображаться заказ")
    public void getAuthorizeUserOrdersShouldBeDisplayed() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        userClient.CreateOrder(accessToken, createOrderRequest);
        userClient.getListAllOrdersUser(accessToken)
                .statusCode(SC_OK)
                .body("success", Matchers.equalTo(true))
                .and()
                .body("orders",Matchers.notNullValue())
                .body("total",Matchers.notNullValue())
                .body("totalToday",Matchers.notNullValue());
    }

    @Test
    @DisplayName("Кейс проверки получения списка заказов неавторизованного пользователя")
    @Description("Должен возвращаться статус код 401 и в теле ответа сообщение \"You should be authorised\"")
    public void getNoAuthorizeUserOrdersShouldNotBeDisplayed() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        ValidatableResponse response = userClient.createUser(createUserRequest);
        accessToken = response.extract().path("accessToken");
        CreateOrderRequest createOrderRequest = new CreateOrderRequest(ingredients);
        userClient.CreateOrder("", createOrderRequest);
        userClient.getListAllOrdersUser("")
                .statusCode(SC_UNAUTHORIZED)
                .body("success",Matchers.equalTo(false))
                .and()
                .body("message",Matchers.equalTo("You should be authorised"));
    }

    @After
    @DisplayName("Кейс удаления пользователя")
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(accessToken);
        }
    }
}
