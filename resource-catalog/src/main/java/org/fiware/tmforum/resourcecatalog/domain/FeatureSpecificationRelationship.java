package org.fiware.tmforum.resourcecatalog.domain;

import lombok.Data;
import org.fiware.tmforum.common.domain.TimePeriod;

import java.net.URI;

@Data
public class FeatureSpecificationRelationship {

    private String featureId;
    private String name;
    private String relationshipType;
    private String resourceSpecificationHref;
    private ResourceSpecificationRef resourceSpecificationId;
    private TimePeriod validFor;
    private String atBaseType;
    private URI atSchemaLocation;
    private String atType;
}
