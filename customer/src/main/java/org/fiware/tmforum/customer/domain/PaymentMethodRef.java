package org.fiware.tmforum.customer.domain;

import lombok.Getter;
import lombok.Setter;
import org.fiware.tmforum.customer.domain.customer.Customer;
import org.fiware.tmforum.mapping.annotations.AttributeGetter;
import org.fiware.tmforum.mapping.annotations.AttributeSetter;
import org.fiware.tmforum.mapping.annotations.AttributeType;

import java.util.List;

public class PaymentMethodRef extends RefEntity {

    @Getter(onMethod = @__({@AttributeGetter(value = AttributeType.PROPERTY, targetName = "name", embedProperty = true)}))
    @Setter(onMethod = @__({@AttributeSetter(value = AttributeType.PROPERTY, targetName = "name", targetClass = String.class)}))
    private String name;

    public PaymentMethodRef(String id) {
        super(id);
    }

    @Override
    public List<String> getReferencedTypes() {
        return List.of(Customer.TYPE_CUSTOMER);
    }

}
