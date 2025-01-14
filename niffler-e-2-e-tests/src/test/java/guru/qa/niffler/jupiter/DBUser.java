package guru.qa.niffler.jupiter;

import guru.qa.niffler.db.model.Authority;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith(DBUserExtension.class)
public @interface DBUser {

     String username();
     String password();
     boolean enabled() default true;
     boolean accountNonExpired() default true;
     boolean accountNonLocked() default true;
     boolean credentialsNonExpired() default true;
     Authority[] authorities();

}
