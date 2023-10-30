package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import io.qameta.allure.AllureId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBUserExtension implements BeforeEachCallback, ParameterResolver {

    public static ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DBUserExtension.class);


    private final UserEntity user = new UserEntity();


    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        List<Method> handleMethods = new ArrayList<>();
        handleMethods.add(extensionContext.getRequiredTestMethod());
        Arrays.stream(extensionContext.getRequiredTestClass().getDeclaredMethods())
                .filter(
                        method -> method.isAnnotationPresent(BeforeEach.class))
                .forEach(handleMethods::add);
        for (Method method : handleMethods) {
            if (
                    Arrays.stream(method.getParameters()).anyMatch(p -> p.getType().isAssignableFrom(UserEntity.class))
                            && method.isAnnotationPresent(DBUser.class)
            ) {
                DBUser annotation = extensionContext.getRequiredTestMethod().getAnnotation(DBUser.class);

                user.setUsername(annotation.username());
                user.setPassword(annotation.password());
                user.setEnabled(annotation.enabled());
                user.setAccountNonExpired(annotation.accountNonExpired());
                user.setAccountNonLocked(annotation.accountNonLocked());
                user.setCredentialsNonExpired(annotation.credentialsNonExpired());
                user.setAuthorities(Arrays.stream(annotation.authorities())
                        .map(a -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setAuthority(a);
                            return ae;
                        }).toList());
                extensionContext.getStore(NAMESPACE).put(getAllureId(extensionContext), user);
                break; // рассчитано на механику, когда нужно авторизоваться в @BeforeEach тем же пользователем, с которым работаем в тесте (для отдельных пользователей подготовлена механика, но в этой точке потребуются изменения)
            }
        }
    }


    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter()
                .getType()
                .isAssignableFrom(UserEntity.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext
                .getStore(DBUserExtension.NAMESPACE)
                .get(getAllureId(extensionContext), UserEntity.class);
    }

    private String getAllureId(ExtensionContext context) {
        AllureId allureId = context.getRequiredTestMethod().getAnnotation(AllureId.class);
        if (allureId == null) {
            throw new IllegalStateException("Annotation @AllureId must be present!");
        }
        return allureId.value();
    }



}
