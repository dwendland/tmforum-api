package org.fiware.tmforum.customer_bill.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.fiware.tmforum.common.domain.Entity;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppliedBillingRateCharacteristic extends Entity {

    private String name;
    private String valueType;
    private Object value;

}
