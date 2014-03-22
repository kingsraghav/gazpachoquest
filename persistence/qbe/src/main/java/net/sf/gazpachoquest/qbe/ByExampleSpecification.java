package net.sf.gazpachoquest.qbe;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.lang3.Validate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * Helper to create find by example query.
 */
@Component
public class ByExampleSpecification {

    static private String pattern(final String str) {
        return "%" + str + "%";
    }

    private EntityManager entityManager;

    public <T> Specification<T> byExample(final T example) {
        return byExample(entityManager, example);
    }

    /**
     * Lookup entities having at least one String attribute matching the passed
     * pattern.
     */
    public <T> Specification<T> byPatternOnStringAttributes(final String pattern, final Class<T> entityType) {
        return byPatternOnStringAttributes(entityManager, pattern, entityType);
    }

    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // http://mactruecolor.blogspot.fi/2012/07/query-by-example-in-spring-data-jpa.html
    private <T> Specification<T> byExample(final EntityManager em, final T example) {
        Validate.notNull(example, "example must not be null");

        @SuppressWarnings("unchecked")
        final Class<T> type = (Class<T>) example.getClass();

        return new Specification<T>() {
            @Override
            public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<>();

                Set<SingularAttribute<T, ?>> types = em.getMetamodel().entity(type).getDeclaredSingularAttributes();

                for (Attribute<T, ?> attr : types) {
                    if (attr.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE
                            || attr.getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE) {
                        continue;
                    }

                    String fieldName = attr.getName();

                    try {
                        if (attr.getJavaType() == String.class) {
                            String fieldValue = (String) ReflectionUtils.invokeMethod((Method) attr.getJavaMember(),
                                    example);
                            if (isNotEmpty(fieldValue)) {
                                // please compiler
                                SingularAttribute<T, String> stringAttr = em.getMetamodel().entity(type)
                                        .getDeclaredSingularAttribute(fieldName, String.class);
                                // apply like
                                predicates.add(builder.like(root.get(stringAttr), pattern(fieldValue)));

                            }
                        } else {
                            Object fieldValue = ReflectionUtils.getField((Field) attr.getJavaMember(), example);
                            if (fieldValue != null) {
                                // please compiler
                                SingularAttribute<T, ?> anyAttr = em.getMetamodel().entity(type)
                                        .getDeclaredSingularAttribute(fieldName, fieldValue.getClass());
                                // apply equal
                                predicates.add(builder.equal(root.get(anyAttr), fieldValue));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new IllegalStateException("OOOUCH!!!", e);
                    }
                }

                if (predicates.size() > 0) {
                    return builder.and(predicates.toArray(new Predicate[predicates.size()]));
                }

                return builder.conjunction();
            }
        };
    }

    private <T> Specification<T> byPatternOnStringAttributes(final EntityManager em, final String pattern,
            final Class<T> type) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                Set<SingularAttribute<T, ?>> types = em.getMetamodel().entity(type).getDeclaredSingularAttributes();

                for (Attribute<T, ?> attr : types) {
                    if (attr.getPersistentAttributeType() == PersistentAttributeType.MANY_TO_ONE
                            || attr.getPersistentAttributeType() == PersistentAttributeType.ONE_TO_ONE) {
                        continue;
                    }

                    String fieldName = attr.getName();

                    try {
                        if (attr.getJavaType() == String.class) {
                            if (isNotEmpty(pattern)) {
                                SingularAttribute<T, String> stringAttr = em.getMetamodel().entity(type)
                                        .getDeclaredSingularAttribute(fieldName, String.class);
                                predicates.add(builder.like(root.get(stringAttr), pattern(pattern)));
                            }
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException("OOOUCH!!!", e);
                    }
                }

                if (predicates.size() > 0) {
                    return builder.or(predicates.toArray(new Predicate[predicates.size()]));
                }

                return builder.conjunction(); // 1 = 1
            }
        };
    }
}