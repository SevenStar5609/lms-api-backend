package vn.edu.hutech.lms_api.specification;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import vn.edu.hutech.lms_api.domain.Course;
import vn.edu.hutech.lms_api.dto.course.CourseSearchCriteria;

import java.util.ArrayList;
import java.util.List;

public final class CourseSpecifications {

    private CourseSpecifications() {
    }

    public static Specification<Course> byCriteria(CourseSearchCriteria criteria) {
        return (root, query, cb) -> {
            if (criteria == null) {
                return cb.conjunction();
            }

            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (criteria.getTitle() != null && !criteria.getTitle().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.getTitle().toLowerCase() + "%"));
            }

            if (criteria.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), criteria.getMinPrice()));
            }

            if (criteria.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), criteria.getMaxPrice()));
            }

            if (criteria.getStatus() != null && !criteria.getStatus().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("status")), criteria.getStatus().toLowerCase()));
            }

            if (criteria.getInstructorId() != null || (criteria.getInstructorName() != null && !criteria.getInstructorName().isBlank())) {
                var instructor = root.join("instructor", JoinType.LEFT);

                if (criteria.getInstructorId() != null) {
                    predicates.add(cb.equal(instructor.get("id"), criteria.getInstructorId()));
                }

                if (criteria.getInstructorName() != null && !criteria.getInstructorName().isBlank()) {
                    predicates.add(cb.like(cb.lower(instructor.get("fullName")), "%" + criteria.getInstructorName().toLowerCase() + "%"));
                }
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
