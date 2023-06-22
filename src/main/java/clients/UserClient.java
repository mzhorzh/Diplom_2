package clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.CreateOrderRequest;
import pojo.CreateUserRequest;
import pojo.LoginUserRequest;
import pojo.UpdateDataUserRequest;

import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {
    public static final String CREATE_URI="/api/auth/register";
    public static final String LOGIN_URI="/api/auth/login";
    public static final String USER_DATA_URI ="/api/auth/user ";
    public static final String ORDER_URI="/api/orders";


    @Step("Создание пользователя")
    public ValidatableResponse createUser(CreateUserRequest createUserRequest){
        return  given()
                .spec(getSpec())
                .body(createUserRequest)
                .when()
                .post(CREATE_URI)
                .then();

    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(LoginUserRequest loginUserRequest){
        return given()
                .spec(getSpec())
                .body(loginUserRequest)
                .when()
                .post(LOGIN_URI)
                .then();
    }

    @Step("Обновление данных пользователя")
    public ValidatableResponse UpdateDataUser(String accessToken, UpdateDataUserRequest updateDataUserRequest){
        return given()
                .header("authorization",accessToken)
                .spec(getSpec())
                .body(updateDataUserRequest)
                .when()
                .patch(USER_DATA_URI)
                .then();
    }

    @Step("Получение данных пользователя")
    public ValidatableResponse getUserData(String accessToken){
        return given()
                .header("Authorization", accessToken)
                .spec(getSpec())
                .when()
                .get(USER_DATA_URI)
                .then();
    }

    @Step("Создание заказа")
    public ValidatableResponse CreateOrder(String accessToken, CreateOrderRequest createOrderRequest){
        return given()
                .header("authorization",accessToken)
                .spec(getSpec())
                .body(createOrderRequest)
                .when()
                .post(ORDER_URI)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getListAllOrdersUser(String accessToken){
        return given()
                .header("authorization",accessToken)
                .spec(getSpec())
                .when()
                .get(ORDER_URI)
                .then();
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken){
        given()
                .header("authorization",accessToken)
                .spec(getSpec())
                .when()
                .delete(USER_DATA_URI);
    }
}