package com.wkclz.camunda.entity.process;

import com.wkclz.camunda.exception.CamundaException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shrimp
 */
@Data
public class ProcessStartRequest implements Serializable {

    private String businessKey;
    private Map<String, VariablesEntity> variables;


    @Data
    static class VariablesEntity implements Serializable {
        private String type;
        private Object value;
    }

    // 所有支持类型的枚举
    public enum Type {
        Boolean, Short, Integer, Long, Double, String, Date, Object, File
    }

    public void addVariable(String name, Type type, Object value) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        if (type == null) {
            throw CamundaException.error("type 不能为空");
        }

        if (this.variables == null) {
            this.variables = new HashMap<>();
        }

        VariablesEntity entity = new VariablesEntity();
        entity.setType(type.name());
        if (value == null) {
            this.variables.put(name, entity);
            return;
        }

        switch (type) {
            case File -> throw CamundaException.error("暂不支持文件，请使将文件转换为 Base64");
            case Object -> throw CamundaException.error("若是对象，还需要传这三个值：" +
                "Object type name: Serialization Data Format: Serialized value");
            default -> entity.setValue(value);
        }
        this.variables.put(name, entity);
    }

}
