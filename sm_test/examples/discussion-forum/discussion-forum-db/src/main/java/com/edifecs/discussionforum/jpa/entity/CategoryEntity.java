package com.edifecs.discussionforum.jpa.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep.kath on 5/14/2014.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = CategoryEntity.FIND_ALL_CATEGORIES, query = "SELECT category FROM CategoryEntity category WHERE category.tenantId=:tenantId")
})

public class CategoryEntity extends AuditObject{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private long tenantId;

    private String name = null;
    private String description = null;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<TopicEntity> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicEntity> topics) {
        this.topics = topics;
    }

    @OneToMany(cascade = CascadeType.ALL)
    private List<TopicEntity> topics = new ArrayList<TopicEntity>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static final String FIND_ALL_CATEGORIES = "Category.findAll";

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }
}
