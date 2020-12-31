package co.aden.sdkautotestshell.vo;

import java.lang.reflect.Method;
import java.util.List;

import lombok.Builder;
import lombok.NonNull;

@Builder(toBuilder = true)
public class ActivityEntity {

    @NonNull
    private String activityName;
    @NonNull
    private List<Method> methods;
    private String extend;

}
