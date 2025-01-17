package org.fiware.tmforum.resourcecatalog;

import org.fiware.resourcecatalog.model.ResourceCandidateCreateVO;
import org.fiware.resourcecatalog.model.ResourceCandidateUpdateVO;
import org.fiware.resourcecatalog.model.ResourceCandidateVO;
import org.fiware.resourcecatalog.model.ResourceCatalogCreateVO;
import org.fiware.resourcecatalog.model.ResourceCatalogUpdateVO;
import org.fiware.resourcecatalog.model.ResourceCatalogVO;
import org.fiware.resourcecatalog.model.ResourceCategoryCreateVO;
import org.fiware.resourcecatalog.model.ResourceCategoryUpdateVO;
import org.fiware.resourcecatalog.model.ResourceCategoryVO;
import org.fiware.resourcecatalog.model.ResourceSpecificationCreateVO;
import org.fiware.resourcecatalog.model.ResourceSpecificationUpdateVO;
import org.fiware.resourcecatalog.model.ResourceSpecificationVO;
import org.fiware.tmforum.common.mapping.IdHelper;
import org.fiware.tmforum.mapping.MappingException;
import org.fiware.tmforum.resourcecatalog.domain.ResourceCandidate;
import org.fiware.tmforum.resourcecatalog.domain.ResourceCatalog;
import org.fiware.tmforum.resourcecatalog.domain.ResourceCategory;
import org.fiware.tmforum.resourcecatalog.domain.ResourceCategoryRef;
import org.fiware.tmforum.resourcecatalog.domain.ResourceSpecification;
import org.fiware.tmforum.resourcecatalog.domain.ResourceSpecificationRef;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Mapper between the internal model and api-domain objects
 */
@Mapper(componentModel = "jsr330", uses = IdHelper.class)
public interface TMForumMapper {

    // resource catalog

    @Mapping(target = "id", source = "id")
    @Mapping(target = "href", source = "id")
    ResourceCatalogVO map(ResourceCatalogCreateVO resourceCatalogCreateVO, URI id);

    ResourceCatalogVO map(ResourceCatalog resourceCatalog);

    ResourceCatalog map(ResourceCatalogVO resourceCatalogVO);

    @Mapping(target = "id", source = "id")
    ResourceCatalog map(ResourceCatalogUpdateVO resourceCatalogUpdateVO, String id);

    // resource specification

    @Mapping(target = "id", source = "id")
    @Mapping(target = "href", source = "id")
    ResourceSpecificationVO map(ResourceSpecificationCreateVO resourceSpecificationCreateVO, URI id);

    ResourceSpecificationVO map(ResourceSpecification resourceSpecification);

    ResourceSpecification map(ResourceSpecificationVO resourceCandidateVO);

    @Mapping(target = "id", source = "id")
    ResourceSpecification map(ResourceSpecificationUpdateVO resourceSpecificationUpdateVO, String id);

    // resource catalog

    @Mapping(target = "id", source = "id")
    @Mapping(target = "href", source = "id")
    ResourceCandidateVO map(ResourceCandidateCreateVO resourceCandidateCreateVO, URI id);

    ResourceCandidateVO map(ResourceCandidate resourceCandidate);

    ResourceCandidate map(ResourceCandidateVO resourceCandidateVO);

    @Mapping(target = "id", source = "id")
    ResourceCandidate map(ResourceCandidateUpdateVO resourceCandidateUpdateVO, String id);

    // resource catalog

    @Mapping(target = "id", source = "id")
    @Mapping(target = "href", source = "id")
    ResourceCategoryVO map(ResourceCategoryCreateVO resourceCategoryCreateVO, URI id);

    ResourceCategoryVO map(ResourceCategory resourceCategory);

    ResourceCategory map(ResourceCategoryVO resourceCategoryVO);

    @Mapping(target = "id", source = "id")
    ResourceCategory map(ResourceCategoryUpdateVO resourceCategoryUpdateVO, String id);

    default URL map(String value) {
        if (value == null) {
            return null;
        }
        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new MappingException(String.format("%s is not a URL.", value), e);
        }
    }

    default String map(URL value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    default URI mapToURI(String value) {
        if (value == null) {
            return null;
        }
        return URI.create(value);
    }

    default String mapFromURI(URI value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    default ResourceSpecificationRef mapFromResourceSpecId(String id) {
        if (id == null) {
            return null;
        }
        return new ResourceSpecificationRef(IdHelper.toNgsiLd(id, ResourceSpecification.TYPE_RESOURCE_SPECIFICATION));
    }

    default ResourceCategoryRef mapFromCategoryId(String id) {
        if (id == null) {
            return null;
        }
        return new ResourceCategoryRef(IdHelper.toNgsiLd(id, ResourceCategory.TYPE_RESOURCE_CATEGORY));
    }

    default String mapFromCategoryRef(ResourceCategoryRef categoryRef) {
        if (categoryRef == null) {
            return null;
        }
        return IdHelper.fromNgsiLd(categoryRef.getId());
    }

    default String mapFromResourceSpecificationRef(ResourceSpecificationRef resourceSpecificationRef) {
        if (resourceSpecificationRef == null) {
            return null;
        }
        return IdHelper.fromNgsiLd(resourceSpecificationRef.getId());
    }
}


